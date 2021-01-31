package ui;

import main.PacmanGame;

import java.awt.*;

/**
 * The Label class is used to display a single line of text on the game screen
 *
 * @author Harry Felton - 18032692
 */
public class Label extends ColouredComponent {
    /**
     * The {@code Text} instance to be displayed
     */
    protected Text text;

    /**
     * Construct a new instance of the {@code Label} class, setting the width and height of the underlying
     * Component to 0 as they're dynamically generated based on the width and height of the {@code text}.
     *
     * @param g The {@code SnakeGame} instance this Label belongs to
     * @param X The X position of the Label
     * @param Y The Y position of the Label
     * @param t The text to display
     */
    public Label(PacmanGame g, double X, double Y, Text t) {
        super(g, X, Y, 0, 0);
        text = t;
    }

    /**
     * Construct a new instance of the {@code Label} class, setting the width and height of the underlying
     * Component to 0 as they're dynamically generated based on the width and height of the {@code text}.
     *
     * Also, sets the X and Y position to 0 for setting later (potentially via {@code Label::center()}.
     *
     * @param g The {@code SnakeGame} instance this Label belongs to
     * @param t The text to display
     * @see #center(boolean, boolean, int, int)
     */
    public Label(PacmanGame g, Text t) {
        this(g, 0, 0, t);
    }

    /**
     * Fired each update tick; this component requires no updates so this method is ignored
     * @param dt Time passed since last update
     */
    @Override
    public void update(double dt) {}

    /**
     * Draws this label to the screen
     */
    @Override
    public void paintComponent() {
        Graphics2D g = gameInstance.getGameGraphics();

        g.setFont(text.getFontInstance());
        g.setColor(color);
        g.drawString(text.getText(), (int)x, (int)y);
    }

    /**
     * Fetches the effective width of the Label class by finding the rendered width of the underlying Text
     *
     * @return Returns the width of the Label
     */
    @Override
    public double getWidth() {
        return text.getRenderedWidth();
    }

    /**
     * Fetches the effective height of the Label class by finding the rendered height of the underlying Text
     *
     * @return Returns the height of the Label
     */
    @Override
    public double getHeight() {
        return text.getRenderedHeight();
    }

    /**
     * Returns the underlying {@code Text} instance that this Label intends to display.
     *
     * @return Returns the {@code Text} instance
     */
    public Text getText() {
        return text;
    }

    /**
     * A proxy method of {@code Component::center} used to avoid having to cast {@code Component} to {@code Label}
     * every time. Using Generics would invalidate the type-safe nature of the class hierarchy, so this approach was used
     * instead.
     *
     * Centers the text via the methods enabled. Will center horizontally only if {@code horizontal} param is true. Likewise
     * with {@code vertical}. The {@code xOffset} and {@code yOffset} will be applied to the X and Y position after centering
     * has completed.
     *
     * @param horizontal If true, centers the component horizontally
     * @param vertical If true, centers the component vertically
     * @param xOffset The offset applied to the X position
     * @param yOffset The offset applied to the Y position
     * @return Returns the Label instance, to enable method chaining
     */
    @Override
    public Label center(boolean horizontal, boolean vertical, int xOffset, int yOffset) {
        return (Label)super.center(horizontal, vertical, xOffset, yOffset);
    }
}
