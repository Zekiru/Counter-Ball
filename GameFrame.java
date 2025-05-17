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

import javax.swing.*;
import java.awt.*;

public class GameFrame {

    private int clientID;
    private JFrame frame;
    private GameCanvas gc;

    public GameFrame(int clientID, GameCanvas gc) {
        this.clientID = clientID;
        this.gc = gc;

        // this.gc = new GameCanvas(w, h, clientID, ball, player, opp);
        
    }

    public void newGC(GameCanvas gc) {
        this.gc = gc;
    }

    public void setUpGUI() {
        frame = new JFrame();
        frame.add(gc);
        frame.setTitle("Counter Ball - Player " + clientID);

        // frame.setSize(new Dimension(w, h));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);

        gc.setUpGameEntities();
        gc.setUpListeners();
    }

    /*
    public static void main(String[] args) {
        int client = 1, w = 1024, h = 768;
        Ball ball = new Ball(
            (w-120)/2,
            (h-120)/2,
            120,
            1,
            Color.BLACK
        );

        GameCanvas gc = new GameCanvas(
            w,
            h,
            client,
            ball,
            new Player(1, w*0.25 - 70/2, (h-70)/2, 70, 5, 100, Color.BLUE, 3),
            new Player(2, w*0.75 - 70/2, (h-70)/2, 70, 5, 100, Color.RED, 3)
        );

        GameFrame gf = new GameFrame(w, h, client, gc);
        // Thread t = new Thread(gc);
        // t.start();
        // ball.startRunnable();

        gf.setUpGUI();

        ball.startProcess(w, h);


    }
    */
}
