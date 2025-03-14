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
    private static final Gson gson = new Gson();

    public int createGame(String gameName) throws DataAccessException {
        String sql = "INSERT INTO games (whitePlayer, blackPlayer, gameState) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, null);
            stmt.setString(2, null);
            stmt.setString(3, gson.toJson(new ChessGame()));

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted == 0) {
                throw new DataAccessException("⚠️ Game insert failed: No rows affected.");
            }

            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int gameID = generatedKeys.getInt(1);
                    System.out.println("✅ Game created with ID: " + gameID);
                    return gameID;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error inserting game: " + e.getMessage());
        }

        throw new DataAccessException("⚠️ Game insert failed, no ID generated.");
    }

    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT whitePlayer, blackPlayer, gameState FROM games WHERE id = ?";
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
            throw new DataAccessException("Error retrieving game: " + e.getMessage());
        }

        return null;
    }

    public List<GameData> getAllGames() throws DataAccessException {
        List<GameData> gameList = new ArrayList<>();
        String sql = "SELECT id, whitePlayer, blackPlayer, gameState FROM games";

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
            throw new DataAccessException("Error retrieving all games: " + e.getMessage());
        }

        return gameList;
    }

    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException {
        String sql = "UPDATE games SET whitePlayer = ?, blackPlayer = ?, gameState = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, updatedGame.whiteUsername());
            stmt.setString(2, updatedGame.blackUsername());
            stmt.setString(3, gson.toJson(updatedGame.game()));
            stmt.setInt(4, gameID);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DataAccessException("⚠️ Update failed: No rows updated.");
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game: " + e.getMessage());
        }
    }

    public void clear() throws DataAccessException {
        String sql = "DELETE FROM games";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing games: " + e.getMessage());
        }
    }
}
