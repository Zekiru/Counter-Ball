/**
    The Drawable abstract class, the blueprint for most drawable classes
    in this project. Covers the essential variables and methods needed to 
    create a drawable class.
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

public abstract class Drawable {

    protected double x, y, w, h, r, rX, rY;
    protected Color color;

    // Constructor to set the attributes of the Drawable
    public Drawable(double x, double y, double w, double h, Color color) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.color = color;

        this.r = 0;
        this.rX = x + (w/2);
        this.rY = y + (h/2);
    }

    // Draws the object
    public abstract void draw(Graphics2D g2d);

    // Getter Methods
    public double getX() { return this.x; }
    public double getY() { return this.y; }
    public double getW() { return this.w; }
    public double getH() { return this.h; }
    public double getR() { return this.r; }

    // Setter Methods
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setW(double w) { this.w = w; }
    public void setH(double h) { this.h = h; }
    
    // Sets the rotation with the pivot point at the center of the object
    public void setR(double r) {
        this.r = r;
        this.rX = x + (w/2);
        this.rY = y + (h/2);
    }

    // Sets the rotation with a custom pivot point
    public void setR(double r, double x, double y) {
        this.r = r;
        this.rX = x;
        this.rY = y;
    }

    // Color Setter Method
    public void setColor(Color c) { this.color = c; }
    
}
