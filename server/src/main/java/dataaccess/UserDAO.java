package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.*;

public class UserDAO {

    public void addUser(UserData user) throws DataAccessException {
        String sql = "INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, user.username());
            stmt.setString(2, BCrypt.hashpw(user.password(), BCrypt.gensalt()));
            stmt.setString(3, user.email());

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted == 0) {
                throw new DataAccessException("User insert failed: No rows affected.");
            }
            System.out.println("User inserted: " + user.username());

        } catch (SQLException e) {
            throw new DataAccessException("Error inserting user: " + e.getMessage());
        }
    }

    public UserData getUser(String username) throws DataAccessException {
        String sql = "SELECT username, password_hash, email FROM users WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserData(
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving user: " + e.getMessage());
        }
        return null;
    }

    public boolean verifyUser(String username, String providedPassword) throws DataAccessException {
        String sql = "SELECT password_hash FROM users WHERE username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String storedHashedPassword = rs.getString("password_hash");
                    return BCrypt.checkpw(providedPassword, storedHashedPassword);
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error verifying user: " + e.getMessage());
        }
        return false;
    }



    public void clear() throws DataAccessException {
        String sql = "DELETE FROM users";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.executeUpdate();
            System.out.println("Users table cleared.");

        } catch (SQLException e) {
            throw new DataAccessException("Error clearing users table: " + e.getMessage());
        }
    }
}