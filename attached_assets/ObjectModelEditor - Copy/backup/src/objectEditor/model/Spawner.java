package objectEditor.model;

import java.util.ArrayList;
import java.util.List;

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
    }
    
    // Getters and setters
    public int getMaxSpawnCap() {
        return maxSpawnCap;
    }
    
    public void setMaxSpawnCap(int maxSpawnCap) {
        this.maxSpawnCap = maxSpawnCap;
    }
    
    public int getSpawnFrequency() {
        return spawnFrequency;
    }
    
    public void setSpawnFrequency(int spawnFrequency) {
        this.spawnFrequency = spawnFrequency;
    }
    
    public boolean isDirected() {
        return isDirected;
    }
    
    public void setDirected(boolean directed) {
        isDirected = directed;
    }
    
    public String getDirection() {
        return direction;
    }
    
    public void setDirection(String direction) {
        this.direction = direction;
    }
    
    public boolean isRandomOrientation() {
        return randomOrientation;
    }
    
    public void setRandomOrientation(boolean randomOrientation) {
        this.randomOrientation = randomOrientation;
    }
    
    public String getObjectType() {
        return objectType;
    }
    
    public void setObjectType(String objectType) {
        this.objectType = objectType;
    }
    
    public String getObjectTemplate() {
        return objectTemplate;
    }
    
    public void setObjectTemplate(String objectTemplate) {
        this.objectTemplate = objectTemplate;
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
    }
    
    /**
     * Updates the orientation/direction of all spawned movement objects
     * if they support it
     */
    private void updateObjectOrientations() {
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
    }
    
    /**
     * Remove an object from the spawned list
     */
    public void removeSpawnedObject(ObjectInstance object) {
        spawnedObjects.remove(object);
    }
    
    /**
     * Clear all spawned objects
     */
    public void clearSpawnedObjects() {
        spawnedObjects.clear();
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
