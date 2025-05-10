import java.awt.*;

public abstract class GameEntity {

    protected double x, y, w, h;
    protected Color color;
    protected boolean active = true;

    public GameEntity(double x, double y, double w, double h, Color color) {
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.color = color;
    }

    // --- Getters ---
    public double getX() { return x; }
    public double getY() { return y; }
    public double getW() { return w; }
    public double getH() { return h; }
    public boolean isActive() { return active; }
    public Color getColor() { return color; }

    // --- Setters ---
    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setActive(boolean active) { this.active = active; }

    // --- Abstract methods to be implemented by subclasses ---
    public abstract void update();
    public abstract void draw(Graphics2D g2d);
    public abstract boolean isColliding(GameEntity other);
    public abstract EntityType getType();

    // --- Enum for entity types ---
    public enum EntityType {
        PLAYER, BALL, WALL, OTHER
    }

}
