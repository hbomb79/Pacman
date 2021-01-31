package fragment;

import main.PacmanGame;
import ui.Component;
import ui.Label;
import ui.Panel;
import ui.Text;

import java.awt.*;

/**
 * This fragment is used to display a pause overlay when the user hits the 'ESCAPE' key.
 *
 * @author Harry Felton - 18032692
 */
public class PauseFragment extends Fragment {
    /**
     * Constructs the PauseFragment
     * @param game The {@code SnakeGame} the fragment is attached to
     */
    public PauseFragment(PacmanGame game) {
        super(game);
    }

    /**
     * Create the components to be displayed
     */
    @Override
    public void createComponents() {
        Text pauseText = new Text("Game Paused");

        components = new Component[]{
                new Panel(gameInstance, 0, 0, PacmanGame.WIDTH, PacmanGame.HEIGHT).setColor(new Color(0x9E649764, true)),
                new Label(gameInstance, pauseText).center(true, true, 0, -100).setColor(Color.green)
        };
    }
}
