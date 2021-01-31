package ui;

import main.PacmanGame;

import java.awt.*;
import java.awt.geom.Rectangle2D;

/**
 * The Panel class is used to display a solid, rectangular block of colour; or, the outline of said block.
 *
 * @author Harry Felton - 18032692
 */
public class Panel extends ColouredComponent {
    /**
     * Whether or not the Panel, when drawn, should be filled in. If false, only an outline is drawn.
     */
    boolean filled = true;

    /**
     * Constructs the instance of this panel
     *
     * @param g The {@code SnakeGame} instance this panel belongs to
     * @param X The X position of the panel
     * @param Y The Y position of the panel
     * @param W The width of the panel
     * @param H The height of the panel
     */
    public Panel(PacmanGame g, double X, double Y, double W, double H) {
        super(g, X, Y, W, H);
    }

    /**
     * Fired each update tick; no work needs to be done so this method is left empty.
     *
     * @param dt Time passed since last update
     */
    @Override
    public void update(double dt) {}

    /**
     * Draws the panel, either as a solid filled block, or an outline.
     *
     * @see #filled
     */
    @Override
    public void paintComponent() {
        Graphics2D g = gameInstance.getGameGraphics();

        Rectangle2D rect = new Rectangle2D.Double(x, y, width, height);
        g.setColor(color);
        if(filled) {
            g.fill(rect);
        } else {
            g.draw(rect);
        }
    }
}
