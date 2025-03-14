package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;

public class AuthDAO {
    private final Map<String, AuthData> authTokens = new HashMap<>();


    public void createAuth(String authToken, String username) throws DataAccessException {
        String sql = "INSERT INTO auth_tokens (token, username) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection()) {
            conn.setAutoCommit(false);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, authToken);
                stmt.setString(2, username);

                int rowsInserted = stmt.executeUpdate();
                if (rowsInserted == 0) {
                    throw new DataAccessException("Token insert failed: No rows affected.");
                }
                conn.commit();
                System.out.println("Auth token stored for: " + username);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error storing auth token: " + e.getMessage());
        }
    }


    public AuthData getAuth(String authToken) throws DataAccessException {
        String sql = "SELECT username FROM auth_tokens WHERE token = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, authToken);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new AuthData(authToken, rs.getString("username"));
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving auth token: " + e.getMessage());
        }
        return null;
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        String sql = "DELETE FROM auth_tokens WHERE token = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, authToken);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error deleting auth token: " + e.getMessage());
        }
    }

    public void clear() throws DataAccessException {
        String sql = "DELETE FROM auth_tokens";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();
            System.out.println("Auth tokens table cleared.");
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing auth tokens table: " + e.getMessage());
        }
    }
}