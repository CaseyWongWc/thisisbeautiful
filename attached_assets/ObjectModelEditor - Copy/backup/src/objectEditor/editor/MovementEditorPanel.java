package objectEditor.editor;

import objectEditor.model.Movement;
import objectEditor.util.FileUtils;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MovementEditorPanel extends BaseEditorPanel<Movement> {
    private static final long serialVersionUID = 1L;

    private JTextField nameField;
    private JTextArea directionsArea;
    private JCheckBox repeatingCheckBox;
    private JCheckBox randomCheckBox;
    private JCheckBox reversibleCheckBox;
    private JSlider moveIntervalSlider;
    private JLabel moveIntervalValueLabel;
    private MovementVisualizer visualizer;
    private Timer animationTimer;
    private boolean isAnimating = false;
    private boolean playInfinitely = false;

    public MovementEditorPanel() {
        super("movement", "Movement");
        initUI();
        loadObjects();
    }

    @Override
    protected void handleListSelectionChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            String selectedName = objectListComponent.getSelectedValue();
            if (selectedName != null) {
                Movement movement = objects.get(selectedName);
                if (movement != null) {
                    editObject(movement);
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
                "Are you sure you want to delete the movement '" + selectedName + "'?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
            
            if (confirm == JOptionPane.YES_OPTION) { 
                // Remove from objects map
                objects.remove(selectedName);
                
                // Remove from list model
                objectList.removeElement(selectedName);
                
                // Delete the file if it exists
                if (currentFile != null) {
                    File file = new File(currentFile);
                    if (file.exists()) {
                        if (file.delete()) {
                            System.out.println("Deleted movement file: " + currentFile);
                        } else {
                            System.err.println("Failed to delete movement file: " + currentFile);
                        }
                    }
                    currentFile = null;
                    fileLabel.setText("Current File: [None]");
                }
                
                // Select another item if available
                if (objectList.size() > 0) {
                    objectListComponent.setSelectedIndex(0);
                } else {
                    clearForm();
                }
            }
        }
    }

    @Override
    protected void saveObject() {
        String selectedName = objectListComponent.getSelectedValue();
        if (selectedName != null) {
            Movement movement = objects.get(selectedName);
            
            // Update movement from form
            String oldName = movement.getName();
            movement.setName(nameField.getText());
            
            // Get directions from text area
            List<String> directions = new ArrayList<>();
            for (String line : directionsArea.getText().split("\n")) {
                line = line.trim();
                if (!line.isEmpty()) {
                    directions.add(line);
                }
            }
            movement.setDirections(directions);
            
            // Update other properties
            movement.setRepeating(repeatingCheckBox.isSelected());
            movement.setRandom(randomCheckBox.isSelected());
            movement.setReversible(reversibleCheckBox.isSelected());
            movement.setMoveInterval(moveIntervalSlider.getValue());
            
            // Handle name change
            if (!oldName.equals(movement.getName())) {
                // Remove old file if name changed
                File oldFile = new File("exports/movement/" + oldName + ".txt");
                if (oldFile.exists()) {
                    oldFile.delete();
                }
                
                // Update the list and map
                objects.remove(oldName);
                objects.put(movement.getName(), movement);
                
                int selectedIndex = objectListComponent.getSelectedIndex();
                objectList.remove(selectedIndex);
                objectList.add(selectedIndex, movement.getName());
                objectListComponent.setSelectedIndex(selectedIndex);
            }
            
            // Save to file
            String fileName = "exports/movement/" + movement.getName() + ".txt";
            FileUtils.saveMovement(movement, fileName);
            
            // Update current file label
            currentFile = fileName;
            fileLabel.setText("Current File: " + fileName);
            System.out.println("Movement saved to file: " + fileName);
        }
    }

    private Movement getCurrentMovement() {
        String name = nameField.getText().trim();
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a name.");
            return null;
        }

        Movement movement = new Movement();
        movement.setName(name);
        List<String> directions = new ArrayList<>();
        for (String line : directionsArea.getText().split("\n")) {
            line = line.trim();
            if (!line.isEmpty()) {
                directions.add(line);
            }
        }
        movement.setDirections(directions);
        movement.setRepeating(repeatingCheckBox.isSelected());
        movement.setRandom(randomCheckBox.isSelected());
        movement.setReversible(reversibleCheckBox.isSelected());
        movement.setMoveInterval(moveIntervalSlider.getValue());
        return movement;
    }

    private void clearForm() {
        nameField.setText("");
        directionsArea.setText("");
        repeatingCheckBox.setSelected(false);
        randomCheckBox.setSelected(false);
        reversibleCheckBox.setSelected(false);
        moveIntervalSlider.setValue(1); // Reset to default
        moveIntervalValueLabel.setText("1"); // Update label
        if (visualizer != null) {
            visualizer.setDirections(new ArrayList<>());
            visualizer.repaint();
        }
        currentFile = null;
        fileLabel.setText("Current File: [None]");
    }

    @Override
    protected void initUI() {
        super.initUI();

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));

        // Basic properties
        JPanel basicPanel = new JPanel(new GridBagLayout());
        basicPanel.setBorder(BorderFactory.createTitledBorder("Basic Properties"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        // Name field
        gbc.gridx = 0;
        gbc.gridy = 0;
        basicPanel.add(new JLabel("Name:"), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        basicPanel.add(nameField, gbc);

        // Checkboxes
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        JPanel checkboxPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        repeatingCheckBox = new JCheckBox("Repeating");
        randomCheckBox = new JCheckBox("Random");
        reversibleCheckBox = new JCheckBox("Reversible");
        checkboxPanel.add(repeatingCheckBox);
        checkboxPanel.add(randomCheckBox);
        checkboxPanel.add(reversibleCheckBox);
        basicPanel.add(checkboxPanel, gbc);
        
        // Move interval slider
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.NONE;
        gbc.weightx = 0.0;
        basicPanel.add(new JLabel("Move Interval:"), gbc);
        
        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        
        JPanel sliderPanel = new JPanel(new BorderLayout(5, 0));
        moveIntervalSlider = new JSlider(JSlider.HORIZONTAL, 1, 10, 1);
        moveIntervalSlider.setMajorTickSpacing(1);
        moveIntervalSlider.setPaintTicks(true);
        moveIntervalSlider.setPaintLabels(true);
        moveIntervalSlider.setSnapToTicks(true);
        
        // Create a tooltip explaining what the slider does
        moveIntervalSlider.setToolTipText(
            "Sets how many turns between movements: 1 = every turn, 2 = every other turn, etc."
        );
        
        // Add a label to show the current value
        moveIntervalValueLabel = new JLabel("1", JLabel.RIGHT);
        moveIntervalValueLabel.setPreferredSize(new Dimension(30, 20));
        
        // Update the label when the slider changes
        moveIntervalSlider.addChangeListener(e -> {
            moveIntervalValueLabel.setText(String.valueOf(moveIntervalSlider.getValue()));
        });
        
        sliderPanel.add(moveIntervalSlider, BorderLayout.CENTER);
        sliderPanel.add(moveIntervalValueLabel, BorderLayout.EAST);
        
        basicPanel.add(sliderPanel, gbc);

        formPanel.add(basicPanel);

        // Create a tabbed pane for the different visual representations
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Directions panel (standard editor)
        JPanel directionsPanel = createDirectionsPanel();
        tabbedPane.addTab("Directions", directionsPanel);
        
        // Visual Demo tab
        JPanel visualDemoPanel = createVisualDemoPanel();
        tabbedPane.addTab("Visual Demo", visualDemoPanel);

        formPanel.add(tabbedPane);
        add(formPanel, BorderLayout.CENTER);
    }
    
    /**
     * Creates the directions panel with text area and direction buttons.
     */
    private JPanel createDirectionsPanel() {
        JPanel directionsPanel = new JPanel(new BorderLayout(5, 5));
        directionsPanel.setBorder(BorderFactory.createTitledBorder("Movement Directions"));

        JLabel instructionsLabel = new JLabel(
            "<html>Enter one direction per line using: N, S, E, W, NE, NW, SE, SW</html>"
        );
        directionsPanel.add(instructionsLabel, BorderLayout.NORTH);

        directionsArea = new JTextArea(10, 30);
        directionsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane directionsScroll = new JScrollPane(directionsArea);
        directionsPanel.add(directionsScroll, BorderLayout.CENTER);

        // Quick insert buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] directions = {"N", "S", "E", "W", "NE", "NW", "SE", "SW"};
        for (String dir : directions) {
            JButton dirButton = new JButton(dir);
            dirButton.addActionListener(e -> {
                directionsArea.append(dir + "\n");
                updateVisualizer();
            });
            buttonPanel.add(dirButton);
        }
        directionsPanel.add(buttonPanel, BorderLayout.SOUTH);

        // Add preset patterns
        JPanel patternPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        patternPanel.setBorder(BorderFactory.createTitledBorder("Preset Patterns"));
        
        String[] patterns = {"Zigzag", "Square", "Circle"};
        for (String pattern : patterns) {
            JButton patternButton = new JButton(pattern);
            patternButton.addActionListener(e -> {
                switch (pattern) {
                    case "Zigzag":
                        directionsArea.setText("E\nNE\nE\nSE");
                        break;
                    case "Square":
                        directionsArea.setText("N\nN\nE\nE\nS\nS\nW\nW");
                        break;
                    case "Circle":
                        directionsArea.setText("N\nNE\nE\nSE\nS\nSW\nW\nNW");
                        break;
                }
                updateVisualizer();
            });
            patternPanel.add(patternButton);
        }
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(buttonPanel, BorderLayout.NORTH);
        southPanel.add(patternPanel, BorderLayout.SOUTH);
        directionsPanel.add(southPanel, BorderLayout.SOUTH);

        return directionsPanel;
    }
    
    /**
     * Creates the visual demo panel with interactive visualization.
     */
    private JPanel createVisualDemoPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        
        // Create visualizer with larger size
        visualizer = new MovementVisualizer();
        visualizer.setPreferredSize(new Dimension(500, 500));
        
        // Wrap the visualizer in a scroll pane to add scrollbars
        JScrollPane visualizerScrollPane = new JScrollPane(visualizer);
        visualizerScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        visualizerScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        visualizerScrollPane.setPreferredSize(new Dimension(350, 350));
        
        // Set a viewport position to center the grid
        visualizer.centerViewport(visualizerScrollPane);
        
        panel.add(visualizerScrollPane, BorderLayout.CENTER);
        
        // Add controls
        JPanel controlsContainer = new JPanel(new BorderLayout());
        controlsContainer.setBorder(
            BorderFactory.createTitledBorder("Animation Controls")
        );
        
        // Options panel with checkboxes
        JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
        JCheckBox infinitePlayCheckBox = new JCheckBox("Play Infinitely");
        infinitePlayCheckBox.setToolTipText("Continue playing past the end of the pattern");
        infinitePlayCheckBox.addActionListener(e -> {
            playInfinitely = infinitePlayCheckBox.isSelected();
            if (isAnimating) {
                // Restart animation with new setting
                stopAnimation();
                startAnimation(playInfinitely);
            }
        });
        optionsPanel.add(infinitePlayCheckBox);
        
        // Zoom controls
        JPanel zoomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 5));
        JButton zoomInButton = new JButton("+");
        zoomInButton.setToolTipText("Zoom In");
        zoomInButton.addActionListener(e -> visualizer.zoomIn());
        
        JButton zoomOutButton = new JButton("-");
        zoomOutButton.setToolTipText("Zoom Out");
        zoomOutButton.addActionListener(e -> visualizer.zoomOut());
        
        JButton resetZoomButton = new JButton("Reset Zoom");
        resetZoomButton.addActionListener(e -> visualizer.resetZoom());
        
        zoomPanel.add(zoomInButton);
        zoomPanel.add(zoomOutButton);
        zoomPanel.add(resetZoomButton);
        
        // Center button
        JButton centerButton = new JButton("Center View");
        centerButton.setToolTipText("Center the view on the starting point");
        centerButton.addActionListener(e -> visualizer.centerViewport(visualizerScrollPane));
        zoomPanel.add(centerButton);
        
        // Combine options and zoom panel
        JPanel topControlsPanel = new JPanel(new BorderLayout());
        topControlsPanel.add(optionsPanel, BorderLayout.WEST);
        topControlsPanel.add(zoomPanel, BorderLayout.EAST);
        
        // Control buttons panel
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        
        JButton stepBackButton = new JButton("< Step Back");
        stepBackButton.addActionListener(e -> visualizer.stepBack());
        
        JButton playButton = new JButton("Play");
        playButton.addActionListener(e -> {
            isAnimating = !isAnimating;
            if (isAnimating) {
                playButton.setText("Pause");
                startAnimation(infinitePlayCheckBox.isSelected());
            } else {
                playButton.setText("Play");
                stopAnimation();
            }
        });
        
        JButton stepForwardButton = new JButton("Step Forward >");
        stepForwardButton.addActionListener(e -> visualizer.stepForward());
        
        JButton resetButton = new JButton("Reset");
        resetButton.addActionListener(e -> {
            stopAnimation();
            playButton.setText("Play");
            isAnimating = false;
            visualizer.reset();
        });
        
        controlPanel.add(stepBackButton);
        controlPanel.add(playButton);
        controlPanel.add(stepForwardButton);
        controlPanel.add(resetButton);
        
        JPanel controlsPanel = new JPanel(new BorderLayout());
        controlsPanel.add(topControlsPanel, BorderLayout.NORTH);
        controlsPanel.add(controlPanel, BorderLayout.CENTER);
        
        controlsContainer.add(controlsPanel, BorderLayout.CENTER);
        panel.add(controlsContainer, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void updateVisualizer() {
        // Parse the directions from the text area
        List<String> directions = new ArrayList<>();
        for (String line : directionsArea.getText().split("\n")) {
            line = line.trim();
            if (!line.isEmpty()) {
                directions.add(line);
            }
        }
        
        // Set them on the visualizer
        if (visualizer != null) {
            visualizer.setDirections(directions);
            visualizer.repaint();
        }
    }
    
    @Override
    protected void editObject(Movement movement) {
        // Set basic properties
        nameField.setText(movement.getName());
        
        // Set directions
        StringBuilder sb = new StringBuilder();
        for (String direction : movement.getDirections()) {
            sb.append(direction).append("\n");
        }
        directionsArea.setText(sb.toString());
        
        // Set checkboxes
        repeatingCheckBox.setSelected(movement.isRepeating());
        randomCheckBox.setSelected(movement.isRandom());
        reversibleCheckBox.setSelected(movement.isReversible());
        
        // Set move interval
        int moveInterval = movement.getMoveInterval();
        moveIntervalSlider.setValue(moveInterval);
        moveIntervalValueLabel.setText(String.valueOf(moveInterval));
        
        // Update the visualizer
        updateVisualizer();
        
        // Set file information
        currentFile = "exports/movement/" + movement.getName() + ".txt";
        fileLabel.setText("Current File: " + currentFile);
    }
    
    @Override
    protected void loadObjects() {
        objectList.clear();
        objects.clear();
        
        File exportsDir = new File("exports/movement");
        if (!exportsDir.exists()) {
            exportsDir.mkdirs();
        }
        
        File[] files = exportsDir.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files != null) {
            for (File file : files) {
                Movement movement = FileUtils.loadMovement(file.getAbsolutePath());
                if (movement != null) {
                    objectList.addElement(movement.getName());
                    objects.put(movement.getName(), movement);
                }
            }
        }
    }
    
    @Override
    protected void createObject() {
        // Create a new movement with default values
        Movement movement = new Movement();
        movement.setName("New Movement");
        
        // Add some default directions (circle pattern)
        List<String> directions = new ArrayList<>();
        directions.add("N");
        directions.add("NE");
        directions.add("E");
        directions.add("SE");
        directions.add("S");
        directions.add("SW");
        directions.add("W");
        directions.add("NW");
        movement.setDirections(directions);
        
        // Set default properties
        movement.setRepeating(true);
        movement.setRandom(false);
        movement.setReversible(false);
        movement.setMoveInterval(1);
        
        // Add to the list and select it
        objectList.addElement(movement.getName());
        objects.put(movement.getName(), movement);
        objectListComponent.setSelectedValue(movement.getName(), true);
        
        // Automatically create a file for the new movement
        String fileName = "exports/movement/" + movement.getName() + ".txt";
        FileUtils.saveMovement(movement, fileName);
        
        // Set current file
        currentFile = fileName;
        fileLabel.setText("Current File: " + fileName);
        System.out.println("New movement automatically saved to file: " + fileName);
        
        editObject(movement);
    }
    
    @Override
    protected void exportObject() {
        String selectedName = objectListComponent.getSelectedValue();
        if (selectedName != null) {
            Movement movement = objects.get(selectedName);
            
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setDialogTitle("Export Movement");
            if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                // Add .txt extension if missing
                if (!file.getName().toLowerCase().endsWith(".txt")) {
                    file = new File(file.getAbsolutePath() + ".txt");
                }
                if (FileUtils.saveMovement(movement, file.getAbsolutePath())) {
                    JOptionPane.showMessageDialog(this, 
                        "Movement exported successfully to " + file.getAbsolutePath());
                }
            }
        }
    }
    
    @Override
    protected void importObject() {
        loadMovementFromFile();
    }
    
    protected void browseObject() {
        loadMovementFromFile();
    }
    
    /**
     * Loads a movement from a file and adds it to the objects list
     */
    private void loadMovementFromFile() {
        JFileChooser fileChooser = new JFileChooser("exports/movement");
        fileChooser.setDialogTitle("Import Movement");
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            @Override
            public boolean accept(File f) {
                return f.isDirectory() || f.getName().endsWith(".txt");
            }

            @Override
            public String getDescription() {
                return "Movement Files (*.txt)";
            }
        });

        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            Movement movement = FileUtils.loadMovement(selectedFile.getAbsolutePath());
            if (movement != null) {
                // Add to list if not already present
                if (!objects.containsKey(movement.getName())) {
                    objectList.addElement(movement.getName());
                }
                objects.put(movement.getName(), movement);
                
                // Select the imported object
                objectListComponent.setSelectedValue(movement.getName(), true);
                
                // If the file isn't in the exports directory, save it there
                if (!selectedFile.getAbsolutePath().startsWith("exports/movement/")) {
                    String fileName = "exports/movement/" + movement.getName() + ".txt";
                    FileUtils.saveMovement(movement, fileName);
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Movement imported successfully: " + movement.getName());
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to import movement from " + selectedFile.getAbsolutePath(), 
                    "Import Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void startAnimation(boolean infinite) {
        if (animationTimer != null) {
            animationTimer.cancel();
        }
        
        playInfinitely = infinite;
        visualizer.resetTurnCounter(); // Reset the turn counter when starting animation
        
        animationTimer = new Timer();
        animationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Get current move interval from the slider
                int moveInterval = moveIntervalSlider.getValue();
                
                // Increment turn counter and check if we should move
                visualizer.incrementTurn();
                
                // Only step forward when the current turn reaches the move interval
                if (visualizer.shouldMove(moveInterval)) {
                    if (playInfinitely) {
                        // If we're at the end but playing infinitely, wrap around to the beginning
                        if (visualizer.isAtEnd()) {
                            visualizer.reset();
                        }
                        visualizer.stepForward();
                    } else {
                        // Only step forward if not at the end
                        if (!visualizer.isAtEnd()) {
                            visualizer.stepForward();
                        } else {
                            // We reached the end, stop the animation
                            SwingUtilities.invokeLater(() -> {
                                stopAnimation();
                                isAnimating = false;
                                // Find the play button and update its text
                                for (Component comp : ((JPanel)visualizer.getParent().getParent().getParent()).getComponents()) {
                                    if (comp instanceof JPanel) {
                                        JPanel panel = (JPanel)comp;
                                        for (Component c : panel.getComponents()) {
                                            if (c instanceof JPanel) {
                                                for (Component btn : ((JPanel)c).getComponents()) {
                                                    if (btn instanceof JButton && ((JButton)btn).getText().equals("Pause")) {
                                                        ((JButton)btn).setText("Play");
                                                        break;
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            });
                        }
                    }
                } else {
                    // Still update the visualizer to show the turn counter even if we don't move
                    visualizer.repaint();
                }
            }
        }, 500, 500);
    }
    
    private void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.cancel();
            animationTimer = null;
        }
    }
    
    private class MovementVisualizer extends JPanel {
        private static final long serialVersionUID = 1L;
        private static final int DEFAULT_CELL_SIZE = 30;
        private int cellSize = DEFAULT_CELL_SIZE;
        private List<String> directions = new ArrayList<>();
        private List<Point> pathPoints = new ArrayList<>();
        private int currentStep = 0;
        private int currentTurn = 0; // Tracks the current turn within the move interval

        public MovementVisualizer() {
            setBackground(Color.WHITE);
        }

        public void setDirections(List<String> directions) {
            this.directions = directions;
            calculatePath();
            currentStep = 0;
            currentTurn = 0; // Reset turn counter when setting directions
        }
        
        public boolean isAtEnd() {
            return currentStep >= pathPoints.size() - 1;
        }
        
        public void stepForward() {
            if (!directions.isEmpty() && (currentStep < pathPoints.size() - 1 || playInfinitely)) {
                if (playInfinitely && currentStep >= pathPoints.size() - 1) {
                    // In infinite mode, loop back to the beginning
                    currentStep = 0;
                } else {
                    currentStep++;
                }
                repaint();
            }
        }
        
        public void stepBack() {
            if (currentStep > 0) {
                currentStep--;
                repaint();
            }
        }
        
        public void reset() {
            currentStep = 0;
            currentTurn = 0; // Also reset turn counter
            repaint();
        }
        
        public void zoomIn() {
            cellSize += 5;
            recalculateSize();
        }
        
        public void zoomOut() {
            cellSize = Math.max(10, cellSize - 5);
            recalculateSize();
        }
        
        public void resetZoom() {
            cellSize = DEFAULT_CELL_SIZE;
            recalculateSize();
        }
        
        private void recalculateSize() {
            if (!pathPoints.isEmpty()) {
                // Calculate bounds
                int minX = Integer.MAX_VALUE;
                int maxX = Integer.MIN_VALUE;
                int minY = Integer.MAX_VALUE;
                int maxY = Integer.MIN_VALUE;
                
                for (Point p : pathPoints) {
                    minX = Math.min(minX, p.x);
                    maxX = Math.max(maxX, p.x);
                    minY = Math.min(minY, p.y);
                    maxY = Math.max(maxY, p.y);
                }
                
                // Add padding
                minX -= 2;
                minY -= 2;
                maxX += 2;
                maxY += 2;
                
                // Calculate new size
                int width = (maxX - minX + 1) * cellSize;
                int height = (maxY - minY + 1) * cellSize;
                
                // Set preferred size
                setPreferredSize(new Dimension(width, height));
                revalidate();
            }
            repaint();
        }
        
        /**
         * Centers the viewport on the starting point
         */
        public void centerViewport(JScrollPane scrollPane) {
            if (pathPoints.isEmpty()) {
                return;
            }
            
            // Get the starting point (0,0)
            Point startPoint = pathPoints.get(0);
            
            // Calculate viewport position
            Rectangle viewRect = scrollPane.getViewport().getViewRect();
            
            int x = startPoint.x * cellSize - (viewRect.width / 2) + cellSize / 2;
            int y = startPoint.y * cellSize - (viewRect.height / 2) + cellSize / 2;
            
            // Ensure coordinates are positive
            x = Math.max(0, x);
            y = Math.max(0, y);
            
            // Set viewport position
            scrollPane.getViewport().setViewPosition(new Point(x, y));
        }
        
        private void calculatePath() {
            // Clear existing path
            pathPoints.clear();
            
            if (directions.isEmpty()) {
                return;
            }
            
            // Start at origin (0, 0)
            Point currentPosition = new Point(0, 0);
            pathPoints.add(new Point(currentPosition));
            
            // Calculate path based on directions
            for (String direction : directions) {
                int dx = 0;
                int dy = 0;
                
                switch (direction.toUpperCase()) {
                    case "N":
                        dy = -1;
                        break;
                    case "S":
                        dy = 1;
                        break;
                    case "E":
                        dx = 1;
                        break;
                    case "W":
                        dx = -1;
                        break;
                    case "NE":
                        dx = 1;
                        dy = -1;
                        break;
                    case "NW":
                        dx = -1;
                        dy = -1;
                        break;
                    case "SE":
                        dx = 1;
                        dy = 1;
                        break;
                    case "SW":
                        dx = -1;
                        dy = 1;
                        break;
                }
                
                currentPosition = new Point(currentPosition.x + dx, currentPosition.y + dy);
                pathPoints.add(new Point(currentPosition));
            }
            
            // Adjust size based on path
            recalculateSize();
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            
            if (directions.isEmpty() || pathPoints.isEmpty()) {
                return;
            }
            
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Find min/max coordinates to center the grid
            int minX = Integer.MAX_VALUE;
            int maxX = Integer.MIN_VALUE;
            int minY = Integer.MAX_VALUE;
            int maxY = Integer.MIN_VALUE;
            
            for (Point p : pathPoints) {
                minX = Math.min(minX, p.x);
                maxX = Math.max(maxX, p.x);
                minY = Math.min(minY, p.y);
                maxY = Math.max(maxY, p.y);
            }
            
            // Calculate grid size with padding
            int gridWidth = maxX - minX + 5;  // Add some padding
            int gridHeight = maxY - minY + 5;
            
            // Calculate offset to center the grid (so 0,0 is in the middle of the panel)
            int offsetX = getWidth() / 2 - cellSize / 2;
            int offsetY = getHeight() / 2 - cellSize / 2;
            
            // Draw grid centered around 0,0
            g2d.setColor(Color.LIGHT_GRAY);
            
            // Draw vertical grid lines
            for (int x = -10; x <= 10; x++) {
                int xPos = offsetX + x * cellSize;
                g2d.drawLine(xPos, 0, xPos, getHeight());
            }
            
            // Draw horizontal grid lines
            for (int y = -10; y <= 10; y++) {
                int yPos = offsetY + y * cellSize;
                g2d.drawLine(0, yPos, getWidth(), yPos);
            }
            
            // Draw coordinate axes
            g2d.setColor(Color.DARK_GRAY);
            g2d.setStroke(new BasicStroke(1.5f));
            g2d.drawLine(0, offsetY, getWidth(), offsetY);  // X-axis
            g2d.drawLine(offsetX, 0, offsetX, getHeight()); // Y-axis
            
            // Performance optimization: Only draw parts that are visible
            Rectangle clipBounds = g2d.getClipBounds();
            
            // Draw path lines
            if (pathPoints.size() > 1) {
                g2d.setColor(Color.GREEN.darker());
                g2d.setStroke(new BasicStroke(2f));
                
                // Draw path up to current step
                for (int i = 0; i < Math.min(currentStep, pathPoints.size() - 1); i++) {
                    Point p1 = pathPoints.get(i);
                    Point p2 = pathPoints.get(i + 1);
                    int x1 = offsetX + p1.x * cellSize;
                    int y1 = offsetY + p1.y * cellSize;
                    int x2 = offsetX + p2.x * cellSize;
                    int y2 = offsetY + p2.y * cellSize;
                    
                    // Check if the line is in the visible area
                    if (clipBounds.intersectsLine(x1, y1, x2, y2)) {
                        g2d.drawLine(x1, y1, x2, y2);
                    }
                }
                
                // Draw circles at each point on the path
                g2d.setColor(Color.BLUE);
                for (int i = 0; i <= Math.min(currentStep, pathPoints.size() - 1); i++) {
                    Point p = pathPoints.get(i);
                    int x = offsetX + p.x * cellSize - 3;
                    int y = offsetY + p.y * cellSize - 3;
                    
                    // Check if the point is in the visible area
                    if (clipBounds.contains(x, y)) {
                        g2d.fillOval(x, y, 6, 6);
                    }
                }
                
                // Highlight start point
                g2d.setColor(Color.RED);
                Point start = pathPoints.get(0);
                int startX = offsetX + start.x * cellSize - 5;
                int startY = offsetY + start.y * cellSize - 5;
                if (clipBounds.contains(startX, startY)) {
                    g2d.fillOval(startX, startY, 10, 10);
                }
                
                // Highlight current point
                Point currentPoint = pathPoints.get(currentStep);
                int currX = offsetX + currentPoint.x * cellSize - 5;
                int currY = offsetY + currentPoint.y * cellSize - 5;
                if (clipBounds.contains(currX, currY)) {
                    g2d.setColor(Color.RED);
                    g2d.fillOval(currX, currY, 10, 10);
                    g2d.setColor(Color.BLACK);
                    g2d.drawOval(currX, currY, 10, 10);
                }
                
                // Display turn counter
                g2d.setColor(Color.BLACK);
                g2d.drawString("Turn: " + currentTurn, 10, getHeight() - 10);
                
                // Display move interval
                if (moveIntervalSlider != null) {
                    int moveInterval = moveIntervalSlider.getValue();
                    g2d.drawString("Move interval: " + moveInterval, 100, getHeight() - 10);
                }
            }
        }
        
        /**
         * Increments the turn counter
         */
        public void incrementTurn() {
            currentTurn++;
            repaint();
        }
        
        /**
         * Resets the turn counter to 0
         */
        public void resetTurnCounter() {
            currentTurn = 0;
            repaint();
        }
        
        /**
         * Determines if movement should occur based on the current turn and move interval
         * @param moveInterval the number of turns between movements
         * @return true if movement should occur, false otherwise
         */
        public boolean shouldMove(int moveInterval) {
            // Should move when the turn counter reaches the move interval
            // Using modulo to handle recurring intervals
            return moveInterval <= 1 || currentTurn % moveInterval == 0;
        }
    }
}
