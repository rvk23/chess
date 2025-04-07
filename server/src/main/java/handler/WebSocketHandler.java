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
            ServerMessage errorMessageObj = new ServerMessage(ServerMessage.ServerMessageType.ERROR);
            session.getBasicRemote().sendText(gson.toJson(errorMessageObj));
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }


    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        System.out.println("Closed connection: " + session.getId());
    }



    private void handleConnect(UserGameCommand command, Session session) {
        try {
            // load to user
            ServerMessage loadGame = new ServerMessage(ServerMessage.ServerMessageType.LOAD_GAME);
            session.getBasicRemote().sendText(gson.toJson(loadGame));

            // notification
            ServerMessage notification = new ServerMessage(ServerMessage.ServerMessageType.NOTIFICATION);
            for (Session s : sessions) {
                if (!s.equals(session)) {
                    s.getBasicRemote().sendText(gson.toJson(notification));
                }
            }
        } catch (IOException ex) {
            sendError(session, "Error handling connect: " + ex.getMessage());
        }

    }

    private void handleMakeMove(UserGameCommand command, Session session) {
        // stuff
    }

    private void handleLeave(UserGameCommand command, Session session) {
        // stuff
    }

    private void handleResign(UserGameCommand command, Session session) {
        // stuff
    }

}
