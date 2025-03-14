package dataaccess;

import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class AuthDAOTest {
    private AuthDAO authDAO;
    private UserDAO userDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        authDAO = new AuthDAO();
        userDAO = new UserDAO();
        authDAO.clear();
        userDAO.clear();
    }

    @Test
    void createAuth_Positive() throws DataAccessException {
        UserData user = new UserData("user", "password", "abc123@test.com");
        userDAO.addUser(user);


        authDAO.createAuth("token123", "user");
        AuthData retrieved = authDAO.getAuth("token123");

        assertNotNull(retrieved);
        assertEquals("user", retrieved.username());
    }

    @Test
    void createAuthNegativeSameToken() throws DataAccessException {

        UserData user = new UserData("user", "password", "abc123@test.com");
        userDAO.addUser(user);

        authDAO.createAuth("token123", "user");
        assertThrows(DataAccessException.class, () -> authDAO.createAuth("token123", "anotherUser"));
    }

    @Test
    void getAuthNegativeInvalidToken() throws DataAccessException {
        assertNull(authDAO.getAuth("invalidToken"));
    }

    @Test
    void deleteAuthPositive() throws DataAccessException {

        UserData user = new UserData("user", "password", "abc123@test.com");
        userDAO.addUser(user);

        authDAO.createAuth("token123", "user");
        authDAO.deleteAuth("token123");
        assertNull(authDAO.getAuth("token123"));
    }

    @Test
    void clearPositive() throws DataAccessException {

        UserData user1 = new UserData("user1", "password1", "abc123@test.com");
        UserData user2 = new UserData("user2", "password2", "abc456@test.com");

        userDAO.addUser(user1);
        userDAO.addUser(user2);

        authDAO.createAuth("token1", "user1");
        authDAO.createAuth("token2", "user2");

        authDAO.clear();
        assertNull(authDAO.getAuth("token1"));
        assertNull(authDAO.getAuth("token2"));
    }
}
