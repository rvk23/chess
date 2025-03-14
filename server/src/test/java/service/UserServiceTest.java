package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import service.UserService;
import java.util.UUID;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private UserService userService;

    @BeforeEach
    void setUp() throws DataAccessException {
        UserDAO userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();

        userDAO.clear();
        authDAO.clear();

        userService = new UserService(userDAO, authDAO);
    }


    @Test
    void registerSuccess() throws DataAccessException {
        String uniqueUsername = "user" + UUID.randomUUID();
        UserData user = new UserData(uniqueUsername, "password", "abc123@email.com");

        AuthData auth = userService.register(user);

        assertNotNull(auth);
        assertEquals(uniqueUsername, auth.username());
    }


    @Test
    void registerFailUserTaken() throws DataAccessException {
        String username = "user";

        if (userService.getAuthDAO().getAuth(username) == null) {
            userService.register(new UserData(username, "password", "abc123@test.com"));
        }

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.register(new UserData(username, "password", "abc123@test.com"));
        });

        assertEquals("Thats already taken", exception.getMessage());
    }

}
