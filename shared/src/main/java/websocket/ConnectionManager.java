package websocket;

import org.eclipse.jetty.websocket.api.Session;
import java.util.*;

import websocket.messages.Connection;
import websocket.messages.ServerMessage;
import com.google.gson.Gson;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;


public class ConnectionManager {
    // more like petshop
    private final Map<String, Connection> connections = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    public void add(String username, Session session, int gameID) {
        connections.put(username, new Connection(username, session, gameID));
    }

    public void remove(String username) {
        connections.remove(username);
    }

    public Connection getConnection(String username) {
        return connections.get(username);
    }


}
