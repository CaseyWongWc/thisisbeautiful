package objectEditor.editor;

import objectEditor.model.ObjectInstance;

import javax.swing.*;
import java.util.List;

/**
 * Abstract base class for editor panels.
 */
public abstract class EditorPanel extends JPanel {
    
    private static final long serialVersionUID = 1L;
    
    private String objectType;
    
    /**
     * Creates a new editor panel.
     * 
     * @param objectType the type of object this panel edits
     */
    public EditorPanel(String objectType) {
        this.objectType = objectType;
        initializeUI();
    }
    
    /**
     * Initializes the UI components.
     */
    protected abstract void initializeUI();
    
    /**
     * Gets the object type this panel edits.
     * 
     * @return the object type
     */
    public String getObjectType() {
        return objectType;
    }
    
    /**
     * Adds a new object.
     * 
     * @param object the object to add
     * @return true if the object was added, false otherwise
     */
    public abstract boolean addObject(ObjectInstance object);
    
    /**
     * Updates an object.
     * 
     * @param object the object to update
     * @return true if the object was updated, false otherwise
     */
    public abstract boolean updateObject(ObjectInstance object);
    
    /**
     * Deletes an object.
     * 
     * @param object the object to delete
     * @return true if the object was deleted, false otherwise
     */
    public abstract boolean deleteObject(ObjectInstance object);
    
    /**
     * Gets all objects.
     * 
     * @return a list of objects
     */
    public abstract List<ObjectInstance> getAllObjects();
    
    /**
     * Gets an object by ID.
     * 
     * @param id the ID of the object
     * @return the object, or null if not found
     */
    public abstract ObjectInstance getObjectById(String id);
    
    /**
     * Saves all objects.
     * 
     * @return true if the objects were saved, false otherwise
     */
    public abstract boolean saveAll();
    
    /**
     * Loads objects from a file.
     * 
     * @param filePath the path to the file
     * @return true if the objects were loaded, false otherwise
     */
    public abstract boolean loadFromFile(String filePath);
    
    /**
     * Imports objects from a file.
     * 
     * @param filePath the path to the file
     * @return the number of objects imported
     */
    public abstract int importFromFile(String filePath);
    
    /**
     * Exports objects to a file.
     * 
     * @param objects the objects to export
     * @param filePath the path to the file
     * @return true if the objects were exported, false otherwise
     */
    public abstract boolean exportToFile(List<ObjectInstance> objects, String filePath);
    
    /**
     * Resets the editor to its default state.
     */
    public abstract void resetEditor();
    
    /**
     * Gets the selected object.
     * 
     * @return the selected object, or null if none is selected
     */
    public abstract ObjectInstance getSelectedObject();
    
    /**
     * Sets the selected object.
     * 
     * @param object the object to select, or null to clear the selection
     */
    public abstract void setSelectedObject(ObjectInstance object);
}
