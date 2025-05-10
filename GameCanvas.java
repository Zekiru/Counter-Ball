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
    private double mX = 0, mY = 0;
    private Ball ball;
    private Player player, opp;

    private ArrayList<GameEntity> ge = new ArrayList<GameEntity>();

    private boolean ballDeflected = false;

    public GameCanvas (int w, int h, int clientID, Ball ball, Player player, Player opp) {
        this.clientID = clientID;

        this.ball = ball;
        this.player = player;
        this.opp = opp;

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
        player.rotateTo(opp.getX() + opp.getW()/2, opp.getY() + opp.getH()/2);
        opp.rotateTo(player.getX() + player.getW()/2, player.getY() + player.getH()/2);

        ge.add(opp);
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

    public boolean getBallDeflected() {
        if (ballDeflected) {
            ballDeflected = false;
            return true;
        }
        return false;
        // return ballDeflected;
    }

    public double getMX() {
        return this.mX;
    }

    public double getMY() {
        return this.mY;
    }

    public void updateMousePos(MouseEvent e) {
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

    @Override
    public void run() {
        while (true) {
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

                if (player.isColliding(opp)) {
                    double dx = player.getCenterX() - opp.getCenterX();
                    double dy = player.getCenterY() - opp.getCenterY();
                    double distance = Math.sqrt(dx * dx + dy * dy);

                    if (distance == 0) {
                        // Prevent divide-by-zero (players perfectly overlapping)
                        dx = 1;
                        dy = 0;
                        distance = 1;
                    }

                    double overlap = (player.getSize() / 2 + opp.getSize() / 2) - distance;

                    // Normalize
                    dx /= distance;
                    dy /= distance;

                    // Push each player away from each other by half the overlap
                    double pushX = dx * (overlap / 2);
                    double pushY = dy * (overlap / 2);

                    player.setX(player.getX() + pushX);
                    player.setY(player.getY() + pushY);
                    opp.setX(opp.getX() - pushX);
                    opp.setY(opp.getY() - pushY);
                }

                // if (!opp.getVulnerable()) {
                //     ball.setColor(Color.DARK_GRAY);
                //     continue;
                // }

                // if (opp.isColliding(ball)) {
                //     ball.setColor(Color.RED);
                //     continue;
                // }

                if (!player.getVulnerable()) {
                    ball.setColor(Color.LIGHT_GRAY);
                    continue;
                }

                if (player.isColliding(ball)) {
                    ball.setColor(Color.BLUE);
                    continue;
                }

                if (player.inDeflectRange(ball) && !player.isColliding(ball)) {
                    ball.setColor(Color.GREEN);
                    continue;
                }

                ball.setColor(Color.BLACK);
                
            }

            player.move();
            opp.move();

            player.inRange(player.inDeflectRange(ball));

            this.repaint();

            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                System.out.println(e);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // 
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // 
    }
        

    @Override
    public void mouseReleased(MouseEvent e) {
        if (player.inDeflectRange(ball) && player.getVulnerable()) {
            ball.redirectTowards(mX, mY);
            ballDeflected = true;
            player.ballDeflected();
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        // 
    }

    @Override
    public void mouseExited(MouseEvent e) {
        // 
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // only update if changed significantly
        if (Math.abs(e.getX() - mX) > 1 || Math.abs(e.getY() - mY) > 1) {
            updateMousePos(e);
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // only update if changed significantly
        if (Math.abs(e.getX() - mX) > 1 || Math.abs(e.getY() - mY) > 1) {
            updateMousePos(e);
        }
    }

}
