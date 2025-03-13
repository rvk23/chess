package dataaccess;

import model.UserData;
import java.util.HashMap;
import java.util.Map;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    private final Map<String, UserData> users = new HashMap<>();

    public void addUser(UserData user) throws DataAccessException {
        users.put(user.username(), user);
        addUserToDatabase(user);
    }

    public UserData getUser(String username) throws DataAccessException {
        UserData user = users.get(username);
        if (user == null) {
            user = getUserFromDatabase(username);
        }
        return user;
    }

    public void clear() throws DataAccessException {
        users.clear();
        clearDatabase();
    }

    private void addUserToDatabase(UserData user) throws DataAccessException {
        String sql = "INSERT INTO users (username, password_hash, email) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, user.username());
            stmt.setString(2, BCrypt.hashpw(user.password(), BCrypt.gensalt()));
            stmt.setString(3, user.email());
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error inserting user into database: " + e.getMessage());
        }
    }

    private UserData getUserFromDatabase(String username) throws DataAccessException {
        String sql = "select username, password_hash, email FROM users where username = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, username);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new UserData(
                            rs.getString("username"),
                            rs.getString("password_hash"), // Hashed password stored
                            rs.getString("email")
                    );
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error retrieving user from database: " + e.getMessage());
        }
        return null;
    }

    private void clearDatabase() throws DataAccessException {
        String sql = "DELETE FROM users";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException("Error clearing users table: " + e.getMessage());
        }
    }

    public boolean verifyUser(String username, String providedPassword) throws DataAccessException {
        UserData user = getUser(username);
        return user != null && BCrypt.checkpw(providedPassword, user.password());
    }
}