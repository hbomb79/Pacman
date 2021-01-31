package entity.ghost;

import entity.PacmanEntity;
import exception.InvalidPathFindingException;
import main.PacmanGame;
import main.PathFinder;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * The FlankGhostEntity is a ghost that attempts to cut off
 * Pacman - It attempts to path find efficiently to a location
 * infront of the pacman.
 *
 * @author Harry Felton
 */
public class FlankGhostEntity extends GhostEntity {
    protected int FLANK_DISTANCE = 5;
    protected int LOCK_DISTANCE = 8;

    public FlankGhostEntity(PacmanGame game, int x, int y) {
        super(game, x, y);

        // Define the sprites used for each direction of movement for the ghost.
        // Unlike Pacman, we can't just rotate these sprites.
        Map<DIRECTION, int[][]> sprites = retrieveGhostSprites();

        // Hand the sprites off to the super class to be loaded via the SpriteController.
        setGhostSprites(sprites);
    }

    /**
     * Returns a HashMap of the sprites for the ghost to use based on
     * it's direction
     *
     * @return The map of sprite locations
     */
    protected Map<DIRECTION, int[][]> retrieveGhostSprites() {
        Map<DIRECTION, int[][]> sprites = new HashMap<>();
        sprites.put(DIRECTION.RIGHT, new int[][]{
                {0,80},
                {16,80},
        });
        sprites.put(DIRECTION.LEFT, new int[][]{
                {32,80},
                {48,80},
        });
        sprites.put(DIRECTION.UP, new int[][]{
                {64,80},
                {80,80},
        });
        sprites.put(DIRECTION.DOWN, new int[][]{
                {96,80},
                {112,80},
        });
        sprites.put(DIRECTION.NONE, sprites.get(DIRECTION.LEFT));

        return sprites;
    }

    /**
     * This method is used when finding a flank point to determine if the Pacman is close
     * enough to directly lock on to.
     *
     * @param pacmanGridLocation The grid based location of the pacman
     * @return The distance between this ghost and the pacman location provided.
     */
    private float getDistance(Point pacmanGridLocation) {
        Point ghostGridLocation = main.Map.getGridBasedPoint(this.x, this.y);
        int dX = Math.abs(pacmanGridLocation.x - ghostGridLocation.x);
        int dY = Math.abs(pacmanGridLocation.y - ghostGridLocation.y);
        return dX + dY;
    }

    /**
     * This method will return a point that flanks the Pacman's movement - or,
     * will return the pacmans location if the ghost is close enough to the
     * player. The distance used to detect this is {@code LOCK_DISTANCE}
     *
     * @return Returns the point that this ghost should navigate to
     */
    private Point findFlankPoint() {
        PacmanEntity pacman = gameInstance.getEntityController().getPlayer();
        Point baseLocation = main.Map.getGridBasedPoint(pacman.getX(), pacman.getY());
        if(getDistance(baseLocation) < LOCK_DISTANCE) {
            return baseLocation;
        }

        main.Map currentMap = gameInstance.getMapController().getSelectedMap();
        return currentMap.calculateFlankRoute(baseLocation, FLANK_DISTANCE, pacman.getDirection());
    }

    /**
     * Gets the position of the Pacman entity, finds a point infront of it, and path finds to it.
     *
     * If the ghost is within a certain range of the pacman, it will seek directly for it.
     */
    @Override
    protected void calculatePath() {
        super.calculatePath(findFlankPoint());
    }
}
