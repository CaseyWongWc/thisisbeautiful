package objectEditor.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Registry for class definitions.
 */
public class ClassRegistry {
    private static ClassRegistry instance;
    
    private Map<String, ClassDefinition> classes;
    
    /**
     * Creates a new class registry.
     */
    private ClassRegistry() {
        classes = new HashMap<>();
        initializeDefaultClasses();
    }
    
    /**
     * Gets the singleton instance of the class registry.
     * 
     * @return the class registry instance
     */
    public static synchronized ClassRegistry getInstance() {
        if (instance == null) {
            instance = new ClassRegistry();
        }
        return instance;
    }
    
    /**
     * Initializes the default classes.
     */
    private void initializeDefaultClasses() {
        // Initialize Item class
        ClassDefinition itemClass = new ClassDefinition("Item");
        itemClass.setDescription("Represents an item in the game.");
        itemClass.addProperty(new Property("name", Property.Type.STRING));
        itemClass.addProperty(new Property("description", Property.Type.STRING));
        itemClass.addProperty(new Property("image", Property.Type.IMAGE));
        itemClass.addProperty(new Property("goldValue", Property.Type.INTEGER));
        itemClass.addProperty(new Property("foodValue", Property.Type.INTEGER));
        itemClass.addProperty(new Property("waterValue", Property.Type.INTEGER));
        itemClass.addProperty(new Property("difficulties", Property.Type.STRING));
        
        // Initialize Creature class
        ClassDefinition creatureClass = new ClassDefinition("Creature");
        creatureClass.setDescription("Represents a creature in the game.");
        creatureClass.addProperty(new Property("name", Property.Type.STRING));
        creatureClass.addProperty(new Property("description", Property.Type.STRING));
        creatureClass.addProperty(new Property("image", Property.Type.IMAGE));
        creatureClass.addProperty(new Property("health", Property.Type.INTEGER));
        creatureClass.addProperty(new Property("attack", Property.Type.INTEGER));
        creatureClass.addProperty(new Property("defense", Property.Type.INTEGER));
        creatureClass.addProperty(new Property("speed", Property.Type.INTEGER));
        creatureClass.addProperty(new Property("item", Property.Type.REFERENCE));
        
        // Register classes
        registerClassDefinition("Item", itemClass);
        registerClassDefinition("Creature", creatureClass);
    }
    
    /**
     * Registers a class definition.
     * 
     * @param name the class name
     * @param classDefinition the class definition
     */
    public void registerClassDefinition(String name, ClassDefinition classDefinition) {
        classes.put(name, classDefinition);
    }
    
    /**
     * Gets a class definition by name.
     * 
     * @param name the class name
     * @return the class definition, or null if not found
     */
    public ClassDefinition getClassDefinition(String name) {
        return classes.get(name);
    }
    
    /**
     * Gets all class definitions.
     * 
     * @return a list of all class definitions
     */
    public List<ClassDefinition> getAllClassDefinitions() {
        return new ArrayList<>(classes.values());
    }
}
