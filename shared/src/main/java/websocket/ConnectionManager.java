package websocket;

import org.eclipse.jetty.websocket.api.Session;
import java.util.*;

public class ConnectionManager {
    private final Map<Integer, Set<Session>> gameConnections = new HashMap<>();

    public void addConnection(int gameID, Session session) {
        gameConnections.computeIfAbsent(gameID, k -> new HashSet<>()).add(session);
    }

    public void removeConnection(int gameID, Session session) {
        Set<Session> sessions = gameConnections.get(gameID);
        if (sessions != null) {
            sessions.remove(session);
            if (sessions.isEmpty()) {
                gameConnections.remove(gameID);
            }
        }
    }

    public Set<Session> getConnections(int gameID) {
        return gameConnections.getOrDefault(gameID, Collections.emptySet());
    }

    public Set<Integer> getAllGames() {
        return gameConnections.keySet();
    }

    public void broadcastToGame(int gameID, String message) {
        Set<Session> sessions = getConnections(gameID);
        System.out.println("DEBUG: Broadcasting to " + sessions.size() + " sessions.");
        for (Session session : sessions) {
            if (session.isOpen()) {
                try {
                    System.out.println("DEBUG: Sending message to " + session.getRemoteAddress());
                    session.getRemote().sendString(message);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                System.out.println("DEBUG: Session closed: " + session.getRemoteAddress());
            }
        }
    }

    public void broadcastToGameExceptSender(int gameID, String message, Session sender) {
        Set<Session> sessions = getConnections(gameID);
        for (Session session : sessions) {
            if (!session.equals(sender) && session.isOpen()) {
                try {
                    session.getRemote().sendString(message);
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void removeSession(Session session) {
        for (Integer gameID : new HashSet<>(gameConnections.keySet())) {
            removeConnection(gameID, session);
        }
    }

}
