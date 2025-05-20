/**
    The Overlay Text Render, draws text over the Canvas.
    Only draws the text whenever the text value that is currently set for
    it is not null.
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

public class OverlayTextRender {

    private String text = null;
    private int w, h;

    // COnstructor that takes the width and height of the component drawn on
    public OverlayTextRender(int w, int h) {
        this.w = w;
        this.h = h;
    }
    
    // Set the Overlay Text. Set to null if you want to hide the Overlay.
    public void setText(String text) { this.text = text; }

    // Draws the Overlay Text
    public void draw(Graphics2D g2d) {
        if (text == null) return;

        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 52));

        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int x = (w - textWidth) / 2;
        int y = h / 2;

        g2d.setColor(new Color(80, 80, 80));
        g2d.drawString(text, x, y);
    }
}
