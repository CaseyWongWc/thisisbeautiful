package objectEditor.model;

import java.io.Serializable;

/**
 * Represents a property definition.
 */
public class Property implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * Property types.
     */
    public enum Type {
        STRING,
        INTEGER,
        FLOAT,
        BOOLEAN,
        ENUM,
        IMAGE,
        REFERENCE
    }
    
    private String name;
    private Type type;
    private String description;
    private Object defaultValue;
    
    /**
     * Creates a new property.
     * 
     * @param name the property name
     * @param type the property type
     */
    public Property(String name, Type type) {
        this.name = name;
        this.type = type;
        this.description = "";
        this.defaultValue = getDefaultValueForType(type);
    }
    
    /**
     * Creates a new property with a description.
     * 
     * @param name the property name
     * @param type the property type
     * @param description the property description
     */
    public Property(String name, Type type, String description) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.defaultValue = getDefaultValueForType(type);
    }
    
    /**
     * Creates a new property with a default value.
     * 
     * @param name the property name
     * @param type the property type
     * @param defaultValue the property default value
     */
    public Property(String name, Type type, Object defaultValue) {
        this.name = name;
        this.type = type;
        this.description = "";
        this.defaultValue = defaultValue;
    }
    
    /**
     * Creates a new property with a description and default value.
     * 
     * @param name the property name
     * @param type the property type
     * @param description the property description
     * @param defaultValue the property default value
     */
    public Property(String name, Type type, String description, Object defaultValue) {
        this.name = name;
        this.type = type;
        this.description = description;
        this.defaultValue = defaultValue;
    }
    
    /**
     * Gets the property name.
     * 
     * @return the property name
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the property name.
     * 
     * @param name the property name
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * Gets the property type.
     * 
     * @return the property type
     */
    public Type getType() {
        return type;
    }
    
    /**
     * Sets the property type.
     * 
     * @param type the property type
     */
    public void setType(Type type) {
        this.type = type;
    }
    
    /**
     * Gets the property description.
     * 
     * @return the property description
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the property description.
     * 
     * @param description the property description
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * Gets the property default value.
     * 
     * @return the property default value
     */
    public Object getDefaultValue() {
        return defaultValue;
    }
    
    /**
     * Sets the property default value.
     * 
     * @param defaultValue the property default value
     */
    public void setDefaultValue(Object defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    /**
     * Gets the default value for a property type.
     * 
     * @param type the property type
     * @return the default value
     */
    private Object getDefaultValueForType(Type type) {
        switch (type) {
            case STRING:
                return "";
            case INTEGER:
                return 0;
            case FLOAT:
                return 0.0f;
            case BOOLEAN:
                return false;
            case ENUM:
                return "";
            case IMAGE:
                return "";
            case REFERENCE:
                return null;
            default:
                return null;
        }
    }
    
    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return name;
    }
}
