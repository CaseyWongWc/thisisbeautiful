package objectEditor.editor;

import objectEditor.model.Terrain;
import objectEditor.util.FileUtils;
import objectEditor.util.ImageUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * Panel for editing terrain properties in the object editor.
 */
public class TerrainEditorPanel extends BaseEditorPanel<Terrain> {
    private static final long serialVersionUID = 1L;

    // Basic properties
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JTextField imagePathField;
    private JLabel imageLabel;
    private String currentImagePath;

    // Terrain-specific properties
    private JSpinner movementCostSpinner;
    private JSpinner strengthCostSpinner;
    private JSpinner thirstCostSpinner;
    private JSpinner hungerCostSpinner;
    private JSpinner goldCostSpinner;

    // Preview panel for terrain appearance
    private JPanel previewPanel;

    /**
     * Creates a new terrain editor panel.
     */
    public TerrainEditorPanel() {
        super("terrain", "Terrain");
        initUI();
        loadObjects();
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
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        basicPanel.add(descScrollPane, gbc);

        // Image path field
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridheight = 1;
        gbc.weightx = 0.0;
        basicPanel.add(new JLabel("Image:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        imagePathField = new JTextField(20);
        basicPanel.add(imagePathField, gbc);

        // Browse image button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton browseImageButton = new JButton("Browse...");
        browseImageButton.addActionListener(e -> browseForImage());
        basicPanel.add(browseImageButton, gbc);

        // Image preview
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setBorder(BorderFactory.createEtchedBorder());
        imageLabel.setPreferredSize(new Dimension(100, 100));
        JScrollPane imageScrollPane = new JScrollPane(imageLabel);
        imageScrollPane.setPreferredSize(new Dimension(200, 200));
        basicPanel.add(imageScrollPane, gbc);

        // Advanced Properties tab
        JPanel advancedPanel = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Movement cost
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        advancedPanel.add(new JLabel("Movement Cost:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        movementCostSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        advancedPanel.add(movementCostSpinner, gbc);

        // Strength cost
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        advancedPanel.add(new JLabel("Strength Cost:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        strengthCostSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        advancedPanel.add(strengthCostSpinner, gbc);

        // Thirst cost
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        advancedPanel.add(new JLabel("Thirst Cost:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        thirstCostSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        advancedPanel.add(thirstCostSpinner, gbc);

        // Hunger cost
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.weightx = 0.0;
        advancedPanel.add(new JLabel("Hunger Cost:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        hungerCostSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        advancedPanel.add(hungerCostSpinner, gbc);

        // Gold cost
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.weightx = 0.0;
        advancedPanel.add(new JLabel("Gold Cost:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        goldCostSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        advancedPanel.add(goldCostSpinner, gbc);

        // Preview panel
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        previewPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintTerrainPreview(g);
            }
        };
        previewPanel.setBorder(BorderFactory.createTitledBorder("Preview"));
        previewPanel.setPreferredSize(new Dimension(200, 200));
        advancedPanel.add(previewPanel, gbc);

        // Add tabs to tabbed pane
        tabbedPane.addTab("Basic", basicPanel);
        tabbedPane.addTab("Advanced", advancedPanel);

        // Add tabbed pane to editor panel
        add(tabbedPane, BorderLayout.CENTER);
        
        // Add action listener to image path field to update image when text changes
        imagePathField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateImage(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateImage(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateImage(); }
            
            private void updateImage() {
                updateImagePreview(imagePathField.getText());
            }
        });
    }

    /**
     * Paints a preview of the terrain based on properties
     */
    private void paintTerrainPreview(Graphics g) {
        if (nameField.getText().isEmpty()) return;

        // Get the terrain name
        String name = nameField.getText().toLowerCase();
        
        // Choose a color based on terrain name
        Color terrainColor;
        if (name.contains("water") || name.contains("ocean") || name.contains("lake") || name.contains("river")) {
            terrainColor = new Color(64, 128, 255); // Blue for water
        } else if (name.contains("forest") || name.contains("jungle") || name.contains("wood")) {
            terrainColor = new Color(34, 139, 34); // Forest green
        } else if (name.contains("mountain") || name.contains("hill")) {
            terrainColor = new Color(139, 137, 137); // Slate gray for mountains
        } else if (name.contains("desert") || name.contains("sand")) {
            terrainColor = new Color(238, 221, 130); // Sandy color
        } else if (name.contains("road") || name.contains("path")) {
            terrainColor = new Color(210, 180, 140); // Tan for roads
        } else if (name.contains("grass") || name.contains("field") || name.contains("plain")) {
            terrainColor = new Color(124, 252, 0); // Lawn green for grass
        } else if (name.contains("swamp") || name.contains("marsh")) {
            terrainColor = new Color(107, 142, 35); // Olive drab for swamps
        } else if (name.contains("town") || name.contains("city") || name.contains("village")) {
            terrainColor = new Color(169, 169, 169); // Dark gray for urban areas
        } else {
            // Default terrain color - using hash of the name to create a consistent color
            int hash = name.hashCode();
            terrainColor = new Color(
                Math.abs(hash % 200 + 55), // Red 
                Math.abs((hash / 100) % 200 + 55), // Green
                Math.abs((hash / 10000) % 200 + 55) // Blue
            );
        }
        
        // Fill the panel with the terrain color
        g.setColor(terrainColor);
        g.fillRect(0, 0, previewPanel.getWidth(), previewPanel.getHeight());
        
        // Draw costs as text
        int moveCost = (Integer)movementCostSpinner.getValue();
        int strengthCost = (Integer)strengthCostSpinner.getValue();
        int thirstCost = (Integer)thirstCostSpinner.getValue();
        int hungerCost = (Integer)hungerCostSpinner.getValue();
        int goldCost = (Integer)goldCostSpinner.getValue();
        
        // Choose text color based on background brightness
        int brightness = (terrainColor.getRed() + terrainColor.getGreen() + terrainColor.getBlue()) / 3;
        g.setColor(brightness > 128 ? Color.BLACK : Color.WHITE);
        
        g.drawString("Move: " + moveCost, 10, 20);
        g.drawString("Strength: " + strengthCost, 10, 40);
        g.drawString("Thirst: " + thirstCost, 10, 60);
        g.drawString("Hunger: " + hungerCost, 10, 80);
        g.drawString("Gold: " + goldCost, 10, 100);
    }

    /**
     * Opens a file browser to select an image
     */
    private void browseForImage() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "jpeg", "png", "gif"));
        fileChooser.setCurrentDirectory(new File("."));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String imagePath = selectedFile.getPath();
            imagePathField.setText(imagePath);
            updateImagePreview(imagePath);
        }
    }
    
    /**
     * Updates the image preview
     */
    private void updateImagePreview(String imagePath) {
        if (imagePath != null && !imagePath.isEmpty()) {
            ImageIcon icon = ImageUtils.loadImageIcon(imagePath);
            if (icon != null) {
                imageLabel.setIcon(icon);
                imageLabel.setText(null); // Clear any text
                currentImagePath = imagePath;
            } else {
                imageLabel.setIcon(null);
                imageLabel.setText("Image not found");
                currentImagePath = null;
            }
        } else {
            imageLabel.setIcon(null);
            imageLabel.setText("No image selected");
            currentImagePath = null;
        }
    }

    @Override
    protected void loadObjects() {
        objects.clear();
        objectList.clear();
        
        // Load terrains from files
        File directory = new File("exports/terrain/");
        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
            if (files != null) {
                for (File file : files) {
                    Terrain terrain = FileUtils.loadTerrain(file.getPath());
                    if (terrain != null) {
                        objects.put(terrain.getName(), terrain);
                        objectList.addElement(terrain.getName());
                    }
                }
            }
        }
    }
    
    @Override
    protected void handleListSelectionChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            String selectedName = objectListComponent.getSelectedValue();
            if (selectedName != null) {
                Terrain terrain = objects.get(selectedName);
                if (terrain != null) {
                    editObject(terrain);
                    
                    // Update the current file path
                    String filePath = "exports/terrain/" + terrain.getName() + ".txt";
                    File file = new File(filePath);
                    if (file.exists()) {
                        currentFile = filePath;
                        fileLabel.setText("Current File: " + filePath);
                    } else {
                        currentFile = null;
                        fileLabel.setText("Current File: [None]");
                    }
                }
            }
        }
    }

    @Override
    protected void createObject() {
        Terrain terrain = new Terrain();
        terrain.setName("New Terrain");
        
        // Add to objects and update UI
        objects.put(terrain.getName(), terrain);
        objectList.addElement(terrain.getName());
        objectListComponent.setSelectedValue(terrain.getName(), true);
        
        // Update the current file
        currentFile = null;
        fileLabel.setText("Current File: [None]");
        
        // Display the object
        editObject(terrain);
    }

    @Override
    protected void editObject(Terrain terrain) {
        if (terrain != null) {
            // Display basic information
            nameField.setText(terrain.getName());
            descriptionArea.setText(terrain.getDescription());
            String imagePath = terrain.getImagePath() != null ? terrain.getImagePath() : "";
            imagePathField.setText(imagePath);
            
            // Display terrain-specific properties
            movementCostSpinner.setValue(terrain.getMovementCost());
            strengthCostSpinner.setValue(terrain.getStrengthCost());
            thirstCostSpinner.setValue(terrain.getThirstCost());
            hungerCostSpinner.setValue(terrain.getHungerCost());
            goldCostSpinner.setValue(terrain.getGoldCost());
            
            // Update image preview
            updateImagePreview(terrain.getImagePath());
            
            // Update preview panel
            previewPanel.repaint();
        }
    }

    @Override
    protected void saveObject() {
        String selectedName = objectListComponent.getSelectedValue();
        if (selectedName != null) {
            Terrain terrain = objects.get(selectedName);
            if (terrain != null) {
                // Update the terrain object from the UI
                updateTerrainFromUI(terrain, selectedName);
                
                // Save to file
                String filePath = "exports/terrain/" + terrain.getName() + ".txt";
                boolean success = FileUtils.saveTerrain(terrain, filePath);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                        "Terrain saved successfully.",
                        "Save Successful", 
                        JOptionPane.INFORMATION_MESSAGE);
                    
                    // Update current file
                    currentFile = filePath;
                    fileLabel.setText("Current File: " + filePath);
                } else {
                    JOptionPane.showMessageDialog(this, 
                        "Failed to save terrain.",
                        "Save Failed", 
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, 
                "Please select a terrain to save.",
                "No Terrain Selected", 
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * Updates a terrain object with current UI values
     */
    private void updateTerrainFromUI(Terrain terrain, String selectedName) {
        if (terrain != null) {
            // Update basic information
            terrain.setName(nameField.getText());
            terrain.setDescription(descriptionArea.getText());
            String imagePath = imagePathField.getText();
            terrain.setImagePath(imagePath.isEmpty() ? null : imagePath);
            
            // Update terrain-specific properties
            terrain.setMovementCost((Integer) movementCostSpinner.getValue());
            terrain.setStrengthCost((Integer) strengthCostSpinner.getValue());
            terrain.setThirstCost((Integer) thirstCostSpinner.getValue());
            terrain.setHungerCost((Integer) hungerCostSpinner.getValue());
            terrain.setGoldCost((Integer) goldCostSpinner.getValue());
            
            // Update object list if name changed
            if (!selectedName.equals(terrain.getName())) {
                objects.remove(selectedName);
                objects.put(terrain.getName(), terrain);
                
                int selectedIndex = objectList.indexOf(selectedName);
                objectList.remove(selectedIndex);
                objectList.addElement(terrain.getName());
                objectListComponent.setSelectedValue(terrain.getName(), true);
            }
        }
    }

    @Override
    protected void deleteObject() {
        String selectedName = objectListComponent.getSelectedValue();
        if (selectedName != null) {
            // Confirm deletion
            int confirmed = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete " + selectedName + "?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION);
                
            if (confirmed == JOptionPane.YES_OPTION) {
                // Remove from objects map
                objects.remove(selectedName);
                
                // Remove from list model
                objectList.removeElement(selectedName);
                
                // Delete file if it exists
                String filePath = "exports/terrain/" + selectedName + ".txt";
                File file = new File(filePath);
                if (file.exists()) {
                    boolean deleted = file.delete();
                    if (!deleted) {
                        JOptionPane.showMessageDialog(this,
                            "Failed to delete file: " + filePath,
                            "Delete Failed",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
                
                // Clear UI if selected object was deleted
                if (objectListComponent.getSelectedValue() == null) {
                    nameField.setText("");
                    descriptionArea.setText("");
                    imagePathField.setText("");
                    imageLabel.setIcon(null);
                    imageLabel.setText("No image selected");
                    currentImagePath = null;
                    movementCostSpinner.setValue(0);
                    strengthCostSpinner.setValue(0);
                    thirstCostSpinner.setValue(0);
                    hungerCostSpinner.setValue(0);
                    goldCostSpinner.setValue(0);
                    
                    currentFile = null;
                    fileLabel.setText("Current File: [None]");
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Please select a terrain to delete.",
                "No Terrain Selected",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    @Override
    protected void exportObject() {
        // Not used in this implementation
    }

    @Override
    protected void importObject() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Import Terrain");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text Files", "txt"));
        fileChooser.setCurrentDirectory(new File("exports/terrain/"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            Terrain terrain = FileUtils.loadTerrain(selectedFile.getPath());
            
            if (terrain != null) {
                // Add terrain to objects
                objects.put(terrain.getName(), terrain);
                
                // Update UI
                boolean exists = false;
                for (int i = 0; i < objectList.size(); i++) {
                    if (objectList.get(i).equals(terrain.getName())) {
                        exists = true;
                        break;
                    }
                }
                
                if (!exists) {
                    objectList.addElement(terrain.getName());
                }
                
                objectListComponent.setSelectedValue(terrain.getName(), true);
                
                // Update current file
                currentFile = selectedFile.getPath();
                fileLabel.setText("Current File: " + currentFile);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to load terrain from file.",
                    "Load Failed", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
