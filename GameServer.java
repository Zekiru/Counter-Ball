import java.net.*;
import java.io.*;
import java.awt.*;

import java.awt.event.*;
import javax.swing.*;

import java.util.ArrayList;
import java.util.Random;

public class GameServer {

    private static final int maxConnections = 2, delay = 10;
    private static final int frameW = 1024, frameH = 768;
    private static final double ballSize = 120, ballInitSPeed = 10;
    private static final double playerSize = 70, playerRange = 100, playerSpeed = 5;

    private int port, connections;
    private ServerSocket ss;
    // private ArrayList<Thread> SSConnections = new ArrayList<Thread>();

    private ServerBall sb;
    private double bX, bY, p1X, p1Y, p2X, p2Y, p1R, p2R;

    // private GameCanvas gc;
    // private Timer timer;
    

    private Socket p1, p2;
    private ReadFromClient p1RFC, p2RFC;
    private WriteToClient p1WTC, p2WTC;
    
    public GameServer(int port) {
        this.port = port;
        this.connections = 0;

        double offsetXP1 = frameW*0.25 - playerSize/2;
        double offsetXP2 = frameW*0.75 - playerSize/2;
        double offsetY = (frameH - playerSize) / 2;

        bX = (frameW - ballSize) / 2;
        bY = (frameH - ballSize) / 2;

        p1X = offsetXP1;
        p1Y = offsetY;

        p2X = offsetXP2;
        p2Y = offsetY;

        sb = new ServerBall(bX, bY, Color.BLACK);

        // gc = new GameCanvas(frameW, frameH);
        // gc.setUpGameEntities();

        try {
            System.out.println("Starting GameServer...");
            ss = new ServerSocket(this.port);
        } catch (IOException e) {
            // ...
        }

    }

    private class ServerBall extends Ball implements Runnable {

        // private Ball ball;
        private boolean running = true;
        private Color color;

        public ServerBall(double x, double y, Color color) {
            super(x, y, ballSize, color);

            this.color = color;
            this.setSpeed(ballInitSPeed);
            this.setDirection(new Random().nextInt(360));

        }

        public void setRunning(boolean run) {
            this.running = run;
        }

        @Override
        public void run() {
            canMove(true);
            while (running) {
                try {
                    double bX, bY, bW, bH;

                    bX = getX();
                    bY = getY();
                    bW = getW();
                    bH = getH();

                    if (bX < 0 || bX + bW > frameW) {
                        bounce(true);
                        setX((bX < 0) ? 0 : frameW - bW);
                    }

                    if (bY < 0 || bY + bH > frameH) {
                        bounce(false);
                        setY((bY < 0) ? 0 : frameH - bH);
                    }

                    move();

                    // System.out.printf("(%f, %f)\n", bX, bY);

                    Thread.sleep(delay);
                } catch (Exception e) {
                // ...
                }
            }
            
            
        }
    }

    private void setUpConnection(int clientID, DataOutputStream out) {
        try {
            // Delay (Sleep time in miliseconds)
            out.writeInt(delay);

            // Frame Attributes
            out.writeInt(frameW);
            out.writeInt(frameH);

            // Ball Attributes
            out.writeDouble(ballSize);
            out.writeDouble(ballInitSPeed);

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

            // Initial Look Direction
            // out.writeDouble(clientID == 1 ? offsetXP2 + playerSize/2 : offsetXP1 + playerSize/2);
            // out.writeDouble(offsetY + playerSize/2);
            // out.writeDouble(clientID == 1 ? offsetXP1 + playerSize/2 : offsetXP2 + playerSize/2);
            // out.writeDouble(offsetY + playerSize/2);
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

        for (Thread t : threads) {
            t.start();
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

            Thread t = new Thread(sb);
            t.start();

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
                    if (clientID == 1) {
                        p1X = in.readDouble();
                        p1Y = in.readDouble();
                        p1R = in.readDouble();
                    } else {
                        p2X = in.readDouble();
                        p2Y = in.readDouble();
                        p2R = in.readDouble();
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
                    out.writeDouble(sb.getX());
                    out.writeDouble(sb.getY());

                    if (clientID == 1) {
                        out.writeDouble(p2X);
                        out.writeDouble(p2Y);
                        out.writeDouble(p2R);
                    } else {
                        out.writeDouble(p1X);
                        out.writeDouble(p1Y);
                        out.writeDouble(p1R);
                    }
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
