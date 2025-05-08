import java.net.*;
import java.util.*;
import java.io.*;
import java.awt.*;

public class GameStarter {
    
    private String host;
    private int clientID, port;

    private GameCanvas gc;
    private GameFrame gf;

    private int w, h;

    private Socket s;
    private DataInputStream in;
    private DataOutputStream out;

    private ReadFromServer rfs;
    private WriteToServer wts;

    // private Player player1, player2;
    

    // private CSConnection csc;

    public GameStarter(String host, int port) {
        this.host = host;
        this.port = port;

        // connectToServer();
    }

    private void connectToServer() {
        try {
            s = new Socket(host, port);
            in = new DataInputStream(s.getInputStream());
            out = new DataOutputStream(s.getOutputStream());

            clientID = in.readInt();
            w = in.readInt();
            h = in.readInt();

            System.out.printf("Connected to server as Player %d\n", clientID);

            rfs = new ReadFromServer(in);
            wts = new WriteToServer(out);

            gc = new GameCanvas(clientID, w, h);
            gf = new GameFrame(clientID, w, h, gc);
            gf.setUpGUI();

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private class ReadFromServer implements Runnable {

        private DataInputStream in;

        public ReadFromServer(DataInputStream in) {
            this.in = in;
            System.out.println("RFS Runnable created.");
        }

        @Override
        public void run() {

        }
    }

    private class WriteToServer implements Runnable {

        private DataOutputStream out;

        public WriteToServer(DataOutputStream out) {
            this.out = out;
            System.out.println("WTS Runnable created.");
        }

        @Override
        public void run() {
            
            
        }
    }

    public static void main(String[] args) {
        GameStarter gs = new GameStarter("localhost", 9452);
        // gs.setUpGameEntities();
        gs.connectToServer();
    }
}
