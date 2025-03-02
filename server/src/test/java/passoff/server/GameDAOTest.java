package passoff.server;

import dataaccess.GameDAO;
import model.GameData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class GameDAOTest {
    private GameDAO gameDAO;

    @BeforeEach
    void setUp() {
        gameDAO = new GameDAO();
    }

    @Test
    void createGameSuccess() {
        int gameID = gameDAO.createGame("New Chess Game");
        GameData game = gameDAO.getGame(gameID);
        assertNotNull(game);
        assertEquals("New Chess Game", game.gameName());
    }

    @Test
    void failGameNotFound() {
        assertNull(gameDAO.getGame(9999)); // Non-existent game
    }

    @Test
    void clear() {
        gameDAO.createGame("Game1");
        gameDAO.clear();
        assertNull(gameDAO.getGame(1));
    }
}
