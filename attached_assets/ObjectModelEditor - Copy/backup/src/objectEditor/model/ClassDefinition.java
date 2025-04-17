package objectEditor.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a class definition.
 */
public class ClassDefinition implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private String name;
    private String description;
    private List<Property> properties;
    
    /**
     * Creates a new class definition.
     * 
     * @param name the class name
     */
    public ClassDefinition(String name) {
        this.name = name;
        this.description = "";
        this.properties = new ArrayList<>();
    }
    
    /**
     * Gets the class name.
     * 
     * @return the class name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the class name.
     * 
     * @param name the class name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the class description.
     * 
     * @return the class description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the class description.
     * 
     * @param description the class description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Gets all properties.
     * 
     * @return a list of all properties
     */
    public List<Property> getProperties() {
        return new ArrayList<>(properties);
    }
    
    /**
     * Gets a property by name.
     * 
     * @param name the property name
     * @return the property, or null if not found
     */
    public Property getProperty(String name) {
        for (Property property : properties) {
            if (property.getName().equals(name)) {
                return property;
            }
        }
        return null;
    }
    
    /**
     * Adds a property.
     * 
     * @param property the property to add
     */
    public void addProperty(Property property) {
        properties.add(property);
    }
    
    /**
     * Removes a property.
     * 
     * @param property the property to remove
     */
    public void removeProperty(Property property) {
        properties.remove(property);
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return name;
    }
}
