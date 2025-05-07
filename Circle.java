/**
    ...
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

public class Circle implements Drawable {
    
    protected double x, y, r, rX, rY, size;
    protected Color color;

    public Circle(double x, double y, double size, Color color) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.color = color;

        this.r = 0;
        this.rX = x + (size/2);
        this.rY = y + (size/2);

    }

    public void draw(Graphics2D g2d) {
        AffineTransform reset = g2d.getTransform();
        Ellipse2D.Double shape = new Ellipse2D.Double(x, y, size, size);
        
        g2d.setColor(color);
        g2d.rotate(Math.toRadians(r), rX,  rY);

        g2d.fill(shape);
        g2d.setTransform(reset);
    }

    // Get
    
    public double getX() {
        return this.x;
    }

    public double getY() {
        return this.y;
    }

    public double getW() {
        return this.size;
    }

    public double getH() {
        return this.size;
    }

    public double getRotation() {
        return this.r;
    }

    // Set

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setW(double w) {
        this.size = w;
    }
    
    public void setH(double h) {
        this.size = h;
    }

    public void setRotation(double r) {
        this.r = r;
        this.rX = x + (size/2);
        this.rY = y + (size/2);
    }

    public void setRotation(double r, double x, double y) {
        this.r = r;
        this.rX = x;
        this.rY = y;
    }

    public void setColor(Color c) {
        this.color = c;
    }

}
