package dataaccess;

import model.GameData;
import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import chess.ChessGame;
import java.sql.*;
import java.util.*;


public class GameDAO {
    private final Map<Integer, GameData> games = new HashMap<>();
    private int nextID = 1;
    private static final Gson gson = new Gson();

    public int createGame(String gameName) throws DataAccessException {
        int gameID = nextID++;
        GameData newGame = new GameData(gameID, null, null, gameName, new ChessGame());
        games.put(gameID, newGame);


        String sql = "INSERT INTO games (whitePlayer, blackPlayer, gameState) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, null);
            stmt.setString(2, null);
            stmt.setString(3, gson.toJson(newGame.game())); // game or gameName
            stmt.executeUpdate();

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error inserting game: " + e.getMessage());
        }

        return gameID;
    }

    public GameData getGame(int gameID) throws DataAccessException {

        if (games.containsKey(gameID)) {
            return games.get(gameID);
        }


        String sql = "select whitePlayer, blackPlayer, gameState FROM games WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameID);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new GameData(
                            gameID,
                            rs.getString("whitePlayer"),
                            rs.getString("blackPlayer"),
                            "Game " + gameID,
                            gson.fromJson(rs.getString("gameState"), ChessGame.class)
                    );
                }
            }
        } catch (SQLException | JsonSyntaxException e) {
            throw new DataAccessException("Error getting game: " + e.getMessage());
        }

        return null;
    }

    public List<GameData> getAllGames() throws DataAccessException {
        List<GameData> gameList = new ArrayList<>(games.values());


        String sql = "select id, whitePlayer, blackPlayer, gameState FROM games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                gameList.add(new GameData(
                        rs.getInt("id"),
                        rs.getString("whitePlayer"),
                        rs.getString("blackPlayer"),
                        "Game " + rs.getInt("id"),
                        gson.fromJson(rs.getString("gameState"), ChessGame.class)
                ));
            }
        } catch (SQLException | JsonSyntaxException e) {
            throw new DataAccessException("Error getting all games: " + e.getMessage());
        }

        return gameList;
    }

    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException {
        if (!games.containsKey(gameID)) {
            throw new IllegalArgumentException("Error: Game ID not found");
        }
        games.put(gameID, updatedGame);


        String sql = "UPDATE games set whitePlayer = ?, blackPlayer = ?, gameState = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, updatedGame.whiteUsername());
            stmt.setString(2, updatedGame.blackUsername());
            stmt.setString(3, gson.toJson(updatedGame.gameName())); // gameName? or game
            stmt.setInt(4, gameID);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game: " + e.getMessage());
        }
    }

    public void clear() throws DataAccessException {
        games.clear();

        String sql = "DELETE FROM games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing games: " + e.getMessage());
        }
    }
}
