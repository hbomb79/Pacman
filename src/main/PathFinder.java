package main;

import entity.ghost.GhostEntity;
import exception.InvalidPathFindingException;

import java.util.ArrayList;
import java.util.Collections;

/**
 * The PathFinder is responsible for calculating the path that a ghost
 * may take to reach the player. It stores all information relevant to this process,
 * and makes each ghosts next step available via public methods.
 *
 * This controller mainly serves as a way for the pathfinding algorithms to test
 * if a particular path is blocked, or to determine the cost of a particular path.
 *
 * @author Harry Felton - 18032692
 */
public class PathFinder {
    /**
     * The PacmanGame instance this path finder is working on.
     */
    protected final PacmanGame gameInstance;

    /**
     * The maximum amount of steps that will be evaluated before we give up.
     */
    protected final int maxDepth;

    protected final Node[][] nodes;

    protected final Map targetMap;

    private final ArrayList<Node> closedNodes = new ArrayList<>();

    private final ArrayList<Node> openNodes = new ArrayList<>();

    private final ArrayList<GhostEntity> ghosts;

    /**
     * The PathFinder constructor
     *
     * @param g The PacmanGame instance the controller belongs to
     * @param maxDepth The maximum depth that the path finding will search
     * @throws InvalidPathFindingException Thrown if no map is currently selected
     */
    public PathFinder(PacmanGame g, int maxDepth) throws InvalidPathFindingException {
        this.gameInstance = g;
        this.maxDepth = maxDepth;
        this.targetMap = g.getMapController().getSelectedMap();
        if(this.targetMap == null) {
            throw new InvalidPathFindingException("Cannot instantiate PathFinder instance as no map is currently selected");
        }

        // Initialise our nodes based on this map.
        int[] points = this.targetMap.getPoints();
        this.nodes = new Node[PacmanGame.HORIZONTAL_GRID_COUNT][PacmanGame.VERTICAL_GRID_COUNT];
        int nodeX = 0;
        int nodeY = 0;

        for(int i = 0; i < points.length; i++) {
            nodes[nodeX][nodeY] = new Node(nodeX, nodeY);

            nodeX++;
            if(nodeX >= PacmanGame.HORIZONTAL_GRID_COUNT) {
                nodeX = 0;
                nodeY++;
            }
        }

        ghosts = g.getEntityController().getGhosts();
    }

    /**
     * Returns the movement cost for a particular path. If a ghost is occupying this grid
     * position, it's cost is raised to 5; otherwise a cost of 1 applies.
     *
     * @param x The X position of the path (grid relative)
     * @param y The Y position of the path (grid relative)
     * @return Returns the movement cost of this path
     */
    private float getPathCost(int x, int y) {
        int gridSize = PacmanGame.GRID_SIZE;
        for(GhostEntity ghost : ghosts) {
            int ghostX = ghost.getX() / gridSize;
            int ghostY = ghost.getY() / gridSize;

            if(ghostX == x && ghostY == y) {
                return 10;
            }
        }

        return 1;
    }

    /**
     * Returns the heuristic cost of a path based on the manhattan distance.
     *
     * @param sX The starting X position (grid relative)
     * @param sY The starting Y position (grid relative)
     * @param tX The target X position (grid relative)
     * @param tY The target Y position (grid relative)
     * @return The heuristic cost of this path.
     */
    private float getHeuristicCost(int sX, int sY, int tX, int tY) {
        int dX = Math.abs(tX - sX);
        int dY = Math.abs(tY - sY);
        return dX + dY;
    }

    /**
     * Returns a {@code Path} object containing all the information about the steps
     * that need to be taken to reach the destination.
     *
     * Note that ALL position based arguments are GRID-BASED, not pixel based.
     *
     * @param sX The starting X position
     * @param sY The starting Y position
     * @param tX The target X position
     * @param tY The target Y position
     * @return Returns the path details stored inside a Path object.
     */
    public Path calculatePath(int sX, int sY, int tX, int tY) {
        // Clear our lists of processed nodes
        closedNodes.clear();
        openNodes.clear();

        // If the destination is not a path, then no path to it can be calculated
        if(!targetMap.isPath(tX, tY)) {
            return null;
        }

        // Initialise our starting nodes
        nodes[sX][sY].cost = 0;
        nodes[sX][sY].depth = 0;

        addToOpenNodes(nodes[sX][sY]);
        nodes[tX][tY].parent = null; // Likely not needed.

        int depth = 0;
        while((depth < this.maxDepth) && (openNodes.size() > 0)) {
            // Keep checking for a path as long as we have options left, and we have not hit our maximum depth.
            Node current = openNodes.get(0);
            if(current.equals(nodes[tX][tY])) {
                // Found target
                break;
            }

            openNodes.remove(current);
            closedNodes.add(current);

            // Search neighbours by checking one to left, above, right, below.
            for(int scanX = -1; scanX < 2; scanX++) {
                for(int scanY = -1; scanY < 2; scanY++) {
                    // Either the center of our search range, or attempting to move diagonally.
                    if((scanX == 0 && scanX == scanY) || (scanX != 0 && scanY != 0)) {
                        continue;
                    }

                    int neighbourX = scanX + current.x;
                    int neighbourY = scanY + current.y;

                    if(targetMap.isPath(neighbourX, neighbourY)) {
                        float neighbourCost = current.cost + getPathCost(neighbourX, neighbourY);
                        Node neighbour = nodes[neighbourX][neighbourY];

                        if(neighbourCost < neighbour.cost) {
                            // The previously calculated cost to this neighbour is wrong; we've found
                            // a better path to this node.
                            openNodes.remove(neighbour);
                            closedNodes.remove(neighbour);
                        }

                        if(!(openNodes.contains(neighbour) || closedNodes.contains(neighbour))) {
                            neighbour.cost = neighbourCost;
                            neighbour.heuristic = getHeuristicCost(sX, sY, neighbourX, neighbourY);
                            depth = Math.max(depth, neighbour.setParentNode(current));
                            addToOpenNodes(neighbour);
                        }
                    }
                }
            }
        }

        if(nodes[tX][tY].parent == null) {
            // No node found it's way to the target. Max depth was reached, or no path exists.
            return null;
        }

        Path p = new Path();
        Node targetNode = nodes[tX][tY];
        final Node startingNode = nodes[sX][sY];
        while(!targetNode.equals(startingNode)) {
            p.addStep(targetNode.x, targetNode.y);

            targetNode = targetNode.parent;
        }
        p.addStep(sX, sY);

        return p;
    }

    /**
     * Adds a given node to the list of open nodes and resorts the collection. The
     * Node inner class implements Comparable, allowing this sorting to sort based
     * on the cost of a particular node as calculated currently.
     *
     * @param n The node to add to the open node list.
     */
    private void addToOpenNodes(Node n) {
        openNodes.add(n);
        Collections.sort(openNodes);
    }

    /**
     * A inner class used to encapsulate information regarding the maps nodes,
     * such as it's cost, position, and depth of path finding at the current moment
     * of calculation. The parameters of this class are highly volatile and shouldn't
     * be relied upon until path finding has completed.
     */
    private static class Node implements Comparable<Node> {
        /**
         * The X position of this node
         */
        private final int x;

        /**
         * The Y position of this node
         */
        private final int y;

        /**
         * The amount of nodes that have been investigated before this node was
         * found. i.e: the depth
         */
        private int depth;

        /**
         * The movement cost to get to this node
         *
         * @see #getPathCost(int, int)
         */
        private float cost;

        /**
         * The heuristic cost to get to this node (manhattan distance)
         *
         * @see #getHeuristicCost(int, int, int, int)
         */
        private float heuristic;

        /**
         * The node that was checked before this one; ie the parent. This node
         * must be a neighbour of this node and any entity path finding to this
         * node must first pass through the parent.
         */
        private Node parent;

        /**
         * Node constructor simply sets the X and Y position of the node to the arguments provided
         *
         * @param x The X position of this node (grid relative)
         * @param y The Y position of this node (grid relative)
         */
        public Node(int x, int y) {
            this.x = x;
            this.y = y;
        }

        /**
         * Sets the parent of this node to the node provided, and returns the current
         * depth of this nodes pathfinding.
         *
         * @param parent The parent to set
         * @return The depth of path finding required to reach this node.
         */
        protected int setParentNode(Node parent) {
            this.parent = parent;
            this.depth = parent.depth + 1;

            return this.depth;
        }

        /**
         * A comparing function used by {@code Collections.sort}. Sorts the
         * nodes based on their cost (combined heuristic and movement cost)
         *
         * @param b The node being compared against this one
         * @return Returns an integer representing the sort result. 0 = no change, -1 sort down, 1 sort up.
         */
        public int compareTo(Node b) {
            float f = heuristic + cost;
            float of = b.heuristic + b.cost;

            if (f < of) {
                return -1;
            } else if (f > of) {
                return 1;
            } else {
                return 0;
            }
        }
    }
}
