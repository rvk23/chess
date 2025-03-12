package dataaccess;

import java.sql.*;
import java.util.Properties;
// imports like sql?

public class DatabaseManager {
    private static final String DATABASE_NAME;
    private static final String USER;
    private static final String PASSWORD;
    private static final String CONNECTION_URL;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        try {
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) {
                    throw new Exception("Unable to load db.properties");
                }
                Properties props = new Properties();
                props.load(propStream);
                DATABASE_NAME = props.getProperty("db.name");
                USER = props.getProperty("db.user");
                PASSWORD = props.getProperty("db.password");

                var host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                CONNECTION_URL = String.format("jdbc:mysql://%s:%d", host, port);
            }
        }
        catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties. " + ex.getMessage());
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    static void createDatabase() throws DataAccessException {
        try {
            var statement = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            try (var preparedStatement = conn.prepareStatement(statement)) {
                preparedStatement.executeUpdate();
            }
            createTables();
        }
        catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DbInfo.getConnection(databaseName)) {
     * // execute SQL statements.
     * }
     * </code>
     */

    static void createTables() throws DataAccessException {
        try (var conn = getConnection();
             var stmt = conn.createStatement()) {

            // tables

            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                    'id' INT NOT NULL AUTO_INCREMENT,
                    'username' varchar(256) NOT NULL,
                    'password_hash' varchar(256) NOT NULL,
                    PRIMARY KEY ('id'),
                    INDEX(username)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                    """);


            // game table
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS games (
                    `id` INT NOT NULL AUTO_INCREMENT,
                    `whitePlayer` varchar(256),
                    `blackPlayer` varchar(256),
                    `gameState` NOT NULL,
                    PRIMARY KEY (`id`),
                    KEY (`whitePlayer`) REFERENCES users(`username`),
                    KEY (`blackPlayer`) REFERENCES users(`username`),
                    INDEX(whitePlayer),
                    INDEX(blackPlayer)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                    """);


        }
        catch (SQLException e) {
            throw new DataAccessException("Error creating tables: " + e.getMessage());
        }
    }




    static Connection getConnection() throws DataAccessException {
        try {
            var conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
            conn.setCatalog(DATABASE_NAME);
            return conn;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
