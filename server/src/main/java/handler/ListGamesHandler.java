package handler;

import com.google.gson.Gson;
import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;
import java.util.List;
import java.util.Map;

public class ListGamesHandler implements Route {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public ListGamesHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request req, Response res) {
        try {

            String authToken = req.headers("authorization");
            if (authToken == null || authToken.isEmpty()) {
                res.status(401);
                return gson.toJson("Error: Unauthorized");
            }

            //list of games
            List<GameData> games = gameService.listGames(authToken);
            res.status(200);
            return gson.toJson(Map.of("games", games));

        }
        catch (RuntimeException e) {
            res.status(500);
            return gson.toJson("Error: Unauthorized");
        }
    }
}
