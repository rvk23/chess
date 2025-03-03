package passoff.server;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import model.AuthData;
import model.UserData;
import service.UserService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTest {
    private UserService userService;

    @BeforeEach
    void setUp() {
        UserDAO userDAO = new UserDAO();
        AuthDAO authDAO = new AuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    void registerSuccess() {
        UserData user = new UserData("user", "password", "abc123@email.com");
        AuthData auth = userService.register(user);
        assertNotNull(auth);
        assertEquals("user", auth.username());
    }

    @Test
    void registerFailUserTaken() {
        UserData user = new UserData("user", "password", "abc123@test.com");
        userService.register(user);
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.register(user);
        });
        assertEquals("That's already taken", exception.getMessage());
    }
}
