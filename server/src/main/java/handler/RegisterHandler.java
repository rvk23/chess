package handler;

import com.google.gson.Gson;
import model.UserData;
import model.AuthData;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;
import java.util.Map;


public class RegisterHandler implements Route {
    private final UserService userService;
    private final Gson gson = new Gson();

    public RegisterHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request req, Response res) {
        UserData user = gson.fromJson(req.body(), UserData.class);
        try {
            AuthData auth = userService.register(user);
            res.status(200);
            return gson.toJson(auth);
        } catch (RuntimeException e) {
            res.status(403);
            return gson.toJson(Map.of("message", e.getMessage()));
        }
    }
}
