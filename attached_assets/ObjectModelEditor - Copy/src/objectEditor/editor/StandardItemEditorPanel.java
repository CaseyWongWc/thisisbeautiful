package objectEditor.editor;

import objectEditor.model.Item;
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
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.util.HashMap;
import java.util.Map;

/**
 * Editor panel for items.
 */
public class StandardItemEditorPanel extends BaseEditorPanel<Item> {
    private static final long serialVersionUID = 1L;

    private JTextField nameField;
    private JTextArea descriptionArea;
    private JTextField imagePathField;
    private JSpinner foodValueSpinner;
    private JSpinner waterValueSpinner;
    private JSpinner goldValueSpinner;
    private JTextField difficultiesField;
    private Map<Integer, JCheckBox> difficultyCheckboxes;
    private JLabel imageLabel;
    private String currentImagePath;

    public StandardItemEditorPanel() {
        super("item", "Item");
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

        // Advanced properties tab
        JPanel advancedPanel = new JPanel(new GridBagLayout());
        gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Food Value
        gbc.gridx = 0;
        gbc.gridy = 0;
        advancedPanel.add(new JLabel("Food Value:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        foodValueSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        advancedPanel.add(foodValueSpinner, gbc);

        // Water Value
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.0;
        advancedPanel.add(new JLabel("Water Value:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        waterValueSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        advancedPanel.add(waterValueSpinner, gbc);

        // Gold Value
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        advancedPanel.add(new JLabel("Gold Value:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        goldValueSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        advancedPanel.add(goldValueSpinner, gbc);

        // Difficulties section
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 5, 5, 5);
        advancedPanel.add(new JLabel("Difficulties:"), gbc);

        // Reset insets
        gbc.insets = new Insets(5, 5, 5, 5);

        // Difficulty checkboxes
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        JPanel difficultiesPanel = new JPanel(new GridLayout(0, 5));
        difficultyCheckboxes = new HashMap<>();

        // Create difficulty checkboxes 1-10
        for (int i = 1; i <= 10; i++) {
            JCheckBox checkbox = new JCheckBox(String.valueOf(i));
            difficultyCheckboxes.put(i, checkbox);
            difficultiesPanel.add(checkbox);
            checkbox.addActionListener(e -> updateDifficultiesField());
        }
        advancedPanel.add(difficultiesPanel, gbc);


        // Hidden difficulties field
        difficultiesField = new JTextField();
        difficultiesField.setVisible(false);
        advancedPanel.add(difficultiesField, gbc);

        // Add advanced panel to tabbed pane
        tabbedPane.addTab("Advanced", advancedPanel);

        // Add tabbed pane to editor panel
        add(tabbedPane, BorderLayout.CENTER);
    }

    /**
     * Updates the difficulties field based on checkbox states
     */
    private void updateDifficultiesField() {
        if (difficultyCheckboxes == null) {
            return;
        }
        
        StringBuilder difficulties = new StringBuilder();
        for (int i = 1; i <= 10; i++) {
            JCheckBox checkbox = difficultyCheckboxes.get(i);
            if (checkbox != null && checkbox.isSelected()) {
                difficulties.append(i).append(",");
            }
        }

        // Remove trailing comma if present
        String diffString = difficulties.toString();
        if (diffString.endsWith(",")) {
            diffString = diffString.substring(0, diffString.length() - 1);
        }

        if (difficultiesField != null) {
            difficultiesField.setText(diffString);
        }
    }

    /**
     * Updates checkboxes based on difficulties field
     */
    private void updateCheckboxesFromField() {
        if (difficultyCheckboxes == null || difficultiesField == null) {
            return;
        }
        
        String difficulties = difficultiesField.getText();
        if(difficulties != null && !difficulties.isEmpty()){
            String[] diffArray = difficulties.split(",");
            for(String diff : diffArray){
                try{
                    int diffInt = Integer.parseInt(diff);
                    JCheckBox checkbox = difficultyCheckboxes.get(diffInt);
                    if (checkbox != null) {
                        checkbox.setSelected(true);
                    }
                } catch (NumberFormatException ignored){}
            }
        }
    }

    @Override
    protected void loadObjects() {
        objectList.clear();

        File folder = new File("exports/item");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files != null) {
            for (File file : files) {
                Item item = FileUtils.loadItem(file.getPath());
                if (item != null) {
                    objectList.addElement(item.getName());
                    objects.put(item.getName(), item);
                }
            }
        }
    }

    @Override
    protected void createObject() {
        Item item = new Item();
        item.setName("New Item");
        item.setDescription("");
        objects.put(item.getName(), item);
        objectList.addElement(item.getName());
        objectListComponent.setSelectedValue(item.getName(), true);

        // Automatically create a file for the new item
        String fileName = "exports/item/" + item.getName() + ".txt";
        FileUtils.saveItem(item, fileName);

        // Set current file
        currentFile = fileName;
        fileLabel.setText("Current File: " + fileName);
        System.out.println("New item automatically saved to file: " + fileName);

        editObject(item);
    }

    @Override
    protected void editObject(Item item) {
        if (item == null) {
            return;
        }
        
        if (nameField != null) {
            nameField.setText(item.getName());
        }
        
        if (descriptionArea != null) {
            descriptionArea.setText(item.getDescription());
        }
        
        if (foodValueSpinner != null) {
            foodValueSpinner.setValue(item.getFoodValue());
        }
        
        if (waterValueSpinner != null) {
            waterValueSpinner.setValue(item.getWaterValue());
        }
        
        if (goldValueSpinner != null) {
            goldValueSpinner.setValue(item.getGoldValue());
        }
        
        if (difficultiesField != null) {
            difficultiesField.setText(item.getDifficulties());
        }
        
        updateCheckboxesFromField();

        currentImagePath = item.getImagePath();
        if (currentImagePath != null && !currentImagePath.isEmpty() && imageLabel != null) {
            ImageIcon icon = ImageUtils.loadScaledImageIcon(currentImagePath, 100, 100);
            if (icon != null) {
                imageLabel.setIcon(icon);
            } else {
                imageLabel.setIcon(null);
            }
        } else if (imageLabel != null) {
            imageLabel.setIcon(null);
        }
    }

    @Override
    protected void saveObject() {
        String selectedName = objectListComponent.getSelectedValue();
        if (selectedName != null) {
            Item item = objects.get(selectedName);
            if (item == null) {
                return;
            }
            
            String oldName = item.getName();

            if (nameField != null) {
                item.setName(nameField.getText());
            }
            
            if (descriptionArea != null) {
                item.setDescription(descriptionArea.getText());
            }
            
            if (foodValueSpinner != null) {
                item.setFoodValue((Integer) foodValueSpinner.getValue());
            }
            
            if (waterValueSpinner != null) {
                item.setWaterValue((Integer) waterValueSpinner.getValue());
            }
            
            if (goldValueSpinner != null) {
                item.setGoldValue((Integer) goldValueSpinner.getValue());
            }
            
            if (difficultiesField != null) {
                item.setDifficulties(difficultiesField.getText());
            }

            if (currentImagePath != null && !currentImagePath.isEmpty()) {
                item.setImagePath(currentImagePath);
            }

            if (!oldName.equals(item.getName())) {
                objects.remove(oldName);
                objects.put(item.getName(), item);
                int selectedIndex = objectListComponent.getSelectedIndex();
                objectList.remove(selectedIndex);
                objectList.add(selectedIndex, item.getName());
                objectListComponent.setSelectedIndex(selectedIndex);
            }

            // For new items, automatically export to file
            if (currentFile == null) {
                String fileName = "exports/item/" + item.getName() + ".txt";
                FileUtils.saveItem(item, fileName);
                currentFile = fileName;
                if (fileLabel != null) {
                    fileLabel.setText("Current File: " + fileName);
                }
                System.out.println("Item automatically saved to file: " + fileName);
            } else {
                // Save to file if current file exists
                FileUtils.saveItem(item, currentFile);
                System.out.println("Item saved to file: " + currentFile);
            }

            // Show save confirmation
            JOptionPane.showMessageDialog(this, "Item saved successfully!", "Save Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    protected void exportObject() {
        String selectedName = objectListComponent.getSelectedValue();
        if (selectedName != null) {
            Item item = objects.get(selectedName);
            if (item == null) {
                return;
            }
            
            String fileName = "exports/item/" + item.getName() + ".txt";
            FileUtils.saveItem(item, fileName);
            currentFile = fileName;
            if (fileLabel != null) {
                fileLabel.setText("Current File: " + fileName);
            }
            JOptionPane.showMessageDialog(this, "Item exported to " + fileName, "Export Successful", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    protected void importObject() {
        JFileChooser fileChooser = new JFileChooser("exports/item");
        fileChooser.setDialogTitle("Import Item");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".txt");
            }

            @Override
            public String getDescription() {
                return "Item Files (*.txt)";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            loadItemFromFile(selectedFile.getPath());
        }
    }

    protected void browseObject() {
        JFileChooser fileChooser = new JFileChooser("exports/item");
        fileChooser.setDialogTitle("Open Item");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".txt");
            }

            @Override
            public String getDescription() {
                return "Item Files (*.txt)";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            loadItemFromFile(selectedFile.getPath());
        }
    }

    private void loadItemFromFile(String filePath) {
        Item item = FileUtils.loadItem(filePath);
        if (item != null) {
            // Add to list if not already present
            if (!objects.containsKey(item.getName())) {
                objectList.addElement(item.getName());
                objects.put(item.getName(), item);
            }
            // Select in list
            objectListComponent.setSelectedValue(item.getName(), true);
            // Set current file
            currentFile = filePath;
            if (fileLabel != null) {
                fileLabel.setText("Current File: " + filePath);
            }
        }
    }

    @Override
    public void handleListSelectionChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            String selectedName = objectListComponent.getSelectedValue();
            if (selectedName != null) {
                Item item = objects.get(selectedName);
                if (item != null) {
                    editObject(item);
                    // Update current file
                    currentFile = "exports/item/" + item.getName() + ".txt";
                    if (fileLabel != null) {
                        fileLabel.setText("Current File: " + currentFile);
                    }
                }
            }
        }
    }

    private void browseImage(ActionEvent e) {
        JFileChooser fileChooser = new JFileChooser("resources");
        fileChooser.setDialogTitle("Open Image");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || 
                    f.getName().toLowerCase().endsWith(".png") || 
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
            if (imageLabel != null) {
                ImageIcon icon = ImageUtils.loadScaledImageIcon(currentImagePath, 100, 100);
                if (icon != null) {
                    imageLabel.setIcon(icon);
                }
            }
        }
    }

    @Override
    protected void deleteObject() {
        String selectedName = objectListComponent.getSelectedValue();
        if (selectedName != null) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the item '" + selectedName + "'?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
                
            if (confirm == JOptionPane.YES_OPTION) {
                // Remove from maps
                objects.remove(selectedName);
                
                // Remove from list
                int selectedIndex = objectListComponent.getSelectedIndex();
                objectList.removeElement(selectedName);
                
                // Delete file if it exists
                if (currentFile != null) {
                    File file = new File(currentFile);
                    if (file.exists()) {
                        if (file.delete()) {
                            System.out.println("Deleted file: " + currentFile);
                        } else {
                            System.out.println("Failed to delete file: " + currentFile);
                        }
                    }
                    currentFile = null;
                    if (fileLabel != null) {
                        fileLabel.setText("Current File: ");
                    }
                }

                // Select another item if available
                if (objectList.size() > 0) {
                    objectListComponent.setSelectedIndex(0);
                } else {
                    if (nameField != null) {
                        nameField.setText("");
                    }
                    if (descriptionArea != null) {
                        descriptionArea.setText("");
                    }
                    if (foodValueSpinner != null) {
                        foodValueSpinner.setValue(0);
                    }
                    if (waterValueSpinner != null) {
                        waterValueSpinner.setValue(0);
                    }
                    if (goldValueSpinner != null) {
                        goldValueSpinner.setValue(0);
                    }
                    if (difficultiesField != null) {
                        difficultiesField.setText("");
                    }
                    if (difficultyCheckboxes != null) {
                        for(JCheckBox cb : difficultyCheckboxes.values()){
                            if (cb != null) {
                                cb.setSelected(false);
                            }
                        }
                    }
                    if (imageLabel != null) {
                        imageLabel.setIcon(null);
                    }
                    currentImagePath = null;
                }
            }
        }
    }
}
