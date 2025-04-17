package objectEditor.editor;

import objectEditor.model.Spawner;
import objectEditor.util.FileUtils;
import objectEditor.util.ImageUtils;
import objectEditor.util.ErrorLogger;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
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
    
    /**
     * Creates a new spawner editor panel.
     */
    public SpawnerEditorPanel() {
        super("spawner", "Spawner");
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
        spawnerPanel.add(maxSpawnCapSpinner, gbc);

        // Spawn Frequency
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 0.0;
        spawnerPanel.add(new JLabel("Spawn Frequency (turns):"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        spawnFrequencySpinner = new JSpinner(new SpinnerNumberModel(5, 1, 100, 1));
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
        spawnerPanel.add(isDirectedCheckbox, gbc);

        // Direction
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.weightx = 0.0;
        spawnerPanel.add(new JLabel("Direction:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        directionComboBox = new JComboBox<>(new String[] {"N", "NE", "E", "SE", "S", "SW", "W", "NW", "none"});
        spawnerPanel.add(directionComboBox, gbc);

        // Random Orientation
        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.weightx = 0.0;
        spawnerPanel.add(new JLabel("Random Orientation:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        randomOrientationCheckbox = new JCheckBox();
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
        spawnerPanel.add(objectTypeComboBox, gbc);

        // Object Template
        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.weightx = 0.0;
        spawnerPanel.add(new JLabel("Object Template:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 1.0;
        objectTemplateField = new JTextField(20);
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

        File folder = new File("exports/spawner");
        if (!folder.exists()) {
            folder.mkdirs();
        }

        File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files != null) {
            for (File file : files) {
                Spawner spawner = FileUtils.loadSpawner(file.getPath());
                if (spawner != null) {
                    objectList.addElement(spawner.getName());
                    objects.put(spawner.getName(), spawner);
                }
            }
        }
    }

    @Override
    protected void createObject() {
        Spawner spawner = new Spawner();
        spawner.setName("New Spawner");
        spawner.setDescription("");
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
        nameField.setText(spawner.getName());
        descriptionArea.setText(spawner.getDescription());
        maxSpawnCapSpinner.setValue(spawner.getMaxSpawnCap());
        spawnFrequencySpinner.setValue(spawner.getSpawnFrequency());
        isDirectedCheckbox.setSelected(spawner.isDirected());
        directionComboBox.setSelectedItem(spawner.getDirection());
        randomOrientationCheckbox.setSelected(spawner.isRandomOrientation());
        objectTypeComboBox.setSelectedItem(spawner.getObjectType());
        objectTemplateField.setText(spawner.getObjectTemplate());

        currentImagePath = spawner.getImagePath();
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
    }

    @Override
    protected void saveObject() {
        String selectedName = objectListComponent.getSelectedValue();
        if (selectedName != null) {
            Spawner spawner = objects.get(selectedName);
            String oldName = spawner.getName();

            spawner.setName(nameField.getText());
            spawner.setDescription(descriptionArea.getText());
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

            if (!oldName.equals(spawner.getName())) {
                objects.remove(oldName);
                objects.put(spawner.getName(), spawner);
                int selectedIndex = objectListComponent.getSelectedIndex();
                objectList.remove(selectedIndex);
                objectList.add(selectedIndex, spawner.getName());
                objectListComponent.setSelectedIndex(selectedIndex);
            }

            // For new spawners, automatically export to file
            if (currentFile == null) {
                String fileName = "exports/spawner/" + spawner.getName() + ".txt";
                FileUtils.saveSpawner(spawner, fileName);
                currentFile = fileName;
                fileLabel.setText("Current File: " + fileName);
                System.out.println("Spawner automatically saved to file: " + fileName);
            } else {
                // Save to file if current file exists
                FileUtils.saveSpawner(spawner, currentFile);
                System.out.println("Spawner saved to file: " + currentFile);
            }

            // Show save confirmation
            JOptionPane.showMessageDialog(this, "Spawner saved successfully!", "Save Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    @Override
    protected void exportObject() {
        String selectedName = objectListComponent.getSelectedValue();
        if (selectedName != null) {
            Spawner spawner = objects.get(selectedName);
            String fileName = "exports/spawner/" + spawner.getName() + ".txt";
            FileUtils.saveSpawner(spawner, fileName);
            currentFile = fileName;
            fileLabel.setText("Current File: " + fileName);
            JOptionPane.showMessageDialog(this, "Spawner exported to " + fileName, "Export Successful", JOptionPane.INFORMATION_MESSAGE);
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
                Spawner spawner = objects.get(selectedName);
                if (spawner != null) {
                    editObject(spawner);
                    // Update current file
                    currentFile = "exports/spawner/" + spawner.getName() + ".txt";
                    fileLabel.setText("Current File: " + currentFile);
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
            ImageIcon icon = ImageUtils.loadScaledImageIcon(currentImagePath, 100, 100);
            if (icon != null) {
                imageLabel.setIcon(icon);
            }
        }
    }

    private void browseTemplate(ActionEvent e) {
        String type = (String) objectTypeComboBox.getSelectedItem();
        String folder = "exports/" + type;
        
        JFileChooser fileChooser = new JFileChooser(folder);
        fileChooser.setDialogTitle("Choose " + type + " Template");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".txt");
            }

            @Override
            public String getDescription() {
                return type.substring(0, 1).toUpperCase() + type.substring(1) + " Files (*.txt)";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            String fileName = selectedFile.getName();
            if (fileName.endsWith(".txt")) {
                fileName = fileName.substring(0, fileName.length() - 4);
            }
            objectTemplateField.setText(fileName);
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
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                // Remove from list
                objectList.removeElement(selectedName);
                Spawner removedSpawner = objects.remove(selectedName);
                
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
                }
                
                // Clear selection
                objectListComponent.clearSelection();
                currentFile = null;
                fileLabel.setText("Current File: [None]");
                
                // Clear object editor
                nameField.setText("");
                descriptionArea.setText("");
                maxSpawnCapSpinner.setValue(1);
                spawnFrequencySpinner.setValue(5);
                isDirectedCheckbox.setSelected(false);
                directionComboBox.setSelectedItem("none");
                randomOrientationCheckbox.setSelected(false);
                objectTypeComboBox.setSelectedItem("item");
                objectTemplateField.setText("");
                imageLabel.setIcon(null);
                currentImagePath = null;
            }
        }
    }
}
