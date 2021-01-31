package ui;

import main.PacmanGame;

import java.awt.*;

/**
 * A coloured version of the {@code Component} class, providing storage, setting and getting of the colour
 * used for this component
 *
 * @author Harry Felton - 18032692
 */
public abstract class ColouredComponent extends Component {
    /**
     * The color of this component
     */
    protected Color color;

    /**
     * Constructs this component
     * @param g The {@code SnakeGame} instance this component belongs to
     * @param X The X position of this component
     * @param Y The Y position of this component
     * @param W The width of this component
     * @param H The height of this component
     */
    public ColouredComponent(PacmanGame g, double X, double Y, double W, double H) {
        super(g, X, Y, W, H);
    }

    /**
     * Sets the colour of this component
     *
     * @param c The color to be set
     * @return Returns the component to enable method chaining
     */
    public ColouredComponent setColor(Color c) {
        color = c;
        return this;
    }

    /**
     * Fetches the color of this component
     *
     * @return Returns the color of this component
     */
    public Color getColor() {
        return color;
    }
}
