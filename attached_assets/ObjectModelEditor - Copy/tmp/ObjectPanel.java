package objectEditor.view;

import objectEditor.controller.EditorController;
import objectEditor.model.ClassDefinition;
import objectEditor.model.ObjectInstance;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

/**
 * Panel for managing object instances.
 */
public class ObjectPanel extends JPanel {
    private EditorController controller;
    private JList<ObjectInstance> objectList;
    private DefaultListModel<ObjectInstance> objectListModel;
    private JButton addButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton clearButton;
    private JButton restoreDefaultsButton;
    private JButton importButton;
    private JButton exportButton;
    private JTextField objectNameField;
    private JLabel iconLabel;
    
    /**
     * Creates a new object panel with the specified controller.
     * 
     * @param controller the controller
     */
    public ObjectPanel(EditorController controller) {
        this.controller = controller;
        initializeUI();
    }
    
    /**
     * Sets the controller for this panel.
     * 
     * @param controller the controller
     */
    public void setController(EditorController controller) {
        this.controller = controller;
    }
    
    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        setLayout(new BorderLayout(5, 5));
        
        // Top section with Available Objects label
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel availableObjectsLabel = new JLabel("Available objects");
        topPanel.add(availableObjectsLabel, BorderLayout.WEST);
        add(topPanel, BorderLayout.NORTH);
        
        // Create list model and list component
        objectListModel = new DefaultListModel<>();
        objectList = new JList<>(objectListModel);
        objectList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        objectList.setCellRenderer(new ObjectListCellRenderer());
        
        // Add selection listener
        objectList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && controller != null) {
                ObjectInstance selectedObject = objectList.getSelectedValue();
                controller.setSelectedObject(selectedObject);
                updateButtonStatus();
            }
        });
        
        // Add double-click listener to edit object properties
        objectList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2 && controller != null) {
                    int index = objectList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        ObjectInstance selectedObject = objectListModel.getElementAt(index);
                        controller.editObjectProperties(selectedObject);
                    }
                }
            }
        });
        
        // Add to a scroll pane
        JScrollPane scrollPane = new JScrollPane(objectList);
        add(scrollPane, BorderLayout.CENTER);
        
        // Create buttons - first row
        JPanel buttonPanel1 = new JPanel(new GridLayout(1, 2, 5, 0));
        addButton = new JButton("Add");
        addButton.addActionListener(e -> {
            if (controller != null) {
                controller.createNewObject();
            }
        });
        
        updateButton = new JButton("Update");
        updateButton.addActionListener(e -> {
            if (controller != null) {
                ObjectInstance selectedObject = objectList.getSelectedValue();
                if (selectedObject != null) {
                    controller.editObjectProperties(selectedObject);
                }
            }
        });
        
        buttonPanel1.add(addButton);
        buttonPanel1.add(updateButton);
        
        // Create buttons - second row
        JPanel buttonPanel2 = new JPanel(new GridLayout(1, 2, 5, 0));
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(e -> {
            if (controller != null) {
                ObjectInstance selectedObject = objectList.getSelectedValue();
                if (selectedObject != null) {
                    controller.deleteObject(selectedObject);
                }
            }
        });
        
        clearButton = new JButton("Clear");
        clearButton.addActionListener(e -> {
            if (controller != null) {
                controller.clearObjects();
            }
        });
        
        buttonPanel2.add(deleteButton);
        buttonPanel2.add(clearButton);
        
        // Create restore defaults button
        JPanel buttonPanel3 = new JPanel(new GridLayout(1, 1));
        restoreDefaultsButton = new JButton("Restore Defaults");
        restoreDefaultsButton.addActionListener(e -> {
            if (controller != null) {
                controller.restoreDefaults();
            }
        });
        buttonPanel3.add(restoreDefaultsButton);
        
        // Create import/export buttons
        JPanel buttonPanel4 = new JPanel(new GridLayout(1, 2, 5, 0));
        importButton = new JButton("Import");
        importButton.addActionListener(e -> {
            if (controller != null) {
                controller.importObjects();
            }
        });
        
        exportButton = new JButton("Export");
        exportButton.addActionListener(e -> {
            if (controller != null) {
                controller.exportObjects();
            }
        });
        
        buttonPanel4.add(importButton);
        buttonPanel4.add(exportButton);
        
        // Create object name field panel
        JPanel namePanel = new JPanel(new BorderLayout(5, 5));
        namePanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        JPanel objectIdentifierPanel = new JPanel(new BorderLayout());
        objectIdentifierPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        JLabel objectIdentifierLabel = new JLabel("<html><center>current object string<br>identifier (name<br>display)</center></html>");
        objectIdentifierLabel.setHorizontalAlignment(SwingConstants.CENTER);
        objectIdentifierPanel.add(objectIdentifierLabel, BorderLayout.CENTER);
        namePanel.add(objectIdentifierPanel, BorderLayout.NORTH);
        
        objectNameField = new JTextField("name1");
        namePanel.add(objectNameField, BorderLayout.CENTER);
        
        // Create icon panel
        JPanel iconPanel = new JPanel(new BorderLayout());
        iconPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 5, 0));
        
        JPanel iconContainerPanel = new JPanel(new BorderLayout());
        iconContainerPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        iconLabel = new JLabel("Icon");
        iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
        iconContainerPanel.add(iconLabel, BorderLayout.CENTER);
        iconPanel.add(iconContainerPanel, BorderLayout.CENTER);
        
        // Combine all button panels
        JPanel buttonsContainer = new JPanel();
        buttonsContainer.setLayout(new BoxLayout(buttonsContainer, BoxLayout.Y_AXIS));
        buttonsContainer.add(buttonPanel1);
        buttonsContainer.add(Box.createVerticalStrut(5));
        buttonsContainer.add(buttonPanel2);
        buttonsContainer.add(Box.createVerticalStrut(5));
        buttonsContainer.add(buttonPanel3);
        buttonsContainer.add(Box.createVerticalStrut(5));
        buttonsContainer.add(buttonPanel4);
        buttonsContainer.add(Box.createVerticalStrut(5));
        buttonsContainer.add(namePanel);
        buttonsContainer.add(iconPanel);
        
        // Bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(buttonsContainer, BorderLayout.NORTH);
        add(bottomPanel, BorderLayout.SOUTH);
        
        // Initialize button states
        updateButtonStatus();
    }
    
    /**
     * Updates the list of objects for the selected class.
     */
    public void updateObjectList(List<ObjectInstance> objects) {
        objectListModel.clear();
        for (ObjectInstance obj : objects) {
            objectListModel.addElement(obj);
        }
        updateButtonStatus();
    }
    
    /**
     * Sets the selected object in the list.
     */
    public void setSelectedObject(ObjectInstance objectInstance) {
        objectList.setSelectedValue(objectInstance, true);
        updateButtonStatus();
        
        // Update name field
        if (objectInstance != null) {
            objectNameField.setText(objectInstance.getName());
        } else {
            objectNameField.setText("");
        }
    }
    
    /**
     * Updates the status of buttons based on current selection.
     */
    private void updateButtonStatus() {
        boolean hasSelectedClass = controller != null && controller.getSelectedClass() != null;
        boolean hasSelectedObject = objectList.getSelectedValue() != null;
        boolean hasObjects = !objectListModel.isEmpty();
        
        addButton.setEnabled(hasSelectedClass);
        updateButton.setEnabled(hasSelectedObject);
        deleteButton.setEnabled(hasSelectedObject);
        clearButton.setEnabled(hasObjects);
        exportButton.setEnabled(hasObjects);
    }
    
    /**
     * Updates button status when a class is selected.
     */
    public void classSelected(ClassDefinition classDefinition) {
        addButton.setEnabled(classDefinition != null);
    }
    
    /**
     * Custom renderer for object list items.
     */
    private static class ObjectListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, 
                int index, boolean isSelected, boolean cellHasFocus) {
            
            JLabel label = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof ObjectInstance) {
                ObjectInstance objectInstance = (ObjectInstance) value;
                label.setText(objectInstance.getName());
            }
            
            return label;
        }
    }
}
