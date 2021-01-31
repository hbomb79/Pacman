package entity.pickup;

import main.PacmanGame;

/**
 * The same as a normal PointPickup, but gives the user additional points
 * and appears on the map as a larger dot.
 *
 * @author Harry Felton
 */
public class LargePointPickup extends PointPickup {
    public LargePointPickup(PacmanGame game, int pX, int pY) {
        super(game, pX, pY);
        this.pointCircleRadius = 4;
        this.pointPickupScore = 1000;
    }
}
