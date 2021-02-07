package main;

import controllers.*;
import fragment.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Random;

/**
 * Base class for this pacman game; instantiates all core controllers, game engine, entity sprites and runs the main
 * render loop.
 *
 * Singleton class, fetch running instance using PacmanGame.getGameInstance();
 *
 * @author Harry Felton - 18032692
 */
public class PacmanGame extends CoreEngine {
    /**
     * The title of the window
     */
    public static final String WINDOW_TITLE = "Pacman!";

    /**
     * Size of the grid used for pathfinding; map size is always a multiple of this.
     */
    public static final int GRID_SIZE = 16;

    /**
     * The count of the grids that make up the width of the game. Ideally, this should be an
     * odd number to allow symmetrical designs and centered spawning points.
     */
    public static final int HORIZONTAL_GRID_COUNT = 19;

    /**
     * The amount of grids that make up the height of the game map.
     */
    public static final int VERTICAL_GRID_COUNT = 19;

    /**
     * The width of the game
     */
    public static final int    WIDTH = GRID_SIZE * HORIZONTAL_GRID_COUNT;

    /**
     * The height of the game
     */
    public static final int    HEIGHT = GRID_SIZE * VERTICAL_GRID_COUNT;

    /**
     * The possible game states
     */
    public enum STATE {
        MENU,
        GAME,
        DEATH
    }

    /**
     * The player currently in-game
     */
    protected Player player;

    /**
     * The current game state
     */
    protected STATE gameState = STATE.MENU;

    /**
     * The state that the game will be in after the next update tick completes
     */
    protected STATE nextState;

    /**
     * Represents whether or not the game is paused
     */
    protected boolean paused = false;

    /**
     * The random number generator
     */
    protected Random randomGenerator;

    /**
     * The UIController to manage the fragments
     *
     * @see #getUIController()
     */
    protected UIController ui;

    /**
     * The EffectController used to update and display currently running effects
     *
     * @see #getEffectsController()
     */
    protected EffectController fx;

    /**
     * The CollisionController used to test for collisions between entities
     *
     * @see #getCollisionController()
     */
    protected CollisionController collision;

    /**
     * The SpriteController is used to dispatch appropriate sprites to their
     * entities.
     *
     * @see #getSpriteController()
     */
    protected SpriteController spriteController;

    /**
     * The MapController is used to load the games levels, and spawn entities at their
     * specified locations. This controller also facilitates collision checking against
     * the maps boundaries (walls).
     *
     * @see #getMapController()
     */
    protected MapController mapController;

    /**
     * The EntityController used to manage and update on screen entities
     *
     * @see #getEntityController()
     */
    protected EntityController entity;

    /**
     * Used to indicate whether or not the graphics are initialised
     */
    private boolean isGraphicsInitialised = false;

    /**
     * The fragment used when the game is in MENU state
     */
    protected MenuFragment menuFragment;

    /**
     * The fragment used when the game is in GAME state, and is paused
     */
    protected PauseFragment pauseFragment;

    /**
     * The fragment used when the game is over
     */
    protected DeathFragment deathFragment;

    /**
     * The fragment used when the game is in GAME state
     */
    protected GameFragment gameFragment;

    /**
     * The {@code SnakeGame} singleton instance
     *
     * @see #getGameInstance()
     */
    protected static PacmanGame gameInstance;

    /**
     * The SoundEffect to use when the player acquires points
     */
    protected final SoundEffect scoreSoundEffect;

    /**
     * The Scoreboard instance used to track high-scores
     */
    protected final Scoreboard scoreboard;

    /**
     * Private constructor as this class is a singleton and can only be initialised
     * from inside this class
     *
     * @see #getGameInstance()
     */
    private PacmanGame() {
        super(WIDTH, HEIGHT, WINDOW_TITLE);

        scoreSoundEffect = SoundEffect.loadSoundEffect("resources/score.wav");
        scoreboard = Scoreboard.loadScoreboard();
    }

    /**
     * The entry point of this game. Creates the singleton instance.
     *
     * @param args The arguments passed to the program
     */
    public static void main(String[] args) {
        // Entry point for game. Use this method to retrieve the singleton instance of SnakeGame.
        PacmanGame.getGameInstance();
    }

    /**
     * Plays the sound effect that should play when a player
     * pickups a point
     */
    public void playScoreSoundEffect() {
        scoreSoundEffect.playOnce(-15f);
    }

    /**
     * Initialise all the controllers for this instance
     */
    @Override
    public void initialiseEngine() {
        super.initialiseEngine();

        spriteController = new SpriteController(this);
        mapController = new MapController(this);
        ui = new UIController(this);
        entity = new EntityController(this);
        collision = new CollisionController(this);
        fx = new EffectController(this);
    }

    /**
     * When the graphics are ready, this method initialises all the UI fragments
     * and changes the game state to the MENU
     */
    protected void graphicsReady() {
        if( !isGraphicsInitialised ) return;

        menuFragment =  (MenuFragment)ui.registerFragment(new MenuFragment(this));
        deathFragment = (DeathFragment)ui.registerFragment(new DeathFragment(this));
        gameFragment =  (GameFragment)ui.registerFragment(new GameFragment(this));
        pauseFragment = (PauseFragment)ui.registerFragment(new PauseFragment(this));

        changeGameState(STATE.MENU);
    }

    /**
     * Called on each update tick. Changes the state of the game to the next queued state. If the game is
     * paused, the {@code PauseFragment} is activated. If the game state is currently {@code GAME}, and the
     * game isn't paused, then the entities and effects are updated.
     *
     * @param dt The amount of time passed since the last tick
     */
    @Override
    public void update(double dt) {
        if(nextState != null) {
            changeGameState(nextState);
            nextState = null;
        }

        if(paused)
            pauseFragment.activate();

        if(gameState == STATE.GAME && !paused) {
            entity.update(dt);
            fx.update(dt);
        }

        ui.update(dt);
    }

    /**
     * Called when the game requests a repaint. If the graphics are ready, and the fragments aren't initialised, initialise
     * them.
     *
     * Clear the game screen, and redraw the entities, effects and UI.
     */
    @Override
    public void paintComponent() {
        if( !isGraphicsInitialised ) {
            isGraphicsInitialised = true;
            graphicsReady();
        }

        engineGraphics.setBackground(black);
        engineGraphics.setColor(yellow);
        engineGraphics.clearRect(0, 0, WIDTH, HEIGHT);

        if(gameState == STATE.GAME) {
            mapController.drawSelectedMap();
            entity.redraw();
            fx.redraw();
        }

        ui.redraw();
    }

    /**
     * Dispatches the key event to the registered entities, unless the key was 'ESCAPE', in which case the game
     * is (un)paused.
     *
     * @param event The KeyEvent to be dispatched
     */
    @Override
    public void keyPressed(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.VK_ESCAPE && gameState == STATE.GAME) {
            paused = !paused;
            // Refresh fragment-based scenes
            changeGameState(gameState);
        } else if (!paused) {
            entity.keyPressed(event);
        }
    }

    /**
     * Dispatches the MouseEvent to the UI controller
     *
     * @param event The MouseEvent to be dispatched
     */
    @Override
    public void mousePressed(MouseEvent event) {
        ui.mousePressed(event);
    }

    /**
     * Dispatches the MouseEvent to the UI controller
     *
     * @param event The MouseEvent to be dispatched
     */
    @Override
    public void mouseReleased(MouseEvent event) {
        ui.mouseReleased(event);
    }

    /**
     * Dispatches the MouseEvent to the UI controller
     *
     * @param event The MouseEvent to be dispatched
     */
    @Override
    public void mouseMoved(MouseEvent event) {
        ui.mouseMoved(event);
    }

    /**
     * Change the game state to the one provided. Activates the fragments that must be active
     * for the selected state.
     *
     * @param s The new game-state
     */
    public void changeGameState(STATE s) {
        ui.deactivateAllFragments();
        if(s == STATE.MENU) {
            menuFragment.activate();
        } else if(s == STATE.DEATH) {
            deathFragment.activate();
        } else if(s == STATE.GAME) {
            gameFragment.activate();
        }

        gameState = s;
    }

    /**
     * Schedule a game-state change
     *
     * @param s The next game state
     */
    public void scheduleGameStateChange(STATE s) {
        nextState = s;
    }

    /**
     * Notifies this game instance that the player provided has died.
     *
     * @param player The losing player
     */
    public void playerDeath(Player player) {
        player.notifyLoss();
        scoreboard.addNewScore(player.getScore());
        scheduleGameStateChange(STATE.DEATH);
    }

    /**
     * Player has collected all the points in the current level.
     *
     * Reset the entity controller, load the next map (if any), and re-initialise
     * ghosts and PacmanEntity.
     */
    public void levelComplete() {
        if(mapController.isNextMap()) {
            mapController.selectNextMap();
        }

        entity.initWithPlayer(player);
    }

    /**
     * Returns the players currently playing
     *
     * @return Return the list of {@code Player} instances
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Start the game by creating the {@code Player} instance, and initialising the {@code EntityController} with the
     * player. Spawns the initial pickups, ghosts, and the player entity (Pacman).
     */
    public void startGame() {
        this.player = new Player();
        mapController.selectMap(0);
        entity.initWithPlayer(this.player);

        scheduleGameStateChange(STATE.GAME);
    }

    /**
     * Quit the game
     */
    public void quitGame() {
        System.exit(1);
    }

    /**
     * Create the random number generator
     *
     * @return Returns the random number generator, creating one if one doesn't already exist
     */
    public Random generateRandom() {
        if(randomGenerator == null)
            randomGenerator = new Random();

        return randomGenerator;
    }

    /**
     * Generates a random point within the boundary of the game
     *
     * @return Returns the {@code Point} generated
     */
    public Point generateRandomPoint() {
        Random r = generateRandom();
        return new Point(r.nextInt(WIDTH), r.nextInt(HEIGHT));
    }

    /**
     * Fetches the {@code SnakeGame} singleton instance
     *
     * @return Returns the existing {@code SnakeGame}, or creates one if it doesn't exist yet.
     */
    public static PacmanGame getGameInstance() {
        // No game? No problem. Create a game instance and store it inside our protected static var.
        if( gameInstance == null ) {
            gameInstance = new PacmanGame();
            gameInstance.engineStart();
        }

        // Return the newly created/previously existing game.
        return gameInstance;
    }

    /**
     * Fetch the game graphics from the underlying game engine
     *
     * @return Returns the {@code Graphics2D} instance
     */
    public Graphics2D getGameGraphics() {
        return engineGraphics;
    }

    /**
     * Returns the sprite controller for entities to fetch their
     * associated sprite frames.
     *
     * @return The {@code SpriteController} instance attached to this game.
     */
    public SpriteController getSpriteController() {
        return spriteController;
    }

    /**
     * Returns the MapController to allow the games code to test for collisions,
     * and to draw the maps from outside the main game loop.
     *
     * @return The {@code MapController} instance attached to this game.
     */
    public MapController getMapController() {
        return mapController;
    }

    /**
     * Fetch the UI controller from the game instance
     *
     * @return Returns the {@code UIController}
     */
    public UIController getUIController() {
        return ui;
    }

    /**
     * Fetch the collision controller from the game instance
     *
     * @return Returns the {@code CollisionController}
     */
    public CollisionController getCollisionController() {
        return collision;
    }

    /**
     * Fetch the entity controller from the game instance
     *
     * @return Returns the {@code EntityController}
     */
    public EntityController getEntityController() {
        return entity;
    }

    /**
     * Fetch the effects controller from the game instance
     *
     * @return Returns the {@code EffectsController}
     */
    public EffectController getEffectsController() {
        return fx;
    }

    /**
     * Fetch the scoreboard instance from this game instance
     *
     * @return Returns the {@code Scoreboard} instance
     */
    public Scoreboard getScoreboard() {
        return scoreboard;
    }
}
