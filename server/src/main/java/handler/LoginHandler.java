package handler;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;
import java.util.Map;

public class LoginHandler implements Route {
    private final UserService userService;
    private final Gson gson = new Gson();

    public LoginHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request req, Response res) {
        try {
            UserData user = gson.fromJson(req.body(), UserData.class);
            AuthData auth = userService.login(user.username(), user.password());
            res.status(200);
            return gson.toJson(auth);
        } catch (IllegalArgumentException e) {
            res.status(401);
            return gson.toJson(Map.of("message", e.getMessage()));
        } catch (DataAccessException e) {
            res.status(500);
            return gson.toJson(Map.of("message", "Internal Server Error"));
        }
    }



}
