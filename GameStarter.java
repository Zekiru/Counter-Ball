import java.net.*;
import java.util.*;
import java.io.*;
import java.awt.*;

public class GameStarter {
    
    private String host;
    private int clientID, port, delay;

    private GameCanvas gc;
    private GameFrame gf;
    private Ball ball;
    private Player player, opp;
    private double playerSize, playerRange, playerSpeed;
    // private double playerX, playerY, playerMX, playerMY;
    // private double oppX, oppY, oppMX, oppMY;

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

        // this.player = new Player();

        // connectToServer();
    }

    private void setUpConnection(DataInputStream in) {
        try {
            // Delay (Sleep time in miliseconds)
            delay = in.readInt();

            // Frame Attributes
            w = in.readInt();
            h = in.readInt();

            // Ball Attributes
            double ballSize = in.readDouble();
            double ballInitSPeed = in.readDouble();

            // Ball Position
            double ballX = in.readDouble();
            double ballY = in.readDouble();
            
            // Player Attributes
            playerSize = in.readDouble();
            playerRange = in.readDouble();
            playerSpeed = in.readDouble();

            // Position
            double playerX = in.readDouble();
            double playerY = in.readDouble();
            double oppX = in.readDouble();
            double oppY = in.readDouble();

            // Initial Look Direction
            // double playerMX = in.readDouble();
            // double playerMY = in.readDouble();
            // double oppMX = in.readDouble();
            // double oppMY = in.readDouble();

            ball = new Ball(ballX, ballY, ballSize, Color.BLACK);
            player = new Player(playerX, playerY, playerSize, playerRange, Color.BLUE);
            opp = new Player(oppX, oppY, playerSize, playerRange, Color.RED);

            ball.setSpeed(ballInitSPeed);
            player.setSpeed(playerSpeed);
            opp.setSpeed(playerSpeed);

        } catch (Exception e) {
            System.out.println("Failed to set up connection.");
        }
    }

    private void connectToServer() {
        try {
            s = new Socket(host, port);
            in = new DataInputStream(s.getInputStream());
            out = new DataOutputStream(s.getOutputStream());

            clientID = in.readInt();
            setUpConnection(in);

            System.out.printf("Connected to server as Player %d\n", clientID);

            rfs = new ReadFromServer(in);
            wts = new WriteToServer(out);
            rfs.waitForStartMsg();

            GameFrame gf = new GameFrame(clientID, ball, player, opp, w, h);
            gf.setUpGUI();

            // System.out.printf("(%f, %f)", player.getX(), player.getY());

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void runThreads() {
        ArrayList<Thread> threads = new ArrayList<Thread>();
        threads.add(new Thread(rfs));
        threads.add(new Thread(wts));

        for (Thread t : threads) {
            t.start();
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
            try {
                while (true) {
                    if (opp == null || ball == null) continue;

                    ball.setX(in.readDouble());
                    ball.setY(in.readDouble());

                    opp.setX(in.readDouble());
                    opp.setY(in.readDouble());
                    opp.setRotation(in.readDouble());
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }

        public void waitForStartMsg() {
            try {
                String startMsg = in.readUTF();
                System.out.println("Message from server: " + startMsg);

                runThreads();
            } catch (IOException e) {
                System.out.println(e);
            }
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
            try {
                while (true) {
                    out.writeDouble(player.getX());
                    out.writeDouble(player.getY());
                    out.writeDouble(player.getRotation());
                    out.flush();

                    try {
                        Thread.sleep(delay);
                    } catch (InterruptedException e) {
                        System.out.println(e);
                    }
                }
            } catch (IOException e) {
                System.out.println(e);
            }
            
        }
    }

    public static void main(String[] args) {
        GameStarter gs = new GameStarter("localhost", 9452);
        // gs.setUpGameEntities();
        gs.connectToServer();
    }
}
