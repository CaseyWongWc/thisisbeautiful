package objectEditor.editor;

import objectEditor.model.Creature;
import objectEditor.model.Item;
import objectEditor.model.Movement;
import objectEditor.util.FileUtils;
import objectEditor.util.ImageUtils;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Panel for editing creatures.
 */
public class CreatureEditorPanel extends BaseEditorPanel<Creature> {
    private static final long serialVersionUID = 1L;

    private JTextField nameField;
    private JTextArea descriptionArea;
    private JSpinner strengthPenaltySpinner;
    private JSpinner waterPenaltySpinner;
    private JSpinner goldPenaltySpinner;
    private JComboBox<String> itemDropComboBox;
    private JComboBox<String> movementComboBox;
    private JPanel difficultiesPanel;
    private Map<Integer, JCheckBox> difficultyCheckboxes;
    private JLabel imageLabel;
    private String currentImagePath;
    private DefaultComboBoxModel<String> itemDropModel;
    private DefaultComboBoxModel<String> movementModel;

    public CreatureEditorPanel() {
        super("creature", "Creature");
        initUI();
        loadObjects();
    }

    @Override
    protected void initUI() {
        super.initUI();

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;

        // Name
        formPanel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Description
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        formPanel.add(descScroll, gbc);

        // Strength Penalty
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Strength Penalty:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        strengthPenaltySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        formPanel.add(strengthPenaltySpinner, gbc);

        // Water Penalty
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Water Penalty:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        waterPenaltySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        formPanel.add(waterPenaltySpinner, gbc);

        // Gold Penalty
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Gold Penalty:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        goldPenaltySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        formPanel.add(goldPenaltySpinner, gbc);

        // Item Drop
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Item Drop:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        
        JPanel itemDropPanel = new JPanel(new BorderLayout(5, 0));
        itemDropModel = new DefaultComboBoxModel<>();
        itemDropComboBox = new JComboBox<>(itemDropModel);
        itemDropPanel.add(itemDropComboBox, BorderLayout.CENTER);
        
        JButton refreshItemsButton = new JButton("Refresh Items");
        refreshItemsButton.addActionListener(e -> refreshItemDropdown());
        itemDropPanel.add(refreshItemsButton, BorderLayout.EAST);
        
        formPanel.add(itemDropPanel, gbc);
        
        // Movement pattern
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Movement:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        
        JPanel movementPanel = new JPanel(new BorderLayout(5, 0));
        movementModel = new DefaultComboBoxModel<>();
        movementComboBox = new JComboBox<>(movementModel);
        movementPanel.add(movementComboBox, BorderLayout.CENTER);
        
        JButton refreshMovementsButton = new JButton("Refresh Movements");
        refreshMovementsButton.addActionListener(e -> refreshMovementDropdown());
        movementPanel.add(refreshMovementsButton, BorderLayout.EAST);
        
        formPanel.add(movementPanel, gbc);

        // Image
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Image:"), gbc);
        gbc.gridx = 1;
        
        JPanel imagePanel = new JPanel(new BorderLayout());
        imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(100, 100));
        imageLabel.setBorder(BorderFactory.createEtchedBorder());
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        JButton browseButton = new JButton("Browse...");
        browseButton.addActionListener(e -> browseImage());
        imagePanel.add(browseButton, BorderLayout.SOUTH);
        
        formPanel.add(imagePanel, gbc);

        // Difficulties
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        formPanel.add(new JLabel("Difficulties:"), gbc);
        gbc.gridx = 1;
        
        difficultiesPanel = new JPanel(new GridLayout(2, 5, 5, 5));
        difficultyCheckboxes = new HashMap<>();
        
        for (int i = 1; i <= 10; i++) {
            JCheckBox cb = new JCheckBox(String.valueOf(i));
            difficultyCheckboxes.put(i, cb);
            difficultiesPanel.add(cb);
        }
        
        formPanel.add(difficultiesPanel, gbc);
        
        add(formPanel, BorderLayout.CENTER);
        
        // Initialize dropdown lists
        refreshItemDropdown();
        refreshMovementDropdown();
    }

    private void refreshItemDropdown() {
        String selected = (String) itemDropComboBox.getSelectedItem();
        
        itemDropModel.removeAllElements();
        itemDropModel.addElement(""); // Empty option
        
        // Get all items from files
        File folder = new File("exports/item");
        if (folder.exists()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));
            if (files != null) {
                for (File file : files) {
                    Item item = FileUtils.loadItem(file.getPath());
                    if (item != null) {
                        itemDropModel.addElement(item.getName());
                    }
                }
            }
        }
        
        // Restore selection if possible
        if (selected != null && !selected.isEmpty()) {
            for (int i = 0; i < itemDropModel.getSize(); i++) {
                if (itemDropModel.getElementAt(i).equals(selected)) {
                    itemDropComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void refreshMovementDropdown() {
        String selected = (String) movementComboBox.getSelectedItem();
        
        movementModel.removeAllElements();
        movementModel.addElement(""); // Empty option
        
        // Get all movements from files
        File folder = new File("exports/movement");
        if (folder.exists()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));
            if (files != null) {
                for (File file : files) {
                    Movement movement = FileUtils.loadMovement(file.getPath());
                    if (movement != null) {
                        movementModel.addElement(movement.getName());
                    }
                }
            }
        }
        
        // Restore selection if possible
        if (selected != null && !selected.isEmpty()) {
            for (int i = 0; i < movementModel.getSize(); i++) {
                if (movementModel.getElementAt(i).equals(selected)) {
                    movementComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    private void browseImage() {
        JFileChooser fileChooser = new JFileChooser("resources");
        fileChooser.setDialogTitle("Select Image");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(java.io.File f) {
                if (f.isDirectory()) return true;
                String name = f.getName().toLowerCase();
                return name.endsWith(".jpg") || name.endsWith(".jpeg") || 
                       name.endsWith(".png") || name.endsWith(".gif");
            }
            
            @Override
            public String getDescription() {
                return "Image Files (*.jpg, *.jpeg, *.png, *.gif)";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            currentImagePath = fileChooser.getSelectedFile().getPath();
            
            // Load and display the image
            ImageIcon icon = ImageUtils.loadScaledImageIcon(currentImagePath, 100, 100);
            imageLabel.setIcon(icon);
            
            System.out.println("Selected image: " + currentImagePath);
        }
    }

    @Override
    protected void loadObjects() {
        objectList.clear();
        objects.clear();
        
        File exportsDir = new File("exports/creature");
        if (!exportsDir.exists()) {
            exportsDir.mkdirs();
        }
        
        File[] files = exportsDir.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files != null) {
            for (File file : files) {
                Creature creature = FileUtils.loadCreature(file.getPath());
                if (creature != null) {
                    objects.put(creature.getName(), creature);
                    objectList.addElement(creature.getName());
                    
                    // Load the referenced item if specified
                    String itemDropName = null;
                    try (FileInputStream in = new FileInputStream(file)) {
                        Properties props = new Properties();
                        props.load(in);
                        itemDropName = props.getProperty("itemDrop");
                    } catch (Exception e) {
                        // Ignore
                    }
                    
                    if (itemDropName != null && !itemDropName.isEmpty()) {
                        Item itemDrop = loadItemByName(itemDropName);
                        creature.setItemDrop(itemDrop);
                    }
                    
                    // Load the referenced movement if specified
                    String movementName = null;
                    try (FileInputStream in = new FileInputStream(file)) {
                        Properties props = new Properties();
                        props.load(in);
                        movementName = props.getProperty("movement");
                    } catch (Exception e) {
                        // Ignore
                    }
                    
                    if (movementName != null && !movementName.isEmpty()) {
                        Movement movement = loadMovementByName(movementName);
                        creature.setMovement(movement);
                    }
                }
            }
        }
        
        refreshItemDropdown();
        refreshMovementDropdown();
    }

    @Override
    protected void editObject(Creature creature) {
        nameField.setText(creature.getName());
        descriptionArea.setText(creature.getDescription());
        strengthPenaltySpinner.setValue(creature.getStrengthPenalty());
        waterPenaltySpinner.setValue(creature.getWaterPenalty());
        goldPenaltySpinner.setValue(creature.getGoldPenalty());
        
        // Set item drop if exists
        if (creature.getItemDrop() != null) {
            String itemName = creature.getItemDrop().getName();
            for (int i = 0; i < itemDropModel.getSize(); i++) {
                if (itemDropModel.getElementAt(i).equals(itemName)) {
                    itemDropComboBox.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            itemDropComboBox.setSelectedIndex(0); // Empty
        }
        
        // Set movement if exists
        if (creature.getMovement() != null) {
            String movementName = creature.getMovement().getName();
            for (int i = 0; i < movementModel.getSize(); i++) {
                if (movementModel.getElementAt(i).equals(movementName)) {
                    movementComboBox.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            movementComboBox.setSelectedIndex(0); // Empty
        }
        
        // Set difficulties
        updateCheckboxesFromDifficulties(creature.getDifficulties());
        
        // Set the image
        currentImagePath = creature.getImagePath();
        if (currentImagePath != null && !currentImagePath.isEmpty()) {
            ImageIcon icon = ImageUtils.loadScaledImageIcon(currentImagePath, 100, 100);
            if (icon != null) {
                imageLabel.setIcon(icon);
            } else {
                imageLabel.setIcon(null);
            }
        } else {
            imageLabel.setIcon(null);
        }
        
        currentFile = "exports/creature/" + creature.getName() + ".txt";
        fileLabel.setText("Current File: " + currentFile);
    }

    @Override
    protected void saveObject() {
        String selectedName = objectListComponent.getSelectedValue();
        if (selectedName != null) {
            Creature creature = objects.get(selectedName);
            if (creature == null) {
                System.err.println("Error: Cannot find creature with name: " + selectedName);
                return;
            }

            String oldName = creature.getName();

            // Update creature properties
            creature.setName(nameField.getText());
            creature.setDescription(descriptionArea.getText());
            creature.setStrengthPenalty((Integer) strengthPenaltySpinner.getValue());
            creature.setWaterPenalty((Integer) waterPenaltySpinner.getValue());
            creature.setGoldPenalty((Integer) goldPenaltySpinner.getValue());
            
            // Handle item drop selection
            String selectedItem = (String) itemDropComboBox.getSelectedItem();
            if (selectedItem != null && !selectedItem.isEmpty()) {
                Item itemDrop = loadItemByName(selectedItem);
                creature.setItemDrop(itemDrop);
                if (itemDrop == null) {
                    System.out.println("Warning: Item '" + selectedItem + "' not found for creature's item drop");
                }
            } else {
                creature.setItemDrop(null);
            }
            
            // Handle movement selection
            String selectedMovement = (String) movementComboBox.getSelectedItem();
            if (selectedMovement != null && !selectedMovement.isEmpty()) {
                Movement movement = loadMovementByName(selectedMovement);
                creature.setMovement(movement);
                if (movement == null) {
                    System.out.println("Warning: Movement '" + selectedMovement + "' not found for creature");
                }
            } else {
                creature.setMovement(null);
            }
            
            // Handle difficulties from checkboxes
            StringBuilder difficultiesSb = new StringBuilder();
            for (Map.Entry<Integer, JCheckBox> entry : difficultyCheckboxes.entrySet()) {
                if (entry.getValue().isSelected()) {
                    if (difficultiesSb.length() > 0) {
                        difficultiesSb.append(",");
                    }
                    difficultiesSb.append(entry.getKey());
                }
            }
            creature.setDifficulties(difficultiesSb.toString());
            
            // Set image path
            creature.setImagePath(currentImagePath);
            
            // Handle name change
            if (!oldName.equals(creature.getName())) {
                // Update list model - need to remove and re-add with new name
                int selectedIndex = objectListComponent.getSelectedIndex();
                objectList.remove(selectedIndex);
                objectList.add(selectedIndex, creature.getName());
                
                // Update objects map
                objects.remove(oldName);
                objects.put(creature.getName(), creature);
                
                // Remove old file if exists
                File oldFile = new File("exports/creature/" + oldName + ".txt");
                if (oldFile.exists()) {
                    oldFile.delete();
                }
                
                // Select the renamed item
                objectListComponent.setSelectedIndex(selectedIndex);
            }
            
            // Save to file
            String filePath = "exports/creature/" + creature.getName() + ".txt";
            FileUtils.saveCreature(creature, filePath);
            
            // Update current file
            currentFile = filePath;
            fileLabel.setText("Current File: " + currentFile);
        }
    }

    @Override
    protected void createObject() {
        // Create a new creature with default properties
        Creature creature = new Creature();
        creature.setName("New Creature");
        creature.setDescription("A new creature");
        
        // Add to the list and map
        objects.put(creature.getName(), creature);
        objectList.addElement(creature.getName());
        
        // Select the new creature in the list
        objectListComponent.setSelectedValue(creature.getName(), true);
        
        // Automatically create a file for the new creature
        String fileName = "exports/creature/" + creature.getName() + ".txt";
        FileUtils.saveCreature(creature, fileName);
        
        // Set current file
        currentFile = fileName;
        fileLabel.setText("Current File: " + fileName);
        System.out.println("New creature automatically saved to file: " + fileName);
        
        // Edit the new creature
        editObject(creature);
    }

    @Override
    protected void deleteObject() {
        String selectedName = objectListComponent.getSelectedValue();
        if (selectedName != null) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the creature '" + selectedName + "'?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                // Remove from map
                objects.remove(selectedName);
                
                // Remove from list model
                objectList.removeElement(selectedName);
                
                // Delete the file
                File file = new File("exports/creature/" + selectedName + ".txt");
                if (file.exists()) {
                    if (file.delete()) {
                        System.out.println("Deleted creature file: " + file.getPath());
                    } else {
                        System.out.println("Failed to delete creature file: " + file.getPath());
                    }
                }
                
                // Select another creature if available
                if (objectList.size() > 0) {
                    objectListComponent.setSelectedIndex(0);
                } else {
                    // Clear form fields
                    nameField.setText("");
                    descriptionArea.setText("");
                    strengthPenaltySpinner.setValue(0);
                    waterPenaltySpinner.setValue(0);
                    goldPenaltySpinner.setValue(0);
                    itemDropComboBox.setSelectedIndex(0);
                    movementComboBox.setSelectedIndex(0);
                    
                    // Clear difficulties
                    for (JCheckBox cb : difficultyCheckboxes.values()) {
                        cb.setSelected(false);
                    }
                    
                    // Clear image
                    imageLabel.setIcon(null);
                    currentImagePath = null;
                    
                    // Clear current file
                    currentFile = null;
                    fileLabel.setText("Current File: [None]");
                }
            }
        }
    }

    private void updateCheckboxesFromDifficulties(String difficulties) {
        // Clear all checkboxes first
        for (JCheckBox cb : difficultyCheckboxes.values()) {
            cb.setSelected(false);
        }
        
        // Set selected difficulties
        if (difficulties != null && !difficulties.isEmpty()) {
            String[] parts = difficulties.split(",");
            for (String part : parts) {
                try {
                    int diff = Integer.parseInt(part.trim());
                    JCheckBox cb = difficultyCheckboxes.get(diff);
                    if (cb != null) {
                        cb.setSelected(true);
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid values
                }
            }
        }
    }

    private Item loadItemByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        File folder = new File("exports/item");
        if (!folder.exists()) {
            return null;
        }

        File[] files = folder.listFiles((dir, filename) -> filename.endsWith(".txt"));
        if (files != null) {
            for (File file : files) {
                Item item = FileUtils.loadItem(file.getPath());
                if (item != null && item.getName().equals(name)) {
                    return item;
                }
            }
        }
        
        return null;
    }
    
    /**
     * Loads a Movement by name from the exports/movement directory.
     *
     * @param name the name of the movement to load
     * @return the Movement object, or null if not found
     */
    private Movement loadMovementByName(String name) {
        if (name == null || name.isEmpty()) {
            return null;
        }

        File folder = new File("exports/movement");
        if (!folder.exists()) {
            return null;
        }

        File[] files = folder.listFiles((dir, filename) -> filename.endsWith(".txt"));
        if (files != null) {
            for (File file : files) {
                Movement movement = FileUtils.loadMovement(file.getPath());
                if (movement != null && movement.getName().equals(name)) {
                    return movement;
                }
            }
        }
        
        return null;
    }
    
    @Override
    protected void handleListSelectionChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            String selectedName = objectListComponent.getSelectedValue();
            if (selectedName != null) {
                Creature creature = objects.get(selectedName);
                if (creature != null) {
                    editObject(creature);
                }
            }
        }
    }
    
    @Override
    protected void importObject() {
        JFileChooser fileChooser = new JFileChooser("exports/creature");
        fileChooser.setDialogTitle("Import Creature");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(java.io.File f) {
                return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt");
            }
            
            @Override
            public String getDescription() {
                return "Text Files (*.txt)";
            }
        });
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            
            // Load creature from file
            Creature creature = FileUtils.loadCreature(file.getPath());
            if (creature != null) {
                // Check if a creature with this name already exists
                if (objects.containsKey(creature.getName())) {
                    int overwrite = JOptionPane.showConfirmDialog(
                        this,
                        "A creature with the name '" + creature.getName() + "' already exists. Overwrite?",
                        "Confirm Overwrite",
                        JOptionPane.YES_NO_OPTION);
                        
                    if (overwrite != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                
                // Add or replace in the map
                objects.put(creature.getName(), creature);
                
                // Update list
                if (!objectList.contains(creature.getName())) {
                    objectList.addElement(creature.getName());
                }
                
                // Select the imported creature
                objectListComponent.setSelectedValue(creature.getName(), true);
                
                // Save to local directory
                String filePath = "exports/creature/" + creature.getName() + ".txt";
                FileUtils.saveCreature(creature, filePath);
                
                // Load the item and movement references if applicable
                String itemDropName = null;
                String movementName = null;
                try (FileInputStream in = new FileInputStream(file)) {
                    Properties props = new Properties();
                    props.load(in);
                    itemDropName = props.getProperty("itemDrop");
                    movementName = props.getProperty("movement");
                } catch (Exception e) {
                    // Ignore
                }
                
                if (itemDropName != null && !itemDropName.isEmpty()) {
                    Item itemDrop = loadItemByName(itemDropName);
                    creature.setItemDrop(itemDrop);
                }
                
                if (movementName != null && !movementName.isEmpty()) {
                    Movement movement = loadMovementByName(movementName);
                    creature.setMovement(movement);
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Creature '" + creature.getName() + "' imported successfully.",
                    "Import Successful", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to import creature from file: " + file.getPath(),
                    "Import Failed", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    @Override
    protected void exportObject() {
        String selectedName = objectListComponent.getSelectedValue();
        if (selectedName != null) {
            Creature creature = objects.get(selectedName);
            if (creature != null) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Export Creature");
                fileChooser.setSelectedFile(new File(creature.getName() + ".txt"));
                fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
                    @Override
                    public boolean accept(java.io.File f) {
                        return f.isDirectory() || f.getName().toLowerCase().endsWith(".txt");
                    }
                    
                    @Override
                    public String getDescription() {
                        return "Text Files (*.txt)";
                    }
                });
                
                int result = fileChooser.showSaveDialog(this);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    
                    // Add .txt extension if not present
                    if (!file.getName().toLowerCase().endsWith(".txt")) {
                        file = new File(file.getPath() + ".txt");
                    }
                    
                    // Check if file already exists
                    if (file.exists()) {
                        int overwrite = JOptionPane.showConfirmDialog(
                            this,
                            "File already exists. Overwrite?",
                            "Confirm Overwrite",
                            JOptionPane.YES_NO_OPTION);
                            
                        if (overwrite != JOptionPane.YES_OPTION) {
                            return;
                        }
                    }
                    
                    // Export to file
                    if (FileUtils.saveCreature(creature, file.getPath())) {
                        JOptionPane.showMessageDialog(this, 
                            "Creature '" + creature.getName() + "' exported successfully to:\n" + file.getPath(),
                            "Export Successful", 
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Failed to export creature to file: " + file.getPath(),
                            "Export Failed", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Please select a creature to export.",
                "No Creature Selected",
                JOptionPane.WARNING_MESSAGE);
        }
    }
}
