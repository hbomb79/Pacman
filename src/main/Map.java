package main;

import entity.Entity;
import entity.PacmanEntity;
import entity.pickup.FruitPointPickup;
import entity.pickup.LargePointPickup;
import entity.pickup.Pickup;
import entity.pickup.PointPickup;
import exception.InvalidMapException;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Map class is used to contain all relevant information about a particular level;
 * All levels are loaded at start-up, and their map file is parsed, checked and stored
 * to be used by the game when spawning Pacman, ghosts, walls, and point pickups.
 *
 * The constructor for the Map class is private; this means you cannot use
 * the {@code new} keyword to create an instance. Instead, the static {@code loadFromFile} must
 * be used.
 *
 * Once start-up is complete, maps should be accessed via the {@code MapController}.
 *
 * @see #loadFromFile(String)
 * @see controllers.MapController
 * @author Harry Felton
 */
public class Map {
    /**
     * A 1D array of the map. The map is split in to
     * a grid, where each integer in here presents one
     * grid square.
     *
     * A 1 indicates that this the grid is traversable (path); 0 means it
     * is not (wall).
     * Additionally, 2 indicates a spawn point for Pacman, 3 indicates a large point spawn and 4,5,6 indicate
     * spawn points for the ghosts
     */
    protected final int[] points;

    /**
     * The spawn point of the Pacman in this map; grid-based. Must be converted to pixel
     * based co-ordinates before this can be used for drawing/spawning.
     */
    protected Point pacmanSpawnPoint;

    /**
     * The spawn points of the various ghosts in this map; grid-based. Must be converted to pixel
     * based co-ordinates before this can be used for drawing/spawning.
     */
    protected LinkedList<Point> ghostSpawnPoints;

    /**
     * The ID of the level as loaded from the map file
     */
    protected final int id;

    /**
     * The name of the level as loaded from the map file
     */
    protected final String name;

    /**
     * A regular expression used to parse out the level information (ID and name)
     */
    protected final static Pattern levelHeaderPattern = Pattern.compile("^(\\d*)\\s*-\\s*([\\w ]+)$");

    /**
     * The color to be used when drawing part of the game grid that is marked as a path/spawn point/etc
     */
    protected final Color pathColour = Color.BLACK;

    /**
     * The color to be used when drawing part of the game grid that is marked as a wall
     */
    protected final Color wallColour = Color.BLUE;

    /**
     * Private constructor for the Map. Private as the only way this
     * class should be instantiated is via the static {@code loadFromFile} method.
     *
     * @param points The array of points used to make up the map
     * @param id The identifier of the map
     * @param name The label of the map
     */
    private Map(int[] points, int id, String name) {
        this.points = points;
        this.id = id;
        this.name = name;

        try {
            scanMap();
        } catch (InvalidMapException e) {
            System.err.println("Map loading failed: " + e.getMessage() + ". Execution aborted!");
            System.exit(-1);
        }
    }

    /**
     * Tests if the position provided is a valid grid-based position.
     *
     * @param x The grid-based X position to test
     * @param y The grid-based Y position to test
     * @return True if the X/Y position is inside the bounds of the map
     */
    protected boolean isValidLocation(int x, int y) {
        return x >= 0 && y >= 0 && x < PacmanGame.HORIZONTAL_GRID_COUNT && y < PacmanGame.VERTICAL_GRID_COUNT;
    }

    /**
     * Tests if the position provided is a valid path position for the
     * path finding
     *
     * @param x The grid-based X position to test
     * @param y The grid-based Y position to test
     * @return True if the position provided is a path, false if it's blocked
     */
    public boolean isPath(int x, int y) {
        if(isValidLocation(x, y)){
            return points[(y*PacmanGame.HORIZONTAL_GRID_COUNT) + x] != 0;
        }

        return false;
    }

    /**
     * Prepares the map for game play by scanning over the map and creating
     * pickups based on the map specified.
     *
     * @param game The game instance the map belongs to
     * @return An array of Pickups to be spawned before the map is played
     */
    public ArrayList<Pickup> prepareMap(PacmanGame game) {
        int frameWidth = PacmanGame.HORIZONTAL_GRID_COUNT;
        int gridSize = PacmanGame.GRID_SIZE;
        ArrayList<Pickup> pointPickups = new ArrayList<>();

        int playerLives = PacmanGame.getGameInstance().getPlayer().getLives();
        Random r = game.generateRandom();
        int amountOfFruit = Math.max(1, r.nextInt(5 - playerLives));

        ArrayList<Integer> fruitPoints = new ArrayList<>();
        int pointsLength = points.length;
        for(int i = 0; i < amountOfFruit; i++) {
            int point;
            while(true) {
                point = r.nextInt(pointsLength);
                if(points[point] != 0) {
                    fruitPoints.add(point);
                    break;
                }
            }
        }

        int y = 0;
        for(int i = 0; i < points.length; i++) {
            if(i != 0 && i % frameWidth == 0) y+=gridSize;
            int x = (i%frameWidth) * gridSize;

            if(points[i] == 3) {
                // Large point pickup
                pointPickups.add(new LargePointPickup(game, x, y));
            } else if(points[i] != 0) {
                // Small pickup, OR, an extra lives pickup
                if(fruitPoints.contains(i)) {
                    pointPickups.add(new FruitPointPickup(game, x, y));
                } else {
                    pointPickups.add(new PointPickup(game, x, y));
                }
            }
        }

        return pointPickups;
    }


    /**
     * Scans the map for spawn points, storing them for later; this allows us to selectively spawn
     * certain ghosts and pickups on the map.
     *
     * @throws InvalidMapException Thrown if the map is invalid (missing points, multiple pacman spawn points, etc)
     */
    protected void scanMap() throws InvalidMapException {
        Point pacmanSpawn = null;
        LinkedList<Point> ghostSpawnPoints = new LinkedList<>();

        int frameWidth = PacmanGame.HORIZONTAL_GRID_COUNT;
        int frameHeight = PacmanGame.VERTICAL_GRID_COUNT;

        int frameCount = frameWidth * frameHeight;
        if(points.length != frameCount) {
            throw new InvalidMapException("Map scan failed: data points do not cover the games area");
        }

        int x = 0;
        int y = 1;
        for (int point : this.points) {
            x++;
            if (x > frameWidth) {
                y++;
                x = 1;
            }

            switch (point) {
                case 2:
                    if (pacmanSpawn == null) {
                        pacmanSpawn = new Point(x, y);
                    } else {
                        throw new InvalidMapException("Map scan failed: Multiple spawn points for Pacman entity discovered. Only one can exist in a map (designated by a '2')");
                    }

                    break;
                case 4:
                case 5:
                case 6:
                    ghostSpawnPoints.add(new Point(x, y));
                    break;
                default:
                    break;
            }
        }

        if(pacmanSpawn == null) {
            throw new InvalidMapException("Map scan failed: No spawn point for Pacman found inside map (designated by a '2')");
        }
        this.pacmanSpawnPoint = pacmanSpawn;
        this.ghostSpawnPoints = ghostSpawnPoints;
    }

    /**
     * Draw the map by iterating through the points stored in the map data, and drawing the game grid.
     *
     * @param game The PacmanGame instance the map is to be drawn to
     */
    public void draw(PacmanGame game) {
        Graphics2D g = game.getGameGraphics();

        int frameWidth = PacmanGame.HORIZONTAL_GRID_COUNT;
        int gridSize = PacmanGame.GRID_SIZE;

        Rectangle shape = new Rectangle(0, 0, gridSize, gridSize);
        for(int i = 0; i < points.length; i++) {
            if(i != 0 && i % frameWidth == 0) shape.y+=gridSize;

            g.setColor(points[i] == 0 ? wallColour : pathColour);
            shape.x = (i%frameWidth) * gridSize;
            g.fill(shape);
        }
    }

    /**
     * Loads a map from a file. This file must
     * be of the correct format:
     *
     * - Line 1 must contain the level number and name (ID - NAME)
     * - All subsequent lines must of the same width
     * - The amount of lines will be checked against the size of the game window
     *
     * @param path The string pointing to the file used for loading
     * @return The map created from this file
     * @throws InvalidMapException Thrown if the map is invalid (missing points, multiple pacman spawn points, etc)
     */
    public static Map loadFromFile(String path) throws InvalidMapException {
        File mapHandle = new File(path);
        Scanner mapReader;
        try {
            mapReader = new Scanner(mapHandle);
        } catch (FileNotFoundException e) {
            throw new InvalidMapException("The path provided does not exist or cannot be read. Path: " + path);
        }

        int[] resultMap = new int[PacmanGame.HORIZONTAL_GRID_COUNT * PacmanGame.VERTICAL_GRID_COUNT];
        String levelName = "";
        int levelID = 0;
        int seekHead = 0;
        AtomicInteger position = new AtomicInteger();

        while(mapReader.hasNextLine()) {
            seekHead++;
            String line = mapReader.nextLine();
            if(seekHead > PacmanGame.VERTICAL_GRID_COUNT + 1) {
                throw new InvalidMapException("Too many data lines: can be no more than " + PacmanGame.VERTICAL_GRID_COUNT + " but found atleast " + seekHead);
            }

            // Parse this current line
            if(seekHead == 1) {
                Matcher m = levelHeaderPattern.matcher(line);
                if(!m.find()) {
                    throw new InvalidMapException("Malformed header line; should be of format 'ID (int) - NAME (string)' ");
                } else {
                    levelID = Integer.parseInt(m.group(1));
                    levelName = m.group(2);
                }
            } else {
                // Confirm the length of this line matches. Strip whitespace first.
                int lineLen = line.replaceAll("\\s", "").length();
                if(lineLen != PacmanGame.HORIZONTAL_GRID_COUNT) {
                    throw new InvalidMapException("Line at " + seekHead + " is of invalid length. All map data lines must of be of length " + PacmanGame.HORIZONTAL_GRID_COUNT + ", found length of " + lineLen + " instead.");
                } else {
                    Arrays.stream( line.replaceAll("\\s{2,}", " ").split("\\s") )
                            .mapToInt(Integer::parseInt)
                            .forEach((int a)-> {
                                resultMap[position.get()] = a;
                                position.getAndIncrement();
                            });

                }
            }
        }

        return new Map(resultMap, levelID, levelName);
    }

    /**
     * Fetches the ID of the map
     *
     * @return The ID
     */
    public int getId() {
        return id;
    }

    /**
     * Fetches the name of the map
     *
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     * Fetches the array of points that make up this map
     *
     * @return The points
     */
    public int[] getPoints() {
        return points;
    }

    /**
     * This method will calculate a location that is a certain distance INFRONT of a moving entity, including
     * taking corners in the map in the event that a wall is reached.
     *
     * @param baseLocation The location to start the calculation from.
     * @param distance The amount of tiles to traverse.
     * @param flankDirection The initial starting direction, usually of the entity located at the baseLocation.
     * @return The point with which to navigate to in order to flank the moving entity.
     */
    public Point calculateFlankRoute(Point baseLocation, int distance, Entity.DIRECTION flankDirection) {
        Point seekLocation = new Point(baseLocation);

        for(int i = 0; i < distance; i++) {
            int newX = seekLocation.x + (flankDirection == Entity.DIRECTION.LEFT ? -1 : (flankDirection == Entity.DIRECTION.RIGHT ? 1 : 0));
            int newY = seekLocation.y + (flankDirection == Entity.DIRECTION.UP ? -1 : (flankDirection == Entity.DIRECTION.DOWN ? 1 : 0));

            if(isPath(newX, newY)) {
                // We can advance in this direction
                seekLocation.setLocation(newX, newY);
            } else {
                // This tile is not a path, meaning the pacman must turn when it reaches
                // We assume that it'll turn towards us, so check the surrounding tiles
                if(isPath(seekLocation.x-1, seekLocation.y)) {
                    // Path to the left is free
                    flankDirection = Entity.DIRECTION.LEFT;
                    seekLocation.setLocation(seekLocation.x-1, seekLocation.y);
                } else if(isPath(seekLocation.x+1, seekLocation.y)) {
                    // Path to the right is free
                    flankDirection = Entity.DIRECTION.RIGHT;
                    seekLocation.setLocation(seekLocation.x+1, seekLocation.y);
                } else if(isPath(seekLocation.x, seekLocation.y+1)) {
                    // Path below is free
                    flankDirection = Entity.DIRECTION.DOWN;
                    seekLocation.setLocation(seekLocation.x, seekLocation.y+1);
                } else if(isPath(seekLocation.x, seekLocation.y-1)) {
                    // Path above is free
                    flankDirection = Entity.DIRECTION.UP;
                    seekLocation.setLocation(seekLocation.x, seekLocation.y-1);
                } else {
                    System.err.println("Unknown state - path finding has found no available tiles to move.");
                }
            }
        }

        return seekLocation;
    }

    /**
     * Provides the point that specifies the position that Pacman should be spawned at, pixel-based
     *
     * @return The point, in pixel based format.
     */
    public Point getPacmanSpawnPoint() {
        int gridSize = PacmanGame.GRID_SIZE;
        return new Point((pacmanSpawnPoint.x - 1) * gridSize, (pacmanSpawnPoint.y - 1) * gridSize);
    }

    /**
     * Fetches the list of ghost spawn points to use
     *
     * @return The points to spawn the ghosts at
     */
    public LinkedList<Point> getGhostSpawnPoints() {
        return ghostSpawnPoints;
    }

    /**
     * Given a point, makes it grid based and returns it
     *
     * @param p The point to be made grid based
     * @return Grid based version of this point
     */
    public static Point getGridBasedPoint(Point p) {
        return new Point(p.x /= PacmanGame.GRID_SIZE, p.y /= PacmanGame.GRID_SIZE);
    }

    /**
     * Given an X and Y position, returns a point that is made grid relative
     *
     * @param x The pixel-based X position
     * @param y The pixel-based Y position
     * @return The grid based {@code Point}
     */
    public static Point getGridBasedPoint(int x, int y) {
        return getGridBasedPoint(new Point(x, y));
    }
}
