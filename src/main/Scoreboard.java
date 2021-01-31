package main;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;

/**
 * The Scoreboard class is used to store the top 5 scores
 * achieved in the game.
 *
 * This class is serializable and is saved/loaded from file when
 * the scores are updated, or the game starts. These scores are
 * displayed on the game over screen, and allows you to see how
 * your score compares to others.
 *
 * @author Harry Felton
 * @see fragment.DeathFragment
 */
public class Scoreboard implements Serializable {
    private static final String SCOREBOARD_PATH = ".scoreboard";

    private final int TOP_SCORES_TO_SHOW = 5;
    private final ArrayList<Integer> topScores = new ArrayList<>();

    /**
     * Save this instance of Scoreboard to the file specified
     * in {@code SCOREBOARD_PATH}
     */
    public void save() {
        Scoreboard.saveScoreboard(this);
    }

    /**
     * Adds a new score to the scores list, resorts it with the highest
     * scores at the top, and then trims any scores that are below the
     * top 5.
     *
     * @param score The score to add
     */
    public void addNewScore(int score) {
        if(topScores.contains(score)) {
            // No need to add duplicate high score..
            return;
        }

        // Add the score and re-sort the list
        topScores.add(score);
        Collections.sort(topScores, Collections.reverseOrder());

        // Remove any scores from the bottom of the list (lowest scores) if they're
        // not in the top 5 scores and save the scoreboard to file
        if (topScores.size() > TOP_SCORES_TO_SHOW) {
            topScores.subList(TOP_SCORES_TO_SHOW, topScores.size()).clear();
        }

        save();
    }

    /**
     * Returns the list of the top scores
     *
     * @return The list of scores
     */
    public ArrayList<Integer> getTopScores() {
        return topScores;
    }

    /**
     * Returns the amount of scores this scoreboard is capable of holding
     *
     * @return The limit of top scores
     */
    public int getTopScoreLimit() {
        return TOP_SCORES_TO_SHOW;
    }

    /**
     * Loads the scoreboard instance from the file {@code SCOREBOARD_PATH} and returns it. If
     * loading fails (e.g. file does not exist), a new scoreboard instance is returned for use instead.
     *
     * @return A loaded scoreboard, or a new one if loading fails
     */
    public static Scoreboard loadScoreboard() {
        try {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(SCOREBOARD_PATH));
            Scoreboard scoreboard = (Scoreboard) in.readObject();
            in.close();

            return scoreboard;
        } catch (Exception e) {
            System.err.println("Failed to load scoreboard from file.. " + e.getMessage());
            e.printStackTrace();

            return new Scoreboard();
        }
    }

    /**
     * Saves this scoreboard to the file {@code SCOREBOARD_PATH}
     *
     * @param scoreboard The scoreboard to save
     */
    public static void saveScoreboard(Scoreboard scoreboard) {
        try {
            ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(SCOREBOARD_PATH));

            out.writeObject(scoreboard);
            out.flush();
            out.close();
        } catch (Exception e) {
            System.err.println("Failed to save scoreboard to file.. " + e.getMessage());
            e.printStackTrace();
        }
    }
}
