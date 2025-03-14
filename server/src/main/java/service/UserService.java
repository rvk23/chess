package service;

import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import java.util.UUID;
import org.mindrot.jbcrypt.BCrypt;

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

        if (user == null || !BCrypt.checkpw(password, user.password())) {
            throw new IllegalArgumentException("Error: Unauthorized");
        }

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
