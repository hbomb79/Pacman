package fragment;

import interfaces.UIMouseReactive;
import main.PacmanGame;
import ui.Component;

import java.awt.event.MouseEvent;

/**
 * Abstracted functionality for game fragments is kept here
 *
 * @author Harry Felton - 18032692
 */
public abstract class Fragment {
    /**
     * The game instance this Fragment belongs to
     */
    protected PacmanGame gameInstance;

    /**
     * Represents whether or not the fragment is currently active
     */
    protected boolean active;

    /**
     * The collection of components that the fragment is intending to display
     */
    protected Component[] components;

    /**
     * Construct the Fragment and request the components
     *
     * @param g The {@code SnakeGame} the fragment belongs to
     */
    public Fragment(PacmanGame g) {
        gameInstance = g;
        createComponents();
    }

    /**
     * Create a default empty-set of components to ensure the class fields are fully initialised
     */
    public void createComponents() {
        components = new Component[0];
    }

    /**
     * Activates the fragment to allow update and draw requests to reach the components
     *
     * @return Returns the same fragment to enable chaining
     */
    public Fragment activate() {
        active = true;
        return this;
    }

    /**
     * Deactivates the fragment to block update and draw requests from reaching the components
     *
     * @return Returns the same fragment to enable chaining
     */
    public Fragment deactivate() {
        active = false;
        return this;
    }

    /**
     * Updates the components registered
     *
     * @param dt Time passed since last update
     */
    public void update(double dt) {
        if(!active) return;
        for(Component c : components) {
            c.update(dt);
        }
    }

    /**
     * Redraws the components registered
     */
    public void redraw() {
        if(!active) return;
        for(Component c : components) {
            c.paintComponent();
        }
    }

    /**
     * Checks if the mouse event provided has landed within the component provided
     *
     * @param c The component to be tested against the event
     * @param event The mouse event to being checked
     * @return Returns true if the mouse event landed inside the components, false otherwise
     */
    private boolean checkEventWithinComponent(Component c, MouseEvent event) {
        int elementX, elementY, eventX, eventY;
        elementX = (int)c.getX();
        elementY = (int)c.getY();
        eventX = event.getX();
        eventY = event.getY();
        return (eventX > elementX && eventX < elementX + c.getWidth()) && (eventY > elementY && eventY < elementY + c.getHeight());
    }

    /**
     * Dispatches the mouse event to the components
     *
     * @param event The {@code MouseEvent} to be dispatched
     */
    public void mousePressed(MouseEvent event) {
        if(!active) return;
        for (Component e : components) {
            // Check the element wants to receive mouse events (does implement UIMouseReactive?)
            if(e instanceof UIMouseReactive) {
                ((UIMouseReactive) e).onMouseDown(event, checkEventWithinComponent(e, event));
            }
        }
    }

    /**
     * Dispatches the mouse event to the components
     *
     * @param event The {@code MouseEvent} to be dispatched
     */
    public void mouseReleased(MouseEvent event) {
        if(!active) return;
        for (Component e : components) {
            if(e instanceof UIMouseReactive) {
                ((UIMouseReactive) e).onMouseUp(event, checkEventWithinComponent(e, event));
            }
        }
    }

    /**
     * Dispatches the mouse event to the components
     *
     * @param event The {@code MouseEvent} to be dispatched
     */
    public void mouseMoved(MouseEvent event) {
        if(!active) return;
        for (Component e : components) {
            if(e instanceof UIMouseReactive) {
                boolean isWithin = checkEventWithinComponent(e, event);
                if(isWithin && !((UIMouseReactive) e).isEntered()) {
                    ((UIMouseReactive) e).onMouseEnter(event);
                } else if(!isWithin && ((UIMouseReactive) e).isEntered()) {
                    ((UIMouseReactive) e).onMouseLeave(event);
                }
            }
        }
    }
}
