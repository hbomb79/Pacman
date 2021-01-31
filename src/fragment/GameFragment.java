package fragment;

import entity.PacmanEntity;
import entity.pickup.FruitPointPickup;
import main.Player;
import main.PacmanGame;
import org.w3c.dom.css.RGBColor;
import ui.Component;
import ui.Label;
import ui.Text;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * This fragment is used to display the current score of the players
 *
 * @author Harry Felton - 18032692
 */
public class GameFragment extends Fragment {
    /**
     * The label used to display the score of player one
     */
    protected Label playerOneScoreLabel;

    /**
     * The label used to display the name of the current level
     */
    protected Label levelNameLabel;

    /**
     * The amount of time that must pass before the score effect resets
     */
    protected final int scoreEffectTransitionTime = 200;

    /**
     * The color to be used during the score effect
     */
    protected final Color scoreEffectColour = new Color(224, 192, 68);

    /**
     * The color to be used when the score effect is not running
     */
    protected final Color scoreBaseColour = new Color(191, 191, 191);

    private final int[][]   EXTRA_LIFE_SPRITE = {{0,0}};
    private final BufferedImage extraLifeSprite;

    /**
     * Construct the GameFragment and adjusts the sprite used to display
     * extra lives by removing black and replacing it with the color
     * of the wall - this is because the sprite will be drawn on top
     * of a wall and changing the colour allows it to blend in much better
     *
     * @param g The {@code SnakeGame} instance this Fragment is drawing to
     */
    public GameFragment(PacmanGame g) {
        super(g);

        BufferedImage sprite = g.getSpriteController()
                .getSprites(EXTRA_LIFE_SPRITE, 16, 16)[0];

        // Deep copy this sprite as changing the colours below will
        // affect the entire sprite sheet.
        this.extraLifeSprite = new BufferedImage(16, 16, BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D spriteGraphics = this.extraLifeSprite.createGraphics();
        try {
            spriteGraphics.drawImage(sprite, 0, 0, null);
        } finally {
            spriteGraphics.dispose();
        }

        // Find any black and change it to the colour of the wall
        // in this game - this will allow the extra lives
        // images to blend in with the wall they'll be drawn over.
        int blackRGB = new Color(0,0,0).getRGB();
        for(int x = 0; x < 16; x++) {
            for(int y = 0; y < 16; y++) {
                if(this.extraLifeSprite.getRGB(x, y) == blackRGB) {
                    this.extraLifeSprite.setRGB(x, y, Color.BLUE.getRGB());
                }
            }
        }
    }

    /**
     * Creates the components to display the score
     */
    @Override
    public void createComponents() {
        super.createComponents();

        playerOneScoreLabel = new Label(gameInstance, new Text("").setSize(14));
        levelNameLabel = new Label(gameInstance, new Text("").setSize(14));
        components = new Component[] { playerOneScoreLabel, levelNameLabel };
    }

    /**
     * Calculates the color to use for the score label based on how long ago the player scored a point.
     *
     * @param elapsed Amount of time since the player last scored
     * @return Returns the color to be used
     */
    private Color getScoreColour(double elapsed) {
        double ratio = Math.min(1, (elapsed * 1000) / scoreEffectTransitionTime); // Range from 0.0 to 1.0

        int deltaRed = fadeColorComponent(scoreBaseColour.getRed(), scoreEffectColour.getRed(), ratio);
        int deltaGreen = fadeColorComponent(scoreBaseColour.getGreen(), scoreEffectColour.getGreen(), ratio);
        int deltaBlue = fadeColorComponent(scoreBaseColour.getBlue(), scoreEffectColour.getBlue(), ratio);

        return new Color(deltaRed, deltaGreen, deltaBlue);
    }

    /**
     * Update the fragment by updating the score labels and changing the colour of the label when a player recently
     * scored.
     *
     * @param dt Time passed since last update
     */
    @Override
    public void update(double dt) {
        if(!active) return;
        super.update(dt);

        Player player = gameInstance.getPlayer();

        Text playerOneText = playerOneScoreLabel.getText();
        playerOneText.setText(String.format("%04d", player.getScore()));

        playerOneScoreLabel.setX(0).setY(playerOneText.getRenderedHeight() * .8);
        playerOneScoreLabel.setColor(getScoreColour(player.getScoreTime()));

        Text levelNameText = levelNameLabel.getText();
        levelNameText.setText(gameInstance.getMapController().getSelectedMap().getName());
        levelNameLabel.center(true, false, 0, (int)(levelNameText.getRenderedHeight() * 0.8));
    }

    /**
     * Redraws the game fragment components, and draws pacman logos in the top
     * right hand corner to display remaining lives
     */
    @Override
    public void redraw() {
        super.redraw();
        if(active) {
            Graphics2D graphics = gameInstance.getGameGraphics();
            int x = PacmanGame.WIDTH - (16 * 4);
            int lives = gameInstance.getPlayer().getLives();
            for (int i = 0; i < lives; i++) {
                graphics.drawImage(extraLifeSprite, x, 0, null);
                x += 16;
            }

            PacmanEntity pacman = gameInstance.getEntityController().getPlayer();
            if(!pacman.getIsVulnerable()) {
                int fullDuration = FruitPointPickup.INVULNERABILITY_DURATION;
                long timeRemaining = pacman.getInvulnerabilityTimeout() - System.currentTimeMillis();
                double ratio = (timeRemaining * 1.0/fullDuration);


                Rectangle2D rect = new Rectangle2D.Double(1,PacmanGame.HEIGHT-5,ratio * PacmanGame.WIDTH,5);
                graphics.setColor(Color.YELLOW);
                graphics.fill(rect);
            }
        }
    }

    /**
     * Given two colour components, {@code comp1} and {@code comp2}, find a mix between the two based on the
     * {@code ratio}. If {@code ratio} is 0, then the color returned will be all {@code comp1}. If {@code ratio} is 1,
     * then the returned color will be entirely made up of {@code comp2}.
     *
     * @param comp1 First colour component to mix
     * @param comp2 Second colour component to mix
     * @param ratio Double between 0-1 representing the mix of the colours
     * @return Returns the colour calculated
     */
    private int fadeColorComponent(int comp1, int comp2, double ratio) {
        return (int)Math.abs((ratio*comp1) + ((1-ratio) * comp2));
    }
}
