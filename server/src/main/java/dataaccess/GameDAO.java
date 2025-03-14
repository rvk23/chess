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
        String sql = "INSERT INTO games (whiteUsername, blackUsername, gameName, gameState) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                stmt.setNull(1, Types.VARCHAR);
                stmt.setNull(2, Types.VARCHAR);
                stmt.setString(3, gameName);
                stmt.setString(4, gson.toJson(new ChessGame()));

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted == 0) {
                    throw new DataAccessException("Game insert failed: No rows affected.");
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int gameID = generatedKeys.getInt(1);
                        conn.commit();
                        System.out.println("Game created with ID: " + gameID);
                        return gameID;
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error inserting game: " + e.getMessage());
        }

        throw new DataAccessException("Game insert failed, no ID generated.");
    }



    public GameData getGame(int gameID) throws DataAccessException {
        String sql = "SELECT whiteUsername, blackUsername, gameName, gameState FROM games WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, gameID);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    throw new DataAccessException("Game with ID " + gameID + " not found.");
                }

                ChessGame gameState;
                try {
                    gameState = gson.fromJson(rs.getString("gameState"), ChessGame.class);
                } catch (JsonSyntaxException e) {
                    throw new DataAccessException("Error parsing game state for game ID " + gameID);
                }

                return new GameData(
                        gameID,
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        gameState
                );
            }
        }
        catch (SQLException e) {
            throw new DataAccessException("Error retrieving game: " + e.getMessage());
        }
    }



    public List<GameData> getAllGames() throws DataAccessException {
        List<GameData> gameList = new ArrayList<>();
        String sql = "SELECT id, whiteUsername, blackUsername, gameName, gameState FROM games";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                //game state valid
                ChessGame gameState;
                try {
                    gameState = gson.fromJson(rs.getString("gameState"), ChessGame.class);
                } catch (JsonSyntaxException e) {
                    throw new DataAccessException("Error parsing game state for game ID " + rs.getInt("id"));
                }

                gameList.add(new GameData(
                        rs.getInt("id"),
                        rs.getString("whiteUsername"),
                        rs.getString("blackUsername"),
                        rs.getString("gameName"),
                        gameState
                ));
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving all games: " + e.getMessage());
        }

        return gameList;
    }


    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException {
        String sql = "UPDATE games SET whiteUsername = ?, blackUsername = ?, gameName = ?, gameState = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, updatedGame.whiteUsername());
            stmt.setString(2, updatedGame.blackUsername());
            stmt.setString(3, updatedGame.gameName());
            stmt.setString(4, gson.toJson(updatedGame.game()));
            stmt.setInt(5, gameID);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated == 0) {
                throw new DataAccessException("Update failed: No rows updated for game ID " + gameID);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error updating game: " + e.getMessage());
        }
    }

    public void clear() throws DataAccessException {
        String sql = "DELETE FROM games";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int rowsDeleted = stmt.executeUpdate();
            // debug
            System.out.println("Cleared " + rowsDeleted + " games from database.");
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing games: " + e.getMessage());
        }
    }
}
