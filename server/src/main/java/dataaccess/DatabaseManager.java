package dataaccess;

import java.sql.*;
import java.util.Properties;


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
            Properties props = new Properties();
            try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
                if (propStream == null) {
                    throw new Exception("Unable to load db.properties");
                }
                props.load(propStream);
            }
            DATABASE_NAME = props.getProperty("db.name");
            USER = props.getProperty("db.user");
            PASSWORD = props.getProperty("db.password");
            var host = props.getProperty("db.host");
            var port = Integer.parseInt(props.getProperty("db.port"));
            CONNECTION_URL = String.format("jdbc:mysql://%s:%d/%s?serverTimezone=UTC", host, port, DATABASE_NAME);

        } catch (Exception ex) {
            throw new RuntimeException("Error loading db.properties: " + ex.getMessage());
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    // change to connect
    public static void createDatabase() throws DataAccessException {
        try (Connection conn = DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
             Statement stmt = conn.createStatement()) {

            String createDB = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            stmt.executeUpdate(createDB);
            System.out.println("Database ensured: " + DATABASE_NAME);

        } catch (SQLException e) {
            throw new DataAccessException("Error creating database: " + e.getMessage());
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

    public static void createTables() throws DataAccessException {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {



            // users table
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS users (
                    id INT NOT NULL AUTO_INCREMENT,
                    username VARCHAR(256) NOT NULL UNIQUE,
                    password_hash VARCHAR(256) NOT NULL,
                    email VARCHAR(256) NOT NULL,
                    PRIMARY KEY (id),
                    INDEX(username)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                    """);

            //games table
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS games (
                    id INT NOT NULL AUTO_INCREMENT,
                    whiteUsername VARCHAR(256),
                    blackUsername VARCHAR(256),
                    gameName VARCHAR(256) NOT NULL,
                    gameState TEXT NOT NULL,
                    PRIMARY KEY (id),
                    FOREIGN KEY (whiteUsername) REFERENCES users(username) ON DELETE SET NULL,
                    FOREIGN KEY (blackUsername) REFERENCES users(username) ON DELETE SET NULL,
                    INDEX(whiteUsername),
                    INDEX(blackUsername)
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                    """);


            //auth tokens table
            stmt.execute("""
                    CREATE TABLE IF NOT EXISTS auth_tokens (
                    token VARCHAR(256) PRIMARY KEY,
                    username VARCHAR(256) NOT NULL,
                    FOREIGN KEY (username) REFERENCES users(username) ON DELETE CASCADE
                    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
                    """);


        }
        catch (SQLException e) {
            throw new DataAccessException("Error creating tables: " + e.getMessage());
        }
    }






    static Connection getConnection() throws DataAccessException {
        try {
            createDatabase();
            return DriverManager.getConnection(CONNECTION_URL, USER, PASSWORD);
        } catch (SQLException e) {
            throw new DataAccessException("Error connecting to database: " + e.getMessage());
        }
    }


}


