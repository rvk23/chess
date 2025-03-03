package server;

import spark.*;
import handler.RegisterHandler;
import service.UserService;
import dataaccess.UserDAO;
import dataaccess.AuthDAO;
import com.google.gson.Gson;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        //This line initializes the server and can be removed once you have a functioning endpoint 
        // DAOs
        UserDAO userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();

        // services
        UserService userService = new UserService(userDAO, authDAO);

        // handler
        Spark.post("/user", new RegisterHandler(userService));

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
