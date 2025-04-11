package client.websocket;

import chess.ChessMove;
import chess.ChessPosition;
import websocket.commands.UserGameCommand;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.client.WebSocketClient;

import java.net.URI;
import java.util.concurrent.ConcurrentHashMap;

public class WebSocketFacade {
    private static final Gson gson = new Gson();
    private static final ConcurrentHashMap<String, WebSocketConnection> connections = new ConcurrentHashMap<>();

    private static WebSocketConnection getConnection(String authToken) throws Exception {
        if (!connections.containsKey(authToken)) {
            var client = new WebSocketClient();
            client.start();
            URI uri = new URI("ws://localhost:8080/ws");
            WebSocketConnection socket = new WebSocketConnection();
            client.connect(socket, uri).get();
            connections.put(authToken, socket);
        }
        return connections.get(authToken);
    }

    public static void sendMove(String authToken, int gameID, ChessMove move) throws Exception {
        WebSocketConnection conn = getConnection(authToken);
        var cmd = new UserGameCommand(UserGameCommand.CommandType.MAKE_MOVE, authToken, gameID, move, null);
        conn.send(gson.toJson(cmd));
    }

    public static void sendLeave(String authToken, int gameID) throws Exception {
        WebSocketConnection conn = getConnection(authToken);
        var cmd = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID, null, null);
        conn.send(gson.toJson(cmd));
    }

    public static void sendResign(String authToken, int gameID) throws Exception {
        WebSocketConnection conn = getConnection(authToken);
        var cmd = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID, null, null);
        conn.send(gson.toJson(cmd));
    }

    public static void sendDisplayMoves(String authToken, int gameID, ChessPosition position) throws Exception {
        WebSocketConnection conn = getConnection(authToken);
        var cmd = new UserGameCommand(UserGameCommand.CommandType.DISPLAY_MOVES, authToken, gameID, null, position);
        conn.send(gson.toJson(cmd));
    }
}
