package objectEditor.editor;

import objectEditor.model.Spawner;
import objectEditor.util.FileUtils;
import objectEditor.util.ImageUtils;
import objectEditor.util.ErrorLogger;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.border.TitledBorder;
import objectEditor.model.ObjectInstance;

/**
 * Editor panel for Spawner objects.
 */
public class SpawnerEditorPanel extends BaseEditorPanel<Spawner> {
    private static final long serialVersionUID = 1L;

    // Basic properties
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JTextField imagePathField;
    private JLabel imageLabel;
    private String currentImagePath;
    
    // Spawner-specific properties
    private JSpinner maxSpawnCapSpinner;
    private JSpinner spawnFrequencySpinner;
    private JCheckBox isDirectedCheckbox;
    private JComboBox<String> directionComboBox;
    private JCheckBox randomOrientationCheckbox;
    private JComboBox<String> objectTypeComboBox;
    private JTextField objectTemplateField;
    
    // Optimization flags
    private boolean isLoadingData = false;
    private boolean isDirty = false;
    private Timer autoSaveTimer;
    private final int AUTO_SAVE_DELAY = 1500; // Increased to 1.5 seconds to reduce save frequency
    private long lastDirtyMarkTime = 0;
    private final long DIRTY_DEBOUNCE_INTERVAL = 300; // Debounce period in milliseconds
    
    /**
     * Creates a new spawner editor panel.
     */
    public SpawnerEditorPanel() {
        super("spawner", "Spawner");
        initUI();
        setupAutoSave();
        loadObjects();
    }
    
    /**
     * Setup auto-save timer to prevent frequent saves
     */
    private void setupAutoSave() {
        autoSaveTimer = new Timer(AUTO_SAVE_DELAY, e -> {
            if (isDirty && !isLoadingData) {
                saveObject();
                isDirty = false;
            }
        });
        autoSaveTimer.setRepeats(false);
    }

    @Override
    protected void initUI() {
        super.initUI();

        // Create tabs for basic and advanced properties
        JTabbedPane tabbedPane = new JTabbedPane();

        // Basic properties tab
        JPanel basicPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Name field
        gbc.gridx = 0;
        gbc.gridy = 0;
        basicPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        nameField.getDocument().addDocumentListener(new LazyDocumentListener());
        basicPanel.add(nameField, gbc);

        // Description field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        basicPanel.add(new JLabel("Description:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridheight = 2;
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.getDocument().addDocumentListener(new LazyDocumentListener());
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        basicPanel.add(descScrollPane, gbc);

        // Image
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridheight = 1;
        gbc.weightx = 0.0;
        basicPanel.add(new JLabel("Image:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(100, 100));
        imageLabel.setBorder(BorderFactory.createEtchedBorder());
        basicPanel.add(imageLabel, gbc);

        // Image browse button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JButton browseButton = new JButton("Browse...");
        browseButton.addActionListener(this::browseImage);
        basicPanel.add(browseButton, gbc);

        // Add basic panel to tabbed pane
        tabbedPane.addTab("Basic", basicPanel);

        // Spawner properties tab
        JPanel spawnerPanel = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Spawn properties section
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        spawnerPanel.add(new JLabel("Spawn Properties"), gbc);
        
        // Max Spawn Cap
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        spawnerPanel.add(new JLabel("Max Spawn Cap:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        maxSpawnCapSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        maxSpawnCapSpinner.addChangeListener(e -> markDirty());
        spawnerPanel.add(maxSpawnCapSpinner, gbc);

        // Spawn Frequency
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        spawnerPanel.add(new JLabel("Spawn Frequency (turns):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        spawnFrequencySpinner = new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));
        spawnFrequencySpinner.addChangeListener(e -> markDirty());
        spawnerPanel.add(spawnFrequencySpinner, gbc);

        // Direction properties
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        spawnerPanel.add(new JLabel("Direction Properties:"), gbc);

        // Reset insets
        gbc.insets = new Insets(5, 5, 5, 5);

        // Is Directed
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        spawnerPanel.add(new JLabel("Is Directed:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        isDirectedCheckbox = new JCheckBox();
        isDirectedCheckbox.addItemListener(e -> markDirty());
        spawnerPanel.add(isDirectedCheckbox, gbc);

        // Direction
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        spawnerPanel.add(new JLabel("Direction:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        directionComboBox = new JComboBox<>(new String[] {"N", "NE", "E", "SE", "S", "SW", "W", "NW", "none"});
        directionComboBox.addItemListener(e -> markDirty());
        spawnerPanel.add(directionComboBox, gbc);

        // Random Orientation
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.0;
        spawnerPanel.add(new JLabel("Random Orientation:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        randomOrientationCheckbox = new JCheckBox();
        randomOrientationCheckbox.addItemListener(e -> markDirty());
        spawnerPanel.add(randomOrientationCheckbox, gbc);

        // Object properties section
        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        spawnerPanel.add(new JLabel("Object to Spawn:"), gbc);

        // Reset insets
        gbc.insets = new Insets(5, 5, 5, 5);

        // Object Type
        gbc.gridx = 0;
        gbc.gridy = 8;
        gbc.gridwidth = 1;
        spawnerPanel.add(new JLabel("Object Type:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        objectTypeComboBox = new JComboBox<>(new String[] {"item", "creature", "trader"});
        objectTypeComboBox.addItemListener(e -> markDirty());
        spawnerPanel.add(objectTypeComboBox, gbc);

        // Object Template
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.weightx = 0.0;
        spawnerPanel.add(new JLabel("Object Template:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        objectTemplateField = new JTextField(20);
        objectTemplateField.getDocument().addDocumentListener(new LazyDocumentListener());
        spawnerPanel.add(objectTemplateField, gbc);

        // Add template browse button
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
        JButton templateBrowseButton = new JButton("Browse Templates...");
        templateBrowseButton.addActionListener(this::browseTemplate);
        spawnerPanel.add(templateBrowseButton, gbc);

        // Add spawner panel to tabbed pane
        tabbedPane.addTab("Spawner", spawnerPanel);

        // Add tabbed pane to editor panel
        add(tabbedPane, BorderLayout.CENTER);
    }

    @Override
    protected void loadObjects() {
        objectList.clear();
        objects.clear(); // Make sure to clear the objects map too

        File folder = new File("exports/spawner");
        if (!folder.exists()) {
            folder.mkdirs();
            return; // No need to process further if the directory was just created
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files != null) {
            // Pre-allocate capacity to avoid resizing during loading
            objectList.ensureCapacity(files.length);
            
            for (File file : files) {
                try {
                    Spawner spawner = FileUtils.loadSpawner(file.getPath());
                    if (spawner != null) {
                        objectList.addElement(spawner.getName());
                        objects.put(spawner.getName(), spawner);
                    }
                } catch (Exception ex) {
                    ErrorLogger.logError("Error loading spawner from " + file.getPath(), ex);
                }
            }
        }
    }

    @Override
    protected void createObject() {
        Spawner spawner = new Spawner();
        spawner.setName("New Spawner");
        spawner.setDescription("");
        spawner.setMaxSpawnCap(1);
        spawner.setSpawnFrequency(5);
        spawner.setDirected(false);
        spawner.setDirection("none");
        spawner.setRandomOrientation(false);
        spawner.setObjectType("item");
        spawner.setObjectTemplate("");
        
        objects.put(spawner.getName(), spawner);
        objectList.addElement(spawner.getName());
        objectListComponent.setSelectedValue(spawner.getName(), true);

        // Automatically create a file for the new spawner
        String fileName = "exports/spawner/" + spawner.getName() + ".txt";
        FileUtils.saveSpawner(spawner, fileName);

        // Set current file
        currentFile = fileName;
        fileLabel.setText("Current File: " + fileName);
        System.out.println("New spawner automatically saved to file: " + fileName);

        editObject(spawner);
    }

    @Override
    protected void editObject(Spawner spawner) {
        if (spawner == null) return;
        
        isLoadingData = true;
        
        try {
            // Use SwingUtilities.invokeLater to batch UI updates
            SwingUtilities.invokeLater(() -> {
                nameField.setText(spawner.getName());
                descriptionArea.setText(spawner.getDescription());
                maxSpawnCapSpinner.setValue(spawner.getMaxSpawnCap());
                spawnFrequencySpinner.setValue(spawner.getSpawnFrequency());
                isDirectedCheckbox.setSelected(spawner.isDirected());
                directionComboBox.setSelectedItem(spawner.getDirection());
                randomOrientationCheckbox.setSelected(spawner.isRandomOrientation());
                objectTypeComboBox.setSelectedItem(spawner.getObjectType());
                objectTemplateField.setText(spawner.getObjectTemplate());
            });

            // Load image in a separate thread to improve responsiveness
            currentImagePath = spawner.getImagePath();
            if (currentImagePath != null && !currentImagePath.isEmpty()) {
                SwingUtilities.invokeLater(() -> {
                    try {
                        ImageIcon icon = ImageUtils.loadScaledImageIcon(currentImagePath, 100, 100);
                        if (icon != null) {
                            imageLabel.setIcon(icon);
                        } else {
                            imageLabel.setIcon(null);
                        }
                    } catch (Exception ex) {
                        ErrorLogger.logError("Error loading image for spawner", ex);
                        imageLabel.setIcon(null);
                    }
                });
            } else {
                SwingUtilities.invokeLater(() -> imageLabel.setIcon(null));
            }
        } finally {
            // Delay setting isLoadingData to false to allow UI updates to complete
            SwingUtilities.invokeLater(() -> isLoadingData = false);
        }
    }

    @Override
    protected void saveObject() {
        String selectedName = objectListComponent.getSelectedValue();
        if (selectedName == null) return;
        
        Spawner spawner = objects.get(selectedName);
        if (spawner == null) return;
        
        String oldName = spawner.getName();
        
        try {
            // Only update if the value has changed
            String newName = nameField.getText();
            String newDesc = descriptionArea.getText();
            
            boolean nameChanged = !oldName.equals(newName);
            if (nameChanged) {
                spawner.setName(newName);
            }
            
            if (!spawner.getDescription().equals(newDesc)) {
                spawner.setDescription(newDesc);
            }
            
            spawner.setMaxSpawnCap((Integer) maxSpawnCapSpinner.getValue());
            spawner.setSpawnFrequency((Integer) spawnFrequencySpinner.getValue());
            spawner.setDirected(isDirectedCheckbox.isSelected());
            spawner.setDirection((String) directionComboBox.getSelectedItem());
            spawner.setRandomOrientation(randomOrientationCheckbox.isSelected());
            spawner.setObjectType((String) objectTypeComboBox.getSelectedItem());
            spawner.setObjectTemplate(objectTemplateField.getText());
            
            if (currentImagePath != null && !currentImagePath.isEmpty()) {
                spawner.setImagePath(currentImagePath);
            }
            
            if (nameChanged) {
                objects.remove(oldName);
                objects.put(newName, spawner);
                
                // Update the list model
                SwingUtilities.invokeLater(() -> {
                    int selectedIndex = objectListComponent.getSelectedIndex();
                    if (selectedIndex >= 0) {
                        objectList.remove(selectedIndex);
                        objectList.add(selectedIndex, newName);
                        objectListComponent.setSelectedIndex(selectedIndex);
                    }
                });
            }
            
            // For new spawners, automatically export to file
            if (currentFile == null) {
                String fileName = "exports/spawner/" + spawner.getName() + ".txt";
                boolean saved = FileUtils.saveSpawner(spawner, fileName);
                if (saved) {
                    currentFile = fileName;
                    fileLabel.setText("Current File: " + fileName);
                }
            } else {
                // Save to file if current file exists
                FileUtils.saveSpawner(spawner, currentFile);
            }
        } catch (Exception ex) {
            ErrorLogger.logError("Error saving spawner", ex);
            JOptionPane.showMessageDialog(this, 
                "Error saving spawner: " + ex.getMessage(), 
                "Save Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    protected void exportObject() {
        String selectedName = objectListComponent.getSelectedValue();
        if (selectedName != null) {
            Spawner spawner = objects.get(selectedName);
            if (spawner != null) {
                String fileName = "exports/spawner/" + spawner.getName() + ".txt";
                boolean saved = FileUtils.saveSpawner(spawner, fileName);
                if (saved) {
                    currentFile = fileName;
                    fileLabel.setText("Current File: " + fileName);
                    JOptionPane.showMessageDialog(this, 
                        "Spawner exported to " + fileName, 
                        "Export Successful", 
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to export spawner to " + fileName, 
                        "Export Failed", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    @Override
    protected void importObject() {
        JFileChooser fileChooser = new JFileChooser("exports/spawner");
        fileChooser.setDialogTitle("Import Spawner");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".txt");
            }

            @Override
            public String getDescription() {
                return "Spawner Files (*.txt)";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            loadSpawnerFromFile(selectedFile.getPath());
        }
    }

    private void loadSpawnerFromFile(String filePath) {
        Spawner spawner = FileUtils.loadSpawner(filePath);
        if (spawner != null) {
            // Add to list if not already present
            if (!objects.containsKey(spawner.getName())) {
                objectList.addElement(spawner.getName());
                objects.put(spawner.getName(), spawner);
            }
            // Select in list
            objectListComponent.setSelectedValue(spawner.getName(), true);
            // Set current file
            currentFile = filePath;
            fileLabel.setText("Current File: " + filePath);
        }
    }

    @Override
    public void handleListSelectionChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            String selectedName = objectListComponent.getSelectedValue();
            if (selectedName != null) {
                // Get the selected object
                Spawner spawner = objects.get(selectedName);
                if (spawner != null) {
                    // Update the UI with the object's properties
                    editObject(spawner);
                    
                    // Set the current file
                    String filePath = "exports/spawner/" + spawner.getName() + ".txt";
                    File file = new File(filePath);
                    if (file.exists()) {
                        currentFile = filePath;
                        fileLabel.setText("Current File: " + filePath);
                    } else {
                        currentFile = null;
                        fileLabel.setText("Current File: <New>");
                    }
                    
                    // Enable/disable buttons based on selection
                    updateButtonStates(true);
                } else {
                    // If the object is not found, clear the editor and disable buttons
                    clearEditor();
                    updateButtonStates(false);
                }
            } else {
                // If nothing is selected, clear the editor and disable buttons
                clearEditor();
                updateButtonStates(false);
            }
        }
    }
    
    private void clearEditor() {
        isLoadingData = true;
        try {
            nameField.setText("");
            descriptionArea.setText("");
            maxSpawnCapSpinner.setValue(1);
            spawnFrequencySpinner.setValue(5);
            isDirectedCheckbox.setSelected(false);
            directionComboBox.setSelectedIndex(0);
            randomOrientationCheckbox.setSelected(false);
            objectTypeComboBox.setSelectedIndex(0);
            objectTemplateField.setText("");
            imageLabel.setIcon(null);
            currentImagePath = null;
            
            currentFile = null;
            fileLabel.setText("Current File: <None>");
        } finally {
            isLoadingData = false;
        }
    }

    private void browseImage(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser("sprites");
        fileChooser.setDialogTitle("Select Image");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".png") || 
                       f.getName().toLowerCase().endsWith(".jpg") || 
                       f.getName().toLowerCase().endsWith(".jpeg") || 
                       f.getName().toLowerCase().endsWith(".gif");
            }

            @Override
            public String getDescription() {
                return "Image Files (*.png, *.jpg, *.jpeg, *.gif)";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            currentImagePath = selectedFile.getPath();
            
            ImageIcon icon = ImageUtils.loadScaledImageIcon(currentImagePath, 100, 100);
            if (icon != null) {
                imageLabel.setIcon(icon);
                markDirty();
            }
        }
    }

    private void browseTemplate(ActionEvent e) {
        String type = (String) objectTypeComboBox.getSelectedItem();
        String folder = "exports/" + type;
        
        // Ensure the folder exists
        File folderFile = new File(folder);
        if (!folderFile.exists()) {
            folderFile.mkdirs();
        }
        
        JFileChooser fileChooser = new JFileChooser(folder);
        fileChooser.setDialogTitle("Select Template");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".txt");
            }

            @Override
            public String getDescription() {
                return "Template Files (*.txt)";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            // Extract just the filename without extension to use as template name
            String templateName = selectedFile.getName();
            if (templateName.endsWith(".txt")) {
                templateName = templateName.substring(0, templateName.length() - 4);
            }
            objectTemplateField.setText(templateName);
            markDirty();
        }
    }

    /**
     * Mark the form as dirty and schedule an auto-save with debouncing
     */
    private void markDirty() {
        if (isLoadingData) return;
        
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastDirtyMarkTime < DIRTY_DEBOUNCE_INTERVAL) {
            // Within debounce interval, ignore this call
            return;
        }
        
        lastDirtyMarkTime = currentTime;
        isDirty = true;
        
        // Cancel existing timer and start a new one
        if (autoSaveTimer.isRunning()) {
            autoSaveTimer.stop();
        }
        autoSaveTimer.start();
    }

    /**
     * Optimized document listener that reduces the frequency of dirty marks
     */
    private class LazyDocumentListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            markDirty();
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            markDirty();
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            markDirty();
        }
    }

    @Override
    protected void deleteObject() {
        String selectedName = objectListComponent.getSelectedValue();
        if (selectedName != null) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the spawner '" + selectedName + "'?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Remove from objects map
                objects.remove(selectedName);
                
                // Remove from list
                objectList.removeElement(selectedName);
                
                // Delete the file
                if (currentFile != null) {
                    File file = new File(currentFile);
                    if (file.exists()) {
                        if (file.delete()) {
                            System.out.println("Deleted file: " + currentFile);
                        } else {
                            System.out.println("Failed to delete file: " + currentFile);
                        }
                    }
                }
                
                // Clear the editor
                clearEditor();
                
                // Update button states
                updateButtonStates(false);
            }
        }
    }
}
