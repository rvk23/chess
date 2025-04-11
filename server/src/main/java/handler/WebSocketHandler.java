package handler;


import dataaccess.*;
import org.eclipse.jetty.websocket.api.*;
import model.*;
import websocket.commands.UserGameCommand;
import websocket.messages.ServerMessage;
import com.google.gson.Gson;
import chess.*;
import dataaccess.GameDAO;
import websocket.ConnectionManager;
import dataaccess.DataAccessException;


import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;





public class WebSocketHandler {

    private static final Gson gson = new Gson();

    private static final ConcurrentHashMap<Session, String> sessionAuthTokens = new ConcurrentHashMap<>();
    private static final GameDAO gameDAO = new GameDAO();
    private static final AuthDAO authDAO = new AuthDAO();

    private Session session;



    private void handleConnect(UserGameCommand command) {

        //stuff
    }




    private void handleMakeMove(UserGameCommand command) {

        //stuff
    }





    private void handleLeave(UserGameCommand command) {

        //stuff
    }

    private void handleResign(UserGameCommand command) {

        //stuff
    }







}
