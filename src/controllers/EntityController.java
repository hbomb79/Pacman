package controllers;

import entity.Entity;
import entity.PacmanEntity;
import entity.ghost.DirectGhostEntity;
import entity.ghost.FlankGhostEntity;
import entity.ghost.GhostEntity;
import entity.ghost.SpeedFlankGhostEntity;
import entity.pickup.Pickup;
import entity.pickup.PointPickup;
import main.Player;
import main.RandomPoint;
import main.PacmanGame;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * The EntityController class handles the adding, removal, updating and drawing of onscreen elements, such
 * as {@code PacmanEntity}, ghosts, and associated pickups
 *
 * @author Harry Felton - 18032692
 */
public class EntityController extends Controller {
    /**
     * The entities currently registered inside this controller; they'll receive requests to update
     * and redraw themselves
     *
     * @see #update(double)
     * @see #redraw()
     */
    protected final LinkedList<Entity> entities = new LinkedList<>();

    /**
     * Entities to be destroyed after the end of the next update cycle
     *
     * @see #destroyPickup(Pickup)
     * @see #destroyPickups()
     */
    protected final LinkedList<Entity> entitiesToDestroy = new LinkedList<>();

    /**
     * Entities to be spawned at the beginning of an update cycle
     *
     * @see #spawnPickup(Pickup, Point)
     * @see #spawnPickups()
     */
    protected final LinkedList<Entity> entitiesToSpawn = new LinkedList<>();

    protected boolean sceneReinitialisationQueued = false;

    /**
     * Instantiates the controller with the {@code SnakeGame} instance to be used later
     *
     * @param g The current SnakeGame instance
     */
    public EntityController(PacmanGame g) {
        super(g);
    }

    /**
     * Initiates the {@code EntityController} with the {@code players} provided.
     *
     * @param player The {@code Player} instance to be used when initiating this controller
     */
    public void initWithPlayer(Player player) {
        entities.clear();
        entitiesToSpawn.clear();
        entitiesToDestroy.clear();

        // Spawn Pacman
        initialisePacman(player);

        // Spawn point/special pickups
        initialiseMapPickups();

        // Spawn ghosts
        initialiseGhosts();
    }

    /**
     * Initialises a {@code PacmanEntity} at the pre-selected spawn point
     * of the map
     *
     * @param player The player that owns the pacman entity
     */
    private void initialisePacman(Player player) {
        Point p = gameInstance.getMapController().getPacmanSpawnPoint();
        PacmanEntity e = new PacmanEntity(gameInstance, player, p.x, p.y);
        entities.add(e);
    }

    /**
     * Initialises this levels ghosts by finding the spawn points as specified by the
     * map and spawning alternating ghosts.
     */
    private void initialiseGhosts() {
        LinkedList<Point> ghostSpawnPoints = gameInstance.getMapController().getGhostSpawnPoints();
        int x = 0;
        for (Point point : ghostSpawnPoints) {
            int ghostX = (point.x - 1) * PacmanGame.GRID_SIZE;
            int ghostY = (point.y - 1) * PacmanGame.GRID_SIZE;
            GhostEntity g;
            switch (x) {
                case 0 -> g = new DirectGhostEntity(gameInstance, ghostX, ghostY);
                case 1 -> g = new FlankGhostEntity(gameInstance, ghostX, ghostY);
                default -> g = new SpeedFlankGhostEntity(gameInstance, ghostX, ghostY);
            }

            x++;
            if (x > 2) x = 0;
            entities.add(g);
        }
    }

    /**
     * Spawns point pickups as specified by the map
     */
    private void initialiseMapPickups() {
        ArrayList<Pickup> pointPickups = gameInstance.getMapController().getPointPickups();
        entities.addAll(pointPickups);
    }

    /**
     * Resets the game map by restoring the ghosts and pacman to their start locations
     *
     * Game pickups will be unaffected.
     */
    public void reinitialiseScene() {
        entities.removeIf((Entity entity) -> !(entity instanceof Pickup));
        initialisePacman(gameInstance.getPlayer());
        initialiseGhosts();
    }

    public void queueSceneReinitialisation() {
        sceneReinitialisationQueued = true;
    }

    /**
     * Spawns any entities that are currently queued, and then send an update tick to all currently registered. After
     * completion, remove any entities that have been queued for removal
     *
     * @param dt Time passed since last tick
     * @see #entities
     * @see #entitiesToSpawn
     * @see #entitiesToDestroy
     */
    public void update(double dt) {
        spawnPickups();
        if(sceneReinitialisationQueued) {
            reinitialiseScene();
            sceneReinitialisationQueued = false;
        }

        int pointPickupCount = 0;
        for (Entity entity : entities) {
            if(entity instanceof PointPickup) pointPickupCount++;
            entity.update(dt);
        }

        if(pointPickupCount == 0) {
            // All point pickups have been collected.
            gameInstance.levelComplete();
        }
        destroyPickups();
    }

    /**
     * Redraws all entities currently registered
     *
     * @see #entities
     */
    public void redraw() {
        for(Entity entity : entities) {
            entity.paintComponent();
        }
    }

    /**
     * Destroys all entities that have been queued to be removed
     *
     * @see #entitiesToDestroy
     */
    protected void destroyPickups() {
        entities.removeAll(entitiesToDestroy);
        entitiesToDestroy.clear();
    }

    /**
     * Spawns any entities that are currently queued
     *
     * @see #entitiesToSpawn
     */
    protected void spawnPickups() {
        entities.addAll(entitiesToSpawn);
        entitiesToSpawn.clear();
    }

    /**
     * Fetches the {@code PacmanEntity} instance belonging to the {@code Player} with ID {@code playerId}
     *
     * @return Returns the matching {@code PacmanEntity}, or {@code null} if no match was found
     */
    public PacmanEntity getPlayer() {
        for(Entity entity : entities) {
            if(entity instanceof PacmanEntity)
                return (PacmanEntity)entity;
        }

        return null;
    }

    /**
     * Fetches the current ghosts from the current entity list
     *
     * @return Returns the ghosts
     */
    public ArrayList<GhostEntity> getGhosts() {
        ArrayList<GhostEntity> res = new ArrayList<>();
        for(Entity e : entities) {
            if(e instanceof GhostEntity) {
                res.add((GhostEntity)e);
            }
        }

        return res;
    }

    /**
     * Handles incoming key presses by dispatching them to the players currently registered
     *
     * @param event The {@code KeyEvent} to be dispatched
     * @see #getPlayer()
     */
    public void keyPressed(KeyEvent event) {
        PacmanEntity player = getPlayer();
        int keycode = event.getKeyCode();
        switch(keycode) {
            case KeyEvent.VK_W:
                player.changeDirection(PacmanEntity.DIRECTION.UP);
                break;

            case KeyEvent.VK_S:
                player.changeDirection(PacmanEntity.DIRECTION.DOWN);
                break;

            case KeyEvent.VK_D:
                player.changeDirection(PacmanEntity.DIRECTION.RIGHT);
                break;

            case KeyEvent.VK_A:
                player.changeDirection(PacmanEntity.DIRECTION.LEFT);
                break;
        }
    }

    /**
     * Given a {@code Pickup} instance, this function generates a random point for it, ensuring that
     * the pickup is clear of any other entities inside the game.
     *
     * @param e The {@code Pickup} instance to be spawned
     */
    public void spawnPickupRandom(Pickup e) {
        RandomPoint p = new RandomPoint(e);
        p.selectPoint();
        spawnPickup(e, p);
    }

    /**
     * Spawns the {@code Pickup} instance at the {@code Point} provided by setting the {@code x} and {@code y}
     * position of the {@code Pickup} to that of the {@code Point} provided
     *
     * @param e The {@code Pickup} instance to be spawned
     * @param p The {@code Point} to be used to place the {@code Pickup}
     */
    public void spawnPickup(Pickup e, Point p) {
        e.setX(p.x);
        e.setY(p.y);

        entitiesToSpawn.add(e);
    }

    /**
     * Queues the given {@code Pickup} instance to be destroyed from the controller
     *
     * @param p The {@code Pickup} instance to be removed
     * @see #destroyPickups()
     */
    public void destroyPickup(Pickup p) {
        entitiesToDestroy.add(p);
    }
}
