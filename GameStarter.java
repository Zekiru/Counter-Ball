/**
    The GameStarter, a class that runs the Client side of the Game.
    Establishes a connection with the GameServer. 
    @author Ezekiel Villasurda (236689)
    @version 20 May 2025
    I have not discussed the Java language code in our program
    with anyone other than my instructor or the teaching assistants
    assigned to this course.
    I have not used Java language code obtained from another student,
    or any other unauthorized source, either modified or unmodified.
    If any Java language code or documentation used in my program
    was obtained from another source, such as a textbook or website,
    that has been clearly noted with a proper citation in the comments
    of my program.
**/

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
    private double ballSize, ballVelocity, ballX, ballY;
    private int id1, id2, playerLives;
    private Player player, opponent;
    private double playerSize, playerRange, playerVelocity;
    private double playerX, playerY, opponentX, opponentY;

    private int w, h, rfsCount, wtsCount;

    private Socket s;
    private DataInputStream in;
    private DataOutputStream out;

    private ReadFromServer rfs;
    private WriteToServer wts;

    private boolean active = false, reset = false;

    // Constructor that takes the needed hostname/ip address and port number
    public GameStarter(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // A Method for setting up a new set of Game Entities
    private void setUpGameEntities() {
        ball = new Ball(ballX, ballY, ballSize, ballVelocity, Color.BLACK);
        player = new Player(id1, playerX, playerY, playerSize, playerVelocity, playerRange, Color.BLUE, playerLives);
        opponent = new Player(id2, opponentX, opponentY, playerSize, playerVelocity, playerRange, Color.RED, playerLives);
    }

    // Resets the variables and objects of this client, aling with resetting the GameCanvas
    public void reset() {
        setUpGameEntities();
        gc.resetGame(ball, player, opponent);
    }

    // On a successful connection attempt, initialize the default values for the Entities.
    private void setUpConnection(DataInputStream in) {
        try {
            id1 = clientID;
            id2 = (clientID == 2) ? 1 : 2;

            // Delay (Sleep time in miliseconds)
            interval = in.readInt();

            // Read/Write Count
            rfsCount = in.readInt();
            wtsCount = in.readInt();

            // Frame Attributes
            w = in.readInt();
            h = in.readInt();

            // Ball Attributes
            ballSize = in.readDouble();
            ballVelocity = in.readDouble();

            // Ball Position
            ballX = in.readDouble();
            ballY = in.readDouble();
            
            // Player Attributes
            playerLives = in.readInt();
            playerSize = in.readDouble();
            playerRange = in.readDouble();
            playerVelocity = in.readDouble();

            // Position
            playerX = in.readDouble();
            playerY = in.readDouble();
            opponentX = in.readDouble();
            opponentY = in.readDouble();

            setUpGameEntities();

        } catch (Exception e) {
            System.out.println("Failed to set up connection.");
        }
    }

    // Attempts to establish a connection with the GameServer
    private void connectToServer() {
        try {
            s = new Socket(host, port);
            in = new DataInputStream(s.getInputStream());
            out = new DataOutputStream(s.getOutputStream());

            clientID = in.readInt();
            setUpConnection(in);

            setUpGameEntities();
            gc = new GameCanvas(w, h, ball, player, opponent);
            gf = new GameFrame(clientID, gc);

            System.out.printf("Connected to server as Player %d\n", clientID);

            rfs = new ReadFromServer(interval, in);
            wts = new WriteToServer(interval, out);

            rfs.waitForStartMsg();

            // Start Game:

            gf.setUpGUI();
            gc.setUpGameEntities();
            gc.setUpListeners();
            

        } catch (IOException e) {
            System.out.println("Failed to Connect to the Server.");
            // System.out.println(e);
            System.exit(0);
        }
    }

    // Runs all the necesary Threads responsible for handling the I/O Streams
    private void runThreads() {
        ArrayList<Thread> threads = new ArrayList<Thread>();
        threads.add(new Thread(rfs));
        threads.add(new Thread(wts));

        for (Thread t : threads) t.start();
    }

    // The class that handles inputs from the server.
    // Runs on a looped thread.
    private class ReadFromServer extends AsyncTask {

        private DataInputStream in;

        // Contructor that takes in the interval between loops and the I/O Stream
        public ReadFromServer(int interval, DataInputStream in) {
            super(interval);

            this.in = in;
            // System.out.println("RFS Runnable created.");
        }

        // The Input loop
        @Override
        public void runnable() {
            try {
                ArrayList<Double> read = new ArrayList<Double>();

                boolean serverActive = in.readBoolean();

                if (!active && serverActive) {
                    active = true;
                    reset = false;
                    gc.setActive(true);
                }

                if (opponent != null) opponent.setLives(in.readInt());

                for (int i = 0; i < rfsCount; i++) read.add(in.readDouble());

                if (ball != null) {
                    ball.setX(read.get(0));
                    ball.setY(read.get(1));
                }

                if (opponent != null) {
                    opponent.setX(read.get(2));
                    opponent.setY(read.get(3));
                    opponent.setR(read.get(4));
                    opponent.setGracedColor(in.readBoolean());

                    boolean isHit = in.readBoolean();

                    // player.setActive(!isHit);
                    opponent.setHitColor(isHit);
                }

                // Game Over:
                in.readBoolean();

                // Reset:
                if (in.readBoolean() && !reset) {
                    active = false;
                    reset = true;
                    reset();
                }

            } catch (IOException e) {
                // System.out.println(e);
                this.endTask();
                if (!wts.isRunning()) connectionLost();
            }
        }  

        // Receives the Start Messgae from the Server
        public void waitForStartMsg() {
            try {
                String startMsg = in.readUTF();
                System.out.println("Message from server: " + startMsg);

                runThreads();
            } catch (IOException e) {
                System.out.println("Failed Server-Client Handshake.");
                System.out.println(e);
                System.exit(0);
            }
        }
    }

    // The class that handles outputs to the server.
    // Runs on a looped thread.
    private class WriteToServer extends AsyncTask {

        private DataOutputStream out;

        // Contructor that takes in the interval between loops and the I/O Stream
        public WriteToServer(int interval, DataOutputStream out) {
            super(interval);

            this.out = out;
            // System.out.println("WTS Runnable created.");
        }

        // The Output loop
        @Override
        public void runnable() {
            try {
                // Player Data:
                ArrayList<Double> write = new ArrayList<Double>();

                write.add(player.getX());
                write.add(player.getY());
                write.add(gc.getMX());
                write.add(gc.getMY());
                write.add(player.getR());

                for (int i = 0; i < wtsCount; i++) out.writeDouble(write.get(i));

                out.writeInt(player.getLives());
                out.writeDouble(player.getPower());
                out.writeBoolean(gc.isDeflected());
                out.writeBoolean(player.isGraced());
                out.writeBoolean(player.isHit());

                // Game Data:
                out.writeBoolean(gc.wantsReset());
                out.writeBoolean(gc.isGameOver());

                out.flush();

            } catch (IOException e) {
                // System.out.println(e);
                this.endTask();
                if (!rfs.isRunning()) connectionLost();
            }
            
        }
    }

    // Handles Connection Loss to the GameServer
    private void connectionLost() {
        System.out.println("Connection to Server Lost.\nTerminating Program.\n");
        System.exit(0);
    }

    // Allows instantiating this class using the CMD
    public static void main(String[] args) {
        String localHost = "localhost";

        GameStarter gs = new GameStarter(localHost, 9452);
        gs.connectToServer();
    }
}
