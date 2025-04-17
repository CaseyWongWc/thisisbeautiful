package objectEditor.editor;

import objectEditor.model.Item;

import javax.swing.*;
import java.awt.*;

/**
 * Panel for editing items. Contains tabs for different types of items.
 */
public class ItemEditorPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    /**
     * Creates a new item editor panel.
     */
    public ItemEditorPanel() {
        setLayout(new BorderLayout());
        
        // Create tabbed pane for different item types
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Create standard item editor panel
        StandardItemEditorPanel standardItemPanel = new StandardItemEditorPanel();
        tabbedPane.addTab("Standard Items", standardItemPanel);
        
        // Add tabbed pane to panel
        add(tabbedPane, BorderLayout.CENTER);
    }
}
