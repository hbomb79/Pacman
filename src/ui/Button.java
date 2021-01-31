package ui;

import interfaces.UIMouseReactive;
import main.PacmanGame;

import java.awt.*;
import java.awt.event.MouseEvent;

/**
 * The Button Component allows the user to interact with a visual element via mouse movement and clicks
 *
 * @author Harry Felton - 18032692
 */
public class Button extends Component implements UIMouseReactive {
    /**
     * The {@code Text} instance to display
     */
    protected Text text;

    /**
     * The horizontal alignment mode to use
     */
    protected HORIZONTAL_TEXT_ALIGN hAlign = HORIZONTAL_TEXT_ALIGN.CENTER;

    /**
     * The vertical alignment mode to use
     */
    protected VERTICAL_TEXT_ALIGN   vAlign = VERTICAL_TEXT_ALIGN.CENTER;

    /**
     * The default background color of this button
     */
    protected Color bgColour;

    /**
     * The default foreground color of this button
     */
    protected Color fgColour;

    /**
     * The background color of this button when it's being hovered over by the mouse
     */
    protected Color hoveredBgColour;

    /**
     * The foreground color of this button when it's being hovered over by the mouse
     */
    protected Color hoveredFgColour;

    /**
     * The background color of this button when it's being clicked
     */
    protected Color activeBgColour;

    /**
     * The foreground color of this button when it's being clicked
     */
    protected Color activeFgColour;

    /**
     * The amount of padding on all sides of the text
     */
    protected int padding = 10;

    /**
     * The {@code Runnable} instance to run when the button is clicked
     */
    protected Runnable activeCallback;

    /**
     * The available horizontal alignment modes
     */
    protected enum HORIZONTAL_TEXT_ALIGN {
        LEFT,
        CENTER,
        RIGHT
    }

    /**
     * The available vertical alignment modes
     */
    protected enum VERTICAL_TEXT_ALIGN {
        LEFT,
        CENTER,
        RIGHT
    }

    /**
     * The current state of the Button
     */
    protected STATE state = STATE.INACTIVE;

    /**
     * The available button states
     */
    protected enum STATE {
        INACTIVE,
        HOVERED,
        ACTIVE
    }

    /**
     * Constructs a new instance of the {@code Button} class, setting the colours of the
     * button to their default values.
     *
     * @param g The {@code SnakeGame} instance this Button belongs to
     * @param X The X position of this Button
     * @param Y The Y position of this Button
     * @param W The width of this Button
     * @param H The height of this Button
     * @param t The {@code Text} instance to display inside the Button
     */
    public Button(PacmanGame g, double X, double Y, double W, double H, Text t) {
        super(g, X, Y, W, H);
        text = t;

        bgColour = Color.black;
        fgColour = Color.white;
        hoveredBgColour = Color.white;
        hoveredFgColour = Color.white;
        activeBgColour = Color.white;
        activeFgColour = Color.white;
    }

    /**
     * Constructs a new instance of the {@code Button} class, setting the colours of the
     * button to their default values, and setting the width and height to zero for later calculation
     * based on text width/height
     *
     * @param g The {@code SnakeGame} instance this Button belongs to
     * @param X The X position of this Button
     * @param Y The Y position of this Button
     * @param t The {@code Text} instance to display inside the Button
     */
    public Button(PacmanGame g, double X, double Y, Text t) {
        this(g, X, Y, 0, 0, t);
    }

    /**
     * Constructs a new instance of the {@code Button} class, setting the colours of the
     * button to their default values, and setting the width, height, X and Y to zero for later calculation
     * based on text width/height and alignment properties
     *
     * @param g The {@code SnakeGame} instance this Button belongs to
     * @param t The {@code Text} instance to display inside the Button
     */
    public Button(PacmanGame g, Text t) {
        this(g, 0, 0, t);
    }

    /**
     * Called by the {@code UIController} when the mouse is detected to have hovered over
     * this Button. Sets the button to it's HOVERED state.
     *
     * @param event The MouseEvent we're responding to
     */
    @Override
    public void onMouseEnter(MouseEvent event) {
        state = STATE.HOVERED;
    }

    /**
     * Called by the {@code UIController} when the mouse is no longer hovering over this
     * this Button. Sets the Button to it's INACTIVE state.
     *
     * @param event The MouseEvent we're responding to
     */
    @Override
    public void onMouseLeave(MouseEvent event) {
        state = STATE.INACTIVE;
    }

    /**
     * Called by the {@code UIController} when the mouse has depressed on top of this button. Sets the button
     * to it's ACTIVE state.
     *
     * @param event The MouseEvent we're responding to
     * @param within Whether or not this mouse event occurred within the bounds of this Button
     */
    @Override
    public void onMouseDown(MouseEvent event, boolean within) {
        if(!within) {
            state = STATE.INACTIVE;
            return;
        }

        state = STATE.ACTIVE;
    }

    /**
     * Called by the {@code UIController} when the mouse is detected to have been released over this button.
     * If the button is ACTIVE at the time (as in, has been clicked), the callback (if any) is executed.
     *
     * @param event The MouseEvent we're responding to
     * @param within Whether or not this mouse event occurred within the bounds of this Button
     * @see #activeCallback
     */
    @Override
    public void onMouseUp(MouseEvent event, boolean within) {
        // if mouse up within the bounds of the button (might not be, we're told anyway incase the button
        // thinks it's still being pressed; for example when someone clicks, drags the mouse off the button, and then
        // releases), and the button state is active, fire the callback.

        if(within && state == STATE.ACTIVE ) {
            if(activeCallback != null) {
                activeCallback.run();
            }
        }
    }

    /**
     * Determines whether or not this button is currently being hovered over by using the current state of this
     * Button as the means.
     *
     * @return Returns true if this Button is being hovered over, false otherwise.
     */
    @Override
    public boolean isEntered() {
        return state == STATE.HOVERED;
    }

    /**
     * No need to update every tick; we only need to update when a mouse event occurs. This method exists
     * only to satisfy the interface {@code EngineComponent}.
     */
    @Override
    public void update(double dt) {}

    /**
     * Redraws the button using the correct colours based on the state of the Button at the time of drawing.
     *
     * @see #state
     */
    @Override
    public void paintComponent() {
        // Figure out what colours we're using based on the 'state'.
        Color fg, bg;
        switch(state){
            case HOVERED -> {
                fg = hoveredFgColour;
                bg = hoveredBgColour;
            }
            case ACTIVE -> {
                fg = activeFgColour;
                bg = activeBgColour;
            }
            default -> {
                fg = fgColour;
                bg = bgColour;
            }
        }

        // Draw the button
        Graphics2D g = gameInstance.getGameGraphics();
        g.setColor(bg);
        g.drawRect((int)x, (int)y, (int)getWidth(), (int)getHeight());

        g.setColor(fg);
        g.setFont(text.getFontInstance());
        g.drawString( text.getText(), (int)x + padding, (int)y + g.getFontMetrics().getHeight() + (int)(padding*0.5) );
    }

    /**
     * Sets the padding of this Button to the provided integer value
     *
     * @param p The padding to be set
     * @return Returns this Button to enable method chaining
     */
    public Button setPadding( int p ) {
        padding = p;
        return this;
    }

    /**
     * Retrieves the width of this Button by calculating the width of the
     * held text, and adding the padding value to the result
     *
     * @return Returns the effective width of this button
     * @see #padding
     */
    public double getWidth() {
        return text.getRenderedWidth() + (2*padding);
    }

    /**
     * Retrieves the height of this Button by calculating the height of the
     * held text, and adding the padding value to the result
     *
     * @return Returns the effective height of this button
     * @see #padding
     */
    public double getHeight() {
        return text.getRenderedHeight() + (2*padding);
    }

    /**
     * Retrieves the set padding amount of this Button
     *
     * @return Returns the current padding
     * @see #padding
     */
    public int getPadding() {
        return padding;
    }

    /**
     * Sets the background color to use when in INACTIVE state.
     *
     * @param c The new colour
     * @return Returns this button instance to enable method chaining
     * @see #state
     * @see #bgColour
     */
    public Button setBgColour(Color c) {
        bgColour = c;
        return this;
    }

    /**
     * Retrieves the background color to be used when the Button is in it's INACTIVE state.
     *
     * @return Returns the background color used when INACTIVE
     * @see #state
     * @see #bgColour
     */
    public Color getBgColour() {
        return bgColour;
    }

    /**
     * Sets the foreground color to use when in INACTIVE state.
     *
     * @param c The new colour
     * @return Returns this button instance to enable method chaining
     * @see #state
     * @see #fgColour
     */
    public Button setFgColour(Color c) {
        fgColour = c;
        return this;
    }

    /**
     * Retrieves the foreground color to be used when the Button is in it's INACTIVE state.
     *
     * @return Returns the foreground color used when INACTIVE
     * @see #state
     * @see #fgColour
     */
    public Color getFgColour() {
        return fgColour;
    }

    /**
     * Sets the background color to use when in HOVERED state.
     *
     * @param c The new colour
     * @return Returns this button instance to enable method chaining
     * @see #state
     * @see #hoveredBgColour
     */
    public Button setHoveredBgColour(Color c) {
        hoveredBgColour = c;
        return this;
    }

    /**
     * Retrieves the background color to be used when the Button is in it's HOVERED state.
     *
     * @return Returns the background color used when HOVERED
     * @see #state
     * @see #hoveredBgColour
     */
    public Color getHoveredBgColour() {
        return hoveredBgColour;
    }

    /**
     * Sets the foreground color to use when in HOVERED state.
     *
     * @param c The new colour
     * @return Returns this button instance to enable method chaining
     * @see #state
     * @see #hoveredFgColour
     */
    public Button setHoveredFgColour(Color c) {
        hoveredFgColour = c;
        return this;
    }

    /**
     * Retrieves the foreground color to be used when the Button is in it's HOVERED state.
     *
     * @return Returns the foreground color used when HOVERED
     * @see #state
     * @see #hoveredFgColour
     */
    public Color getHoveredFgColour() {
        return hoveredFgColour;
    }

    /**
     * Sets the background color to use when in ACTIVE state.
     *
     * @param c The new colour
     * @return Returns this button instance to enable method chaining
     * @see #state
     * @see #activeBgColour
     */
    public Button setActiveBgColour(Color c) {
        activeBgColour = c;
        return this;
    }

    /**
     * Retrieves the background color to be used when the Button is in it's ACTIVE state.
     *
     * @return Returns the background color used when ACTIVE
     * @see #state
     * @see #activeBgColour
     */
    public Color getActiveBgColour() {
        return activeBgColour;
    }

    /**
     * Sets the foreground color to use when in ACTIVE state.
     *
     * @param c The new colour
     * @return Returns this button instance to enable method chaining
     * @see #state
     * @see #activeFgColour
     */
    public Button setActiveFgColour(Color c) {
        activeFgColour = c;
        return this;
    }

    /**
     * Retrieves the foreground color to be used when the Button is in it's ACTIVE state.
     *
     * @return Returns the foreground color used when ACTIVE
     * @see #state
     * @see #activeFgColour
     */
    public Color getActiveFgColour() {
        return activeFgColour;
    }

    /**
     * Sets the callback to use when the Button is clicked
     *
     * @param r The {@code Runnable} instance to run on click
     * @return Returns this Button instance to enable method chaning
     */
    public Button setCallback( Runnable r ) {
        activeCallback = r;
        return this;
    }

    /**
     * Fetches the {@code Runnable} instance supplied as the callback
     *
     * @return Returns the callback
     */
    public Runnable getCallback() {
        return activeCallback;
    }

    /**
     * A proxy method of {@code Component::center} used to avoid having to cast {@code Component} to {@code Button}
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
     * @return Returns the Button instance, to enable method chaining
     */
    @Override
    public Button center(boolean horizontal, boolean vertical, int xOffset, int yOffset) {
        return (Button)super.center(horizontal, vertical, xOffset, yOffset);
    }
}
