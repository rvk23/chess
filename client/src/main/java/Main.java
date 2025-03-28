import chess.*;
import java.util.Scanner;
import client.ServerFacade;
import model.AuthData;
import ui.PreloginUI;
import ui.PostloginUI;


public class Main {
    public static void main(String[] args) {
        System.out.println("♕ 240 Chess Client");

        int port = 8080;
        ServerFacade facade = new ServerFacade(port);
        Scanner scanner = new Scanner(System.in);

        while (true) {
            PreloginUI prelogin = new PreloginUI(facade, scanner);
            AuthData auth = prelogin.run();

            PostloginUI postlogin = new PostloginUI(facade, auth, scanner);
            postlogin.run();
        }

    }
}