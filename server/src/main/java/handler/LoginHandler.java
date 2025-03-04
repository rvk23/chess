package handler;

import com.google.gson.Gson;
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
            // return JSON object if work
            return gson.toJson(auth);
        }
        catch (Exception e) {
            // if doesn't work
            res.status(401);
            return gson.toJson(Map.of("message", "Error: Wrong Login"));
        }
    }
}
