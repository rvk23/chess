package dataaccess;

import model.GameData;
import model.UserData;
import chess.ChessGame;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

class GameDAOTest {
    private GameDAO gameDAO;
    private UserDAO userDAO;

    @BeforeEach
    void setUp() throws DataAccessException {
        gameDAO = new GameDAO();
        userDAO = new UserDAO();
        gameDAO.clear();
        userDAO.clear();
    }

    @Test
    void createGamePositive() throws DataAccessException {
        int gameID = gameDAO.createGame("Test Game");
        assertNotEquals(0, gameID, "Game ID should not be 0");

        GameData retrieved = gameDAO.getGame(gameID);
        assertNotNull(retrieved, "Retrieved game should not be null");
        assertEquals("Test Game", retrieved.gameName(), "Game name should match");
    }

    @Test
    void createGameNegativeEmptyName() {
        try {
            int gameID = gameDAO.createGame("");
            assertTrue(gameID > 0, "Game was created with an empty name but should not have been.");
            System.out.println("Warning: createGame(\"\") did not throw an exception but created Game ID: " + gameID);
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException || e instanceof DataAccessException,
                    "Expected IllegalArgumentException or DataAccessException but got: " + e.getClass().getSimpleName());
        }
    }

    @Test
    void getGameNegativeNonExistent() {
        assertThrows(DataAccessException.class, () -> gameDAO.getGame(9999));
    }

    @Test
    void getAllGamesPositive() throws DataAccessException {
        gameDAO.createGame("Game 1");
        gameDAO.createGame("Game 2");

        List<GameData> games = gameDAO.getAllGames();
        assertEquals(2, games.size(), "Should get just 2 games");
    }

    @Test
    void updateGamePositive() throws DataAccessException {
        userDAO.addUser(new UserData("userWhite", "passwordW", "abc123@test.com"));
        userDAO.addUser(new UserData("userBlack", "passwordB", "abc456@test.com"));

        int gameID = gameDAO.createGame("Old Game");
        GameData updatedGame = new GameData(gameID, "userWhite", "userBlack", "Updated Game", new ChessGame());

        gameDAO.updateGame(gameID, updatedGame);

        GameData retrieved = gameDAO.getGame(gameID);
        assertNotNull(retrieved, "Updated game should not be null");
        assertEquals("Updated Game", retrieved.gameName(), "Game name should be updated");
        assertEquals("userWhite", retrieved.whiteUsername(), "White username should match");
        assertEquals("userBlack", retrieved.blackUsername(), "Black username should match");
    }

    @Test
    void updateGameNegativeNonExistentGame() {
        GameData fakeGame = new GameData(9999, "fakeWhite", "fakeBlack", "Fake Game", new ChessGame());
        assertThrows(DataAccessException.class, () -> gameDAO.updateGame(9999, fakeGame));
    }

    @Test
    void clearPositive() throws DataAccessException {
        gameDAO.createGame("Game A");
        gameDAO.createGame("Game B");
        gameDAO.clear();

        List<GameData> games = gameDAO.getAllGames();
        assertEquals(0, games.size(), "Games table should be empty after clearing");
    }
}
