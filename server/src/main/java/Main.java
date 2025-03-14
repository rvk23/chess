import server.Server;
import dataaccess.*;

import chess.*;

// test server
// add
public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Server: " + piece);

        Server server = new Server();
        server.run(8080);


        // test database manually
        try {
            System.out.println("Ensuring database exists");
            DatabaseManager.createDatabase();
            System.out.println("Creating tables");
            // create tables
            DatabaseManager.createTables();
            System.out.println("Tables created");
        } catch (DataAccessException e) {
            System.err.println("Error creating tables:  " + e.getMessage());
        }

    }
}