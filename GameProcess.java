import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class GameProcess 
implements Runnable, MouseListener, MouseMotionListener {

    /* 

        The Magnum Opus class of this project, responsible for the system between 
    the player and ball which brings the mechanics to reality. Controls and specifies 
    interaction with the active entities in the game and is extended by these entities 
    as a result.

    */

    private int w, h, delay;

    private Player p1, p2;
    private Ball ball;
    // private GameCanvas gc;
    private ArrayList<GameEntity> ge;

    private double mX = 0, mY = 0;

    private Color contactColor = new Color(153, 0, 153);
    private Color graceColor = new Color(200, 200, 200);

    private boolean running = false, listenersRunning = false;

    private boolean canDash = true, isDashing = false;
    private boolean canCharge = true, canDeflect = false;
    private boolean isCharging = false, isDeflected = false;
    private boolean inCooldown = false, inGrace = false;
    private boolean inContact = false, inRecovery = false;

    // Client Constructor:
    public GameProcess(Player p1, Player p2, Ball ball, int w, int h, int delay) {
        this.w = w;
        this.h = h;
        this.delay = delay;
        this.p1 = p1;
        this.p2 = p2;
        this.ball = ball;
        // this.gc = gc;

        this.ge = new ArrayList<GameEntity>();

        ge.add(this.p2);
        ge.add(this.p1);
        ge.add(this.ball);

    }

    // Server Constructor:
    // public GameProcess(int delay, Ball ball) {
    //     this.delay = delay;
    //     this.ball = ball;

    //     this.ge = new ArrayList<GameEntity>();

    //     ge.add(this.ball);

    // }

    public void setUpListeners() {
        // gc.addMouseListener(this);
        // gc.addMouseMotionListener(this);

        // gc.addKeyListener(player);

        // gc.setFocusable(true);
        // gc.requestFocusInWindow();

        this.listenersRunning = true;
    }

    private void updateMousePosition(MouseEvent e) {
        if (Math.abs(e.getX() - mX) < 1) return;
        if (Math.abs(e.getY() - mY) < 1) return;

        this.mX = e.getX();
        this.mY = e.getY();
    }

    public void playerCharge(Player player) {
        if (!canCharge) return;

        canCharge = false;
        canDeflect = true;
        // player.playChargeAnim();
    }

    private void playerDeflect(Player player) {
        if (!canDeflect) return;

        // player.playDeflectAnim();
    }

    private void playerGrace(Player player) {
        

        // player.changeColor(graceColor);
    }

    private void playerContact(Player player) {

        // player.changeColor(contactColor);
    }

    private void ballDeflect() {
        ball.redirectTowards(mX, mY);
    }

    // Execute Processes:
    @Override
    public void run() {
        while (true) {
            // double bX = ball.getX(), bY = ball.getY(), bSize = ball.getW();
            // double pX = player.getX(), pY = player.getY(), pSize = player.getW();

            // if (listenersRunning) p1.rotateTo(mX, mY);

            handleBorderCollision();



            try {
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                // TODO: handle exception
            }
        }
    }

    private void handleBorderCollision() {
        for (GameEntity e : ge) {
            double eX, eY, eW, eH, w, h;

            eX = e.getX();
            eY = e.getY();
            eW = e.getW();
            eH = e.getH();

            // w = gc.getW();
            // h = gc.getH();

            // if (eX < 0 || eX + eW > w) {
            //     if (e == ball) ball.bounce(true);
            //     e.setX((eX < 0) ? 0 : w - eW);
            // }
    
            // if (eY < 0 || eY + eH > h) {
            //     if (e == ball) ball.bounce(false);
            //     e.setY((eY < 0) ? 0 : h - eH);
            // }
        }
    }

    private void handleEntityCollision() {
        
    }

    // Mouse Listener

    @Override
    public void mouseClicked(MouseEvent e) {
        // Not used
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // mousePressed = true;
        // charge();
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // mousePressed = false;
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

    @Override
    public void mouseDragged(MouseEvent e) {
        updateMousePosition(e);
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        updateMousePosition(e);
    }
}
