package main;

import controllers.CollisionController;
import entity.pickup.Pickup;
import interfaces.CollisionElement;

import java.awt.*;

/**
 * The RandomPoint is used to find a set of co-ordinates that is not colliding with any other entities or the game
 * boundary.
 *
 * @author Harry Felton - 18032692
 */
public class RandomPoint extends Point implements CollisionElement {
    /**
     * The width of the collision box used when testing if this point is colliding
     */
    protected int width;

    /**
     * The height of the collision box used when testing if this point is colliding
     */
    protected int height;

    /**
     * The {@code Pickup} instance attached to the point
     */
    protected Pickup pickup;

    /**
     * Construct the new random point, and set the width and height to that of the {@code Pickup} instance provided
     *
     * @param pickup The pickup instance used to test for free co-ordinates
     */
    public RandomPoint(Pickup pickup) {
        super();

        width = pickup.getWidth();
        height = pickup.getHeight();
        this.pickup = pickup;
    }

    /**
     * Selects a random and free point for use by the {@code Pickup} by querying the {@code CollisionController} and
     * the pickup to ensure it's outside of the deadzone, and not in the way of any other entities
     */
    public void selectPoint() {
        PacmanGame g = PacmanGame.getGameInstance();
        CollisionController c = g.getCollisionController();

        Point p;
        do {
            p = g.generateRandomPoint();
            x = p.x;
            y = p.y;
        } while (!pickup.checkSpawnPoint(x, y) || c.checkCollision(this, getBounds()));
    }

    /**
     * Notifies this point that we've collided with the game boundary. Consume the collision event so that the
     * EntityController will return true during our collision testing
     *
     * @param collisionBox The collision box intersecting the boundary
     * @param CollisionType The type of the collision
     * @return Return true to consume the collision
     * @see #selectPoint()
     */
    @Override
    public boolean collidedWithGameBoundary(Rectangle collisionBox, CollisionController.COLLISION_TYPE CollisionType) {
        return true;
    }

    /**
     * Notifies us that we've been collided with. We're not interested in that so the collision is ignored
     *
     * @param collisionBox The collision box
     * @param source The source of the collision
     * @param infringedBoundary The boundary infringed by the collision
     * @return Returns false to allow the collision to continue to propagate
     */
    @Override
    public boolean collidedWithBy(Rectangle collisionBox, CollisionElement source, Rectangle infringedBoundary) {
        return false;
    }

    /**
     * Test if the collision box provided is colliding with the random point. This entity doesn't exist in the
     * game, and cannot be collided with by other entities
     *
     * @param collision The collision box to test against
     * @param source The source of the collision box
     * @return Returns null to indicate no boundary has been infringed by this collision
     */
    @Override
    public Rectangle isCollisionBoxIntersecting(Rectangle collision, CollisionElement source) {
        return null;
    }

    /**
     * Calculates the boundary of the {@code RandomPoint}
     * @return Returns the boundary
     */
    @Override
    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}
