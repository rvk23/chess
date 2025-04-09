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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;





public class WebSocketHandler implements WebSocketListener {

    private static final Gson gson = new Gson();

    private static final ConcurrentHashMap<Session, String> sessionAuthTokens = new ConcurrentHashMap<>();
    private static final ConnectionManager connectionManager = new ConnectionManager();
    private static final GameDAO gameDAO = new GameDAO();

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
                sendError("Missing command type");
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
            sendError("Invalid WebSocket message: " + ex.getMessage());
        }
    }

    @Override
    public void onWebSocketBinary(byte[] payload, int offset, int length) {
    }

    @Override
    public void onWebSocketClose(int statusCode, String reason) {
        sessionAuthTokens.remove(session);
        for (Integer gameID : connectionManager.getAllGames()) {
            connectionManager.removeConnection(gameID, session);
        }
        System.out.println("Closed connection: " + session.getRemoteAddress());
    }

    @Override
    public void onWebSocketError(Throwable cause) {
        cause.printStackTrace();
    }

    private void sendError(String errorMessage) {
        try {
            ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage);
            session.getRemote().sendString(gson.toJson(error));
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
                sendError("Error: Invalid auth token");
                return;
            }
            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                sendError("Error: Game not found");
                return;
            }

            ChessGame game = gameData.game();
            sessionAuthTokens.put(session, command.getAuthToken());
            connectionManager.addConnection(command.getGameID(), session);

            ServerMessage loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGame.setGame(game);
            session.getRemote().sendString(gson.toJson(loadGame));

            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    authData.username() + " connected.");
            connectionManager.broadcastToGameExceptSender(command.getGameID(), gson.toJson(notification), session);
        }
        catch (Exception ex) {
            sendError("Error handling connect: " + ex.getMessage());
        }
    }


    private String getUsernameFromAuthToken(String authToken) {
        try {
            AuthDAO authDAO = new AuthDAO();
            AuthData authData = authDAO.getAuth(authToken);
            if (authData != null) {
                return authData.username();
            }
        } catch (DataAccessException ex) {
            ex.printStackTrace();
        }
        return "Unknown";
    }

    private void handleMakeMove(UserGameCommand command) {
        try {
            ChessMove move = command.getMove();
            if (move == null) {
                sendError("Error: No move provided");
                return;
            }
            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                sendError("Error: Game not found");
                return;
            }
            ChessGame game = gameData.game();
            game.makeMove(move);

            GameData updatedGame = new GameData(gameData.gameID(),
                    gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game);
            gameDAO.updateGame(command.getGameID(), updatedGame);

            ServerMessage loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGame.setGame(game);
            connectionManager.broadcastToGame(command.getGameID(), gson.toJson(loadGame));

            String username = getUsernameFromAuthToken(sessionAuthTokens.get(session));
            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    username + " made a move.");
            connectionManager.broadcastToGame(command.getGameID(), gson.toJson(notification));
        }
        catch (Exception ex) {
            sendError("Error handling move: " + ex.getMessage());
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
            sendError("Error handling leave: " + ex.getMessage());
        }
    }

    private void handleResign(UserGameCommand command) {
        try {
            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                sendError("Error: Game not found");
                return;
            }

            String username = getUsernameFromAuthToken(sessionAuthTokens.get(session));
            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    username + " resigned.");
            connectionManager.broadcastToGame(command.getGameID(), gson.toJson(notification));
        }
        catch (Exception ex) {
            sendError("Error handling resign: " + ex.getMessage());
        }
    }


}
