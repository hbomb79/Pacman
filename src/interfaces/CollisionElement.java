package interfaces;

import controllers.CollisionController;

import java.awt.*;

public interface CollisionElement {
    /**
     * Called by the {@code CollisionController} when the {@code collisionBox} is colliding with the boundary of the game
     *
     * @param collisionBox The collision box intersecting the boundary
     * @param CollisionType The type of the collision
     * @return Returns true if the collision event is consumed, false otherwise
     */
    boolean collidedWithGameBoundary(Rectangle collisionBox, CollisionController.COLLISION_TYPE CollisionType);

    /**
     * Called by the {@code CollisionController} when the {@code collisionBox} has collided with this entity.
     *
     * @param collisionBox The collision box
     * @param source The source of the collision
     * @param infringedBoundary The boundary infringed by the collision
     * @return Returns true if the collision event is consumed, false otherwise
     */
    boolean collidedWithBy(Rectangle collisionBox, CollisionElement source, Rectangle infringedBoundary);

    /**
     * Used to test if the collision box has collided with this entity
     *
     * @param collision The collision box to test against
     * @param source The source of the collision box
     * @return Returns true if the collision box is intersecting this entity, false otherwise
     */
    Rectangle isCollisionBoxIntersecting(Rectangle collision, CollisionElement source);

    /**
     * Calculates the boundary of this entity
     *
     * @return Returns the boundary
     */
    Rectangle getBounds();
}
