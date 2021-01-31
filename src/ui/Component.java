package ui;

import interfaces.EngineComponent;
import main.PacmanGame;

/**
 * The core component class, used to abstract common information from
 * UI elements, such as position and size.
 *
 * @author Harry Felton - 18032692
 */
public abstract class Component implements EngineComponent {
    /**
     * The {@code SnakeGame} instance the component belongs to
     */
    protected PacmanGame gameInstance;

    /**
     * The position of the component on the X-axis
     */
    protected double x;

    /**
     * The position of the component on the Y-axis
     */
    protected double y;

    /**
     * The width of the component
     */
    protected double width;

    /**
     * The height of the component
     */
    protected double height;

    /**
     * Construct an instance of the Component class, initialising with all provided information
     *
     * @param g The {@code SnakeGame} instance the component belongs to
     * @param X The X position of the component
     * @param Y The Y position of the component
     * @param W The width of this component
     * @param H The height of this component
     */
    public Component(PacmanGame g, double X, double Y, double W, double H) {
        gameInstance = g;
        x = X;
        y = Y;
        width = W;
        height = H;
    }

    /**
     * Set the X position of the component
     *
     * @param a The new X position
     * @return Returns this component to enable method chaining
     */
    public Component setX( double a ) {
        x = a;
        return this;
    }

    /**
     * Fetch the X position of the component
     *
     * @return Returns the X position
     */
    public double getX() {
        return x;
    }

    /**
     * Set the Y position of the component
     *
     * @param b The new Y position
     * @return Returns this component to enable method chaining
     */
    public Component setY( double b ) {
        y = b;
        return this;
    }

    /**
     * Fetch the Y position of the component
     *
     * @return Returns the Y position
     */
    public double getY() {
        return y;
    }

    /**
     * Set the width of the component
     *
     * @param a The new width
     * @return Returns this component to enable method chaining
     */
    public Component setWidth( double a ) {
        width = a;
        return this;
    }

    /**
     * Fetch the width of the component
     *
     * @return Returns the width
     */
    public double getWidth() {
        return width;
    }

    /**
     * Set the height of the component
     *
     * @param b The new height
     * @return Returns this component to enable method chaining
     */
    public Component setHeight( double b ) {
        height = b;
        return this;
    }

    /**
     * Fetch the height of the component
     *
     * @return Returns the height
     */
    public double getHeight() {
        return height;
    }

    /**
     * Centers the component either horizontally, vertically, or both. Offsets the position of
     * the component using the offsets provided
     *
     * @param horizontal If true, centers the component horizontally
     * @param vertical If true, centers the component vertically
     * @param xOffset The offset applied to the X position
     * @param yOffset The offset applied to the Y position
     * @return Returns the component to enable method chaining
     */
    public Component center(boolean horizontal, boolean vertical, int xOffset, int yOffset) {
        int frameWidth = PacmanGame.WIDTH;
        int frameHeight = PacmanGame.HEIGHT;
        double dX = xOffset;
        double dY = yOffset;

        // This function assumes the frame of reference is the entire game window, and thus the max width and height
        // is sourced straight from the game instance.
        if( horizontal ) {
            dX = dX + (frameWidth / 2.0) - (getWidth() / 2.0);
        }

        if( vertical ) {
            dY = dY + (frameHeight / 2.0) - (getHeight() / 2.0);
        }

        this.x = dX;
        this.y = dY;

        return this;
    }
}
