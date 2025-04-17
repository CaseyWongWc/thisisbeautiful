package objectEditor.controller;

import objectEditor.model.ClassDefinition;
import objectEditor.model.ObjectInstance;
import objectEditor.model.Property;
import objectEditor.model.PropertyType;
import objectEditor.view.EditorFrame;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controller for the object editor application.
 */
public class EditorController {
    private EditorFrame view;
    private List<ClassDefinition> classes;
    private List<ObjectInstance> objects;
    private ClassDefinition selectedClass;
    private ObjectInstance selectedObject;
    private FileController fileController;
    private File currentFile;
    private boolean modified;
    
    /**
     * Creates a new editor controller with the specified view.
     * 
     * @param view the view
     */
    public EditorController(EditorFrame view) {
        this.view = view;
        this.classes = new ArrayList<>();
        this.objects = new ArrayList<>();
        this.fileController = new FileController();
        this.modified = false;
        
        // Initialize view with controller
        view.initializePanelControllers();
        
        // Create example classes as requested
        createExampleClasses();
        
        // Update UI
        updateClassList();
        updateObjectList();
    }
    
    /**
     * Creates example classes with the names "class one" and "class two".
     */
    private void createExampleClasses() {
        // Create Class One
        ClassDefinition classOne = new ClassDefinition("class one");
        classOne.addProperty(new Property("name", PropertyType.STRING));
        classOne.addProperty(new Property("value", PropertyType.INTEGER));
        classOne.addProperty(new Property("enabled", PropertyType.BOOLEAN));
        
        // Create Class Two
        ClassDefinition classTwo = new ClassDefinition("class two");
        classTwo.addProperty(new Property("title", PropertyType.STRING));
        classTwo.addProperty(new Property("description", PropertyType.STRING));
        classTwo.addProperty(new Property("priority", PropertyType.INTEGER));
        
        // Add reference from class one to class two
        classOne.addProperty(new Property("reference", classTwo));
        
        // Add classes
        classes.add(classOne);
        classes.add(classTwo);
        
        // Set initial selected class
        selectedClass = classOne;
    }
    
    /**
     * Updates the class list in the view.
     */
    private void updateClassList() {
        view.getClassPanel().updateClassList(classes);
        view.getClassPanel().setSelectedClass(selectedClass);
    }
    
    /**
     * Updates the object list in the view for the selected class.
     */
    private void updateObjectList() {
        List<ObjectInstance> classObjects = new ArrayList<>();
        
        if (selectedClass != null) {
            for (ObjectInstance obj : objects) {
                if (obj.getClassDefinition().equals(selectedClass)) {
                    classObjects.add(obj);
                }
            }
        }
        
        view.getObjectPanel().updateObjectList(classObjects);
    }
    
    /**
     * Sets the selected class.
     * 
     * @param classDefinition the class to select
     */
    public void setSelectedClass(ClassDefinition classDefinition) {
        this.selectedClass = classDefinition;
        view.getObjectPanel().classSelected(classDefinition);
        updateObjectList();
    }
    
    /**
     * Gets the selected class.
     * 
     * @return the selected class
     */
    public ClassDefinition getSelectedClass() {
        return selectedClass;
    }
    
    /**
     * Sets the selected object.
     * 
     * @param objectInstance the object to select
     */
    public void setSelectedObject(ObjectInstance objectInstance) {
        this.selectedObject = objectInstance;
        
        if (objectInstance != null) {
            // Update property panel
            view.getPropertyPanel().setObject(objectInstance);
            
            // Update workspace panel
            view.getWorkspacePanel().setSelectedObject(objectInstance);
            
            // Update info text
            updateInfoText(objectInstance);
        } else {
            view.getPropertyPanel().setObject(null);
            view.getInfoTextArea().setText("");
        }
    }
    
    /**
     * Updates the information text for the selected object.
     */
    private void updateInfoText(ObjectInstance obj) {
        StringBuilder sb = new StringBuilder();
        sb.append("Selected Object: ").append(obj.getName()).append("\n");
        sb.append("Class: ").append(obj.getClassDefinition().getName()).append("\n\n");
        sb.append("Properties:\n");
        
        Map<String, Object> propertyValues = obj.getPropertyValues();
        for (Map.Entry<String, Object> entry : propertyValues.entrySet()) {
            sb.append(entry.getKey()).append(": ");
            Object value = entry.getValue();
            if (value instanceof ObjectInstance) {
                sb.append(((ObjectInstance) value).getName());
            } else {
                sb.append(value);
            }
            sb.append("\n");
        }
        
        view.getInfoTextArea().setText(sb.toString());
    }
    
    /**
     * Gets the selected object.
     * 
     * @return the selected object
     */
    public ObjectInstance getSelectedObject() {
        return selectedObject;
    }
    
    /**
     * Creates a new object of the selected class.
     */
    public void createNewObject() {
        if (selectedClass == null) {
            return;
        }
        
        // Create new object
        ObjectInstance newObject = selectedClass.createInstance();
        objects.add(newObject);
        
        // Update UI
        updateObjectList();
        setSelectedObject(newObject);
        setModified(true);
    }
    
    /**
     * Updates the properties of an object.
     * 
     * @param objectInstance the object to update
     * @param updatedValues the updated property values
     */
    public void updateObjectProperties(ObjectInstance objectInstance, Map<String, Object> updatedValues) {
        for (Map.Entry<String, Object> entry : updatedValues.entrySet()) {
            objectInstance.setPropertyValue(entry.getKey(), entry.getValue());
        }
        
        // Update UI
        updateObjectList();
        view.getWorkspacePanel().repaint();
        updateInfoText(objectInstance);
        setModified(true);
    }
    
    /**
     * Edits the properties of an object.
     * 
     * @param objectInstance the object to edit
     */
    public void editObjectProperties(ObjectInstance objectInstance) {
        if (objectInstance == null) {
            return;
        }
        
        setSelectedObject(objectInstance);
    }
    
    /**
     * Deletes an object.
     * 
     * @param objectInstance the object to delete
     */
    public void deleteObject(ObjectInstance objectInstance) {
        // Confirm deletion
        int result = JOptionPane.showConfirmDialog(view, 
                "Are you sure you want to delete the object '" + objectInstance.getName() + "'?", 
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (result != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Remove the object from any references
        for (ObjectInstance obj : objects) {
            for (Property property : obj.getClassDefinition().getProperties()) {
                if (property.getType() == PropertyType.OBJECT_REFERENCE) {
                    if (objectInstance.equals(obj.getPropertyValue(property.getName()))) {
                        obj.setPropertyValue(property.getName(), null);
                    }
                }
            }
        }
        
        // Remove object
        objects.remove(objectInstance);
        view.getWorkspacePanel().removeObject(objectInstance);
        
        // Update selection
        if (selectedObject == objectInstance) {
            setSelectedObject(null);
        }
        
        // Update UI
        updateObjectList();
        setModified(true);
    }
    
    /**
     * Clears all objects of the selected class.
     */
    public void clearObjects() {
        if (selectedClass == null) {
            return;
        }
        
        // Confirm deletion
        int result = JOptionPane.showConfirmDialog(view, 
                "Are you sure you want to delete all objects of class '" + selectedClass.getName() + "'?", 
                "Confirm Deletion", JOptionPane.YES_NO_OPTION);
        
        if (result != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Remove objects
        List<ObjectInstance> objectsToRemove = new ArrayList<>();
        for (ObjectInstance obj : objects) {
            if (obj.getClassDefinition().equals(selectedClass)) {
                objectsToRemove.add(obj);
            }
        }
        
        for (ObjectInstance obj : objectsToRemove) {
            // Remove the object from any references
            for (ObjectInstance remainingObj : objects) {
                if (!objectsToRemove.contains(remainingObj)) {
                    for (Property property : remainingObj.getClassDefinition().getProperties()) {
                        if (property.getType() == PropertyType.OBJECT_REFERENCE) {
                            if (obj.equals(remainingObj.getPropertyValue(property.getName()))) {
                                remainingObj.setPropertyValue(property.getName(), null);
                            }
                        }
                    }
                }
            }
            
            // Remove from workspace
            view.getWorkspacePanel().removeObject(obj);
            
            // Remove from objects list
            objects.remove(obj);
        }
        
        // Update selection
        if (selectedObject != null && selectedObject.getClassDefinition().equals(selectedClass)) {
            setSelectedObject(null);
        }
        
        // Update UI
        updateObjectList();
        setModified(true);
    }
    
    /**
     * Gets all objects of a specific class.
     * 
     * @param classDefinition the class
     * @return the objects of the class
     */
    public List<ObjectInstance> getObjectsOfClass(ClassDefinition classDefinition) {
        List<ObjectInstance> classObjects = new ArrayList<>();
        
        for (ObjectInstance obj : objects) {
            if (obj.getClassDefinition().equals(classDefinition)) {
                classObjects.add(obj);
            }
        }
        
        return classObjects;
    }
    
    /**
     * Restores default objects.
     */
    public void restoreDefaults() {
        // In a real application, this might load some predefined object templates
        JOptionPane.showMessageDialog(view, 
                "Restore defaults not implemented yet.", 
                "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Imports objects from a file.
     */
    public void importObjects() {
        // In a real application, this would show a file chooser and load objects from a file
        JOptionPane.showMessageDialog(view, 
                "Import not implemented yet.", 
                "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Exports objects to a file.
     */
    public void exportObjects() {
        // In a real application, this would show a file chooser and save objects to a file
        JOptionPane.showMessageDialog(view, 
                "Export not implemented yet.", 
                "Information", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Creates a new project.
     */
    public void newProject() {
        // Confirm if there are unsaved changes
        if (modified) {
            int result = JOptionPane.showConfirmDialog(view, 
                    "There are unsaved changes. Do you want to save them before creating a new project?", 
                    "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION);
            
            if (result == JOptionPane.CANCEL_OPTION) {
                return;
            } else if (result == JOptionPane.YES_OPTION) {
                if (!saveProject()) {
                    return;
                }
            }
        }
        
        // Clear current data
        classes.clear();
        objects.clear();
        currentFile = null;
        selectedClass = null;
        selectedObject = null;
        modified = false;
        
        // Create new data
        createExampleClasses();
        
        // Update UI
        updateClassList();
        updateObjectList();
        setSelectedObject(null);
        view.getWorkspacePanel().clearObjects();
        view.setCurrentFileName(null);
    }
    
    /**
     * Opens a project from a file.
     */
    public void openProject() {
        // Confirm if there are unsaved changes
        if (modified) {
            int result = JOptionPane.showConfirmDialog(view, 
                    "There are unsaved changes. Do you want to save them before opening a new project?", 
                    "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION);
            
            if (result == JOptionPane.CANCEL_OPTION) {
                return;
            } else if (result == JOptionPane.YES_OPTION) {
                if (!saveProject()) {
                    return;
                }
            }
        }
        
        // Show file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Open Project");
        int result = fileChooser.showOpenDialog(view);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            try {
                // Load project from file
                Object[] data = fileController.loadFromFile(file);
                
                if (data != null && data.length == 2) {
                    // Update data
                    classes = (List<ClassDefinition>) data[0];
                    objects = (List<ObjectInstance>) data[1];
                    currentFile = file;
                    selectedClass = classes.isEmpty() ? null : classes.get(0);
                    selectedObject = null;
                    modified = false;
                    
                    // Update UI
                    updateClassList();
                    updateObjectList();
                    setSelectedObject(null);
                    view.getWorkspacePanel().clearObjects();
                    view.setCurrentFileName(file.getName());
                    
                    JOptionPane.showMessageDialog(view, 
                            "Project loaded successfully.", 
                            "Project Loaded", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, 
                        "Failed to load project: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Saves the current project.
     * 
     * @return true if the project was saved, false otherwise
     */
    public boolean saveProject() {
        if (currentFile == null) {
            return saveProjectAs();
        }
        
        try {
            // Save project to file
            fileController.saveToFile(currentFile, new Object[]{classes, objects});
            modified = false;
            
            JOptionPane.showMessageDialog(view, 
                    "Project saved successfully.", 
                    "Project Saved", JOptionPane.INFORMATION_MESSAGE);
            
            return true;
        } catch (Exception e) {
            JOptionPane.showMessageDialog(view, 
                    "Failed to save project: " + e.getMessage(), 
                    "Error", JOptionPane.ERROR_MESSAGE);
            
            return false;
        }
    }
    
    /**
     * Saves the current project to a new file.
     * 
     * @return true if the project was saved, false otherwise
     */
    public boolean saveProjectAs() {
        // Show file chooser
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Project As");
        int result = fileChooser.showSaveDialog(view);
        
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            // Add .oed extension if missing
            if (!file.getName().toLowerCase().endsWith(".oed")) {
                file = new File(file.getAbsolutePath() + ".oed");
            }
            
            // Confirm overwrite if file exists
            if (file.exists()) {
                result = JOptionPane.showConfirmDialog(view, 
                        "File already exists. Do you want to overwrite it?", 
                        "Confirm Overwrite", JOptionPane.YES_NO_OPTION);
                
                if (result != JOptionPane.YES_OPTION) {
                    return false;
                }
            }
            
            try {
                // Save project to file
                fileController.saveToFile(file, new Object[]{classes, objects});
                currentFile = file;
                modified = false;
                
                // Update UI
                view.setCurrentFileName(file.getName());
                
                JOptionPane.showMessageDialog(view, 
                        "Project saved successfully.", 
                        "Project Saved", JOptionPane.INFORMATION_MESSAGE);
                
                return true;
            } catch (Exception e) {
                JOptionPane.showMessageDialog(view, 
                        "Failed to save project: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        return false;
    }
    
    /**
     * Confirms if the user wants to exit when there are unsaved changes.
     * 
     * @return true if the user confirms exit, false otherwise
     */
    public boolean confirmExit() {
        if (!modified) {
            return true;
        }
        
        int result = JOptionPane.showConfirmDialog(view, 
                "There are unsaved changes. Do you want to save them before exiting?", 
                "Unsaved Changes", JOptionPane.YES_NO_CANCEL_OPTION);
        
        if (result == JOptionPane.CANCEL_OPTION) {
            return false;
        } else if (result == JOptionPane.YES_OPTION) {
            return saveProject();
        } else {
            return true;
        }
    }
    
    /**
     * Sets the modified flag.
     * 
     * @param modified true if the project has been modified, false otherwise
     */
    public void setModified(boolean modified) {
        this.modified = modified;
    }
}
