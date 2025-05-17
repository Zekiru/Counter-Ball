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
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

// import GameEntity.EntityType;

public class GameCanvas extends JComponent implements Runnable, MouseListener, MouseMotionListener {
    
    private int clientID, w, h;
    private Ball ball;
    private Player player, opponent;
    private double mX = 0, mY = 0;

    private ArrayList<GameEntity> ge = new ArrayList<GameEntity>();

    private volatile boolean running = true;
    private boolean isDeflected = false;
    private double initialV = 5;
    private int delta = 1;

    public GameCanvas (int w, int h, int clientID, Ball ball, Player player, Player opponent) {
        this.clientID = clientID;

        this.w = w;
        this.h = h;

        this.ball = ball;
        this.player = player;
        this.opponent = opponent;

        this.setPreferredSize(new Dimension(w, h));

    }

    public int getW() { return this.w; }
    public int getH() { return this.h; }
    public Player getPlayer() { return this.player; }
    public Player getOpponent() { return this.opponent; }
    public Ball getBall() { return this.ball; }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;

        RenderingHints rh = new RenderingHints(
            RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_ON
        );
        g2d.setRenderingHints(rh);

        for (GameEntity e : ge) e.draw(g2d);

        // Draw player lives
        drawHearts(g2d, player.getLives(), 20, 20); // top-left
        drawHearts(g2d, opponent.getLives(), w - 110, 20); // top-right
    }

    public void setUpGameEntities() {
        player.rotateTo(opponent.getX() + opponent.getW()/2, opponent.getY() + opponent.getH()/2);
        opponent.rotateTo(player.getX() + player.getW()/2, player.getY() + player.getH()/2);

        ge.add(opponent);
        ge.add(player);
        ge.add(ball);
    }

    public void setUpListeners() {
        setFocusable(true);
        requestFocusInWindow();

        addMouseListener(this);
        addMouseMotionListener(this);

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

    public void startGameLoop() { new Thread(this).start(); }
    // public void startGameLoop() { new GameProcess(10, this); }
    public void endGameLoop() { this.running = false; }

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
            // System.out.println(player.getLives());
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
    public void mousePressed(MouseEvent e) { player.deflectProcess(10, ball); }
        
    @Override
    public void mouseReleased(MouseEvent e) {}

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) { updateMousePos(e); }

    @Override
    public void mouseMoved(MouseEvent e) { updateMousePos(e); }

    // UI

    private void drawHearts(Graphics2D g2d, int lives, int x, int y) {
        g2d.setColor(Color.RED);
        int heartSize = 20;
        int gap = 5;

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
        bottom.setR(45); // Assuming Drawable supports rotation via `r`

        left.draw(g2d);
        right.draw(g2d);
        bottom.draw(g2d);
    }

}
