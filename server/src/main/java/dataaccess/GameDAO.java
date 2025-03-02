package dataaccess;

import model.GameData;
import java.util.HashMap;
import java.util.Map;

public class GameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextID = 1;

    public int createGame(String gameName) {
        int gameID = nextID++;
        games.put(gameID, new GameData(gameID, null, null, gameName, new chess.ChessGame()));
        return gameID;
    }

    public GameData getGame(int gameID) {
        return games.get(gameID);
    }

    public void clear() {
        games.clear();
    }
}
