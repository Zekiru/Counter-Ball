import java.awt.*;
// import java.awt.geom.*;

public interface Drawable {

    double getX();
    double getY();
    double getW();
    double getH();
    double getRotation();

    void draw(Graphics2D g2d);

    void setX(double x);
    void setY(double y);
    void setW(double w);
    void setH(double h);
    void setRotation(double r);
    void setRotation(double r, double x, double y);
    void setColor(Color c);
    
}
