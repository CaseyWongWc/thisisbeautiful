package objectEditor.model;

import objectEditor.util.FileUtils;
import objectEditor.util.ImageUtils;

import java.awt.Graphics;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Represents a game map consisting of a grid of MapCell objects.
 * The map can be resized and cells can be accessed and modified.
 */
public class GameMap extends ObjectInstance {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private String imagePath;
    private int width;
    private int height;
    private MapCell[][] cells;
    private int cellSize = 64; // Default display size for cells
    
    /**
     * Creates a new empty map with the given dimensions.
     * 
     * @param name The name of the map
     * @param width The width of the map in cells
     * @param height The height of the map in cells
     */
    public GameMap(String name, int width, int height) {
        this.name = name;
        this.description = "";
        this.imagePath = "";
        this.width = width;
        this.height = height;
        initializeEmptyCells();
    }
    
    /**
     * Creates a new empty map with default dimensions (10x10).
     * 
     * @param name The name of the map
     */
    public GameMap(String name) {
        this(name, 10, 10);
    }
    
    /**
     * Initializes the map with empty cells.
     */
    private void initializeEmptyCells() {
        cells = new MapCell[height][width];
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[y][x] = new MapCell();
            }
        }
    }
    
    /**
     * Resizes the map, preserving the existing cells where possible.
     * 
     * @param newWidth The new width of the map
     * @param newHeight The new height of the map
     */
    public void resize(int newWidth, int newHeight) {
        if (newWidth <= 0 || newHeight <= 0) {
            throw new IllegalArgumentException("Map dimensions must be positive");
        }
        
        MapCell[][] newCells = new MapCell[newHeight][newWidth];
        
        // Initialize all new cells
        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                // If the cell is within the old bounds, copy it
                if (y < height && x < width) {
                    newCells[y][x] = cells[y][x];
                } else {
                    // Otherwise create a new empty cell
                    newCells[y][x] = new MapCell();
                }
            }
        }
        
        // Update dimensions and cell array
        this.width = newWidth;
        this.height = newHeight;
        this.cells = newCells;
    }
    
    /**
     * Gets the cell at the specified coordinates.
     * 
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The MapCell at the specified coordinates, or null if out of bounds
     */
    public MapCell getCell(int x, int y) {
        if (isValidCoordinate(x, y)) {
            return cells[y][x];
        }
        return null;
    }
    
    /**
     * Sets the cell at the specified coordinates.
     * 
     * @param x The x coordinate
     * @param y The y coordinate
     * @param cell The MapCell to set at the specified coordinates
     * @return true if the cell was set, false if coordinates are out of bounds
     */
    public boolean setCell(int x, int y, MapCell cell) {
        if (isValidCoordinate(x, y)) {
            cells[y][x] = cell;
            return true;
        }
        return false;
    }
    
    /**
     * Sets the terrain for the cell at the specified coordinates.
     * 
     * @param x The x coordinate
     * @param y The y coordinate
     * @param terrain The terrain to set
     * @return true if the terrain was set, false if coordinates are out of bounds
     */
    public boolean setTerrain(int x, int y, Terrain terrain) {
        MapCell cell = getCell(x, y);
        if (cell != null) {
            cell.setTerrain(terrain);
            return true;
        }
        return false;
    }
    
    /**
     * Adds an entity to the cell at the specified coordinates.
     * 
     * @param x The x coordinate
     * @param y The y coordinate
     * @param entity The entity to add
     * @return true if the entity was added, false if coordinates are out of bounds
     */
    public boolean addEntity(int x, int y, ObjectInstance entity) {
        MapCell cell = getCell(x, y);
        if (cell != null) {
            cell.addEntity(entity);
            return true;
        }
        return false;
    }
    
    /**
     * Removes an entity from the cell at the specified coordinates.
     * 
     * @param x The x coordinate
     * @param y The y coordinate
     * @param entity The entity to remove
     * @return true if the entity was removed, false if coordinates are out of bounds or entity not found
     */
    public boolean removeEntity(int x, int y, ObjectInstance entity) {
        MapCell cell = getCell(x, y);
        if (cell != null) {
            return cell.removeEntity(entity);
        }
        return false;
    }
    
    /**
     * Fills the entire map with the specified terrain.
     * 
     * @param terrain The terrain to fill the map with
     */
    public void fillWithTerrain(Terrain terrain) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cells[y][x].setTerrain(terrain);
            }
        }
    }
    
    /**
     * Gets all cells that contain a specific type of entity.
     * 
     * @param entityClass The class of the entity to search for
     * @return A map of cell coordinates (as Point objects) to entities of the specified type
     */
    public Map<Point, List<ObjectInstance>> findEntitiesByType(Class<?> entityClass) {
        Map<Point, List<ObjectInstance>> result = new HashMap<>();
        
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                MapCell cell = cells[y][x];
                List<ObjectInstance> matchingEntities = new ArrayList<>();
                
                for (ObjectInstance entity : cell.getEntities()) {
                    if (entityClass.isInstance(entity)) {
                        matchingEntities.add(entity);
                    }
                }
                
                if (!matchingEntities.isEmpty()) {
                    result.put(new Point(x, y), matchingEntities);
                }
            }
        }
        
        return result;
    }
    
    /**
     * Checks if the given coordinates are valid for this map.
     * 
     * @param x The x coordinate
     * @param y The y coordinate
     * @return true if the coordinates are valid, false otherwise
     */
    public boolean isValidCoordinate(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }
    
    /**
     * Renders the entire map to the specified graphics context.
     * 
     * @param g The graphics context to render to
     * @param x The x coordinate at which to start rendering
     * @param y The y coordinate at which to start rendering
     * @param zoom The zoom level (1.0 = 100%)
     */
    public void render(Graphics g, int x, int y, float zoom) {
        int scaledCellSize = (int)(cellSize * zoom);
        
        for (int cellY = 0; cellY < height; cellY++) {
            for (int cellX = 0; cellX < width; cellX++) {
                // Calculate the position to render this cell
                int cellRenderX = x + (cellX * scaledCellSize);
                int cellRenderY = y + (cellY * scaledCellSize);
                
                // Render the cell
                cells[cellY][cellX].render(g, cellRenderX, cellRenderY, scaledCellSize);
            }
        }
    }
    
    // Implementation of ObjectInstance interface
    
    
    public String getName() {
        return name;
    }
    
    
    public void setName(String name) {
        this.name = name;
    }
    
    
    public String getDescription() {
        return description;
    }
    
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    
    public String getImagePath() {
        return imagePath;
    }
    
    
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    // Getters and setters
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public int getCellSize() {
        return cellSize;
    }
    
    public void setCellSize(int cellSize) {
        this.cellSize = cellSize;
    }
    
    /**
     * Gets a string representation of the map.
     * 
     * @return A string representation of the map
     */
    @Override
    public String toString() {
        return "GameMap [name=" + name + ", width=" + width + ", height=" + height + "]";
    }
}
