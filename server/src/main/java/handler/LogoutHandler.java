package handler;
import com.google.gson.Gson;
import service.UserService;
import spark.Request;
import spark.Response;
import spark.Route;

public class LogoutHandler implements Route {
    private final UserService userService;
    private final Gson gson = new Gson();

    public LogoutHandler(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Object handle(Request req, Response res) {
        String authToken = req.headers("authorization");

        if (authToken == null || authToken.isEmpty()) {
            res.status(401);
            return gson.toJson( "unauthorized");
        }

        try {
            userService.logout(authToken);
            res.status(200);
            return "{}";
        } catch (RuntimeException e) {
            res.status(401);
            return gson.toJson("unauthorized");
        }
    }
}