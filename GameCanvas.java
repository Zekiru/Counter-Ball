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
    private double mX, mY;
    // private double mX, mY, playerSize, playerRange, ballSize, ballSpeed;
    // private int w, h;
    private Ball ball;
    private Player player, opp, p1, p2;
    // private double playerSize, playerRange, offsetXP1, offsetXP2, offsetY, ballSize, ballSpeed;

    private ArrayList<GameEntity> ge = new ArrayList<GameEntity>();

    // private boolean play = false, includePlayers;

    public GameCanvas (int clientID, Ball ball, Player player, Player opp, int w, int h) {
        this.clientID = clientID;

        this.ball = ball;
        this.player = player;
        this.opp = opp;

        this.w = w;
        this.h = h;

        // this.includePlayers = false;

        this.setPreferredSize(new Dimension(w, h));
        // this.timer = new Timer(10, this);
    }

    // public GameCanvas(int clientID, int w, int h) {
    //     this.clientID = clientID;
    //     this.w = w;
    //     this.h = h;
    //     this.includePlayers = true;

    //     this.ballTickCount = 1;

    //     this.setPreferredSize(new Dimension(w, h));
    //     this.timer = new Timer(10, this);

    // }

    // public void play(boolean play) {
    //     this.play = play;

    //     if (play) {
    //         timer.start();
    //     } else {
    //         timer.stop();
    //     }
        
    // }

    // public ArrayList<GameEntity> getGE() {
    //     return ge;
    // }

    public void setUpGameEntities() {
        player.rotateTo(opp.getX() + opp.getW()/2, opp.getY() + opp.getH()/2);
        opp.rotateTo(player.getX() + player.getW()/2, player.getY() + player.getH()/2);

        ge.add(opp);
        ge.add(player);
        ge.add(ball);
    }

    public void setUpListeners() {
        // if (!includePlayers) return;

        setFocusable(true);
        requestFocusInWindow();

        addMouseListener(this);
        addMouseMotionListener(this);
        addKeyListener(player);
        addMouseListener(player);
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

    boolean botCanSwing = true;

    @Override
    public void run() {
        while (true) {
            player.rotateTo(mX, mY);

            // updateBallSpeed();

            Thread aimBot = new Thread(() -> {
                if (!botCanSwing) return;
                int delay = 300;

                double x = player.getX() + player.getW()/2;
                double y = player.getY() + player.getH()/2;
        
                try {

                    if (botCanSwing) {
                        Thread.sleep(25);
                        ball.redirectTowards(x, y);
                        opp.playSwingAnim();
                    }
                    
                    botCanSwing = false;
                    Thread.sleep(delay);
                    botCanSwing = true;
                } catch(InterruptedException ex) {
                    // ...
                }
            });
        
            // Thread t = new Thread(aimBot);
            // t.start();
            

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

                // if (opp.inSwingRange(ball) && !t.isAlive()) {

                //     // t = new Thread(aimBot);
                //     // t.start();
                    
                // }


                if (opp.isColliding(ball)) {
                    // System.out.println("Collision");
                    ball.setColor(Color.RED);
                    continue;
                }

                if (player.isColliding(ball)) {
                    // System.out.println("Collision");
                    ball.setColor(Color.BLUE);
                    continue;
                }

                if (player.inSwingRange(ball) && !player.isColliding(ball)) {
                    ball.setColor(Color.GREEN);
                    continue;
                }

                ball.setColor(Color.BLACK);
                
            }

            // if (player.inSwingRange(ball) && !player.isColliding(ball)) {
            //     ball.setColor(Color.GREEN);
            //     // System.out.println("YES");
            // }

            for (GameEntity e : ge) e.move();

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
        
        if (player.inSwingRange(ball)) ball.redirectTowards(mX, mY);
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
