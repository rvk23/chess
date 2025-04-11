package websocket.messages;

import org.eclipse.jetty.websocket.api.Session;
import java.io.IOException;

public class Connection {
    public final String username;
    public final Session session;
    public final int gameID;

    public Connection(String username, Session session, int gameID) {
        this.username = username;
        this.session = session;
        this.gameID = gameID;
    }

    public void send(String message) throws IOException {
        session.getRemote().sendString(message);
    }
}
