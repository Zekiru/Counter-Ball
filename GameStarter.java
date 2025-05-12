import java.net.*;
import java.util.*;
import java.io.*;
import java.awt.*;

public class GameStarter {
    
    private String host;
    private int clientID, port, interval;
    private GameCanvas gc;
    private GameFrame gf;

    private Ball ball;
    private int playerLives, oppLives;
    private Player player, opponent;
    private double playerSize, playerRange, playerVelocity;
    // private boolean ballDeflected = false;

    private int w, h, rfsCount, wtsCount;

    private Socket s;
    private DataInputStream in;
    private DataOutputStream out;

    private ReadFromServer rfs;
    private WriteToServer wts;

    public GameStarter(String host, int port) {
        this.host = host;
        this.port = port;
    }

    private void setUpConnection(DataInputStream in) {
        try {
            int id1 = clientID;
            int id2 = (clientID == 2) ? 1 : 2;

            // Delay (Sleep time in miliseconds)
            interval = in.readInt();

            // Read/Write Count
            rfsCount = in.readInt();
            wtsCount = in.readInt();

            // Frame Attributes
            w = in.readInt();
            h = in.readInt();

            // Ball Attributes
            double ballSize = in.readDouble();
            double ballVelocity = in.readDouble();

            // Ball Position
            double ballX = in.readDouble();
            double ballY = in.readDouble();
            
            // Player Attributes
            playerLives = in.readInt();
            playerSize = in.readDouble();
            playerRange = in.readDouble();
            playerVelocity = in.readDouble();

            // Position
            double playerX = in.readDouble();
            double playerY = in.readDouble();
            double opponentX = in.readDouble();
            double opponentY = in.readDouble();

            ball = new Ball(ballX, ballY, ballSize, ballVelocity, Color.BLACK);
            player = new Player(id1, playerX, playerY, playerSize, playerVelocity, playerRange, Color.BLUE, playerLives);
            opponent = new Player(id2, opponentX, opponentY, playerSize, playerVelocity, playerRange, Color.RED, playerLives);

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

            gc = new GameCanvas(w, h, clientID, ball, player, opponent);
            gf = new GameFrame(w, h, clientID, gc);

            System.out.printf("Connected to server as Player %d\n", clientID);

            rfs = new ReadFromServer(in);
            wts = new WriteToServer(out);

            rfs.waitForStartMsg();

            gf.setUpGUI();

        } catch (IOException e) {
            System.out.println(e);
        }
    }

    private void runThreads() {
        ArrayList<Thread> threads = new ArrayList<Thread>();
        threads.add(new Thread(rfs));
        threads.add(new Thread(wts));

        for (Thread t : threads) t.start();
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
                    ArrayList<Double> read = new ArrayList<Double>();

                    oppLives = in.readInt();

                    for (int i = 0; i < rfsCount; i++) read.add(in.readDouble());

                    if (ball != null) {
                        ball.setX(read.get(0));
                        ball.setY(read.get(1));
                    }

                    if (opponent != null) {
                        opponent.setX(read.get(2));
                        opponent.setY(read.get(3));
                        opponent.setR(read.get(4));
                    }
                    
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
                    ArrayList<Double> write = new ArrayList<Double>();

                    write.add(player.getX());
                    write.add(player.getY());
                    write.add(gc.getMX());
                    write.add(gc.getMY());
                    write.add(player.getR());

                    for (int i = 0; i < wtsCount; i++) out.writeDouble(write.get(i));

                    out.writeInt(player.getLives());
                    out.writeBoolean(gc.isDeflected());

                    out.flush();

                    try {
                        Thread.sleep(interval);
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
        String localHost = "localhost";
        String host = "192.168.100.53";

        GameStarter gs = new GameStarter(host, 9452);
        // gs.setUpGameEntities();
        gs.connectToServer();
    }
}
