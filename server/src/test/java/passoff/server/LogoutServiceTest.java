package passoff.server;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import service.UserService;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LogoutServiceTest {
    private UserService userService;
    private AuthDAO authDAO;
    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
        authDAO = new AuthDAO();
        userService = new UserService(userDAO, authDAO);
    }

    @Test
    void logoutSuccess() {
        UserData user = new UserData("user", "password", "ac123@test.com");
        userService.register(user);
        AuthData auth = userService.login(user.username(), user.password());

        userService.logout(auth.authToken());
        assertNull(authDAO.getAuth(auth.authToken()));
    }

    @Test
    void logoutFail() {
        Exception exception = assertThrows(RuntimeException.class, () -> {
            userService.logout("invalidToken");
        });
        assertEquals("unauthorized", exception.getMessage());
    }

}
