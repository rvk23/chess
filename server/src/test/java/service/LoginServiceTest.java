package service;


import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import service.UserService;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class LoginServiceTest {
    private UserService userService;
    private AuthDAO authDAO;
    private UserDAO userDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO = new UserDAO();
        authDAO = new AuthDAO();

        // clear
        userDAO.clear();
        authDAO.clear();

        userService = new UserService(userDAO, authDAO);
    }


    @Test
    void login() throws DataAccessException {
        UserData user = new UserData("user", "password", "abc123@test.com");
        userService.register(user);

        AuthData auth = userService.login(user.username(), user.password());
        assertNotNull(auth);
        assertEquals("user", auth.username());
    }

    @Test
    void loginWrongPassword() throws DataAccessException {
        UserData user = new UserData("user", "password", "abc123@test.com");
        userService.register(user);

        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.login(user.username(), "wrongpassword");
        });
        assertEquals("Error: Unauthorized", exception.getMessage());

    }
}
