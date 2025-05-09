/**
    This class houses the main method which will create a JFrame for 
    the canvas to be displayed in. It instantiates a new RectangleCanvas 
    with a preferred size of the standard canvas size dimension (800, 600).
    @author Ezekiel Villasurda (236689)
    @version 17 March 2025
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

import java.util.*;
import javax.swing.*;
import java.awt.*;

public class GameFrame {

    private int clientID, w, h;
    private JFrame frame;
    private GameCanvas gc;

    public GameFrame(int clientID, Ball ball, Player player, Player opp, int w, int h) {
        this.clientID = clientID;
        this.w = w;
        this.h = h;

        this.gc = new GameCanvas(clientID, ball, player, opp, w, h);
        
    }

    public void setUpGUI() {
        frame = new JFrame();
        frame.add(gc);
        frame.setTitle("Player " + clientID);
        // frame.setSize(w, h);
        frame.pack();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        gc.setUpGameEntities();
        gc.setUpListeners();

        Thread t = new Thread(gc);
        t.start();
        // gc.play(true);
    } 
    
    public static void main(String[] args) {
        int client = 1, w = 1024, h = 768;
        Ball ball = new Ball((w-120)/2, (h-120)/2, 120, Color.BLACK);
        ball.setSpeed(10);
        ball.canMove(true);

        GameFrame gf = new GameFrame(
            client,
            ball,
            new Player(w*0.25 - 70/2, (h-70)/2, 70, 100, Color.BLUE),
            new Player(w*0.75 - 70/2, (h-70)/2, 70, 100, Color.RED),
            w,
            h
        );
        gf.setUpGUI();
    }
}
