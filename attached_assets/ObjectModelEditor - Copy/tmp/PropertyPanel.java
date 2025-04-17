package objectEditor.view;

import objectEditor.controller.EditorController;
import objectEditor.model.ClassDefinition;
import objectEditor.model.ObjectInstance;
import objectEditor.model.Property;
import objectEditor.model.PropertyType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.Map;

/**
 * Panel for editing object properties.
 */
public class PropertyPanel extends JPanel {
    private EditorController controller;
    private JPanel propertyFieldsPanel;
    private JTabbedPane tabbedPane;
    private Map<String, JComponent> propertyEditors;
    private ObjectInstance currentObject;
    
    /**
     * Creates a new property panel with the specified controller.
     * 
     * @param controller the controller
     */
    public PropertyPanel(EditorController controller) {
        this.controller = controller;
        this.propertyEditors = new HashMap<>();
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
        
        // Create tabbed pane for property tabs
        tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        
        // Create first tab (Tab 1)
        JPanel tab1Panel = new JPanel(new BorderLayout());
        JLabel tab1Label = new JLabel("object properties 1");
        tab1Panel.add(tab1Label, BorderLayout.CENTER);
        tab1Panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        // Create second tab (Tab 2)
        JPanel tab2Panel = new JPanel(new BorderLayout());
        JLabel tab2Label = new JLabel("object properties 2");
        tab2Panel.add(tab2Label, BorderLayout.CENTER);
        tab2Panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        // Create third tab (Tab 3)
        JPanel tab3Panel = new JPanel(new BorderLayout());
        JLabel tab3Label = new JLabel("object properties 3");
        tab3Panel.add(tab3Label, BorderLayout.CENTER);
        tab3Panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        
        // Add tabs to tabbed pane
        tabbedPane.addTab("1", tab1Panel);
        tabbedPane.addTab("2", tab2Panel);
        tabbedPane.addTab("3", tab3Panel);
        
        // Create property fields panel that will contain the actual property editors
        propertyFieldsPanel = new JPanel();
        propertyFieldsPanel.setLayout(new BoxLayout(propertyFieldsPanel, BoxLayout.Y_AXIS));
        
        // Add a scroll pane for property fields
        JScrollPane scrollPane = new JScrollPane(propertyFieldsPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        
        // Add the tabbed pane at the top and the property fields below
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(tabbedPane, BorderLayout.NORTH);
        
        // Add apply button
        JButton applyButton = new JButton("Apply Changes");
        applyButton.addActionListener(this::applyChanges);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(applyButton);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        // Add components to the main panel
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Applies changes to the object properties.
     */
    private void applyChanges(ActionEvent event) {
        if (controller == null || currentObject == null) return;
        
        // Gather property values from editors
        Map<String, Object> updatedValues = new HashMap<>();
        
        for (Map.Entry<String, JComponent> entry : propertyEditors.entrySet()) {
            String propertyName = entry.getKey();
            JComponent editor = entry.getValue();
            
            ClassDefinition classDefinition = currentObject.getClassDefinition();
            Property property = findProperty(classDefinition, propertyName);
            
            if (property != null) {
                Object value = getValueFromEditor(editor, property.getType());
                updatedValues.put(propertyName, value);
            }
        }
        
        // Update object with new values
        controller.updateObjectProperties(currentObject, updatedValues);
    }
    
    /**
     * Finds a property by name in a class definition.
     */
    private Property findProperty(ClassDefinition classDefinition, String propertyName) {
        for (Property property : classDefinition.getProperties()) {
            if (property.getName().equals(propertyName)) {
                return property;
            }
        }
        return null;
    }
    
    /**
     * Gets the value from a property editor component.
     */
    private Object getValueFromEditor(JComponent editor, PropertyType type) {
        switch (type) {
            case STRING:
                return ((JTextField) editor).getText();
                
            case INTEGER:
                try {
                    return Integer.parseInt(((JTextField) editor).getText());
                } catch (NumberFormatException e) {
                    return 0;
                }
                
            case DOUBLE:
                try {
                    return Double.parseDouble(((JTextField) editor).getText());
                } catch (NumberFormatException e) {
                    return 0.0;
                }
                
            case BOOLEAN:
                return ((JCheckBox) editor).isSelected();
                
            case OBJECT_REFERENCE:
                return ((JComboBox<?>) editor).getSelectedItem();
                
            default:
                return null;
        }
    }
    
    /**
     * Sets the object to edit.
     */
    public void setObject(ObjectInstance objectInstance) {
        this.currentObject = objectInstance;
        updatePropertyFields();
    }
    
    /**
     * Updates the property fields for the current object.
     */
    private void updatePropertyFields() {
        propertyFieldsPanel.removeAll();
        propertyEditors.clear();
        
        if (currentObject == null) {
            repaint();
            revalidate();
            return;
        }
        
        // Get class definition and properties
        ClassDefinition classDefinition = currentObject.getClassDefinition();
        java.util.List<Property> properties = classDefinition.getProperties();
        
        // Create property editors
        for (Property property : properties) {
            JPanel propertyPanel = new JPanel(new BorderLayout());
            propertyPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            
            // Create label
            JLabel label = new JLabel(property.getName() + ":");
            label.setPreferredSize(new Dimension(100, label.getPreferredSize().height));
            propertyPanel.add(label, BorderLayout.WEST);
            
            // Create editor component based on property type
            JComponent editor = createEditorComponent(property, currentObject);
            propertyEditors.put(property.getName(), editor);
            propertyPanel.add(editor, BorderLayout.CENTER);
            
            propertyFieldsPanel.add(propertyPanel);
        }
        
        repaint();
        revalidate();
    }
    
    /**
     * Creates an editor component for a property.
     */
    private JComponent createEditorComponent(Property property, ObjectInstance objectInstance) {
        PropertyType type = property.getType();
        Object value = objectInstance.getPropertyValue(property.getName());
        
        switch (type) {
            case STRING:
                JTextField textField = new JTextField();
                if (value != null) {
                    textField.setText(value.toString());
                }
                return textField;
                
            case INTEGER:
                JTextField intField = new JTextField();
                if (value != null) {
                    intField.setText(value.toString());
                }
                return intField;
                
            case DOUBLE:
                JTextField doubleField = new JTextField();
                if (value != null) {
                    doubleField.setText(value.toString());
                }
                return doubleField;
                
            case BOOLEAN:
                JCheckBox checkBox = new JCheckBox();
                if (value != null) {
                    checkBox.setSelected((Boolean) value);
                }
                return checkBox;
                
            case OBJECT_REFERENCE:
                JComboBox<ObjectInstance> comboBox = new JComboBox<>();
                if (property.getReferenceClass() != null && controller != null) {
                    java.util.List<ObjectInstance> referenceObjects = 
                            controller.getObjectsOfClass(property.getReferenceClass());
                    
                    comboBox.addItem(null);  // Allow null reference
                    for (ObjectInstance obj : referenceObjects) {
                        comboBox.addItem(obj);
                    }
                    
                    comboBox.setSelectedItem(value);
                }
                return comboBox;
                
            default:
                return new JTextField();
        }
    }
}
