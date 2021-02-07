package entity.ghost;

import controllers.CollisionController;
import controllers.EffectController;
import controllers.SpriteController;
import effects.TextFadeEffect;
import entity.Entity;
import entity.PacmanEntity;
import exception.InvalidPathFindingException;
import interfaces.CollisionElement;
import main.PacmanGame;
import main.Path;
import main.PathFinder;
import main.Player;
import ui.Text;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * The abstract GhostEntity class is used to store the pathfinding logic used by
 * the various different ghosts.
 *
 * For instance, one ghost may follow Pacman directly, while another pathfinds to a location
 * in-front of Pacman. When pathfinding, the cost of a path will be increased significantly if
 * another ghost is already in that path. This encourages the ghosts to split up, even if
 * the path isn't the most direct.
 *
 * @author Harry Felton - 18032692
 */
public abstract class GhostEntity extends Entity {
    /**
     * The positions of the sprites in the master spritesheet that we'll use to draw the ghost.
     */
    private final Map<DIRECTION, int[][]> EATEN_GHOST_SPRITES;
    private final Map<DIRECTION, int[][]> FLEEING_GHOST_SPRITES;

    private enum STATE {
        TRACKING,
        FLEEING,
        EATEN
    }
    private STATE currentState = STATE.TRACKING;
    private STATE nextState;

    private final int       GHOST_SPRITE_DELAY = 100;
    private long            GHOST_SPRITE_TARGET_TIME = 0;

    /**
     * The width of each PacmanSprite, and therefore, the entity itself.
     */
    private final int       GHOST_SPRITE_WIDTH = 16;

    /**
     * The height of each PacmanSprite, and therefore, the entity itself.
     */
    private final int       GHOST_SPRITE_HEIGHT = 16;

    private int currentFrame = 0;
    private final Map<DIRECTION, BufferedImage[]> sprites = new HashMap<>();
    private final Map<DIRECTION, BufferedImage[]> fleeingSprites = new HashMap<>();
    private final Map<DIRECTION, BufferedImage[]> eatenSprites = new HashMap<>();

    private Map<DIRECTION, BufferedImage[]> activeSprites = this.sprites;

    protected int PATH_FINDING_MAX_DEPTH = 20;

    protected Path pathfindingRoute;
    protected Point nextTarget;

    private final Point gridSpawnPoint;

    public GhostEntity(PacmanGame game, int x, int y) {
        super(game, x, y);

        this.gridSpawnPoint = main.Map.getGridBasedPoint(x, y);
        this.nextTarget = main.Map.getGridBasedPoint(x, y);
        this.velocity = 1;

        SpriteController spriteController = gameInstance.getSpriteController();

        // Populate the sprite lists for when the ghost has been eaten
        EATEN_GHOST_SPRITES = new HashMap<>();
        EATEN_GHOST_SPRITES.put(DIRECTION.RIGHT, new int[][]{{128,80}});
        EATEN_GHOST_SPRITES.put(DIRECTION.LEFT, new int[][]{{144,80}});
        EATEN_GHOST_SPRITES.put(DIRECTION.UP, new int[][]{{160,80}});
        EATEN_GHOST_SPRITES.put(DIRECTION.DOWN, new int[][]{{176,80}});
        EATEN_GHOST_SPRITES.put(DIRECTION.NONE, EATEN_GHOST_SPRITES.get(DIRECTION.UP));

        // Populate the sprite lists for when the ghost is fleeing
        FLEEING_GHOST_SPRITES = new HashMap<>();
        FLEEING_GHOST_SPRITES.put(DIRECTION.RIGHT, new int[][]{{128,64}, {144,64}, {160, 64}, {176, 64}});

        // These sprites have no direction so just one sprite set is required for all directions
        FLEEING_GHOST_SPRITES.put(DIRECTION.LEFT,   FLEEING_GHOST_SPRITES.get(DIRECTION.RIGHT));
        FLEEING_GHOST_SPRITES.put(DIRECTION.UP,     FLEEING_GHOST_SPRITES.get(DIRECTION.RIGHT));
        FLEEING_GHOST_SPRITES.put(DIRECTION.DOWN,   FLEEING_GHOST_SPRITES.get(DIRECTION.RIGHT));
        FLEEING_GHOST_SPRITES.put(DIRECTION.NONE,   FLEEING_GHOST_SPRITES.get(DIRECTION.RIGHT));


        // Fetch sprites from the SpriteController
        EATEN_GHOST_SPRITES.forEach((DIRECTION d, int[][] pos) -> {
            BufferedImage[] spriteSet = spriteController.getSprites(pos, GHOST_SPRITE_WIDTH, GHOST_SPRITE_HEIGHT);
            this.eatenSprites.put(d, spriteSet);
        });
        FLEEING_GHOST_SPRITES.forEach((DIRECTION d, int[][] pos) -> {
            BufferedImage[] spriteSet = spriteController.getSprites(pos, GHOST_SPRITE_WIDTH, GHOST_SPRITE_HEIGHT);
            this.fleeingSprites.put(d, spriteSet);
        });
    }

    /**
     * Given a Map of key,value pairs, this method will iterate over them. The value, a 2D int array, is used
     * to determine the positions of the sprites to be loaded. The key, a DIRECTION enum, is used as the key in the
     * {@code sprites} array existing on this instance. This allows the {@code GhostEntity} to use a certain selection
     * of sprites based on it's direction of movement.
     *
     * @param spriteMap The Map which will be iterated over and loaded from.
     */
    public void setGhostSprites(Map<DIRECTION, int[][]> spriteMap) {
        SpriteController spriteController = gameInstance.getSpriteController();
        spriteMap.forEach((DIRECTION d, int[][] pos) -> {
            BufferedImage[] spriteSet = spriteController.getSprites(pos, GHOST_SPRITE_WIDTH, GHOST_SPRITE_HEIGHT);
            this.sprites.put(d, spriteSet);
        });
    }

    /**
     * Returns the grid-based position of the pacman, OR a point to flee to if the Pacman is invulnerable.
     *
     * @return The target point to track
     */
    private Point getTargetGridPosition() {
        if(currentState == STATE.EATEN) {
            return this.gridSpawnPoint;
        }

        PacmanEntity target = gameInstance.getEntityController().getPlayer();
        int x = target.getX();
        int y = target.getY();

        // Transform to grid based by rounding to nearest multiple of the grid size
        int gridSize = PacmanGame.GRID_SIZE;
        Point p = new Point(x / gridSize, y / gridSize);

        if(!target.getIsVulnerable()) {
            // We want to calculate an avoidance path away from this
            // point so we aren't eaten by the Pacman.
            return gameInstance.getMapController()
                    .getSelectedMap()
                    .calculateFlankRoute(p, 5, target.getDirection());
        } else {
            return p;
        }
    }

    /**
     * Gets the position of the Pacman entity, grid relative, and attempts to use the {@code PathFinder}
     * class to seek the entity out.
     *
     * @param targetPoint The location that the ghost is trying to seek to
     */
    protected void calculatePath(Point targetPoint) {
        int gridSize = PacmanGame.GRID_SIZE;
        int gridX = this.x / gridSize;
        int gridY = this.y / gridSize;

        try {
            PathFinder pathFinder = new PathFinder(gameInstance, PATH_FINDING_MAX_DEPTH);
            pathfindingRoute = pathFinder.calculatePath(gridX, gridY, targetPoint.x, targetPoint.y);
        } catch (InvalidPathFindingException e) {
            System.err.println("Ghost#calculatePath - Failed to calculate path to target: " + e.getMessage());
            e.printStackTrace();
        }
    }

    protected void calculatePath() {
        calculatePath(getTargetGridPosition());
    }

    /**
     * This function is called by the PacmanEntity when it's state has changed; this allows all the ghosts
     * to re-evaluate their status (tracking or fleeing) based on a callback, rather than polling the entity
     * for it's state every tick.
     */
    public void pacmanStateChange(PacmanEntity e) {
        STATE newState = e.getIsVulnerable() ? STATE.TRACKING : STATE.FLEEING;
        if(currentState == STATE.EATEN) {
            // We're currently navigating back to our spawn point; just because the Pacman's state has changed,
            // doesn't mean we are going to stop doing this. Once the ghost has returned home, this new state may
            // take effect.
            nextState = newState;
        } else {
            currentState = newState;
        }
    }

    /**
     * If no path was found for the ghost, or if the ghost is a RANDOM_SEEKER not yet in range of the Pacman,
     * then this method will create a new random point near and adjacent to the Pacman to follow.
     */
    protected void queueRandomTurn() {
        // Search in a 4*4 area for a free space, add all free spaces to an array and pick
        // one at random. The ghost will then path find to this space to completion
        // before re-attempting pathfinding.
        main.Map currentMap = gameInstance.getMapController().getSelectedMap();
        if(currentMap == null) {
            return;
        }

        Point baseLocation = main.Map.getGridBasedPoint(this.x, this.y);
        ArrayList<Point> choices = new ArrayList<>();

        for(int a = -2; a < 2; a++) {
            for(int b = -2; b < 2; b++) {
                int tX = baseLocation.x + a;
                int tY = baseLocation.y + b;

                if(currentMap.isPath(tX, tY) && !(tX == baseLocation.x && tY == baseLocation.y)) {
                    choices.add(new Point(tX, tY));
                }
            }
        }

        if(choices.size() == 0) return;

        Random r = gameInstance.generateRandom();
        calculatePath(choices.get(r.nextInt(choices.size())));
    }

    /**
     * Returns the direction that this ghost must move in order to
     * move closer to the target {@code Point} provided
     *
     * @param target The target we're moving towards
     * @return The direction the ghost must move
     */
    private DIRECTION findDirectionOfTarget(Point target) {
        int targetX = target.x * 16;
        int targetY = target.y * 16;
        if(targetX < this.x) {
            return DIRECTION.LEFT;
        } else if(targetX > this.x) {
            return DIRECTION.RIGHT;
        } else if(targetY < this.y) {
            return DIRECTION.UP;
        } else if(targetY > this.y) {
            return DIRECTION.DOWN;
        } else {
            return DIRECTION.NONE;
        }
    }

    /**
     * Moves the ghost by the distance provided in the direction provided
     *
     * @param d The direction to move in
     * @param distance The distance by which to move
     */
    private void moveGhost(DIRECTION d, int distance) {
        switch (d) {
            case UP -> this.y -= distance;
            case DOWN -> this.y += distance;
            case LEFT -> this.x -= distance;
            case RIGHT -> this.x += distance;
        }
    }

    /**
     * Moves the ghost closer to the current step we're working towards; if we reach a step in
     * this turn, we'll re-calculate our path and request the next step.
     *
     * This is achieved by setting the direction of the ghost to that of the nearest step. If
     * the ghost is in random mode, is out of range, or could not find a path, a random direction
     * will be queued instead.
     */
    private void trackTarget() {
        Point gridBasedPoint = main.Map.getGridBasedPoint(this.x, this.y);
        int gridBasedX = gridBasedPoint.x;
        int gridBasedY = gridBasedPoint.y;
        int gridSize = PacmanGame.GRID_SIZE;

        // We have a path finding route to follow
        if(pathfindingRoute != null){
            // Is there a target we're currently tracking? Keep moving towards
            // this target if so. Changing the target midway can cause the ghost
            // to be diagonal from it's new target point, causing the ghost
            // to fail tracking. Only one axis of movement can be tracked at a time.
            if(nextTarget == null) {
                // There's no current step we're tracing, get the next step
                // Otherwise, we'll keep our current target
                nextTarget = pathfindingRoute.getCurrentStep();
            }

            // We've got a target to track
            if(nextTarget != null) {
                // Check here if we're already at the target - this calculation must be pixel perfect, not grid
                // based because the ghost can exist inside the same grid, but rarely will it be perfectly aligned to it.
                // Failure to be aligned will cause the aforementioned diagonal freeze.
                if(this.x == ( nextTarget.x * PacmanGame.GRID_SIZE ) && this.y == (nextTarget.y * PacmanGame.GRID_SIZE)) {
                    // We're on top of our target, recalculate the path finding and get the second step - the first step
                    // is always going to be our current location and that isn't helpful in this case as we know
                    // we're perfectly aligned already.

                    calculatePath();
                    // Check if we received a valid path from the path finding and walk to the second step
                    if(pathfindingRoute != null) {
                        nextTarget = pathfindingRoute.walkSteps(2);
                    }
                }
            }
        }

        // No path finding route present, may have failed.
        if(pathfindingRoute == null) {
            // The path finding couldn't find a path to our target, give it a dummy target instead.
            queueRandomTurn();
            nextTarget = pathfindingRoute.walkSteps(2);
        }

        // No target to trace, this can be because we're already at our destination, or because
        // it's impossible to path find too, or beyond the max depth of the path finding calculation.
        if(nextTarget == null) return;

        // Given our current direction and velocity, find out if we're going to reach our destination in this
        // single movement.
        int diffX = Math.abs((nextTarget.x*gridSize) - this.x);
        int diffY = Math.abs((nextTarget.y*gridSize) - this.y);
        int targetPixelX = nextTarget.x * gridSize;
        int targetPixelY = nextTarget.y * gridSize;
        this.direction = findDirectionOfTarget(nextTarget);

        if(targetPixelX != this.x && targetPixelY == this.y) {
            // We're heading towards this point/on the correct axis
            moveTowardsStep(diffX);
        } else if(targetPixelY != this.y && targetPixelX == this.x) {
            // We're heading towards this point
            moveTowardsStep(diffY);
        } else {
            System.err.println("Unknown state of movement detected. Direction: " + direction + ", current x/y: " + gridBasedX + " ("+this.x+"), " + gridBasedY + " ("+this.y+"), " + " - target x/y: " + nextTarget.x + "("+targetPixelX+"), " + nextTarget.y + "("+targetPixelY+")");
        }
    }

    /**
     * This method will attempt to move the ghost towards a point. If the point is so close
     * that one movement (by distance velocity) will move past it, this method will
     * redirect that excess distance to the direction specified by the next path finding
     * step.
     *
     * @param diffDistance The amount of distance between current location and target location
     */
    private void moveTowardsStep(int diffDistance) {
        if(velocity <= diffDistance || !pathfindingRoute.hasNextStep()) {
            // We will land on or just before this point. Great!
            moveGhost(direction, velocity);
        } else {
            // We're going to overshoot the point...
            // Move ghost as much as possible; up to the end of this stage
            moveGhost(direction, diffDistance);

            // Calculate excess distance and redirect to new direction
            int excess = velocity - diffDistance;
            Point nextStep = pathfindingRoute.getNextStep();
            DIRECTION newDirection = findDirectionOfTarget(nextStep);

            // Move ghost in new direction with excess momentum
            moveGhost(newDirection, excess);
            this.direction = newDirection;
        }
    }

    /**
     * Handles each tick update delivered to this entity. The ghost will be moved closer to the goal (path
     * finding, or a random goal if the Pacman is out of range). This method will also handle the sprite
     * animations that occur while the ghost is moving.
     *
     * @param dt The amount of time (ms) since last update tick.
     */
    @Override
    public void update(double dt) {
        Point current = main.Map.getGridBasedPoint(this.x, this.y);
        if(currentState == STATE.EATEN && current.x == this.gridSpawnPoint.x && current.y == this.gridSpawnPoint.y) {
            // We were eaten, but we've returned to our spawn point now.
            if(nextState != null) {
                currentState = nextState;
                nextState = null;
            }
        }

        // Based on our current state, swap out the sprites we'll be using when drawing the pacman.
        System.out.println("Current state: " + currentState);
        this.activeSprites = switch(currentState) {
            case FLEEING -> this.fleeingSprites;
            case EATEN -> this.eatenSprites;
            default -> this.sprites;
        };

        trackTarget();
        // Used to delay sprite animations, wait SPRITE_DELAY_TIME before
        // advancing to the next sprite.
        if(System.currentTimeMillis() >= GHOST_SPRITE_TARGET_TIME) {
            currentFrame++;
            if(currentFrame >= activeSprites.get(direction).length) {
                currentFrame = 0;
            }

            GHOST_SPRITE_TARGET_TIME = System.currentTimeMillis() + GHOST_SPRITE_DELAY;
        }
    }

    @Override
    public void paintComponent() {
        Graphics2D graphics = gameInstance.getGameGraphics();

        BufferedImage[] subSprites = activeSprites.get(direction == DIRECTION.NONE ? DIRECTION.RIGHT : direction);
        graphics.drawImage(subSprites[Math.min(subSprites.length - 1, currentFrame)], this.x, this.y, null);
    }

    @Override
    public Rectangle getBounds() {
        return new Rectangle(this.x, this.y, this.width, this.height);
    }

    @Override
    public boolean collidedWithGameBoundary(Rectangle collisionBox, CollisionController.COLLISION_TYPE CollisionType) {
        return false;
    }

    /**
     * A notification method that is executed when this entity is collided with. Test if the source is the
     * {@code PacmanEntity} we're seeking out. If so, notify game of loss condition.
     *
     * @param collisionBox The collision box
     * @param source The source of the collision
     * @param infringedBoundary The boundary infringed by the collision
     * @return Whether or not this collision has been consumed. If true, no more processing of this collision should occur.
     */
    @Override
    public boolean collidedWithBy(Rectangle collisionBox, CollisionElement source, Rectangle infringedBoundary) {
        if(source instanceof PacmanEntity) {
            // Block the processing of a collision if we've been eaten/are returning to spawn
            if(this.currentState == STATE.EATEN) return false;

            // Collided with player, game over.. or if the Pacman is invulnerable; the ghost returns to it's spawn point
            PacmanEntity pacman = (PacmanEntity)source;
            Player player = pacman.getPlayer();
            if(pacman.getIsVulnerable()) {
                int lives = player.reduceLives();

                if (lives <= 0) {
                    gameInstance.playerDeath(player);
                } else {
                    gameInstance.getEntityController().queueSceneReinitialisation();
                }
            } else {
                // This ghost has been eaten.
                currentState = STATE.EATEN;
                player.increaseScore(5000);

                EffectController fx = gameInstance.getEffectsController();
                fx.spawnEffect(new TextFadeEffect(this.x, this.y, new Text("+5000").setSize(10), Color.BLUE, 15));
            }

            return true;
        }

        return false;
    }

    @Override
    public Rectangle isCollisionBoxIntersecting(Rectangle collision, CollisionElement source) {
        Rectangle bounds = getBounds();
        return bounds.intersects(collision) ? bounds : null;
    }
}
