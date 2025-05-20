/**
    The Game Canvas, responsible for drawing the game on the JFrame.
    Handles some local game logic, especially the local hit detection
    for the current player.
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
import java.util.ArrayList;
import javax.swing.*;

public class GameCanvas extends JComponent implements Runnable, MouseListener, MouseMotionListener, KeyListener {
    
    private int w, h;
    private Ball ball;
    private Player player, opponent;
    private double mX = w/2, mY = h/2;

    private ArrayList<GameEntity> ge = new ArrayList<GameEntity>();

    private volatile boolean running = true;
    private boolean isDeflected = false;
    private boolean gameOver = false, reset = false;

    private Thread gcThread;

    private HeartsRender hr;
    private OverlayTextRender otr;

    // Constructor that instantiates the Canvas with the needed objects and variables:
    public GameCanvas (int w, int h, Ball ball, Player player, Player opponent) {

        this.w = w;
        this.h = h;

        this.ball = ball;
        this.player = player;
        this.opponent = opponent;

        this.setPreferredSize(new Dimension(w, h));

        this.gcThread = new Thread(this);

        this.hr = new HeartsRender(player.getLives(), opponent.getLives(), 5, 30, w, h);
        this.otr = new OverlayTextRender(w, h);
        this.otr.setText("Ready?");
        
    }

    // Starts and stops the Canvas for special events such as a game over.
    public void setActive(boolean active) {
        if (active) {
            this.running = true;
            this.gameOver = false;
            this.reset = false;
            otr.setText(null);
            gcThread = new Thread(this);
            gcThread.start();
        } else {
            this.running = false;
        }
    }

    // Method for resetting the game locally
    public void resetGame(Ball ball, Player player, Player opponent) {
        this.running = false;
        this.gameOver = false;

        this.reset = false;
        otr.setText("Ready?");

        this.ball = ball;
        this.player = player;
        this.opponent = opponent;

        this.setUpGameEntities();
        this.setUpListeners();
        this.repaint();
    }

    // Getter Methods
    public int getW() { return this.w; }
    public int getH() { return this.h; }
    public Player getPlayer() { return this.player; }
    public Player getOpponent() { return this.opponent; }
    public Ball getBall() { return this.ball; }
    public boolean isGameOver() { return this.gameOver; }
    public double getMX() { return this.mX; }
    public double getMY() { return this.mY; }

    // Switch-off Getter Method for Resets
    public boolean wantsReset() {
        if (reset) { reset = false; return true; }
        return false;
    }

    // Switch-off Getter Method for Deflection
    public boolean isDeflected() {
        if (isDeflected) { isDeflected = false; return true; }
        return false;
    }

    // Method that paints the objects/entities
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        RenderingHints rh = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2d.setRenderingHints(rh);

        for (GameEntity e : ge) e.draw(g2d);

        hr.draw(g2d);
        otr.draw(g2d);

    }

    // Method for setting up instructions before drawing the entitites
    public void setUpGameEntities() {        
        player.rotateTo(w/2, h/2);
        opponent.rotateTo(w/2, h/2);

        ge.clear();
        ge.add(opponent);
        ge.add(player);
        ge.add(ball);
    }

    // Method for setting up Listeners
    public void setUpListeners() {
        setFocusable(true);
        requestFocusInWindow();

        addMouseListener(this);
        addMouseMotionListener(this);

        addKeyListener(this);
        addKeyListener(player);
        addMouseListener(player);
    }

    // Tracks Current Mouse Position relative to this Component
    public void updateMousePos(MouseEvent e) {
        if (Math.abs(e.getX() - mX) <= 1 && Math.abs(e.getY() - mY) <= 1) return;
        this.mX = e.getX();
        this.mY = e.getY();
    }

    // Instructions for when the game is finished by the players
    public void gameOver() {
        gameOver = true;
        this.setActive(false);

        String gameOverMessage = (player.getLives() > opponent.getLives()) ? "You Win!" : "You Lose.";
        // setOverlayText((player.getLives() > opponent.getLives()) ? "You Win!" : "You Lose.");
        otr.setText(gameOverMessage);
    }

    // The game loop, implementing runnable
    @Override
    public void run() {
        while (running) {
            player.setMX(mX);
            player.setMY(mY);
            player.rotateTo(mX, mY);

            for (GameEntity e : ge) {
                double eX, eY, eW, eH;

                eX = e.getX();
                eY = e.getY();
                eW = e.getW();
                eH = e.getH();

                if (e == ball) continue;

                if (eX < 0 || eX + eW > this.w) {
                    // if (e == ball) ball.bounce(true);
                    e.setX((eX < 0) ? 0 : w - eW);
                }
        
                if (eY < 0 || eY + eH > this.h) {
                    // if (e == ball) ball.bounce(false);
                    e.setY((eY < 0) ? 0 : h - eH);
                }
                
            }

            if (player.isColliding(opponent)) handleCircleRigidBodyCollision(player, opponent);

            handleEntityInteraction();

            if (player.isDeflected()) this.isDeflected = true;

            for (GameEntity e : ge) if (e != ball) e.update();

            if (player.getLives() <= 0 || opponent.getLives() <= 0) { gameOver(); }

            hr.setLives(player.getLives(), opponent.getLives());

            this.repaint();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }

    // Method for Handling Rigid Body Collisions between Entities
    private void handleCircleRigidBodyCollision(GameEntity e1, GameEntity e2) {
        double dx = (e1.getX() + e1.getW()) - (e2.getX() + e2.getW());
        double dy = (e1.getY() + e1.getH()) - (e2.getY() + e2.getH());
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance == 0) {
            // Prevent divide-by-zero (players perfectly overlapping)
            dx = 1;
            dy = 0;
            distance = 1;
        }

        double overlap = (e1.getW() / 2 + e2.getW() / 2) - distance;

        // Normalize
        dx /= distance;
        dy /= distance;

        // Push each player away from each other by half the overlap
        double pushX = dx * (overlap / 2);
        double pushY = dy * (overlap / 2);

        e1.setX(e1.getX() + pushX);
        e1.setY(e1.getY() + pushY);


        e2.setX(e2.getX() - pushX);
        e2.setY(e2.getY() - pushY);
    }

    // Method for handling Interactions between Entities
    // Also used for setting the appropriate Colors of the Entities
    private void handleEntityInteraction() {
        if (player.isColliding(ball) && player.isVulnerable()) {
            player.hurt(ball);
        }
        
        ball.defaultColor();
        if (player.isInRange(ball) && !player.isInCooldown()) ball.inRangeColor();
        if (player.isInCooldown()) ball.warningColor();
        if (player.isGraced()) { ball.gracedColor(); }
        if (player.isHit()) ball.hitColor();
    }
    
    // Event Methods
    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override // Initiate Deflect Process on Mouse Press
    public void mousePressed(MouseEvent e) { if (running) player.deflectProcess(10, ball); }
        
    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override // Update Mouse Position on Mouse Drag
    public void mouseDragged(MouseEvent e) { if (running) updateMousePos(e); }

    @Override // Update Mouse Position on Mouse Moved
    public void mouseMoved(MouseEvent e) { if (running) updateMousePos(e); }

    @Override // Detect when the player Presses the R Key
    public void keyPressed(KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_R && gameOver) reset = true; }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

}
