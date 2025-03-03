package service;

import model.AuthData;
import model.UserData;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import java.util.UUID;

public class UserService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(UserData user) {
        if (userDAO.getUser(user.username()) != null) {
            throw new RuntimeException("That's already taken");
        }
        userDAO.addUser(user);
        String token = UUID.randomUUID().toString();
        authDAO.createAuth(token, user.username());
        return new AuthData(token, user.username());
    }
}
