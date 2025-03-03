package dataaccess;

import model.AuthData;
import java.util.HashMap;
import java.util.Map;

public class AuthDAO {
    private final Map<String, AuthData> authTokens = new HashMap<>();

    public void createAuth(String authToken, String username) {
        authTokens.put(authToken, new AuthData(authToken, username));
    }

    public AuthData getAuth(String authToken) {
        return authTokens.get(authToken);
    }

    public void deleteAuth(String authToken) {
        authTokens.remove(authToken);
    }

    public void clear() {
        authTokens.clear();
    }
}
