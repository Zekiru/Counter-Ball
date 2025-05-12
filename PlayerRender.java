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

public class PlayerRender extends Drawable {
    
    protected double r2, size;
    protected Color currentColor;
    protected boolean canLook = true;
    private AsyncTask animation;

    public PlayerRender(double x, double y, double size, Color color) {
        super(x, y, size, size, color);

        this.currentColor = color;
        this.size = size;
        this.r2 = 0;

        this.animation = new AsyncTask(10) {
			@Override
			protected void runnable() { endTask(); }
        };

    }

    @Override
    public void draw(Graphics2D g2d) {
        double angleBat = 90;
        double angleHandL = 85;
        double angleHandR = 95;

        double batW = size*0.3;
        double batH = size*1.5;
        double handSize = size*0.5;

        double handOffsetX = x + size*1.04;
        double handOffsetY = y + (size - handSize) / 2;
        double batOffsetX = handOffsetX + (handSize - batW) / 2;
        double batOffsetY = handOffsetY + size/20;

        double rX = x + size/2;
        double rY = y + size/2;

        Rectangle bat = new Rectangle(batOffsetX, batOffsetY, batW, batH, currentColor);
        Circle handL = new Circle(handOffsetX, handOffsetY, handSize, currentColor);
        Circle handR = new Circle(handOffsetX, handOffsetY, handSize, currentColor);
        Circle body = new Circle(x, y, size, currentColor);

        bat.setR(r + angleBat, rX, rY);
        handL.setR(r + angleHandL, rX, rY);   
        handR.setR(r + angleHandR, rX, rY);

        bat.draw(g2d);
        handL.draw(g2d);
        handR.draw(g2d);
        body.draw(g2d);

    }

    protected void playChargeAnim(double duration) { 
        animation.endTask();
        animation = new ChargeAnimation(10, duration); 
    }

    private class ChargeAnimation extends AsyncTask {

        private double current = 0, target = 70;

        public ChargeAnimation(int interval, double duration) {
            super(interval, duration);

            startTask();
        }

		@Override
		protected void runnable() {
			if (current < target) current += 2;
            r2 = (double) current;
		}

    }

    protected void playDeflectAnim(double duration) { 
        animation.endTask();
        animation = new DeflectAnimation(10, duration); 
    }

    private class DeflectAnimation extends AsyncTask {

        public DeflectAnimation(int interval, double duration) {
            super(interval, duration);
            r2 = 0;
            canLook = false;

            startTask();
        }

        @Override
        protected void runnable() { setR(getR() - (360 / (duration * 100))); }

        @Override
        protected void finish() { canLook = true; }
    }

    protected void rotateTo(double x, double y) {
        if (!canLook) return;

        double centerX = this.x + (this.size / 2);
        double centerY = this.y + (this.size / 2);
    
        double dx = x - centerX;
        double dy = y - centerY;
    
        double angle = Math.toDegrees(Math.atan2(dy, dx)) + r2;
        if (angle < 0) angle += 360;
    
        this.setR(angle);
    }

    protected void changeColor(Color color) { currentColor = color; }

    public void defaultColor() { currentColor = this.color; }

}
