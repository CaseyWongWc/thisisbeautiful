package objectEditor.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Base class for all objects in the game.
 */
public abstract class ObjectInstance implements Serializable, Cloneable {
    private static final long serialVersionUID = 1L;
    
    private String id;
    private String type;
    private String name;
    private String description;
    private String imagePath;
    private Map<String, Object> properties;
    
    /**
     * Creates a new object instance with default values.
     */
    public ObjectInstance() {
        id = "";
        type = "";
        name = "";
        description = "";
        imagePath = "";
        properties = new HashMap<>();
    }
    
    /**
     * @return the ID of the object
     */
    public String getId() {
        return id;
    }
    
    /**
     * Sets the ID of the object.
     * 
     * @param id the ID to set
     */
    public void setId(String id) {
        this.id = id;
    }
    
    /**
     * @return the type of the object
     */
    public String getType() {
        return type;
    }
    
    /**
     * Sets the type of the object.
     * 
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }
    
    /**
     * @return the name of the object
     */
    public String getName() {
        return name;
    }
    
    /**
     * Sets the name of the object.
     * 
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }
    
    /**
     * @return the description of the object
     */
    public String getDescription() {
        return description;
    }
    
    /**
     * Sets the description of the object.
     * 
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }
    
    /**
     * @return the image path of the object
     */
    public String getImagePath() {
        return imagePath;
    }
    
    /**
     * Sets the image path of the object.
     * 
     * @param imagePath the image path to set
     */
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    /**
     * Sets a property value by name.
     * 
     * @param name the name of the property
     * @param value the value to set
     */
    public void setPropertyValue(String name, Object value) {
        switch (name) {
            case "id":
                setId((String) value);
                break;
            case "type":
                setType((String) value);
                break;
            case "name":
                setName((String) value);
                break;
            case "description":
                setDescription((String) value);
                break;
            case "imagePath":
                setImagePath((String) value);
                break;
            default:
                setProperty(name, value);
                break;
        }
    }
    
    /**
     * Sets a property in the properties map.
     * 
     * @param name the name of the property
     * @param value the value to set
     */
    protected void setProperty(String name, Object value) {
        if (properties == null) {
            properties = new HashMap<>();
        }
        properties.put(name, value);
    }
    
    /**
     * Gets a property from the properties map.
     * 
     * @param name the name of the property
     * @return the property value, or null if not found
     */
    protected Object getProperty(String name) {
        return properties != null ? properties.get(name) : null;
    }
    
    /**
     * Gets the object type for this instance.
     * 
     * @return the object type
     */
    public String getObjectType() {
        return type;
    }
    
    /**
     * Creates a deep copy of this object.
     * 
     * @return a clone of this object
     */
    @Override
    public Object clone() {
        try {
            ObjectInstance clone = (ObjectInstance) super.clone();
            
            // Perform a deep copy of mutable fields
            if (properties != null) {
                clone.properties = new HashMap<>(properties);
            }
            
            return clone;
        } catch (CloneNotSupportedException e) {
            // This should never happen since we implement Cloneable
            throw new RuntimeException("Clone not supported", e);
        }
    }
}
