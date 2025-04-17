package objectEditor.editor;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import objectEditor.model.*;
import objectEditor.util.ErrorLogger;
import objectEditor.util.FileUtils;
import objectEditor.util.ImageCache;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel for editing terrain cells in the object editor.
 */
public class TerrainEditorPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // Terrain basic properties
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JTextField imagePathField;
    private JButton browseImageButton;
    private JLabel imagePreviewLabel;
    
    // Terrain properties
    private JSpinner movementCostSpinner;
    private JSpinner strengthCostSpinner;
    private JSpinner thirstCostSpinner;
    private JSpinner goldCostSpinner;
    
    // Contained objects lists
    private JList<String> spawnerList;
    private JList<String> traderList;
    private JList<String> creatureList;
    private JList<String> itemList;
    
    // Available objects lists for selection
    private List<Spawner> availableSpawners;
    private List<Trader> availableTraders;
    private List<Creature> availableCreatures;
    private List<Item> availableItems;
    
    // Object management buttons
    private JButton addSpawnerButton;
    private JButton removeSpawnerButton;
    private JButton addTraderButton;
    private JButton removeTraderButton;
    private JButton addCreatureButton;
    private JButton removeCreatureButton;
    private JButton addItemButton;
    private JButton removeItemButton;
    
    // Preview panel for terrain appearance
    private JPanel previewPanel;
    
    // Action buttons
    private JButton newButton;
    private JButton saveButton;
    private JButton loadButton;
    
    // Current terrain and state
    private Terrain currentTerrain;
    private boolean dirty;
    private String currentFile;
    private JLabel fileLabel;
    
    /**
     * Creates a new terrain editor panel.
     */
    public TerrainEditorPanel() {
        initializeTerrainEditor();
        loadAvailableObjects();
    }
    
    /**
     * Initializes the terrain editor UI components.
     */
    private void initializeTerrainEditor() {
        setLayout(new BorderLayout(10, 10));
        
        // Create the top panel with basic properties
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        
        // Create the basic properties panel
        JPanel basicPropertiesPanel = new JPanel(new GridBagLayout());
        basicPropertiesPanel.setBorder(BorderFactory.createTitledBorder("Basic Properties"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Name field
        gbc.gridx = 0;
        gbc.gridy = 0;
        basicPropertiesPanel.add(new JLabel("Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        basicPropertiesPanel.add(nameField, gbc);
        
        // Description field
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        basicPropertiesPanel.add(new JLabel("Description:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        gbc.gridheight = 2;
        descriptionArea = new JTextArea(3, 20);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descScrollPane = new JScrollPane(descriptionArea);
        basicPropertiesPanel.add(descScrollPane, gbc);
        
        // Image path
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridheight = 1;
        gbc.weightx = 0.0;
        basicPropertiesPanel.add(new JLabel("Image:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        JPanel imagePathPanel = new JPanel(new BorderLayout(5, 0));
        imagePathField = new JTextField();
        browseImageButton = new JButton("Browse...");
        imagePathPanel.add(imagePathField, BorderLayout.CENTER);
        imagePathPanel.add(browseImageButton, BorderLayout.EAST);
        basicPropertiesPanel.add(imagePathPanel, gbc);
        
        // Image preview
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        imagePreviewLabel = new JLabel();
        imagePreviewLabel.setPreferredSize(new Dimension(100, 100));
        imagePreviewLabel.setBorder(BorderFactory.createEtchedBorder());
        imagePreviewLabel.setHorizontalAlignment(SwingConstants.CENTER);
        basicPropertiesPanel.add(imagePreviewLabel, gbc);
        
        // Create the cost spinners
        JPanel costsPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        costsPanel.setBorder(BorderFactory.createTitledBorder("Terrain Costs"));
        
        costsPanel.add(new JLabel("Movement Cost:"));
        movementCostSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        costsPanel.add(movementCostSpinner);
        
        costsPanel.add(new JLabel("Strength Cost:"));
        strengthCostSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        costsPanel.add(strengthCostSpinner);
        
        costsPanel.add(new JLabel("Thirst Cost:"));
        thirstCostSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        costsPanel.add(thirstCostSpinner);
        
        costsPanel.add(new JLabel("Gold Cost:"));
        goldCostSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        costsPanel.add(goldCostSpinner);
        
        // Create action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        
        newButton = new JButton("New Terrain");
        newButton.addActionListener(e -> createNewTerrain());
        
        saveButton = new JButton("Save Terrain");
        saveButton.addActionListener(e -> saveTerrain());
        
        loadButton = new JButton("Load Terrain");
        loadButton.addActionListener(e -> loadTerrain());
        
        actionPanel.add(newButton);
        actionPanel.add(saveButton);
        actionPanel.add(loadButton);
        
        // Add file label
        fileLabel = new JLabel("Current File: [None]");
        JPanel fileLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        fileLabelPanel.add(fileLabel);
        
        // Add components to top panel
        JPanel topLeftPanel = new JPanel(new BorderLayout());
        topLeftPanel.add(basicPropertiesPanel, BorderLayout.CENTER);
        
        JPanel topRightPanel = new JPanel(new BorderLayout());
        topRightPanel.add(costsPanel, BorderLayout.NORTH);
        
        topPanel.add(topLeftPanel, BorderLayout.WEST);
        topPanel.add(topRightPanel, BorderLayout.CENTER);
        
        JPanel topControlPanel = new JPanel(new BorderLayout());
        topControlPanel.add(actionPanel, BorderLayout.NORTH);
        topControlPanel.add(fileLabelPanel, BorderLayout.SOUTH);
        
        topPanel.add(topControlPanel, BorderLayout.SOUTH);
        
        // Create the contained objects panel
        JPanel objectsPanel = new JPanel(new GridLayout(1, 4, 10, 0));
        objectsPanel.setBorder(BorderFactory.createTitledBorder("Contained Objects"));
        
        // Spawners
        JPanel spawnersPanel = createObjectListPanel("Spawners", 
            spawnerList = new JList<>(), 
            addSpawnerButton = new JButton("Add"), 
            removeSpawnerButton = new JButton("Remove"));
        
        // Traders
        JPanel tradersPanel = createObjectListPanel("Traders", 
            traderList = new JList<>(), 
            addTraderButton = new JButton("Add"), 
            removeTraderButton = new JButton("Remove"));
        
        // Creatures
        JPanel creaturesPanel = createObjectListPanel("Creatures", 
            creatureList = new JList<>(), 
            addCreatureButton = new JButton("Add"), 
            removeCreatureButton = new JButton("Remove"));
        
        // Items
        JPanel itemsPanel = createObjectListPanel("Items", 
            itemList = new JList<>(), 
            addItemButton = new JButton("Add"), 
            removeItemButton = new JButton("Remove"));
        
        // Add object list panels to container
        objectsPanel.add(spawnersPanel);
        objectsPanel.add(tradersPanel);
        objectsPanel.add(creaturesPanel);
        objectsPanel.add(itemsPanel);
        
        // Create the preview panel
        previewPanel = new JPanel() {
            private static final long serialVersionUID = 1L;
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Paint the terrain preview
                if (currentTerrain != null) {
                    if (currentTerrain.getImagePath() != null && !currentTerrain.getImagePath().isEmpty()) {
                        try {
                            BufferedImage img = ImageCache.getImage(currentTerrain.getImagePath());
                            if (img != null) {
                                int width = getWidth();
                                int height = getHeight();
                                int imgWidth = img.getWidth();
                                int imgHeight = img.getHeight();
                                
                                // Calculate scaling to fit the panel while maintaining aspect ratio
                                double scale = Math.min((double)width / imgWidth, (double)height / imgHeight);
                                int scaledWidth = (int)(imgWidth * scale);
                                int scaledHeight = (int)(imgHeight * scale);
                                
                                // Draw the image centered in the panel
                                g.drawImage(img, (width - scaledWidth) / 2, (height - scaledHeight) / 2, 
                                           scaledWidth, scaledHeight, this);
                            } else {
                                g.setColor(Color.LIGHT_GRAY);
                                g.fillRect(0, 0, getWidth(), getHeight());
                                g.setColor(Color.RED);
                                g.drawString("Image not found: " + currentTerrain.getImagePath(), 10, getHeight() / 2);
                            }
                        } catch (Exception e) {
                            ErrorLogger.logError("Error rendering terrain preview", e);
                            g.setColor(Color.RED);
                            g.drawString("Error loading image", 10, getHeight() / 2);
                        }
                    } else {
                        // Draw a colored rectangle if no image
                        g.setColor(getTerrainColor(currentTerrain));
                        g.fillRect(0, 0, getWidth(), getHeight());
                        g.setColor(Color.BLACK);
                        g.drawString(currentTerrain.getName(), 10, getHeight() / 2);
                    }
                } else {
                    // Draw placeholder if no terrain
                    g.setColor(Color.LIGHT_GRAY);
                    g.fillRect(0, 0, getWidth(), getHeight());
                    g.setColor(Color.BLACK);
                    g.drawString("No terrain selected", 10, getHeight() / 2);
                }
            }
        };
        previewPanel.setPreferredSize(new Dimension(200, 200));
        previewPanel.setBorder(BorderFactory.createTitledBorder("Terrain Preview"));
        
        // Set up button actions
        setupButtonActions();
        
        // Create main layout
        add(topPanel, BorderLayout.NORTH);
        add(objectsPanel, BorderLayout.CENTER);
        add(previewPanel, BorderLayout.SOUTH);
        
        // Initialize with a new terrain
        createNewTerrain();
    }
    
    /**
     * Creates a panel containing a list and add/remove buttons for object management
     */
    private JPanel createObjectListPanel(String title, JList<String> list, JButton addButton, JButton removeButton) {
        JPanel panel = new JPanel(new BorderLayout(0, 5));
        panel.setBorder(BorderFactory.createTitledBorder(title));
        
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane scrollPane = new JScrollPane(list);
        scrollPane.setPreferredSize(new Dimension(150, 150));
        
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Sets up the button actions for object management
     */
    private void setupButtonActions() {
        // Image browse button
        browseImageButton.addActionListener(e -> browseImage());
        
        // Spawner buttons
        addSpawnerButton.addActionListener(e -> addSpawnerToTerrain());
        removeSpawnerButton.addActionListener(e -> removeSpawnerFromTerrain());
        
        // Trader buttons
        addTraderButton.addActionListener(e -> addTraderToTerrain());
        removeTraderButton.addActionListener(e -> removeTraderFromTerrain());
        
        // Creature buttons
        addCreatureButton.addActionListener(e -> addCreatureToTerrain());
        removeCreatureButton.addActionListener(e -> removeCreatureFromTerrain());
        
        // Item buttons
        addItemButton.addActionListener(e -> addItemToTerrain());
        removeItemButton.addActionListener(e -> removeItemFromTerrain());
    }
    
    /**
     * Loads available objects from the exports directory
     */
    private void loadAvailableObjects() {
        loadAvailableSpawners();
        loadAvailableTraders();
        loadAvailableCreatures();
        loadAvailableItems();
    }
    
    /**
     * Loads available spawners
     */
    private void loadAvailableSpawners() {
        availableSpawners = new ArrayList<>();
        
        File spawnerDir = new File("exports/spawner/");
        if (spawnerDir.exists() && spawnerDir.isDirectory()) {
            File[] spawnerFiles = spawnerDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
            if (spawnerFiles != null) {
                for (File file : spawnerFiles) {
                    try {
                        Spawner spawner = FileUtils.loadSpawner(file.getPath());
                        if (spawner != null) {
                            availableSpawners.add(spawner);
                        }
                    } catch (Exception e) {
                        ErrorLogger.logError("Error loading spawner: " + file.getName(), e);
                    }
                }
            }
        }
    }
    
    /**
     * Loads available traders
     */
    private void loadAvailableTraders() {
        availableTraders = new ArrayList<>();
        
        File traderDir = new File("exports/trader/");
        if (traderDir.exists() && traderDir.isDirectory()) {
            File[] traderFiles = traderDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
            if (traderFiles != null) {
                for (File file : traderFiles) {
                    try {
                        Trader trader = FileUtils.loadTrader(file.getPath());
                        if (trader != null) {
                            availableTraders.add(trader);
                        }
                    } catch (Exception e) {
                        ErrorLogger.logError("Error loading trader: " + file.getName(), e);
                    }
                }
            }
        }
    }
    
    /**
     * Loads available creatures
     */
    private void loadAvailableCreatures() {
        availableCreatures = new ArrayList<>();
        
        File creatureDir = new File("exports/creature/");
        if (creatureDir.exists() && creatureDir.isDirectory()) {
            File[] creatureFiles = creatureDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
            if (creatureFiles != null) {
                for (File file : creatureFiles) {
                    try {
                        Creature creature = FileUtils.loadCreature(file.getPath());
                        if (creature != null) {
                            availableCreatures.add(creature);
                        }
                    } catch (Exception e) {
                        ErrorLogger.logError("Error loading creature: " + file.getName(), e);
                    }
                }
            }
        }
    }
    
    /**
     * Loads available items
     */
    private void loadAvailableItems() {
        availableItems = new ArrayList<>();
        
        File itemDir = new File("exports/item/");
        if (itemDir.exists() && itemDir.isDirectory()) {
            File[] itemFiles = itemDir.listFiles((dir, name) -> name.toLowerCase().endsWith(".txt"));
            if (itemFiles != null) {
                for (File file : itemFiles) {
                    try {
                        Item item = FileUtils.loadItem(file.getPath());
                        if (item != null) {
                            availableItems.add(item);
                        }
                    } catch (Exception e) {
                        ErrorLogger.logError("Error loading item: " + file.getName(), e);
                    }
                }
            }
        }
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
     * Creates a new terrain with default values
     */
    private void createNewTerrain() {
        currentTerrain = new Terrain();
        currentTerrain.setName("New Terrain");
        currentTerrain.setDescription("");
        currentTerrain.setMovementCost(0);
        currentTerrain.setStrengthCost(0);
        currentTerrain.setThirstCost(0);
        currentTerrain.setGoldCost(0);
        
        updateUIFromTerrain();
        
        currentFile = null;
        fileLabel.setText("Current File: [None]");
        
        setDirty(false);
    }
    
    /**
     * Updates the UI to display the current terrain
     */
    private void updateUIFromTerrain() {
        if (currentTerrain != null) {
            // Update basic fields
            nameField.setText(currentTerrain.getName());
            descriptionArea.setText(currentTerrain.getDescription());
            imagePathField.setText(currentTerrain.getImagePath() != null ? currentTerrain.getImagePath() : "");
            
            // Update cost spinners
            movementCostSpinner.setValue(currentTerrain.getMovementCost());
            strengthCostSpinner.setValue(currentTerrain.getStrengthCost());
            thirstCostSpinner.setValue(currentTerrain.getThirstCost());
            goldCostSpinner.setValue(currentTerrain.getGoldCost());
            
            // Update image preview
            updateImagePreview();
            
            // Update the contained objects lists
            updateContainedObjectsLists();
            
            // Update the preview panel
            previewPanel.repaint();
        }
    }
    
    /**
     * Updates the image preview
     */
    private void updateImagePreview() {
        if (currentTerrain != null && currentTerrain.getImagePath() != null && !currentTerrain.getImagePath().isEmpty()) {
            BufferedImage img = ImageCache.getImage(currentTerrain.getImagePath());
            if (img != null) {
                // Scale image for preview
                Image scaledImg = img.getScaledInstance(100, 100, Image.SCALE_SMOOTH);
                imagePreviewLabel.setIcon(new ImageIcon(scaledImg));
            } else {
                imagePreviewLabel.setIcon(null);
                imagePreviewLabel.setText("Image not found");
            }
        } else {
            imagePreviewLabel.setIcon(null);
            imagePreviewLabel.setText("No image selected");
        }
    }
    
    /**
     * Updates the UI to display the contained objects list
     */
    private void updateContainedObjectsLists() {
        if (currentTerrain == null) return;
        
        // Update spawners list
        DefaultListModel<String> spawnerModel = new DefaultListModel<>();
        for (Spawner spawner : currentTerrain.getSpawners()) {
            spawnerModel.addElement(spawner.getName());
        }
        spawnerList.setModel(spawnerModel);
        
        // Update traders list
        DefaultListModel<String> traderModel = new DefaultListModel<>();
        for (Trader trader : currentTerrain.getTraders()) {
            traderModel.addElement(trader.getName());
        }
        traderList.setModel(traderModel);
        
        // Update creatures list
        DefaultListModel<String> creatureModel = new DefaultListModel<>();
        for (Creature creature : currentTerrain.getCreatures()) {
            creatureModel.addElement(creature.getName());
        }
        creatureList.setModel(creatureModel);
        
        // Update items list
        DefaultListModel<String> itemModel = new DefaultListModel<>();
        for (Item item : currentTerrain.getItems()) {
            itemModel.addElement(item.getName());
        }
        itemList.setModel(itemModel);
    }
    
    /**
     * Updates the terrain object from UI values
     */
    private void updateTerrainFromUI() {
        if (currentTerrain != null) {
            // Update basic properties
            currentTerrain.setName(nameField.getText());
            currentTerrain.setDescription(descriptionArea.getText());
            
            String imagePath = imagePathField.getText();
            currentTerrain.setImagePath(imagePath.isEmpty() ? null : imagePath);
            
            // Update the terrain costs
            currentTerrain.setMovementCost((Integer) movementCostSpinner.getValue());
            currentTerrain.setStrengthCost((Integer) strengthCostSpinner.getValue());
            currentTerrain.setThirstCost((Integer) thirstCostSpinner.getValue());
            currentTerrain.setGoldCost((Integer) goldCostSpinner.getValue());
        }
    }
    
    /**
     * Loads a terrain from file
     */
    private void loadTerrain() {
        JFileChooser fileChooser = new JFileChooser("exports/terrain");
        fileChooser.setDialogTitle("Load Terrain");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Terrain Files (*.txt)", "txt"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            Terrain loadedTerrain = FileUtils.loadTerrain(selectedFile.getPath());
            
            if (loadedTerrain != null) {
                currentTerrain = loadedTerrain;
                currentFile = selectedFile.getPath();
                fileLabel.setText("Current File: " + currentFile);
                
                updateUIFromTerrain();
                setDirty(false);
                
                JOptionPane.showMessageDialog(this, 
                    "Terrain loaded successfully from " + currentFile,
                    "Load Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Error loading terrain from " + selectedFile.getPath(),
                    "Load Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Saves the current terrain
     */
    private void saveTerrain() {
        if (currentTerrain == null) {
            JOptionPane.showMessageDialog(this, 
                "No terrain to save.",
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Update terrain from UI
        updateTerrainFromUI();
        
        // Generate file name if none exists
        if (currentFile == null) {
            currentFile = "exports/terrain/" + currentTerrain.getName() + ".txt";
        }
        
        // Ensure directories exist
        File dir = new File("exports/terrain");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        
        // Save the terrain
        if (FileUtils.saveTerrain(currentTerrain, currentFile)) {
            JOptionPane.showMessageDialog(this, 
                "Terrain saved successfully to " + currentFile,
                "Save Successful", JOptionPane.INFORMATION_MESSAGE);
            setDirty(false);
            fileLabel.setText("Current File: " + currentFile);
        } else {
            JOptionPane.showMessageDialog(this, 
                "Error saving terrain to " + currentFile,
                "Save Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Opens a file browser to select an image
     */
    private void browseImage() {
        JFileChooser fileChooser = new JFileChooser("resources/");
        fileChooser.setDialogTitle("Select Image");
        fileChooser.setFileFilter(new FileNameExtensionFilter(
            "Image Files (jpg, png, gif)", "jpg", "jpeg", "png", "gif"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            imagePathField.setText(selectedFile.getPath());
            
            // Update terrain and preview
            if (currentTerrain != null) {
                currentTerrain.setImagePath(selectedFile.getPath());
                updateImagePreview();
                previewPanel.repaint();
                setDirty(true);
            }
        }
    }
    
    /**
     * Adds a spawner to the terrain
     */
    private void addSpawnerToTerrain() {
        if (currentTerrain == null) {
            JOptionPane.showMessageDialog(this, "Please create or load a terrain first.");
            return;
        }
        
        if (availableSpawners.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No spawners available. Please create some spawners first.",
                "No Spawners", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create a dialog to select a spawner
        String[] spawnerNames = new String[availableSpawners.size()];
        for (int i = 0; i < availableSpawners.size(); i++) {
            spawnerNames[i] = availableSpawners.get(i).getName();
        }
        
        String selected = (String) JOptionPane.showInputDialog(this,
            "Select a spawner to add:",
            "Add Spawner",
            JOptionPane.QUESTION_MESSAGE,
            null,
            spawnerNames,
            spawnerNames[0]);
        
        if (selected != null) {
            // Find the selected spawner
            for (Spawner spawner : availableSpawners) {
                if (spawner.getName().equals(selected)) {
                    try {
                        // Clone the spawner before adding
                        Spawner clone = (Spawner) spawner.clone();
                        currentTerrain.addSpawner(clone);
                        setDirty(true);
                        updateContainedObjectsLists();
                        return;
                    } catch (Exception e) {
                        ErrorLogger.logError("Error adding spawner to terrain", e);
                        JOptionPane.showMessageDialog(this, 
                            "Error adding spawner: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
        }
    }
    
    /**
     * Removes a spawner from the terrain
     */
    private void removeSpawnerFromTerrain() {
        if (currentTerrain == null) {
            JOptionPane.showMessageDialog(this, "Please create or load a terrain first.");
            return;
        }
        
        int selectedIdx = spawnerList.getSelectedIndex();
        if (selectedIdx >= 0 && selectedIdx < currentTerrain.getSpawners().size()) {
            Spawner spawner = currentTerrain.getSpawners().get(selectedIdx);
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove the spawner '" + spawner.getName() + "'?",
                "Confirm Removal", JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                currentTerrain.removeSpawner(spawner);
                setDirty(true);
                updateContainedObjectsLists();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a spawner to remove.");
        }
    }
    
    /**
     * Adds a trader to the terrain
     */
    private void addTraderToTerrain() {
        if (currentTerrain == null) {
            JOptionPane.showMessageDialog(this, "Please create or load a terrain first.");
            return;
        }
        
        if (availableTraders.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No traders available. Please create some traders first.",
                "No Traders", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create a dialog to select a trader
        String[] traderNames = new String[availableTraders.size()];
        for (int i = 0; i < availableTraders.size(); i++) {
            traderNames[i] = availableTraders.get(i).getName();
        }
        
        String selected = (String) JOptionPane.showInputDialog(this,
            "Select a trader to add:",
            "Add Trader",
            JOptionPane.QUESTION_MESSAGE,
            null,
            traderNames,
            traderNames[0]);
        
        if (selected != null) {
            // Find the selected trader
            for (Trader trader : availableTraders) {
                if (trader.getName().equals(selected)) {
                    try {
                        // Clone the trader before adding
                        Trader clone = (Trader) trader.clone();
                        currentTerrain.addTrader(clone);
                        setDirty(true);
                        updateContainedObjectsLists();
                        return;
                    } catch (Exception e) {
                        ErrorLogger.logError("Error adding trader to terrain", e);
                        JOptionPane.showMessageDialog(this, 
                            "Error adding trader: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
        }
    }
    
    /**
     * Removes a trader from the terrain
     */
    private void removeTraderFromTerrain() {
        if (currentTerrain == null) {
            JOptionPane.showMessageDialog(this, "Please create or load a terrain first.");
            return;
        }
        
        int selectedIdx = traderList.getSelectedIndex();
        if (selectedIdx >= 0 && selectedIdx < currentTerrain.getTraders().size()) {
            Trader trader = currentTerrain.getTraders().get(selectedIdx);
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove the trader '" + trader.getName() + "'?",
                "Confirm Removal", JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                currentTerrain.removeTrader(trader);
                setDirty(true);
                updateContainedObjectsLists();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a trader to remove.");
        }
    }
    
    /**
     * Adds a creature to the terrain
     */
    private void addCreatureToTerrain() {
        if (currentTerrain == null) {
            JOptionPane.showMessageDialog(this, "Please create or load a terrain first.");
            return;
        }
        
        if (availableCreatures.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No creatures available. Please create some creatures first.",
                "No Creatures", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create a dialog to select a creature
        String[] creatureNames = new String[availableCreatures.size()];
        for (int i = 0; i < availableCreatures.size(); i++) {
            creatureNames[i] = availableCreatures.get(i).getName();
        }
        
        String selected = (String) JOptionPane.showInputDialog(this,
            "Select a creature to add:",
            "Add Creature",
            JOptionPane.QUESTION_MESSAGE,
            null,
            creatureNames,
            creatureNames[0]);
        
        if (selected != null) {
            // Find the selected creature
            for (Creature creature : availableCreatures) {
                if (creature.getName().equals(selected)) {
                    try {
                        // Clone the creature before adding
                        Creature clone = (Creature) creature.clone();
                        currentTerrain.addCreature(clone);
                        setDirty(true);
                        updateContainedObjectsLists();
                        return;
                    } catch (Exception e) {
                        ErrorLogger.logError("Error adding creature to terrain", e);
                        JOptionPane.showMessageDialog(this, 
                            "Error adding creature: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
        }
    }
    
    /**
     * Removes a creature from the terrain
     */
    private void removeCreatureFromTerrain() {
        if (currentTerrain == null) {
            JOptionPane.showMessageDialog(this, "Please create or load a terrain first.");
            return;
        }
        
        int selectedIdx = creatureList.getSelectedIndex();
        if (selectedIdx >= 0 && selectedIdx < currentTerrain.getCreatures().size()) {
            Creature creature = currentTerrain.getCreatures().get(selectedIdx);
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove the creature '" + creature.getName() + "'?",
                "Confirm Removal", JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                currentTerrain.removeCreature(creature);
                setDirty(true);
                updateContainedObjectsLists();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a creature to remove.");
        }
    }
    
    /**
     * Adds an item to the terrain
     */
    private void addItemToTerrain() {
        if (currentTerrain == null) {
            JOptionPane.showMessageDialog(this, "Please create or load a terrain first.");
            return;
        }
        
        if (availableItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No items available. Please create some items first.",
                "No Items", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        // Create a dialog to select an item
        String[] itemNames = new String[availableItems.size()];
        for (int i = 0; i < availableItems.size(); i++) {
            itemNames[i] = availableItems.get(i).getName();
        }
        
        String selected = (String) JOptionPane.showInputDialog(this,
            "Select an item to add:",
            "Add Item",
            JOptionPane.QUESTION_MESSAGE,
            null,
            itemNames,
            itemNames[0]);
        
        if (selected != null) {
            // Find the selected item
            for (Item item : availableItems) {
                if (item.getName().equals(selected)) {
                    try {
                        // Clone the item before adding
                        Item clone = (Item) item.clone();
                        currentTerrain.addItem(clone);
                        setDirty(true);
                        updateContainedObjectsLists();
                        return;
                    } catch (Exception e) {
                        ErrorLogger.logError("Error adding item to terrain", e);
                        JOptionPane.showMessageDialog(this, 
                            "Error adding item: " + e.getMessage(),
                            "Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
        }
    }
    
    /**
     * Removes an item from the terrain
     */
    private void removeItemFromTerrain() {
        if (currentTerrain == null) {
            JOptionPane.showMessageDialog(this, "Please create or load a terrain first.");
            return;
        }
        
        int selectedIdx = itemList.getSelectedIndex();
        if (selectedIdx >= 0 && selectedIdx < currentTerrain.getItems().size()) {
            Item item = currentTerrain.getItems().get(selectedIdx);
            
            int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to remove the item '" + item.getName() + "'?",
                "Confirm Removal", JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                currentTerrain.removeItem(item);
                setDirty(true);
                updateContainedObjectsLists();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to remove.");
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
     * Refreshes the available objects lists
     */
    public void refreshAvailableObjects() {
        loadAvailableObjects();
    }
}
