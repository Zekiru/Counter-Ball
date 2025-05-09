import java.awt.*;

public class Ball extends Circle implements GameEntity {

    private double speed, direction;
    private boolean canMove;

    public Ball(double x, double y, double size, Color color) {
        super(x, y, size, color);
        
        this.speed = 0;
        this.direction = 0;

        this.canMove = false;
    }

    // Get

    public Type getType() {
        return Type.BALL;
    }

    public double getSpeed() {
        return speed;
    }

    public double getDirection() {
        return direction;
    }

    // Set

    public void setSpeed(double speed) {
        this.speed = speed;
    }
    
    public void setDirection(double direction) {
        this.direction = direction;
    }

    public void canMove(boolean state) {
        this.canMove = state;
    }

    // Functional
    
    public void move() {
        if (!canMove) return;

        double radians = Math.toRadians(direction);

        this.x += Math.cos(radians) * this.speed;
        this.y += Math.sin(radians) * this.speed;
    }

    public void bounce(boolean vertical) {
        if (vertical) {
            // Reflect horizontally (e.g., left or right wall)
            direction = (180 - direction) % 360;
        } else {
            // Reflect vertically (e.g., top or bottom wall)
            direction = (360 - direction) % 360;
        }
    
        if (direction < 0) direction += 360;
    }

    public void redirectTowards(double x, double y) {
        double dx = x - (this.x + (this.size/2));
        double dy = y - (this.y + (this.size/2));
        direction = Math.toDegrees(Math.atan2(dy, dx));
        if (direction < 0) direction += 360;
    }

    public boolean isColliding(GameEntity s) {

        if (s.getType() == Type.BALL) {
            // Code Here
            return false;
        }

        if (s.getType() == Type.PLAYER) {
            double x1, x2, y1, y2, dist, r1, r2;

            r1 = this.size/2;
            r2 = s.getW()/2;

            x1 = this.x + r1;
            y1 = this.y + r1;
            x2 = s.getX() + r2;
            y2 = s.getY() + r2;

            dist = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));

            // System.out.printf("%f : %f\n", dist, r1 + r2);

            return dist < r1 + r2;
        }

        return false;
    }

}
