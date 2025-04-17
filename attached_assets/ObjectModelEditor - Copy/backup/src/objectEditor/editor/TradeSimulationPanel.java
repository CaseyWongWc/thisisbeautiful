package objectEditor.editor;

import objectEditor.model.Item;
import objectEditor.model.Trader;
import objectEditor.util.ImageUtils;
import objectEditor.util.FileUtils;

import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import java.awt.Cursor;
import java.awt.Component;
import java.awt.event.MouseEvent;
import java.awt.event.MouseAdapter;
import java.awt.Color;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.util.Random;

/**
 * Panel for simulating trading with a trader
 */
public class TradeSimulationPanel extends JPanel {
    
    // Panels
    private JPanel tradeOptionsPanel;
    private JPanel tradeOfferPanel;
    private JPanel tradeNavPanel; // Panel for trade navigation buttons
    private JPanel tradeOfferButtonsPanel;
    private JPanel currentTradeItemPanel; // Panel to display the current trade item
    
    // Trade action buttons
    private JButton acceptButton;
    private JButton nextItemButton;
    private JButton declineButton;
    private JButton stealButton;
    private JButton leaveButton;
    
    // Trade display components
    private JTextArea tradeLogArea;
    private JLabel playerGoldValueLabel;
    private JLabel playerWaterValueLabel;
    private JLabel playerFoodValueLabel;
    private JLabel playerStrengthValueLabel;
    private JLabel traderImageLabel;
    private JLabel traderName;
    private JLabel traderMessage;
    
    // Player statistics
    private int playerGold = 100;
    private int playerWater = 50;
    private int playerFood = 50;
    private int playerStrength = 10;
    private int DEFAULT_PLAYER_GOLD = 100;
    private int DEFAULT_PLAYER_WATER = 50;
    private int DEFAULT_PLAYER_FOOD = 50;
    private int DEFAULT_PLAYER_STRENGTH = 10;
    
    // Spinners for adjusting player values
    private JSpinner goldSpinner;
    private JSpinner waterSpinner;
    private JSpinner foodSpinner;
    private JSpinner strengthSpinner;
    
    // Trade simulation state
    private Trader currentTrader;
    private Item selectedTradeItem; // Currently selected trade item
    private ArrayList<Item> allItems = new ArrayList<>();
    private Random random = new Random();
    
    public TradeSimulationPanel() {
        setLayout(new BorderLayout());
        
        initComponents();
        setUpEventHandlers();
        loadAllItems();
    }
    
    private void initComponents() {
        // Set up the main layout
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Create the left panel for trader info and player stats
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(300, 500));
        
        // Create the current trade item panel
        currentTradeItemPanel = new JPanel(new BorderLayout());
        currentTradeItemPanel.setBorder(BorderFactory.createTitledBorder("Current Item"));
        currentTradeItemPanel.setBackground(new Color(245, 245, 240));
        currentTradeItemPanel.setVisible(false);  // Initially hidden
        leftPanel.add(currentTradeItemPanel, BorderLayout.NORTH);
        
        // Create the trader info panel
        JPanel traderInfoPanel = new JPanel(new BorderLayout());
        traderInfoPanel.setBorder(BorderFactory.createTitledBorder("Trader"));
        
        traderImageLabel = new JLabel();
        traderImageLabel.setHorizontalAlignment(SwingConstants.CENTER);
        traderInfoPanel.add(traderImageLabel, BorderLayout.CENTER);
        
        traderName = new JLabel("No trader selected");
        traderName.setHorizontalAlignment(SwingConstants.CENTER);
        traderName.setFont(traderName.getFont().deriveFont(Font.BOLD, 14f));
        traderInfoPanel.add(traderName, BorderLayout.NORTH);
        
        traderMessage = new JLabel("Select a trader to begin trading");
        traderMessage.setHorizontalAlignment(SwingConstants.CENTER);
        traderInfoPanel.add(traderMessage, BorderLayout.SOUTH);
        
        leftPanel.add(traderInfoPanel, BorderLayout.CENTER);
        
        // Create player stats panel
        JPanel playerStatsPanel = new JPanel(new GridBagLayout());
        playerStatsPanel.setBorder(BorderFactory.createTitledBorder("Player Stats"));
        
        // Use GridBagLayout for player stats
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(2, 5, 2, 5);
        gbc.anchor = GridBagConstraints.WEST;
        
        // Gold
        gbc.gridx = 0;
        gbc.gridy = 0;
        playerStatsPanel.add(new JLabel("Gold:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        playerGoldValueLabel = new JLabel(String.valueOf(playerGold));
        playerStatsPanel.add(playerGoldValueLabel, gbc);
        
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        goldSpinner = new JSpinner(new SpinnerNumberModel(playerGold, 0, 1000, 10));
        goldSpinner.addChangeListener(e -> {
            playerGold = (Integer) goldSpinner.getValue();
            playerGoldValueLabel.setText(String.valueOf(playerGold));
        });
        playerStatsPanel.add(goldSpinner, gbc);
        
        // Water
        gbc.gridx = 0;
        gbc.gridy = 1;
        playerStatsPanel.add(new JLabel("Water:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        playerWaterValueLabel = new JLabel(String.valueOf(playerWater));
        playerStatsPanel.add(playerWaterValueLabel, gbc);
        
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        waterSpinner = new JSpinner(new SpinnerNumberModel(playerWater, 0, 100, 5));
        waterSpinner.addChangeListener(e -> {
            playerWater = (Integer) waterSpinner.getValue();
            playerWaterValueLabel.setText(String.valueOf(playerWater));
        });
        playerStatsPanel.add(waterSpinner, gbc);
        
        // Food
        gbc.gridx = 0;
        gbc.gridy = 2;
        playerStatsPanel.add(new JLabel("Food:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        playerFoodValueLabel = new JLabel(String.valueOf(playerFood));
        playerStatsPanel.add(playerFoodValueLabel, gbc);
        
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        foodSpinner = new JSpinner(new SpinnerNumberModel(playerFood, 0, 100, 5));
        foodSpinner.addChangeListener(e -> {
            playerFood = (Integer) foodSpinner.getValue();
            playerFoodValueLabel.setText(String.valueOf(playerFood));
        });
        playerStatsPanel.add(foodSpinner, gbc);
        
        // Strength
        gbc.gridx = 0;
        gbc.gridy = 3;
        playerStatsPanel.add(new JLabel("Strength:"), gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1.0;
        playerStrengthValueLabel = new JLabel(String.valueOf(playerStrength));
        playerStatsPanel.add(playerStrengthValueLabel, gbc);
        
        gbc.gridx = 2;
        gbc.weightx = 0.0;
        strengthSpinner = new JSpinner(new SpinnerNumberModel(playerStrength, 1, 100, 1));
        strengthSpinner.addChangeListener(e -> {
            playerStrength = (Integer) strengthSpinner.getValue();
            playerStrengthValueLabel.setText(String.valueOf(playerStrength));
        });
        playerStatsPanel.add(strengthSpinner, gbc);
        
        // Reset button
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 3;
        JButton resetStatsButton = new JButton("Reset Stats");
        resetStatsButton.addActionListener(e -> resetPlayerStats());
        playerStatsPanel.add(resetStatsButton, gbc);
        
        leftPanel.add(playerStatsPanel, BorderLayout.SOUTH);
        
        // Create the right panel for trade offers and log
        JPanel rightPanel = new JPanel(new BorderLayout());
        
        // Create the trade offers panel (to be filled with trader items)
        tradeOfferPanel = new JPanel();
        tradeOfferPanel.setBorder(BorderFactory.createTitledBorder("Trade Offers"));
        tradeOfferPanel.setLayout(new BoxLayout(tradeOfferPanel, BoxLayout.Y_AXIS));
        
        // Create a container that includes trade offers and navigation
        JPanel tradeOffersContainer = new JPanel(new BorderLayout());
        tradeOffersContainer.add(tradeOfferPanel, BorderLayout.CENTER);
        
        // Create navigation panel and next item button
        tradeNavPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        nextItemButton = new JButton("Next Item");
        nextItemButton.addActionListener(e -> cycleToNextTradeItem());
        tradeNavPanel.add(nextItemButton);
        tradeOffersContainer.add(tradeNavPanel, BorderLayout.SOUTH);
        
        JScrollPane tradeOffersScrollPane = new JScrollPane(tradeOffersContainer);
        tradeOffersScrollPane.setPreferredSize(new Dimension(400, 250));
        
        // Create trade log panel
        JPanel tradeLogPanel = new JPanel(new BorderLayout());
        tradeLogPanel.setBorder(BorderFactory.createTitledBorder("Trade Log"));
        
        tradeLogArea = new JTextArea(10, 40);
        tradeLogArea.setEditable(false);
        tradeLogArea.setLineWrap(true);
        tradeLogArea.setWrapStyleWord(true);
        JScrollPane logScrollPane = new JScrollPane(tradeLogArea);
        
        tradeLogPanel.add(logScrollPane, BorderLayout.CENTER);
        
        // Create button panel for trade actions
        tradeOptionsPanel = new JPanel(new FlowLayout());
        
        acceptButton = new JButton("Accept Trade");
        declineButton = new JButton("Decline Trade");
        stealButton = new JButton("Attempt Theft");
        leaveButton = new JButton("Leave Trade");
        
        // Add buttons to panel
        tradeOptionsPanel.add(acceptButton);
        tradeOptionsPanel.add(declineButton);
        tradeOptionsPanel.add(stealButton);
        tradeOptionsPanel.add(leaveButton);
        
        // Assemble right panel
        rightPanel.add(tradeOffersScrollPane, BorderLayout.NORTH);
        rightPanel.add(tradeLogPanel, BorderLayout.CENTER);
        rightPanel.add(tradeOptionsPanel, BorderLayout.SOUTH);
        
        // Add left and right panels to main panel
        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);
        
        // Set action buttons disabled initially
        setActionButtonsEnabled(false);
    }
    
    private void setUpEventHandlers() {
        acceptButton.addActionListener(e -> acceptTrade());
        nextItemButton.addActionListener(e -> cycleToNextTradeItem());
        declineButton.addActionListener(e -> declineTrade());
        stealButton.addActionListener(e -> attemptTheft());
        leaveButton.addActionListener(e -> leaveTrade());
    }
    
    /**
     * Set action buttons enabled/disabled
     */
    private void setActionButtonsEnabled(boolean enabled) {
        acceptButton.setEnabled(enabled);
        declineButton.setEnabled(enabled);
        stealButton.setEnabled(enabled);
        leaveButton.setEnabled(enabled);
        nextItemButton.setEnabled(enabled);
    }
    
    /**
     * Reset player stats to default values
     */
    private void resetPlayerStats() {
        playerGold = DEFAULT_PLAYER_GOLD;
        playerWater = DEFAULT_PLAYER_WATER;
        playerFood = DEFAULT_PLAYER_FOOD;
        playerStrength = DEFAULT_PLAYER_STRENGTH;
        
        goldSpinner.setValue(playerGold);
        waterSpinner.setValue(playerWater);
        foodSpinner.setValue(playerFood);
        strengthSpinner.setValue(playerStrength);
        
        playerGoldValueLabel.setText(String.valueOf(playerGold));
        playerWaterValueLabel.setText(String.valueOf(playerWater));
        playerFoodValueLabel.setText(String.valueOf(playerFood));
        playerStrengthValueLabel.setText(String.valueOf(playerStrength));
        
        tradeLogArea.append("Player stats reset to default values.\n");
    }
    
    /**
     * Set the current trader to display
     * @param trader The trader to display
     */
    public void setTrader(Trader trader) {
        this.currentTrader = trader;
        
        // Clear previous trade items
        clearTrader();
        
        if (trader != null) {
            traderName.setText(trader.getName());
            traderMessage.setText(trader.getEncounterDialogue());
            
            // Load trader image
            String imagePath = trader.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                ImageIcon icon = ImageUtils.createScaledImageIcon(imagePath, 128, 128);
                traderImageLabel.setIcon(icon);
            } else {
                traderImageLabel.setIcon(null);
                traderImageLabel.setText("No image");
            }
            
            // Add trade items
            displayTradeOffers(trader);
            
            // Enable trade actions
            setActionButtonsEnabled(true);
            
            // Add initial encounter message (log already cleared by clearTrader)
            // No need to clear again, already done in clearTrader()
            tradeLogArea.append("You approach " + trader.getName() + ".\n");
            tradeLogArea.append(trader.getName() + " says: \"" + trader.getEncounterDialogue() + "\"\n");
        }
    }
    
    /**
     * Clear the current trader display
     */
    private void clearTrader() {
        // Clear trader info
        traderName.setText("No trader selected");
        traderMessage.setText("Select a trader to begin trading");
        traderImageLabel.setIcon(null);
        
        // Clear trade offers
        tradeOfferPanel.removeAll();
        
        // Reset trade item selection
        updateCurrentTradeItemDisplay(null);
        
        // Disable trade actions
        setActionButtonsEnabled(false);
        // Clear the trade log
        // No need to clear again, already done in clearTrader()
        
        // Clear the trade log
        // No need to clear again, already done in clearTrader()
    }
    
    /**
     * Display the trade offers for a trader
     * @param trader The trader whose offers to display
     */
    private void displayTradeOffers(Trader trader) {
        if (trader == null) return;
        
        List<Item> tradeOffers = trader.getTradeOffers();
        tradeOfferPanel.removeAll();
        
        if (tradeOffers != null && !tradeOffers.isEmpty()) {
            for (Item item : tradeOffers) {
                // Create a panel for each item
                JPanel itemPanel = new JPanel(new BorderLayout());
                itemPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                    BorderFactory.createEmptyBorder(5, 5, 5, 5)));
                
                // Left - Item image
                JLabel imageLabel = new JLabel();
                imageLabel.setPreferredSize(new Dimension(40, 40));
                if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
                    ImageIcon icon = ImageUtils.createScaledImageIcon(item.getImagePath(), 32, 32);
                    imageLabel.setIcon(icon);
                } else {
                    imageLabel.setText("No Image");
                }
                
                // Center - Item name and cost
                JPanel itemInfoPanel = new JPanel(new BorderLayout());
                
                JLabel nameLabel = new JLabel(item.getName());
                nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
                
                JLabel costLabel = new JLabel("Cost: " + item.getGoldValue() + " gold");
                
                itemInfoPanel.add(nameLabel, BorderLayout.NORTH);
                itemInfoPanel.add(costLabel, BorderLayout.CENTER);
                
                // Assemble item panel
                itemPanel.add(imageLabel, BorderLayout.WEST);
                itemPanel.add(itemInfoPanel, BorderLayout.CENTER);
                
                tradeOfferPanel.add(itemPanel);
                tradeOfferPanel.add(Box.createVerticalStrut(5));
            }
        } else {
            tradeOfferPanel.add(new JLabel("This trader has no items to trade."));
        }
        
        tradeOfferPanel.revalidate();
        
        // Select the first item for trading if available
        if (tradeOffers != null && !tradeOffers.isEmpty()) {
            updateCurrentTradeItemDisplay(tradeOffers.get(0));
        }
        
        tradeOfferPanel.repaint();
    }
    
    /**
     * Accept the current trade offer
     */
    private void acceptTrade() {
        if (currentTrader == null) return;
        
        List<Item> tradeOffers = currentTrader.getTradeOffers();
        if (tradeOffers == null || tradeOffers.isEmpty()) {
            tradeLogArea.append("This trader has no items to trade.\n");
            return;
        }
        
        // Use the currently selected item or fall back to the first one
        Item selectedItem = (selectedTradeItem != null) ? selectedTradeItem : tradeOffers.get(0);
        
        // Check if player has enough gold
        if (playerGold < selectedItem.getGoldValue()) {
            tradeLogArea.append("You don't have enough gold to buy this item.\n");
            return;
        }
        
        // Process the trade
        playerGold -= selectedItem.getGoldValue();
        goldSpinner.setValue(playerGold);
        
        // Apply item benefits
        if (selectedItem.getWaterValue() > 0) {
            int newWater = Math.min(playerWater + selectedItem.getWaterValue(), 100);
            playerWater = newWater;
            waterSpinner.setValue(playerWater);
        }
        
        if (selectedItem.getFoodValue() > 0) {
            int newFood = Math.min(playerFood + selectedItem.getFoodValue(), 100);
            playerFood = newFood;
            foodSpinner.setValue(playerFood);
        }
        
        // Log the trade
        tradeLogArea.append("You purchased " + selectedItem.getName() + " for " + selectedItem.getGoldValue() + " gold.\n");
        if (selectedItem.getWaterValue() > 0) {
            tradeLogArea.append("Water increased by " + selectedItem.getWaterValue() + ".\n");
        }
        if (selectedItem.getFoodValue() > 0) {
            tradeLogArea.append("Food increased by " + selectedItem.getFoodValue() + ".\n");
        }
        
        // Trader response
        tradeLogArea.append(currentTrader.getName() + " says: \"" + currentTrader.getPositiveDialogue() + "\"\n");
    }
    
    /**
     * Decline the current trade offer
     */
    private void declineTrade() {
        if (currentTrader == null) return;
        
        tradeLogArea.append("You decline the offer.\n");
        tradeLogArea.append(currentTrader.getName() + " says: \"" + currentTrader.getTradeEventDialogue() + "\"\n");
    }
    
    /**
     * Attempt to steal from the trader
     */
    private void attemptTheft() {
        if (currentTrader == null) return;
        
        List<Item> tradeOffers = currentTrader.getTradeOffers();
        if (tradeOffers == null || tradeOffers.isEmpty()) {
            tradeLogArea.append("This trader has no items to steal.\n");
            return;
        }
        
        // Determine success chance based on player strength and trader perception
        int successChance = Math.min(playerStrength * 2, 75);  // Cap at 75%
        int roll = random.nextInt(100);
        
        if (roll < successChance) {
            // Success - get a random item from the trader
            int itemIndex = random.nextInt(tradeOffers.size());
            Item stolenItem = tradeOffers.get(itemIndex);
            
            // Apply item benefits
            if (stolenItem.getWaterValue() > 0) {
                int newWater = Math.min(playerWater + stolenItem.getWaterValue(), 100);
                playerWater = newWater;
                waterSpinner.setValue(playerWater);
            }
            
            if (stolenItem.getFoodValue() > 0) {
                int newFood = Math.min(playerFood + stolenItem.getFoodValue(), 100);
                playerFood = newFood;
                foodSpinner.setValue(playerFood);
            }
            
            // Log the theft
            tradeLogArea.append("You successfully steal " + stolenItem.getName() + "!\n");
            if (stolenItem.getWaterValue() > 0) {
                tradeLogArea.append("Water increased by " + stolenItem.getWaterValue() + ".\n");
            }
            if (stolenItem.getFoodValue() > 0) {
                tradeLogArea.append("Food increased by " + stolenItem.getFoodValue() + ".\n");
            }
        } else {
            // Failure - lose gold as a penalty
            int penalty = 10 + random.nextInt(20);
            playerGold = Math.max(0, playerGold - penalty);
            goldSpinner.setValue(playerGold);
            
            tradeLogArea.append("You failed to steal anything and got caught!\n");
            tradeLogArea.append("You pay " + penalty + " gold as a penalty.\n");
            tradeLogArea.append(currentTrader.getName() + " says: \"" + currentTrader.getAggroDialogue() + "\"\n");
        }
    }
    
    /**
     * Leave the current trade interaction
     */
    private void leaveTrade() {
        if (currentTrader == null) return;
        
        tradeLogArea.append("You leave the trade with " + currentTrader.getName() + ".\n");
        
        // Clear the trader
        clearTrader();
    }
    
    /**
     * Load all items from files
     */
    private void loadAllItems() {
        try {
            File itemDir = new File("data/item");
            if (itemDir.exists() && itemDir.isDirectory()) {
                File[] itemFiles = itemDir.listFiles((dir, name) -> name.endsWith(".properties"));
                if (itemFiles != null) {
                    for (File file : itemFiles) {
                        Item item = FileUtils.loadItem(file.getPath());
                        if (item != null) {
                            allItems.add(item);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Updates the current trade item display to show the selected item
     * @param item The item to display, or null to clear the display
     */
    private void updateCurrentTradeItemDisplay(Item item) {
        selectedTradeItem = item;
        
        // Clear the current panel
        currentTradeItemPanel.removeAll();
        
        if (item == null) {
            currentTradeItemPanel.setVisible(false);
            return;
        }
        
        currentTradeItemPanel.setVisible(true);
        
        // Create a compact item display panel
        JPanel itemInfoPanel = new JPanel(new BorderLayout(5, 0));
        
        // Left - Item image
        JLabel imageLabel = new JLabel();
        imageLabel.setPreferredSize(new Dimension(48, 48));
        if (item.getImagePath() != null && !item.getImagePath().isEmpty()) {
            ImageIcon icon = ImageUtils.createScaledImageIcon(item.getImagePath(), 32, 32);
            imageLabel.setIcon(icon);
        } else {
            imageLabel.setText("No Image");
        }
        itemInfoPanel.add(imageLabel, BorderLayout.WEST);
        
        // Center - Item details
        JPanel detailsPanel = new JPanel(new BorderLayout());
        detailsPanel.setOpaque(false);
        
        JLabel nameLabel = new JLabel(item.getName());
        nameLabel.setFont(nameLabel.getFont().deriveFont(Font.BOLD));
        detailsPanel.add(nameLabel, BorderLayout.NORTH);
        
        // Item costs and benefits
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
        statsPanel.setOpaque(false);
        
        // Gold cost
        JLabel costLabel = new JLabel("Cost: " + item.getGoldValue() + " gold");
        statsPanel.add(costLabel);
        
        // Add spacing
        statsPanel.add(Box.createHorizontalStrut(5));
        
        // Benefits
        StringBuilder benefitsText = new StringBuilder("Benefits: ");
        boolean hasBenefits = false;
        
        if (item.getWaterValue() > 0) {
            benefitsText.append("+").append(item.getWaterValue()).append(" water");
            hasBenefits = true;
        }
        
        if (item.getFoodValue() > 0) {
            if (hasBenefits) benefitsText.append(", ");
            benefitsText.append("+").append(item.getFoodValue()).append(" food");
            hasBenefits = true;
        }
        
        if (!hasBenefits) {
            benefitsText.append("none");
        }
        
        JLabel benefitsLabel = new JLabel(benefitsText.toString());
        statsPanel.add(benefitsLabel);
        
        detailsPanel.add(statsPanel, BorderLayout.CENTER);
        
        itemInfoPanel.add(detailsPanel, BorderLayout.CENTER);
        
        currentTradeItemPanel.add(itemInfoPanel, BorderLayout.CENTER);
        currentTradeItemPanel.revalidate();
        currentTradeItemPanel.repaint();
    }
    
    /**
     * Cycles to the next trade item in the trader's inventory
     */
    private void cycleToNextTradeItem() {
        if (currentTrader == null) return;
        
        List<Item> tradeOffers = currentTrader.getTradeOffers();
        if (tradeOffers == null || tradeOffers.isEmpty()) return;
        
        // If no item is selected, select the first one
        if (selectedTradeItem == null) {
            updateCurrentTradeItemDisplay(tradeOffers.get(0));
            return;
        }
        
        // Find the current item index
        int currentIndex = tradeOffers.indexOf(selectedTradeItem);
        if (currentIndex == -1) {
            // Item not found, start from the beginning
            updateCurrentTradeItemDisplay(tradeOffers.get(0));
            return;
        }
        
        // Select the next item in the list
        int nextIndex = (currentIndex + 1) % tradeOffers.size();
        updateCurrentTradeItemDisplay(tradeOffers.get(nextIndex));
    }
}
