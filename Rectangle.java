/**
    This class is a shape that draws a rectangle in the canvas. An instance of 
    this object takes parameters that describe the rectangle and can be modifed 
    using methods, especially for translating the position the shape.
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

import java.awt.*;
import java.awt.geom.*;

public class Rectangle extends Drawable {

    public Rectangle(double x, double y, double w, double h, Color color) {
        super(x, y, w, h, color);
    }

    public void draw(Graphics2D g2d) {
        AffineTransform reset = g2d.getTransform();
        Rectangle2D.Double shape = new Rectangle2D.Double(x, y, w, h);
        
        g2d.setColor(color);
        g2d.rotate(Math.toRadians(r), rX,  rY);

        g2d.fill(shape);
        g2d.setTransform(reset);
    }

}
