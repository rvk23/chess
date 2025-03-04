package dataaccess;

import model.GameData;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
// wifi check 2

public class GameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextID = 1;

    public int createGame(String gameName) {
        int gameID = nextID++;
        games.put(gameID, new GameData(gameID, null, null, gameName, new chess.ChessGame()));
        return gameID;
    }

    public GameData getGame(int gameID) {
        return games.getOrDefault(gameID, null);
    }

    public List<GameData> getAllGames() {
        return new ArrayList<>(games.values());
    }

    public void updateGame(int gameID, GameData updatedGame) {
        if (!games.containsKey(gameID)) {
            throw new IllegalArgumentException("Error: Game ID not found");
        }
        games.put(gameID, updatedGame);
    }

    public void clear() {
        games.clear();
    }
}
