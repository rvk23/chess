package handler;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint("/ws")
public class WebSocketHandler {

    private static final Set<Session> sessions = ConcurrentHashMap.newKeySet();
    private static final Gson gson = new Gson();

    private static final ConcurrentHashMap<Session, String> sessionAuthTokens = new ConcurrentHashMap<>();
    private static final ConcurrentHashMap<Integer, Set<Session>> gameSessions = new ConcurrentHashMap<>();


    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        System.out.println("Opened connection: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received message: " + message);

        try {
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);

            switch (command.getCommandType()) {
                case CONNECT -> handleConnect(command, session);
                case MAKE_MOVE -> handleMakeMove(command, session);
                case LEAVE -> handleLeave(command, session);
                case RESIGN -> handleResign(command, session);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            sendError(session, "Invalid WebSocket message: " + ex.getMessage());
        }
    }

    private void sendError(Session session, String errorMessage) {
        try {
            ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage);
            session.getBasicRemote().sendText(gson.toJson(error));
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        sessionAuthTokens.remove(session);
        for (Set<Session> gameSet : gameSessions.values()) {
            gameSet.remove(session);
        }
        System.out.println("Closed connection: " + session.getId());
    }



    private void handleConnect(UserGameCommand command, Session session) {
        try {
            if (!isValidAuthToken(command.getAuthToken())) {
                sendError(session, "Error: Invalid auth token");
                return;
            }

            if (!isValidGameID(command.getGameID())) {
                sendError(session, "Error: Invalid game ID");
                return;
            }

            sessionAuthTokens.put(session, command.getAuthToken());
            gameSessions.putIfAbsent(command.getGameID(), ConcurrentHashMap.newKeySet());
            gameSessions.get(command.getGameID()).add(session);

            // load to user
            ServerMessage loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            session.getBasicRemote().sendText(gson.toJson(loadGame));


            String username = getUsernameFromAuthToken(sessionAuthTokens.get(session));
            // notification
            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + " joined the game.");

            Set<Session> gamePlayers = gameSessions.get(command.getGameID());
            for (Session s : gamePlayers) {
                if (!s.equals(session)) {
                    s.getBasicRemote().sendText(gson.toJson(notification));
                }
            }
        }
        catch (IOException ex) {
            sendError(session, "Error handling connect: " + ex.getMessage());
        }

    }


    private String getUsernameFromAuthToken(String authToken) {
        return authToken;
    }

    private void handleMakeMove(UserGameCommand command, Session session) {
        try {
            Integer gameID = command.getGameID();
            Set<Session> gamePlayers = gameSessions.get(gameID);

            if (gamePlayers == null) {
                sendError(session, "Error: Not part of a valid game");
                return;
            }

            // stuff


            ServerMessage loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            for (Session s : gamePlayers) {
                s.getBasicRemote().sendText(gson.toJson(loadGame));
            }

            String username = getUsernameFromAuthToken(sessionAuthTokens.get(session));
            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + " made a move.");

            for (Session s : gamePlayers) {
                if (!s.equals(session)) {
                    s.getBasicRemote().sendText(gson.toJson(notification));
                }
            }
        }
        catch (IOException ex) {
            sendError(session, "Error handling move: " + ex.getMessage());
        }
    }

    private void handleLeave(UserGameCommand command, Session session) {
        try {
            Integer gameID = command.getGameID();
            Set<Session> gamePlayers = gameSessions.get(gameID);

            if (gamePlayers != null) {
                gamePlayers.remove(session);
            }

            sessions.remove(session);
            sessionAuthTokens.remove(session);

            String username = getUsernameFromAuthToken(sessionAuthTokens.get(session));
            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + " left the game.");
            if (gamePlayers != null) {
                for (Session s : gamePlayers) {
                    s.getBasicRemote().sendText(gson.toJson(notification));
                }
            }

            session.close();
        }
        catch (Exception ex) {
            sendError(session, "Error handling leave: " + ex.getMessage());
        }
    }

    private void handleResign(UserGameCommand command, Session session) {
        try {
            Integer gameID = command.getGameID();
            Set<Session> gamePlayers = gameSessions.get(gameID);

            if (gamePlayers == null) {
                sendError(session, "Error: No game found to resign from");
                return;
            }

            String username = getUsernameFromAuthToken(sessionAuthTokens.get(session));
            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, username + " resigned.");
            for (Session s : gamePlayers) {
                s.getBasicRemote().sendText(gson.toJson(notification));
            }


        }
        catch (IOException ex) {
            sendError(session, "Error handling resign: " + ex.getMessage());
        }
    }

    private boolean isValidAuthToken(String authToken) {
        // more stuff
        return authToken != null && !authToken.isEmpty();
    }

    private boolean isValidGameID(Integer gameID) {
        // more stuff
        return gameID != null && gameID > 0;
    }

}
