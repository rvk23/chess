package handler;


import dataaccess.*;
import model.*;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import com.google.gson.Gson;
import chess.*;
import dataaccess.GameDAO;
import websocket.ConnectionManager;
import dataaccess.DataAccessException;
import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.InvalidMoveException;
import chess.ChessPosition;
import java.util.Collection;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketClose;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketConnect;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;




@WebSocket
public class WebSocketHandler {

    private static final Gson gson = new Gson();

    private static final Map<Session, String> sessionUsernameMap = new ConcurrentHashMap<>();
    private static final GameDAO gameDAO = new GameDAO();
    private static final AuthDAO authDAO = new AuthDAO();
    private static final ConnectionManager connectionManager = new ConnectionManager();

    private Session session;


    @OnWebSocketConnect
    public void onConnect(Session session) {
        System.out.println("Opened connection: " + session.getRemoteAddress());
    }

    @OnWebSocketClose
    public void onClose(Session session, int statusCode, String reason) {
        System.out.println("Closed connection: " + session.getRemoteAddress());
        String username = sessionUsernameMap.remove(session);
        if (username != null) {
            connectionManager.remove(username);
        }
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        try {
            UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
            switch (command.getCommandType()) {
                case CONNECT -> handleConnect(session, command);
                case MAKE_MOVE -> handleMakeMove(session, command);
                case LEAVE -> handleLeave(session, command);
                case RESIGN -> handleResign(session, command);
                case DISPLAY_MOVES -> handleDisplayMoves(session, command);
                case REDRAW -> handleRedraw(session, command);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
            sendError(session, "Error: Invalid WebSocket message");
        }
    }

    private void handleConnect(Session session, UserGameCommand command) throws IOException {

        //stuff
        try {
            AuthData auth = authDAO.getAuth(command.getAuthToken());
            if (auth == null) {
                sendError(session, "Error: Invalid authToken");
                return;
            }

            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                sendError(session, "Error: Invalid gameID");
                return;
            }

            String username = auth.username();
            connectionManager.add(username, session, command.getGameID());
            sessionUsernameMap.put(session, username);

            ServerMessage loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGame.setGame(gameData.game());
            session.getRemote().sendString(gson.toJson(loadGame));

            String notificationMsg;
            if (username.equals(gameData.whiteUsername())) {
                notificationMsg = username + " joined as White";
            }
            else if (username.equals(gameData.blackUsername())) {
                notificationMsg = username + " joined as Black";
            }
            else {
                notificationMsg = username + " joined as Observer";
            }

            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, notificationMsg);
            connectionManager.broadcastToGameExcept(username, command.getGameID(), notification);

        }
        catch (DataAccessException e) {
            sendError(session, "Error: " + e.getMessage());
        }
    }




    private void handleMakeMove(Session session, UserGameCommand command) throws IOException {

        //stuff
        try {
            System.out.println("[handleMakeMove] " + gson.toJson(command));
            AuthData auth = authDAO.getAuth(command.getAuthToken());
            if (auth == null) {
                sendError(session, "Error: Invalid authToken");
                return;
            }

            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                sendError(session, "Error: Invalid gameID");
                return;
            }

            ChessGame originalGame = gameData.game();
            ChessMove move = command.getMove();
            ChessPiece movingPiece = originalGame.getBoard().getPiece(move.getStartPosition());

            if (movingPiece == null) {
                sendError(session, "Error: No piece at start position");
                return;
            }

            ChessGame.TeamColor moverColor = movingPiece.getTeamColor();
            if ((moverColor == ChessGame.TeamColor.WHITE && !auth.username().equals(gameData.whiteUsername())) ||
                    (moverColor == ChessGame.TeamColor.BLACK && !auth.username().equals(gameData.blackUsername()))) {
                sendError(session, "Error: You cannot move this piece.");
                return;
            }

            ChessGame cloneGame = originalGame.deepCopy();
            try {
                cloneGame.makeMove(move);
            }
            catch (InvalidMoveException e) {
                sendError(session, "Error: " + e.getMessage());
                return;
            }

            try {
                originalGame.makeMove(move);
            }
            catch (InvalidMoveException e) {
                sendError(session, "Error: " + e.getMessage());
                return;
            }

            gameDAO.updateGame(gameData.gameID(), new GameData(
                    gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), originalGame
            ));

            ServerMessage loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            loadGame.setGame(originalGame);
            connectionManager.broadcastToGame(command.getGameID(), loadGame);

            String moveDesc = auth.username() + " moved from " + move.getStartPosition() + " to " + move.getEndPosition();
            ServerMessage moveNotification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION, moveDesc);
            connectionManager.broadcastToGameExcept(auth.username(), command.getGameID(), moveNotification);

            if (originalGame.isInCheck(originalGame.getTeamTurn())) {
                ServerMessage checkMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        "Check! " + originalGame.getTeamTurn() + " is in check!");
                connectionManager.broadcastToGame(command.getGameID(), checkMsg);
            }

            if (originalGame.getGameOver()) {
                ServerMessage gameOverMsg = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                        "Game Over!");
                connectionManager.broadcastToGame(command.getGameID(), gameOverMsg);
            }

        }
        catch (DataAccessException e) {
            sendError(session, "Error: " + e.getMessage());
        }
    }





    private void handleLeave(Session session, UserGameCommand command) throws IOException {

        //stuff

        try {
            String username = sessionUsernameMap.remove(session);
            if (username == null) {
                return;
            }

            connectionManager.remove(username);

            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                return;
            }

            boolean updated = false;
            ChessGame game = gameData.game();

            if (username.equals(gameData.whiteUsername())) {
                gameData = new GameData(gameData.gameID(), null, gameData.blackUsername(), gameData.gameName(), game);
                updated = true;
            }
            else if (username.equals(gameData.blackUsername())) {
                gameData = new GameData(gameData.gameID(), gameData.whiteUsername(), null, gameData.gameName(), game);
                updated = true;
            }

            if (updated) {
                gameDAO.updateGame(gameData.gameID(), gameData);
            }

            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    username + " left the game");
            connectionManager.broadcastToGame(command.getGameID(), notification);

        }
        catch (DataAccessException e) {
            sendError(session, "Error: " + e.getMessage());
        }
    }

    private void handleResign(Session session, UserGameCommand command) throws IOException {

        //stuff
        try {
            System.out.println("[handleResign] Resign request received.");
            AuthData auth = authDAO.getAuth(command.getAuthToken());
            if (auth == null) {
                sendError(session, "Error: Invalid authToken");
                return;
            }

            GameData gameData = gameDAO.getGame(command.getGameID());
            if (gameData == null) {
                sendError(session, "Error: Invalid gameID");
                return;
            }

            ChessGame game = gameData.game();
            String username = auth.username();

            if (game.getGameOver()) {
                sendError(session, "Error: Game is already over.");
                return;
            }

            if (!username.equals(gameData.whiteUsername()) && !username.equals(gameData.blackUsername())) {
                sendError(session, "Error: Observers cannot resign.");
                return;
            }

            game.setGameOver(true);

            gameDAO.updateGame(gameData.gameID(), new GameData(
                    gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName(), game
            ));

            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION,
                    username + " resigned");
            connectionManager.broadcastToGame(command.getGameID(), notification);

        }
        catch (DataAccessException e) {
            sendError(session, "Error: " + e.getMessage());
        }
    }


    private void handleDisplayMoves(Session session, UserGameCommand command) throws IOException {
        // display moves

    }


    private void handleRedraw(Session session, UserGameCommand command) throws IOException {
        // redraw board

    }

    private void sendError(Session session, String errorMessage) throws IOException {
        ServerMessage error = new ServerMessage(ServerMessage.ServerMessageType.ERROR, errorMessage);
        session.getRemote().sendString(gson.toJson(error));
    }

}
