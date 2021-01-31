package controllers;

import main.PacmanGame;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * The SpriteController is responsible for loading and distributing all
 * sprites to their entities.
 *
 * @author Harry Felton - 18032692
 */
public class SpriteController extends Controller {
    private final String SPRITE_PATH = "resources/spritesheet.png";
    private final int SPRITE_STARTING_X = 455;
    private final int SPRITE_STARTING_Y = 0;
    protected BufferedImage spritesheet;


    /**
     * The SpriteController constructor, attempts to load the spritesheet for use later.
     *
     * @param g The PacmanGame instance the controller belongs to
     */
    public SpriteController(PacmanGame g) {
        super(g);
        try {
            this.spritesheet = ImageIO.read(new File(SPRITE_PATH));
        } catch (IOException e) {
            System.err.println("FATAL: Failed to load image for sprites: " + e.getMessage() + " Execution aborted.");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Fetch the sprites defined in the array provided. The images are subbed out of the master spritesheet
     * and the width and height provided is used.
     *
     * @param spritePoints The locations of the sprites
     * @param spriteWidth The width of the sprites
     * @param spriteHeight The height of the sprites
     * @return An array of sub-images representing the entity.
     */
    public BufferedImage[] getSprites(int[][] spritePoints, int spriteWidth, int spriteHeight) {
        int spriteLength = spritePoints.length;
        BufferedImage[] sprites = new BufferedImage[spriteLength];
        for (int i = 0; i < spriteLength; i++) {
            int[] point = spritePoints[i];
            sprites[i] = spritesheet.getSubimage(point[0] + SPRITE_STARTING_X, point[1] + SPRITE_STARTING_Y, spriteWidth, spriteHeight);
        }

        return sprites;
    }
}
