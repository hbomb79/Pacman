package effects;

import interfaces.EffectFrame;
import ui.Text;

import java.awt.*;

/**
 * This {@code Effect} class is used to show a piece of text, and have it slowly fade out and rise out of position
 *
 * @author Harry Felton - 18032692
 */
public class TextFadeEffect extends Effect {
    /**
     * The color of the text at the start of the effect
     */
    protected final Color color;

    /**
     * The text to display
     */
    protected final Text text;

    /**
     * The amount of space the text will rise throughout the effect animation
     */
    protected final int riseAmount;

    /**
     * Instantiates the effect by providing the frames to the superclass.
     *
     * @param x The position of the effect on the X-axis
     * @param y The position of the effect on the Y-axis
     * @param text The text to be displayed during the effect
     * @param color The color of the text at the start of the effect
     * @param riseAmount How far the text will rise (y-axis) during the effect
     * @see #generateFrames(int)
     * @see #provideFrames(EffectFrame[])
     */
    public TextFadeEffect(int x, int y, Text text, Color color, int riseAmount) {
        super(x, y);

        this.color = color;
        this.text = text;
        this.riseAmount = riseAmount;

        provideFrames(generateFrames(10));
    }

    /**
     * Generates the frames for this effect, with each frame being slightly higher and more transparent than the last.
     * The frames will last for 'amount' of game ticks, and at the end should be completely transparent.
     *
     * @param amount The amount of frames to generate
     * @return EffectFrame[] The list of frames generated
     * @see Frame
     */
    private EffectFrame[] generateFrames(int amount) {
        EffectFrame[] frames = new EffectFrame[amount];
        // Get the transparency value of the color. We need to get this to 0, so find out how much we need to reduce it by
        // each frame.
        double alphaReductionPerFrame = (double)color.getAlpha() / amount;
        double yRisePerFrame = (double)riseAmount / amount;
        for(int i = 0; i < amount; i++) {
            Color frameColour = new Color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha() - (int)alphaReductionPerFrame*i);
            frames[i] = new Frame(text, frameColour, (int)yRisePerFrame * i);
        }

        return frames;
    }

    /**
     * This nested class serves to abstract away the information pertaining to each frame of the {@code Effect}
     */
    private static class Frame implements EffectFrame {
        /**
         * The text of the frame
         */
        private final Text text;

        /**
         * The color of the frame
         */
        private final Color color;

        /**
         * The amount of y-axis travel this frame is exhibiting
         */
        private final int yRise;

        /**
         * Instantiates the {@code Frame} instance with the provided information
         * @param text The text to be displayed
         * @param color The color of the text being displayed
         * @param yRise The amount of y-axis travel on the text
         */
        public Frame(Text text, Color color, int yRise) {
            this.text = text;
            this.color = color;
            this.yRise = yRise;
        }

        /**
         * Draws the frame to the screen via the {@code Graphics2D} object {@code graphics} provided. The frame will
         * be drawn at {@code x,y}, with the {@code yRise} adjusting the {@code y} co-ordinate provided
         *
         * @param graphics The graphics instance with which the frame will be drawn to the screen
         * @param x The position of the frame on the x-axis
         * @param y The position of the frame on the y-axis
         */
        @Override
        public void drawFrame(Graphics2D graphics, int x, int y) {
            graphics.setFont(text.getFontInstance());
            graphics.setColor(color);
            graphics.drawString(text.getText(), x, y - yRise);
        }
    }
}
