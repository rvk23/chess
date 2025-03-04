package service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;

import java.util.List;
import java.util.stream.Collectors;

public class GameService {
    private final GameDAO gameDAO;
    private final AuthDAO authDAO;

    public GameService(GameDAO gameDAO, AuthDAO authDAO) {
        this.gameDAO = gameDAO;
        this.authDAO = authDAO;
    }

    public List<GameData> listGames(String authToken) {
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null) {
            throw new RuntimeException("Error: unauthorized");
        }
        return gameDAO.getAllGames();
    }
}
