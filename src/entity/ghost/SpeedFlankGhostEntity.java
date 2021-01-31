package entity.ghost;

import main.PacmanGame;

import java.util.HashMap;
import java.util.Map;

/**
 * The SpeedFlankGhostEntity is functions exactly the same
 * as the FlankGhostEntity, however this ghost has the same movement speed
 * as the player, proving to be a very difficult opponent indeed. In fact,
 * I had to disable the third ghost just to pass the level to test
 * the end win screen...
 *
 * @author Harry Felton
 */
public class SpeedFlankGhostEntity extends FlankGhostEntity {
    public SpeedFlankGhostEntity(PacmanGame game, int x, int y) {
        super(game, x, y);
        this.velocity = 2;
    }

    /**
     * Returns a HashMap of the sprites for the ghost to use based on
     * it's direction
     *
     * @return The map of sprite locations
     */
    @Override
    protected Map<DIRECTION, int[][]> retrieveGhostSprites() {
        Map<DIRECTION, int[][]> sprites = new HashMap<>();
        sprites.put(DIRECTION.RIGHT, new int[][]{
                {0,96},
                {16,96},
        });
        sprites.put(DIRECTION.LEFT, new int[][]{
                {32,96},
                {48,96},
        });
        sprites.put(DIRECTION.UP, new int[][]{
                {64,96},
                {80,96},
        });
        sprites.put(DIRECTION.DOWN, new int[][]{
                {96,96},
                {112,96},
        });
        sprites.put(DIRECTION.NONE, sprites.get(DIRECTION.LEFT));

        return sprites;
    }
}
