import java.awt.Color;
import java.awt.event.*;

public class Player extends PlayerDrawable implements GameEntity, MouseListener, KeyListener {
    
    private int lives;
    private double speed, direction, range;
    private boolean canMove, canSwing, canDash, vulnerable;
    private boolean isMoving, upPressed, downPressed, leftPressed, rightPressed;

    private Thread animThread;

    public Player(double x, double y, double size, double range, Color color) {
        super(x, y, size, color);

        this.lives = 3;
        this.speed = 5;
        this.direction = 0;
        this.range = range;

        this.canMove = true;
        this.canSwing = true;
        this.canDash = true;
        this.vulnerable = true;

        this.isMoving = false;
        this.upPressed = false;
        this.downPressed = false;
        this.leftPressed = false;
        this.rightPressed = false;

        this.animThread = new Thread();
    }

    @Override
    public GameEntity.Type getType() {
        return Type.PLAYER;
    }

    @Override
    public double getSpeed() {
        return this.speed;
    }

    @Override
    public double getDirection() {
        return this.direction;
    }

    public double getRange() {
        return this.range;
    }

    @Override
    public void setSpeed(double speed) {
        this.speed = speed;
    }

    @Override
    public void setDirection(double direction) {
        this.direction = direction;
    }

    public void setRange(double range) {
        this.range = range;
    }

    // Mouse Listener

    @Override
    public void mouseClicked(MouseEvent e) {
        // Not used
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Not used
        playChargeAnim();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        canLook = false;
        playSwingAnim();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // Not used
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // Not used
    }

    // Key Listener

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_W) upPressed = true;
        if (key == KeyEvent.VK_S) downPressed = true;
        if (key == KeyEvent.VK_A) leftPressed = true;
        if (key == KeyEvent.VK_D) rightPressed = true;

        updateMovement();
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_W) upPressed = false;
        if (key == KeyEvent.VK_S) downPressed = false;
        if (key == KeyEvent.VK_A) leftPressed = false;
        if (key == KeyEvent.VK_D) rightPressed = false;

        updateMovement();
    }

    @Override
    public void keyTyped(KeyEvent e) {
        // Not used
    }

    private void updateMovement() {
        if (!canMove) return;

        double dx = 0;
        double dy = 0;
    
        if (upPressed) dy -= 1;
        if (downPressed) dy += 1;
        if (leftPressed) dx -= 1;
        if (rightPressed) dx += 1;
    
        if (dx == 0 && dy == 0) {
            isMoving = false;
            // setSpeed(0);
        } else {
            double length = Math.sqrt(dx * dx + dy * dy);
            dx /= length;
            dy /= length;
    
            double angle = Math.toDegrees(Math.atan2(dy, dx));
            if (angle < 0) angle += 360;
    
            setDirection(angle);
            isMoving = true;
            // setSpeed(5);
        }
    }

    @Override
    public void move() {
        if (!isMoving) return;

        double radians = Math.toRadians(direction);

        this.x += Math.cos(radians) * this.speed;
        this.y += Math.sin(radians) * this.speed;
    }

    @Override
    public boolean isColliding(GameEntity s) {
        if (s.getType() == Type.PLAYER) {
            return false;
        }

        if (s.getType() == Type.BALL) {
            if (!vulnerable) return false;

            double x1, x2, y1, y2, dist, r1, r2;

            r1 = this.size / 2;
            r2 = s.getW() / 2;

            x1 = this.x + r1;
            y1 = this.y + r1;
            x2 = s.getX() + r2;
            y2 = s.getY() + r2;

            dist = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
            return dist < r1 + r2;
        }

        return false;
    }

    public boolean inSwingRange(Ball ball) {
        if (!canSwing) return false;

        double x1, x2, y1, y2, dist, r1, r2;

        r1 = this.size / 2;
        r2 = ball.getW() / 2;

        x1 = this.x + r1;
        y1 = this.y + r1;
        x2 = ball.getX() + r2;
        y2 = ball.getY() + r2;

        dist = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        return dist < r1 + r2 + this.range;
    }

}