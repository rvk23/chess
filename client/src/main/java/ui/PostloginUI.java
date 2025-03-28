package ui;

import client.ServerFacade;
import model.AuthData;
import model.GameData;

import java.util.*;

public class PostloginUI {

    private final ServerFacade facade;
    private final AuthData auth;
    private final Scanner scanner;

    // actual game id
    private final Map<Integer, Integer> gameNumberToID = new HashMap<>();

    public PostloginUI(ServerFacade facade, AuthData auth, Scanner scanner) {
        this.facade = facade;
        this.auth = auth;
        this.scanner = scanner;
    }

    public void run() {
        System.out.println("Welcome, " + auth.username() + "! Type 'help' for options.");

        while (true) {
            System.out.print("[Postlogin] >>> ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "help" -> printHelp();
                case "logout" -> {
                    try {
                        facade.logout(auth.authToken());
                        System.out.println("Logged out!");
                        return;
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
                case "create" -> {
                    System.out.print("Game name: ");
                    String gameName = scanner.nextLine();
                    try {
                        facade.createGame(auth.authToken(), gameName);
                        System.out.println("Game created: " + gameName);
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
                case "list" -> {
                    try {
                        GameData[] games = facade.listGames(auth.authToken());
                        gameNumberToID.clear();
                        System.out.println("Games:");
                        for (int i = 0; i < games.length; i++) {
                            gameNumberToID.put(i + 1, games[i].gameID());
                            System.out.printf("  %d. \"%s\"  White: %s  Black: %s%n",
                                    i + 1,
                                    games[i].gameName(),
                                    orEmpty(games[i].whiteUsername()),
                                    orEmpty(games[i].blackUsername()));
                        }
                        if (games.length == 0) {
                            System.out.println("  (no games available)");
                        }
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
                case "play" -> {
                    try {
                        System.out.print("Enter game number to play: ");
                        int number = Integer.parseInt(scanner.nextLine().trim());
                        int gameID = gameNumberToID.getOrDefault(number, -1);
                        if (gameID == -1) { throw new Exception("Invalid game number");}


                        GameData[] games = facade.listGames(auth.authToken());
                        GameData selectedGame = null;
                        for (GameData game: games) {
                            if (game.gameID() == gameID) {
                                selectedGame = game;
                                break;
                            }
                        }


                        if (selectedGame == null) {
                            throw new Exception("Game not found");
                        }

                        String user = auth.username();
                        String color;

                        if (user.equals(selectedGame.whiteUsername())) {
                            color = "WHITE";
                            System.out.println("You are already WHITE in this game it will rejoin you.");
                        } else if (user.equals(selectedGame.blackUsername())) {
                            color = "BLACK";
                            System.out.println("You are already BLACK in this game it will rejoin you.");
                        } else {
                            System.out.print("Play as WHITE or BLACK: ");
                            color = scanner.nextLine().trim().toUpperCase();
                            facade.joinGame(auth.authToken(), gameID, color);
                            System.out.println("Joined game " + gameID + " as " + color);
                        }

                        System.out.println("Drawing your board");
                        ChessBoardUI.drawBoard(auth.authToken(), facade, gameID, color);



                        /*System.out.print("Play as WHITE or BLACK: ");
                        String color = scanner.nextLine().trim().toUpperCase();

                        facade.joinGame(auth.authToken(), gameID, color);
                        System.out.println("Joined game " + gameID + " as " + color);
                        System.out.println("Drawing initial board...");
                        ChessBoardUI.drawBoard(auth.authToken(), facade, gameID, color);
                        */



                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
                case "observe" -> {
                    try {
                        System.out.print("Enter game number to observe: ");
                        int number = Integer.parseInt(scanner.nextLine().trim());
                        int gameID = gameNumberToID.getOrDefault(number, -1);
                        if (gameID == -1) {throw new Exception("Invalid game number");}

                        facade.observeGame(auth.authToken(), gameID);
                        System.out.println("Observing game " + gameID + "...");
                        ChessBoardUI.drawBoard(auth.authToken(), facade, gameID, "WHITE");
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
                default -> System.out.println("Unknown command. Type 'help' to see options.");
            }
        }
    }

    private void printHelp() {
        System.out.println("""
            Commands:
              help       Show this menu
              logout     Log out of your account
              create     Create a new chess game
              list       List all available games
              play       Join a game as a player
              observe    Observe a game as a spectator
        """);
    }

    private String orEmpty(String value) {
        return value != null ? value : "(empty)";
    }
}
