package objectEditor.model;

/**
 * Represents a game map made up of a grid of Terrain cells.
 */
public class Map extends ObjectInstance {
    private static final long serialVersionUID = 1L;
    
    private final int width;
    private final int height;
    private final Terrain[][] grid;
    private String type;

    /**
     * Creates a new map with the specified dimensions.
     * 
     * @param width the width of the map grid
     * @param height the height of the map grid
     */
    public Map(int width, int height) {
        super();
        this.width = width;
        this.height = height;
        this.grid = new Terrain[height][width];
        this.setName("New Map");
        this.setDescription("");
        this.setType("Map");
        initializeEmptyGrid();
    }

    /**
     * Initializes the grid with empty terrain cells.
     */
    private void initializeEmptyGrid() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Terrain terrain = new Terrain();
                terrain.setGridX(x);
                terrain.setGridY(y);
                grid[y][x] = terrain;
            }
        }
    }

    /**
     * Gets the terrain at the specified coordinates.
     * 
     * @param x the x coordinate
     * @param y the y coordinate
     * @return the terrain at the specified position
     * @throws IndexOutOfBoundsException if coordinates are out of bounds
     */
    public Terrain getTerrainAt(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException("Coordinates out of bounds");
        }
        return grid[y][x];
    }

    /**
     * Sets the terrain at the specified coordinates.
     * 
     * @param x the x coordinate
     * @param y the y coordinate
     * @param terrain the terrain to set
     * @throws IndexOutOfBoundsException if coordinates are out of bounds
     */
    public void setTerrainAt(int x, int y, Terrain terrain) {
        if (x < 0 || x >= width || y < 0 || y >= height) {
            throw new IndexOutOfBoundsException("Coordinates out of bounds");
        }
        terrain.setGridX(x);
        terrain.setGridY(y);
        grid[y][x] = terrain;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
    
    @Override
    public Object clone() {
        Map clone = (Map) super.clone();
        // We don't deep clone the grid since it's a complex operation
        // that would need to be handled separately
        return clone;
    }
}
