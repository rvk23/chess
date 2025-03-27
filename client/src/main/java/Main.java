import chess.*;
import java.util.Scanner;
import client.ServerFacade;
import model.AuthData;
import ui.PreloginUI;
import ui.PostloginUI;


public class Main {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

        int port = 8080;
        ServerFacade facade = new ServerFacade(port);
        Scanner scanner = new Scanner(System.in);

        PreloginUI prelogin = new PreloginUI(facade, scanner);
        AuthData auth = prelogin.run();

        PostloginUI postlogin = new PostloginUI(facade, auth, scanner);
        postlogin.run();


    }
}