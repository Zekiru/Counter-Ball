import java.awt.*;
import java.awt.event.*;

public class Player extends PlayerDrawable implements GameEntity, MouseListener, KeyListener {
    
    private int lives;
    private double speed, direction, range, power;
    private boolean canMove, canDeflect, canDash, vulnerable;
    private boolean isMoving, upPressed, downPressed, leftPressed, rightPressed;
    private boolean mousePressed, isCharging, ballDeflected, inPostDeflect, inDeflectRange;
    private Thread chargeAction, deflectAction, postDeflectAction;

    public Player(double x, double y, double size, double range, Color color) {
        super(x, y, size, color);

        this.lives = 3;
        this.speed = 5;
        this.direction = 0;
        this.range = range;
        this.power = 0;

        this.canMove = true;
        this.canDeflect = true;
        this.canDash = true;
        this.vulnerable = true;

        this.isMoving = false;
        this.upPressed = false;
        this.downPressed = false;
        this.leftPressed = false;
        this.rightPressed = false;

        this.mousePressed = false;
        this.isCharging = false;
        this.ballDeflected = false;
        this.inPostDeflect = false;
        this.inDeflectRange = false;

        this.chargeAction = new Thread();
        this.deflectAction = new Thread();
        this.postDeflectAction = new Thread();
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

    public double getCenterX() {
        return this.x + (this.size / 2);
    }

    public double getCenterY() {
        return this.y + (this.size / 2);
    }

    public double getSize() {
        return this.size;
    }

    public boolean getVulnerable() {
        return vulnerable;
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

    public void setVulnerable(boolean vulnerable) {
        this.vulnerable = vulnerable;
    }

    public void inRange(boolean inRange) {
        inDeflectRange = inRange;
    }

    // Mouse Listener

    @Override
    public void mouseClicked(MouseEvent e) {
        // Not used
    }

    @Override
    public void mousePressed(MouseEvent e) {
        mousePressed = true;
        charge();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        mousePressed = false;
        // deflect();
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

    public void move() {
        if (!isMoving) return;

        double radians = Math.toRadians(direction);

        this.x += Math.cos(radians) * this.speed;
        this.y += Math.sin(radians) * this.speed;
    }

    public boolean isColliding(GameEntity e) {
        if (e.getType() == Type.BALL || e.getType() == Type.PLAYER) {
            double r1 = this.size / 2;
            double r2 = e.getW() / 2;

            double centerX1 = this.x + r1;
            double centerY1 = this.y + r1;
            double centerX2 = e.getX() + r2;
            double centerY2 = e.getY() + r2;

            double dx = centerX2 - centerX1;
            double dy = centerY2 - centerY1;
            double distance = Math.sqrt(dx * dx + dy * dy);
            double minDist = r1 + r2;

            if (e.getType() == Type.BALL && vulnerable) return distance < minDist;

            if (e.getType() == Type.PLAYER) return distance < minDist;
        }

        return false;
    }

    public boolean inDeflectRange(Ball ball) {
        if (!canDeflect && !inPostDeflect) return false;

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

    public void charge() {
        Thread charge = new Thread(() -> {
            int delay = 10;
            power = 0;
            canLook = true;
            isCharging = true;
            playChargeAnim();
            try {
                while (mousePressed && power < 100) {
                    power += 1;
                    Thread.sleep(delay);
                }
            } catch(InterruptedException e) {
                // System.out.println(e);
            }
            // canDeflect = false;
            deflect();
        });

        if (!canDeflect) return;
        chargeAction = new Thread(charge);
        chargeAction.start();
    }

    public void deflect() {
        Thread deflect = new Thread(() -> {
            int delay = 10, iterations = 100;
            canDeflect = false;
            playDeflectAnim();
            try {
                for (int i = 0; i < iterations; i++) {
                    if (ballDeflected) {
                        ballDeflected = false;
                        return;
                    }
                    Thread.sleep(delay);
                }
            } catch(InterruptedException e) {
                // System.out.println(e);
            }

            vulnerable = true;
            isCharging = false;
            canDeflect = true;
            inPostDeflect = false;
        });

        if (!isCharging) return;
        deflectAction = new Thread(deflect);
        deflectAction.start();
    }

    public void ballDeflected() {
        ballDeflected = true;
        postDeflect();
    }

    public void postDeflect() {
        Thread postDeflect = new Thread(() -> {
            int delay = 10, iterations = 200;
            canDeflect = false;
            vulnerable = false;
            inPostDeflect = true;
            changeColor(new Color(200, 200, 200));
            try {
                for (int i = 0; i < iterations; i++) {
                    if (!inDeflectRange || vulnerable) break;
                    Thread.sleep(delay);
                }
            } catch(InterruptedException e) {
                // System.out.println(e);
            }
            revertColor();
            vulnerable = true;
            isCharging = false;
            canDeflect = true;
            inPostDeflect = false;
        });

        postDeflectAction.interrupt();
        postDeflectAction = new Thread(postDeflect);
        postDeflectAction.start();
    }

    

}