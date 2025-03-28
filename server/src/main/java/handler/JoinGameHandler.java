package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import service.GameService;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Map;

public class JoinGameHandler implements Route {
    private final GameService gameService;
    private final Gson gson = new Gson();

    public JoinGameHandler(GameService gameService) {
        this.gameService = gameService;
    }

    @Override
    public Object handle(Request req, Response res) {
        String authToken = req.headers("authorization");
        if (authToken == null || authToken.isEmpty()) {
            res.status(401);
            return gson.toJson(Map.of("message", "Error: Unauthorized"));
        }

        Map<String, Object> requestMap = gson.fromJson(req.body(), Map.class);
        String playerColor = (String) requestMap.get("playerColor");
        Double gameIDDouble = (Double) requestMap.get("gameID");

        if (playerColor == null || gameIDDouble == null) {
            res.status(400);
            return gson.toJson(Map.of("message", "Error: Bad Request"));
        }


        int gameID = gameIDDouble.intValue();
        try {
            gameService.joinGame(authToken, gameID, playerColor);
            res.status(200);
            return "{}";
        }
        catch (IllegalArgumentException e) {
            if (e.getMessage().contains("Error: Unauthorized")) {
                res.status(401);
            }
            else if (e.getMessage().contains("Error: Invalid game ID") || e.getMessage().contains("Error: Invalid team color")) {
                res.status(400);
            }
            else if (e.getMessage().contains("Error: White slot already taken") || e.getMessage().contains("Error: Black slot already taken")) {
                res.status(403);
            }
            else {
                res.status(500);
            }
            return gson.toJson(Map.of("message", e.getMessage()));
        } catch (DataAccessException e) {
            throw new RuntimeException(e);
        }
    }
}

