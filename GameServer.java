import java.net.*;
import java.util.*;
import java.io.*;
import java.awt.*;
// import javax.swing.*;

public class GameServer {

    private static final int maxConnections = 2;
    private static final int frameW = 1024;
    private static final int frameH = 768;

    private int port, connections;
    private ServerSocket ss;
    private ArrayList<Thread> SSConnections = new ArrayList<Thread>();

    private GameCanvas gc;

    private Socket p1, p2;
    private ReadFromClient p1RFC, p2RFC;
    private WriteToClient p1WTC, p2WTC;
    
    public GameServer(int port) {
        this.port = port;
        this.connections = 0;

        gc = new GameCanvas(frameW, frameH);
        gc.setUpGameEntities();

        try {
            System.out.println("Starting GameServer...");
            ss = new ServerSocket(port);
        } catch (IOException e) {
            // ...
        }

    }

    public void acceptConnections() {
        try {
            System.out.println("Waiting for connections...");
            while (connections < maxConnections) {
                Socket s = ss.accept();
                DataInputStream in = new DataInputStream(s.getInputStream());
                DataOutputStream out = new DataOutputStream(s.getOutputStream());

                connections++;
                out.writeInt(connections);
                out.writeInt(frameW);
                out.writeInt(frameH);

                System.out.printf("Player %d has connected.\n", connections);

                ReadFromClient rfc = new ReadFromClient(connections, in);
                WriteToClient wtc = new WriteToClient(connections, out);

                if (connections == 1) {
                    p1 = s;
                    p1RFC = rfc;
                    p1WTC = wtc;
                } else {
                    p2 = s;
                    p2RFC = rfc;
                    p2WTC = wtc;
                }
            }

            System.out.println("Starting Game.");

            while (true) {

            }
        } catch (IOException e) {
            // ...
        }
    }

    private class ReadFromClient implements Runnable {

        private int clientID;
        private DataInputStream in;

        public ReadFromClient(int clientID, DataInputStream in) {
            this.clientID = clientID;
            this.in = in;
            System.out.printf("RFC Player %d Runnable created.", clientID);
        }

        @Override
        public void run() {

        }
    }

    private class WriteToClient implements Runnable {

        private int clientID;
        private DataOutputStream out;

        public WriteToClient(int clientID, DataOutputStream out) {
            this.clientID = clientID;
            this.out = out;
            System.out.printf("WTC Player %d Runnable created.", clientID);
        }

        @Override
        public void run() {
            Ball ball = (Ball) gc.getGE().get(2);

            try {
                out.writeDouble(ball.getX());
                out.writeDouble(ball.getY());
            } catch (Exception e) {
                // ...
            }
        }
    }

    private class SSConnection implements Runnable {

        private int clientID;
        private Socket s;
        private DataInputStream in;
        private DataOutputStream out;

        public SSConnection(int clientID, Socket s) {
            this.clientID = clientID;
            this.s = s;

            try {
                in = new DataInputStream(s.getInputStream());
                out = new DataOutputStream(s.getOutputStream());
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        @Override
        public void run() {
            try {
                out.writeInt(clientID);
                out.flush();

                while (true) {

                }
            } catch (IOException e) {
                // ...
            }
            
        }
    }

    public static void main(String[] args) {
        GameServer gs = new GameServer(9452);
        // gs.setUpGameEntities();
        gs.acceptConnections();
    }
}
