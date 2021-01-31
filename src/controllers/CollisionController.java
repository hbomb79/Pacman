package controllers;

import entity.Entity;
import interfaces.CollisionElement;
import main.PacmanGame;

import java.awt.*;

/**
 * CollisionController is responsible for checking for collisions between entities running inside the
 * game.
 *
 * @author Harry Felton - 18032692
 */
public class CollisionController extends Controller {
    /**
     * Used by collision logic to detect if the game collision
     * is a WALL, or an EDGE. This saves us having to create
     * skeleton classes just to tell this difference based on the
     * collision element type provided.
     */
    public enum COLLISION_TYPE {
        WALL,
        EDGE
    };

    /**
     * The EntityController from the game instance we'll be using to poll
     * entities for collisions.
     */
    EntityController entities;

    /**
     * The MapController from the game instance used when testing if the collision
     * provided is intersecting part of the map/walls.
     */
    MapController mapping;

    /**
     * Instantiate the controller by passing the SnakeGame instance to the super class,
     * and storing a reference to the EntityController from the game.
     *
     * @param g The SnakeGame instance that the {@code CollisionController} belongs to.
     */
    public CollisionController(PacmanGame g) {
        super(g);
        entities = g.getEntityController();
        mapping = g.getMapController();
    }

    /**
     * Checks to see if the {@code collisionBox} provided is colliding with any registered elements
     * from {@code EntityController}, including any {@code PacmanEntity} or the boundary of the game.
     *
     * @param source The source of the collision
     * @param collisionBox The collision boundary to be tested
     * @return If a collision occurred
     */
    public boolean checkCollision(CollisionElement source, Rectangle collisionBox) {
        // If the source is colliding with the game boundary
        if(collisionBox.x < 0 || collisionBox.x + collisionBox.width > PacmanGame.WIDTH || collisionBox.y < 0 || collisionBox.y + collisionBox.height > PacmanGame.HEIGHT) {
            if(source.collidedWithGameBoundary( collisionBox, COLLISION_TYPE.EDGE )) {
                return true;
            }
        }

        // Test if source is colliding with the game walls
        Rectangle infringed = mapping.checkSelectedMapCollision(collisionBox);
        if(infringed != null) {
            if(source.collidedWithGameBoundary( infringed, COLLISION_TYPE.WALL )) {
                return true;
            }
        }

        // Check with the entity controller for currently on-map entities
        for(Entity p : entities.entities) {
            Rectangle collidedWith = p.isCollisionBoxIntersecting(collisionBox, source);
            if(collidedWith != null) {
                if(p.collidedWithBy(collisionBox, source, collidedWith)) {
                    return true;
                }
            }
        }

        return false;
    }
}
