package service;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.GameService;
import handler.ListGamesHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import spark.Request;
import spark.Response;
import java.util.Map;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class ListGamesHandlerTest {
    private ListGamesHandler handler;
    private GameService gameService;
    private Gson gson;
    private AuthDAO authDAO;
    private GameDAO gameDAO;

    @BeforeEach
    void setUp() {
        gameDAO = new GameDAO();
        authDAO = new AuthDAO();
        gameService = new GameService(gameDAO, authDAO);
        handler = new ListGamesHandler(gameService);
        gson = new Gson();
    }

    @Test
    void handleSuccess() throws DataAccessException {

        gameDAO.clear();
        authDAO.clear();

        String authToken = "validToken";


        UserDAO userDAO = new UserDAO();
        userDAO.addUser(new UserData("user", "password", "user@email.com"));


        authDAO.createAuth(authToken, "user");


        gameDAO.createGame("Game one");
        gameDAO.createGame("Game two");

        Request req = new FakeRequest(authToken);
        Response res = new FakeResponse();

        String jsonResponse = (String) handler.handle(req, res);
        assertNotNull(jsonResponse);

        var responseMap = gson.fromJson(jsonResponse, Map.class);
        List<?> games = (List<?>) responseMap.get("games");

        assertNotNull(games);
        assertEquals(2, games.size(), "The two created games should be listed");
    }




    @Test
    void handleUnauthorized() {

        Request req = new FakeRequest(null);
        Response res = new FakeResponse();


        String jsonResponse = (String) handler.handle(req, res);
        assertEquals(gson.toJson(Map.of("message", "Error: Unauthorized")), jsonResponse);
    }

    //fake request
    static class FakeRequest extends Request {
        private final String authToken;

        FakeRequest(String authToken) {
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

    //fake response
    static class FakeResponse extends Response {
        private int statusCode;

        @Override
        public void status(int statusCode) {
            this.statusCode = statusCode;
        }

        public int getStatusCode() {
            return statusCode;
        }
    }
}
