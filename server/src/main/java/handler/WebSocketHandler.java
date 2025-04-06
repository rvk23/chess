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

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        System.out.println("Opened connection: " + session.getId());
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("Received message: " + message);
    }

    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        System.out.println("Closed connection: " + session.getId());
    }
}
