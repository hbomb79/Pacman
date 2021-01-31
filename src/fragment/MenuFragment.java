package fragment;

import main.PacmanGame;
import ui.Button;
import ui.Component;
import ui.Label;
import ui.Text;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

/**
 * This fragment is used to display the main menu
 *
 * @author Harry Felton - 18032692
 */
public class MenuFragment extends Fragment {
    private final int[][]   PACMAN_SPRITE_LOCATIONS = {{0,0}, {16,0}, {32,0}, {16,0}};
    private final int       SPRITE_DELAY = 100;
    private final int       POINT_COUNT = 5;

    private final BufferedImage[]   pacmanSprites;
    private long                    spriteTargetTime = 0;
    private int                     pacmanSpriteCurrentFrame = 0;
    private final int               pacmanSpriteMaxFrame;

    private int pointPosition = 16;

    /**
     * Constructs the fragment
     * @param game The {@code SnakeGame} the fragment is attached to
     */
    public MenuFragment(PacmanGame game) {
        super(game);

        this.pacmanSprites = game.getSpriteController().getSprites(PACMAN_SPRITE_LOCATIONS, 16, 16);
        this.pacmanSpriteMaxFrame = this.pacmanSprites.length;
    }

    /**
     * Create the components to be displayed
     */
    @Override
    public void createComponents() {
        super.createComponents();

        Text spText = new Text("Start");
        Text quitText = new Text("Quit");

        Runnable startSinglePlayer = () -> gameInstance.startGame();

        components = new Component[]{
                new Label(gameInstance, new Text("Pacman!").setSize(30)).center(true, true, 0, -60).setColor(Color.yellow),
                new Button(gameInstance, spText).center(true, true, 0,-25).setCallback(startSinglePlayer),
                new Button(gameInstance, quitText).center(true, true, 0, 25).setCallback(gameInstance::quitGame)
        };
    }

    /**
     * A modified update function to advance our mini-sprite on the main menu.
     *
     * @param dt Time passed since last update
     */
    @Override
    public void update(double dt) {
        super.update(dt);

        if(active) {
            if (System.currentTimeMillis() >= spriteTargetTime) {
                pacmanSpriteCurrentFrame = pacmanSpriteCurrentFrame == pacmanSpriteMaxFrame - 1 ? 0 : pacmanSpriteCurrentFrame + 1;
                spriteTargetTime = System.currentTimeMillis() + SPRITE_DELAY;
            }

            pointPosition--;
            if(pointPosition <= 8) {
                pointPosition=24;
            }
        }
    }

    /**
     * A modified redraw method which, draws the components (via super), and also our mini sprite we have
     * showing on the menu screen.
     */
    @Override
    public void redraw() {
        super.redraw();

        if(active) {
            Graphics2D g = gameInstance.getGameGraphics();
            int x = -40 + PacmanGame.WIDTH/2;
            g.drawImage(pacmanSprites[pacmanSpriteCurrentFrame], x, 220, null);

            Ellipse2D circle;
            final int radius = 2;
            for(int i = 0; i < POINT_COUNT; i++) {
                g.setColor(Color.orange);

                circle = new Ellipse2D.Double((x + pointPosition + (i*16))-radius, 228-radius, radius*2, radius*2);
                g.fill(circle);
            }
        }
    }
}
