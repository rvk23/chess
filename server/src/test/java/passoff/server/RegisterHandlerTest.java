package passoff.server;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.UserService;
import handler.RegisterHandler;
import spark.Request;
import spark.Response;

import static org.junit.jupiter.api.Assertions.*;


public class RegisterHandlerTest {
    private RegisterHandler handler;
    private UserService userService;
    private Gson gson;

    @BeforeEach
    void setUp() {
        UserDAO userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();
        userService = new UserService(userDAO, authDAO);
        handler = new RegisterHandler(userService);
        gson = new Gson();
    }

    @Test
    void handleSuccess() throws Exception {
        // json request
        String jsonInput = gson.toJson(new UserData("testUser", "pass123", "email@test.com"));


        RequestStub req = new RequestStub(jsonInput);
        ResponseStub res = new ResponseStub();

        // call handler
        String jsonResponse = (String) handler.handle(req, res);
        AuthData response = gson.fromJson(jsonResponse, AuthData.class);

        // assertions
        assertNotNull(response.authToken());
        assertEquals("testUser", response.username());
        assertEquals(200, res.status());
    }

    @Test
    void handleAlreadyTaken() throws Exception {

        userService.register(new UserData("testUser", "pass123", "email@test.com"));


        String jsonInput = gson.toJson(new UserData("testUser", "pass123", "email@test.com"));
        RequestStub req = new RequestStub(jsonInput);
        ResponseStub res = new ResponseStub();

        // call handler
        String jsonResponse = (String) handler.handle(req, res);

        // assertions
        assertEquals(403, res.status());
        assertTrue(jsonResponse.contains("Error: Username Already Taken"));
    }


    private static class RequestStub extends Request {
        private final String body;

        RequestStub(String body) {
            this.body = body;
        }

        @Override
        public String body() {
            return body;
        }
    }


    private static class ResponseStub extends Response {
        private int status;

        @Override
        public void status(int statusCode) {
            this.status = statusCode;
        }

        @Override
        public int status() {
            return this.status;
        }
    }
}
