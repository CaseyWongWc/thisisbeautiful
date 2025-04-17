package objectEditor.model;

import objectEditor.util.ErrorLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A Terrain class representing a single cell on a game map.
 * It can contain collections of game objects such as spawners, traders, creatures, items,
 * and player actors (which will be implemented in the future).
 */
public class Terrain extends ObjectInstance {
    // Collections of contained objects
    private List<Spawner> spawners;
    private List<Trader> traders;
    private List<Creature> creatures;
    private List<Item> items;
    
    // This will be implemented later
    // private List<PlayerActor> playerActors;
    
    // Map position information
    private int x;
    private int y;
    private int movementCost;
    private int strengthCost;
    private int thirstCost;
    private int goldCost;
    
    // Used for optimized object comparison
    private int lastHashCode;
    
    /**
     * Create a new terrain cell with default values
     */
    public Terrain() {
        super();
        this.spawners = new ArrayList<>();
        this.traders = new ArrayList<>();
        this.creatures = new ArrayList<>();
        this.items = new ArrayList<>();
        // this.playerActors = new ArrayList<>(); // Will be implemented later
        this.x = 0;
        this.y = 0;
        this.movementCost = 0;
        this.strengthCost = 0;
        this.thirstCost = 0;
        this.goldCost = 0;
        updateHashCode();
    }
    
    /**
     * Create a terrain cell with specific parameters
     */
    public Terrain(String name, String description, String imagePath, int x, int y) {
        super();
        setName(name);
        setDescription(description);
        setImagePath(imagePath);
        this.spawners = new ArrayList<>();
        this.traders = new ArrayList<>();
        this.creatures = new ArrayList<>();
        this.items = new ArrayList<>();
        // this.playerActors = new ArrayList<>(); // Will be implemented later
        this.x = x;
        this.y = y;
        updateHashCode();
    }
    
    // Getters and setters for position
    
    public int getX() {
        return x;
    }
    
    public void setX(int x) {
        this.x = x;
        updateHashCode();
    }
    
    public int getY() {
        return y;
    }
    
    public void setY(int y) {
        this.y = y;
        updateHashCode();
    }
    
    public int getMovementCost() {
        return movementCost;
    }
    
    public void setMovementCost(int movementCost) {
        this.movementCost = movementCost;
        updateHashCode();
    }
    
    public int getStrengthCost() {
        return strengthCost;
    }
    
    public void setStrengthCost(int strengthCost) {
        this.strengthCost = strengthCost;
        updateHashCode();
    }
    
    public int getThirstCost() {
        return thirstCost;
    }
    
    public void setThirstCost(int thirstCost) {
        this.thirstCost = thirstCost;
        updateHashCode();
    }
    
    public int getGoldCost() {
        return goldCost;
    }
    
    public void setGoldCost(int goldCost) {
        this.goldCost = goldCost;
        updateHashCode();
    }
    
    /**
     * Sets the grid X position (alias for setX for Map compatibility)
     * @param x The X coordinate
     */
    public void setGridX(int x) {
        setX(x);
    }
    
    /**
     * Sets the grid Y position (alias for setY for Map compatibility)
     * @param y The Y coordinate
     */
    public void setGridY(int y) {
        setY(y);
    }
    
    // Collection getters
    
    public List<Spawner> getSpawners() {
        return spawners;
    }
    
    public List<Trader> getTraders() {
        return traders;
    }
    
    public List<Creature> getCreatures() {
        return creatures;
    }
    
    public List<Item> getItems() {
        return items;
    }
    
    /* 
     * This will be implemented in the future
     * 
     * public List<PlayerActor> getPlayerActors() {
     *     return playerActors;
     * }
     */
    
    // Methods to add objects to the terrain
    
    /**
     * Add a spawner to this terrain cell
     * @param spawner The spawner to add
     * @return true if added successfully
     */
    public boolean addSpawner(Spawner spawner) {
        if (spawner != null && !spawners.contains(spawner)) {
            return spawners.add(spawner);
        }
        return false;
    }
    
    /**
     * Add a trader to this terrain cell
     * @param trader The trader to add
     * @return true if added successfully
     */
    public boolean addTrader(Trader trader) {
        if (trader != null && !traders.contains(trader)) {
            return traders.add(trader);
        }
        return false;
    }
    
    /**
     * Add a creature to this terrain cell
     * @param creature The creature to add
     * @return true if added successfully
     */
    public boolean addCreature(Creature creature) {
        if (creature != null && !creatures.contains(creature)) {
            return creatures.add(creature);
        }
        return false;
    }
    
    /**
     * Add an item to this terrain cell
     * @param item The item to add
     * @return true if added successfully
     */
    public boolean addItem(Item item) {
        if (item != null && !items.contains(item)) {
            return items.add(item);
        }
        return false;
    }
    
    /* 
     * This will be implemented in the future
     * 
     * public boolean addPlayerActor(PlayerActor player) {
     *     if (player != null && !playerActors.contains(player)) {
     *         return playerActors.add(player);
     *     }
     *     return false;
     * }
     */
    
    // Methods to remove objects from the terrain
    
    /**
     * Remove a spawner from this terrain cell
     * @param spawner The spawner to remove
     * @return true if removed successfully
     */
    public boolean removeSpawner(Spawner spawner) {
        return spawners.remove(spawner);
    }
    
    /**
     * Remove a trader from this terrain cell
     * @param trader The trader to remove
     * @return true if removed successfully
     */
    public boolean removeTrader(Trader trader) {
        return traders.remove(trader);
    }
    
    /**
     * Remove a creature from this terrain cell
     * @param creature The creature to remove
     * @return true if removed successfully
     */
    public boolean removeCreature(Creature creature) {
        return creatures.remove(creature);
    }
    
    /**
     * Remove an item from this terrain cell
     * @param item The item to remove
     * @return true if removed successfully
     */
    public boolean removeItem(Item item) {
        return items.remove(item);
    }
    
    /* 
     * This will be implemented in the future
     * 
     * public boolean removePlayerActor(PlayerActor player) {
     *     return playerActors.remove(player);
     * }
     */
    
    /**
     * Process a turn for all contained objects that support turn processing
     */
    public void processTurn() {
        try {
            // Process spawners
            for (Spawner spawner : new ArrayList<>(spawners)) {
                List<ObjectInstance> newObjects = spawner.processTurn();
                for (ObjectInstance obj : newObjects) {
                    // Add the newly spawned objects to the appropriate collections
                    if (obj instanceof Item) {
                        addItem((Item) obj);
                    } else if (obj instanceof Creature) {
                        addCreature((Creature) obj);
                    } else if (obj instanceof Trader) {
                        addTrader((Trader) obj);
                    }
                }
            }
            
            // Process other object types if they have turn-based logic
            // For example, creatures might move or attack
            
            // TO DO: Add turn processing for other object types as needed
        } catch (Exception e) {
            ErrorLogger.logError("Error processing turn for terrain: " + getName(), e);
        }
    }
    
    /**
     * Clear all contained objects from this terrain cell
     */
    public void clearAll() {
        spawners.clear();
        traders.clear();
        creatures.clear();
        items.clear();
        // playerActors.clear(); // Will be implemented later
    }
    
    /**
     * Updates the hash code for efficient comparison
     */
    private void updateHashCode() {
        lastHashCode = Objects.hash(
            getName(),
            getDescription(),
            getImagePath(),
            x,
            y,
            movementCost,
            strengthCost,
            thirstCost,
            goldCost
            // We don't include the collections in the hash code 
            // as they can change frequently
        );
    }
    
    @Override
    public int hashCode() {
        return lastHashCode;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        
        Terrain other = (Terrain) obj;
        return hashCode() == other.hashCode() &&
               Objects.equals(getName(), other.getName()) &&
               Objects.equals(getDescription(), other.getDescription()) &&
               Objects.equals(getImagePath(), other.getImagePath()) &&
               x == other.x &&
               y == other.y;
               // We don't include the collections in equals
               // Only the core properties of the terrain cell
    }
    
    @Override
    public String toString() {
        return "Terrain: " + getName() + 
               " (Position: " + x + "," + y + 
               ", Spawners: " + spawners.size() + 
               ", Traders: " + traders.size() + 
               ", Creatures: " + creatures.size() + 
               ", Items: " + items.size() + ")";
    }
    
    @Override
    public Object clone() {
        Terrain clone = (Terrain) super.clone();
        
        // Create new collections for the clone
        clone.spawners = new ArrayList<>(spawners);
        clone.traders = new ArrayList<>(traders);
        clone.creatures = new ArrayList<>(creatures);
        clone.items = new ArrayList<>(items);
        // clone.playerActors = new ArrayList<>(playerActors); // Will be implemented later
        
        return clone;
    }
}
