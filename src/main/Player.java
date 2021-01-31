package main;

/**
 * The Player class is used to keep track of the player stats
 *
 * @author Harry Felton - 18032692
 */
public class Player {
    /**
     * The players current score
     */
    private int score = 0;

    /**
     * The amount of lives this player has left
     */
    private int lives = 3;

    /**
     * Represents whether or not the player has been disqualified/has lost
     */
    private boolean hasLost = false;

    /**
     * Time passed since the last time this player scored
     */
    private double timeSinceLastScore = 0;

    /**
     * Constructs the player instance
     */
    public Player() {
    }

    /**
     * Increase the score of the player by {@code amount}
     *
     * @param amount The amount by which to increase the players score
     * @see #score
     */
    public void increaseScore(int amount) {
        score += amount;
        timeSinceLastScore = 0;
    }

    /**
     * Returns the score of this player
     *
     * @return Returns the score of the player
     */
    public int getScore() {
        return score;
    }

    /**
     * Returns the time since the player scored a point
     *
     * @return Returns the time elapsed since the player scored
     */
    public double getScoreTime() {
        return timeSinceLastScore;
    }

    /**
     * Notify the player that they've lost
     */
    public void notifyLoss() {
        hasLost = true;
    }

    /**
     * Removes a life from this player, if no lives remain, notifies the game of a loss condition
     *
     * @return Returns the amount of lives the player currently has
     */
    public int reduceLives() {
        if(this.lives > 0) {
            this.lives--;
        }

        return this.lives;
    }

    /**
     * Returns the amount of lives left for this player
     *
     * @return The amount of lives
     */
    public int getLives() {
        return lives;
    }

    /**
     * Increases the amount of lives for this player by 1
     *
     * @return The new amount of lives
     */
    public int increaseLives() {
        return this.increaseLives(1);
    }

    /**
     * Increases the amount of lives for this player by the {@code amount} provided
     *
     * @param amount The amount of lives to add
     * @return The new amount of lives
     */
    public int increaseLives(int amount) {
        this.lives = Math.min(3, this.lives+amount);
        return this.lives;
    }

    /**
     * Update the Player instance
     *
     * @param dt Time since last update
     */
    public void update(double dt) {
        timeSinceLastScore += dt;
    }
}
