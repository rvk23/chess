package service;

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
    private UserDAO userDAO;

    @BeforeEach
    void setUp() throws Exception {
        userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();
        userService = new UserService(userDAO, authDAO);
        handler = new RegisterHandler(userService);
        gson = new Gson();

        userDAO.clear();
        authDAO.clear();
    }


    @Test
    void handleSuccess() throws Exception {

        String jsonInput = gson.toJson(new UserData("user", "password", "abc123@test.com"));
        RequestStub req = new RequestStub(jsonInput);
        ResponseStub res = new ResponseStub();


        String jsonResponse = (String) handler.handle(req, res);


        assertNotNull(jsonResponse, "Response should not be null");


        AuthData response = gson.fromJson(jsonResponse, AuthData.class);


        assertNotNull(response, "AuthData should not be null");
        assertNotNull(response.authToken(), "Auth token should not be null");
        assertEquals("user", response.username());
        assertEquals(200, res.status());
    }


    @Test
    void handleAlreadyTaken() throws Exception {

        if (userDAO.getUser("user") == null) {
            userService.register(new UserData("user", "password", "abc123@test.com"));
        }


        String jsonInput = gson.toJson(new UserData("user", "password", "abc123@test.com"));
        RequestStub req = new RequestStub(jsonInput);
        ResponseStub res = new ResponseStub();


        String jsonResponse = (String) handler.handle(req, res);


        assertEquals(403, res.status(), "Should return 403 for duplicate user");
        assertTrue(jsonResponse.contains("Error: Username Already Taken"), "Error message should match");
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
