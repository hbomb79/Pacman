package entity.ghost;

import exception.InvalidPathFindingException;
import main.PacmanGame;
import main.PathFinder;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * The DirectGhostEntity is a ghost that directly follows the
 * Pacman as efficiently as possible - It attempts to find the
 * shortest path to the player.
 *
 * As per the path finding implementation, the ghost may NOT take
 * this most direct path if a ghost already exists on the path,
 * this depends completely on whether or not the cost of an
 * alternative path is shorter.
 *
 * @author Harry Felton
 */
public class DirectGhostEntity extends GhostEntity {
    public DirectGhostEntity(PacmanGame game, int x, int y) {
        super(game, x, y);

        // Define the sprites used for each direction of movement for the ghost.
        // Unlike Pacman, we can't just rotate these sprites.
        Map<DIRECTION, int[][]> sprites = new HashMap<>();
        sprites.put(DIRECTION.RIGHT, new int[][]{
                {0,64},
                {16,64},
        });
        sprites.put(DIRECTION.LEFT, new int[][]{
                {32,64},
                {48,64},
        });
        sprites.put(DIRECTION.UP, new int[][]{
                {64,64},
                {80,64},
        });
        sprites.put(DIRECTION.DOWN, new int[][]{
                {96,64},
                {112,64},
        });
        sprites.put(DIRECTION.NONE, sprites.get(DIRECTION.LEFT));

        // Hand the sprites off to the super class to be loaded via the SpriteController.
        setGhostSprites(sprites);
    }
}
