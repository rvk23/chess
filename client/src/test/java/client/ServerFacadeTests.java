package client;

import org.junit.jupiter.api.*;
import server.Server;
import client.ServerFacade;
import model.AuthData;
import static org.junit.jupiter.api.Assertions.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.InputStream;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }


    @BeforeEach
    public void clearDB() throws Exception {
        var url = new URL("http://localhost:" + server.port() + "/db");
        var conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("DELETE");
        conn.getInputStream().close();
    }

    @Test
    public void registerPositive() throws Exception {
        var facade = new ServerFacade(server.port());
        AuthData auth = facade.register("user", "password", "abc123@test.com");

        assertNotNull(auth.authToken(), "Auth token should not be null");
        assertEquals("user", auth.username(), "Username should match input");
    }

    @Test
    public void registerNegative() throws Exception {
        var facade = new ServerFacade(server.port());
        facade.register("double", "password", "double@example.com");

        Exception exception = assertThrows(Exception.class, () ->
        {
            facade.register("double", "password", "double@example.com");
        });

        String msg = exception.getMessage().toLowerCase();
        assertTrue(msg.contains("username already taken") || msg.contains("403"), "Expected same username error");
    }


}
