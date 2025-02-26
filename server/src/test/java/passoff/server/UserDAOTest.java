package passoff.server;



import dataaccess.UserDAO;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class UserDAOTest {

    private UserDAO userDAO;

    @BeforeEach
    void setUp() {
        userDAO = new UserDAO();
    }

    @Test
    void addUser_Success() {
        UserData user = new UserData("user", "password", "abc123@email.com");
        userDAO.addUser(user);
        assertEquals(user, userDAO.getUser("user"));
    }

    @Test
    void getUser_notFound() {
        assertNull(userDAO.getUser("no_user"));
    }

    @Test
    void clear_Success() {
        userDAO.addUser(new UserData("user2", "password2", "abc456@test.com"));
        userDAO.clear();
        assertNull(userDAO.getUser("user2"));
    }
}
