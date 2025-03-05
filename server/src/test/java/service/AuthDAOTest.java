package service;

import dataaccess.AuthDAO;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class AuthDAOTest {
    private AuthDAO authDAO;

    @BeforeEach
    void setUp() {
        authDAO = new AuthDAO();
    }

    @Test
    void createAuthSuccess() {
        authDAO.createAuth("token", "user");
        AuthData auth = authDAO.getAuth("token");
        assertNotNull(auth);
        assertEquals("user", auth.username());
    }

    @Test
    void getAuthFailNotFound() {
        assertNull(authDAO.getAuth("noToken"));
    }

    @Test
    void clearSuccess() {
        authDAO.createAuth("token", "user");
        authDAO.clear();
        assertNull(authDAO.getAuth("token"));
    }
}
