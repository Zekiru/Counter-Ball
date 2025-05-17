/**
    ...
    @author Ezekiel Villasurda (236689)
    @version 17 May 2025
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
    private boolean canMove = false, gameOver = false, reset = false;

    private String overlayText = "Ready?";

    private Thread gcThread;

    public GameCanvas (int w, int h, Ball ball, Player player, Player opponent) {

        this.w = w;
        this.h = h;

        this.ball = ball;
        this.player = player;
        this.opponent = opponent;

        this.setPreferredSize(new Dimension(w, h));

        this.gcThread = new Thread(this);

        // setUpInitialData();
    }

    public void setActive(boolean active) {
        if (active) {
            this.running = true;
            this.gameOver = false;
            this.reset = false;
            this.overlayText = null;
            gcThread = new Thread(this);
            gcThread.start();
        } else {
            this.running = false;
        }

        this.setCanMove(false);
    }

    public void setCanMove(boolean canMove) {
        this.canMove = canMove;
        player.setActive(canMove);
        
    } 

    public void resetGame(Ball ball, Player player, Player opponent) {
        this.running = false;
        this.gameOver = false;

        this.reset = false;
        this.overlayText = "Ready?";

        ge.clear();

        this.ball = ball;
        this.player = player;
        this.opponent = opponent;

        this.setUpGameEntities();
        this.setUpListeners();
        this.repaint();
    }

    public int getW() { return this.w; }
    public int getH() { return this.h; }
    public Player getPlayer() { return this.player; }
    public Player getOpponent() { return this.opponent; }
    public Ball getBall() { return this.ball; }
    public boolean isGameOver() { return this.gameOver; }

    public boolean wantsReset() {
        if (reset) {
            reset = false;
            return true;
        } else {
            return false;
        }
    }



    public void setOverlayText(String text) {
        this.overlayText = text;
        repaint();
    }


    private void heartsOverlay(Graphics2D g2d) {
        int playerLives = player.getLives();
        int opponentLives = opponent.getLives();
        int size = 20;
        int gap = 5;

        drawHearts(g2d, playerLives, 20, 20, gap);
        drawHearts(g2d, opponentLives, w - ( 20 + (size + gap) * opponentLives), 20, gap);
    }

    private void drawHearts(Graphics2D g2d, int lives, int x, int y, int gap) {
        g2d.setColor(Color.RED);
        int heartSize = 20;

        // Assuming Max Lives is 3:
        for (int i = 0; i < lives; i++) {
            int hx = x + i * (heartSize + gap);
            drawHeartShape(g2d, hx, y, heartSize);
        }
    }

    private void drawHeartShape(Graphics2D g2d, int x, int y, int size) {
        double half = size / 2.0;
        double quarter = size / 4.0;

        // Top-left "lobe" of the heart
        Rectangle left = new Rectangle(x, y, half, half, Color.RED);
        left.setR(45);
        
        // Top-right "lobe"
        Rectangle right = new Rectangle(x + half, y, half, half, Color.RED);
        right.setR(45);

        // Bottom triangle (rotated rectangle to mimic point)
        Rectangle bottom = new Rectangle(x + quarter, y + quarter, half, half, Color.RED);
        bottom.setR(45);

        left.draw(g2d);
        right.draw(g2d);
        bottom.draw(g2d);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        RenderingHints rh = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2d.setRenderingHints(rh);

        for (GameEntity e : ge) e.draw(g2d);

        heartsOverlay(g2d);

        // Draw Overlay Text
        if (overlayText != null) {
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 52));
            FontMetrics fm = g2d.getFontMetrics();
            int textWidth = fm.stringWidth(overlayText);
            int x = (getWidth() - textWidth) / 2;
            int y = getHeight() / 2;

            g2d.setColor(new Color(80, 80, 80));
            g2d.drawString(overlayText, x, y);
        }
    }

    public void setUpGameEntities() {        
        player.rotateTo(w/2, h/2);
        opponent.rotateTo(w/2, h/2);

        ge.add(opponent);
        ge.add(player);
        ge.add(ball);
    }

    public void setUpListeners() {
        setFocusable(true);
        requestFocusInWindow();

        addMouseListener(this);
        addMouseMotionListener(this);

        addKeyListener(this);
        addKeyListener(player);
        addMouseListener(player);
    }

    public boolean isDeflected() {
        if (isDeflected) {
            isDeflected = false;
            return true;
        }
        return false;
    }

    public double getMX() { return this.mX; }

    public double getMY() { return this.mY; }

    public void updateMousePos(MouseEvent e) {
        if (Math.abs(e.getX() - mX) <= 1 && Math.abs(e.getY() - mY) <= 1) return;
        this.mX = e.getX();
        this.mY = e.getY();
    }

    public void gameOver() {
        gameOver = true;
        this.setActive(false);
        // String instructions = "\nPress Spacebar to Play Again.";
        setOverlayText((player.getLives() > opponent.getLives()) ? "You Win!" : "You Lose.");
    }

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

            // opponent.setMX(player.getCenterX());
            // opponent.setMY(player.getCenterY());

            // if (opponent.isInRange(ball) && opponent.isVulnerable()) {
            //     // ball.isInRangeColor();
            //     opponent.deflectProcess(10, ball);
            //     continue;
            // }

            if (player.isColliding(opponent)) handleCircleRigidBodyCollision(player, opponent);

            handleEntityInteraction();

            if (player.isDeflected()) this.isDeflected = true;

            for (GameEntity e : ge) if (e != ball) e.update();

            if (player.getLives() <= 0 || opponent.getLives() <= 0) { gameOver(); }

            this.repaint();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }

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

    private void handleEntityInteraction() {
        if (player.isColliding(ball) && player.isVulnerable()) {
            player.hurt(ball);
        }
        
        ball.defaultColor();
        if (player.isInRange(ball)) {
            if (!player.isInCooldown()) {
                ball.inRangeColor();
            } else {
                ball.warningColor();
            }
        }
        if (player.isGraced()) { ball.gracedColor(); }
        if (player.isHit()) ball.hitColor();
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) { if (canMove) player.deflectProcess(10, ball); }
        
    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) { if (canMove) updateMousePos(e); }

    @Override
    public void mouseMoved(MouseEvent e) { if (canMove) updateMousePos(e); }

    @Override
    public void keyPressed(KeyEvent e) { if (e.getKeyCode() == KeyEvent.VK_R && gameOver) reset = true; }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

}
