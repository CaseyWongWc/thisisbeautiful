package objectEditor.editor;

import objectEditor.model.ObjectInstance;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Base editor panel for all objects.
 *
 * @param <T> the type of object to edit
 */
public abstract class BaseEditorPanel<T extends ObjectInstance> extends JPanel implements ListSelectionListener {
    private static final long serialVersionUID = 1L;

    /** The list of objects. */
    protected DefaultListModel<String> objectList;
    /** The list component. */
    protected JList<String> objectListComponent;
    /** The map of objects. */
    protected Map<String, T> objects;
    /** The object type. */
    protected String objectType;
    /** The object label. */
    protected String objectLabel;
    /** The current file. */
    protected String currentFile;
    /** The file label. */
    protected JLabel fileLabel;
    
    // Action buttons
    protected JButton copyButton;
    protected JButton deleteButton;
    protected JButton browseButton;
    protected JButton updateButton;

    /**
     * Creates a new base editor panel.
     *
     * @param objectType the object type
     * @param objectLabel the object label
     */
    public BaseEditorPanel(String objectType, String objectLabel) {
        this.objectType = objectType;
        this.objectLabel = objectLabel;
        objectList = new DefaultListModel<>();
        objectListComponent = new JList<>(objectList);
        objects = new HashMap<>();
        currentFile = null;
    }
    
    /**
     * Initializes the UI.
     */
    protected void initUI() {
        setLayout(new BorderLayout());
        
        // Object list panel
        JPanel listPanel = new JPanel(new BorderLayout());
        listPanel.setBorder(BorderFactory.createTitledBorder(objectLabel + "s"));
        
        // Create and configure object list
        objectListComponent.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        objectListComponent.addListSelectionListener(this);
        JScrollPane listScrollPane = new JScrollPane(objectListComponent);
        listScrollPane.setPreferredSize(new Dimension(200, 400));
        listPanel.add(listScrollPane, BorderLayout.CENTER);
        
        // Create button panel
        JPanel buttonPanel = new JPanel(new GridLayout(5, 1, 5, 5));
        
        JButton newButton = new JButton("New " + objectLabel);
        newButton.addActionListener(this::handleNewObject);
        
        copyButton = new JButton("Copy");
        copyButton.addActionListener(this::handleCopyObject);
        
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this::handleDeleteObject);
        
        browseButton = new JButton("Browse...");
        browseButton.addActionListener(this::handleBrowseObject);
        
        updateButton = new JButton("Update");
        updateButton.addActionListener(this::handleSaveObject);
        
        buttonPanel.add(newButton);
        buttonPanel.add(copyButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(browseButton);
        buttonPanel.add(updateButton);
        
        JPanel listButtonPanel = new JPanel(new BorderLayout());
        listButtonPanel.add(buttonPanel, BorderLayout.NORTH);
        
        // Add file label
        fileLabel = new JLabel("Current File: [None]");
        listButtonPanel.add(fileLabel, BorderLayout.SOUTH);
        
        listPanel.add(listButtonPanel, BorderLayout.SOUTH);
        
        // Add list panel to main panel
        add(listPanel, BorderLayout.WEST);
        
        // Initially disable editing-related buttons until an object is selected
        updateButtonStates(false);
    }
    
    /**
     * Updates button states based on selection
     * 
     * @param hasSelection whether there is a selection
     */
    protected void updateButtonStates(boolean hasSelection) {
        copyButton.setEnabled(hasSelection);
        deleteButton.setEnabled(hasSelection);
        updateButton.setEnabled(hasSelection);
        // Browse button always enabled
    }
    
    /**
     * Loads objects from storage.
     */
    protected abstract void loadObjects();
    
    /**
     * Creates a new object.
     */
    protected abstract void createObject();
    
    /**
     * Edits an object.
     * 
     * @param object the object to edit
     */
    protected abstract void editObject(T object);
    
    /**
     * Saves the current object.
     */
    protected abstract void saveObject();
    
    /**
     * Exports the current object.
     */
    protected abstract void exportObject();
    
    /**
     * Imports an object.
     */
    protected abstract void importObject();
    
    /**
     * Deletes the current object.
     */
    protected abstract void deleteObject();
    
    /**
     * Handles the list selection changed event.
     * 
     * @param e the event
     */
    protected abstract void handleListSelectionChanged(ListSelectionEvent e);
    
    /**
     * Handles selection changes in the list.
     *
     * @param e the list selection event
     */
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            boolean hasSelection = objectListComponent.getSelectedIndex() != -1;
            updateButtonStates(hasSelection);
            handleListSelectionChanged(e);
        }
    }
    
    /**
     * Handles the new object button event.
     *
     * @param e the action event
     */
    protected void handleNewObject(ActionEvent e) {
        createObject();
    }
    
    /**
     * Handles the save object button event.
     *
     * @param e the action event
     */
    protected void handleSaveObject(ActionEvent e) {
        saveObject();
    }
    
    /**
     * Handles the delete object button event.
     *
     * @param e the action event
     */
    protected void handleDeleteObject(ActionEvent e) {
        String selectedName = objectListComponent.getSelectedValue();
        if (selectedName != null) {
            deleteObject();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please select an object to delete.",
                    "No Object Selected",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * Handles the import object button event.
     *
     * @param e the action event
     */
    protected void handleImportObject(ActionEvent e) {
        importObject();
    }
    
    /**
     * Handles the export object button event.
     *
     * @param e the action event
     */
    protected void handleExportObject(ActionEvent e) {
        exportObject();
    }
    
    /**
     * Handles the copy object button event.
     *
     * @param e the action event
     */
    protected void handleCopyObject(ActionEvent e) {
        String selectedName = objectListComponent.getSelectedValue();
        if (selectedName != null) {
            T originalItem = objects.get(selectedName);
            if (originalItem != null) {
                try {
                    // Create a deep copy by serializing to JSON and back
                    T copiedItem = (T) originalItem.clone();
                    
                    // Set a new name for the copy
                    copiedItem.setName(selectedName + " (Copy)");
                    
                    // Add to the list and map
                    objects.put(copiedItem.getName(), copiedItem);
                    objectList.addElement(copiedItem.getName());
                    objectListComponent.setSelectedValue(copiedItem.getName(), true);
                    
                    // Automatically save the copied item to a file
                    String fileName = "exports/" + objectType + "/" + copiedItem.getName() + ".txt";
                    
                    if (objectType.equals("item")) {
                        objectEditor.util.FileUtils.saveItem((objectEditor.model.Item)copiedItem, fileName);
                    } else if (objectType.equals("creature")) {
                        objectEditor.util.FileUtils.saveCreature((objectEditor.model.Creature)copiedItem, fileName);
                    } else if (objectType.equals("trader")) {
                        objectEditor.util.FileUtils.saveTrader((objectEditor.model.Trader)copiedItem, fileName);
                    } else if (objectType.equals("movement")) {
                        objectEditor.util.FileUtils.saveMovement((objectEditor.model.Movement)copiedItem, fileName);
                    } else if (objectType.equals("spawner")) {
                        objectEditor.util.FileUtils.saveSpawner((objectEditor.model.Spawner)copiedItem, fileName);
                    }
                    
                    // Update current file
                    currentFile = fileName;
                    fileLabel.setText("Current File: " + fileName);
                    System.out.println("Copied item automatically saved to file: " + fileName);
                } catch (Exception ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this, "Failed to copy item: " + ex.getMessage(), 
                            "Copy Failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please select an object to copy.",
                    "No Object Selected",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * Handles the browse object button event.
     *
     * @param e the action event
     */
    protected void handleBrowseObject(ActionEvent e) {
        // The browse button replaces the import button functionality
        importObject();
    }
}
