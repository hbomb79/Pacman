package controllers;

import entity.pickup.Pickup;
import entity.pickup.PointPickup;
import main.Map;
import main.PacmanGame;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * The MapController is responsible for storing all the Pacman levels,
 * and providing these levels to the game on request.
 *
 * @author Harry Felton - 18032692
 */
public class MapController extends Controller {
    private final String mapRoot = "resources/maps/";
    protected final LinkedList<Map> maps = new LinkedList<>();
    protected Map selectedMap;

    /**
     * The MapController constructor, attempts to load the maps from the 'resources/maps/' directory
     * and handles any arising exceptions.
     *
     * @param g The PacmanGame instance the controller belongs to
     */
    public MapController(PacmanGame g) {
        super(g);

        try{
            loadMaps();
        } catch (Exception e) {
            System.err.println("FATAL: Unable to load maps: " + e.getMessage() + ". Execution aborted.");
            e.printStackTrace();
            System.exit(-1);
        }
    }

    /**
     * Loads all maps for the game
     * @throws Exception If no map files exist inside directory, or if the directory does not exist.
     */
    private void loadMaps() throws Exception {
        File directory = new File(mapRoot);
        String[] ls = directory.list();

        if(ls == null) {
            throw new Exception("Failed to load maps; map root provided is invalid.");
        }

        for(String f : ls) {
            Map m = Map.loadFromFile(mapRoot + f);
            maps.add(m);
        }
    }

    /**
     * Returns the map with the matching ID, or null if none is found
     *
     * @param ID The ID to test for
     * @return A Map instance if one exists with the corresponding ID, null otherwise.
     */
    protected Map loadMapWithID(int ID) {
        for(Map m : maps) {
            if(m.getId() == ID) {
                return m;
            }
        }

        return null;
    }

    /**
     * Returns the amount of maps currently loaded
     *
     * @return Returns the count
     */
    public int getMapCount() {
        return maps.size();
    }

    /**
     * Draws the map currently selected
     */
    public void drawSelectedMap() {
        if(selectedMap == null) return;

        selectedMap.draw(gameInstance);
    }

    /**
     * Tests the collision box provided against the map. Each point in the map is iterated over and
     * expanded to the specified grid size when testing.
     *
     * @param collisionBox The collision to test against
     * @return Returns the boundary of the map that was collided with, or null if no collision found.
     */
    public Rectangle checkSelectedMapCollision(Rectangle collisionBox) {
        if(selectedMap == null) {
            return null;
        }

        int mapGridSize = PacmanGame.GRID_SIZE;
        int mapWidth = PacmanGame.HORIZONTAL_GRID_COUNT * mapGridSize;

        int[] points = selectedMap.getPoints();
        int y = 0;
        int x = 0;
        for(int i = 0; i < points.length; i++) {
            if(points[i] == 0) {
                // Test for a collision against these points.
                Rectangle infringed = new Rectangle(x, y, mapGridSize, mapGridSize);
                if (infringed.intersects(collisionBox)) {
                    return infringed;
                }
            }

            x += mapGridSize;
            if(x >= mapWidth) {
                x = 0;
                y += mapGridSize;
            }
        }

        return null;
    }

    /**
     * Returns the pacman spawn point from the selected map
     *
     * @return Returns the spawn point, or null if no map is selected
     */
    public Point getPacmanSpawnPoint() {
        if(selectedMap == null) return null;

        return selectedMap.getPacmanSpawnPoint();
    }

    /**
     * Returns the list of spawn points for ghosts as specified
     * by the selected map
     *
     * @return Returns the spawn points, or null if no map is selected
     */
    public LinkedList<Point> getGhostSpawnPoints() {
        if(selectedMap == null) return null;

        return selectedMap.getGhostSpawnPoints();
    }

    /**
     * Returns the point pickups that the selected map has specified.
     *
     * @return Returns the PointPickups
     */
    public ArrayList<Pickup> getPointPickups() {
        if(selectedMap == null) return null;

        return selectedMap.prepareMap(gameInstance);
    }

    /**
     * Selects the map that has the ID specified
     *
     * @param ID The maps ID to look for
     * @return The map found, if any. Null otherwise
     */
    public Map selectMap(int ID) {
        Map m = loadMapWithID(ID);
        if(m == null) {
            System.err.println("Map with ID " + ID + " does not exist!");
            return null;
        }

        selectedMap = m;
        return m;
    }

    /**
     * Tests if the map controller has a map after the currently selected map
     *
     * @return True if there is a next map, false otherwise
     */
    public boolean isNextMap() {
        return selectedMap.getId() + 1 < getMapCount();
    }

    /**
     * Advances the selected map to the next level, if any
     *
     * @return The next map, or null if no maps next
     */
    public Map selectNextMap() {
        if(selectedMap == null) {
            System.err.println("Cannot select next map as no map is currently selected!");
            return null;
        } else if(selectedMap.getId() + 1 >= getMapCount()) {
            System.err.println("Cannot select next map, no further map exists!");
            return null;
        }

        return selectMap(selectedMap.getId() + 1);
    }

    /**
     * The currently selected map
     *
     * @return The map selected
     */
    public Map getSelectedMap() {
        return selectedMap;
    }
}
