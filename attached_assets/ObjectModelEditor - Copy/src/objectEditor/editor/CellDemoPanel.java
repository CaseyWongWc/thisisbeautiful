package objectEditor.editor;

import objectEditor.model.*;
import objectEditor.util.FileUtils;
import objectEditor.util.ImageUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A panel for demonstrating the MapCell rendering capabilities.
 * This panel allows selecting a terrain and adding various entities to visualize
 * how a cell would look with different configurations.
 */
public class CellDemoPanel extends BaseEditorPanel<ObjectInstance> {
    private MapCell currentCell;
    private JPanel renderPanel;
    private JLabel terrainInfoLabel;
    private JLabel terrainThumbnailLabel;
    private JComboBox<String> entityTypeComboBox;
    private JComboBox<String> entityNameComboBox;
    private JComboBox<String> terrainComboBox;
    private JButton addEntityButton;
    private JButton clearEntitiesButton;
    private JButton addTerrainButton;
    private JButton removeTerrainButton;
    private JList<String> entityList;
    private DefaultListModel<String> entityListModel;
    private JPanel previewPanel;
    private int cellSize = 120;

    // Maps to store loaded objects
    private List<Item> loadedItems;
    private List<Creature> loadedCreatures;
    private List<Spawner> loadedSpawners;
    private List<Trader> loadedTraders;
    private List<Terrain> loadedTerrains;
    
    // Store entity references for the current cell
    private List<ObjectInstance> cellEntities;

    /**
     * Creates a new cell demo panel.
     */
    public CellDemoPanel() {
        super("cell", "Cell");
        
        // Initialize collections
        loadedItems = new ArrayList<>();
        loadedCreatures = new ArrayList<>();
        loadedSpawners = new ArrayList<>();
        loadedTraders = new ArrayList<>();
        loadedTerrains = new ArrayList<>();
        cellEntities = new ArrayList<>();
        
        // Initialize the current cell
        currentCell = new MapCell();
        
        initComponents();
        setupListeners();
        loadObjects();
    }

    /**
     * Initializes the UI components.
     */
    private void initComponents() {
        // Set up main layout
        setLayout(new BorderLayout(10, 10));
        
        // Left panel - Terrain selection
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Terrain"));
        
        // Create terrain thumbnail panel
        JPanel terrainSelectionPanel = createTerrainSelectionPanel();
        leftPanel.add(terrainSelectionPanel, BorderLayout.CENTER);
        
        terrainInfoLabel = new JLabel("No terrain selected");
        leftPanel.add(terrainInfoLabel, BorderLayout.SOUTH);
        
        // Center panel - Cell preview
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBorder(BorderFactory.createTitledBorder("Cell Preview"));
        
        previewPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                
                // Draw cell centered in panel
                if (currentCell != null) {
                    int x = (getWidth() - cellSize) / 2;
                    int y = (getHeight() - cellSize) / 2;
                    currentCell.render(g, x, y, cellSize);
                }
            }
        };
        previewPanel.setPreferredSize(new Dimension(150, 150));
        centerPanel.add(previewPanel, BorderLayout.CENTER);
        
        // Add cell size slider
        JSlider sizeSlider = new JSlider(JSlider.HORIZONTAL, 50, 200, cellSize);
        sizeSlider.setMajorTickSpacing(50);
        sizeSlider.setMinorTickSpacing(10);
        sizeSlider.setPaintTicks(true);
        sizeSlider.setPaintLabels(true);
        sizeSlider.addChangeListener(e -> {
            cellSize = sizeSlider.getValue();
            previewPanel.repaint();
        });
        
        JPanel sliderPanel = new JPanel(new BorderLayout());
        sliderPanel.setBorder(BorderFactory.createTitledBorder("Cell Size"));
        sliderPanel.add(sizeSlider, BorderLayout.CENTER);
        centerPanel.add(sliderPanel, BorderLayout.SOUTH);
        
        // Right panel - Entity control
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Entities"));
        
        // Entity selector
        JPanel entitySelectorPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        entitySelectorPanel.add(new JLabel("Entity Type:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        String[] entityTypes = {"Item", "Creature", "Trader", "Spawner"};
        entityTypeComboBox = new JComboBox<>(entityTypes);
        entityTypeComboBox.addActionListener(e -> updateEntityNameComboBox());
        entitySelectorPanel.add(entityTypeComboBox, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        entitySelectorPanel.add(new JLabel("Entity Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        entityNameComboBox = new JComboBox<>();
        entitySelectorPanel.add(entityNameComboBox, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        addEntityButton = new JButton("Add Entity");
        clearEntitiesButton = new JButton("Clear All");
        buttonPanel.add(addEntityButton);
        buttonPanel.add(clearEntitiesButton);
        entitySelectorPanel.add(buttonPanel, gbc);
        
        rightPanel.add(entitySelectorPanel, BorderLayout.NORTH);
        
        // Entity list
        entityListModel = new DefaultListModel<>();
        entityList = new JList<>(entityListModel);
        entityList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane entityScrollPane = new JScrollPane(entityList);
        entityScrollPane.setBorder(BorderFactory.createTitledBorder("Entities in Cell"));
        entityScrollPane.setPreferredSize(new Dimension(200, 150));
        rightPanel.add(entityScrollPane, BorderLayout.CENTER);
        
        // Remove button
        JButton removeEntityButton = new JButton("Remove Selected Entity");
        removeEntityButton.addActionListener(e -> removeSelectedEntity());
        rightPanel.add(removeEntityButton, BorderLayout.SOUTH);
        
        // Add panels to main layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(leftPanel, BorderLayout.WEST);
        topPanel.add(centerPanel, BorderLayout.CENTER);
        topPanel.add(rightPanel, BorderLayout.EAST);
        
        add(topPanel, BorderLayout.CENTER);
        
        // Grid info panel (shows information about how the grid scales)
        JPanel gridInfoPanel = new JPanel();
        gridInfoPanel.setBorder(BorderFactory.createTitledBorder("Grid Sizing Information"));
        gridInfoPanel.setLayout(new BoxLayout(gridInfoPanel, BoxLayout.Y_AXIS));
        
        gridInfoPanel.add(new JLabel("• 0 entities: Terrain only"));
        gridInfoPanel.add(new JLabel("• 1-2 entities: 1×2 grid"));
        gridInfoPanel.add(new JLabel("• 3-4 entities: 2×2 grid"));
        gridInfoPanel.add(new JLabel("• 5+ entities: 3×2 grid"));
        gridInfoPanel.add(Box.createVerticalStrut(5));
        gridInfoPanel.add(new JLabel("Note: Excess entities will be shown with a +X indicator"));
        
        add(gridInfoPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates the terrain selection panel with a thumbnail image and dropdown.
     * 
     * @return The terrain selection panel
     */
    private JPanel createTerrainSelectionPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        
        // Create the terrain selection combo box
        terrainComboBox = new JComboBox<>();
        // Will be populated in loadObjects()
        
        panel.add(new JLabel("Select Terrain:"), BorderLayout.NORTH);
        panel.add(terrainComboBox, BorderLayout.CENTER);
        
        // Create thumbnail panel
        terrainThumbnailLabel = new JLabel();
        terrainThumbnailLabel.setHorizontalAlignment(SwingConstants.CENTER);
        terrainThumbnailLabel.setVerticalAlignment(SwingConstants.CENTER);
        terrainThumbnailLabel.setPreferredSize(new Dimension(100, 100));
        terrainThumbnailLabel.setBorder(BorderFactory.createEtchedBorder());
        terrainThumbnailLabel.setText("No terrain selected");
        
        JPanel thumbnailPanel = new JPanel(new BorderLayout());
        thumbnailPanel.setBorder(BorderFactory.createTitledBorder("Preview"));
        thumbnailPanel.add(terrainThumbnailLabel, BorderLayout.CENTER);
        panel.add(thumbnailPanel, BorderLayout.SOUTH);
        
        // Add terrain buttons - these will now set the background terrain, not add as entities
        JPanel terrainButtonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        addTerrainButton = new JButton("Set Terrain");
        removeTerrainButton = new JButton("Clear Terrain");
        terrainButtonPanel.add(addTerrainButton);
        terrainButtonPanel.add(removeTerrainButton);
        
        // Add the buttons below the thumbnail
        thumbnailPanel.add(terrainButtonPanel, BorderLayout.SOUTH);
        
        // Add listener to combo box
        terrainComboBox.addActionListener(e -> {
            int selectedIndex = terrainComboBox.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < loadedTerrains.size()) {
                Terrain selectedTerrain = loadedTerrains.get(selectedIndex);
                updateTerrainThumbnail(selectedTerrain);
                updateTerrainInfo(selectedTerrain);
            }
        });
        
        return panel;
    }
    
    /**
     * Updates the terrain thumbnail with the image from the selected terrain.
     * 
     * @param terrain The terrain to display
     */
    private void updateTerrainThumbnail(Terrain terrain) {
        if (terrain != null && terrain.getImagePath() != null && !terrain.getImagePath().isEmpty()) {
            ImageIcon icon = ImageUtils.loadImageIcon(terrain.getImagePath());
            if (icon != null) {
                // Resize the icon to fit the thumbnail
                Image img = icon.getImage();
                Image resizedImg = img.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                terrainThumbnailLabel.setIcon(new ImageIcon(resizedImg));
                terrainThumbnailLabel.setText("");
            } else {
                terrainThumbnailLabel.setIcon(null);
                terrainThumbnailLabel.setText("No image");
            }
        } else {
            terrainThumbnailLabel.setIcon(null);
            terrainThumbnailLabel.setText("No terrain selected");
        }
    }

    /**
     * Updates the terrain information label.
     * 
     * @param terrain The terrain to display information for
     */
    private void updateTerrainInfo(Terrain terrain) {
        if (terrain != null) {
            StringBuilder info = new StringBuilder("<html>");
            info.append("Name: ").append(terrain.getName()).append("<br>");
            info.append("Costs - Movement: ").append(terrain.getMovementCost());
            info.append(", Strength: ").append(terrain.getStrengthCost());
            info.append("<br>Thirst: ").append(terrain.getThirstCost());
            info.append(", Gold: ").append(terrain.getGoldCost());
            info.append("</html>");
            
            terrainInfoLabel.setText(info.toString());
        } else {
            terrainInfoLabel.setText("No terrain selected");
        }
    }

    /**
     * Updates the entity name combo box based on the selected entity type.
     */
    private void updateEntityNameComboBox() {
        entityNameComboBox.removeAllItems();
        
        String selectedType = (String) entityTypeComboBox.getSelectedItem();
        
        if ("Item".equals(selectedType)) {
            for (Item item : loadedItems) {
                entityNameComboBox.addItem(item.getName());
            }
        } else if ("Creature".equals(selectedType)) {
            for (Creature creature : loadedCreatures) {
                entityNameComboBox.addItem(creature.getName());
            }
        } else if ("Trader".equals(selectedType)) {
            for (Trader trader : loadedTraders) {
                entityNameComboBox.addItem(trader.getName());
            }
        } else if ("Spawner".equals(selectedType)) {
            for (Spawner spawner : loadedSpawners) {
                entityNameComboBox.addItem(spawner.getName());
            }
        }
    }

    /**
     * Sets up the event listeners.
     */
    private void setupListeners() {
        addEntityButton.addActionListener(e -> addEntityToCell());
        clearEntitiesButton.addActionListener(e -> clearEntities());
        addTerrainButton.addActionListener(e -> setTerrainForCell());
        removeTerrainButton.addActionListener(e -> clearTerrainForCell());
    }

    /**
     * Sets the selected terrain as the cell's background.
     */
    private void setTerrainForCell() {
        int selectedIndex = terrainComboBox.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < loadedTerrains.size()) {
            Terrain selectedTerrain = loadedTerrains.get(selectedIndex);
            
            // Set the terrain as the cell's background
            currentCell.setTerrain(selectedTerrain);
            previewPanel.repaint();
            
            // Update the entity list to show the current terrain
            updateEntityList();
        }
    }
    
    /**
     * Clears the terrain from the cell.
     */
    private void clearTerrainForCell() {
        currentCell.setTerrain(null);
        previewPanel.repaint();
        updateEntityList();
    }

    /**
     * Adds the selected entity to the cell.
     */
    private void addEntityToCell() {
        String selectedType = (String) entityTypeComboBox.getSelectedItem();
        String selectedName = (String) entityNameComboBox.getSelectedItem();
        
        if (selectedType == null || selectedName == null) {
            return;
        }
        
        ObjectInstance entity = null;
        
        // Find the selected entity
        if ("Item".equals(selectedType)) {
            for (Item item : loadedItems) {
                if (item.getName().equals(selectedName)) {
                    entity = item;
                    break;
                }
            }
        } else if ("Creature".equals(selectedType)) {
            for (Creature creature : loadedCreatures) {
                if (creature.getName().equals(selectedName)) {
                    entity = creature;
                    break;
                }
            }
        } else if ("Trader".equals(selectedType)) {
            for (Trader trader : loadedTraders) {
                if (trader.getName().equals(selectedName)) {
                    entity = trader;
                    break;
                }
            }
        } else if ("Spawner".equals(selectedType)) {
            for (Spawner spawner : loadedSpawners) {
                if (spawner.getName().equals(selectedName)) {
                    entity = spawner;
                    break;
                }
            }
        }
        
        if (entity != null) {
            currentCell.addEntity(entity);
            cellEntities.add(entity);
            updateEntityList();
            previewPanel.repaint();
        }
    }

    /**
     * Removes the selected entity from the cell.
     */
    private void removeSelectedEntity() {
        int selectedIndex = entityList.getSelectedIndex();
        if (selectedIndex >= 0 && selectedIndex < cellEntities.size()) {
            ObjectInstance entity = cellEntities.get(selectedIndex);
            currentCell.removeEntity(entity);
            cellEntities.remove(selectedIndex);
            updateEntityList();
            previewPanel.repaint();
        }
    }

    /**
     * Clears all entities from the cell.
     */
    private void clearEntities() {
        currentCell.clearEntities();
        cellEntities.clear();
        updateEntityList();
        previewPanel.repaint();
    }

    /**
     * Updates the entity list with the current cell entities.
     */
    private void updateEntityList() {
        entityListModel.clear();
        
        // Add terrain info at the top of the entity list
        Terrain currentTerrain = currentCell.getTerrain();
        if (currentTerrain != null) {
            entityListModel.addElement("Terrain: " + currentTerrain.getName());
        } else {
            entityListModel.addElement("Terrain: None");
        }
        
        // Add all other entities
        for (ObjectInstance entity : cellEntities) {
            // Skip terrains as they are now handled separately
            if (entity instanceof Terrain) {
                continue;
            }
            
            String typeName = "Unknown";
            if (entity instanceof Item) {
                typeName = "Item";
            } else if (entity instanceof Creature) {
                typeName = "Creature";
            } else if (entity instanceof Trader) {
                typeName = "Trader";
            } else if (entity instanceof Spawner) {
                typeName = "Spawner";
            }
            
            entityListModel.addElement(typeName + ": " + entity.getName());
        }
    }

    /**
     * Loads objects from the exports directory.
     */
    @Override
    protected void loadObjects() {
        loadedItems = loadItems();
        loadedCreatures = loadCreatures();
        loadedTraders = loadTraders();
        loadedSpawners = loadSpawners();
        loadedTerrains = loadTerrains();
        
        updateEntityNameComboBox();
        
        // Initialize terrain combo box with available terrains
        terrainComboBox.removeAllItems();
        for (Terrain terrain : loadedTerrains) {
            terrainComboBox.addItem(terrain.getName());
        }
        
        // Initialize terrain thumbnail if terrains are loaded
        if (loadedTerrains.size() > 0) {
            terrainComboBox.setSelectedIndex(0);
            Terrain firstTerrain = loadedTerrains.get(0);
            updateTerrainThumbnail(firstTerrain);
            updateTerrainInfo(firstTerrain);
        }
        
        // Update the entity list
        updateEntityList();
    }

    /**
     * Loads items from the exports directory.
     * 
     * @return The list of loaded items
     */
    private List<Item> loadItems() {
        List<Item> items = new ArrayList<>();
        File dir = new File("exports/item");
        
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".txt"));
            
            if (files != null) {
                for (File file : files) {
                    try {
                        Item item = FileUtils.loadItem(file.getPath());
                        if (item != null) {
                            items.add(item);
                        }
                    } catch (Exception e) {
                        System.err.println("Error loading item: " + file.getName());
                        e.printStackTrace();
                    }
                }
            }
        }
        
        return items;
    }

    /**
     * Loads creatures from the exports directory.
     * 
     * @return The list of loaded creatures
     */
    private List<Creature> loadCreatures() {
        List<Creature> creatures = new ArrayList<>();
        File dir = new File("exports/creature");
        
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".txt"));
            
            if (files != null) {
                for (File file : files) {
                    try {
                        System.out.println("Loading creature from " + file.getPath());
                        Creature creature = FileUtils.loadCreature(file.getPath());
                        if (creature != null) {
                            creatures.add(creature);
                        }
                    } catch (Exception e) {
                        System.err.println("Error loading creature: " + file.getName());
                        e.printStackTrace();
                    }
                }
            }
        }
        
        return creatures;
    }
    
    /**
     * Loads traders from the exports directory.
     * 
     * @return The list of loaded traders
     */
    private List<Trader> loadTraders() {
        List<Trader> traders = new ArrayList<>();
        File dir = new File("exports/trader");
        
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".txt"));
            
            if (files != null) {
                for (File file : files) {
                    try {
                        System.out.println("Loading trader from " + file.getPath());
                        Trader trader = FileUtils.loadTrader(file.getPath());
                        if (trader != null) {
                            traders.add(trader);
                        }
                    } catch (Exception e) {
                        System.err.println("Error loading trader: " + file.getName());
                        e.printStackTrace();
                    }
                }
            }
        }
        
        return traders;
    }
    
    /**
     * Loads spawners from the exports directory.
     * 
     * @return The list of loaded spawners
     */
    private List<Spawner> loadSpawners() {
        List<Spawner> spawners = new ArrayList<>();
        File dir = new File("exports/spawner");
        
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".txt"));
            
            if (files != null) {
                for (File file : files) {
                    try {
                        Spawner spawner = FileUtils.loadSpawner(file.getPath());
                        if (spawner != null) {
                            spawners.add(spawner);
                        }
                    } catch (Exception e) {
                        System.err.println("Error loading spawner: " + file.getName());
                        e.printStackTrace();
                    }
                }
            }
        }
        
        return spawners;
    }
    
    /**
     * Loads terrains from the exports directory.
     * 
     * @return The list of loaded terrains
     */
    private List<Terrain> loadTerrains() {
        List<Terrain> terrains = new ArrayList<>();
        File dir = new File("exports/terrain");
        
        System.out.println("Loading terrains from: " + dir.getAbsolutePath());
        
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".txt"));
            
            if (files != null) {
                System.out.println("Found " + files.length + " terrain files");
                for (File file : files) {
                    try {
                        System.out.println("Loading terrain from file: " + file.getPath());
                        Terrain terrain = FileUtils.loadTerrain(file.getPath());
                        if (terrain != null) {
                            System.out.println("Successfully loaded terrain: " + terrain.getName());
                            terrains.add(terrain);
                        } else {
                            System.err.println("Failed to load terrain from: " + file.getPath() + " (null result)");
                        }
                    } catch (Exception e) {
                        System.err.println("Error loading terrain: " + file.getName());
                        e.printStackTrace();
                    }
                }
            } else {
                System.err.println("No terrain files found in: " + dir.getAbsolutePath());
            }
        } else {
            System.err.println("Terrain directory does not exist: " + dir.getAbsolutePath());
        }
        
        System.out.println("Loaded " + terrains.size() + " terrains in total");
        return terrains;
    }

    /**
     * Calculate the grid size based on the number of entities.
     * 
     * @return The dimensions of the grid
     */
    public Dimension calculateGridSize() {
        int entityCount = cellEntities.size();
        
        if (entityCount <= 0) {
            return new Dimension(1, 1);  // Just terrain
        } else if (entityCount <= 2) {
            return new Dimension(1, 2);  // 1x2 grid
        } else if (entityCount <= 4) {
            return new Dimension(2, 2);  // 2x2 grid
        } else {
            return new Dimension(3, 2);  // 3x2 grid for 5+ entities
        }
    }

    /**
     * Implementation of abstract methods from BaseEditorPanel
     */
    @Override
    protected void createObject() {
        // Not applicable for CellDemoPanel
    }

    @Override
    protected void editObject(ObjectInstance object) {
        // Not applicable for CellDemoPanel
    }

    @Override
    protected void saveObject() {
        // Not applicable for CellDemoPanel
    }

    @Override
    protected void exportObject() {
        // Not applicable for CellDemoPanel
    }

    @Override
    protected void importObject() {
        // Not applicable for CellDemoPanel
    }

    @Override
    protected void deleteObject() {
        // Not applicable for CellDemoPanel
    }

    @Override
    protected void handleListSelectionChanged(ListSelectionEvent e) {
        // Not applicable for CellDemoPanel
    }
}
