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




}
