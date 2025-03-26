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


    @Test
    public void loginPositive() throws Exception {
        var facade = new ServerFacade(server.port());
        facade.register("user", "password", "abc123@test.com");

        AuthData auth = facade.login("user", "password");

        assertNotNull(auth.authToken(), "Auth token should not be null on login");
        assertEquals("user", auth.username(), "Returned username should match login");
    }

    @Test
    public void loginNegative() throws Exception {
        var facade = new ServerFacade(server.port());
        facade.register("userwrong", "wrongpassword", "abc123@test.com");

        Exception exception = assertThrows(Exception.class, () ->
                facade.login("userwrong", "notpassword")
        );

        String msg = exception.getMessage().toLowerCase();
        assertTrue(msg.contains("unauthorized") || msg.contains("401"), "Expected unauthorized error");
    }


    @Test
    public void logoutPositive() throws Exception {
        var facade = new ServerFacade(server.port());
        var auth = facade.register("user", "password", "abc123@test.com");

        assertDoesNotThrow(() -> facade.logout(auth.authToken()), "Logout should not throw an exception");

        Exception ex = assertThrows(Exception.class, () -> {
            facade.listGames(auth.authToken());
        });
        assertTrue(ex.getMessage().toLowerCase().contains("unauthorized"), "Expected unauthorized after logout");
    }

    @Test
    public void logoutNegative() {
        var facade = new ServerFacade(server.port());

        Exception ex = assertThrows(Exception.class, () ->
                facade.logout("invalid-token123")
        );

        assertTrue(ex.getMessage().toLowerCase().contains("unauthorized") || ex.getMessage().contains("401"),
                "Expected error due to invalid auth token");
    }



}
