package client;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import model.GameData;
import chess.*;
import java.util.Map;


public class ServerFacade {
    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
    }


    public ChessGame getGameState(String authToken, int gameID) throws Exception {
        var url = serverUrl + "/game/state?gameID=" + gameID;
        var conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", authToken);
        conn.setRequestProperty("Content-Type", "application/json");

        if (conn.getResponseCode() != 200) {
            try (var reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                throw new RuntimeException(reader.readLine());
            }
        }

        try (var in = new InputStreamReader(conn.getInputStream())) {
            return gson.fromJson(in, ChessGame.class);
        }
    }






    // register user pass email
    public AuthData register(String username, String password, String email) throws Exception {
        var user = new UserData(username, password, email);
        var conn = (HttpURLConnection) new URL(serverUrl + "/user").openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        try (var out = conn.getOutputStream()) {
            out.write(gson.toJson(user).getBytes());
        }


        if (conn.getResponseCode() != 200) {
            try (var reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                String errorJson = reader.readLine();
                Map<String, String> errorMap = gson.fromJson(errorJson, Map.class);
                String message = errorMap.getOrDefault("message", "Unknown error");
                throw new RuntimeException(message);
            }
        }

        try (var in = new InputStreamReader(conn.getInputStream())) {
            return gson.fromJson(in, AuthData.class);
        }
    }


    // login user pass no email
    public AuthData login(String username, String password) throws Exception {
        var user = new UserData(username, password, null);
        var conn = (HttpURLConnection) new URL(serverUrl + "/session").openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Content-Type", "application/json");

        try (var out = conn.getOutputStream()) {
            out.write(gson.toJson(user).getBytes());
        }

        if (conn.getResponseCode() != 200) {
            try (var reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                String errorJson = reader.readLine();
                Map<String, String> errorMap = gson.fromJson(errorJson, Map.class);
                String message = errorMap.getOrDefault("message", "Unknown error");
                throw new RuntimeException(message);
            }
        }

        try (var in = new InputStreamReader(conn.getInputStream())) {
            return gson.fromJson(in, AuthData.class);
        }
    }

    public void logout(String authToken) throws Exception {
        var conn = (HttpURLConnection) new URL(serverUrl + "/session").openConnection();
        conn.setRequestMethod("DELETE");
        conn.setRequestProperty("Authorization", authToken);

        if (conn.getResponseCode() != 200) {
            try (var reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                String errorJson = reader.readLine();
                Map<String, String> errorMap = gson.fromJson(errorJson, Map.class);
                String message = errorMap.getOrDefault("message", "Unknown error");
                throw new RuntimeException(message);
            }
        }
    }


    // list off games
    public GameData[] listGames(String authToken) throws Exception {
        var conn = (HttpURLConnection) new URL(serverUrl + "/game").openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Authorization", authToken);
        conn.setRequestProperty("Content-Type", "application/json");

        if (conn.getResponseCode() != 200) {
            try (var reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                String errorJson = reader.readLine();
                Map<String, String> errorMap = gson.fromJson(errorJson, Map.class);
                String message = errorMap.getOrDefault("message", "Unknown error");
                throw new RuntimeException(message);
            }
        }

        try (var in = new InputStreamReader(conn.getInputStream())) {
            return gson.fromJson(in, GameListWrapper.class).games;
        }
    }

    private static class GameListWrapper {
        GameData[] games;
    }

    public void createGame(String authToken, String gameName) throws Exception {
        var conn = (HttpURLConnection) new URL(serverUrl + "/game").openConnection();
        conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", authToken);
        conn.setRequestProperty("Content-Type", "application/json");

        // request
        var body = gson.toJson(new GameNameWrapper(gameName));
        try (var out = conn.getOutputStream()) {
            out.write(body.getBytes());
        }

        if (conn.getResponseCode() != 200) {
            try (var reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                String errorJson = reader.readLine();
                Map<String, String> errorMap = gson.fromJson(errorJson, Map.class);
                String message = errorMap.getOrDefault("message", "Unknown error");
                throw new RuntimeException(message);
            }
        }
    }

    // add in
    private static class GameNameWrapper {
        String gameName;
        GameNameWrapper(String name) {
            this.gameName = name;
        }
    }


    public void joinGame(String authToken, int gameID, String playerColor) throws Exception {
        var conn = (HttpURLConnection) new URL(serverUrl + "/game").openConnection();
        conn.setRequestMethod("PUT");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", authToken);
        conn.setRequestProperty("Content-Type", "application/json");

        var body = gson.toJson(new JoinGameRequest(playerColor, gameID));
        try (var out = conn.getOutputStream()) {
            out.write(body.getBytes());
        }

        if (conn.getResponseCode() != 200) {
            try (var reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                String errorJson = reader.readLine();
                Map<String, String> errorMap = gson.fromJson(errorJson, Map.class);
                String message = errorMap.getOrDefault("message", "Unknown error");
                throw new RuntimeException(message);
            }
        }
    }

    private static class JoinGameRequest {
        String playerColor;
        int gameID;

        JoinGameRequest(String playerColor, int gameID) {
            this.playerColor = playerColor;
            this.gameID = gameID;
        }
    }





    public void observeGame(String authToken, int gameID) throws Exception {
        var conn = (HttpURLConnection) new URL(serverUrl + "/game").openConnection();
        conn.setRequestMethod("PUT");
        conn.setDoOutput(true);
        conn.setRequestProperty("Authorization", authToken);
        conn.setRequestProperty("Content-Type", "application/json");

        var body = gson.toJson(new JoinGameRequest("OBSERVER" , gameID));
        try (var out = conn.getOutputStream()) {
            out.write(body.getBytes());
        }

        if (conn.getResponseCode() != 200) {
            try (var reader = new BufferedReader(new InputStreamReader(conn.getErrorStream()))) {
                String errorJson = reader.readLine();
                Map<String, String> errorMap = gson.fromJson(errorJson, Map.class);
                String message = errorMap.getOrDefault("message", "Unknown error");
                throw new RuntimeException(message);
            }
        }
    }




}
