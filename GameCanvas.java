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

public class GameCanvas extends JComponent implements Runnable, MouseListener, MouseMotionListener {
    
    private int clientID, w, h;
    private Ball ball;
    private Player player, opponent;
    private double mX = 0, mY = 0;

    private ArrayList<GameEntity> ge = new ArrayList<GameEntity>();

    private volatile boolean running = true;
    private boolean isDeflected = false;

    public GameCanvas (int w, int h, int clientID, Ball ball, Player player, Player opponent) {
        this.clientID = clientID;

        this.ball = ball;
        this.player = player;
        this.opponent = opponent;

        this.w = w;
        this.h = h;

        this.setPreferredSize(new Dimension(w, h));

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

    // public void updateBallSpeed() {
    //     if (ballSpeed < 17) {
    //         ballSpeed += 0.003;
    //     } else {
    //         ballSpeed = 15 + Math.log(ballTickCount) / 4;
            
    //     }
        
    //     ballTickCount++;
    //     ball.setSpeed(ballSpeed);

    //     // System.out.println(ballSpeed);
    // }

    public void startGameLoop() { new Thread(this).start(); }
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

                if (player.isColliding(opponent)) {
                    double dx = player.getCenterX() - opponent.getCenterX();
                    double dy = player.getCenterY() - opponent.getCenterY();
                    double distance = Math.sqrt(dx * dx + dy * dy);

                    if (distance == 0) {
                        // Prevent divide-by-zero (players perfectly overlapping)
                        dx = 1;
                        dy = 0;
                        distance = 1;
                    }

                    double overlap = (player.getSize() / 2 + opponent.getSize() / 2) - distance;

                    // Normalize
                    dx /= distance;
                    dy /= distance;

                    // Push each player away from each other by half the overlap
                    double pushX = dx * (overlap / 2);
                    double pushY = dy * (overlap / 2);

                    player.setX(player.getX() + pushX);
                    player.setY(player.getY() + pushY);
                    opponent.setX(opponent.getX() - pushX);
                    opponent.setY(opponent.getY() - pushY);
                }

                // if (opponent.isGraced()) {
                //     ball.gracedColor();
                //     continue;
                // }

                if (opponent.isColliding(ball)) {
                    ball.hitColor();
                    continue;
                }

                if (player.isGraced()) {
                    ball.gracedColor();
                    continue;
                }

                if (e.isColliding(ball)) {
                    ball.hitColor();
                    continue;
                }

                if (player.isInRange(ball) && !player.isColliding(ball)) {
                    ball.isInRangeColor();
                    continue;
                }

                ball.defaultColor();
                
            }

            for (GameEntity e : ge) if (e != ball) e.update();

            this.repaint();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {}

    @Override
    public void mousePressed(MouseEvent e) {
        player.deflectProcess(10, ball);
    }
        

    @Override
    public void mouseReleased(MouseEvent e) {
        // if (player.inDeflectRange(ball) && player.getVulnerable()) {
        //     ball.redirectTowards(mX, mY);
        //     ballDeflected = true;
        //     player.ballDeflected();
        // }
    }

    @Override
    public void mouseEntered(MouseEvent e) {}

    @Override
    public void mouseExited(MouseEvent e) {}

    @Override
    public void mouseDragged(MouseEvent e) { updateMousePos(e); }

    @Override
    public void mouseMoved(MouseEvent e) { updateMousePos(e); }

}
