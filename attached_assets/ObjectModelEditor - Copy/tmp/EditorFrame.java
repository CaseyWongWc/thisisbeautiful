package objectEditor.view;

import objectEditor.controller.EditorController;
import objectEditor.util.IconManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main frame for the object editor application.
 */
public class EditorFrame extends JFrame {
    private EditorController controller;
    private ClassPanel classPanel;
    private ObjectPanel objectPanel;
    private PropertyPanel propertyPanel;
    private WorkspacePanel workspacePanel;
    private JTextArea infoTextArea;
    private JLabel statusBar;
    private JMenuItem saveMenuItem;
    private JMenuItem saveAsMenuItem;
    
    /**
     * Creates a new editor frame.
     */
    public EditorFrame() {
        setTitle("Object Editor");
        setSize(1024, 768);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        initializeUI();
        
        // Add window listener to handle close events
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (controller != null && controller.confirmExit()) {
                    dispose();
                    System.exit(0);
                }
            }
        });
    }
    
    /**
     * Sets the controller for this frame.
     * 
     * @param controller the controller
     */
    public void setController(EditorController controller) {
        this.controller = controller;
    }
    
    /**
     * Initializes panel controllers with the main controller.
     */
    public void initializePanelControllers() {
        classPanel.setController(controller);
        objectPanel.setController(controller);
        propertyPanel.setController(controller);
        workspacePanel.setController(controller);
    }
    
    /**
     * Initializes the UI components.
     */
    private void initializeUI() {
        // Create menu bar
        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);
        
        // Create panels
        classPanel = new ClassPanel(null);
        objectPanel = new ObjectPanel(null);
        propertyPanel = new PropertyPanel(null);
        workspacePanel = new WorkspacePanel(null);
        
        // Create information panel
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoTextArea = new JTextArea();
        infoTextArea.setEditable(false);
        infoTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane infoScrollPane = new JScrollPane(infoTextArea);
        infoScrollPane.setPreferredSize(new Dimension(200, 200));
        infoPanel.add(new JLabel("Information"), BorderLayout.NORTH);
        infoPanel.add(infoScrollPane, BorderLayout.CENTER);
        
        // Create left panel
        JPanel leftPanel = new JPanel(new BorderLayout());
        JPanel leftTopPanel = new JPanel(new GridLayout(2, 1));
        leftTopPanel.add(classPanel);
        leftTopPanel.add(objectPanel);
        leftPanel.add(leftTopPanel, BorderLayout.CENTER);
        leftPanel.add(infoPanel, BorderLayout.SOUTH);
        
        // Create right panel
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(propertyPanel, BorderLayout.NORTH);
        rightPanel.add(workspacePanel, BorderLayout.CENTER);
        
        // Create split pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        splitPane.setDividerLocation(300);
        
        // Create status bar
        statusBar = new JLabel("Ready");
        statusBar.setBorder(BorderFactory.createEtchedBorder());
        
        // Add components to frame
        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(splitPane, BorderLayout.CENTER);
        getContentPane().add(statusBar, BorderLayout.SOUTH);
    }
    
    /**
     * Creates the menu bar.
     * 
     * @return the menu bar
     */
    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();
        
        // File menu
        JMenu fileMenu = new JMenu("File");
        
        JMenuItem newMenuItem = new JMenuItem("New");
        newMenuItem.addActionListener(this::newProject);
        
        JMenuItem openMenuItem = new JMenuItem("Open...");
        openMenuItem.addActionListener(this::openProject);
        
        saveMenuItem = new JMenuItem("Save");
        saveMenuItem.addActionListener(this::saveProject);
        
        saveAsMenuItem = new JMenuItem("Save As...");
        saveAsMenuItem.addActionListener(this::saveProjectAs);
        
        JMenuItem exitMenuItem = new JMenuItem("Exit");
        exitMenuItem.addActionListener(this::exit);
        
        fileMenu.add(newMenuItem);
        fileMenu.add(openMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(saveMenuItem);
        fileMenu.add(saveAsMenuItem);
        fileMenu.addSeparator();
        fileMenu.add(exitMenuItem);
        
        // Edit menu
        JMenu editMenu = new JMenu("Edit");
        
        JMenuItem importMenuItem = new JMenuItem("Import Objects...");
        importMenuItem.addActionListener(this::importObjects);
        
        JMenuItem exportMenuItem = new JMenuItem("Export Objects...");
        exportMenuItem.addActionListener(this::exportObjects);
        
        JMenuItem restoreMenuItem = new JMenuItem("Restore Defaults");
        restoreMenuItem.addActionListener(this::restoreDefaults);
        
        editMenu.add(importMenuItem);
        editMenu.add(exportMenuItem);
        editMenu.addSeparator();
        editMenu.add(restoreMenuItem);
        
        // Help menu
        JMenu helpMenu = new JMenu("Help");
        
        JMenuItem aboutMenuItem = new JMenuItem("About");
        aboutMenuItem.addActionListener(this::showAbout);
        
        helpMenu.add(aboutMenuItem);
        
        // Add menus to menu bar
        menuBar.add(fileMenu);
        menuBar.add(editMenu);
        menuBar.add(helpMenu);
        
        return menuBar;
    }
    
    /**
     * Sets the current file name in the title bar.
     * 
     * @param fileName the file name
     */
    public void setCurrentFileName(String fileName) {
        if (fileName == null) {
            setTitle("Object Editor - Untitled");
        } else {
            setTitle("Object Editor - " + fileName);
        }
    }
    
    /**
     * Creates a new project.
     */
    private void newProject(ActionEvent event) {
        if (controller != null) {
            controller.newProject();
        }
    }
    
    /**
     * Opens a project.
     */
    private void openProject(ActionEvent event) {
        if (controller != null) {
            controller.openProject();
        }
    }
    
    /**
     * Saves the current project.
     */
    private void saveProject(ActionEvent event) {
        if (controller != null) {
            controller.saveProject();
        }
    }
    
    /**
     * Saves the current project with a new name.
     */
    private void saveProjectAs(ActionEvent event) {
        if (controller != null) {
            controller.saveProjectAs();
        }
    }
    
    /**
     * Exits the application.
     */
    private void exit(ActionEvent event) {
        if (controller != null && controller.confirmExit()) {
            dispose();
            System.exit(0);
        }
    }
    
    /**
     * Imports objects.
     */
    private void importObjects(ActionEvent event) {
        if (controller != null) {
            controller.importObjects();
        }
    }
    
    /**
     * Exports objects.
     */
    private void exportObjects(ActionEvent event) {
        if (controller != null) {
            controller.exportObjects();
        }
    }
    
    /**
     * Restores default objects.
     */
    private void restoreDefaults(ActionEvent event) {
        if (controller != null) {
            controller.restoreDefaults();
        }
    }
    
    /**
     * Shows the about dialog.
     */
    private void showAbout(ActionEvent event) {
        JOptionPane.showMessageDialog(this, 
                "Object Editor\nVersion 1.0\n\nA Java Swing-based object editor", 
                "About", JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Gets the class panel.
     * 
     * @return the class panel
     */
    public ClassPanel getClassPanel() {
        return classPanel;
    }
    
    /**
     * Gets the object panel.
     * 
     * @return the object panel
     */
    public ObjectPanel getObjectPanel() {
        return objectPanel;
    }
    
    /**
     * Gets the property panel.
     * 
     * @return the property panel
     */
    public PropertyPanel getPropertyPanel() {
        return propertyPanel;
    }
    
    /**
     * Gets the workspace panel.
     * 
     * @return the workspace panel
     */
    public WorkspacePanel getWorkspacePanel() {
        return workspacePanel;
    }
    
    /**
     * Gets the information text area.
     * 
     * @return the information text area
     */
    public JTextArea getInfoTextArea() {
        return infoTextArea;
    }
}
