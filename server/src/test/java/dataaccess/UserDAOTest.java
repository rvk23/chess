package dataaccess;

import model.UserData;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class UserDAOTest {
    private UserDAO userDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        userDAO = new UserDAO();
        userDAO.clear();
    }

    @Test
    void addUserPositive() throws DataAccessException {
        UserData user = new UserData("user", "password", "abc123@test.com");
        userDAO.addUser(user);
        UserData retrieved = userDAO.getUser("user");
        assertNotNull(retrieved);
        assertEquals("user", retrieved.username());
        assertEquals("abc123@test.com", retrieved.email());
    }

    @Test
    void addUserNegativeDuplicateUser() throws DataAccessException {
        UserData user = new UserData("user", "password", "abc123@test.com");
        userDAO.addUser(user);
        assertThrows(DataAccessException.class, () -> userDAO.addUser(user));
    }

    @Test
    void getUserNegativeNonExistentUser() throws DataAccessException {
        assertNull(userDAO.getUser("nonUser"));
    }

    @Test
    void clearPositive() throws DataAccessException {
        UserData user1 = new UserData("user1", "password1", "abc123@test.com");
        UserData user2 = new UserData("user2", "password2", "abc456@test.com");

        userDAO.addUser(user1);
        userDAO.addUser(user2);

        userDAO.clear();
        assertNull(userDAO.getUser("user1"));
        assertNull(userDAO.getUser("user2"));
    }
}
