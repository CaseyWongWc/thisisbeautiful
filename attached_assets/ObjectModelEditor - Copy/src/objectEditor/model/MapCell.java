package objectEditor.model;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import objectEditor.util.ImageUtils;

/**
 * Represents a single cell in a game map.
 * Each cell has a terrain background and can contain multiple game entities.
 * The cell dynamically adjusts its visual grid based on the number of entities.
 */
public class MapCell {
    private Terrain terrain;
    private List<ObjectInstance> entities;
    private int x;
    private int y;
    
    /**
     * Creates a new empty map cell.
     */
    public MapCell() {
        this.terrain = null;
        this.entities = new ArrayList<>();
        this.x = 0;
        this.y = 0;
    }
    
    /**
     * Creates a new map cell with specified terrain and coordinates.
     * 
     * @param terrain The terrain for this cell
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public MapCell(Terrain terrain, int x, int y) {
        this.terrain = terrain;
        this.entities = new ArrayList<>();
        this.x = x;
        this.y = y;
    }
    
    /**
     * Gets the terrain for this cell.
     * 
     * @return The terrain
     */
    public Terrain getTerrain() {
        return terrain;
    }
    
    /**
     * Sets the terrain for this cell.
     * 
     * @param terrain The terrain to set
     */
    public void setTerrain(Terrain terrain) {
        this.terrain = terrain;
    }
    
    /**
     * Gets the entities in this cell.
     * 
     * @return The list of entities
     */
    public List<ObjectInstance> getEntities() {
        return new ArrayList<>(entities);
    }
    
    /**
     * Gets the x coordinate of this cell.
     * 
     * @return The x coordinate
     */
    public int getX() {
        return x;
    }
    
    /**
     * Sets the x coordinate of this cell.
     * 
     * @param x The x coordinate to set
     */
    public void setX(int x) {
        this.x = x;
    }
    
    /**
     * Gets the y coordinate of this cell.
     * 
     * @return The y coordinate
     */
    public int getY() {
        return y;
    }
    
    /**
     * Sets the y coordinate of this cell.
     * 
     * @param y The y coordinate to set
     */
    public void setY(int y) {
        this.y = y;
    }
    
    /**
     * Adds an entity to this cell.
     * Always adds the entity, even if an entity with the same name/type already exists.
     * 
     * @param entity The entity to add
     */
    public void addEntity(ObjectInstance entity) {
        if (entity != null) {
            entities.add(entity);
        }
    }
    
    /**
     * Removes an entity from this cell.
     * 
     * @param entity The entity to remove
     * @return true if the entity was removed, false otherwise
     */
    public boolean removeEntity(ObjectInstance entity) {
        return entities.remove(entity);
    }
    
    /**
     * Removes all entities from this cell.
     */
    public void clearEntities() {
        entities.clear();
    }
    
    /**
     * Gets the number of entities in this cell.
     * 
     * @return The number of entities
     */
    public int getEntityCount() {
        return entities.size();
    }
    
    /**
     * Calculates the grid dimensions based on the number of entities.
     * 1-2 entities: 1x2 grid
     * 3-4 entities: 2x2 grid
     * 5+ entities: 3x2 grid
     * 
     * @return The dimensions of the grid
     */
    public Dimension calculateGridSize() {
        int entityCount = entities.size();
        
        if (entityCount <= 0) {
            return new Dimension(1, 1);  // Just terrain
        } else if (entityCount <= 2) {
            return new Dimension(1, 2);  // 1x2 grid
        } else if (entityCount <= 4) {
            return new Dimension(2, 2);  // 2x2 grid
        } else {
            return new Dimension(3, 2);  // 3x2 grid for 5+ entities
        }
    }
    
    /**
     * Renders this cell to the provided graphics context.
     * 
     * @param g The graphics context
     * @param x The x position to render at (in pixels)
     * @param y The y position to render at (in pixels)
     * @param cellSize The size of the cell (width and height in pixels)
     */
    public void render(Graphics g, int x, int y, int cellSize) {
        // Draw terrain as background
        if (terrain != null && terrain.getImagePath() != null) {
            String imagePath = terrain.getImagePath();
            
            // Handle potential null imagePath
            if (imagePath != null && !imagePath.isEmpty()) {
                ImageIcon terrainIcon = ImageUtils.loadImageIcon(imagePath);
                
                if (terrainIcon != null && terrainIcon.getIconWidth() > 0) {
                    g.drawImage(terrainIcon.getImage(), x, y, cellSize, cellSize, null);
                } else {
                    // Fallback if image loading fails
                    g.setColor(Color.GRAY);
                    g.fillRect(x, y, cellSize, cellSize);
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y, cellSize, cellSize);
                    g.setColor(Color.BLACK);
                    g.setFont(new Font("Arial", Font.PLAIN, 10));
                    drawMultiLineText(g, "Terrain: " + terrain.getName(), x + 5, y + 20, 10);
                }
            } else {
                // Fallback if no image path
                g.setColor(Color.LIGHT_GRAY);
                g.fillRect(x, y, cellSize, cellSize);
                g.setColor(Color.BLACK);
                g.drawRect(x, y, cellSize, cellSize);
                g.setFont(new Font("Arial", Font.PLAIN, 10));
                drawMultiLineText(g, "Terrain: " + terrain.getName(), x + 5, y + 20, 10);
            }
        } else {
            // No terrain - draw empty cell
            g.setColor(Color.LIGHT_GRAY);
            g.fillRect(x, y, cellSize, cellSize);
            g.setColor(Color.BLACK);
            g.drawRect(x, y, cellSize, cellSize);
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            drawMultiLineText(g, "No Terrain", x + 5, y + 20, 10);
        }
        
        // Skip entity rendering if no entities
        if (entities.isEmpty()) {
            return;
        }
        
        // Calculate grid layout
        Dimension gridSize = calculateGridSize();
        int gridWidth = gridSize.width;
        int gridHeight = gridSize.height;
        
        // Calculate size of each grid slot
        int slotWidth = cellSize / gridWidth;
        int slotHeight = cellSize / gridHeight;
        
        // Render entities in grid slots
        for (int i = 0; i < Math.min(entities.size(), gridWidth * gridHeight); i++) {
            ObjectInstance entity = entities.get(i);
            
            // Skip null entities
            if (entity == null) {
                continue;
            }
            
            // Calculate position in grid
            int row = i / gridWidth;
            int col = i % gridWidth;
            
            // Calculate pixel position
            int entityX = x + (col * slotWidth);
            int entityY = y + (row * slotHeight);
            
            // Draw entity icon
            drawEntityIcon(g, entityX, entityY, slotWidth, slotHeight, entity);
        }
        
        // Extra entities indicator if needed
        if (entities.size() > gridWidth * gridHeight) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("Arial", Font.BOLD, 10));
            g.drawString("+" + (entities.size() - (gridWidth * gridHeight)), 
                     x + cellSize - 15, y + cellSize - 5);
        }
    }
    
    /**
     * Draws an entity icon at the specified position.
     * 
     * @param g The graphics context
     * @param x The x position (in pixels)
     * @param y The y position (in pixels)
     * @param width The width of the icon
     * @param height The height of the icon
     * @param entity The entity to draw
     */
    private void drawEntityIcon(Graphics g, int x, int y, int width, int height, ObjectInstance entity) {
        if (entity == null) {
            return;
        }
        
        String imagePath = entity.getImagePath();
        if (imagePath != null && !imagePath.isEmpty()) {
            ImageIcon icon = ImageUtils.loadImageIcon(imagePath);
            
            if (icon != null && icon.getIconWidth() > 0) {
                // Draw a slightly smaller image to have a border
                int padding = 1;
                g.drawImage(icon.getImage(), 
                         x + padding, 
                         y + padding, 
                         width - (padding * 2), 
                         height - (padding * 2), 
                         null);
                
                // Draw a border around the entity
                g.setColor(Color.BLACK);
                g.drawRect(x + padding, y + padding, width - (padding * 2) - 1, height - (padding * 2) - 1);
            } else {
                // Fallback if image loading fails
                drawEntityFallback(g, x, y, width, height, entity);
            }
        } else {
            // No image path - draw fallback
            drawEntityFallback(g, x, y, width, height, entity);
        }
    }
    
    /**
     * Draws a fallback representation of an entity.
     * 
     * @param g The graphics context
     * @param x The x position (in pixels)
     * @param y The y position (in pixels)
     * @param width The width of the icon
     * @param height The height of the icon
     * @param entity The entity to draw
     */
    private void drawEntityFallback(Graphics g, int x, int y, int width, int height, ObjectInstance entity) {
        // Draw a colored rectangle with the entity type
        if (entity instanceof Item) {
            g.setColor(new Color(255, 220, 150)); // Light orange for items
        } else if (entity instanceof Creature) {
            g.setColor(new Color(255, 150, 150)); // Light red for creatures
        } else if (entity instanceof Trader) {
            g.setColor(new Color(150, 255, 150)); // Light green for traders
        } else if (entity instanceof Spawner) {
            g.setColor(new Color(150, 150, 255)); // Light blue for spawners
        } else {
            g.setColor(Color.LIGHT_GRAY);
        }
        
        g.fillRect(x, y, width, height);
        g.setColor(Color.BLACK);
        g.drawRect(x, y, width - 1, height - 1);
        
        // Draw the entity name (shortened if needed)
        String name = entity.getName();
        if (name != null && !name.isEmpty()) {
            g.setFont(new Font("Arial", Font.PLAIN, 9));
            
            // Shorten name if too long
            if (name.length() > 10) {
                name = name.substring(0, 8) + "..";
            }
            
            // Calculate text position to center it
            FontMetrics fm = g.getFontMetrics();
            int textX = x + (width - fm.stringWidth(name)) / 2;
            int textY = y + (height + fm.getAscent()) / 2;
            
            g.drawString(name, textX, textY);
        }
    }
    
    /**
     * Draws multi-line text at the specified position.
     * Breaks text at spaces if it exceeds a fixed width.
     * 
     * @param g The graphics context
     * @param text The text to draw
     * @param x The x position to start drawing
     * @param y The y position to start drawing
     * @param lineHeight The height of each line
     */
    private void drawMultiLineText(Graphics g, String text, int x, int y, int lineHeight) {
        if (text == null || text.isEmpty()) {
            return;
        }
        
        FontMetrics metrics = g.getFontMetrics();
        int maxWidth = 80; // Fixed max width for text wrapping
        
        String[] words = text.split(" ");
        StringBuilder currentLine = new StringBuilder();
        int currentY = y;
        
        for (String word : words) {
            String testLine = currentLine.toString();
            if (!testLine.isEmpty()) {
                testLine += " ";
            }
            testLine += word;
            
            if (metrics.stringWidth(testLine) <= maxWidth) {
                currentLine.append(currentLine.length() > 0 ? " " : "").append(word);
            } else {
                if (currentLine.length() > 0) {
                    g.drawString(currentLine.toString(), x, currentY);
                    currentY += lineHeight;
                    currentLine = new StringBuilder(word);
                } else {
                    // Word is too long, draw it anyway
                    g.drawString(word, x, currentY);
                    currentY += lineHeight;
                    currentLine = new StringBuilder();
                }
            }
        }
        
        // Draw the last line
        if (currentLine.length() > 0) {
            g.drawString(currentLine.toString(), x, currentY);
        }
    }
}
