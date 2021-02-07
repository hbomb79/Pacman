package entity.pickup;

import controllers.EffectController;
import controllers.SpriteController;
import effects.TextFadeEffect;
import entity.Entity;
import entity.PacmanEntity;
import interfaces.CollisionElement;
import main.PacmanGame;
import main.Player;
import ui.Text;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * The FruitPointPickup exists on the map and when picked up by pacman, will
 * provide the player with points, and increase the players extra lives if
 * any are missing (max. 3).
 *
 * @author Harry Felton
 */
public class FruitPointPickup extends Pickup {
    private enum PICKUP_TYPE {
        LIFE,
        INVULNERABLE
    }

    public static final int INVULNERABILITY_DURATION = 3000;

    protected int pointPickupScore = 500;

    protected final int[][]         FRUIT_SPRITES = {{32,48}, {48,48}, {64,48}, {80,48}, {96, 48}, {112, 48}};
    protected final BufferedImage   sprite;

    /**
     * Constructs the fruit pickup by choosing a random fruit sprite and using that when drawing
     * the pickup
     *
     * @param game The game instance this pickup belongs to
     * @param pX The X position of the pickup
     * @param pY The Y position of the pickup
     */
    public FruitPointPickup(PacmanGame game, int pX, int pY) {
        super(game, pX, pY);

        Random r = game.generateRandom();
        int spriteIndex = r.nextInt(FRUIT_SPRITES.length);

        SpriteController spriteController = game.getSpriteController();
        BufferedImage[] sprites = spriteController.getSprites(new int[][]{FRUIT_SPRITES[spriteIndex]}, 16, 16);
        this.sprite = sprites[0];
    }

    /**
     * This method will be executed when another entity intersects with this pickup. If intersected
     * with by a {@code PacmanEntity}, the pickup will apply the effect (add score, destroy self, spawn text effects)
     *
     * @param collisionBox The collision box
     * @param source The source of the collision
     * @param infringedBoundary The boundary infringed by the collision
     * @return Returns false as to not consume the collision event
     * @see #applyEffect(Entity)
     */
    @Override
    public boolean collidedWithBy(Rectangle collisionBox, CollisionElement source, Rectangle infringedBoundary) {
        if(source instanceof PacmanEntity) {
            applyEffect((PacmanEntity)source);
        }
        return false;
    }

    /**
     * Applies this pickup effect to the {@code PacmanEntity}, if the entity provided is a {@code PacmanEntity}.
     *
     * @param e The entity the effect is being applied to
     */
    @Override
    public void applyEffect(Entity e) {
        super.applyEffect(e);

        if(e instanceof PacmanEntity) {
            PacmanEntity pacman = (PacmanEntity) e;
            Player player = pacman.getPlayer();
            player.increaseScore(pointPickupScore);

            pacman.makeInvulnerable(INVULNERABILITY_DURATION);
            gameInstance.fruitSoundEffect.playOnce(gameInstance.SOUND_EFFECT_VOLUME);

            EffectController fx = gameInstance.getEffectsController();
            fx.spawnEffect(new TextFadeEffect(this.x, this.y, new Text("+"+this.pointPickupScore).setSize(10 + (this.pointPickupScore/100)), Color.YELLOW, 10));

            gameInstance.playScoreSoundEffect();
        }
    }

    /**
     * Draws the pickup on screen
     */
    @Override
    public void paintComponent() {
        Graphics2D g = gameInstance.getGameGraphics();
        g.drawImage(sprite, this.x, this.y, null);
    }
}
