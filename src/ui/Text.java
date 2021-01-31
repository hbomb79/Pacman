package ui;

import main.PacmanGame;

import java.awt.*;

/**
 * A simple class that allows the encapsulation of text information for easier transport of this information
 * inside of classes. For example, a Button class can simply accept a Text object, rather than two strings and an int.
 *
 * @author Harry Felton - 18032692
 */
public class Text {
    /**
     * The default font to be used when none is specified
     */
    protected static final String   DEFAULT_FONT = "Arial";

    /**
     * The default font size to be used when none is specified
     */
    protected static final int      DEFAULT_SIZE = 20;

    /**
     * The default font style to be used when none is specified
     */
    protected static final int      DEFAULT_STYLE = Font.PLAIN;

    /**
     * The text being stored
     */
    protected String text;

    /**
     * The font to be used when displaying
     */
    protected String font;

    /**
     * The font size to be used when displaying
     */
    protected int size;

    /**
     * The font style to be used when displaying
     */
    protected int style;

    /**
     * Constructs the Text class with the information provided
     * @param t The string to be stored
     * @param f The font to be used
     * @param z The font size to use
     * @param s The font style to use
     */
    public Text(String t, String f, int z, int s) {
        text = t;
        font = f;
        size = z;
        style = s;
    }

    /**
     * Constructs the Text class with the information provided, using default values for the font style
     *
     * @param t The string to be stored
     * @param f The font to be used
     * @param z The font size to use
     */
    public Text(String t, String f, int z) {
        this(t, f, z, DEFAULT_STYLE);
    }

    /**
     * Constructs the Text class with the information provided, using default values for the font style and size
     *
     * @param t The string to be stored
     * @param f The font to be used
     */
    public Text(String t, String f) {
        this(t, f, DEFAULT_SIZE, DEFAULT_STYLE);
    }

    /**
     * Constructs the Text class with the information provided, using default values for the font style, size and typeface.
     *
     * @param t The string to be stored
     */
    public Text(String t) {
        this(t, DEFAULT_FONT, DEFAULT_SIZE, DEFAULT_STYLE);
    }

    /**
     * Set the text being stored
     *
     * @param t The new text value
     * @return Returns the {@code Text} instance to enable method chaining
     */
    public Text setText(String t) {
        text = t;
        return this;
    }

    /**
     * Fetch the text being stored
     *
     * @return Returns the text
     */
    public String getText() {
        return text;
    }

    /**
     * Set the text font to use
     *
     * @param f The new font
     * @return Returns the {@code Text} instance to enable method chaining
     */
    public Text setFont(String f) {
        font = f;
        return this;
    }

    /**
     * Fetch the font typeface being used
     *
     * @return Returns the typeface
     */
    public String getFont() {
        return font;
    }

    /**
     * Set the font size to use
     *
     * @param s The new size
     * @return Returns the {@code Text} instance to enable method chaining
     */
    public Text setSize(int s) {
        size = s;
        return this;
    }

    /**
     * Fetch the font size being used
     *
     * @return Returns the font size
     */
    public int getSize() {
        return size;
    }

    /**
     * Creates a {@code Font} instance containing the typeface, style and size to use when setting the font
     * via the {@code Graphics2D} instance
     *
     * @return Returns the font information using a {@code Font} instance
     */
    public Font getFontInstance() {
        return new Font(font, style, size);
    }

    /**
     * Calculates the width of the text when rendered using the font information provided
     *
     * @return Returns the width of the rendered text
     */
    public int getRenderedWidth() {
        // Get the graphics renderer from our engine
        PacmanGame game = PacmanGame.getGameInstance();
        Graphics g = game.getGameGraphics();

        // Store the old font here for later
        Font oldFont = g.getFont();
        // Set the font used by the renderer to the one specified by this Text instance
        g.setFont(new Font(font, Font.PLAIN, size));
        // Find the width
        int width = g.getFontMetrics().stringWidth(text);
        // Restore the font
        g.setFont(oldFont);

        return width;
    }

    /**
     * Calculates the height of the text when rendered using the font information provided
     *
     * @return Returns the height of the rendered text
     */
    public int getRenderedHeight() {
        // Get the graphics renderer from our engine
        PacmanGame game = PacmanGame.getGameInstance();
        Graphics g = game.getGameGraphics();

        // Store the old font here for later
        Font oldFont = g.getFont();
        // Set the font used by the renderer to the one specified by this Text instance
        g.setFont(new Font(font, Font.PLAIN, size));
        // Find the height
        int height = g.getFontMetrics().getHeight();
        // Restore the font
        g.setFont(oldFont);

        return height;
    }
}
