package client;

import websocket.commands.UserGameCommand;
import com.google.gson.Gson;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

public class WebSocketClientManager {

    private static WebSocketClient socket;
    private static final Gson gson = new Gson();

    public static void connect() {
        try {
            if (socket == null || socket.isClosed()) {
                socket = new WebSocketClient(new URI("ws://localhost:8080/ws")) {
                    @Override
                    public void onOpen(ServerHandshake serverHandshake) {
                        System.out.println("[WebSocketClient] Connected to server.");
                    }

                    @Override
                    public void onMessage(String message) {
                        System.out.println("[WebSocketClient] Message received: " + message);
                    }

                    @Override
                    public void onClose(int code, String reason, boolean remote) {
                        System.out.println("[WebSocketClient] Connection closed.");
                    }

                    @Override
                    public void onError(Exception ex) {
                        System.out.println("[WebSocketClient] Error: " + ex.getMessage());
                    }
                };
                socket.connectBlocking();
            }
        } catch (Exception e) {
            System.out.println("[WebSocketClient] Connection failed: " + e.getMessage());
        }
    }

    public static void send(UserGameCommand command) {
        try {
            if (socket == null || socket.isClosed()) {
                connect();
            }
            socket.send(gson.toJson(command));
        } catch (Exception e) {
            System.out.println("[WebSocketClient] Send error: " + e.getMessage());
        }
    }

    public static void close() {
        try {
            if (socket != null) {
                socket.close();
            }
        } catch (Exception e) {
            System.out.println("[WebSocketClient] Close error: " + e.getMessage());
        }
    }
}
