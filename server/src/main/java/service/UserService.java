package service;

import dataaccess.DataAccessException;
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

    public AuthData register(UserData user) throws DataAccessException {
        if (userDAO.getUser(user.username()) != null) {
            throw new RuntimeException("Thats already taken");
        }
        userDAO.addUser(user);
        String token = UUID.randomUUID().toString();
        authDAO.createAuth(token, user.username());
        return new AuthData(token, user.username());
    }
    public AuthData login(String username, String password) throws DataAccessException {
        UserData user = userDAO.getUser(username);

        if (user == null || !user.password().equals(password)) {
            throw new RuntimeException("Wrong password");
        }

        // need new auth token
        String token = UUID.randomUUID().toString();
        authDAO.createAuth(token, username);

        return new AuthData(token, username);
    }

    public void logout(String authToken) throws DataAccessException {
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new RuntimeException("unauthorized");
        }
        authDAO.deleteAuth(authToken);
    }
    public AuthDAO getAuthDAO() {
        return authDAO;
    }

}
