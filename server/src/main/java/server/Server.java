package server;

import spark.*;
import handler.*;
import service.*;
import dataaccess.*;
import com.google.gson.Gson;
// check server

public class Server {

    private int assignedPort;

    public int run(int desiredPort) {

        DatabaseManager.initializeDatabase();

        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint 
        // DAOs
        UserDAO userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();
        GameDAO gameDAO = new GameDAO();

        // services
        UserService userService = new UserService(userDAO, authDAO);
        GameService gameService = new GameService(gameDAO, authDAO);

        // handler
        Spark.post("/user", new RegisterHandler(userService)); // Register user
        Spark.post("/session", new LoginHandler(userService)); // Login user
        Spark.delete("/session", new LogoutHandler(userService)); // Logout user
        Spark.get("/game", new ListGamesHandler(gameService)); // List all games
        Spark.post("/game", new CreateGameHandler(gameService)); // Create game
        Spark.put("/game", new JoinGameHandler(gameService)); // Join game
        Spark.get("/game/state", new GameStateHandler(gameService)); // return game state


        Spark.delete("/db", (req, res) -> {
            DatabaseManager.clearDatabase();
            res.status(200);
            return new Gson().toJson(new SuccessResponse("Database cleared successfully."));
        });

        Spark.exception(Exception.class, (exception, req, res) -> {
            res.status(500);
            res.body(new Gson().toJson(new ErrorResponse("Error: " + exception.getMessage())));
        });


        Spark.awaitInitialization();
        assignedPort = Spark.port();
        return assignedPort;
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }

    public int port() {
        return assignedPort;
    }



    static class ErrorResponse {
        String message;
        ErrorResponse(String message) { this.message = message; }
    }

    static class SuccessResponse {
        String message;
        SuccessResponse(String message) { this.message = message; }
    }
}
