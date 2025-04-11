package client.websocket;

import chess.ChessMove;
import chess.ChessPosition;
import com.google.gson.Gson;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.*;

import java.util.List;

@WebSocket
public class WebSocketConnection {
    private Session session;
    private static final Gson gson = new Gson();

    private record ServerMessage(String serverMessageType, List<MoveData> moves, String message, String errorMessage) {}
    private record MoveData(Position startPosition, Position endPosition) {}
    private record Position(int row, int col) {}

    @OnWebSocketConnect
    public void onConnect(Session session) {
        this.session = session;
        System.out.println("[WebSocket] Connected!");
    }

    @OnWebSocketMessage
    public void onMessage(String message) {
        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);

        switch (serverMessage.serverMessageType) {
            case "MOVES" -> {
                if (serverMessage.moves() == null || serverMessage.moves().isEmpty()) {
                    System.out.println("No legal moves available.");
                } else {
                    System.out.println("Legal moves:");
                    for (MoveData move : serverMessage.moves()) {
                        String from = formatPosition(move.startPosition());
                        String to = formatPosition(move.endPosition());
                        System.out.println("  " + from + " -> " + to);
                    }
                }
            }
            case "NOTIFICATION" -> {
                System.out.println("[Notification] " + serverMessage.message());
            }
            case "ERROR" -> {
                System.out.println("[Error] " + serverMessage.errorMessage());
            }
            case "LOAD_GAME" -> {
                System.out.println("[Board update] (game reloaded)");
            }
            default -> {
                System.out.println("[WebSocket] Unknown message type: " + serverMessage.serverMessageType());
            }
        }
    }

    @OnWebSocketClose
    public void onClose(int statusCode, String reason) {
        System.out.println("[WebSocket] Connection closed: " + reason);
    }

    public void send(String message) throws Exception {
        session.getRemote().sendString(message);
    }

    private String formatPosition(Position pos) {
        char file = (char) ('a' + pos.col() - 1);
        int rank = 9 - pos.row();
        return "" + file + rank;
    }
}
