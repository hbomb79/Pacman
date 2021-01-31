package entity;

import controllers.CollisionController;
import controllers.EntityController;
import controllers.MapController;
import controllers.SpriteController;
import entity.ghost.GhostEntity;
import interfaces.CollisionElement;
import main.PacmanGame;
import main.Player;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * The PacmanEntity handles the drawing, animations, collisions, pickups, etc of the player/pacman.
 */
public class PacmanEntity extends Entity implements CollisionElement {
    /**
     * The current frame of the sprites used to represent the {@code PacmanEntity}.
     */
    protected int currentFrame = 0;

    /**
     * The amount of frames that exist when representing the {@code PacmanEntity}.
     */
    protected final int maxFrames;

    /**
     * The positions of the sprites in the master spritesheet that we'll use to draw Pacman.
     */
    private final int[][]   PACMAN_SPRITES = {{0,0}, {16,0}, {32,0}, {16,0}};

    private final int       SPRITE_DELAY = 100;
    private long            SPRITE_TARGET_TIME = 0;

    /**
     * The width of each PacmanSprite, and therefore, the entity itself.
     */
    private final int       PACMAN_SPRITE_WIDTH = 16;

    /**
     * The height of each PacmanSprite, and therefore, the entity itself.
     */
    private final int       PACMAN_SPRITE_HEIGHT = 16;

    /**
     * The sprites loaded during initialisation, used to draw the {@code PacmanEntity}
     */
    protected final BufferedImage[] sprites;

    /**
     * The player that is controlling this pacman
     */
    protected final Player player;

    /**
     * The direction that this entity wants to turn, will attempt to
     * change the direction to this one every tick update
     */
    protected DIRECTION nextDirection;

    protected boolean   isVulnerable            = true;
    protected long      invulnerabilityTimeout  = 0;

    /**
     * PacmanEntity constructor is responsible for initialising the super class, and requesting
     * the pacman sprites from the SpriteController.
     *
     * @param game The PacmanGame instance the entity belongs to
     * @param player The player instance attached to this entity
     * @param x The X position of the pacman entity
     * @param y The Y position of the pacman entity
     */
    public PacmanEntity(PacmanGame game, Player player, int x, int y) {
        super(game, x, y);

        this.player = player;

        SpriteController s = game.getSpriteController();
        this.sprites = s.getSprites(PACMAN_SPRITES, PACMAN_SPRITE_WIDTH, PACMAN_SPRITE_HEIGHT);
        this.maxFrames = this.sprites.length;
    }

    /**
     * Fetch the attached player
     *
     * @return Returns the {@code Player} instance that the snake belongs to
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Changes the direction of this entity to the new direction provided.
     *
     * @param direction The new direction of movement
     */
    public void changeDirection(DIRECTION direction) {
        this.nextDirection = direction;
    }

    /**
     * This method is called when the pacman entity attempts to move through a part of the game boundary;
     * as in any WALLS. A collision with the edge of the game space allows the pacman to teleport through
     * to the other side of the map.
     *
     * Hence, collisionBox can either be a wall, or the boundary of the game. Depending on which we receive,
     * this function will either STOP the entity from moving, or will transport the entity to the other end of the map.
     *
     * @param collisionBox The collision box intersecting the boundary.
     * @param collisionType The collision type we're responding to, EDGE or WALL.
     * @return True if the collision event is consumed, false otherwise.
     */
    @Override
    public boolean collidedWithGameBoundary(Rectangle collisionBox, CollisionController.COLLISION_TYPE collisionType) {
        if(collisionType == CollisionController.COLLISION_TYPE.WALL) {
            // BLOCK this entities movement by restoring it back to the edge of the wall
            // The edge we move the entity back to depends on it's direction
            switch(direction) {
                case UP:
                    this.y = collisionBox.y + collisionBox.height;
                    break;
                case LEFT:
                    this.x = collisionBox.x + collisionBox.width;
                    break;
                case RIGHT:
                    this.x = collisionBox.x - this.width;
                    break;
                case DOWN:
                    this.y = collisionBox.y - this.height;
                    break;
                default: break;
            }
        } else {
            // Transport Pacman to the other side of the map.
            switch (direction) {
                case UP:
                    this.y = PacmanGame.HEIGHT - this.height;
                    break;
                case DOWN:
                    this.y = 0;
                    break;
                case LEFT:
                    this.x = PacmanGame.WIDTH - this.width;
                    break;
                case RIGHT:
                    this.x = 0;
                    break;
                default: break;
            }
        }

        return false;
    }

    /**
     * Tests if the provided collision box is intersecting with this entity. If the collision box
     * originated from this entity, null is returned. If not, this entities infringed collision
     * box is returned if a collision occurred, null otherwise.
     *
     * @param collision The collision box to test against
     * @param source The source of the collision box
     * @return Returns the collision boundary of this entity if a collision is present, null otherwise.
     */
    @Override
    public Rectangle isCollisionBoxIntersecting(Rectangle collision, CollisionElement source) {
        if(!source.equals(this)) {
            Rectangle bounds = getBounds();
            return bounds.intersects(collision) ? bounds : null;
        }

        return null;
    }

    /**
     * Returns a rectangle containing the boundary of this entity.
     *
     * @return A bounding rectangle
     */
    @Override
    public Rectangle getBounds() {
        return new Rectangle(this.x, this.y, this.width, this.height);
    }

    /**
     * Moves the PacmanEntity in it's current direction based on the per-second-velocity, and the amount of time
     * that has passed since the last update ({@code dt}).
     *
     * @param dt The amount of time that has passed since the last update tick
     */
    public void move(double dt) {
        double apparentVelocity = velocity;
        switch(direction) {
            case UP:
                apparentVelocity = apparentVelocity * -1;
            case DOWN:
                y+=apparentVelocity;
                break;

            case LEFT:
                apparentVelocity = apparentVelocity * -1;
            case RIGHT:
                x+=apparentVelocity;
                break;
            case NONE:
            default: break;
        }
    }

    /**
     * Handles an update tick passed to this entity by processing movement and collisions
     *
     * @param dt The amount of time that has passed since the last update tick
     */
    @Override
    public void update(double dt) {
        CollisionController collisions = gameInstance.getCollisionController();
        MapController mapController = gameInstance.getMapController();

        // Move the entity
        move(dt);

        // Check for collisions at new location, and rectify them if any occur.
        collisions.checkCollision(this, getBounds());

        // If the entity wants to make a turn, check if it can right now.
        if( this.nextDirection != null ) {
            int buffer = 1;

            // Generate some offsets to shift the collision box of the entity in the direction we're searching
            int x = nextDirection == DIRECTION.LEFT ? buffer * -1 : nextDirection == DIRECTION.RIGHT ? buffer : 0;
            int y = nextDirection == DIRECTION.UP ? buffer * -1 : nextDirection == DIRECTION.DOWN ? buffer : 0;

            // Check if the area is clear
            Rectangle newDirectionCheck = new Rectangle(this.x + x, this.y + y, this.width, this.height);
            if (mapController.checkSelectedMapCollision(newDirectionCheck) == null) {
                this.direction = nextDirection;
                nextDirection = null;
            }
        }

        // Used to delay sprite animations, wait SPRITE_DELAY_TIME before
        // advancing to the next sprite.
        if(System.currentTimeMillis() >= SPRITE_TARGET_TIME) {
            currentFrame = currentFrame == maxFrames-1 ? 0 : currentFrame+1;
            SPRITE_TARGET_TIME = System.currentTimeMillis() + SPRITE_DELAY;
        }

        if(!this.isVulnerable && System.currentTimeMillis() >= invulnerabilityTimeout) {
            // No longer invulnerable.
            setIsVulnerable(true);
        }

        // Update the player model with our dt, used for score colour animations
        player.update(dt);
    }

    /**
     * Draws the PacmanEntity to it's game instance and advance the sprites
     */
    @Override
    public void paintComponent() {
        Graphics2D g = gameInstance.getGameGraphics();
        BufferedImage sprite = sprites[currentFrame];

        double rotationDegrees = 0;
        switch(direction) {
            case UP:
                rotationDegrees = -90;
                break;
            case DOWN:
                rotationDegrees = 90;
                break;
            case LEFT:
                rotationDegrees = 180;
                break;
            default: break;
        }
        double theta = Math.toRadians(rotationDegrees);
        AffineTransform rot = AffineTransform.getRotateInstance(theta, PACMAN_SPRITE_WIDTH/2.0, PACMAN_SPRITE_HEIGHT/2.0);
        AffineTransformOp transform = new AffineTransformOp(rot, AffineTransformOp.TYPE_BILINEAR);

        g.drawImage(transform.filter(sprite, null), this.x, this.y, null);
    }

    protected void setIsVulnerable(boolean isVulnerable) {
        this.isVulnerable = isVulnerable;

        // Notify ghosts
        ArrayList<GhostEntity> ghosts = gameInstance.getEntityController().getGhosts();
        for(GhostEntity ghost: ghosts) {
            ghost.pacmanStateChange(this);
        }
    }

    public boolean getIsVulnerable() {
        return isVulnerable;
    }

    public void makeInvulnerable(long duration) {
        this.invulnerabilityTimeout = System.currentTimeMillis() + duration;
        setIsVulnerable(false);
    }

    public long getInvulnerabilityTimeout() {
        return invulnerabilityTimeout;
    }

    @Override
    public boolean collidedWithBy(Rectangle collisionBox, CollisionElement source, Rectangle infringedBoundary) {
        return false;
    }
}
