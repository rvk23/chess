package client;

import com.google.gson.Gson;
import model.AuthData;
import model.UserData;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerFacade {
    private final String serverUrl;
    private final Gson gson = new Gson();

    public ServerFacade(int port) {
        this.serverUrl = "http://localhost:" + port;
    }

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
                throw new RuntimeException(reader.readLine());
            }
        }

        try (var in = new InputStreamReader(conn.getInputStream())) {
            return gson.fromJson(in, AuthData.class);
        }
    }
}
