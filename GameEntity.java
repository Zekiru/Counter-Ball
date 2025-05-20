/**
    The Game Entity abstract class, serves as the blueprint for all entities
    that run in the Game. Includes special features exclusive to objects that
    interact with other Entities, such as Collision.
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

public abstract class GameEntity {

    protected double x, y, w, h;
    protected Color color;
    protected boolean active = true;

    // Constructor for setting the attributes of the Game Entity
    public GameEntity(double x, double y, double w, double h, Color color) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.color = color;
    }

    // Getters
    public double getX() { return x; }
    public double getY() { return y; }
    public double getW() { return w; }
    public double getH() { return h; }
    public boolean isActive() { return active; }
    public Color getColor() { return color; }

    // Setters
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setActive(boolean active) { this.active = active; }

    // Abstract methods to be implemented by subclasses
    public abstract void update(); // Update Movement
    public abstract void draw(Graphics2D g2d); // Draw Entity
    public abstract boolean isColliding(GameEntity other); // Collision Detection
    public abstract EntityType getType(); // Returns the Type of the Entity

    // Enum for entity types
    public enum EntityType {
        PLAYER, BALL
    }

}
