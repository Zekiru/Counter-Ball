import java.net.*;
import java.io.*;
import java.awt.*;

// import java.awt.event.*;
// import javax.swing.*;

import java.util.ArrayList;
// import java.util.Random;

public class GameServer {

    private static final int maxConnections = 2, interval = 10;
    private static final int w = 1024, h = 768, rfcCount = 5, wtcCount = 5;
    private static final double ballSize = 120, ballVelocity = 10;
    private static final double playerSize = 70, playerRange = 100, playerSpeed = 5;

    private int port, connections;
    private ServerSocket ss;

    private Ball b;
    private double bX, bY, p1X, p1Y, p1R, m1X, m1Y, p2X, p2Y, p2R, m2X, m2Y;
    private boolean ballDeflected = false;
    

    private Socket p1, p2;
    private ReadFromClient p1RFC, p2RFC;
    private WriteToClient p1WTC, p2WTC;
    
    public GameServer(int port) {
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

        b = new Ball(bX, bY, ballSize, ballVelocity, Color.BLACK, w, h, interval);

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
            out.writeDouble(playerSize);
            out.writeDouble(playerRange);
            out.writeDouble(playerSpeed);

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

                    runThreads();
                }
            }

            System.out.println("Starting Game.");

            // Thread t = new Thread(b);
            // t.start();
            b.startRunnable();
            
            Thread detectDeflect = new Thread(() -> {
                try {
                    while (true) {
                        // System.out.println();
                        Thread.sleep(100);
                    }
                } catch(Exception e) {
                    System.out.println(e);
                }
            });

            detectDeflect.start();

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
            System.out.printf("RFC Player %d Runnable created.\n", clientID);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    ArrayList<Double> read = new ArrayList<Double>();

                    for (int i = 0; i < rfcCount; i++) read.add(in.readDouble());

                    if (clientID == 1) {
                        p1X = read.get(0);
                        p1Y = read.get(1);
                        m1X = read.get(2);
                        m1Y = read.get(3);
                        p1R = read.get(4);
                        if (in.readBoolean()) b.redirectTowards(m1X, m1Y);
                    } else {
                        p2X = read.get(0);
                        p2Y = read.get(1);
                        m2X = read.get(2);
                        m2Y = read.get(3);
                        p2R = read.get(4);
                        if (in.readBoolean()) b.redirectTowards(m2X, m2Y);
                    }
                    
                }
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    private class WriteToClient implements Runnable {

        private int clientID;
        private DataOutputStream out;

        public WriteToClient(int clientID, DataOutputStream out) {
            this.clientID = clientID;
            this.out = out;
            System.out.printf("WTC Player %d Runnable created.\n", clientID);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    ArrayList<Double> write = new ArrayList<Double>();

                    write.add(b.getX());
                    write.add(b.getY());

                    if (clientID == 1) {
                        write.add(p2X);
                        write.add(p2Y);
                        write.add(p2R);
                    } else {
                        write.add(p1X);
                        write.add(p1Y);
                        write.add(p1R);
                    }

                    for (int i = 0; i < wtcCount; i++) out.writeDouble(write.get(i));

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

        public void sendStartMsg() {
            try {
                out.writeUTF("Starting the game. Enjoy!");
            } catch (IOException e) {
                System.out.println(e);
            }
        }
    }

    public static void main(String[] args) {
        GameServer gs = new GameServer(9452);
        // gs.setUpGameEntities();
        gs.acceptConnections();
    }

}
