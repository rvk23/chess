package service;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import service.UserService;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import handler.LogoutHandler;
import spark.Request;
import spark.Response;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutHandlerTest {
    private LogoutHandler handler;
    private UserService userService;
    private Gson gson;

    @BeforeEach
    void setUp() throws DataAccessException {
        UserDAO userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();
        userService = new UserService(userDAO, authDAO);
        handler = new LogoutHandler(userService);
        gson = new Gson();


        userDAO.clear();
        authDAO.clear();
    }


    private static class TestRequest extends Request {
        private final String authToken;

        public TestRequest(String authToken) {
            this.authToken = authToken;
        }

        @Override
        public String headers(String header) {
            if ("authorization".equals(header)) {
                return authToken;
            }
            return null;
        }
    }

    private static class TestResponse extends Response {
        private int statusCode;

        @Override
        public void status(int statusCode) {
            this.statusCode = statusCode;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }

    @Test
    void handleSuccess() throws DataAccessException {
        //login a user
        UserData user = new UserData("user", "password", "abc123@test.com");
        userService.register(user);
        AuthData auth = userService.login(user.username(), user.password());

        //request with auth token
        TestRequest req = new TestRequest(auth.authToken());
        TestResponse res = new TestResponse();

        Object jsonResponse = handler.handle(req, res);

        //check auth token is removed
        assertNull(userService.getAuthDAO().getAuth(auth.authToken()));
        assertEquals(200, res.getStatusCode());
        assertEquals("{}", jsonResponse);
    }

    @Test
    void handleInvalidToken() {

        TestRequest req = new TestRequest("invalidToken");
        TestResponse res = new TestResponse();


        Object jsonResponse = handler.handle(req, res);


        assertEquals(401, res.getStatusCode());
        assertEquals(gson.toJson(Map.of("message", "Error: Unauthorized")), jsonResponse);
    }
}
