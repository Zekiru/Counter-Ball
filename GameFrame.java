/**
    The GameFrame, a class houses the main method which will create a
    JFrame for the GameCanvas to be displayed in. This class would require
    you to pass the Client ID and a GameCanvas object which is needed to
    run the game.
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

import javax.swing.*;

public class GameFrame {

    private int clientID;
    private JFrame frame;
    private GameCanvas gc;

    // Constructor that accepts the Client ID and the GameCanvas
    public GameFrame(int clientID, GameCanvas gc) {
        this.clientID = clientID;
        this.gc = gc;
        
    }

    // A method to set up the GUI
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
}
