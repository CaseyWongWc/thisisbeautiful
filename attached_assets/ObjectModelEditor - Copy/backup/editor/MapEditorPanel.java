package objectEditor.editor;

import objectEditor.model.Map;
import objectEditor.model.Terrain;
import objectEditor.util.ErrorLogger;
import objectEditor.util.FileUtils;
import objectEditor.util.ImageCache;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel for editing maps in the object editor.
 */
public class MapEditorPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // Map dimensions
    private JSpinner widthSpinner;
    private JSpinner heightSpinner;
    
    // Map grid display
    private JPanel mapGridPanel;
    private JScrollPane gridScrollPane;
    private Terrain selectedTerrain;
    private List<Terrain> availableTerrains;
    private JList<String> terrainList;
    private JButton applyTerrainButton;
    
    // Map properties
    private JTextField nameField;
    private JTextArea descriptionArea;
    private Map currentMap;
    private boolean dirty;
    
    // Action buttons
    private JButton newButton;
    private JButton saveButton;
    private JButton loadButton;
    
    // Advanced editor for selected terrain cell
    private JButton editTerrainButton;
    
    // File path
    private String currentFile;
    private JLabel fileLabel;
    
    /**
     * Creates a new map editor panel.
     */
    public MapEditorPanel() {
        initializeMapEditor();
    }
    
    /**
     * Initializes the map editor UI components.
     */
    private void initializeMapEditor() {
        setLayout(new BorderLayout(10, 10));
        
        // Create the top panel with map properties
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        
        // Map properties panel
        JPanel propertiesPanel = new JPanel(new GridBagLayout());
        propertiesPanel.setBorder(BorderFactory.createTitledBorder("Map Properties"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Name field
        gbc.gridx = 0;
        gbc.gridy = 0;
        propertiesPanel.add(new JLabel("Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        propertiesPanel.add(nameField, gbc);
        
        // Description field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        propertiesPanel.add(new JLabel("Description:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridheight = 2;
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        propertiesPanel.add(descScrollPane, gbc);
        
        // Create the dimension panel
        JPanel dimensionsPanel = new JPanel(new GridLayout(1, 4, 5, 5));
        dimensionsPanel.setBorder(BorderFactory.createTitledBorder("Map Dimensions"));
        
        dimensionsPanel.add(new JLabel("Width:"));
        widthSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        dimensionsPanel.add(widthSpinner);
        dimensionsPanel.add(new JLabel("Height:"));
        heightSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        dimensionsPanel.add(heightSpinner);
        
        // Create action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        newButton = new JButton("New Map");
        newButton.addActionListener(e -> createNewMap());
        
        saveButton = new JButton("Save Map");
        saveButton.addActionListener(e -> saveMap());
        
        loadButton = new JButton("Load Map");
        loadButton.addActionListener(e -> loadMap());
        
        JButton resizeButton = new JButton("Resize Map");
        resizeButton.addActionListener(e -> resizeMap());
        
        actionPanel.add(newButton);
        actionPanel.add(saveButton);
        actionPanel.add(loadButton);
        actionPanel.add(resizeButton);
        
        // Add file label
        fileLabel = new JLabel("Current File: [None]");
        JPanel fileLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fileLabelPanel.add(fileLabel);
        
        // Add components to top panel
        JPanel topLeftPanel = new JPanel(new BorderLayout());
        topLeftPanel.add(propertiesPanel, BorderLayout.CENTER);
        
        JPanel topRightPanel = new JPanel(new BorderLayout());
        topRightPanel.add(dimensionsPanel, BorderLayout.NORTH);
        
        topPanel.add(topLeftPanel, BorderLayout.WEST);
        topPanel.add(topRightPanel, BorderLayout.CENTER);
        
        JPanel topControlPanel = new JPanel(new BorderLayout());
        topControlPanel.add(actionPanel, BorderLayout.NORTH);
        topControlPanel.add(fileLabelPanel, BorderLayout.SOUTH);
        
        topPanel.add(topControlPanel, BorderLayout.SOUTH);
        
        // Create the map grid panel
        mapGridPanel = new JPanel();
        mapGridPanel.setLayout(new GridLayout(10, 10, 2, 2)); // Default size
        mapGridPanel.setBackground(Color.BLACK);
        
        // Create scroll pane for the grid
        gridScrollPane = new JScrollPane(mapGridPanel);
        gridScrollPane.setPreferredSize(new Dimension(500, 350));
        
        // Create terrain selection list
        JPanel terrainSelectionPanel = new JPanel(new BorderLayout(5, 5));
        terrainSelectionPanel.setBorder(BorderFactory.createTitledBorder("Terrain Selection"));
        
        terrainList = new JList<>();
        terrainList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        terrainList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int selectedIdx = terrainList.getSelectedIndex();
                if (selectedIdx >= 0 && selectedIdx < availableTerrains.size()) {
                    selectedTerrain = availableTerrains.get(selectedIdx);
                }
            }
        });
        
        JScrollPane terrainScrollPane = new JScrollPane(terrainList);
        terrainScrollPane.setPreferredSize(new Dimension(200, 150));
        
        // Create apply terrain button
        applyTerrainButton = new JButton("Apply Selected Terrain");
        applyTerrainButton.addActionListener(e -> {
            if (selectedTerrain != null) {
                // The logic for applying terrain to selected cells will be implemented
                // when we handle cell selection in the grid panel
                JOptionPane.showMessageDialog(this, 
                    "Please click on a cell in the map grid to apply the terrain.",
                    "Apply Terrain", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Please select a terrain first.",
                    "No Terrain Selected", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        // Create edit terrain button
        editTerrainButton = new JButton("Edit Selected Cell");
        editTerrainButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "Please right-click on a cell in the map grid to edit its properties.",
                "Edit Cell", JOptionPane.INFORMATION_MESSAGE);
        });
        
        // Add components to terrain selection panel
        terrainSelectionPanel.add(new JLabel("Available Terrains:"), BorderLayout.NORTH);
        terrainSelectionPanel.add(terrainScrollPane, BorderLayout.CENTER);
        
        JPanel terrainButtonPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        terrainButtonPanel.add(applyTerrainButton);
        terrainButtonPanel.add(editTerrainButton);
        terrainSelectionPanel.add(terrainButtonPanel, BorderLayout.SOUTH);
        
        // Create bottom panel for terrain selection
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(terrainSelectionPanel, BorderLayout.CENTER);
        
        // Create main layout
        add(topPanel, BorderLayout.NORTH);
        add(gridScrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Initialize with a new map
        createNewMap();
        
        // Load available terrains
        loadAvailableTerrains();
        
        // Set up grid click handler
        initializeGridClickHandler();
    }
    
    /**
     * Loads available terrains from the exports directory
     */
    private void loadAvailableTerrains() {
        availableTerrains = new ArrayList<>();
        DefaultListModel<String> terrainListModel = new DefaultListModel<>();
        
        File terrainDir = new File("exports/terrain/");
        if (terrainDir.exists() && terrainDir.isDirectory()) {
            File[] terrainFiles = terrainDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
            if (terrainFiles != null) {
                for (File file : terrainFiles) {
                    try {
                        Terrain terrain = FileUtils.loadTerrain(file.getPath());
                        if (terrain != null) {
                            availableTerrains.add(terrain);
                            terrainListModel.addElement(terrain.getName());
                        }
                    } catch (Exception e) {
                        ErrorLogger.logError("Error loading terrain: " + file.getName(), e);
                    }
                }
            }
        }
        
        terrainList.setModel(terrainListModel);
        if (!availableTerrains.isEmpty()) {
            terrainList.setSelectedIndex(0);
        }
    }
    
    /**
     * Initializes the map grid cell click handler
     */
    private void initializeGridClickHandler() {
        // Cell click adapter will be attached to each cell
        // when we create them in updateGridDisplay()
    }
    
    /**
     * Creates a new map
     */
    private void createNewMap() {
        int width = (Integer) widthSpinner.getValue();
        int height = (Integer) heightSpinner.getValue();
        
        currentMap = new Map(width, height);
        currentMap.setName("New Map");
        currentMap.setDescription("");
        
        nameField.setText(currentMap.getName());
        descriptionArea.setText(currentMap.getDescription());
        
        currentFile = null;
        fileLabel.setText("Current File: [None]");
        
        updateGridDisplay();
        setDirty(false);
    }
    
    /**
     * Saves the current map
     */
    private void saveMap() {
        if (currentMap == null) {
            JOptionPane.showMessageDialog(this, 
                "No map to save.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Update map properties from UI
        currentMap.setName(nameField.getText());
        currentMap.setDescription(descriptionArea.getText());
        
        // Generate file name if none exists
        if (currentFile == null) {
            currentFile = "exports/map/" + currentMap.getName() + ".txt";
        }
        
        // Ensure directories exist
        File dir = new File("exports/map");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // Save the map
        if (FileUtils.saveMap(currentMap, currentFile)) {
            JOptionPane.showMessageDialog(this, 
                "Map saved successfully to " + currentFile,
                "Save Successful", JOptionPane.INFORMATION_MESSAGE);
            setDirty(false);
            fileLabel.setText("Current File: " + currentFile);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Error saving map to " + currentFile,
                "Save Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Loads a map from file
     */
    private void loadMap() {
        JFileChooser fileChooser = new JFileChooser("exports/map");
        fileChooser.setDialogTitle("Load Map");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Map Files (*.txt)", "txt"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            Map loadedMap = FileUtils.loadMap(selectedFile.getPath());
            
            if (loadedMap != null) {
                currentMap = loadedMap;
                nameField.setText(currentMap.getName());
                descriptionArea.setText(currentMap.getDescription());
                widthSpinner.setValue(currentMap.getWidth());
                heightSpinner.setValue(currentMap.getHeight());
                
                currentFile = selectedFile.getPath();
                fileLabel.setText("Current File: " + currentFile);
                
                updateGridDisplay();
                setDirty(false);
                
                JOptionPane.showMessageDialog(this, 
                    "Map loaded successfully from " + currentFile,
                    "Load Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error loading map from " + selectedFile.getPath(),
                    "Load Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Resizes the map to the dimensions specified in the spinners
     */
    private void resizeMap() {
        if (currentMap == null) {
            JOptionPane.showMessageDialog(this, "Please create a map first.");
            return;
        }
        
        int newWidth = (Integer) widthSpinner.getValue();
        int newHeight = (Integer) heightSpinner.getValue();
        
        // Create a new map with the new dimensions
        Map oldMap = currentMap;
        Map newMap = new Map(newWidth, newHeight);
        newMap.setName(oldMap.getName());
        newMap.setDescription(oldMap.getDescription());
        
        // Copy over terrain data from the old map where possible
        for (int y = 0; y < Math.min(oldMap.getHeight(), newHeight); y++) {
            for (int x = 0; x < Math.min(oldMap.getWidth(), newWidth); x++) {
                try {
                    Terrain oldTerrain = oldMap.getTerrainAt(x, y);
                    if (oldTerrain != null) {
                        newMap.setTerrainAt(x, y, (Terrain)oldTerrain.clone());
                    }
                } catch (Exception e) {
                    ErrorLogger.logError("Error copying terrain during resize", e);
                }
            }
        }
        
        // Set the new map as the current map
        currentMap = newMap;
        
        // Update the UI
        updateGridDisplay();
        setDirty(true);
    }
    
    /**
     * Updates the map grid display to reflect the current map dimensions and terrain
     */
    private void updateGridDisplay() {
        if (currentMap == null) return;
        
        // Update dimension spinners
        widthSpinner.setValue(currentMap.getWidth());
        heightSpinner.setValue(currentMap.getHeight());
        
        // Clear existing grid
        mapGridPanel.removeAll();
        
        // Set up new grid layout
        mapGridPanel.setLayout(new GridLayout(currentMap.getHeight(), currentMap.getWidth(), 2, 2));
        
        // Create cell labels
        MouseAdapter cellClickAdapter = new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getSource() instanceof JLabel) {
                    JLabel cellLabel = (JLabel) e.getSource();
                    String[] coords = cellLabel.getName().split("_");
                    if (coords.length == 2) {
                        try {
                            int x = Integer.parseInt(coords[0]);
                            int y = Integer.parseInt(coords[1]);
                            if (e.getButton() == MouseEvent.BUTTON1) {
                                // Left click - apply terrain
                                applyTerrainToCell(x, y);
                            } else if (e.getButton() == MouseEvent.BUTTON3) {
                                // Right click - edit cell
                                editTerrainCell(x, y);
                            }
                        } catch (NumberFormatException ex) {
                            ErrorLogger.logError("Invalid cell coordinates", ex);
                        }
                    }
                }
            }
        };
        
        // Add cells to the grid
        for (int y = 0; y < currentMap.getHeight(); y++) {
            for (int x = 0; x < currentMap.getWidth(); x++) {
                JLabel cellLabel = new JLabel();
                cellLabel.setOpaque(true);
                cellLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                cellLabel.setPreferredSize(new Dimension(40, 40));
                cellLabel.setHorizontalAlignment(SwingConstants.CENTER);
                cellLabel.setName(x + "_" + y); // Store coordinates in the name
                
                try {
                    Terrain terrain = currentMap.getTerrainAt(x, y);
                    if (terrain != null) {
                        // Try to load the terrain image
                        if (terrain.getImagePath() != null && !terrain.getImagePath().isEmpty()) {
                            BufferedImage img = ImageCache.getImage(terrain.getImagePath());
                            if (img != null) {
                                Image scaledImg = img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
                                cellLabel.setIcon(new ImageIcon(scaledImg));
                            }
                        }
                        
                        // Set cell background based on terrain if no image available
                        if (cellLabel.getIcon() == null) {
                            cellLabel.setBackground(getTerrainColor(terrain));
                            cellLabel.setText(terrain.getName().substring(0, 1));
                        }
                    } else {
                        // Default styling for empty cells
                        cellLabel.setBackground(Color.DARK_GRAY);
                        cellLabel.setText("?");
                    }
                } catch (Exception e) {
                    // If there's an error, show a default cell
                    cellLabel.setBackground(Color.RED);
                    cellLabel.setText("!");
                    ErrorLogger.logError("Error rendering map cell at " + x + "," + y, e);
                }
                
                // Add click listener
                cellLabel.addMouseListener(cellClickAdapter);
                
                // Add cell to grid
                mapGridPanel.add(cellLabel);
            }
        }
        
        // Refresh the grid display
        mapGridPanel.revalidate();
        mapGridPanel.repaint();
    }
    
    /**
     * Returns a color representing the terrain type
     * 
     * @param terrain the terrain to get a color for
     * @return a color representing the terrain
     */
    private Color getTerrainColor(Terrain terrain) {
        // Simple hash function to create a consistent color for each terrain name
        String name = terrain.getName().toLowerCase();
        if (name.contains("water") || name.contains("ocean") || name.contains("lake") || name.contains("river")) {
            return new Color(64, 128, 255); // Blue for water
        } else if (name.contains("forest") || name.contains("jungle") || name.contains("wood")) {
            return new Color(34, 139, 34); // Forest green
        } else if (name.contains("mountain") || name.contains("hill")) {
            return new Color(139, 137, 137); // Slate gray for mountains
        } else if (name.contains("desert") || name.contains("sand")) {
            return new Color(238, 221, 130); // Sandy color
        } else if (name.contains("road") || name.contains("path")) {
            return new Color(210, 180, 140); // Tan for roads
        } else if (name.contains("grass") || name.contains("field") || name.contains("plain")) {
            return new Color(124, 252, 0); // Lawn green for grass
        } else if (name.contains("swamp") || name.contains("marsh")) {
            return new Color(107, 142, 35); // Olive drab for swamps
        } else if (name.contains("town") || name.contains("city") || name.contains("village")) {
            return new Color(169, 169, 169); // Dark gray for urban areas
        } else {
            // Default terrain color - using hash of the name to create a consistent color
            int hash = name.hashCode();
            return new Color(
                Math.abs(hash % 200 + 55), // Red 
                Math.abs((hash / 100) % 200 + 55), // Green
                Math.abs((hash / 10000) % 200 + 55) // Blue
            );
        }
    }
    
    /**
     * Applies the selected terrain to a cell at the specified coordinates
     * 
     * @param x the x coordinate of the cell
     * @param y the y coordinate of the cell
     */
    private void applyTerrainToCell(int x, int y) {
        if (currentMap == null || selectedTerrain == null) return;
        
        try {
            // Create a clone of the selected terrain
            Terrain clonedTerrain = (Terrain) selectedTerrain.clone();
            clonedTerrain.setX(x);
            clonedTerrain.setY(y);
            
            // Apply it to the map
            currentMap.setTerrainAt(x, y, clonedTerrain);
            
            // Update the UI
            updateGridDisplay();
            
            // Mark the editor as dirty
            setDirty(true);
        } catch (Exception e) {
            ErrorLogger.logError("Error applying terrain to cell", e);
            JOptionPane.showMessageDialog(this, 
                "Error applying terrain: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Opens a dialog to edit the terrain at the specified coordinates
     * 
     * @param x the x coordinate of the cell
     * @param y the y coordinate of the cell
     */
    private void editTerrainCell(int x, int y) {
        if (currentMap == null) return;
        
        try {
            // Make a final reference to the terrain object
            Terrain terrainAtCell = currentMap.getTerrainAt(x, y);
            if (terrainAtCell == null) {
                // If no terrain exists, create a new one
                Terrain newTerrain = new Terrain();
                newTerrain.setName("New Terrain at " + x + "," + y);
                newTerrain.setX(x);
                newTerrain.setY(y);
                currentMap.setTerrainAt(x, y, newTerrain);
                // Call the method again with the new terrain
                editTerrainCell(x, y);
                return;
            }
            
            // Use TerrainEditorDialog to edit the terrain
            TerrainEditorDialog dialog = new TerrainEditorDialog(
                (Frame) SwingUtilities.getWindowAncestor(this), 
                "Edit Terrain at " + x + "," + y,
                terrainAtCell,
                x,
                y);
                
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
            
            // If the dialog was accepted, update the map
            if (dialog.wasAccepted()) {
                currentMap.setTerrainAt(x, y, dialog.getTerrain());
                updateGridDisplay();
                setDirty(true);
            }
        } catch (Exception e) {
            ErrorLogger.logError("Error editing terrain cell", e);
            JOptionPane.showMessageDialog(this, 
                "Error editing terrain cell: " + e.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Sets the dirty flag
     * 
     * @param dirty whether the content has been modified
     */
    private void setDirty(boolean dirty) {
        this.dirty = dirty;
        saveButton.setEnabled(dirty);
    }
    
    /**
     * Refreshes the list of available terrains
     */
    public void refreshTerrainList() {
        loadAvailableTerrains();
    }
    
    /**
     * Inner class for editing terrain cells
     */
    private class TerrainEditorDialog extends JDialog {
        private static final long serialVersionUID = 1L;
        
        private Terrain terrain;
        private boolean accepted = false;
        
        // UI components
        private JTextField nameField;
        private JTextField descriptionField;
        private JTextField imagePathField;
        private JSpinner movementCostSpinner;
        private JSpinner strengthCostSpinner;
        private JSpinner thirstCostSpinner;
        private JSpinner goldCostSpinner;
        
        /**
         * Creates a new terrain editor dialog
         * 
         * @param owner the owner frame
         * @param title the dialog title
         * @param terrain the terrain to edit
         * @param x the x coordinate of the cell
         * @param y the y coordinate of the cell
         */
        public TerrainEditorDialog(Frame owner, String title, Terrain terrain, int x, int y) {
            super(owner, title, true);
            this.terrain = terrain;
            
            initializeUI();
            populateFields();
        }
        
        /**
         * Initializes the UI components
         */
        private void initializeUI() {
            JPanel panel = new JPanel(new BorderLayout(10, 10));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Create property fields
            JPanel formPanel = new JPanel(new GridLayout(0, 2, 5, 5));
            
            nameField = new JTextField();
            formPanel.add(new JLabel("Name:"));
            formPanel.add(nameField);
            
            descriptionField = new JTextField();
            formPanel.add(new JLabel("Description:"));
            formPanel.add(descriptionField);
            
            imagePathField = new JTextField();
            JButton browseButton = new JButton("Browse...");
            JPanel imagePanel = new JPanel(new BorderLayout(5, 0));
            imagePanel.add(imagePathField, BorderLayout.CENTER);
            imagePanel.add(browseButton, BorderLayout.EAST);
            
            formPanel.add(new JLabel("Image Path:"));
            formPanel.add(imagePanel);
            
            // Cost spinners
            movementCostSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
            formPanel.add(new JLabel("Movement Cost:"));
            formPanel.add(movementCostSpinner);
            
            strengthCostSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
            formPanel.add(new JLabel("Strength Cost:"));
            formPanel.add(strengthCostSpinner);
            
            thirstCostSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
            formPanel.add(new JLabel("Thirst Cost:"));
            formPanel.add(thirstCostSpinner);
            
            goldCostSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
            formPanel.add(new JLabel("Gold Cost:"));
            formPanel.add(goldCostSpinner);
            
            // Add contained objects info if terrain is not null
            if (terrain != null) {
                JLabel spawnersLabel = new JLabel("Spawners: " + terrain.getSpawners().size());
                JLabel tradersLabel = new JLabel("Traders: " + terrain.getTraders().size());
                JLabel creaturesLabel = new JLabel("Creatures: " + terrain.getCreatures().size());
                JLabel itemsLabel = new JLabel("Items: " + terrain.getItems().size());
                
                JPanel objectsPanel = new JPanel(new GridLayout(4, 1, 5, 5));
                objectsPanel.setBorder(BorderFactory.createTitledBorder("Contained Objects"));
                objectsPanel.add(spawnersLabel);
                objectsPanel.add(tradersLabel);
                objectsPanel.add(creaturesLabel);
                objectsPanel.add(itemsLabel);
                
                panel.add(objectsPanel, BorderLayout.CENTER);
            }
            
            // Browse button action
            browseButton.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser("resources/");
                FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "Image Files", "jpg", "jpeg", "png", "gif");
                fileChooser.setFileFilter(filter);
                
                int result = fileChooser.showOpenDialog(this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    String path = fileChooser.getSelectedFile().getPath();
                    imagePathField.setText(path);
                }
            });
            
            // Create OK/Cancel buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton okButton = new JButton("OK");
            JButton cancelButton = new JButton("Cancel");
            
            okButton.addActionListener(e -> {
                updateTerrain();
                accepted = true;
                dispose();
            });
            
            cancelButton.addActionListener(e -> dispose());
            
            buttonPanel.add(okButton);
            buttonPanel.add(cancelButton);
            
            // Add everything to the panel
            panel.add(formPanel, BorderLayout.NORTH);
            panel.add(buttonPanel, BorderLayout.SOUTH);
            
            getContentPane().add(panel);
            pack();
        }
        
        /**
         * Populates the fields with terrain data
         */
        private void populateFields() {
            if (terrain != null) {
                nameField.setText(terrain.getName());
                descriptionField.setText(terrain.getDescription());
                imagePathField.setText(terrain.getImagePath() != null ? terrain.getImagePath() : "");
                movementCostSpinner.setValue(terrain.getMovementCost());
                strengthCostSpinner.setValue(terrain.getStrengthCost());
                thirstCostSpinner.setValue(terrain.getThirstCost());
                goldCostSpinner.setValue(terrain.getGoldCost());
            }
        }
        
        /**
         * Updates the terrain with the field values
         */
        private void updateTerrain() {
            if (terrain != null) {
                terrain.setName(nameField.getText());
                terrain.setDescription(descriptionField.getText());
                terrain.setImagePath(imagePathField.getText().isEmpty() ? null : imagePathField.getText());
                terrain.setMovementCost((Integer) movementCostSpinner.getValue());
                terrain.setStrengthCost((Integer) strengthCostSpinner.getValue());
                terrain.setThirstCost((Integer) thirstCostSpinner.getValue());
                terrain.setGoldCost((Integer) goldCostSpinner.getValue());
            }
        }
        
        /**
         * @return whether the dialog was accepted
         */
        public boolean wasAccepted() {
            return accepted;
        }
        
        /**
         * @return the edited terrain
         */
        public Terrain getTerrain() {
            return terrain;
        }
    }
}
