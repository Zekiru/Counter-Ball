/**
    The Hearts Render, allows the display the lives of both players
    on the GUI. Draws the amount of lives for each player with hearts.
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

import java.awt.*;

public class HeartsRender {
    private int pLives, oLives;
    private double w, h, gap, size;

    // Contructor for setting up the attributes of the Render
    public HeartsRender(int pLives, int oLives, double gap, double size, double w, double h) {
        this.pLives = pLives;
        this.oLives = oLives;
        this.gap = gap;
        this.size = size;
        this.w = w;
        this.h = h;

    }

    // Updates the lives count within this class
    public void setLives(int pLives, int oLives) {
        this.pLives = pLives;
        this.oLives = oLives;
    }
    
    // Draws the Hearts Render
    public void draw(Graphics2D g2d) {
        drawHearts(g2d, 20, 20, pLives);
        drawHearts(g2d, w - ( 20 + (size + gap) * oLives), 20, oLives);

    }

    // Draws the set of Hearts for the players
    private void drawHearts(Graphics2D g2d,  double x, double y, int lives) {
        g2d.setColor(Color.RED);
        double heartSize = 20;

        for (int i = 0; i < lives; i++) {
            double hx = x + i * (heartSize + gap);
            drawHeartShape(g2d, hx, y, heartSize);
        }
    }

    // Draws the individual Heart Shapes
    private void drawHeartShape(Graphics2D g2d, double x, double y, double size) {
        double half = size / 2.0;
        double quarter = size / 4.0;

        // Top-left "lobe" of the heart
        Rectangle left = new Rectangle(x, y, half, half, Color.RED);
        left.setR(45);
        
        // Top-right "lobe"
        Rectangle right = new Rectangle(x + half, y, half, half, Color.RED);
        right.setR(45);

        // Bottom triangle (rotated rectangle to mimic point)
        Rectangle bottom = new Rectangle(x + quarter, y + quarter, half, half, Color.RED);
        bottom.setR(45);

        left.draw(g2d);
        right.draw(g2d);
        bottom.draw(g2d);
    }
}
