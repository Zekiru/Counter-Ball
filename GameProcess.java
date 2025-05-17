import java.awt.*;
import java.util.*;

public class GameProcess extends AsyncTask {

    /* 

        The Magnum Opus class of this project, responsible for the system between 
    the player and ball which brings the mechanics to reality. Controls and specifies 
    interaction with the active entities in the game and is extended by these entities 
    as a result.

    */

    private int w, h;
    private Player p1, p2;
    private Ball ball;
    private GameCanvas gc;
    private ArrayList<GameEntity> ge;

    // Client Constructor:
    public GameProcess(int interval, GameCanvas gc) {
        super(interval);
        this.gc = gc;

        this.w = gc.getW();
        this.h = gc.getH();
        this.p1 = gc.getPlayer();
        this.p2 = gc.getOpponent();
        this.ball = gc.getBall();

        this.ge = new ArrayList<GameEntity>();

        ge.add(this.p2);
        ge.add(this.p1);
        ge.add(this.ball);

        startTask();
    }

    // Execute Processes:
    @Override
    public void runnable() {

        gc.run();
        
        // GameEntity.EntityType p = GameEntity.EntityType.PLAYER;
        // GameEntity.EntityType b = GameEntity.EntityType.PLAYER;

        // for (GameEntity e : ge) {

            // GameEntity.EntityType et1 = e1.getType();

            // handleBordersCollision(e);

            // for (GameEntity e2 : ge) {

            //     if (e1 == e2) continue;

            //     GameEntity.EntityType et2 = e2.getType();

            //     boolean bothPlayers = et1 == p && et2 == p;


            //     if (bothPlayers) handlePlayersCollision((Player) e1, (Player) e2);

            //     // if (playerBall) handlePlayerBallCollision(e1, e2);

            //     // if (ballPlayer) handlePlayerBallCollision(e2, e1);
            // }
        // }

        // handlePlayersCollision(p1, p2);

        // handlePlayerBallCollision(p1);
        // handlePlayerBallCollision(p2);

        // for (GameEntity e : ge) e.update();

        // System.out.printf("Ball(%f, %f)\n", this.ball.getX(), this.ball.getY());

        // gc.repaint();
    }

    private void handleBordersCollision(GameEntity e) {
        double eX, eY, eW, eH;

        eX = e.getX();
        eY = e.getY();
        eW = e.getW();
        eH = e.getH();

        if (eX < 0 || eX + eW > w) {
            if (e == ball) ball.bounce(true);
            e.setX((eX < 0) ? 0 : w - eW);
        }

        if (eY < 0 || eY + eH > h) {
            if (e == ball) ball.bounce(false);
            e.setY((eY < 0) ? 0 : h - eH);
        }
    }

    private void handlePlayersCollision(Player p1, Player p2) {

        double dx = p1.getCenterX() - p2.getCenterX();
        double dy = p1.getCenterY() - p2.getCenterY();
        double distance = Math.sqrt(dx * dx + dy * dy);

        if (distance == 0) {
            // Prevent divide-by-zero (players perfectly overlapping)
            dx = 1;
            dy = 0;
            distance = 1;
        }

        double overlap = (p1.getSize() / 2 + p2.getSize() / 2) - distance;

        // Normalize
        dx /= distance;
        dy /= distance;

        // Push each player away from each other by half the overlap
        double pushX = dx * (overlap / 2);
        double pushY = dy * (overlap / 2);

        p1.setX(p1.getX() + pushX);
        p1.setY(p1.getY() + pushY);
        p2.setX(p2.getX() - pushX);
        p2.setY(p2.getY() - pushY);

    }

    private void handlePlayerBallCollision(Player p) {

        if (p.isColliding(ball)) {
            p.hurt(ball);
        } else if (p.isInRange(ball)) {
            ball.inRangeColor();
        } else {
            ball.defaultColor();
        }

    }

}
