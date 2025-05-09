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
            // Delay (Sleep time in miliseconds)
            delay = in.readInt();

            // Read/Write Count
            rfsCount = in.readInt();
            wtsCount = in.readInt();

            // Frame Attributes
            w = in.readInt();
            h = in.readInt();

            // Ball Attributes
            double ballSize = in.readDouble();
            double ballSpeed = in.readDouble();

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

            ball = new Ball(w, h, delay, ballX, ballY, ballSize, ballSpeed);
            player = new Player(playerX, playerY, playerSize, playerRange, Color.BLUE);
            opp = new Player(oppX, oppY, playerSize, playerRange, Color.RED);

            ball.setSpeed(ballSpeed);
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

            gc = new GameCanvas(w, h, clientID, ball, player, opp);
            gf = new GameFrame(w, h, clientID, gc);

            System.out.printf("Connected to server as Player %d\n", clientID);

            rfs = new ReadFromServer(in);
            wts = new WriteToServer(out);

            rfs.waitForStartMsg();

            gf.setUpGUI();

            // gf = new GameFrame(w, h, clientID, ball, player, opp);
            // gf.setUpGUI();

            // System.out.printf("(%f, %f)", player.getX(), player.getY());

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

                    for (int i = 0; i < rfsCount; i++) read.add(in.readDouble());

                    if (ball != null) {
                        ball.setX(read.get(0));
                        ball.setY(read.get(1));
                    }

                    if (opp != null) {
                        opp.setX(read.get(2));
                        opp.setY(read.get(3));
                        opp.setRotation(read.get(4));
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
                    write.add(player.getRotation());

                    for (int i = 0; i < wtsCount; i++) out.writeDouble(write.get(i));

                    out.writeBoolean(gc.getBallDeflected());

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
