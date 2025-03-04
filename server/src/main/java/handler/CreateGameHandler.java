package handler;

import com.google.gson.Gson;
import model.GameData;
import dataaccess.AuthDAO;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class CreateGameHandler implements Route {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public CreateGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request req, Response res) {
        String authToken = req.headers("authorization");
        if (authToken == null || !gameService.isAuthenticated(authToken)) {
            res.status(401);
            return gson.toJson(Map.of("message", "Error: Unauthorized"));
        }



        Map<String, String> requestMap = gson.fromJson(req.body(), Map.class);
        String gameName = requestMap.get("gameName");

        if (gameName == null || gameName.isEmpty()) {
            res.status(400);
            return gson.toJson(Map.of("message", "Error: Bad Request"));

        }

        int gameID = gameService.createGame(authToken, gameName);
        res.status(200);
        return gson.toJson(Map.of("gameID", gameID));
    }
}
