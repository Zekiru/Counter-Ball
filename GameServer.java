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
    
    public GameServer(int port, int interval) {
        super(interval); // Milliseconds between loops
        this.port = port;
        this.connections = 0;

        double offsetXP1 = w*0.25 - playerSize/2;
        double offsetXP2 = w*0.75 - playerSize/2;
        double offsetY = (h - playerSize) / 2;

        bX = (w - ballSize) / 2;
        bY = (h - ballSize) / 2;

        p1X = offsetXP1;
        p1Y = offsetY;

        p2X = offsetXP2;
        p2Y = offsetY;

        // player1 = new Player(1, p1X, p1Y, playerSize, playerVelocity, playerRange, Color.BLUE, lives);

        // player2 = new Player(1, p2X, p2Y, playerSize, playerVelocity, playerRange, Color.BLUE, lives);

        b = new Ball(bX, bY, ballSize, ballVelocity, Color.BLACK);

        try {
            System.out.println("Starting GameServer...");
            ss = new ServerSocket(this.port);
        } catch (IOException e) {
            // ...
        }

    }

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

            this.startTask();
            b.startProcess(w, h);

        } catch (IOException e) {
            System.out.println("Failed to accept connection/s.");
        }
    }

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

    private class ReadFromClient extends AsyncTask {

        private int clientID;
        private DataInputStream in;

        public ReadFromClient(int interval, int clientID, DataInputStream in) {
            super(interval);
            this.clientID = clientID;
            this.in = in;
            // System.out.printf("RFC Player %d Runnable created.\n", clientID);
        }

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
            } catch (IOException e) {
                // System.out.printf("RFC Failed for Player %d\n", clientID);
                this.endTask();
            }
        }
    }

    private class WriteToClient extends AsyncTask {

        private int clientID;
        private DataOutputStream out;

        public WriteToClient(int interval, int clientID, DataOutputStream out) {
            super(interval);

            this.clientID = clientID;
            this.out = out;
            // System.out.printf("WTC Player %d Runnable created.\n", clientID);
        }

        @Override
        public void runnable() {
            try {
                ArrayList<Double> write = new ArrayList<Double>();

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
                

                out.flush();

                try {
                    Thread.sleep(interval);
                } catch (InterruptedException e) {
                    System.out.println(e);
                }
            } catch (IOException e) {
                // System.out.printf("WTC Failed for Player %d\n", clientID);
                this.endTask();
            }
        }

        public void sendStartMsg() {
            try {
                out.writeUTF("Starting the game. Enjoy!");
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    @Override
    protected void runnable() {
        // Game Logic:
        b.setActive(!p1Hit && !p2Hit);
        if (!b.isActive()) b.resetVelocity();

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

    @Override
    protected void finish() { System.exit(0); }

    public static void main(String[] args) {
        int interval = 10; // Milliseconds between loops
        GameServer gs = new GameServer(9452, interval);
        gs.acceptConnections();
    }

}
