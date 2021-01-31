package entity;

import interfaces.CollisionElement;
import interfaces.EngineComponent;
import main.PacmanGame;

public abstract class Entity implements EngineComponent, CollisionElement {
    /**
     * The PacmanGame instance this entity belongs to
     */
    protected PacmanGame gameInstance;

    /**
     * The current direction of movement for this entity
     */
    protected DIRECTION direction = DIRECTION.NONE;

    /**
     * The available directions of movement for an entity
     */
    public enum DIRECTION {
        UP,
        RIGHT,
        DOWN,
        LEFT,
        NONE // Starting
    }

    protected int velocity = 2;

    /**
     * The current X position of this entity
     */
    protected int x;

    /**
     * The current Y position of this entity
     */
    protected int y;

    /**
     * The width of this entity, marked final as the dimensions should never change
     */
    protected int width;

    /**
     * The height of this entity, marked final as the dimensions should never change
     */
    protected int height;

    public Entity(PacmanGame game, int x, int y, int width, int height) {
        this.gameInstance = game;

        this.x = x;
        this.y = y;

        this.width = width;
        this.height = height;
    }

    public Entity(PacmanGame game, int x, int y) {
        this(game, x, y, PacmanGame.GRID_SIZE, PacmanGame.GRID_SIZE);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public DIRECTION getDirection() {
        return direction;
    }
}
