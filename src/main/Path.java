package main;

import java.awt.*;
import java.util.ArrayList;

/**
 * This class is used to encapsulate the path that an entity must take to reach its destination.
 * Generated by {@code PathFinder} and usually used by {@code GhostEntity}.
 *
 * @author Harry Felton
 */
public class Path {
    /**
     * The steps to follow
     */
    private final ArrayList<Point> steps = new ArrayList<>();

    /**
     * The step currently being followed
     */
    private int currentStep = -1;

    public Path() {}

    /**
     * Adds a step provided to the array, at the start (prepends)
     *
     * @param x The x position of the step
     * @param y The y position of the step
     */
    public void addStep(int x, int y) {
        steps.add(0, new Point(x, y));
    }

    /**
     * Gets the step at the index provided, if the index is valid.
     *
     * @param index The index
     * @return The step at the index provided, or null if the index is invalid/out of bounds
     */
    public Point getStep(int index) {
        if(index > -1 && index < steps.size()) {
            return steps.get(index);
        }

        return null;
    }

    /**
     * Advances the step counter and returns the step at the new index.
     *
     * @return The next step, or null if the step index is invalid
     */
    public Point getNextStep() {
        currentStep++;
        return getStep(currentStep);
    }

    /**
     * Checks if there is a next step to follow
     *
     * @return True if there is a next stop, false otherwise
     */
    public boolean hasNextStep() {
        return (currentStep + 1) < steps.size();
    }

    /**
     * Returns the step currently being followed
     *
     * @return The step
     */
    public Point getCurrentStep() {
        if(currentStep == -1) {
            return getNextStep();
        }

        return getStep(currentStep);
    }

    /**
     * Steps through a certain amount of steps. If the index breaches the bounds of
     * the steps, null is returned instead
     *
     * @param amount The amount of steps to walk through
     * @return The step that is {@code amount} steps ahead of the current step, or null if the step doesn't exist
     */
    public Point walkSteps(int amount) {
        Point step = getStep(currentStep + amount);
        if(step != null) {
            currentStep += amount;
        }

        return step;
    }

    /**
     * Returns the count of steps that exist in this path
     *
     * @return The count of steps
     */
    public int getStepCount() {
        return steps.size();
    }
}
