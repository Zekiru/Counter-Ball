public interface GameEntity extends Drawable {

    enum Type {
        BALL,
        PLAYER,
    }

    // Get
    Type getType();
    double getSpeed();
    double getDirection();

    // Set
    void setSpeed(double speed);
    void setDirection(double direction);

    // Functional
    void move();
    boolean isColliding(GameEntity s);

}
