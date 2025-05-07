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

public class GameFrame {
    
    public static void main(String[] args) {
        
        JFrame frame = new JFrame();
        GameCanvas rectangleCanvas = new GameCanvas(1024, 768);

        frame.add(rectangleCanvas);

        frame.setTitle("Collision Detection");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
;    }
}
