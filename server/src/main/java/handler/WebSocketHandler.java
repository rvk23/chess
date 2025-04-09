package handler;


import dataaccess.*;
import org.eclipse.jetty.websocket.api.*;
import model.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import com.google.gson.Gson;
import chess.*;
import dataaccess.GameDAO;
import websocket.ConnectionManager;
import dataaccess.DataAccessException;


import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;





public class WebSocketHandler implements WebSocketListener {

    private static final Gson gson = new Gson();

    private static final ConcurrentHashMap<Session, String> sessionAuthTokens = new ConcurrentHashMap<>();
    private static final ConnectionManager connectionManager = new ConnectionManager();
    private static final GameDAO gameDAO = new GameDAO();
    private static final ConcurrentHashMap<String, Session> authTokenSessions = new ConcurrentHashMap<>();


    private Session session;


    @Override
    public void onWebSocketConnect(Session session) {
        System.out.println("Opened connection: " + session.getRemoteAddress());
        this.session = session;
    }

    @Override
    public void onWebSocketText(String message) {
        System.out.println("Received message: " + message);
        try {
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            if (command.getCommandType() == null) {
                sendError(session, "Missing command type");
                return;
            }
            switch (command.getCommandType()) {
                case CONNECT -> handleConnect(command);
                case MAKE_MOVE -> handleMakeMove(command);
                case LEAVE -> handleLeave(command);
                case RESIGN -> handleResign(command);
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
            sendError(session, "Invalid WebSocket message: " + ex.getMessage());
        }
    }

    @Override
    public void onWebSocketBinary(byte[] payload, int offset, int length) {
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        String authToken = sessionAuthTokens.remove(session);
        if (authToken != null) {
            authTokenSessions.remove(authToken);
        }
        connectionManager.removeSession(session);
        System.out.println("Closed connection: " + session.getRemoteAddress());
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        cause.printStackTrace();
    }

    private void sendError(Session sessionToSend, String errorMessage) {
        try {
            ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage);
            sessionToSend.getRemote().sendString(gson.toJson(error));
        }
        catch (IOException ex) {
            ex.printStackTrace();
        }
    }




    private void handleConnect(UserGameCommand command) {
        try {
            AuthDAO authDAO = new AuthDAO();
            AuthData authData = authDAO.getAuth(command.getAuthToken());
            if (authData == null) {
                sendError(session, "Error: Invalid auth token");
                return;
            }
            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                sendError(session, "Error: Game not found");
                return;
            }

            ChessGame game = gameData.game();
            sessionAuthTokens.put(session, command.getAuthToken());
            authTokenSessions.put(command.getAuthToken(), session);
            connectionManager.addConnection(command.getGameID(), session);

            ServerMessage loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGame.setGame(game);
            session.getRemote().sendString(gson.toJson(loadGame));

            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    authData.username() + " connected.");
            connectionManager.broadcastToGameExceptSender(command.getGameID(), gson.toJson(notification), session);
        }
        catch (Exception ex) {
            sendError(session, "Error handling connect: " + ex.getMessage());
        }
    }


    private String getUsernameFromAuthToken(String authToken) {
        try {
            AuthDAO authDAO = new AuthDAO();
            AuthData authData = authDAO.getAuth(authToken);
            if (authData != null) {
                return authData.username();
            }
        }
        catch (DataAccessException ex) {
            ex.printStackTrace();
        }
        return "Unknown";
    }

    private void handleMakeMove(UserGameCommand command) {
        try {
            ChessMove move = command.getMove();
            if (move == null) {
                sendError(session, "Error: No move provided");
                return;
            }

            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                sendError(session, "Error: Game not found");
                return;
            }

            String authToken = command.getAuthToken();
            AuthDAO authDAO = new AuthDAO();
            AuthData authData = authDAO.getAuth(authToken);

            if (authData == null) {
                ChessGame.TeamColor currentTurn = gameData.game().getTeamTurn();
                String playerToSend = (currentTurn == ChessGame.TeamColor.WHITE) ? gameData.whiteUsername() : gameData.blackUsername();

                ServerMessage errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Error: Invalid auth token");
                String errorJson = gson.toJson(errorMessage);

                sendErrorToUser(playerToSend, errorJson);

                return;
            }

            String username = authData.username();
            if (!username.equals(gameData.whiteUsername()) && !username.equals(gameData.blackUsername())) {
                sendError(session, "Error: Observer cannot make a move");
                return;
            }

            ChessGame game = gameData.game();

            try {
                game.makeMove(move);
            }
            catch (InvalidMoveException e) {

                ServerMessage errorMessage = new ServerMessage(ServerMessage.ServerMessageType.ERROR, "Error: Invalid move: " + e.getMessage());
                String errorJson = gson.toJson(errorMessage);
                sendErrorToUser(username, errorJson);
                return;
            }

            GameData updatedGame = new GameData(
                    gameData.gameID(),
                    gameData.whiteUsername(),
                    gameData.blackUsername(),
                    gameData.gameName(),
                    game
            );
            gameDAO.updateGame(command.getGameID(), updatedGame);

            ServerMessage loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGame.setGame(game);
            connectionManager.broadcastToGame(command.getGameID(), gson.toJson(loadGame));

            String notificationMessage = username + " moved from " +
                    posToString(move.getStartPosition()) + " to " +
                    posToString(move.getEndPosition());
            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notificationMessage);

            Session moverSession = authTokenSessions.get(authToken);
            connectionManager.broadcastToGameExceptSender(command.getGameID(), gson.toJson(notification), moverSession);

        }
        catch (Exception ex) {
            ex.printStackTrace();
            sendError(session, "Error handling move: " + ex.getMessage());
        }
    }





    private void handleLeave(UserGameCommand command) {
        try {
            connectionManager.removeConnection(command.getGameID(), session);
            String username = getUsernameFromAuthToken(sessionAuthTokens.remove(session));
            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    username + " left the game.");
            connectionManager.broadcastToGame(command.getGameID(), gson.toJson(notification));
            session.close();
        }
        catch (Exception ex) {
            sendError(session, "Error handling leave: " + ex.getMessage());
        }
    }

    private void handleResign(UserGameCommand command) {
        try {
            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                sendError(session, "Error: Game not found");
                return;
            }

            String username = getUsernameFromAuthToken(sessionAuthTokens.get(session));
            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    username + " resigned.");
            connectionManager.broadcastToGame(command.getGameID(), gson.toJson(notification));
        }
        catch (Exception ex) {
            sendError(session, "Error handling resign: " + ex.getMessage());
        }
    }


    private String posToString(ChessPosition pos) {
        char colChar = (char) ('a' + (pos.getColumn() - 1));
        return "" + colChar + pos.getRow();
    }


    private void sendErrorToUser(String username, String errorJson) {
        if (username == null) {
            return;
        }
        for (var entry : sessionAuthTokens.entrySet()) {
            Session session = entry.getKey();
            String token = entry.getValue();
            try {
                AuthDAO authDAO = new AuthDAO();
                AuthData authData = authDAO.getAuth(token);
                if (authData != null && authData.username().equals(username)) {
                    session.getRemote().sendString(errorJson);
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }



}
