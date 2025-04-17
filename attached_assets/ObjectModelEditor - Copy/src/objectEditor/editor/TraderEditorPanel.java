package objectEditor.editor;

import objectEditor.model.Trader;
import objectEditor.model.Item;
import objectEditor.model.Movement;
import objectEditor.util.FileUtils;
import objectEditor.util.ImageUtils;
import objectEditor.util.ErrorLogger;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Panel for editing traders.
 */
public class TraderEditorPanel extends BaseEditorPanel<Trader> {
    private static final long serialVersionUID = 1L;

    // Basic properties
    private JTextField nameField;
    private JTextArea descriptionArea;
    private JLabel imageLabel;
    private String currentImagePath;
    
    // Dialogue options
    private JTextArea encounterDialogueArea;
    private JTextArea tradeEventDialogueArea;
    private JTextArea positiveDialogueArea;
    private JTextArea leaveTradeDialogueArea;
    private JTextArea aggroDialogueArea;
    
    // Behavior properties
    private JSpinner maxOffersBeforeDeclineSpinner;
    private JSpinner maxAggroDurationSpinner;
    private JSpinner stealSuccessRateSpinner;
    private JSpinner minPlayerResourceSpinner;
    private JSpinner maxPlayerResourceSpinner;
    private JCheckBox isAggroCheckBox;
    private JCheckBox aggroOnMaxRejectCheckBox;
    
    // Penalty properties
    private JSpinner strengthPenaltySpinner;
    private JSpinner waterPenaltySpinner;
    private JSpinner foodPenaltySpinner;
    
    // Movement properties
    private JComboBox<String> passiveMovementComboBox;
    private JComboBox<String> aggroMovementComboBox;
    private DefaultComboBoxModel<String> passiveMovementModel;
    private DefaultComboBoxModel<String> aggroMovementModel;
    
    // Trade offers
    private DefaultListModel<String> tradeOffersListModel;
    private JList<String> tradeOffersList;
    private DefaultComboBoxModel<String> availableItemsModel;
    private JComboBox<String> availableItemsComboBox;
    
    // Trade simulation
    private TradeSimulationPanel tradeSimulationPanel;
    private JDialog tradeSimulationDialog;

    public TraderEditorPanel() {
        super("trader", "Trader");
        initUI();
        loadObjects();
        createTradeSimulationDialog();
    }

    @Override
    protected void initUI() {
        super.initUI();
        
        // Create tabbed pane for organizing properties
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Basic properties panel
        JPanel basicPanel = createBasicPanel();
        tabbedPane.addTab("Basic Properties", basicPanel);
        
        // Dialogue panel
        JPanel dialoguePanel = createDialoguePanel();
        tabbedPane.addTab("Dialogue", dialoguePanel);
        
        // Behavior panel
        JPanel behaviorPanel = createBehaviorPanel();
        tabbedPane.addTab("Behavior", behaviorPanel);
        
        // Trade Offers panel
        JPanel tradeOffersPanel = createTradeOffersPanel();
        tabbedPane.addTab("Trade Offers", tradeOffersPanel);
        
        // Add the tabbed pane to this panel
        add(tabbedPane, BorderLayout.CENTER);
        
        // Initialize dropdown lists
        refreshMovementDropdowns();
        
        // Add the Trade Demo button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton tradeDemoButton = new JButton("Open Trade Demo");
        tradeDemoButton.addActionListener(e -> openTradeSimulation());
        buttonPanel.add(tradeDemoButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private JPanel createBasicPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Name
        panel.add(new JLabel("Name:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        nameField = new JTextField(20);
        panel.add(nameField, gbc);
        
        // Description
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        descriptionArea = new JTextArea(5, 20);
        descriptionArea.setLineWrap(true);
        JScrollPane descScroll = new JScrollPane(descriptionArea);
        panel.add(descScroll, gbc);
        
        // Image
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Image:"), gbc);
        gbc.gridx = 1;
        
        JPanel imagePanel = new JPanel(new BorderLayout());
        imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(100, 100));
        imageLabel.setBorder(BorderFactory.createEtchedBorder());
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        
        JButton browseButton = new JButton("Browse...");
        browseButton.addActionListener(e -> browseImage());
        imagePanel.add(browseButton, BorderLayout.SOUTH);
        
        panel.add(imagePanel, gbc);
        
        // Penalties section
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(15, 4, 4, 4);
        panel.add(new JLabel("Penalties:"), gbc);
        gbc.insets = new Insets(4, 4, 4, 4);
        
        // Strength Penalty
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Strength Penalty:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        strengthPenaltySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        panel.add(strengthPenaltySpinner, gbc);
        
        // Water Penalty
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Water Penalty:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        waterPenaltySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        panel.add(waterPenaltySpinner, gbc);
        
        // Food Penalty
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        panel.add(new JLabel("Food Penalty:"), gbc);
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        foodPenaltySpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
        panel.add(foodPenaltySpinner, gbc);
        
        return panel;
    }
    
    private JPanel createDialoguePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        
        // Encounter Dialogue
        panel.add(new JLabel("Encounter Dialogue:"), gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        encounterDialogueArea = new JTextArea(4, 30);
        encounterDialogueArea.setLineWrap(true);
        encounterDialogueArea.setWrapStyleWord(true);
        JScrollPane encounterScroll = new JScrollPane(encounterDialogueArea);
        panel.add(encounterScroll, gbc);
        
        // Trade Event Dialogue
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        panel.add(new JLabel("Trade Event Dialogue:"), gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        tradeEventDialogueArea = new JTextArea(4, 30);
        tradeEventDialogueArea.setLineWrap(true);
        tradeEventDialogueArea.setWrapStyleWord(true);
        JScrollPane tradeEventScroll = new JScrollPane(tradeEventDialogueArea);
        panel.add(tradeEventScroll, gbc);
        
        // Positive Dialogue
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        panel.add(new JLabel("Positive Dialogue:"), gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        positiveDialogueArea = new JTextArea(4, 30);
        positiveDialogueArea.setLineWrap(true);
        positiveDialogueArea.setWrapStyleWord(true);
        JScrollPane positiveScroll = new JScrollPane(positiveDialogueArea);
        panel.add(positiveScroll, gbc);
        
        // Leave Trade Dialogue
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        panel.add(new JLabel("Leave Trade Dialogue:"), gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        leaveTradeDialogueArea = new JTextArea(4, 30);
        leaveTradeDialogueArea.setLineWrap(true);
        leaveTradeDialogueArea.setWrapStyleWord(true);
        JScrollPane leaveTradeScroll = new JScrollPane(leaveTradeDialogueArea);
        panel.add(leaveTradeScroll, gbc);
        
        // Aggro Dialogue
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        panel.add(new JLabel("Aggro Dialogue:"), gbc);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 1.0;
        gbc.weighty = 1.0;
        aggroDialogueArea = new JTextArea(4, 30);
        aggroDialogueArea.setLineWrap(true);
        aggroDialogueArea.setWrapStyleWord(true);
        JScrollPane aggroScroll = new JScrollPane(aggroDialogueArea);
        panel.add(aggroScroll, gbc);
        
        return panel;
    }
    
    private JPanel createBehaviorPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        
        // Behavior properties section
        JPanel behaviorPropsPanel = new JPanel(new GridBagLayout());
        behaviorPropsPanel.setBorder(BorderFactory.createTitledBorder("Behavior Properties"));
        GridBagConstraints propGbc = new GridBagConstraints();
        propGbc.insets = new Insets(4, 4, 4, 4);
        propGbc.fill = GridBagConstraints.HORIZONTAL;
        propGbc.gridx = 0;
        propGbc.gridy = 0;
        propGbc.anchor = GridBagConstraints.WEST;
        
        // Max Offers Before Decline
        behaviorPropsPanel.add(new JLabel("Max Offers Before Decline:"), propGbc);
        propGbc.gridx = 1;
        propGbc.weightx = 1.0;
        maxOffersBeforeDeclineSpinner = new JSpinner(new SpinnerNumberModel(3, 1, 20, 1));
        behaviorPropsPanel.add(maxOffersBeforeDeclineSpinner, propGbc);
        
        // Max Aggro Duration
        propGbc.gridx = 0;
        propGbc.gridy++;
        propGbc.weightx = 0.0;
        behaviorPropsPanel.add(new JLabel("Max Aggro Duration:"), propGbc);
        propGbc.gridx = 1;
        propGbc.weightx = 1.0;
        maxAggroDurationSpinner = new JSpinner(new SpinnerNumberModel(60, 1, 600, 1));
        behaviorPropsPanel.add(maxAggroDurationSpinner, propGbc);
        
        // Steal Success Rate
        propGbc.gridx = 0;
        propGbc.gridy++;
        propGbc.weightx = 0.0;
        behaviorPropsPanel.add(new JLabel("Steal Success Rate:"), propGbc);
        propGbc.gridx = 1;
        propGbc.weightx = 1.0;
        stealSuccessRateSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.05));
        behaviorPropsPanel.add(stealSuccessRateSpinner, propGbc);
        
        // Min Player Resource Percentage
        propGbc.gridx = 0;
        propGbc.gridy++;
        propGbc.weightx = 0.0;
        behaviorPropsPanel.add(new JLabel("Min Player Resource %:"), propGbc);
        propGbc.gridx = 1;
        propGbc.weightx = 1.0;
        minPlayerResourceSpinner = new JSpinner(new SpinnerNumberModel(0.0, 0.0, 1.0, 0.05));
        behaviorPropsPanel.add(minPlayerResourceSpinner, propGbc);
        
        // Max Player Resource Percentage
        propGbc.gridx = 0;
        propGbc.gridy++;
        propGbc.weightx = 0.0;
        behaviorPropsPanel.add(new JLabel("Max Player Resource %:"), propGbc);
        propGbc.gridx = 1;
        propGbc.weightx = 1.0;
        maxPlayerResourceSpinner = new JSpinner(new SpinnerNumberModel(1.0, 0.0, 1.0, 0.05));
        behaviorPropsPanel.add(maxPlayerResourceSpinner, propGbc);
        
        // Is Aggro Checkbox
        propGbc.gridx = 0;
        propGbc.gridy++;
        propGbc.weightx = 0.0;
        propGbc.gridwidth = 2;
        isAggroCheckBox = new JCheckBox("Is Aggro");
        behaviorPropsPanel.add(isAggroCheckBox, propGbc);
        
        // Aggro On Max Reject Checkbox
        propGbc.gridx = 0;
        propGbc.gridy++;
        aggroOnMaxRejectCheckBox = new JCheckBox("Aggro On Max Reject");
        behaviorPropsPanel.add(aggroOnMaxRejectCheckBox, propGbc);
        
        // Add behavior props panel
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        gbc.gridwidth = 2;
        panel.add(behaviorPropsPanel, gbc);
        
        // Movement section
        JPanel movementPanel = new JPanel(new GridBagLayout());
        movementPanel.setBorder(BorderFactory.createTitledBorder("Movement Patterns"));
        GridBagConstraints moveGbc = new GridBagConstraints();
        moveGbc.insets = new Insets(4, 4, 4, 4);
        moveGbc.fill = GridBagConstraints.HORIZONTAL;
        moveGbc.gridx = 0;
        moveGbc.gridy = 0;
        moveGbc.anchor = GridBagConstraints.WEST;
        
        // Passive Movement
        moveGbc.gridx = 0;
        moveGbc.gridy = 0;
        moveGbc.weightx = 0.0;
        moveGbc.gridwidth = 1;
        movementPanel.add(new JLabel("Passive Movement:"), moveGbc);
        moveGbc.gridx = 1;
        moveGbc.weightx = 1.0;
        
        passiveMovementModel = new DefaultComboBoxModel<>();
        passiveMovementComboBox = new JComboBox<>(passiveMovementModel);
        movementPanel.add(passiveMovementComboBox, moveGbc);
        
        // Aggro Movement
        moveGbc.gridx = 0;
        moveGbc.gridy++;
        moveGbc.weightx = 0.0;
        movementPanel.add(new JLabel("Aggro Movement:"), moveGbc);
        moveGbc.gridx = 1;
        moveGbc.weightx = 1.0;
        
        aggroMovementModel = new DefaultComboBoxModel<>();
        aggroMovementComboBox = new JComboBox<>(aggroMovementModel);
        movementPanel.add(aggroMovementComboBox, moveGbc);
        
        // Refresh Movements Button
        moveGbc.gridx = 0;
        moveGbc.gridy++;
        moveGbc.gridwidth = 2;
        moveGbc.weightx = 1.0;
        JButton refreshMovementsButton = new JButton("Refresh Movements");
        refreshMovementsButton.addActionListener(e -> refreshMovementDropdowns());
        movementPanel.add(refreshMovementsButton, moveGbc);
        
        // Add movement panel
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.weightx = 1.0;
        gbc.weighty = 0.0;
        panel.add(movementPanel, gbc);
        
        return panel;
    }
    
    private JPanel createTradeOffersPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Trade Offers List
        tradeOffersListModel = new DefaultListModel<>();
        tradeOffersList = new JList<>(tradeOffersListModel);
        tradeOffersList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane offersScrollPane = new JScrollPane(tradeOffersList);
        offersScrollPane.setBorder(BorderFactory.createTitledBorder("Current Trade Offers"));
        panel.add(offersScrollPane, BorderLayout.CENTER);
        
        // Control panel for adding/removing items
        JPanel controlPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        
        // Available items dropdown
        availableItemsModel = new DefaultComboBoxModel<>();
        availableItemsComboBox = new JComboBox<>(availableItemsModel);
        controlPanel.add(availableItemsComboBox, gbc);
        
        // Add Item button
        gbc.gridx = 0;
        gbc.gridy++;
        JButton addItemButton = new JButton("Add Item");
        addItemButton.addActionListener(e -> {
            String selectedItem = (String) availableItemsComboBox.getSelectedItem();
            if (selectedItem != null && !selectedItem.isEmpty() && !tradeOffersListModel.contains(selectedItem)) {
                tradeOffersListModel.addElement(selectedItem);
            }
        });
        controlPanel.add(addItemButton, gbc);
        
        // Remove Item button
        gbc.gridx = 0;
        gbc.gridy++;
        JButton removeItemButton = new JButton("Remove Selected Item");
        removeItemButton.addActionListener(e -> {
            int selectedIndex = tradeOffersList.getSelectedIndex();
            if (selectedIndex != -1) {
                tradeOffersListModel.remove(selectedIndex);
            }
        });
        controlPanel.add(removeItemButton, gbc);
        
        // Refresh Items button
        gbc.gridx = 0;
        gbc.gridy++;
        JButton refreshItemsButton = new JButton("Refresh Available Items");
        refreshItemsButton.addActionListener(e -> refreshAvailableItems());
        controlPanel.add(refreshItemsButton, gbc);
        
        // Add control panel to main panel
        panel.add(controlPanel, BorderLayout.EAST);
        
        // Initialize items
        refreshAvailableItems();
        
        return panel;
    }
    
    private void createTradeSimulationDialog() {
        tradeSimulationPanel = new TradeSimulationPanel();
        
        // Fix: Use JFrame as parent instead of Window
        Frame frame = JFrame.getFrames().length > 0 ? JFrame.getFrames()[0] : new JFrame();
        tradeSimulationDialog = new JDialog(frame, "Trade Simulation", false);
        tradeSimulationDialog.setSize(800, 600);
        tradeSimulationDialog.setLocationRelativeTo(null);
        tradeSimulationDialog.setContentPane(tradeSimulationPanel);
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> tradeSimulationDialog.setVisible(false));
        buttonPanel.add(closeButton);
        
        tradeSimulationDialog.add(buttonPanel, BorderLayout.SOUTH);
    }
    
    private void openTradeSimulation() {
        String selectedName = objectListComponent.getSelectedValue();
        if (selectedName != null) {
            Trader trader = objects.get(selectedName);
            if (trader != null) {
                // Check if the trader has at least one trade offer
                if (trader.getTradeOffers() == null || trader.getTradeOffers().isEmpty()) {
                    JOptionPane.showMessageDialog(this,
                            "This trader has no trade offers. Please add at least one item to the trader's trade offers.",
                            "No Trade Offers",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Save current state before opening simulation
                saveObject();
                
                // Set the trader in the simulation panel
                tradeSimulationPanel.setTrader(trader);
                
                // Show the dialog
                tradeSimulationDialog.setTitle("Trade Simulation - " + trader.getName());
                tradeSimulationDialog.setVisible(true);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please select a trader to simulate trades with.",
                    "No Trader Selected",
                    JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void refreshAvailableItems() {
        String selected = (String) availableItemsComboBox.getSelectedItem();
        
        availableItemsModel.removeAllElements();
        
        // Get all items from files
        File folder = new File("exports/item");
        if (folder.exists()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));
            if (files != null) {
                for (File file : files) {
                    Item item = FileUtils.loadItem(file.getPath());
                    if (item != null) {
                        availableItemsModel.addElement(item.getName());
                    }
                }
            }
        }
        
        // Restore selection if possible
        if (selected != null && !selected.isEmpty()) {
            for (int i = 0; i < availableItemsModel.getSize(); i++) {
                if (availableItemsModel.getElementAt(i).equals(selected)) {
                    availableItemsComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
    }
    
    private void refreshMovementDropdowns() {
        String passiveSelected = (String) passiveMovementComboBox.getSelectedItem();
        String aggroSelected = (String) aggroMovementComboBox.getSelectedItem();
        
        passiveMovementModel.removeAllElements();
        aggroMovementModel.removeAllElements();
        
        // Add empty option
        passiveMovementModel.addElement("");
        aggroMovementModel.addElement("");
        
        // Get all movements from files
        File folder = new File("exports/movement");
        if (folder.exists()) {
            File[] files = folder.listFiles((dir, name) -> name.endsWith(".txt"));
            if (files != null) {
                for (File file : files) {
                    Movement movement = FileUtils.loadMovement(file.getPath());
                    if (movement != null) {
                        String movementName = movement.getName();
                        passiveMovementModel.addElement(movementName);
                        aggroMovementModel.addElement(movementName);
                    }
                }
            }
        }
        
        // Restore selection for passive movement if possible
        if (passiveSelected != null && !passiveSelected.isEmpty()) {
            for (int i = 0; i < passiveMovementModel.getSize(); i++) {
                if (passiveMovementModel.getElementAt(i).equals(passiveSelected)) {
                    passiveMovementComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        // Restore selection for aggro movement if possible
        if (aggroSelected != null && !aggroSelected.isEmpty()) {
            for (int i = 0; i < aggroMovementModel.getSize(); i++) {
                if (aggroMovementModel.getElementAt(i).equals(aggroSelected)) {
                    aggroMovementComboBox.setSelectedIndex(i);
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
            if (icon != null) {
                imageLabel.setIcon(icon);
            }
            
            System.out.println("Selected image: " + currentImagePath);
        }
    }

    @Override
    protected void loadObjects() {
        objectList.clear();
        objects.clear();
        
        File exportsDir = new File("exports/trader");
        if (!exportsDir.exists()) {
            exportsDir.mkdirs();
        }
        
        File[] files = exportsDir.listFiles((dir, name) -> name.endsWith(".txt"));
        if (files != null) {
            for (File file : files) {
                Trader trader = FileUtils.loadTrader(file.getPath());
                if (trader != null) {
                    objects.put(trader.getName(), trader);
                    objectList.addElement(trader.getName());
                    
                    // Load the trade offers if specified
                    String tradeOffersStr = null;
                    try (FileInputStream in = new FileInputStream(file)) {
                        Properties props = new Properties();
                        props.load(in);
                        tradeOffersStr = props.getProperty("tradeOffers");
                    } catch (Exception e) {
                        ErrorLogger.logError("Error reading trade offers from " + file.getPath(), e);
                    }
                    
                    if (tradeOffersStr != null && !tradeOffersStr.isEmpty()) {
                        String[] itemNames = tradeOffersStr.split(",");
                        List<Item> tradeOffers = new ArrayList<>();
                        for (String itemName : itemNames) {
                            Item item = loadItemByName(itemName.trim());
                            if (item != null) {
                                tradeOffers.add(item);
                            }
                        }
                        trader.setTradeOffers(tradeOffers);
                    }
                    
                    // Load the passive movement if specified
                    String passiveMovementName = null;
                    try (FileInputStream in = new FileInputStream(file)) {
                        Properties props = new Properties();
                        props.load(in);
                        passiveMovementName = props.getProperty("passiveMovement");
                    } catch (Exception e) {
                        ErrorLogger.logError("Error reading passive movement from " + file.getPath(), e);
                    }
                    
                    if (passiveMovementName != null && !passiveMovementName.isEmpty()) {
                        Movement passiveMovement = loadMovementByName(passiveMovementName);
                        trader.setPassiveMovement(passiveMovement);
                    }
                    
                    // Load the aggro movement if specified
                    String aggroMovementName = null;
                    try (FileInputStream in = new FileInputStream(file)) {
                        Properties props = new Properties();
                        props.load(in);
                        aggroMovementName = props.getProperty("aggroMovement");
                    } catch (Exception e) {
                        ErrorLogger.logError("Error reading aggro movement from " + file.getPath(), e);
                    }
                    
                    if (aggroMovementName != null && !aggroMovementName.isEmpty()) {
                        Movement aggroMovement = loadMovementByName(aggroMovementName);
                        trader.setAggroMovement(aggroMovement);
                    }
                }
            }
        }
        
        refreshAvailableItems();
        refreshMovementDropdowns();
    }

    @Override
    protected void editObject(Trader trader) {
        // Set basic properties
        nameField.setText(trader.getName());
        descriptionArea.setText(trader.getDescription());
        
        // Set dialogue
        encounterDialogueArea.setText(trader.getEncounterDialogue());
        tradeEventDialogueArea.setText(trader.getTradeEventDialogue());
        positiveDialogueArea.setText(trader.getPositiveDialogue());
        leaveTradeDialogueArea.setText(trader.getLeaveTradeDialogue());
        aggroDialogueArea.setText(trader.getAggroDialogue());
        
        // Set behavior properties - using safe setters to avoid null pointer exceptions
        try {
            maxOffersBeforeDeclineSpinner.setValue(trader.getMaxOffersBeforeDecline());
        } catch (Exception e) {
            ErrorLogger.logError("Error setting maxOffersBeforeDeclineSpinner", e);
            maxOffersBeforeDeclineSpinner.setValue(3);
        }
        
        try {
            maxAggroDurationSpinner.setValue(trader.getMaxAggroDuration());
        } catch (Exception e) {
            ErrorLogger.logError("Error setting maxAggroDurationSpinner", e);
            maxAggroDurationSpinner.setValue(60);
        }
        
        try {
            stealSuccessRateSpinner.setValue((double)trader.getStealSuccessRate());
        } catch (Exception e) {
            ErrorLogger.logError("Error setting stealSuccessRateSpinner", e);
            stealSuccessRateSpinner.setValue(0.0);
        }
        
        try {
            minPlayerResourceSpinner.setValue(trader.getMinPlayerResourcePercentage());
        } catch (Exception e) {
            ErrorLogger.logError("Error setting minPlayerResourceSpinner", e);
            minPlayerResourceSpinner.setValue(0.0);
        }
        
        try {
            maxPlayerResourceSpinner.setValue(trader.getMaxPlayerResourcePercentage());
        } catch (Exception e) {
            ErrorLogger.logError("Error setting maxPlayerResourceSpinner", e);
            maxPlayerResourceSpinner.setValue(1.0);
        }
        
        // Set checkboxes
        isAggroCheckBox.setSelected(trader.isAggro());
        aggroOnMaxRejectCheckBox.setSelected(trader.isAggroOnMaxReject());
        
        // Set penalties
        try {
            strengthPenaltySpinner.setValue(trader.getStrengthPenalty());
        } catch (Exception e) {
            ErrorLogger.logError("Error setting strengthPenaltySpinner", e);
            strengthPenaltySpinner.setValue(0);
        }
        
        try {
            waterPenaltySpinner.setValue(trader.getWaterPenalty());
        } catch (Exception e) {
            ErrorLogger.logError("Error setting waterPenaltySpinner", e);
            waterPenaltySpinner.setValue(0);
        }
        
        try {
            foodPenaltySpinner.setValue(trader.getFoodPenalty());
        } catch (Exception e) {
            ErrorLogger.logError("Error setting foodPenaltySpinner", e);
            foodPenaltySpinner.setValue(0);
        }
        
        // Set passive movement if exists
        if (trader.getPassiveMovement() != null) {
            String movementName = trader.getPassiveMovement().getName();
            for (int i = 0; i < passiveMovementModel.getSize(); i++) {
                if (passiveMovementModel.getElementAt(i).equals(movementName)) {
                    passiveMovementComboBox.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            passiveMovementComboBox.setSelectedIndex(0); // Empty
        }
        
        // Set aggro movement if exists
        if (trader.getAggroMovement() != null) {
            String movementName = trader.getAggroMovement().getName();
            for (int i = 0; i < aggroMovementModel.getSize(); i++) {
                if (aggroMovementModel.getElementAt(i).equals(movementName)) {
                    aggroMovementComboBox.setSelectedIndex(i);
                    break;
                }
            }
        } else {
            aggroMovementComboBox.setSelectedIndex(0); // Empty
        }
        
        // Set trade offers
        tradeOffersListModel.clear();
        for (Item item : trader.getTradeOffers()) {
            tradeOffersListModel.addElement(item.getName());
        }
        
        // Set the image
        currentImagePath = trader.getImagePath();
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
        
        currentFile = "exports/trader/" + trader.getName() + ".txt";
        fileLabel.setText("Current File: " + currentFile);
    }

    @Override
    protected void saveObject() {
        String selectedName = objectListComponent.getSelectedValue();
        if (selectedName != null) {
            Trader trader = objects.get(selectedName);
            if (trader == null) {
                ErrorLogger.logError("Cannot find trader with name: " + selectedName);
                return;
            }

            String oldName = trader.getName();

            // Update basic properties
            trader.setName(nameField.getText());
            trader.setDescription(descriptionArea.getText());
            trader.setImagePath(currentImagePath);
            
            // Update dialogue
            trader.setEncounterDialogue(encounterDialogueArea.getText());
            trader.setTradeEventDialogue(tradeEventDialogueArea.getText());
            trader.setPositiveDialogue(positiveDialogueArea.getText());
            trader.setLeaveTradeDialogue(leaveTradeDialogueArea.getText());
            trader.setAggroDialogue(aggroDialogueArea.getText());
            
            // Update behavior properties
            trader.setMaxOffersBeforeDecline((Integer) maxOffersBeforeDeclineSpinner.getValue());
            trader.setMaxAggroDuration((Integer) maxAggroDurationSpinner.getValue());
            trader.setStealSuccessRate(((Number) stealSuccessRateSpinner.getValue()).floatValue());
            trader.setMinPlayerResourcePercentage((Double) minPlayerResourceSpinner.getValue());
            trader.setMaxPlayerResourcePercentage((Double) maxPlayerResourceSpinner.getValue());
            trader.setAggro(isAggroCheckBox.isSelected());
            trader.setAggroOnMaxReject(aggroOnMaxRejectCheckBox.isSelected());
            
            // Update penalties
            trader.setStrengthPenalty((Integer) strengthPenaltySpinner.getValue());
            trader.setWaterPenalty((Integer) waterPenaltySpinner.getValue());
            trader.setFoodPenalty((Integer) foodPenaltySpinner.getValue());
            
            // Handle passive movement selection
            String selectedPassiveMovement = (String) passiveMovementComboBox.getSelectedItem();
            if (selectedPassiveMovement != null && !selectedPassiveMovement.isEmpty()) {
                Movement movement = loadMovementByName(selectedPassiveMovement);
                trader.setPassiveMovement(movement);
                if (movement == null) {
                    ErrorLogger.logWarning("Movement '" + selectedPassiveMovement + "' not found for trader's passive movement");
                }
            } else {
                trader.setPassiveMovement(null);
            }
            
            // Handle aggro movement selection
            String selectedAggroMovement = (String) aggroMovementComboBox.getSelectedItem();
            if (selectedAggroMovement != null && !selectedAggroMovement.isEmpty()) {
                Movement movement = loadMovementByName(selectedAggroMovement);
                trader.setAggroMovement(movement);
                if (movement == null) {
                    ErrorLogger.logWarning("Movement '" + selectedAggroMovement + "' not found for trader's aggro movement");
                }
            } else {
                trader.setAggroMovement(null);
            }
            
            // Handle trade offers
            List<Item> tradeOffers = new ArrayList<>();
            for (int i = 0; i < tradeOffersListModel.size(); i++) {
                String itemName = tradeOffersListModel.get(i);
                Item item = loadItemByName(itemName);
                if (item != null) {
                    tradeOffers.add(item);
                } else {
                    ErrorLogger.logWarning("Item '" + itemName + "' not found for trader's trade offers");
                }
            }
            trader.setTradeOffers(tradeOffers);
            
            // Handle name change
            if (!oldName.equals(trader.getName())) {
                // Update list model - need to remove and re-add with new name
                int selectedIndex = objectListComponent.getSelectedIndex();
                objectList.remove(selectedIndex);
                objectList.add(selectedIndex, trader.getName());
                
                // Update objects map
                objects.remove(oldName);
                objects.put(trader.getName(), trader);
                
                // Remove old file if exists
                File oldFile = new File("exports/trader/" + oldName + ".txt");
                if (oldFile.exists()) {
                    oldFile.delete();
                }
                
                // Select the renamed item
                objectListComponent.setSelectedIndex(selectedIndex);
            }
            
            // Save to file
            String filePath = "exports/trader/" + trader.getName() + ".txt";
            FileUtils.saveTrader(trader, filePath);
            
            // Update current file
            currentFile = filePath;
            fileLabel.setText("Current File: " + currentFile);
        }
    }

    @Override
    protected void createObject() {
        // Create a new trader with default properties
        Trader trader = new Trader();
        trader.setName("New Trader");
        trader.setDescription("A new trader");
        
        // Add to the list and map
        objects.put(trader.getName(), trader);
        objectList.addElement(trader.getName());
        
        // Select the new trader in the list
        objectListComponent.setSelectedValue(trader.getName(), true);
        
        // Automatically create a file for the new trader
        String fileName = "exports/trader/" + trader.getName() + ".txt";
        FileUtils.saveTrader(trader, fileName);
        
        // Set current file
        currentFile = fileName;
        fileLabel.setText("Current File: " + fileName);
        System.out.println("New trader automatically saved to file: " + fileName);
        
        // Edit the new trader
        editObject(trader);
    }

    @Override
    protected void deleteObject() {
        String selectedName = objectListComponent.getSelectedValue();
        if (selectedName != null) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete the trader '" + selectedName + "'?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION);
                
            if (confirm == JOptionPane.YES_OPTION) {
                // Remove from map
                objects.remove(selectedName);
                
                // Remove from list model
                objectList.removeElement(selectedName);
                
                // Delete the file
                File file = new File("exports/trader/" + selectedName + ".txt");
                if (file.exists()) {
                    if (file.delete()) {
                        System.out.println("Deleted trader file: " + file.getPath());
                    } else {
                        ErrorLogger.logWarning("Failed to delete trader file: " + file.getPath());
                    }
                }
                
                // Select another trader if available
                if (objectList.size() > 0) {
                    objectListComponent.setSelectedIndex(0);
                } else {
                    clearForm();
                }
            }
        }
    }
    
    private void clearForm() {
        // Clear all form fields
        nameField.setText("");
        descriptionArea.setText("");
        
        // Clear dialogue fields
        encounterDialogueArea.setText("");
        tradeEventDialogueArea.setText("");
        positiveDialogueArea.setText("");
        leaveTradeDialogueArea.setText("");
        aggroDialogueArea.setText("");
        
        // Reset spinners
        maxOffersBeforeDeclineSpinner.setValue(3);
        maxAggroDurationSpinner.setValue(60);
        stealSuccessRateSpinner.setValue(0.0);
        minPlayerResourceSpinner.setValue(0.0);
        maxPlayerResourceSpinner.setValue(1.0);
        strengthPenaltySpinner.setValue(0);
        waterPenaltySpinner.setValue(0);
        foodPenaltySpinner.setValue(0);
        
        // Reset checkboxes
        isAggroCheckBox.setSelected(false);
        aggroOnMaxRejectCheckBox.setSelected(false);
        
        // Reset movement selections
        passiveMovementComboBox.setSelectedIndex(0);
        aggroMovementComboBox.setSelectedIndex(0);
        
        // Clear trade offers
        tradeOffersListModel.clear();
        
        // Clear image
        imageLabel.setIcon(null);
        currentImagePath = null;
        
        // Clear current file
        currentFile = null;
        fileLabel.setText("Current File: [None]");
    }

    @Override
    protected void handleListSelectionChanged(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            String selectedName = objectListComponent.getSelectedValue();
            if (selectedName != null) {
                Trader trader = objects.get(selectedName);
                if (trader != null) {
                    editObject(trader);
                }
            }
        }
    }
    
    @Override
    protected void importObject() {
        JFileChooser fileChooser = new JFileChooser("exports/trader");
        fileChooser.setDialogTitle("Import Trader");
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
            
            // Load trader from file
            Trader trader = FileUtils.loadTrader(file.getPath());
            if (trader != null) {
                // Check if a trader with this name already exists
                if (objects.containsKey(trader.getName())) {
                    int overwrite = JOptionPane.showConfirmDialog(
                        this,
                        "A trader with the name '" + trader.getName() + "' already exists. Overwrite?",
                        "Confirm Overwrite",
                        JOptionPane.YES_NO_OPTION);
                        
                    if (overwrite != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                
                // Add or replace in the map
                objects.put(trader.getName(), trader);
                
                // Update list
                if (!objectList.contains(trader.getName())) {
                    objectList.addElement(trader.getName());
                }
                
                // Select the imported trader
                objectListComponent.setSelectedValue(trader.getName(), true);
                
                // Save to local directory
                String filePath = "exports/trader/" + trader.getName() + ".txt";
                FileUtils.saveTrader(trader, filePath);
                
                // Load the trade offers if specified
                String tradeOffersStr = null;
                try (FileInputStream in = new FileInputStream(file)) {
                    Properties props = new Properties();
                    props.load(in);
                    tradeOffersStr = props.getProperty("tradeOffers");
                } catch (Exception e) {
                    ErrorLogger.logError("Error loading trade offers during import", e);
                }
                
                if (tradeOffersStr != null && !tradeOffersStr.isEmpty()) {
                    String[] itemNames = tradeOffersStr.split(",");
                    List<Item> tradeOffers = new ArrayList<>();
                    for (String itemName : itemNames) {
                        Item item = loadItemByName(itemName.trim());
                        if (item != null) {
                            tradeOffers.add(item);
                        }
                    }
                    trader.setTradeOffers(tradeOffers);
                }
                
                // Load the passive and aggro movements if specified
                String passiveMovementName = null;
                String aggroMovementName = null;
                try (FileInputStream in = new FileInputStream(file)) {
                    Properties props = new Properties();
                    props.load(in);
                    passiveMovementName = props.getProperty("passiveMovement");
                    aggroMovementName = props.getProperty("aggroMovement");
                } catch (Exception e) {
                    ErrorLogger.logError("Error loading movements during import", e);
                }
                
                if (passiveMovementName != null && !passiveMovementName.isEmpty()) {
                    Movement movement = loadMovementByName(passiveMovementName);
                    trader.setPassiveMovement(movement);
                }
                
                if (aggroMovementName != null && !aggroMovementName.isEmpty()) {
                    Movement movement = loadMovementByName(aggroMovementName);
                    trader.setAggroMovement(movement);
                }
                
                JOptionPane.showMessageDialog(this, 
                    "Trader '" + trader.getName() + "' imported successfully.",
                    "Import Successful", 
                    JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to import trader from file: " + file.getPath(),
                    "Import Failed", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    @Override
    protected void exportObject() {
        String selectedName = objectListComponent.getSelectedValue();
        if (selectedName != null) {
            Trader trader = objects.get(selectedName);
            if (trader != null) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setDialogTitle("Export Trader");
                fileChooser.setSelectedFile(new File(trader.getName() + ".txt"));
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
                    if (FileUtils.saveTrader(trader, file.getPath())) {
                        JOptionPane.showMessageDialog(this, 
                            "Trader '" + trader.getName() + "' exported successfully to:\n" + file.getPath(),
                            "Export Successful", 
                            JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(this, 
                            "Failed to export trader to file: " + file.getPath(),
                            "Export Failed", 
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Please select a trader to export.",
                "No Trader Selected",
                JOptionPane.WARNING_MESSAGE);
        }
    }
    
    /**
     * Loads an Item by name from the exports/item directory.
     */
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
        
        ErrorLogger.logWarning("Item not found: " + name);
        return null;
    }
    
    /**
     * Loads a Movement by name from the exports/movement directory.
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
        
        ErrorLogger.logWarning("Movement not found: " + name);
        return null;
    }
}
