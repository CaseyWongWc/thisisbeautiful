package objectEditor.view;

import objectEditor.controller.EditorController;
import objectEditor.model.ObjectInstance;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Panel for displaying and arranging objects in a workspace.
 */
public class WorkspacePanel extends JPanel {
    private EditorController controller;
    private List<ObjectInstance> displayedObjects;
    private Map<ObjectInstance, Point> objectPositions;
    private ObjectInstance selectedObject;
    
    /**
     * Creates a new workspace panel with the specified controller.
     * 
     * @param controller the controller
     */
    public WorkspacePanel(EditorController controller) {
        this.controller = controller;
        this.displayedObjects = new ArrayList<>();
        this.objectPositions = new HashMap<>();
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
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        // Add mouse listener to handle object selection
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                selectObjectAt(e.getPoint());
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawObjects(g);
    }
    
    /**
     * Draws the objects on the canvas.
     */
    private void drawObjects(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw connections between objects
        g2d.setColor(Color.GRAY);
        drawObjectConnections(g2d);
        
        // Draw objects
        for (ObjectInstance obj : displayedObjects) {
            Point pos = objectPositions.get(obj);
            if (pos != null) {
                drawObject(g2d, obj, pos, obj == selectedObject);
            }
        }
    }
    
    /**
     * Draws connections between objects based on their references.
     */
    private void drawObjectConnections(Graphics2D g) {
        // In a real application, this would examine object properties to find references
        // and draw lines between related objects
    }
    
    /**
     * Draws an object on the canvas.
     */
    private void drawObject(Graphics2D g, ObjectInstance obj, Point pos, boolean isSelected) {
        int width = 120;
        int height = 60;
        
        // Draw box
        if (isSelected) {
            g.setColor(new Color(230, 230, 255));
            g.fillRect(pos.x, pos.y, width, height);
            g.setColor(new Color(100, 100, 255));
            g.setStroke(new BasicStroke(2));
        } else {
            g.setColor(new Color(240, 240, 240));
            g.fillRect(pos.x, pos.y, width, height);
            g.setColor(Color.BLACK);
            g.setStroke(new BasicStroke(1));
        }
        
        g.drawRect(pos.x, pos.y, width, height);
        
        // Draw text
        g.setColor(Color.BLACK);
        g.drawString(obj.getName(), pos.x + 10, pos.y + 20);
        g.drawString("(" + obj.getClassDefinition().getName() + ")", pos.x + 10, pos.y + 40);
    }
    
    /**
     * Selects the object at the specified point.
     */
    private void selectObjectAt(Point point) {
        ObjectInstance selected = null;
        
        for (ObjectInstance obj : displayedObjects) {
            Point pos = objectPositions.get(obj);
            if (pos != null) {
                Rectangle rect = new Rectangle(pos.x, pos.y, 120, 60);
                if (rect.contains(point)) {
                    selected = obj;
                    break;
                }
            }
        }
        
        if (selected != selectedObject) {
            selectedObject = selected;
            if (controller != null) {
                controller.setSelectedObject(selected);
            }
            repaint();
        }
    }
    
    /**
     * Adds an object to the workspace.
     */
    public void addObject(ObjectInstance obj) {
        if (!displayedObjects.contains(obj)) {
            displayedObjects.add(obj);
            
            // Assign a position if none exists
            if (!objectPositions.containsKey(obj)) {
                int x = 50 + (displayedObjects.size() % 3) * 150;
                int y = 50 + (displayedObjects.size() / 3) * 100;
                objectPositions.put(obj, new Point(x, y));
            }
            
            repaint();
        }
    }
    
    /**
     * Removes an object from the workspace.
     */
    public void removeObject(ObjectInstance obj) {
        displayedObjects.remove(obj);
        objectPositions.remove(obj);
        
        if (selectedObject == obj) {
            selectedObject = null;
        }
        
        repaint();
    }
    
    /**
     * Clears all objects from the workspace.
     */
    public void clearObjects() {
        displayedObjects.clear();
        objectPositions.clear();
        selectedObject = null;
        repaint();
    }
    
    /**
     * Sets the displayed objects.
     */
    public void setObjects(List<ObjectInstance> objects) {
        displayedObjects.clear();
        displayedObjects.addAll(objects);
        
        // Assign positions to new objects
        for (int i = 0; i < objects.size(); i++) {
            ObjectInstance obj = objects.get(i);
            if (!objectPositions.containsKey(obj)) {
                int x = 50 + (i % 3) * 150;
                int y = 50 + (i / 3) * 100;
                objectPositions.put(obj, new Point(x, y));
            }
        }
        
        if (selectedObject != null && !displayedObjects.contains(selectedObject)) {
            selectedObject = null;
        }
        
        repaint();
    }
    
    /**
     * Sets the selected object.
     */
    public void setSelectedObject(ObjectInstance obj) {
        if (obj != null && !displayedObjects.contains(obj)) {
            addObject(obj);
        }
        
        selectedObject = obj;
        repaint();
    }
}
