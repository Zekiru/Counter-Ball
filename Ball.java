import java.awt.*;
import java.util.Random;

public class Ball extends GameEntity {

    private int delta = 1;
    private double size, velocity, initialVelocity, direction;

    private Circle render;
    private Color currentColor;

    private final static Color hitColor = Color.BLACK;
    private final static Color gracedColor = new Color(200, 200, 200);
    private final static Color inRangeColor = Color.GREEN;

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
        if (!this.active) return;

		double radians = Math.toRadians(direction);

        this.x += Math.cos(radians) * this.velocity;
        this.y += Math.sin(radians) * this.velocity;
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

    public void redirectTowards(double x, double y) {
        double dx = x - (this.x + (this.size/2));
        double dy = y - (this.y + (this.size/2));

        direction = Math.toDegrees(Math.atan2(dy, dx));

        if (direction < 0) direction += 360;
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

    public void changeColor(Color color) { currentColor = color; }

    public void defaultColor() { currentColor = this.color; }

    public void isInRangeColor() { currentColor = inRangeColor; }

    public void gracedColor() { currentColor = gracedColor; }

    public void hitColor() { currentColor = hitColor; }

    public void updateBallVelocity() {
        
    }

    public void resetBallVelocity() {
        velocity = initialVelocity;
        delta = 1;
    }

    public void run(int w, int h) {
        AsyncTask r = new AsyncTask(10) {
            @Override
            protected void runnable() {
                if (velocity < 17) {
                    velocity += 0.003;
                } else {
                    velocity = 15 + Math.log(delta) / 4;
                    delta++;
                }

                if (x < 0 || x + size > w) {
                    bounce(true);
                    setX((x < 0) ? 0 : w - size);
                }

                if (y < 0 || y + size > h) {
                    bounce(false);
                    setY((y < 0) ? 0 : h - size);
                }

                update();
            }
        };
        r.startTask();
    }

}
