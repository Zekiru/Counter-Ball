/**
    The Game Server, required to run the game on a network.
    Connects with a max player size of 2.
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
import java.io.*;
import java.awt.*;
import java.util.ArrayList;

public class GameServer extends AsyncTask{

    private static final int maxConnections = 2;
    private static final int w = 1024, h = 768, rfcCount = 5, wtcCount = 5;
    private static final double ballSize = 120, ballVelocity = 1;
    private static final double playerSize = 70, playerRange = 100, playerVelocity = 5;
    private static final int lives = 3;

    private int port, connections;
    private ServerSocket ss;

    private Ball b;
    // private Player player1, player2;
    private int p1Lives = lives, p2Lives = lives;
    private double bX, bY, p1X, p1Y, p1R, m1X, m1Y, p2X, p2Y, p2R, m2X, m2Y;
    private double power = 0;
    private boolean p1Graced, p1Hit, p2Graced, p2Hit;
    

    // private Socket p1, p2;
    private ReadFromClient p1RFC, p2RFC;
    private WriteToClient p1WTC, p2WTC;

    private boolean p1Connected = false, p2Connected = false;
    private boolean gameOver = false, active = false, reset = false;
    private boolean p1Reset = false, p2Reset = false;
    
    // Constructor that takes the needed Port and the Interval between loops
    public GameServer(int port, int interval) {
        super(interval); // Milliseconds between loops
        this.port = port;
        this.connections = 0;

        setUpInitialData();

        try {
            System.out.println("Starting GameServer...");
            ss = new ServerSocket(this.port);
        } catch (IOException e) {
            // ...
        }

    }

    // Sets up the initial Game Data before running the Server
    private void setUpInitialData() {
        p1Lives = lives;
        p2Lives = lives;

        double offsetXP1 = w*0.25 - playerSize/2;
        double offsetXP2 = w*0.75 - playerSize/2;
        double offsetY = (h - playerSize) / 2;

        bX = (w - ballSize) / 2;
        bY = (h - ballSize) / 2;

        p1X = offsetXP1;
        p1Y = offsetY;

        p2X = offsetXP2;
        p2Y = offsetY;

        b = new Ball(bX, bY, ballSize, ballVelocity, Color.BLACK);
    }

    // On a successful connection, output the initial Game Data values to the Client
    private void setUpConnection(int clientID, DataOutputStream out) {
        try {
            // Delay (Sleep time in miliseconds)
            out.writeInt(interval);

            // Read/Write Count
            out.writeInt(wtcCount);
            out.writeInt(rfcCount);

            // Frame Attributes
            out.writeInt(w);
            out.writeInt(h);

            // Ball Attributes
            out.writeDouble(ballSize);
            out.writeDouble(ballVelocity);

            // Ball Position
            out.writeDouble(bX);
            out.writeDouble(bY);
            
            // Player Attributes
            out.writeInt(lives);
            out.writeDouble(playerSize);
            out.writeDouble(playerRange);
            out.writeDouble(playerVelocity);

            // Player Position
            out.writeDouble((clientID == 1) ? p1X : p2X);
            out.writeDouble(p1Y);
            out.writeDouble((clientID == 2) ? p2X : p1X);
            out.writeDouble(p2Y);

            out.flush();
        } catch (Exception e) {
            System.out.println("Failed to set up connection.");
        }

    }

    // Starts accepting connections from Clients (Max of 2)
    public void acceptConnections() {
        try {
            System.out.println("Waiting for connections...");
            while (connections < maxConnections) {
                Socket s = ss.accept();
                DataInputStream in = new DataInputStream(s.getInputStream());
                DataOutputStream out = new DataOutputStream(s.getOutputStream());

                connections++;
                out.writeInt(connections);

                setUpConnection(connections, out);

                System.out.printf("Player %d has Connected.\n", connections);

                ReadFromClient rfc = new ReadFromClient(interval, connections, in);
                WriteToClient wtc = new WriteToClient(interval, connections, out);

                if (connections == 1) {
                    // p1 = s;
                    p1RFC = rfc;
                    p1WTC = wtc;
                    p1Connected = true;
                } else {
                    // p2 = s;
                    p2RFC = rfc;
                    p2WTC = wtc;
                    p2Connected = true;

                    runThreads();
                }
            }

            System.out.println("Starting Game.");

            // Run Game Below:
            startGame();
            this.startTask();

        } catch (IOException e) {
            System.out.println("Failed to accept connection/s.");
        }
    }

    // Runs all the necesary Threads responsible for handling the I/O Streams
    private void runThreads() {
        p1WTC.sendStartMsg();
        p2WTC.sendStartMsg();

        ArrayList<Thread> threads = new ArrayList<Thread>();
        threads.add(new Thread(p1RFC));
        threads.add(new Thread(p2RFC));
        threads.add(new Thread(p1WTC));
        threads.add(new Thread(p2WTC));

        for (Thread t : threads) t.start();
    }

    // The class that handles inputs from the client.
    // Runs on a looped thread.
    private class ReadFromClient extends AsyncTask {

        private int clientID;
        private DataInputStream in;

        // Contructor that takes in the interval between loops, the Client ID and the I/O Stream
        public ReadFromClient(int interval, int clientID, DataInputStream in) {
            super(interval);
            this.clientID = clientID;
            this.in = in;
            // System.out.printf("RFC Player %d Runnable created.\n", clientID);
        }

        // The Input loop
        @Override
        public void runnable() {
            try {
                ArrayList<Double> read = new ArrayList<Double>();

                for (int i = 0; i < rfcCount; i++) read.add(in.readDouble());

                if (clientID == 1) {
                    p1X = read.get(0);
                    p1Y = read.get(1);
                    m1X = read.get(2);
                    m1Y = read.get(3);
                    p1R = read.get(4);
                    p1Lives = in.readInt();
                    power = in.readDouble();

                    if (in.readBoolean()) b.redirectTowards(m1X, m1Y, power);

                    p1Graced = in.readBoolean();
                    p1Hit = in.readBoolean();
                } else {
                    p2X = read.get(0);
                    p2Y = read.get(1);
                    m2X = read.get(2);
                    m2Y = read.get(3);
                    p2R = read.get(4);
                    p2Lives = in.readInt();
                    power = in.readDouble();

                    if (in.readBoolean()) b.redirectTowards(m2X, m2Y, power);

                    p2Graced = in.readBoolean();
                    p2Hit = in.readBoolean();
                }

                // Game Data:
                boolean reset = in.readBoolean();;

                if (clientID == 1) {
                    p1Reset = (!p1Reset) ? reset : true;
                } else {
                    p2Reset = (!p2Reset) ? reset : true;
                }

                gameOver = in.readBoolean();

            } catch (IOException e) {
                // System.out.printf("RFC Failed for Player %d\n", clientID);
                this.endTask();
            }
        }
    }

    // The class that handles outputs to the client.
    // Runs on a looped thread.
    private class WriteToClient extends AsyncTask {

        private int clientID;
        private DataOutputStream out;

        // Contructor that takes in the interval between loops, the Client ID and the I/O Stream
        public WriteToClient(int interval, int clientID, DataOutputStream out) {
            super(interval);

            this.clientID = clientID;
            this.out = out;
            // System.out.printf("WTC Player %d Runnable created.\n", clientID);
        }

        // The Output loop
        @Override
        public void runnable() {
            try {
                ArrayList<Double> write = new ArrayList<Double>();

                out.writeBoolean(active);

                write.add(b.getX());
                write.add(b.getY());

                if (clientID == 1) {
                    out.writeInt(p2Lives);
                    write.add(p2X);
                    write.add(p2Y);
                    write.add(p2R);
                } else {
                    out.writeInt(p1Lives);
                    write.add(p1X);
                    write.add(p1Y);
                    write.add(p1R);
                }

                for (int i = 0; i < wtcCount; i++) out.writeDouble(write.get(i));


                if (clientID == 1) {
                    out.writeBoolean(p2Graced);
                    out.writeBoolean(p2Hit);
                } else {
                    out.writeBoolean(p1Graced);
                    out.writeBoolean(p1Hit);
                }
                
                out.writeBoolean(gameOver);
                out.writeBoolean(reset);

                out.flush();

            } catch (IOException e) {
                // System.out.printf("WTC Failed for Player %d\n", clientID);
                this.endTask();
            }
        }

        // Send a Start Message to the CLient
        public void sendStartMsg() {
            try {
                out.writeUTF("Starting the game. Enjoy!");
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    // Initiates the Start of the Game, with a 3 second pause before actually starting
    private void startGame() {
        active = false;
        AsyncTask startProcess = new AsyncTask(1000, 3) {
            
            @Override
            protected void runnable() {}

            @Override
            protected void finish() {
                active = true;
                gameOver = false;
                reset = false;
                b.startProcess(w, h);
            }
        };

        startProcess.startTask();
    }

    // Resets the Entire Game
    private void reset() {
        reset = true;
        setUpInitialData();
        startGame();
    }

    // The game loop of the Server
    @Override
    protected void runnable() {
        // Handle Ball Logic:
        if (!gameOver) {
            b.setActive(!p1Hit && !p2Hit);
            if (!b.isActive()) b.resetVelocity();
        }

        // Handle Game Over:
        if ((p1Lives <= 0 || p2Lives <= 0) && !gameOver) {
            gameOver =  true;
            active = false;
            b.endProcess();
            // reset();
        }

        // Handle Reset:
        if (p1Reset && p2Reset) {
            p1Reset = false;
            p2Reset = false;
            reset();
        }

        // Handle Disconnections:
        if (!p1RFC.isRunning() && !p1WTC.isRunning() && p1Connected) {
            p1Connected = false;
            System.out.println("Player 1 has Disconnected");
        }

        if (!p2RFC.isRunning() && !p2WTC.isRunning() && p2Connected) {
            p2Connected = false;
            System.out.println("Player 2 has Disconnected");
        }

        if (!p1Connected && !p2Connected) {
            System.out.println("Both Players have Disconnected. Terminating Program.");
            this.endTask();
        }
    }

    // Runs instructions when the Server loop ends
    @Override
    protected void finish() {
        if (p1Lives > 0 && p2Lives > 0) System.exit(0);
        System.out.println("Game Over!");
        System.out.printf("Player %d Wins!\n", ((p1Lives > p2Lives) ? 1 : 2));
        System.exit(0);
    }

    // Allows instantiating this class using the CMD
    public static void main(String[] args) {
        int interval = 10; // Milliseconds between loops
        GameServer gs = new GameServer(9452, interval);
        gs.acceptConnections();
    }

}
