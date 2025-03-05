package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

import java.util.List;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public boolean isAuthenticated(String authToken) {
        return authDAO.getAuth(authToken) != null;
    }

    public List<GameData> listGames(String authToken) {
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new IllegalArgumentException("Error: Unauthorized");
        }
        return gameDAO.getAllGames();
    }

    public int createGame(String authToken, String gameName) {
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new IllegalArgumentException("Error: Unauthorized");
        }

        if (gameName == null || gameName.trim().isEmpty()) {
            throw new IllegalArgumentException("Error: Game name cannot be empty");
        }

        return gameDAO.createGame(gameName);
    }

    public void joinGame(String authToken, int gameID, String playerColor) {
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new IllegalArgumentException("Error: Unauthorized");
        }

        GameData game = gameDAO.getGame(gameID);
        if (game == null) {
            throw new IllegalArgumentException("Error: Invalid game ID");
        }

        if ("WHITE".equalsIgnoreCase(playerColor)) {
            if (game.whiteUsername() != null) {
                throw new IllegalArgumentException("Error: White slot already taken");
            }
            game = new GameData(gameID, auth.username(), game.blackUsername(), game.gameName(), game.game());
        }
        else if ("BLACK".equalsIgnoreCase(playerColor)) {
            if (game.blackUsername() != null) {
                throw new IllegalArgumentException("Error: Black slot already taken");
            }
            game = new GameData(gameID, game.whiteUsername(), auth.username(), game.gameName(), game.game());
        }
        else {
            throw new IllegalArgumentException("Error: Invalid team color");
        }

        gameDAO.updateGame(gameID, game);
    }
}
