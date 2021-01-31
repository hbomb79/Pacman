package controllers;

import fragment.Fragment;
import main.PacmanGame;

import java.awt.event.MouseEvent;
import java.util.LinkedList;

/**
 * The UIController is responsible for managing and drawing {@code Fragments} on screen
 *
 * @author Harry Felton - 18032692
 */
public class UIController extends Controller {
    /**
     * The fragments currently being handled
     */
    protected LinkedList<Fragment> fragments = new LinkedList<>();

    /**
     * Instantiates the UIController with the {@code SnakeGame} instance
     *
     * @param g The {@code SnakeGame} instance
     */
    public UIController(PacmanGame g) {
        super(g);
    }

    /**
     * Given a {@code Fragment}, registers this fragment to be handled
     *
     * @param f The {@code Fragment} to be registered
     * @return Returns the {@code Fragment} {@code f}, as provided
     * @see #fragments
     */
    public Fragment registerFragment(Fragment f) {
        fragments.add(f);
        return f;
    }

    /**
     * Removes the {@code Fragment} provided from the fragments list
     *
     * @param f The {@code Fragment} to be removed
     * @return Returns the {@code Fragment} {@code f}, as provided
     * @see #fragments
     */
    public Fragment removeFragment(Fragment f) {
        fragments.remove(f);
        return f;
    }

    /**
     * When called, all {@code Fragment} instances registered will be deactivated, meaning no update ticks will be
     * dispatched, and the fragment will not be painted
     *
     * @see #fragments
     * @see #update(double)
     * @see #redraw()
     */
    public void deactivateAllFragments() {
        for(Fragment f : fragments) {
            f.deactivate();
        }
    }

    /**
     * Dispatches an update tick to all {@code Fragment} instances currently registered and active
     *
     * @param dt Time passed since last update
     * @see #fragments
     */
    public void update(double dt) {
        for(Fragment f : fragments) {
            f.update(dt);
        }
    }

    /**
     * Requests a redraw from all {@code Fragment} instances currently registered and active
     */
    public void redraw() {
        for(Fragment f : fragments)
            f.redraw();
    }

    /**
     * Dispatches a MouseEvent representing the depression of the mouse button to all active {@code Fragment} instances
     *
     * @param event The {@code MouseEvent} to be dispatched
     */
    public void mousePressed(MouseEvent event) {
        for (Fragment f : fragments)
            f.mousePressed(event);
    }

    /**
     * Dispatches a MouseEvent representing the release of the mouse button to all active {@code Fragment} instances
     *
     * @param event The {@code MouseEvent} to be dispatched
     */
    public void mouseReleased(MouseEvent event) {
        for (Fragment f : fragments)
            f.mouseReleased(event);
    }

    /**
     * Dispatches a MouseEvent representing the movement of the mouse button to all active {@code Fragment} instances
     *
     * @param event The {@code MouseEvent} to be dispatched
     */
    public void mouseMoved(MouseEvent event) {
        for (Fragment f : fragments)
            f.mouseMoved(event);
    }
}
