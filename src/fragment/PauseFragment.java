package fragment;

import main.PacmanGame;
import ui.Component;
import ui.Button;
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
        Button menuButton = new Button(gameInstance, new Text("Main Menu").setSize(15)).center(true, true, 0, -20);
        Button resumeButton = new Button(gameInstance, new Text("Resume").setSize(15)).center(true, true, 0, 30);

        Runnable returnToMenu = () -> {
            gameInstance.togglePause();
            gameInstance.scheduleGameStateChange(PacmanGame.STATE.MENU);
        };

        components = new Component[]{
                new Panel(gameInstance, 0, 0, PacmanGame.WIDTH, PacmanGame.HEIGHT).setColor(new Color(0x9E649764, true)),
                new Label(gameInstance, pauseText).center(true, true, 0, -100).setColor(Color.green),
                menuButton.setCallback(returnToMenu),
                resumeButton.setCallback(gameInstance::togglePause)
        };
    }
}
