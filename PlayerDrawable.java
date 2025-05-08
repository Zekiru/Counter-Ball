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
// import java.awt.event.*;
// import java.awt.geom.*;

public class PlayerDrawable implements Drawable {
    
    protected double x, y, r, r2, rX, rY, size;
    protected Color color;
    protected Thread thread;
    protected boolean canLook;

    public PlayerDrawable(double x, double y, double size, Color color) {
        this.x = x;
        this.y = y;
        this.size = size;
        this.color = color;

        this.r = 0;
        this.r2 = 0;
        this.rX = x + (size/2);
        this.rY = y + (size/2);

        this.canLook = true;

        this.thread = new Thread();

    }

    public void draw(Graphics2D g2d) {
        double angleBat, angleHandL, angleHandR, batW, batH, handSize, batOffsetX, batOffsetY, handOffsetX, handOffsetY, rX, rY;

        angleBat = 90;
        angleHandL = 85;
        angleHandR = 95;

        batW = size*0.3;
        batH = size*1.5;
        handSize = size*0.5;

        handOffsetX = x + size*1.04;
        handOffsetY = y + (size - handSize) / 2;
        batOffsetX = handOffsetX + (handSize - batW) / 2;
        batOffsetY = handOffsetY + size/20;

        rX = x + size/2;
        rY = y + size/2;

        Rectangle bat = new Rectangle(batOffsetX, batOffsetY, batW, batH, color);
        Circle handL = new Circle(handOffsetX, handOffsetY, handSize, color);
        Circle handR = new Circle(handOffsetX, handOffsetY, handSize, color);
        Circle body = new Circle(x, y, size, color);

        bat.setRotation(r + angleBat, rX, rY);
        handL.setRotation(r + angleHandL, rX, rY);   
        handR.setRotation(r + angleHandR, rX, rY);

        bat.draw(g2d);
        handL.draw(g2d);
        handR.draw(g2d);
        body.draw(g2d);

    }

    protected void playSwingAnim() {
        // System.out.println("SWING");

        Thread swingAnim = new Thread(() -> {
            int delay, iterations;

            delay = 1;
            iterations = 90;

            canLook = false;

            try {
                for (int i = 0; i < iterations; i++) {
                    setRotation(getRotation() - 360/iterations);
                    if (i == iterations - 1) canLook = true;
                    Thread.sleep(delay);
                }
            } catch(InterruptedException ex) {
                // ...
                canLook = true;
            }
        });

        r2 = 0;
        thread.interrupt();
        thread = new Thread(swingAnim);
        thread.start();
    }

    public void playChargeAnim() {
        // System.out.println("CHARGE");

        Thread chargeAnim = new Thread(() -> {
            int delay, iterations;
            double current, target;

            delay = 1;
            iterations = 180;

            current = 0;
            target = 70;

            // canLook = false;

            try {
                for (int i = 0; i < iterations; i++) {
                    if (current < target) current += (double) 60/iterations;
                    r2 = current;
                    // System.out.println(r2);
                    // if (i == iterations - 1) canLook = true;
                    Thread.sleep(delay);
                    // if (i == iterations - 1) r2 = 0;
                }
            } catch(InterruptedException ex) {
                // ...
                r2 = 0;
            }
        });

        thread.interrupt();
        thread = new Thread(chargeAnim);
        thread.start();
    }

    protected void rotateTo(double x, double y) {
        if (!canLook) return;

        double centerX = this.x + (this.size / 2);
        double centerY = this.y + (this.size / 2);
    
        double dx = x - centerX;
        double dy = y - centerY;
    
        double angle = Math.toDegrees(Math.atan2(dy, dx)) + r2;
        if (angle < 0) angle += 360;
    
        this.setRotation(angle);
        // this.setDirection(angle);    // for movement direction, optional
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
