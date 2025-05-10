import java.awt.*;
// import java.awt.geom.*;

public abstract class Drawable {

    protected double x, y, w, h, r, rX, rY;
    protected Color color;

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

    public abstract void draw(Graphics2D g2d);

    public double getX() { return this.x; }
    public double getY() { return this.y; }
    public double getW() { return this.w; }
    public double getH() { return this.h; }
    public double getR() { return this.r; }

    public void setX(double x) { this.x = x; }
    public void setY(double y) { this.y = y; }
    public void setW(double w) { this.w = w; }
    public void setH(double h) { this.h = h; }

    public void setR(double r) {
        this.r = r;
        this.rX = x + (w/2);
        this.rY = y + (h/2);
    }

    public void setR(double r, double x, double y) {
        this.r = r;
        this.rX = x;
        this.rY = y;
    }

    public void setColor(Color c) { this.color = c; }
    
}
