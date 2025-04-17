package objectEditor.model;

import objectEditor.util.ErrorLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * A Spawner class that can create and manage instances of various game objects
 * (items, creatures, traders) with configurable spawn settings.
 */
public class Spawner extends ObjectInstance {
    // Basic spawner properties
    private int maxSpawnCap;
    private int spawnFrequency;
    private boolean isDirected;
    private String direction; // N, NE, NW, SE, SW, E, W, S, none
    private boolean randomOrientation;
    
    // What type of object this spawns
    private String objectType; // "item", "creature", "trader"
    private String objectTemplate; // Name of the template object to spawn
    
    // Current spawned objects
    private List<ObjectInstance> spawnedObjects;
    private int turnCounter;
    
    // Used for optimized object comparison
    private int lastHashCode;
    
    /**
     * Create a new spawner
     */
    public Spawner() {
        super();
        this.maxSpawnCap = 1;
        this.spawnFrequency = 1;
        this.isDirected = false;
        this.direction = "none";
        this.randomOrientation = false;
        this.objectType = "item";
        this.objectTemplate = "";
        this.spawnedObjects = new ArrayList<>();
        this.turnCounter = 0;
        updateHashCode();
    }
    
    /**
     * Create a spawner with all parameters
     */
    public Spawner(String name, String description, String imagePath, 
                  int maxSpawnCap, int spawnFrequency, boolean isDirected,
                  String direction, boolean randomOrientation,
                  String objectType, String objectTemplate) {
        super();
        setName(name);
        setDescription(description);
        setImagePath(imagePath);
        this.maxSpawnCap = maxSpawnCap;
        this.spawnFrequency = spawnFrequency;
        this.isDirected = isDirected;
        this.direction = direction;
        this.randomOrientation = randomOrientation;
        this.objectType = objectType;
        this.objectTemplate = objectTemplate;
        this.spawnedObjects = new ArrayList<>();
        this.turnCounter = 0;
        updateHashCode();
    }
    
    // Getters and setters
    public int getMaxSpawnCap() {
        return maxSpawnCap;
    }
    
    public void setMaxSpawnCap(int maxSpawnCap) {
        this.maxSpawnCap = maxSpawnCap;
        updateHashCode();
    }
    
    public int getSpawnFrequency() {
        return spawnFrequency;
    }
    
    public void setSpawnFrequency(int spawnFrequency) {
        this.spawnFrequency = spawnFrequency;
        updateHashCode();
    }
    
    public boolean isDirected() {
        return isDirected;
    }
    
    public void setDirected(boolean directed) {
        isDirected = directed;
        updateHashCode();
    }
    
    public String getDirection() {
        return direction;
    }
    
    public void setDirection(String direction) {
        this.direction = direction != null ? direction : "none";
        updateHashCode();
    }
    
    public boolean isRandomOrientation() {
        return randomOrientation;
    }
    
    public void setRandomOrientation(boolean randomOrientation) {
        this.randomOrientation = randomOrientation;
        updateHashCode();
    }
    
    public String getObjectType() {
        return objectType;
    }
    
    public void setObjectType(String objectType) {
        this.objectType = objectType != null ? objectType : "item";
        updateHashCode();
    }
    
    public String getObjectTemplate() {
        return objectTemplate;
    }
    
    public void setObjectTemplate(String objectTemplate) {
        this.objectTemplate = objectTemplate != null ? objectTemplate : "";
        updateHashCode();
    }
    
    public List<ObjectInstance> getSpawnedObjects() {
        return spawnedObjects;
    }
    
    /**
     * Process a new turn, possibly spawning new objects
     * @return Any newly spawned objects
     */
    public List<ObjectInstance> processTurn() {
        List<ObjectInstance> newlySpawned = new ArrayList<>();
        
        // Increment turn counter
        turnCounter++;
        
        // Check if it's time to spawn
        if (turnCounter >= spawnFrequency && spawnedObjects.size() < maxSpawnCap) {
            // Create a new instance based on template
            ObjectInstance newObject = createObjectFromTemplate();
            
            if (newObject != null) {
                spawnedObjects.add(newObject);
                newlySpawned.add(newObject);
                
                // Reset turn counter
                turnCounter = 0;
            }
        }
        
        // If randomOrientation is enabled, update directions of spawned objects
        if (randomOrientation) {
            updateObjectOrientations();
        }
        
        return newlySpawned;
    }
    
    /**
     * Create a new object based on the template
     */
    private ObjectInstance createObjectFromTemplate() {
        try {
            // In a real implementation, this would look up the template
            // and create a proper instance
            
            // For now, just create a basic instance based on the object type
            switch (objectType.toLowerCase()) {
                case "item":
                    Item item = new Item();
                    item.setName(objectTemplate + " (spawned)");
                    item.setDescription("Spawned by " + getName());
                    return item;
                    
                case "creature":
                    Creature creature = new Creature();
                    creature.setName(objectTemplate + " (spawned)");
                    creature.setDescription("Spawned by " + getName());
                    return creature;
                    
                case "trader":
                    Trader trader = new Trader();
                    trader.setName(objectTemplate + " (spawned)");
                    trader.setDescription("Spawned by " + getName());
                    return trader;
                    
                default:
                    return null;
            }
        } catch (Exception e) {
            ErrorLogger.logError("Error creating object from template: " + objectTemplate, e);
            return null;
        }
    }
    
    /**
     * Updates the orientation/direction of all spawned movement objects
     * if they support it
     */
    private void updateObjectOrientations() {
        try {
            // This would update movement patterns or orientations
            // of spawned objects that support it
            // Implementation depends on how movement is handled
            
            // For now just a placeholder
            String[] directions = {"N", "NE", "E", "SE", "S", "SW", "W", "NW"};
            int randomIndex = (int) (Math.random() * directions.length);
            String newDirection = directions[randomIndex];
            
            // For example:
            for (ObjectInstance obj : spawnedObjects) {
                // If the object has movement capabilities, update its direction
                if (obj instanceof Creature) {
                    Creature creature = (Creature) obj;
                    // This would set the direction if the creature has movement properties
                    // creature.setMovementDirection(newDirection);
                }
            }
        } catch (Exception e) {
            ErrorLogger.logError("Error updating object orientations", e);
        }
    }
    
    /**
     * Remove an object from the spawned list
     */
    public void removeSpawnedObject(ObjectInstance object) {
        if (object != null) {
            spawnedObjects.remove(object);
        }
    }
    
    /**
     * Clear all spawned objects
     */
    public void clearSpawnedObjects() {
        spawnedObjects.clear();
    }
    
    /**
     * Updates the hash code for efficient comparison
     */
    private void updateHashCode() {
        lastHashCode = Objects.hash(
            getName(),
            getDescription(),
            getImagePath(),
            maxSpawnCap,
            spawnFrequency,
            isDirected,
            direction,
            randomOrientation,
            objectType,
            objectTemplate
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
        
        Spawner other = (Spawner) obj;
        return hashCode() == other.hashCode() &&
               Objects.equals(getName(), other.getName()) &&
               Objects.equals(getDescription(), other.getDescription()) &&
               Objects.equals(getImagePath(), other.getImagePath()) &&
               maxSpawnCap == other.maxSpawnCap &&
               spawnFrequency == other.spawnFrequency &&
               isDirected == other.isDirected &&
               Objects.equals(direction, other.direction) &&
               randomOrientation == other.randomOrientation &&
               Objects.equals(objectType, other.objectType) &&
               Objects.equals(objectTemplate, other.objectTemplate);
    }
    
    @Override
    public String toString() {
        return "Spawner: " + getName() + 
               " (Type: " + objectType + 
               ", Template: " + objectTemplate + 
               ", Max: " + maxSpawnCap + 
               ", Frequency: " + spawnFrequency + ")";
    }
}
