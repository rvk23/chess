package ui;

import client.ServerFacade;
import client.websocket.WebSocketFacade;
import chess.*;

import java.util.Collection;
import java.util.Scanner;

public class GameUI {

    public static void run(String authToken, int gameID, ServerFacade facade, String perspective) throws Exception {
        Scanner scanner = new Scanner(System.in);
        System.out.println("You are now in a game! Type 'help' to see commands.");

        while (true) {
            System.out.print("[Game] >>> ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "help" -> printHelp();

                case "move" -> {
                    try {
                        System.out.print("Enter start position (ex: e2): ");
                        String start = scanner.nextLine().trim().toLowerCase();
                        System.out.print("Enter end position (ex: e4): ");
                        String end = scanner.nextLine().trim().toLowerCase();

                        ChessPosition startPos = parsePosition(start);
                        ChessPosition endPos = parsePosition(end);

                        ChessMove move = new ChessMove(startPos, endPos, null);

                        WebSocketFacade.sendMove(authToken, gameID, move);

                        System.out.println("Move sent: " + positionToString(startPos) + " -> " + positionToString(endPos));
                    }
                    catch (Exception e) {
                        System.out.println("Invalid move: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                case "legal" -> {
                    try {
                        System.out.print("Enter piece position to show legal moves (ex: e2): ");
                        String pos = scanner.nextLine().trim().toLowerCase();

                        ChessPosition position = parsePosition(pos);

                        WebSocketFacade.sendDisplayMoves(authToken, gameID, position);

                        System.out.println("Request sent to display legal moves for " + pos);
                    } catch (Exception e) {
                        System.out.println("Invalid position: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                case "redraw" -> {
                    try {
                        ChessBoardUI.drawBoard(authToken, facade, gameID, perspective);
                    } catch (Exception e) {
                        System.out.println("Error drawing board: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                case "resign" -> {
                    WebSocketFacade.sendResign(authToken, gameID);
                    System.out.println("You resigned from the game.");
                    return;
                }

                case "leave" -> {
                    WebSocketFacade.sendLeave(authToken, gameID);
                    System.out.println("You left the game.");
                    return;
                }

                default -> System.out.println("Unknown command. Type 'help' to see options.");
            }
        }
    }

    private static void printHelp() {
        System.out.println("""
            Commands:
              help       Show this menu
              move       Move a piece
              legal      Show legal moves for a piece
              redraw     Redraw the board
              leave      Leave the game
              resign     Resign from the game
        """);
    }

    private static ChessPosition parsePosition(String pos) {
        if (pos.length() != 2) throw new IllegalArgumentException("Position must be 2 characters (ex: e2)");

        char file = pos.charAt(0);
        char rank = pos.charAt(1);

        int column = file - 'a' + 1;
        int row = rank - '0';

        if (column < 1 || column > 8 || row < 1 || row > 8) {
            throw new IllegalArgumentException("Position out of bounds (must be a1-h8)");
        }

        return new ChessPosition(row, column);
    }


    private static String positionToString(ChessPosition pos) {
        char col = (char) ('a' + (pos.getColumn()));
        return "" + col + (pos.getRow() + 1);
    }
}
