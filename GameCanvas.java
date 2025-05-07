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

public class GameCanvas extends JComponent implements ActionListener, MouseListener, MouseMotionListener {
    
    private int w, h, tickCount;
    private double mX, mY, playerSize, playerRange, ballSize, ballSpeed;
    private ArrayList<GameEntity> ge = new ArrayList<GameEntity>();
    private Ball ball;
    private Player player;
    private Timer timer;

    public GameCanvas(int w, int h) {
        this.w = w;
        this.h = h;

        this.tickCount = 1;

        this.setPreferredSize(new Dimension(w, h));
        this.timer = new Timer(10, this);

        addMouseListener(this);
        addMouseMotionListener(this);

        playerSize = 80;
        playerRange = playerSize;

        player = new Player(w/2 - playerSize, h/2 - playerSize, playerSize, playerRange, Color.BLACK);

        ballSize = 120;
        ballSpeed = 1;

        ball = new Ball(100, h/2 - ballSize, ballSize, Color.BLUE);
        ball.setSpeed(ballSpeed);
        ball.setDirection(45);

        
        this.ge.add(player);
        this.ge.add(ball);

        this.addMouseListener(player);
        this.addKeyListener(player);
        this.setFocusable(true);
        this.requestFocusInWindow();

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

        this.timer.start();
    }

    public void updateMousePos(MouseEvent e) {
        this.mX = e.getX();
        this.mY = e.getY();
    }

    public void updateBallSpeed(int x) {
        ballSpeed = 1 + Math.pow(Math.log(x), 2) / 5;

        ball.setSpeed(ballSpeed);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() != this.timer) return;

        player.rotateTo(mX, mY);

        updateBallSpeed(tickCount);

        for (GameEntity e : ge) {
            double eX, eY, eW, eH;

            eX = e.getX();
            eY = e.getY();
            eW = e.getW();
            eH = e.getH();

            if (eX < 0 || eX + eW > this.w) {
                if (e == ball) ball.bounce(true);
                e.setX((eX < 0) ? 0 : w - eW);
            }
    
            if (eY < 0 || eY + eH > this.h) {
                if (e == ball) ball.bounce(false);
                e.setY((eY < 0) ? 0 : h - eH);
            }

            if (e == ball) continue;

            if (ball.isColliding(e)) {
                ball.setColor(Color.RED);

                // Thread stopBall = new Thread(() -> {
                //     try {
                //         while (ball.isColliding(e)) {
                //             ball.setColor(Color.RED);
                //             ball.canMove(false);
                //             Thread.sleep(10);
                //         }
                //         Thread.sleep(1000);
                //         tickCount = 1;
                //         ball.canMove(true);
                //     } catch (InterruptedException ie) {
                //         // ...
                //     }
                // });

                // stopBall.start();

                // ball.bounce(true);
                // ball.bounce(false);
            } else {
                ball.setColor(Color.BLUE);
            }
        }

        if (player.inSwingRange(ball) && !player.isColliding(ball)) {
            ball.setColor(Color.GREEN);
            // System.out.println("YES");
        }

        for (GameEntity e : ge) e.move();

        tickCount++;
        this.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        return;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        return;
    }
        

    @Override
    public void mouseReleased(MouseEvent e) {
        
        if (player.inSwingRange(ball)) ball.redirectTowards(mX, mY);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        return;
    }

    @Override
    public void mouseExited(MouseEvent e) {
        return;
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        updateMousePos(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        updateMousePos(e);
    }

}
