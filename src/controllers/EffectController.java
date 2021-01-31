package controllers;

import effects.Effect;
import main.PacmanGame;

import java.util.LinkedList;

/**
 * EffectController facilitates the spawning and animation of effects in the game
 *
 * @author Harry Felton - 18032692
 */
public class EffectController extends Controller {
    /**
     * The effects currently being shown
     *
     * @see #redraw()
     * @see #update(double)
     */
    protected LinkedList<Effect> effects = new LinkedList<>();

    /**
     * The effects to be removed after the next tick update
     *
     * @see #removeEffects()
     */
    protected LinkedList<Effect> effectsToRemove = new LinkedList<>();

    /**
     * The effects to spawn before the next tick cycle starts
     *
     * @see #spawnEffects()
     */
    protected LinkedList<Effect> effectsToSpawn = new LinkedList<>();

    /**
     * Instantiates the {@code EffectController} and stores the {@code SnakeGame} instance for use later
     *
     * @param g The SnakeGame singleton instance
     */
    public EffectController(PacmanGame g) {
        super(g);
    }

    /**
     * Queues the {@code effect} instance to be spawned before next update tick
     *
     * @param effect The effect instance to be added
     * @see #update(double)
     */
    public void spawnEffect(Effect effect) {
        effectsToSpawn.add(effect);
    }

    /**
     * Queues the destruction of the {@code effect} instance provided. Will be destroyed after the next update tick
     * has completed
     *
     * @param effect The effect instance to be destroyed
     * @see #update(double)
     */
    public void destroyEffect(Effect effect) {
        effectsToRemove.add(effect);
    }

    /**
     * Removes all {@code Effect} instances queued for destruction
     *
     * @see #effectsToRemove
     * @see #destroyEffect(Effect)
     * @see #update(double)
     */
    private void removeEffects() {
        effects.removeAll(effectsToRemove);
        effectsToRemove.clear();
    }

    /**
     * Spawns all queued {@code Effect} instances
     *
     * @see #effectsToSpawn
     * @see #spawnEffect(Effect)
     * @see #update(double)
     */
    private void spawnEffects() {
        effects.addAll(effectsToSpawn);
        effectsToSpawn.clear();
    }

    /**
     * This method is called at the start of an update tick. First, any queued {@code effects} will be spawned. Then,
     * active effects will be updated to advance the animation. Finally, any queued {@code effects} to be removed, will
     * be removed.
     *
     * @param dt Measure of time since last update
     * @see #spawnEffects()
     * @see #removeEffects()
     */
    public void update(double dt){
        spawnEffects();
        for(Effect effect : effects) {
            effect.update(dt);
        }

        removeEffects();
    }

    /**
     * Requests each registered {@code effect} to redraw itself.
     */
    public void redraw(){
        for(Effect effect : effects) {
            effect.paintComponent();
        }
    }
}
