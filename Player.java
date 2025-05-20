/**
    The Player, a Controllable GameEntity. Has multiple helper classes
    that conducts the player's actions.
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
import java.awt.event.*;

public class Player extends GameEntity implements MouseListener, KeyListener {
    
    private int lives, clientID;
    private double size, velocity, direction, range, power;
    private double mX = 0, mY = 0;
    
    private boolean canMove = true, canDeflect = true, vulnerable = true;
    private boolean isMoving, isCharging, isDeflecting, isDeflected, isInCooldown, isGraced, isHit;

    private boolean mousePressed, upPressed, downPressed, leftPressed, rightPressed;

    protected PlayerRender render;

    // Constructor that sets all the needed attributes for the Player
    public Player(int clientID, double x, double y, double size, double velocity, double range, Color color, int lives) {
        super(x, y, size, size, color);

        this.clientID = clientID;
        this.size = size;
        this.velocity = velocity;
        this.direction = 0;
        this.range = range;
        this.power = 0;
        this.lives = lives;

        this.render = new PlayerRender(x, y, size, color);
    }

    // Getter Methods
    public int getClientID() { return this.clientID; }
    public int getLives() { return this.lives; }

    public double getSize() { return this.size; }
    public double getVelocity() { return this.velocity; }
    public double getDirection() { return this.direction; }
    public double getRange() { return this.range; }
    public double getPower() { return this.power; }

    public double getCenterX() { return x + (size / 2); }
    public double getCenterY() { return y + (size / 2); }
    public double getR() { return render.getR(); }

    public boolean canDeflect() { return canDeflect; }
    public boolean isVulnerable() { return vulnerable; }
    public boolean isMoving() { return isMoving; }
    public boolean isCharging() { return isCharging; }
    public boolean isInCooldown() { return isInCooldown; }
    
    // Switch-off Getter Method for Deflection
    public boolean isDeflected() {
        if (isDeflected) {
            isDeflected = false;
            return true;
        }
        return false; 
    }

    // More Getter Methods
    public boolean isGraced() { return isGraced; }
    public boolean isHit() { return isHit; }

    // Setter Methods
    public void setVelocity(double velocity) { this.velocity = velocity; }
    public void setDirection(double direction) { this.direction = direction; }
    public void setRange(double range) { this.range = range; }

    public void setMX(double mX) { this.mX = mX; }
    public void setMY(double mY) { this.mY = mY; }

    public void setR(double r) { render.setR(r); }
    public void rotateTo(double x, double y) { render.rotateTo(x, y); }

    public void setLives(int lives) { this.lives = Math.max(0, lives); }

    // Updates the player's movement if the Player is active
    @Override
	public void update() {
        if (!this.active || !isMoving) return;

		double radians = Math.toRadians(direction);

        this.x += Math.cos(radians) * this.velocity;
        this.y += Math.sin(radians) * this.velocity;
	}

    // Draws the Player
	@Override
	public void draw(Graphics2D g2d) { 
        render.setX(this.x);
        render.setY(this.y);
        render.setW(this.size);
        render.setH(this.size);
        render.setColor(color);

        render.draw(g2d);
    }

    // Returns the Entity Type of the Player
	@Override
	public GameEntity.EntityType getType() { return EntityType.PLAYER; }

    // Mouse Events
    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override // Detects if Mouse Pressed
    public void mousePressed(MouseEvent e) { mousePressed = true; }

    @Override // Detects if Mouse Released
    public void mouseReleased(MouseEvent e) { mousePressed = false; }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}


    // Key Pressed Events
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_W) upPressed = true;
        if (key == KeyEvent.VK_S) downPressed = true;
        if (key == KeyEvent.VK_A) leftPressed = true;
        if (key == KeyEvent.VK_D) rightPressed = true;

        updateMovement();
    }

    // Key Released Events
    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_W) upPressed = false;
        if (key == KeyEvent.VK_S) downPressed = false;
        if (key == KeyEvent.VK_A) leftPressed = false;
        if (key == KeyEvent.VK_D) rightPressed = false;

        updateMovement();
    }

    // Key Typed Events
    @Override
    public void keyTyped(KeyEvent e) {}

    // Updates the Player's movements based on the Key Events above
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
        } else {
            double length = Math.sqrt(dx * dx + dy * dy);
            dx /= length;
            dy /= length;
    
            double angle = Math.toDegrees(Math.atan2(dy, dx));
            if (angle < 0) angle += 360;
    
            setDirection(angle);
            isMoving = true;
        }
    }

    // Collision Detection Method
    @Override
    public boolean isColliding(GameEntity e) {
        EntityType type = e.getType();
        
        // Circle on Circle Collisions
        if (type == EntityType.BALL || type == EntityType.PLAYER) {
            if (type == EntityType.BALL && !vulnerable) return false;

            double r1 = size / 2;
            double r2 = e.getW() / 2;

            double centerX1 = this.x + r1;
            double centerY1 = this.y + r1;
            double centerX2 = e.getX() + r2;
            double centerY2 = e.getY() + r2;

            double dx = centerX2 - centerX1;
            double dy = centerY2 - centerY1;
            double distance = Math.sqrt(dx * dx + dy * dy);
            double minDist = r1 + r2;

            return distance < minDist;
        }

        return false;
    }

    // In Range for Ball Deflect Method
    public boolean isInRange(GameEntity e) {
        // if (!canDeflect || !vulnerable) return false;

        double x1, x2, y1, y2, dist, r1, r2;

        r1 = this.size / 2;
        r2 = e.getW() / 2;

        x1 = this.x + r1;
        y1 = this.y + r1;
        x2 = e.getX() + r2;
        y2 = e.getY() + r2;

        dist = Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        return dist < r1 + r2 + this.range;
    }

    // Player Render Color Setters
    public void defaultColor() { render.defaultColor(); }
    public void warningColor() { render.warningColor(); }
    public void inRangeColor() { render.inRangeColor(); }
    public void gracedColor() { render.gracedColor(); }
    public void hitColor() { render.hitColor(); }

    public void setGracedColor(boolean graced) {
        if (graced) { isGraced = true; gracedColor(); }
        else { isGraced = false; defaultColor(); }
    }

    public void setHitColor(boolean hit) {
        if (isGraced) return;
        if (hit) { isHit = true; hitColor(); }
        else { isHit = false; defaultColor(); }
    }

    // Starts the Deflection Process using the helper classes
    public void deflectProcess(int interval, Ball ball) { if (canDeflect) new Charge(interval, ball); }

    // Disable triggering the Deflection Process
    public void disableDeflect() { canDeflect = false; }

    // Reverts the states required for Deflection
    public void revertStates() {
        canDeflect = true;
        vulnerable = true;

        isCharging = false;
        isInCooldown = false;
        isGraced = false;

        isHit = false;

        defaultColor();

    }

    // The Start of the Deflection Process.
    // Player can keep pressing their mouse to increase the deflect power
    private class Charge extends AsyncTask {

        private Ball ball;

        public Charge(int interval, Ball ball) {
            super(interval);

            this.ball = ball;

            render.canLook = true;
            render.playChargeAnim(0.4);
            power = 0;
            isCharging = true;
            disableDeflect();
            
            startTask();
        }

        @Override
        protected void runnable() {
            if (isHit) { endTask(); }

            power++;
            if ((!mousePressed || power >= 100) && !isHit) {
                render.animation.endTask();
                new Deflect(interval, 0.18, ball);
                endTask();
            }
        }
    }

    // The actual deflect action, redirects the ball when in range
    // Proceeds to the cooldown when the ball is not in range
    private class Deflect extends AsyncTask {

        private Ball ball;
        private boolean safe = false;

        public Deflect(int interval, double duration, Ball ball) {
            super(interval, duration);

            this.ball = ball;

            render.canLook = false;
            render.playDeflectAnim(duration);
            gracedColor();
            isDeflecting = true;
            isCharging = false;
            disableDeflect();

            startTask();
        }


        @Override
        protected void runnable() {
            if (isHit) { safe = false; endTask(); }

            if (isInRange(ball) && !isHit) {
                isDeflected = true;
                safe = true;
                ball.redirectTowards(mX, mY, power);
                new Grace(interval, 1, ball, false);
                endTask();
            }
        }

        @Override
        protected void finish() {
            isDeflecting = false;
            if (isHit) render.animation.endTask();
            if (!safe && !isHit) new DeflectCooldown(interval, 1);
        }
    }

    // The cooldown for when the player misses/deflects out of range
    // Resets the states for deflection upon ending
    private class DeflectCooldown extends AsyncTask {

        public DeflectCooldown(int interval, double duration) {
            super(interval, duration);

            render.defaultColor();
            render.canLook = true;
            isInCooldown = true;
            disableDeflect();

            startTask();
        }

        @Override
        protected void runnable() {}

        @Override
        protected void finish() { revertStates(); }
    }

    // Also know as Invulnerability, but a "Grace" period seemed like a shorter name
    // Grants the player Invulnerability whenever the Ball is deflected or the Player
    // is hit
    private class Grace extends AsyncTask {

        private Ball ball;
        private boolean hit = false;

        public Grace(int interval, double duration, Ball ball, boolean hit) {
            super(interval, duration);

            this.ball = ball;
            this.hit = hit;

            inState();

            startTask();
        }

        private void inState() {
            gracedColor();
            isGraced = true;
            isHit = false;
            vulnerable = false;
            disableDeflect();
        }

        @Override
        protected void runnable() {
            if ((!isInRange(ball) || vulnerable || isHit) && !hit) endTask();
        }

        @Override
        protected void finish() { revertStates(); }
    }

    // Handles the event when the Player is Hit with the Ball
    private class Hit extends AsyncTask {

        private Ball ball;

        public Hit(int interval, double duration, Ball ball) {
            super(interval, duration);

            this.ball = ball;
            render.animation.endTask();
            lives--;

            inState();

            startTask();
        }

        private void inState() {
            hitColor();
            isHit = true;
            isGraced = false;
            vulnerable = false;
            disableDeflect();
            ball.setActive(false);
        }

        @Override
        protected void runnable() { inState(); }

        @Override
        protected void finish() {
            new Grace(interval, 3, ball, true);

            render.canLook = true;
            render.r2 = 0;

            setActive(true);

            ball.setActive(true);
            ball.defaultColor();
            ball.resetVelocity();
        }
    }

    // A method for triggering the Player being hit
    public void hurt(Ball ball) { 
        if (isHit || isGraced || !vulnerable || isDeflecting) return;
        
        disableDeflect();
        new Hit(10, 1, ball);
    }

}