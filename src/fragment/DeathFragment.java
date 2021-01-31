package fragment;

import main.Player;
import main.PacmanGame;
import main.Scoreboard;
import ui.Button;
import ui.Component;
import ui.Label;
import ui.Text;

import java.awt.*;
import java.util.ArrayList;

/**
 * The DeathFragment is displayed when the game has ended. It shows the winning player and their score.
 *
 * @author Harry Felton - 18032692
 */
public class DeathFragment extends Fragment {
    /**
     * The text displaying the information of the winning player
     */
    protected Text gameResultText;

    /**
     * The label responsible for displaying the information of the winning player
     */
    protected Label gameResultLabel;

    /**
     * Constructs the Fragment
     *
     * @param game The game instance the fragment is attached to
     */
    public DeathFragment(PacmanGame game) {
        super(game);
    }

    /**
     * Creates the components for display when the fragment becomes active
     */
    @Override
    public void createComponents() {
        super.createComponents();

        Text menuText = new Text("Menu");
        Text quitText = new Text("Quit");
        Text gameOverText = new Text("Game Over");
        gameResultText = new Text("");
        gameResultLabel = new Label(gameInstance, gameResultText);

        Runnable backToMenu = () -> gameInstance.scheduleGameStateChange(PacmanGame.STATE.MENU);

        components = new Component[]{
                new Label(gameInstance, gameOverText).center(true, true, 0, -80),
                gameResultLabel,
                new Button(gameInstance, menuText).center(true, true, -50, 110).setCallback(backToMenu),
                new Button(gameInstance, quitText).center(true, true, 50, 110).setCallback(gameInstance::quitGame)
        };
    }

    /**
     * Updates the fragment to display new information about the winning players
     *
     * @param dt Time since the last update occurred
     */
    @Override
    public void update(double dt) {
        if(!active) return;
        super.update(dt);

        Player player = gameInstance.getPlayer();
        gameResultText.setText("You got " + player.getScore() + " score!");
        gameResultLabel.center(true, true, 0, -55);
    }

    /**
     * Draws the components for this fragment, and also displays the scoreboard
     * of top scores achieved
     */
    @Override
    public void redraw() {
        super.redraw();
        if(!active) return;

        Scoreboard scoreboard = gameInstance.getScoreboard();
        ArrayList<Integer> scores = scoreboard.getTopScores();

        Graphics2D graphics = gameInstance.getGameGraphics();

        int ourScore = gameInstance.getPlayer().getScore();
        int availableScores = scores.size();

        int drawX = 100;
        int drawY = 125;

        graphics.setFont(new Font("Arial", Font.PLAIN, 16));
        for (int i = 0; i < scoreboard.getTopScoreLimit(); i++) {
            int score = i < availableScores ? scores.get(i) : 0;
            graphics.setColor(score == 0 ? Color.GRAY : (score == ourScore ? Color.GREEN : Color.YELLOW));
            graphics.drawString((i + 1) + ". " + (score == 0 ? "<EMPTY>" : score), drawX, drawY + (i * 20));
        }
    }
}
