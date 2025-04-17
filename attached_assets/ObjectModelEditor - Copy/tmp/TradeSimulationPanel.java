package objectEditor.editor;

import objectEditor.model.Trader;
import objectEditor.model.Item;
import objectEditor.util.ErrorLogger;
import objectEditor.util.ImageUtils;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Panel for simulating trading with a trader.
 */
public class TradeSimulationPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    
    // Player resources
    private int playerGold = 100;
    private int playerWater = 80;
    private int playerFood = 70;
    private int playerStrength = 100;
    
    // UI Components
    private JLabel traderImageLabel;
    private JTextArea dialogueArea;
    private JPanel traderInfoPanel;
    private JPanel playerStatsPanel;
    private JPanel traderOffersPanel;
    private JPanel tradeActionsPanel;
    
    // Player stats spinners
    private JSpinner goldSpinner;
    private JSpinner waterSpinner;
    private JSpinner foodSpinner;
    private JSpinner strengthSpinner;
    
    // Trader stats
    private Trader trader;
    private int rejectionCount = 0;
    private boolean isTraderAggro = false;
    private Random random = new Random();
    
    // Simulation state
    private JPanel tradeResultPanel;
    private JLabel tradeResultLabel;
    private List<JButton> actionButtons = new ArrayList<>();
    
    /**
     * Creates a new trade simulation panel.
     */
    public TradeSimulationPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        initUI();
    }
    
    /**
     * Initializes the UI components.
     */
    private void initUI() {
        // Top panel with trader info and player stats
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        
        // Trader info panel (left)
        traderInfoPanel = createTraderInfoPanel();
        topPanel.add(traderInfoPanel, BorderLayout.WEST);
        
        // Player stats panel (right)
        playerStatsPanel = createPlayerStatsPanel();
        topPanel.add(playerStatsPanel, BorderLayout.EAST);
        
        // Center panel with dialogue
        JPanel centerPanel = new JPanel(new BorderLayout());
        dialogueArea = new JTextArea(5, 30);
        dialogueArea.setEditable(false);
        dialogueArea.setLineWrap(true);
        dialogueArea.setWrapStyleWord(true);
        dialogueArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        dialogueArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("Dialogue"),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));
        centerPanel.add(new JScrollPane(dialogueArea), BorderLayout.CENTER);
        
        // Trader offers panel
        traderOffersPanel = createTraderOffersPanel();
        centerPanel.add(traderOffersPanel, BorderLayout.SOUTH);
        
        // Trade result panel
        tradeResultPanel = new JPanel(new BorderLayout());
        tradeResultPanel.setBorder(BorderFactory.createTitledBorder("Trade Result"));
        tradeResultLabel = new JLabel("No trade completed yet");
        tradeResultLabel.setHorizontalAlignment(SwingConstants.CENTER);
        tradeResultPanel.add(tradeResultLabel, BorderLayout.NORTH);
        
        // A panel to show the results visually
        JPanel resultVisualPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        resultVisualPanel.setPreferredSize(new Dimension(400, 100));
        tradeResultPanel.add(resultVisualPanel, BorderLayout.CENTER);
        
        // Bottom panel with actions
        tradeActionsPanel = createTradeActionsPanel();
        
        // Add all panels to main layout
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(tradeResultPanel, BorderLayout.EAST);
        add(tradeActionsPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Creates the trader info panel.
     */
    private JPanel createTraderInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Trader"));
        
        // Trader image
        traderImageLabel = new JLabel();
        traderImageLabel.setPreferredSize(new Dimension(100, 100));
        traderImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        traderImageLabel.setBorder(BorderFactory.createEtchedBorder());
        panel.add(traderImageLabel, BorderLayout.CENTER);
        
        // Trader name and description
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        JLabel nameLabel = new JLabel("Name: ");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        JTextArea descArea = new JTextArea(2, 20);
        descArea.setEditable(false);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBackground(panel.getBackground());
        
        infoPanel.add(nameLabel);
        infoPanel.add(new JScrollPane(descArea));
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Creates the player stats panel.
     */
    private JPanel createPlayerStatsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createTitledBorder("Player Stats"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Load stat icons
        ImageIcon goldIcon = ImageUtils.loadScaledImageIcon("resources/gold.png", 20, 20);
        ImageIcon waterIcon = ImageUtils.loadScaledImageIcon("resources/water.png", 20, 20);
        ImageIcon foodIcon = ImageUtils.loadScaledImageIcon("resources/food.png", 20, 20);
        ImageIcon strengthIcon = ImageUtils.loadScaledImageIcon("resources/strength.png", 20, 20);
        
        // Gold
        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel goldLabel = new JLabel("Gold:", goldIcon, JLabel.LEFT);
        panel.add(goldLabel, gbc);
        gbc.gridx = 1;
        goldSpinner = new JSpinner(new SpinnerNumberModel(playerGold, 0, 999, 1));
        goldSpinner.addChangeListener(e -> playerGold = (int)goldSpinner.getValue());
        panel.add(goldSpinner, gbc);
        
        // Water
        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel waterLabel = new JLabel("Water:", waterIcon, JLabel.LEFT);
        panel.add(waterLabel, gbc);
        gbc.gridx = 1;
        waterSpinner = new JSpinner(new SpinnerNumberModel(playerWater, 0, 100, 1));
        waterSpinner.addChangeListener(e -> playerWater = (int)waterSpinner.getValue());
        panel.add(waterSpinner, gbc);
        
        // Food
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel foodLabel = new JLabel("Food:", foodIcon, JLabel.LEFT);
        panel.add(foodLabel, gbc);
        gbc.gridx = 1;
        foodSpinner = new JSpinner(new SpinnerNumberModel(playerFood, 0, 100, 1));
        foodSpinner.addChangeListener(e -> playerFood = (int)foodSpinner.getValue());
        panel.add(foodSpinner, gbc);
        
        // Strength
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel strengthLabel = new JLabel("Strength:", strengthIcon, JLabel.LEFT);
        panel.add(strengthLabel, gbc);
        gbc.gridx = 1;
        strengthSpinner = new JSpinner(new SpinnerNumberModel(playerStrength, 0, 100, 1));
        strengthSpinner.addChangeListener(e -> playerStrength = (int)strengthSpinner.getValue());
        panel.add(strengthSpinner, gbc);
        
        // Reset button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        JButton resetButton = new JButton("Reset Stats");
        resetButton.addActionListener(e -> resetPlayerStats());
        panel.add(resetButton, gbc);
        
        return panel;
    }
    
    /**
     * Creates the trader offers panel.
     */
    private JPanel createTraderOffersPanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createTitledBorder("Trader Offers"));
        
        JPanel offersPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        // Will be populated when trader is set
        
        panel.add(new JScrollPane(offersPanel), BorderLayout.CENTER);
        return panel;
    }
    
    /**
     * Creates the trade actions panel.
     */
    private JPanel createTradeActionsPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        panel.setBorder(BorderFactory.createTitledBorder("Actions"));
        
        // Accept trade button
        JButton acceptButton = new JButton("Accept Trade");
        acceptButton.addActionListener(new ActionButtonHandler(this, "accept"));
        actionButtons.add(acceptButton);
        
        // Decline trade button
        JButton declineButton = new JButton("Decline Trade");
        declineButton.addActionListener(new ActionButtonHandler(this, "decline"));
        actionButtons.add(declineButton);
        
        // Steal button
        JButton stealButton = new JButton("Attempt to Steal");
        stealButton.addActionListener(new ActionButtonHandler(this, "steal"));
        actionButtons.add(stealButton);
        
        // Leave button
        JButton leaveButton = new JButton("Leave");
        leaveButton.addActionListener(new ActionButtonHandler(this, "leave"));
        actionButtons.add(leaveButton);
        
        panel.add(acceptButton);
        panel.add(declineButton);
        panel.add(stealButton);
        panel.add(leaveButton);
        
        return panel;
    }
    
    /**
     * Sets the trader for this simulation panel.
     */
    public void setTrader(Trader trader) {
        this.trader = trader;
        if (trader == null) {
            return;
        }
        
        // Reset simulation state
        rejectionCount = 0;
        isTraderAggro = trader.isAggro();
        
        // Update trader info
        updateTraderInfo();
        
        // Populate trader offers
        populateTraderOffers();
        
        // Display initial dialogue
        if (isTraderAggro) {
            dialogueArea.setText(trader.getAggroDialogue());
        } else {
            dialogueArea.setText(trader.getEncounterDialogue());
        }
        
        // Update UI state based on trader aggro status
        updateUIState();
    }
    
    /**
     * Updates the trader information in the UI.
     */
    private void updateTraderInfo() {
        // Set trader image
        if (trader != null && trader.getImagePath() != null && !trader.getImagePath().isEmpty()) {
            ImageIcon icon = ImageUtils.loadScaledImageIcon(trader.getImagePath(), 100, 100);
            traderImageLabel.setIcon(icon);
        } else {
            traderImageLabel.setIcon(null);
        }
        
        // Set trader name and description
        JLabel nameLabel = (JLabel) ((JPanel)traderInfoPanel.getComponent(1)).getComponent(0);
        JTextArea descArea = (JTextArea) ((JScrollPane)((JPanel)traderInfoPanel.getComponent(1)).getComponent(1)).getViewport().getView();
        
        if (trader != null) {
            nameLabel.setText("Name: " + trader.getName());
            descArea.setText(trader.getDescription());
        } else {
            nameLabel.setText("Name: ");
            descArea.setText("");
        }
    }
    
    /**
     * Populates the trader offers panel with the trader's items.
     */
    private void populateTraderOffers() {
        JPanel offersPanel = new JPanel(new GridLayout(0, 3, 10, 10));
        
        if (trader != null && !isTraderAggro) {
            List<Item> tradeOffers = trader.getTradeOffers();
            if (tradeOffers != null && !tradeOffers.isEmpty()) {
                for (Item item : tradeOffers) {
                    offersPanel.add(createItemPanel(item));
                }
            } else {
                JLabel noOffersLabel = new JLabel("This trader has no trade offers.");
                noOffersLabel.setHorizontalAlignment(SwingConstants.CENTER);
                offersPanel.add(noOffersLabel);
            }
        } else {
            JLabel aggroLabel = new JLabel("Trader is hostile and won't trade!");
            aggroLabel.setHorizontalAlignment(SwingConstants.CENTER);
            aggroLabel.setForeground(Color.RED);
            offersPanel.add(aggroLabel);
        }
        
        // Update the offers panel
        JScrollPane scrollPane = (JScrollPane) traderOffersPanel.getComponent(0);
        scrollPane.setViewportView(offersPanel);
        traderOffersPanel.revalidate();
        traderOffersPanel.repaint();
    }
    
    /**
     * Creates a panel to display an item.
     */
    private JPanel createItemPanel(Item item) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBorder(BorderFactory.createEtchedBorder());
        
        // Item image
        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(60, 60));
        imageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            ImageIcon icon = ImageUtils.loadScaledImageIcon(item.getImagePath(), 60, 60);
            imageLabel.setIcon(icon);
        }
        
        // Item name and values
        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        
        JLabel valueLabel = new JLabel("Gold: " + item.getGoldValue());
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Show food and water values if applicable
        JPanel statsPanel = new JPanel(new GridLayout(0, 1));
        statsPanel.add(valueLabel);
        
        if (item.getFoodValue() > 0) {
            JLabel foodLabel = new JLabel("Food: +" + item.getFoodValue());
            foodLabel.setHorizontalAlignment(SwingConstants.CENTER);
            statsPanel.add(foodLabel);
        }
        
        if (item.getWaterValue() > 0) {
            JLabel waterLabel = new JLabel("Water: +" + item.getWaterValue());
            waterLabel.setHorizontalAlignment(SwingConstants.CENTER);
            statsPanel.add(waterLabel);
        }
        
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.add(nameLabel, BorderLayout.NORTH);
        infoPanel.add(statsPanel, BorderLayout.CENTER);
        
        panel.add(imageLabel, BorderLayout.CENTER);
        panel.add(infoPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    /**
     * Updates the UI state based on trader aggro status.
     */
    private void updateUIState() {
        boolean canTrade = trader != null && !isTraderAggro;
        for (JButton button : actionButtons) {
            if (button.getText().equals("Accept Trade") || button.getText().equals("Decline Trade")) {
                button.setEnabled(canTrade);
            }
        }
        
        if (isTraderAggro) {
            dialogueArea.setText(trader.getAggroDialogue());
            dialogueArea.setForeground(Color.RED);
        } else {
            dialogueArea.setForeground(Color.BLACK);
        }
    }
    
    /**
     * Resets the player stats to default values.
     */
    private void resetPlayerStats() {
        playerGold = 100;
        playerWater = 80;
        playerFood = 70;
        playerStrength = 100;
        
        goldSpinner.setValue(playerGold);
        waterSpinner.setValue(playerWater);
        foodSpinner.setValue(playerFood);
        strengthSpinner.setValue(playerStrength);
        
        // Reset trader state too
        if (trader != null) {
            isTraderAggro = trader.isAggro();
            rejectionCount = 0;
            
            // Update UI
            updateUIState();
            populateTraderOffers();
            
            if (isTraderAggro) {
                dialogueArea.setText(trader.getAggroDialogue());
            } else {
                dialogueArea.setText(trader.getEncounterDialogue());
            }
        }
        
        // Reset trade result
        tradeResultLabel.setText("No trade completed yet");
        JPanel resultVisualPanel = (JPanel) tradeResultPanel.getComponent(1);
        resultVisualPanel.removeAll();
        resultVisualPanel.revalidate();
        resultVisualPanel.repaint();
    }
    
    /**
     * Handles accepting a trade.
     */
    public void acceptTrade() {
        if (trader == null || isTraderAggro) return;
        
        List<Item> tradeOffers = trader.getTradeOffers();
        if (tradeOffers == null || tradeOffers.isEmpty()) {
            dialogueArea.setText("This trader has no offers to accept.");
            return;
        }
        
        // Choose a random item from trader's offers
        Item selectedItem = tradeOffers.get(random.nextInt(tradeOffers.size()));
        int itemValue = selectedItem.getGoldValue();
        
        // Check if player has enough gold
        if (playerGold < itemValue) {
            dialogueArea.setText("You don't have enough gold for this trade. The trader looks disappointed.");
            return;
        }
        
        // Complete the trade
        playerGold -= itemValue;
        goldSpinner.setValue(playerGold);
        
        // Apply food and water bonuses if applicable
        if (selectedItem.getFoodValue() > 0) {
            playerFood = Math.min(100, playerFood + selectedItem.getFoodValue());
            foodSpinner.setValue(playerFood);
        }
        
        if (selectedItem.getWaterValue() > 0) {
            playerWater = Math.min(100, playerWater + selectedItem.getWaterValue());
            waterSpinner.setValue(playerWater);
        }
        
        // Update dialogue
        dialogueArea.setText(trader.getPositiveDialogue());
        
        // Show trade result
        StringBuilder resultText = new StringBuilder("Trade completed! You acquired: " + selectedItem.getName());
        if (selectedItem.getFoodValue() > 0 || selectedItem.getWaterValue() > 0) {
            resultText.append(" (");
            if (selectedItem.getFoodValue() > 0) {
                resultText.append("Food +").append(selectedItem.getFoodValue());
                if (selectedItem.getWaterValue() > 0) resultText.append(", ");
            }
            if (selectedItem.getWaterValue() > 0) {
                resultText.append("Water +").append(selectedItem.getWaterValue());
            }
            resultText.append(")");
        }
        tradeResultLabel.setText(resultText.toString());
        
        // Update visual result panel
        JPanel resultVisualPanel = (JPanel) tradeResultPanel.getComponent(1);
        resultVisualPanel.removeAll();
        
        // Add item image
        JLabel itemImageLabel = new JLabel();
        if (selectedItem.getImagePath() != null && !selectedItem.getImagePath().isEmpty()) {
            ImageIcon icon = ImageUtils.loadScaledImageIcon(selectedItem.getImagePath(), 60, 60);
            itemImageLabel.setIcon(icon);
        }
        
        // Add item details
        JPanel itemDetailsPanel = new JPanel(new GridLayout(0, 1));
        itemDetailsPanel.add(new JLabel("Item: " + selectedItem.getName()));
        itemDetailsPanel.add(new JLabel("Gold: -" + selectedItem.getGoldValue()));
        
        if (selectedItem.getFoodValue() > 0) {
            itemDetailsPanel.add(new JLabel("Food: +" + selectedItem.getFoodValue()));
        }
        
        if (selectedItem.getWaterValue() > 0) {
            itemDetailsPanel.add(new JLabel("Water: +" + selectedItem.getWaterValue()));
        }
        
        itemDetailsPanel.add(new JLabel("Remaining gold: " + playerGold));
        
        resultVisualPanel.add(itemImageLabel);
        resultVisualPanel.add(itemDetailsPanel);
        
        resultVisualPanel.revalidate();
        resultVisualPanel.repaint();
    }
    
    /**
     * Handles declining a trade.
     */
    public void declineTrade() {
        if (trader == null || isTraderAggro) return;
        
        rejectionCount++;
        
        if (rejectionCount >= trader.getMaxOffersBeforeDecline()) {
            if (trader.isAggroOnMaxReject()) {
                // Trader becomes aggressive
                isTraderAggro = true;
                dialogueArea.setText(trader.getAggroDialogue());
                dialogueArea.setForeground(Color.RED);
                
                // Apply penalties
                applyTraderPenalties();
                
                // Update UI
                updateUIState();
                populateTraderOffers();
            } else {
                // Trader is annoyed but not aggressive
                dialogueArea.setText("The trader seems annoyed and doesn't want to trade with you anymore.");
            }
        } else {
            // Trader offers another item
            dialogueArea.setText("The trader nods and offers something else.");
        }
    }
    
    /**
     * Applies penalties to player when trader becomes aggressive.
     */
    private void applyTraderPenalties() {
        int strengthPenalty = trader.getStrengthPenalty();
        int waterPenalty = trader.getWaterPenalty();
        int foodPenalty = trader.getFoodPenalty();
        
        // Apply penalties
        playerStrength = Math.max(0, playerStrength - strengthPenalty);
        playerWater = Math.max(0, playerWater - waterPenalty);
        playerFood = Math.max(0, playerFood - foodPenalty);
        
        // Update UI
        strengthSpinner.setValue(playerStrength);
        waterSpinner.setValue(playerWater);
        foodSpinner.setValue(playerFood);
        
        // Show result
        StringBuilder penaltyText = new StringBuilder("The trader attacked! You suffered penalties: ");
        if (strengthPenalty > 0) penaltyText.append("Strength -").append(strengthPenalty).append(" ");
        if (waterPenalty > 0) penaltyText.append("Water -").append(waterPenalty).append(" ");
        if (foodPenalty > 0) penaltyText.append("Food -").append(foodPenalty);
        
        tradeResultLabel.setText(penaltyText.toString());
        
        // Update visual result panel
        JPanel resultVisualPanel = (JPanel) tradeResultPanel.getComponent(1);
        resultVisualPanel.removeAll();
        
        // Add penalty details
        JPanel penaltyPanel = new JPanel(new GridLayout(3, 1));
        penaltyPanel.add(new JLabel("Strength: " + playerStrength + " (-" + strengthPenalty + ")"));
        penaltyPanel.add(new JLabel("Water: " + playerWater + " (-" + waterPenalty + ")"));
        penaltyPanel.add(new JLabel("Food: " + playerFood + " (-" + foodPenalty + ")"));
        
        resultVisualPanel.add(penaltyPanel);
        
        resultVisualPanel.revalidate();
        resultVisualPanel.repaint();
    }
    
    /**
     * Handles attempting to steal from the trader.
     */
    public void attemptSteal() {
        if (trader == null) return;
        
        float stealSuccessRate = trader.getStealSuccessRate();
        boolean success = random.nextFloat() < stealSuccessRate;
        
        if (success) {
            // Successful steal
            List<Item> tradeOffers = trader.getTradeOffers();
            if (tradeOffers != null && !tradeOffers.isEmpty()) {
                Item stolenItem = tradeOffers.get(random.nextInt(tradeOffers.size()));
                
                dialogueArea.setText("You successfully stole from the trader!");
                tradeResultLabel.setText("You stole: " + stolenItem.getName());
                
                // Apply food and water bonuses if applicable
                if (stolenItem.getFoodValue() > 0) {
                    playerFood = Math.min(100, playerFood + stolenItem.getFoodValue());
                    foodSpinner.setValue(playerFood);
                }
                
                if (stolenItem.getWaterValue() > 0) {
                    playerWater = Math.min(100, playerWater + stolenItem.getWaterValue());
                    waterSpinner.setValue(playerWater);
                }
                
                // Update visual result panel
                JPanel resultVisualPanel = (JPanel) tradeResultPanel.getComponent(1);
                resultVisualPanel.removeAll();
                
                // Add item image
                JLabel itemImageLabel = new JLabel();
                if (stolenItem.getImagePath() != null && !stolenItem.getImagePath().isEmpty()) {
                    ImageIcon icon = ImageUtils.loadScaledImageIcon(stolenItem.getImagePath(), 60, 60);
                    itemImageLabel.setIcon(icon);
                }
                
                // Add item details
                JPanel itemDetailsPanel = new JPanel(new GridLayout(0, 1));
                itemDetailsPanel.add(new JLabel("Stolen Item: " + stolenItem.getName()));
                itemDetailsPanel.add(new JLabel("Gold Value: " + stolenItem.getGoldValue()));
                
                if (stolenItem.getFoodValue() > 0) {
                    itemDetailsPanel.add(new JLabel("Food: +" + stolenItem.getFoodValue()));
                }
                
                if (stolenItem.getWaterValue() > 0) {
                    itemDetailsPanel.add(new JLabel("Water: +" + stolenItem.getWaterValue()));
                }
                
                resultVisualPanel.add(itemImageLabel);
                resultVisualPanel.add(itemDetailsPanel);
                
                resultVisualPanel.revalidate();
                resultVisualPanel.repaint();
            } else {
                dialogueArea.setText("You attempted to steal, but the trader had nothing of value.");
            }
        } else {
            // Failed steal - trader becomes aggressive
            isTraderAggro = true;
            dialogueArea.setText("Your attempt to steal failed! The trader is now hostile!");
            dialogueArea.setForeground(Color.RED);
            
            // Apply penalties
            applyTraderPenalties();
            
            // Update UI
            updateUIState();
            populateTraderOffers();
        }
    }
    
    /**
     * Handles leaving the trade interaction.
     */
    public void leaveTrade() {
        if (trader == null) return;
        
        dialogueArea.setText(trader.getLeaveTradeDialogue());
        tradeResultLabel.setText("You left the trade interaction.");
        
        // Update visual result panel
        JPanel resultVisualPanel = (JPanel) tradeResultPanel.getComponent(1);
        resultVisualPanel.removeAll();
        resultVisualPanel.revalidate();
        resultVisualPanel.repaint();
    }
}
