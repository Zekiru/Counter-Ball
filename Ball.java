import java.awt.*;
import java.util.Random;

public class Ball extends GameEntity {

    private int delta = 1;
    private double size, velocity, initialVelocity, direction, power = 0;

    private Circle render;
    private Color currentColor;

    private AsyncTask ballProcess;

    private final static Color hitColor = new Color(0, 0, 0);
    private final static Color gracedColor = new Color(200, 200, 200);
    private final static Color warningColor = new Color(255, 165, 0);
    private final static Color inRangeColor = new Color(0, 255, 0);

    public Ball(double x, double y, double size, double velocity, Color color) {
        super(x, y, size, size, color);
        
        this.size = size;
        this.velocity = velocity;
        this.initialVelocity = velocity;
        this.direction = new Random().nextInt(360);

        this.render = new Circle(x, y, size, color);
        this.currentColor = color;
    }

    public double getVelocity() {return this.velocity; }
    public void setVelocity(double velocity) { this.velocity = velocity; }

    @Override
	public void update() {
        double powerMultiplier = 15;
        double powerAdd = (powerMultiplier * power / 100);
        double powerAndVelocity = velocity + powerAdd;

        if (!this.active) return;

		double radians = Math.toRadians(direction);

        this.x += Math.cos(radians) * powerAndVelocity;
        this.y += Math.sin(radians) * powerAndVelocity;
	}

	@Override
	public void draw(Graphics2D g2d) { 
        render.setX(this.x);
        render.setY(this.y);
        render.setW(this.size);
        render.setH(this.size);
        render.setColor(this.currentColor);
        render.draw(g2d);
    }

	@Override
	public GameEntity.EntityType getType() { return EntityType.BALL; }

    public void resetVelocity() {
        velocity = initialVelocity;
        power = 0;
        delta = 1;
    }

    public void bounce(boolean vertical) {
        if (vertical) {
            // Reflect horizontally (e.g., left or right wall)
            direction = (180 - direction) % 360;
        } else {
            // Reflect vertically (e.g., top or bottom wall)
            direction = (360 - direction) % 360;
        }
    
        if (direction < 0) direction += 360;
    }

    public void redirectTowards(double x, double y, double power) {
        double dx = x - (this.x + (this.size/2));
        double dy = y - (this.y + (this.size/2));

        direction = Math.toDegrees(Math.atan2(dy, dx));

        if (direction < 0) direction += 360;

        this.power += ((power < 25) ? 0 : power);
    }

    public boolean isColliding(GameEntity e) {
        EntityType type = e.getType();
        // Circle on Circle Collisions
        if (type == EntityType.BALL || type == EntityType.PLAYER) {
            double r1 = size / 2;
            double r2 = e.getW() / 2;

            double centerX1 = this.x + r1;
            double centerY1 = this.y + r1;
            double centerX2 = e.getX() + r2;
            double centerY2 = e.getY() + r2;

            double dx = centerX2 - centerX1;
            double dy = centerY2 - centerY1;
            double distance = Math.sqrt(dx * dx + dy * dy);
            double minDist = r1 + r2;

            return distance < minDist;
        }

        return false;
    }

    public void defaultColor() { currentColor = this.color; }

    public void inRangeColor() { currentColor = inRangeColor; }

    public void warningColor() { currentColor = warningColor; }

    public void gracedColor() { currentColor = gracedColor; }

    public void hitColor() { currentColor = hitColor; }

    public void endProcess() { if (ballProcess != null) ballProcess.endTask(); }

    public void startProcess(int w, int h) {
        ballProcess = new AsyncTask(10) {
            @Override
            protected void runnable() {
                // System.out.println(velocity);
                // System.out.println(power);
                velocity = initialVelocity + Math.log(Math.pow((delta + 4)/5, 3/2));
                delta++;

                if (x < 0 || x + size > w) {
                    bounce(true);
                    setX((x < 0) ? 0 : w - size);
                }

                if (y < 0 || y + size > h) {
                    bounce(false);
                    setY((y < 0) ? 0 : h - size);
                }

                update();

                if (delta % 4 == 0) power -= (power > 0.01) ? Math.log(power + 1) / 10 : power;
                power = (power > 50) ? 50 : power;
            }
        };
        ballProcess.startTask();
    }

}
