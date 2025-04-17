package objectEditor.view;

import objectEditor.controller.EditorController;
import objectEditor.model.ClassDefinition;
import objectEditor.model.Property;
import objectEditor.model.PropertyType;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel for displaying and managing classes.
 */
public class ClassPanel extends JPanel implements ListSelectionListener, ActionListener {
    private EditorController controller;
    private JList<ClassDefinition> classList;
    private DefaultListModel<ClassDefinition> classListModel;
    private JButton newButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton propertiesButton;
    private ClassDefinition selectedClass;
    
    /**
     * Creates a new class panel with the specified controller.
     * 
     * @param controller the controller
     */
    public ClassPanel(EditorController controller) {
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
        
        // Create class list
        classListModel = new DefaultListModel<>();
        classList = new JList<>(classListModel);
        classList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        classList.setCellRenderer(new ClassListCellRenderer());
        classList.addListSelectionListener(this);
        
        JScrollPane scrollPane = new JScrollPane(classList);
        
        // Create buttons
        JPanel buttonPanel = new JPanel(new GridLayout(1, 4, 5, 5));
        
        newButton = new JButton("New");
        newButton.addActionListener(this);
        
        editButton = new JButton("Edit");
        editButton.addActionListener(this);
        editButton.setEnabled(false);
        
        deleteButton = new JButton("Delete");
        deleteButton.addActionListener(this);
        deleteButton.setEnabled(false);
        
        propertiesButton = new JButton("Props");
        propertiesButton.addActionListener(this);
        propertiesButton.setEnabled(false);
        
        buttonPanel.add(newButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(propertiesButton);
        
        // Add components to panel
        add(new JLabel("Classes"), BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newButton) {
            createNewClass();
        } else if (e.getSource() == editButton) {
            editSelectedClass();
        } else if (e.getSource() == deleteButton) {
            if (selectedClass != null) {
                int result = JOptionPane.showConfirmDialog(this, 
                        "Are you sure you want to delete the class '" + selectedClass.getName() + "'?", 
                        "Confirm Deletion", JOptionPane.YES_NO_OPTION);
                
                if (result == JOptionPane.YES_OPTION && controller != null) {
                    JOptionPane.showMessageDialog(this, 
                        "Deleting classes not implemented yet.", 
                        "Information", JOptionPane.INFORMATION_MESSAGE);
                    // Commented out as method does not exist yet
                    // controller.deleteClass(selectedClass);
                }
            }
        } else if (e.getSource() == propertiesButton) {
            editClassProperties();
        }
    }
    
    /**
     * Creates a new class.
     */
    private void createNewClass() {
        String className = JOptionPane.showInputDialog(this, "Enter class name:", "New Class", JOptionPane.PLAIN_MESSAGE);
        
        if (className != null && !className.trim().isEmpty() && controller != null) {
            JOptionPane.showMessageDialog(this, 
                "Creating new classes not implemented yet.", 
                "Information", JOptionPane.INFORMATION_MESSAGE);
            // Commented out as method does not exist yet
            // controller.createNewClass(className);
        }
    }
    
    /**
     * Edits the selected class.
     */
    private void editSelectedClass() {
        if (selectedClass != null) {
            String newName = JOptionPane.showInputDialog(this, "Enter class name:", selectedClass.getName());
            
            if (newName != null && !newName.trim().isEmpty()) {
                selectedClass.setName(newName);
                classList.repaint();
                
                if (controller != null) {
                    controller.setModified(true);
                }
            }
        }
    }
    
    /**
     * Edits the properties of the selected class.
     */
    private void editClassProperties() {
        if (selectedClass != null) {
            ClassDefinition classDefinition = selectedClass;
            
            // Display property editor dialog
            JOptionPane.showMessageDialog(this, 
                "Editing class properties not implemented yet.", 
                "Information", JOptionPane.INFORMATION_MESSAGE);
            
            // This method doesn't exist yet
            // if (controller != null) {
            //     controller.updateClass(classDefinition);
            // }
        }
    }
    
    @Override
    public void valueChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            boolean hasSelection = classList.getSelectedIndex() != -1;
            editButton.setEnabled(hasSelection);
            deleteButton.setEnabled(hasSelection);
            propertiesButton.setEnabled(hasSelection);
            
            ClassDefinition selection = classList.getSelectedValue();
            
            if (selection != null) {
                selectedClass = selection;
                if (controller != null) {
                    controller.setSelectedClass(selection);
                }
            }
        }
    }
    
    /**
     * Updates the class list model.
     * 
     * @param classes the classes to display
     */
    public void updateClassList(List<ClassDefinition> classes) {
        classListModel.clear();
        for (ClassDefinition cls : classes) {
            classListModel.addElement(cls);
        }
    }
    
    /**
     * Sets the selected class.
     * 
     * @param classDefinition the class to select
     */
    public void setSelectedClass(ClassDefinition classDefinition) {
        if (classDefinition != null) {
            classList.setSelectedValue(classDefinition, true);
        } else {
            classList.clearSelection();
        }
    }
    
    /**
     * Custom cell renderer for the class list.
     */
    private class ClassListCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, 
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            if (value instanceof ClassDefinition) {
                ClassDefinition cls = (ClassDefinition) value;
                setText(cls.getName());
            }
            
            return this;
        }
    }
}
