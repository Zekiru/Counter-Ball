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
    
    private int clientID, w, h, ballTickCount;
    private double mX, mY;
    // private double mX, mY, playerSize, playerRange, ballSize, ballSpeed;
    // private int w, h;
    private Ball ball;
    private Player player, opponent, p1, p2;
    private double playerSize, playerRange, ballSize, ballSpeed;

    private ArrayList<GameEntity> ge = new ArrayList<GameEntity>();
    private Timer timer;

    private boolean play = false, includePlayers;

    public GameCanvas (int w, int h) {
        this.w = w;
        this.h = h;
        this.includePlayers = false;

        this.setPreferredSize(new Dimension(w, h));
        this.timer = new Timer(10, this);
    }

    public GameCanvas(int clientID, int w, int h) {
        this.clientID = clientID;
        this.w = w;
        this.h = h;
        this.includePlayers = true;

        this.ballTickCount = 1;

        this.setPreferredSize(new Dimension(w, h));
        this.timer = new Timer(10, this);

        // this.ge = ge;
        // this.p1 = (Player) ge.get(0);
        // this.p2 = (Player) ge.get(1);
        // this.ball = (Ball) ge.get(2);

        // playerSize = 80;
        // playerRange = playerSize;

        // ballSize = 120;
        // ballSpeed = 1;

        // player1 = new Player(w*0.33 + playerSize/2, h/2 + playerSize/2, playerSize, playerRange, Color.BLUE);
        // player2 = new Player(w*0.66 + playerSize/2, h/2 + playerSize/2, playerSize, playerRange, Color.RED);
        // player = (clientID == 1) ? player1: player2;


        // ball = new Ball(100, h/2 - ballSize, ballSize, Color.BLACK);
        // ball.setSpeed(ballSpeed);
        // ball.setDirection(45);

        // setUpGameEntities();
        // setUpListeners();

        // frame.addKeyListener(player);
        // setFocusable(true);
        // requestFocusInWindow();

        // addMouseListener(this);
        // addMouseMotionListener(this);
        // addKeyListener(player);
        // addMouseListener(player);
    }

    public void play(boolean play) {
        this.play = play;

        if (play) {
            timer.start();
        } else {
            timer.stop();
        }
        
    }

    public ArrayList<GameEntity> getGE() {
        return ge;
    }

    public void setUpGameEntities() {
        // Attributes:
        playerSize = 70;
        playerRange = 100;
        ballSize = 120;
        ballSpeed = 5;

        // Game Entities:

        if (includePlayers) {
            p1 = new Player(w*0.25 - playerSize/2, (h - playerSize) / 2, playerSize, playerRange, Color.BLUE);
            p2 = new Player(w*0.75 - playerSize/2, (h - playerSize) / 2, playerSize, playerRange, Color.RED);
            player = (clientID != 2) ? p1: p2;
            opponent = (player == p1) ? p2: p1;

            p1.rotateTo(p2.getX() + p2.getW()/2, p2.getY() + p2.getH()/2);
            p2.rotateTo(p1.getX() + p1.getW()/2, p1.getY() + p1.getH()/2);

            ge.add(p1);
            ge.add(p2);
        }

        ball = new Ball((w - ballSize) / 2, (h - ballSize) / 2, ballSize, Color.BLACK);

        ball.setSpeed(ballSpeed);
        ball.setDirection(45);
        
        ge.add(ball);
    }

    public void setUpListeners() {
        if (!includePlayers) return;

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

        if (!play) return;

        for (GameEntity e : ge) e.draw(g2d);
    }

    public void updateMousePos(MouseEvent e) {
        this.mX = e.getX();
        this.mY = e.getY();
    }

    // public ArrayList<GameEntity> getGE() {
    //     return ge;
    // }

    public void updateBallSpeed() {
        if (ballSpeed < 17) {
            ballSpeed += 0.003;
        } else {
            ballSpeed = 15 + Math.log(ballTickCount) / 4;
            
        }
        
        ballTickCount++;
        ball.setSpeed(ballSpeed);

        System.out.println(ballSpeed);
    }

    boolean botCanSwing = true;

    @Override
    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() != this.timer) return;

        if (includePlayers) {
            player.rotateTo(mX, mY);

            double playerX = player.getX() + playerSize/2;
            double playerY = player.getY() + playerSize/2;

            opponent.rotateTo(playerX, playerY);
        }

        updateBallSpeed();

        Thread aimBot = new Thread(() -> {
            if (!botCanSwing) return;
            int delay = 300;

            double playerX = player.getX() + playerSize/2;
            double playerY = player.getY() + playerSize/2;
    
            try {

                if (botCanSwing) {
                    Thread.sleep(25);
                    ball.redirectTowards(playerX, playerY);
                    opponent.playSwingAnim();
                }
                
                botCanSwing = false;
                Thread.sleep(delay);
                botCanSwing = true;
            } catch(InterruptedException ex) {
                // ...
            }
        });
    
        Thread t = new Thread(aimBot);
        // t.start();
        

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

            if (opponent.inSwingRange(ball)) {
                // double playerX = player.getX() + playerSize/2;
                // double playerY = player.getY() + playerSize/2;
                
                // ball.redirectTowards(playerX, playerY);
                // opponent.playSwingAnim();

                if (!t.isAlive()) {
                    t = new Thread(aimBot);
                    t.start();
                }
                
            }

            if (e == ball) continue;

            if (p2.isColliding(ball)) {
                // System.out.println("Collision");
                ball.setColor(Color.RED);
                continue;
            }

            if (p1.isColliding(ball)) {
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
