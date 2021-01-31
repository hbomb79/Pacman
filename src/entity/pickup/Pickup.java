package entity.pickup;

import controllers.CollisionController;
import entity.Entity;
import interfaces.CollisionElement;
import main.PacmanGame;

import java.awt.*;

/**
 * The Pickup class handles some of the backend core functionality of on-screen Pickups
 *
 * @author Harry Felton - 18032692
 */
public abstract class Pickup extends Entity {
    /**
     * Represents if the Pickup is marked to be destroyed by the {@code EntityController}
     */
    protected boolean toBeDestroyed = false;

    /**
     * The dead-zone of the game (in pixels), used to stop {@code Pickup} instances from being spawned
     * too close to the edge of the screen.
     */
    protected int spawnDeadzone = 50;

    /**
     * Constructs a {@code Pickup} instance with the information provided, and calls the init callback
     *
     * @param game The {@code PacmanGame} instance this Pickup belongs too
     * @param pX The X position of the {@code Pickup}
     * @param pY The Y position of the {@code Pickup}
     */
    public Pickup(PacmanGame game, int pX, int pY) {
        super(game, pX, pY);

        init();
    }

    /**
     * Constructs the {@code Pickup} with default position of {@code x,y(1,1)}
     *
     * @param game The {@code PacmanGame} instance this Pickup belongs too
     */
    public Pickup(PacmanGame game) {
        this(game, 1, 1);
    }

    /**
     * Called when the {@code Pickup} is ready for initialisation
     */
    public void init(){
    }

    /**
     * Applies the effect by destroying the Pickup via the {@code EntityController} attached to the {@code gameInstance}
     *
     * @param e The entity the effect is being applied to
     */
    public void applyEffect(Entity e) {
        destroy();
    }

    /**
     * Destroy the {@code Pickup} by scheduling it's removal via the {@code EntityController}
     */
    public void destroy(){
        gameInstance.getEntityController().destroyPickup(this);
        toBeDestroyed = true;
    }

    /**
     * Creates a {@code Rectangle} which represents the boundary of the {@code Pickup}
     *
     * @return The boundary of the pickup
     */
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    /**
     * Checks to see if the collision box provided intersects this pickups boundary
     *
     * @param collision The collision box to test
     * @param source The source of the collision
     * @return Returns true if the collision box is proven to intersect, false otherwise
     */
    @Override
    public Rectangle isCollisionBoxIntersecting(Rectangle collision, CollisionElement source) {
        return getBounds().intersects(collision) ? getBounds() : null;
    }

    /**
     * Called when the pickup has collided with the border of the game; no action required
     * @param collisionBox The collision box of the pickup that is said to be colliding with the boundary
     * @param CollisionType The type of the collision
     * @return Returns false as the collision is being ignored
     */
    @Override
    public boolean collidedWithGameBoundary(Rectangle collisionBox, CollisionController.COLLISION_TYPE CollisionType) { return false; }

    /**
     * Tests that the given co-ordinates are within the eligible zone of the game (as in not outside of the deadzone)
     *
     * @param x The x co-ordinate to be tested
     * @param y The y co-ordinate to be tested
     * @return Returns true if position is valid
     */
    public boolean checkSpawnPoint(int x, int y) {
        Rectangle eligibleZone = new Rectangle(spawnDeadzone, spawnDeadzone, PacmanGame.WIDTH - (spawnDeadzone*2), PacmanGame.HEIGHT - (spawnDeadzone*2));
        return eligibleZone.contains(x,y);
    }

    /**
     * Called for each tick of the game; no action is required for this entity
     * @param dt Amount of time passed since last update
     */
    @Override
    public void update(double dt) {}
}
