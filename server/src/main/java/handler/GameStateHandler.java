package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.GameData;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class GameStateHandler implements Route {

    private final GameService gameService;
    private final Gson gson = new Gson();

    public GameStateHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request req, Response res) {
        String authToken = req.headers("authorization");
        String gameIDParam = req.queryParams("gameID");

        try {
            if (authToken == null || gameIDParam == null) {
                res.status(400);
                return gson.toJson(Map.of("message", "Error: Missing parameters"));
            }

            int gameID = Integer.parseInt(gameIDParam);
            GameData game = gameService.getGame(authToken, gameID);

            if (game == null) {
                res.status(404);
                return gson.toJson(Map.of("message", "Error: Game not found"));
            }

            res.status(200);
            return gson.toJson(game.game());
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Error: " + e.getMessage()));
        } catch (Exception e) {
            res.status(400);
            return gson.toJson(Map.of("message", "Error: Invalid gameID"));
        }
    }
}
