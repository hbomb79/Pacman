package entity.pickup;

import controllers.EffectController;
import effects.TextFadeEffect;
import entity.Entity;
import entity.PacmanEntity;
import interfaces.CollisionElement;
import main.PacmanGame;
import ui.Text;

import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * The PointPickup exists on the map and when picked up by pacman, will
 * provide the player with points, and play a sound effect.
 */
public class PointPickup extends Pickup {
    protected int pointCircleRadius = 2;
    protected int pointPickupScore = 100;

    public PointPickup(PacmanGame game, int pX, int pY) {
        super(game, pX, pY);
        this.width = 8;
        this.height = 8;
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
            pacman.getPlayer().increaseScore(pointPickupScore);

            EffectController fx = gameInstance.getEffectsController();
            fx.spawnEffect(new TextFadeEffect(this.x, this.y, new Text("+"+this.pointPickupScore).setSize(10 + (this.pointPickupScore/500)), Color.YELLOW, 10));

            gameInstance.playScoreSoundEffect();
        }
    }

    /**
     * Draws the pickup on screen
     */
    @Override
    public void paintComponent() {
        Graphics2D graphics = gameInstance.getGameGraphics();
        graphics.setColor(Color.ORANGE);

        final int diff = (PacmanGame.GRID_SIZE/2);
        graphics.fill(new Ellipse2D.Double(this.x + diff, this.y + diff, pointCircleRadius*2, pointCircleRadius*2));
    }
}
