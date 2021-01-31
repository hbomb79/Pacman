package effects;

import controllers.EffectController;
import interfaces.EffectFrame;
import interfaces.EngineComponent;
import main.PacmanGame;

import java.util.Arrays;
import java.util.LinkedList;

/**
 * The Effect class is responsible for animating effects inside the game window
 *
 * @author Harry Felton - 18032692
 */
public abstract class Effect implements EngineComponent {
    /**
     * The current frame index to be displayed
     */
    protected int frame = 0;

    /**
     * The total amount of frames to be displayed to complete this effect
     */
    protected int endFrame;

    /**
     * The frames provided to this {@code Effect}. The frame at {@code frame} will be displayed
     */
    protected final LinkedList<EffectFrame> frames = new LinkedList<>();

    /**
     * The x position of the {@code Effect}
     */
    protected final int x;

    /**
     * The x position of the {@code Effect}
     */
    protected final int y;

    /**
     * The {@code SnakeGame} instance this {@code Effect} is attached to
     */
    protected final PacmanGame gameInstance;

    /**
     * The {@code SnakeGame} {@code EffectController}, stored here to reduce calls to {@code getEffectsController()}
     */
    protected final EffectController fx;

    /**
     * Instantiate the {@code Effect} with the position provided. The {@code SnakeGame} instance is retrieved via the
     * static {@code SnakeGame.getGameInstance()}.
     *
     * @param x The position of the effect on the X-axis
     * @param y The position of the effect on the Y-axis
     */
    protected Effect(int x, int y) {
        this.x = x;
        this.y = y;

        this.gameInstance = PacmanGame.getGameInstance();
        this.fx = gameInstance.getEffectsController();
    }

    /**
     * This method is used to provide the {@code EffectFrame} instances. These frames will then be displayed based on
     * the {@code frame} selected
     *
     * @param frames The list of {@code EffectFrames} to be added to the frames list
     * @see #frames
     * @see #frame
     */
    protected void provideFrames(EffectFrame[] frames) {
        this.frames.addAll(Arrays.asList(frames));
        endFrame = this.frames.size();
    }

    /**
     * Called on each update tick, advances the {@code Effect} to the next frame.
     * If no frames left, the {@code Effect} is destroyed
     *
     * @param dt The time passed since the last update
     */
    @Override
    public void update(double dt) {
        frame++;
        if(frame >= endFrame)
            destroyEffect();
    }

    /**
     * Paints the {@code Effect} by displaying the {@code EffectFrame} currently selected by {@code frame}
     *
     * @see #frame
     * @see #frames
     */
    @Override
    public void paintComponent() {
        if(frame < endFrame) frames.get(frame).drawFrame(gameInstance.getGameGraphics(), x, y);
    }

    /**
     * Destroy this effect by queueing it's removal via the {@code EffectController}
     *
     * @see controllers.EntityController
     */
    private void destroyEffect() {
        fx.destroyEffect(this);
    }
}
