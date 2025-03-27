package ui;

import client.ServerFacade;
import model.AuthData;

import java.util.Scanner;

public class PreloginUI {

    private final ServerFacade facade;
    private final Scanner scanner;

    public PreloginUI(ServerFacade facade, Scanner scanner) {
        this.facade = facade;
        this.scanner = scanner;
    }

    public AuthData run() {
        System.out.println("Welcome to my Chess game type 'help' for options.");

        while (true) {
            System.out.print("[Prelogin] >>> ");
            String input = scanner.nextLine().trim().toLowerCase();

            switch (input) {
                case "help" -> printHelp();
                case "quit" -> {
                    System.out.println("Bye");
                    System.exit(0);
                }
                case "register" -> {
                    System.out.print("Username: ");
                    String username = scanner.nextLine();
                    System.out.print("Password: ");
                    String password = scanner.nextLine();
                    System.out.print("Email: ");
                    String email = scanner.nextLine();
                    try {
                        var auth = facade.register(username, password, email);
                        System.out.println("Registered as " + username);
                        return auth;
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
                case "login" -> {
                    System.out.print("Username: ");
                    String username = scanner.nextLine();
                    System.out.print("Password: ");
                    String password = scanner.nextLine();
                    try {
                        var auth = facade.login(username, password);
                        System.out.println("Logged in as " + username);
                        return auth;
                    } catch (Exception e) {
                        System.out.println("Error: " + e.getMessage());
                    }
                }
                default -> System.out.println("Not an option. Type 'help' to see options.");
            }
        }
    }

    private void printHelp() {
        System.out.println("""
            Options:
              help      Show help menu
              quit      Exit
              register  Create a new account
              login     Log into a previous made account
        """);
    }
}
