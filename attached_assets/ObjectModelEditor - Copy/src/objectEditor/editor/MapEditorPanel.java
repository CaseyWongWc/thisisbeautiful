package objectEditor.editor;

import objectEditor.model.*;
import objectEditor.util.FileUtils;
import objectEditor.util.ImageUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * A panel for creating and editing GameMap objects.
 * Allows resizing the map and editing individual cells.
 */
public class MapEditorPanel extends BaseEditorPanel<GameMap> {
    private GameMap currentMap;
    private JPanel mapViewPanel;
    private JScrollPane mapScrollPane;
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JSpinner widthSpinner;
    private JSpinner heightSpinner;
    private JButton resizeButton;
    
    private JComboBox<String> terrainComboBox;
    private JComboBox<String> entityTypeComboBox;
    private JComboBox<String> entityNameComboBox;
    private JLabel terrainThumbnailLabel;
    private JPanel cellInfoPanel;
    private JLabel cellInfoLabel;
    
    // The cell that's currently being edited
    private Point selectedCell;
    
    // Maps to store loaded objects
    private List<Item> loadedItems;
    private List<Creature> loadedCreatures;
    private List<Spawner> loadedSpawners;
    private List<Trader> loadedTraders;
    private List<Terrain> loadedTerrains;
    
    // Current editing tool
    private enum Tool { SELECT, TERRAIN, ENTITY, ERASE }
    private Tool currentTool = Tool.SELECT;
    
    // Current entity for the entity tool
    private ObjectInstance currentEntity;
    
    // Current terrain for the terrain tool
    private Terrain currentTerrain;
    
    // Zoom level
    private float zoomLevel = 1.0f;
    
    /**
     * Creates a new map editor panel.
     */
    public MapEditorPanel() {
        super();
        
        initComponents();
        setupListeners();
        loadObjects();
        
        // Create a default map
        createNewMap("New Map", 10, 10);
    }
    
    /**
     * Creates a new map with the given parameters.
     * 
     * @param name The name of the map
     * @param width The width of the map in cells
     * @param height The height of the map in cells
     */
    private void createNewMap(String name, int width, int height) {
        currentMap = new GameMap(name, width, height);
        
        // Initialize with the first terrain if available
        if (!loadedTerrains.isEmpty()) {
            currentMap.fillWithTerrain(loadedTerrains.get(0));
        }
        
        // Update UI components
        updateMapView();
        nameField.setText(name);
        widthSpinner.setValue(width);
        heightSpinner.setValue(height);
    }
    
    /**
     * Initializes the UI components.
     */
    private void initComponents() {
        // Set up main layout
        setLayout(new BorderLayout(10, 10));
        
        // Top panel - Map properties
        JPanel propertiesPanel = new JPanel(new BorderLayout(5, 5));
        propertiesPanel.setBorder(BorderFactory.createTitledBorder("Map Properties"));
        
        // Create property fields
        JPanel fieldPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(2, 2, 2, 2);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        fieldPanel.add(new JLabel("Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        fieldPanel.add(nameField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0;
        fieldPanel.add(new JLabel("Width:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        widthSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        JPanel widthPanel = new JPanel(new BorderLayout());
        widthPanel.add(widthSpinner, BorderLayout.WEST);
        widthPanel.add(new JLabel(" cells"), BorderLayout.CENTER);
        fieldPanel.add(widthPanel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0;
        fieldPanel.add(new JLabel("Height:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        heightSpinner = new JSpinner(new SpinnerNumberModel(10, 1, 100, 1));
        JPanel heightPanel = new JPanel(new BorderLayout());
        heightPanel.add(heightSpinner, BorderLayout.WEST);
        heightPanel.add(new JLabel(" cells"), BorderLayout.CENTER);
        fieldPanel.add(heightPanel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        resizeButton = new JButton("Resize Map");
        fieldPanel.add(resizeButton, gbc);
        
        propertiesPanel.add(fieldPanel, BorderLayout.WEST);
        
        // Description
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.add(new JLabel("Description"), BorderLayout.NORTH);
        descriptionArea = new JTextArea(5, 30);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        JScrollPane descriptionScrollPane = new JScrollPane(descriptionArea);
        descriptionPanel.add(descriptionScrollPane, BorderLayout.CENTER);
        
        propertiesPanel.add(descriptionPanel, BorderLayout.CENTER);
        
        add(propertiesPanel, BorderLayout.NORTH);
        
        // Left panel - Tools
        JPanel toolsPanel = new JPanel(new BorderLayout(5, 5));
        toolsPanel.setBorder(BorderFactory.createTitledBorder("Tools"));
        
        // Tool buttons
        JPanel toolButtonsPanel = new JPanel(new GridLayout(4, 1, 0, 5));
        
        JButton selectButton = new JButton("Select");
        selectButton.addActionListener(e -> currentTool = Tool.SELECT);
        toolButtonsPanel.add(selectButton);
        
        JButton terrainButton = new JButton("Terrain");
        terrainButton.addActionListener(e -> currentTool = Tool.TERRAIN);
        toolButtonsPanel.add(terrainButton);
        
        JButton entityButton = new JButton("Entity");
        entityButton.addActionListener(e -> currentTool = Tool.ENTITY);
        toolButtonsPanel.add(entityButton);
        
        JButton eraseButton = new JButton("Erase");
        eraseButton.addActionListener(e -> currentTool = Tool.ERASE);
        toolButtonsPanel.add(eraseButton);
        
        JPanel toolButtonsWrapper = new JPanel(new BorderLayout());
        toolButtonsWrapper.setBorder(BorderFactory.createTitledBorder("Tool Selection"));
        toolButtonsWrapper.add(toolButtonsPanel, BorderLayout.CENTER);
        
        toolsPanel.add(toolButtonsWrapper, BorderLayout.NORTH);
        
        // Terrain selection panel
        JPanel terrainPanel = new JPanel(new BorderLayout(5, 5));
        terrainPanel.setBorder(BorderFactory.createTitledBorder("Terrain"));
        
        // Add a more descriptive label
        JLabel terrainLabel = new JLabel("Select Terrain Type:");
        terrainLabel.setToolTipText("Choose the terrain type to place on the map");
        terrainPanel.add(terrainLabel, BorderLayout.NORTH);
        
        // Create the terrain combo box
        terrainComboBox = new JComboBox<>();
        terrainComboBox.setToolTipText("Available terrain types");
        terrainPanel.add(terrainComboBox, BorderLayout.CENTER);
        
        // Terrain thumbnail
        terrainThumbnailLabel = new JLabel();
        terrainThumbnailLabel.setHorizontalAlignment(SwingConstants.CENTER);
        terrainThumbnailLabel.setPreferredSize(new Dimension(100, 100));
        terrainThumbnailLabel.setBorder(BorderFactory.createEtchedBorder());
        terrainThumbnailLabel.setText("No terrain selected");
        
        // Improved thumbnail panel
        JPanel thumbnailPanel = new JPanel(new BorderLayout());
        thumbnailPanel.setBorder(BorderFactory.createTitledBorder("Terrain Preview"));
        thumbnailPanel.add(terrainThumbnailLabel, BorderLayout.CENTER);
        
        // Add buttons for managing terrain
        JPanel terrainButtonPanel = new JPanel(new GridLayout(1, 2, 5, 0));
        JButton setTerrainButton = new JButton("Set Terrain");
        setTerrainButton.setToolTipText("Apply selected terrain to the current cell");
        setTerrainButton.addActionListener(e -> {
            if (selectedCell != null && currentTerrain != null && currentMap != null) {
                currentMap.setTerrain(selectedCell.x, selectedCell.y, currentTerrain);
                updateMapView();
                updateCellInfoPanel();
            }
        });
        terrainButtonPanel.add(setTerrainButton);
        
        thumbnailPanel.add(terrainButtonPanel, BorderLayout.SOUTH);
        terrainPanel.add(thumbnailPanel, BorderLayout.SOUTH);
        
        // Entity selection panel
        JPanel entityPanel = new JPanel(new BorderLayout(5, 5));
        entityPanel.setBorder(BorderFactory.createTitledBorder("Entity"));
        
        JPanel entitySelectionPanel = new JPanel(new GridBagLayout());
        GridBagConstraints entityGbc = new GridBagConstraints();
        entityGbc.insets = new Insets(2, 2, 2, 2);
        entityGbc.fill = GridBagConstraints.HORIZONTAL;
        
        entityGbc.gridx = 0;
        entityGbc.gridy = 0;
        entitySelectionPanel.add(new JLabel("Entity Type:"), entityGbc);
        
        entityGbc.gridx = 0;
        entityGbc.gridy = 1;
        String[] entityTypes = {"Item", "Creature", "Trader", "Spawner"};
        entityTypeComboBox = new JComboBox<>(entityTypes);
        entityTypeComboBox.addActionListener(e -> updateEntityNameComboBox());
        entitySelectionPanel.add(entityTypeComboBox, entityGbc);
        
        entityGbc.gridx = 0;
        entityGbc.gridy = 2;
        entitySelectionPanel.add(new JLabel("Entity Name:"), entityGbc);
        
        entityGbc.gridx = 0;
        entityGbc.gridy = 3;
        entityNameComboBox = new JComboBox<>();
        entitySelectionPanel.add(entityNameComboBox, entityGbc);
        
        entityPanel.add(entitySelectionPanel, BorderLayout.CENTER);
        
        // Cell information panel
        cellInfoPanel = new JPanel(new BorderLayout());
        cellInfoPanel.setBorder(BorderFactory.createTitledBorder("Cell Information"));
        cellInfoLabel = new JLabel("No cell selected");
        cellInfoLabel.setHorizontalAlignment(SwingConstants.LEFT);
        cellInfoLabel.setVerticalAlignment(SwingConstants.TOP);
        cellInfoPanel.add(cellInfoLabel, BorderLayout.CENTER);
        
        // Add panels to tools panel
        JPanel leftCenterPanel = new JPanel(new GridLayout(3, 1, 0, 10));
        leftCenterPanel.add(terrainPanel);
        leftCenterPanel.add(entityPanel);
        leftCenterPanel.add(cellInfoPanel);
        
        toolsPanel.add(leftCenterPanel, BorderLayout.CENTER);
        
        // Add tools panel to main layout
        add(toolsPanel, BorderLayout.WEST);
        
        // Center panel - Map view
        JPanel mapPanel = new JPanel(new BorderLayout());
        mapPanel.setBorder(BorderFactory.createTitledBorder("Map View"));
        
        mapViewPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                paintMap(g);
            }
        };
        mapViewPanel.setBackground(Color.WHITE);
        
        mapScrollPane = new JScrollPane(mapViewPanel);
        mapPanel.add(mapScrollPane, BorderLayout.CENTER);
        
        // Zoom control
        JPanel zoomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        zoomPanel.add(new JLabel("Zoom:"));
        
        JSpinner zoomSpinner = new JSpinner(new SpinnerNumberModel(100, 10, 500, 10));
        zoomSpinner.setPreferredSize(new Dimension(80, zoomSpinner.getPreferredSize().height));
        zoomSpinner.addChangeListener(e -> {
            zoomLevel = ((Number) zoomSpinner.getValue()).intValue() / 100f;
            updateMapView();
        });
        zoomPanel.add(zoomSpinner);
        zoomPanel.add(new JLabel("%"));
        
        mapPanel.add(zoomPanel, BorderLayout.SOUTH);
        
        // Add map panel to main layout
        add(mapPanel, BorderLayout.CENTER);
    }
    
    /**
     * Sets up the event listeners for the UI components.
     */
    private void setupListeners() {
        // Resize button
        resizeButton.addActionListener(e -> {
            int newWidth = (Integer) widthSpinner.getValue();
            int newHeight = (Integer) heightSpinner.getValue();
            
            if (currentMap != null) {
                currentMap.resize(newWidth, newHeight);
                updateMapView();
            }
        });
        
        // Map view mouse listeners
        mapViewPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMapClick(e.getX(), e.getY());
            }
        });
        
        // Name field listener
        nameField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (currentMap != null) {
                    currentMap.setName(nameField.getText());
                }
            }
        });
        
        // Description area listener
        descriptionArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                if (currentMap != null) {
                    currentMap.setDescription(descriptionArea.getText());
                }
            }
        });
        
        // Terrain combo box listener
        terrainComboBox.addActionListener(e -> {
            int selectedIndex = terrainComboBox.getSelectedIndex();
            if (selectedIndex >= 0 && selectedIndex < loadedTerrains.size()) {
                currentTerrain = loadedTerrains.get(selectedIndex);
                updateTerrainThumbnail(currentTerrain);
            }
        });
        
        // Entity combo box listener
        entityNameComboBox.addActionListener(e -> {
            updateCurrentEntity();
        });
    }
    
    /**
     * Updates the current entity based on the selected entity type and name.
     */
    private void updateCurrentEntity() {
        String selectedType = (String) entityTypeComboBox.getSelectedItem();
        String selectedName = (String) entityNameComboBox.getSelectedItem();
        
        if (selectedType == null || selectedName == null) {
            currentEntity = null;
            return;
        }
        
        // Find the selected entity
        if ("Item".equals(selectedType)) {
            for (Item item : loadedItems) {
                if (item.getName().equals(selectedName)) {
                    currentEntity = item;
                    break;
                }
            }
        } else if ("Creature".equals(selectedType)) {
            for (Creature creature : loadedCreatures) {
                if (creature.getName().equals(selectedName)) {
                    currentEntity = creature;
                    break;
                }
            }
        } else if ("Trader".equals(selectedType)) {
            for (Trader trader : loadedTraders) {
                if (trader.getName().equals(selectedName)) {
                    currentEntity = trader;
                    break;
                }
            }
        } else if ("Spawner".equals(selectedType)) {
            for (Spawner spawner : loadedSpawners) {
                if (spawner.getName().equals(selectedName)) {
                    currentEntity = spawner;
                    break;
                }
            }
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
        
        updateCurrentEntity();
    }
    
    /**
     * Handles a click on the map view.
     * 
     * @param x The x coordinate of the click
     * @param y The y coordinate of the click
     */
    private void handleMapClick(int x, int y) {
        if (currentMap == null) return;
        
        // Convert screen coordinates to cell coordinates
        int cellSize = (int)(currentMap.getCellSize() * zoomLevel);
        int cellX = x / cellSize;
        int cellY = y / cellSize;
        
        // Check if the coordinates are valid
        if (cellX >= 0 && cellX < currentMap.getWidth() && cellY >= 0 && cellY < currentMap.getHeight()) {
            // Update selected cell
            selectedCell = new Point(cellX, cellY);
            
            // Handle the action based on the current tool
            switch (currentTool) {
                case SELECT:
                    // Just select the cell
                    break;
                case TERRAIN:
                    // Apply the current terrain to the cell
                    if (currentTerrain != null) {
                        currentMap.setTerrain(cellX, cellY, currentTerrain);
                    }
                    break;
                case ENTITY:
                    // Add the current entity to the cell
                    if (currentEntity != null) {
                        currentMap.addEntity(cellX, cellY, currentEntity);
                    }
                    break;
                case ERASE:
                    // Clear the cell
                    MapCell cell = currentMap.getCell(cellX, cellY);
                    if (cell != null) {
                        cell.clearEntities();
                    }
                    break;
            }
            
            // Update the map view
            updateMapView();
            
            // Update the cell information panel
            updateCellInfoPanel();
        }
    }
    
    /**
     * Updates the cell information panel with details about the selected cell.
     */
    private void updateCellInfoPanel() {
        if (selectedCell != null && currentMap != null) {
            MapCell cell = currentMap.getCell(selectedCell.x, selectedCell.y);
            if (cell != null) {
                StringBuilder info = new StringBuilder();
                info.append("<html>");
                info.append("Position: (").append(selectedCell.x).append(", ").append(selectedCell.y).append(")<br>");
                
                if (cell.getTerrain() != null) {
                    info.append("Terrain: ").append(cell.getTerrain().getName()).append("<br>");
                } else {
                    info.append("Terrain: None<br>");
                }
                
                info.append("Entities: ").append(cell.getEntityCount()).append("<br>");
                
                if (cell.getEntityCount() > 0) {
                    info.append("<ul>");
                    for (ObjectInstance entity : cell.getEntities()) {
                        info.append("<li>").append(entity.getObjectType()).append(": ").append(entity.getName()).append("</li>");
                    }
                    info.append("</ul>");
                }
                
                info.append("</html>");
                
                cellInfoLabel.setText(info.toString());
            } else {
                cellInfoLabel.setText("Invalid cell");
            }
        } else {
            cellInfoLabel.setText("No cell selected");
        }
    }
    
    /**
     * Updates the map view panel.
     */
    private void updateMapView() {
        if (currentMap != null) {
            int cellSize = (int)(currentMap.getCellSize() * zoomLevel);
            int width = currentMap.getWidth() * cellSize;
            int height = currentMap.getHeight() * cellSize;
            
            mapViewPanel.setPreferredSize(new Dimension(width, height));
            mapViewPanel.revalidate();
            mapViewPanel.repaint();
        }
    }
    
    /**
     * Paints the map on the graphics context.
     * 
     * @param g The graphics context
     */
    private void paintMap(Graphics g) {
        if (currentMap == null) return;
        
        Graphics2D g2d = (Graphics2D) g;
        
        // Get the cell size
        int cellSize = (int)(currentMap.getCellSize() * zoomLevel);
        
        // Draw each cell
        for (int y = 0; y < currentMap.getHeight(); y++) {
            for (int x = 0; x < currentMap.getWidth(); x++) {
                MapCell cell = currentMap.getCell(x, y);
                if (cell != null) {
                    // Draw the cell background
                    Terrain terrain = cell.getTerrain();
                    if (terrain != null) {
                        // Try to draw the terrain image
                        boolean drewImage = false;
                        if (terrain.getImagePath() != null && !terrain.getImagePath().isEmpty()) {
                            ImageIcon icon = ImageUtils.loadImageIcon(terrain.getImagePath());
                            if (icon != null) {
                                Image img = icon.getImage();
                                g2d.drawImage(img, x * cellSize, y * cellSize, cellSize, cellSize, null);
                                drewImage = true;
                            }
                        }
                        
                        // If no image, draw a label with the terrain name
                        if (!drewImage) {
                            g2d.setColor(Color.LIGHT_GRAY);
                            g2d.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                            g2d.setColor(Color.BLACK);
                            g2d.drawRect(x * cellSize, y * cellSize, cellSize - 1, cellSize - 1);
                            g2d.drawString("Terrain: " + terrain.getName(), x * cellSize + 5, y * cellSize + 15);
                        }
                    } else {
                        // Draw an empty cell
                        g2d.setColor(Color.WHITE);
                        g2d.fillRect(x * cellSize, y * cellSize, cellSize, cellSize);
                        g2d.setColor(Color.BLACK);
                        g2d.drawRect(x * cellSize, y * cellSize, cellSize - 1, cellSize - 1);
                    }
                    
                    // Draw entities if any
                    if (cell.getEntityCount() > 0) {
                        g2d.setColor(Color.RED);
                        g2d.drawString("Entities: " + cell.getEntityCount(), x * cellSize + 5, y * cellSize + 30);
                    }
                }
            }
        }
        
        // Highlight the selected cell
        if (selectedCell != null) {
            g2d.setColor(Color.YELLOW);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawRect(selectedCell.x * cellSize, selectedCell.y * cellSize, cellSize - 1, cellSize - 1);
        }
    }
    
    /**
     * Loads all objects from the exports directory.
     */
    private void loadObjects() {
        loadedItems = loadItems();
        loadedCreatures = loadCreatures();
        loadedSpawners = loadSpawners();
        loadedTraders = loadTraders();
        loadedTerrains = loadTerrains();
        
        // Initialize UI components with loaded objects
        updateEntityNameComboBox();
        
        terrainComboBox.removeAllItems();
        for (Terrain terrain : loadedTerrains) {
            terrainComboBox.addItem(terrain.getName());
        }
        
        // Initialize with the first terrain if available
        if (!loadedTerrains.isEmpty()) {
            terrainComboBox.setSelectedIndex(0);
            currentTerrain = loadedTerrains.get(0);
            updateTerrainThumbnail(currentTerrain);
        }
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
     * Loads terrains from the exports directory.
     * 
     * @return The list of loaded terrains
     */
    private List<Terrain> loadTerrains() {
        List<Terrain> terrains = new ArrayList<>();
        File dir = new File("exports/terrain");
        
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".txt"));
            
            if (files != null) {
                for (File file : files) {
                    try {
                        Terrain terrain = FileUtils.loadTerrain(file.getPath());
                        if (terrain != null) {
                            terrains.add(terrain);
                        }
                    } catch (Exception e) {
                        System.err.println("Error loading terrain: " + file.getName());
                        e.printStackTrace();
                    }
                }
            }
        }
        
        return terrains;
    }
    
    /**
     * Updates the terrain thumbnail with the image of the given terrain.
     */
    private void updateTerrainThumbnail(Terrain terrain) {
        if (terrain != null) {
            // Set a title with the terrain name
            terrainThumbnailLabel.setText(terrain.getName());
            terrainThumbnailLabel.setVerticalTextPosition(JLabel.BOTTOM);
            terrainThumbnailLabel.setHorizontalTextPosition(JLabel.CENTER);
            
            // Try to load the image if there is one
            if (terrain.getImagePath() != null && !terrain.getImagePath().isEmpty()) {
                System.out.println("Loading terrain thumbnail from: " + terrain.getImagePath());
                ImageIcon icon = ImageUtils.loadImageIcon(terrain.getImagePath());
                if (icon != null) {
                    // Resize the icon to fit the thumbnail
                    Image img = icon.getImage();
                    Image resizedImg = img.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
                    terrainThumbnailLabel.setIcon(new ImageIcon(resizedImg));
                } else {
                    // Create a default colored icon with the terrain name
                    BufferedImage defaultImg = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2d = defaultImg.createGraphics();
                    g2d.setColor(new Color(200, 230, 200)); // Light green background
                    g2d.fillRect(0, 0, 80, 80);
                    g2d.setColor(Color.BLACK);
                    g2d.drawRect(0, 0, 79, 79);
                    g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
                    
                    // Draw the terrain name centered in the image
                    FontMetrics metrics = g2d.getFontMetrics();
                    String name = terrain.getName();
                    int x = (80 - metrics.stringWidth(name)) / 2;
                    int y = ((80 - metrics.getHeight()) / 2) + metrics.getAscent();
                    g2d.drawString(name, x, y);
                    g2d.dispose();
                    
                    terrainThumbnailLabel.setIcon(new ImageIcon(defaultImg));
                    System.out.println("Created default icon for terrain: " + terrain.getName());
                }
            } else {
                // Create a color block with the terrain name
                BufferedImage img = new BufferedImage(80, 80, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = img.createGraphics();
                g2d.setColor(new Color(200, 200, 230)); // Light blue background
                g2d.fillRect(0, 0, 80, 80);
                g2d.setColor(Color.BLACK);
                g2d.drawRect(0, 0, 79, 79);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 12));
                FontMetrics metrics = g2d.getFontMetrics();
                String name = terrain.getName();
                int x = (80 - metrics.stringWidth(name)) / 2;
                int y = ((80 - metrics.getHeight()) / 2) + metrics.getAscent();
                g2d.drawString(name, x, y);
                g2d.dispose();
                
                terrainThumbnailLabel.setIcon(new ImageIcon(img));
                System.out.println("Created color block for terrain: " + terrain.getName());
            }
        } else {
            terrainThumbnailLabel.setIcon(null);
            terrainThumbnailLabel.setText("No terrain selected");
        }
    }
    
    @Override
    public void saveObject() {
        if (currentMap != null) {
            FileUtils.saveMap(currentMap, "exports/map/" + currentMap.getName() + ".txt");
            JOptionPane.showMessageDialog(this, "Map saved successfully!", "Save", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    @Override
    public void loadObject(GameMap object) {
        if (object != null) {
            currentMap = object;
            nameField.setText(currentMap.getName());
            descriptionArea.setText(currentMap.getDescription());
            widthSpinner.setValue(currentMap.getWidth());
            heightSpinner.setValue(currentMap.getHeight());
            updateMapView();
        }
    }
    
    @Override
    public void clearSelection() {
        selectedCell = null;
        updateMapView();
        cellInfoLabel.setText("No cell selected");
    }
    
    @Override
    public void createNewObject() {
        // Default to a 10x10 map
        createNewMap("New Map", 10, 10);
    }
}
