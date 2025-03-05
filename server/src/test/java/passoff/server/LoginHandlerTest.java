package passoff.server;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import handler.LoginHandler;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class LoginHandlerTest {
    private LoginHandler handler;
    private UserService userService;
    private Gson gson;

    @BeforeEach
    void setUp() {
        UserDAO userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();
        userService = new UserService(userDAO, authDAO);
        handler = new LoginHandler(userService);
        gson = new Gson();
    }

    @Test
    void handleSuccess() {
        // register a user first
        UserData user = new UserData("user", "password", "abc123@test.com");
        userService.register(user);


        String requestBody = gson.toJson(new UserData(user.username(), user.password(), null));

        // create request and response
        TestRequest req = new TestRequest(requestBody);
        TestResponse res = new TestResponse();


        String jsonResponse = (String) handler.handle(req, res);


        AuthData response = gson.fromJson(jsonResponse, AuthData.class);


        assertNotNull(response.authToken());
        assertEquals("user", response.username());
    }

    @Test
    void handleInvalid() {
        // login request to JSON
        String requestBody = gson.toJson(new UserData("wrongUser", "wrongPass", null));


        TestRequest req = new TestRequest(requestBody);
        TestResponse res = new TestResponse();


        String jsonResponse = (String) handler.handle(req, res);


        String expectedResponse = gson.toJson(Map.of("message", "Error: Wrong Login"));



        assertEquals(expectedResponse, jsonResponse);
        assertEquals(401, res.status());
    }


    private static class TestRequest extends Request {
        private final String body;

        TestRequest(String body) {
            this.body = body;
        }

        @Override
        public String body() {
            return body;
        }
    }


    private static class TestResponse extends Response {
        private int status;

        @Override
        public void status(int statusCode) {
            this.status = statusCode;
        }

        @Override
        public int status() {
            return status;
        }
    }
}
