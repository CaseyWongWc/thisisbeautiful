package objectEditor.util;

import objectEditor.model.*;

import java.io.*;
import java.util.*;

/**
 * Utility class for file operations.
 */
public class FileUtils {
    
    /**
     * Saves an Item to a file.
     */
    public static boolean saveItem(Item item, String filePath) {
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            
            Properties props = new Properties();
            props.setProperty("name", item.getName());
            props.setProperty("description", item.getDescription());
            props.setProperty("imagePath", item.getImagePath());
            props.setProperty("foodValue", String.valueOf(item.getFoodValue()));
            props.setProperty("waterValue", String.valueOf(item.getWaterValue()));
            props.setProperty("goldValue", String.valueOf(item.getGoldValue()));

            try (FileOutputStream out = new FileOutputStream(filePath)) {
                props.store(out, "Item: " + item.getName());
            }
            return true;
        } catch (IOException e) {
            ErrorLogger.logError("Failed to save item to " + filePath, e);
            return false;
        }
    }
    
    /**
     * Loads an Item from a file.
     */
    public static Item loadItem(String filePath) {
        try {
            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream(filePath)) {
                props.load(in);
            }

            Item item = new Item();
            item.setName(props.getProperty("name", ""));
            item.setDescription(props.getProperty("description", ""));
            item.setImagePath(props.getProperty("imagePath", ""));
            item.setFoodValue(Integer.parseInt(props.getProperty("foodValue", "0")));
            item.setWaterValue(Integer.parseInt(props.getProperty("waterValue", "0")));
            item.setGoldValue(Integer.parseInt(props.getProperty("goldValue", "0")));
            
            return item;
        } catch (IOException e) {
            ErrorLogger.logError("Failed to load item from " + filePath, e);
            return null;
        } catch (NumberFormatException e) {
            ErrorLogger.logError("Error parsing number values in file: " + filePath, e);
            return null;
        }
    }
    
    /**
     * Saves a Creature to a file.
     */
    public static boolean saveCreature(Creature creature, String filePath) {
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            
            Properties props = new Properties();
            props.setProperty("name", creature.getName());
            props.setProperty("description", creature.getDescription());
            props.setProperty("imagePath", creature.getImagePath());
            props.setProperty("strengthPenalty", String.valueOf(creature.getStrengthPenalty()));
            props.setProperty("waterPenalty", String.valueOf(creature.getWaterPenalty()));
            props.setProperty("goldPenalty", String.valueOf(creature.getGoldPenalty()));
            
            // Save item drop name (if exists)
            if (creature.getItemDrop() != null) {
                props.setProperty("itemDrop", creature.getItemDrop().getName());
            } else {
                // Ensure no old item drop remains in the file
                props.remove("itemDrop");
            }
            
            // Save movement name (if exists)
            if (creature.getMovement() != null) {
                props.setProperty("movement", creature.getMovement().getName());
            } else {
                // Ensure no old movement remains in the file
                props.remove("movement");
            }
            
            // Save difficulties
            props.setProperty("difficulties", creature.getDifficultiesAsString());

            try (FileOutputStream out = new FileOutputStream(filePath)) {
                props.store(out, "Creature: " + creature.getName());
            }
            return true;
        } catch (IOException e) {
            ErrorLogger.logError("Failed to save creature to " + filePath, e);
            return false;
        }
    }
    
    /**
     * Loads a Creature from a file.
     */
    public static Creature loadCreature(String filePath) {
        try {
            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream(filePath)) {
                props.load(in);
            }

            Creature creature = new Creature();
            creature.setName(props.getProperty("name", ""));
            creature.setDescription(props.getProperty("description", ""));
            creature.setImagePath(props.getProperty("imagePath", ""));
            
            try {
                creature.setStrengthPenalty(Integer.parseInt(props.getProperty("strengthPenalty", "0")));
                creature.setWaterPenalty(Integer.parseInt(props.getProperty("waterPenalty", "0")));
                creature.setGoldPenalty(Integer.parseInt(props.getProperty("goldPenalty", "0")));
            } catch (NumberFormatException e) {
                ErrorLogger.logError("Error parsing number values in creature file: " + filePath, e);
            }
            
            // We need to handle loading the item drop separately as the Creature class 
            // expects an Item object, not a string. This is handled in the CreatureEditorPanel
            
            // We need to handle loading the movement separately as the Creature class
            // expects a Movement object, not a string. This is handled in the CreatureEditorPanel
            
            // Load difficulties
            creature.setDifficulties(props.getProperty("difficulties", ""));
            
            return creature;
        } catch (IOException e) {
            ErrorLogger.logError("Failed to load creature from " + filePath, e);
            return null;
        }
    }
    
    /**
     * Saves a Trader to a file.
     */
    public static boolean saveTrader(Trader trader, String filePath) {
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            
            Properties props = new Properties();
            props.setProperty("name", trader.getName());
            props.setProperty("description", trader.getDescription());
            props.setProperty("type", trader.getType());
            props.setProperty("imagePath", trader.getImagePath() != null ? trader.getImagePath() : "");
            
            // Save dialogue options
            props.setProperty("encounterDialogue", trader.getEncounterDialogue());
            props.setProperty("tradeEventDialogue", trader.getTradeEventDialogue());
            props.setProperty("positiveDialogue", trader.getPositiveDialogue());
            props.setProperty("leaveTradeDialogue", trader.getLeaveTradeDialogue());
            props.setProperty("aggroDialogue", trader.getAggroDialogue());
            
            // Save numeric properties
            props.setProperty("maxOffersBeforeDecline", String.valueOf(trader.getMaxOffersBeforeDecline()));
            props.setProperty("maxAggroDuration", String.valueOf(trader.getMaxAggroDuration()));
            props.setProperty("stealSuccessRate", String.valueOf(trader.getStealSuccessRate()));
            props.setProperty("minPlayerResourcePercentage", String.valueOf(trader.getMinPlayerResourcePercentage()));
            props.setProperty("maxPlayerResourcePercentage", String.valueOf(trader.getMaxPlayerResourcePercentage()));
            props.setProperty("strengthPenalty", String.valueOf(trader.getStrengthPenalty()));
            props.setProperty("waterPenalty", String.valueOf(trader.getWaterPenalty()));
            props.setProperty("foodPenalty", String.valueOf(trader.getFoodPenalty()));
            
            // Save boolean properties
            props.setProperty("isAggro", String.valueOf(trader.isAggro()));
            props.setProperty("aggroOnMaxReject", String.valueOf(trader.isAggroOnMaxReject()));
            
            // Save passive movement name (if exists)
            if (trader.getPassiveMovement() != null) {
                props.setProperty("passiveMovement", trader.getPassiveMovement().getName());
            } else {
                props.remove("passiveMovement");
            }
            
            // Save aggro movement name (if exists)
            if (trader.getAggroMovement() != null) {
                props.setProperty("aggroMovement", trader.getAggroMovement().getName());
            } else {
                props.remove("aggroMovement");
            }
            
            // Save trade offers as a comma-separated list of item names
            StringBuilder tradeOffersStr = new StringBuilder();
            List<Item> tradeOffers = trader.getTradeOffers();
            if (tradeOffers != null) {
                for (int i = 0; i < tradeOffers.size(); i++) {
                    if (i > 0) {
                        tradeOffersStr.append(",");
                    }
                    tradeOffersStr.append(tradeOffers.get(i).getName());
                }
                props.setProperty("tradeOffers", tradeOffersStr.toString());
            } else {
                props.setProperty("tradeOffers", "");
            }

            try (FileOutputStream out = new FileOutputStream(filePath)) {
                props.store(out, "Trader: " + trader.getName());
            }
            return true;
        } catch (IOException e) {
            ErrorLogger.logError("Failed to save trader to " + filePath, e);
            return false;
        } catch (Exception e) {
            ErrorLogger.logError("Unexpected error saving trader to " + filePath, e);
            return false;
        }
    }
    
    /**
     * Loads a Trader from a file.
     */
    public static Trader loadTrader(String filePath) {
        try {
            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream(filePath)) {
                props.load(in);
            }

            Trader trader = new Trader();
            trader.setName(props.getProperty("name", ""));
            trader.setDescription(props.getProperty("description", ""));
            trader.setImagePath(props.getProperty("imagePath", ""));
            
            // Load dialogue options
            trader.setEncounterDialogue(props.getProperty("encounterDialogue", ""));
            trader.setTradeEventDialogue(props.getProperty("tradeEventDialogue", ""));
            trader.setPositiveDialogue(props.getProperty("positiveDialogue", ""));
            trader.setLeaveTradeDialogue(props.getProperty("leaveTradeDialogue", ""));
            trader.setAggroDialogue(props.getProperty("aggroDialogue", ""));
            
            // Load numeric properties with appropriate error handling
            try {
                trader.setMaxOffersBeforeDecline(Integer.parseInt(props.getProperty("maxOffersBeforeDecline", "3")));
            } catch (NumberFormatException e) {
                trader.setMaxOffersBeforeDecline(3);
                ErrorLogger.logWarning("Error parsing maxOffersBeforeDecline in file: " + filePath);
            }
            
            try {
                trader.setMaxAggroDuration(Integer.parseInt(props.getProperty("maxAggroDuration", "60")));
            } catch (NumberFormatException e) {
                trader.setMaxAggroDuration(60);
                ErrorLogger.logWarning("Error parsing maxAggroDuration in file: " + filePath);
            }
            
            try {
                trader.setStealSuccessRate(Float.parseFloat(props.getProperty("stealSuccessRate", "0.0")));
            } catch (NumberFormatException e) {
                trader.setStealSuccessRate(0.0f);
                ErrorLogger.logWarning("Error parsing stealSuccessRate in file: " + filePath);
            }
            
            try {
                trader.setMinPlayerResourcePercentage(Double.parseDouble(props.getProperty("minPlayerResourcePercentage", "0.0")));
            } catch (NumberFormatException e) {
                trader.setMinPlayerResourcePercentage(0.0);
                ErrorLogger.logWarning("Error parsing minPlayerResourcePercentage in file: " + filePath);
            }
            
            try {
                trader.setMaxPlayerResourcePercentage(Double.parseDouble(props.getProperty("maxPlayerResourcePercentage", "1.0")));
            } catch (NumberFormatException e) {
                trader.setMaxPlayerResourcePercentage(1.0);
                ErrorLogger.logWarning("Error parsing maxPlayerResourcePercentage in file: " + filePath);
            }
            
            try {
                trader.setStrengthPenalty(Integer.parseInt(props.getProperty("strengthPenalty", "0")));
            } catch (NumberFormatException e) {
                trader.setStrengthPenalty(0);
                ErrorLogger.logWarning("Error parsing strengthPenalty in file: " + filePath);
            }
            
            try {
                trader.setWaterPenalty(Integer.parseInt(props.getProperty("waterPenalty", "0")));
            } catch (NumberFormatException e) {
                trader.setWaterPenalty(0);
                ErrorLogger.logWarning("Error parsing waterPenalty in file: " + filePath);
            }
            
            try {
                trader.setFoodPenalty(Integer.parseInt(props.getProperty("foodPenalty", "0")));
            } catch (NumberFormatException e) {
                trader.setFoodPenalty(0);
                ErrorLogger.logWarning("Error parsing foodPenalty in file: " + filePath);
            }
            
            // Load boolean properties
            trader.setAggro(Boolean.parseBoolean(props.getProperty("isAggro", "false")));
            trader.setAggroOnMaxReject(Boolean.parseBoolean(props.getProperty("aggroOnMaxReject", "false")));
            
            // Trade offers, passive movement, and aggro movement need to be handled separately
            // by the TraderEditorPanel, similar to how CreatureEditorPanel handles item drop
            
            return trader;
        } catch (IOException e) {
            ErrorLogger.logError("Failed to load trader from " + filePath, e);
            return null;
        } catch (Exception e) {
            ErrorLogger.logError("Unexpected error loading trader from " + filePath, e);
            return null;
        }
    }
    
    /**
     * Saves a Movement to a file.
     */
    public static boolean saveMovement(Movement movement, String filePath) {
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            
            Properties props = new Properties();
            props.setProperty("name", movement.getName());
            
            // Join directions with newlines for storage
            StringBuilder directionsStr = new StringBuilder();
            for (String dir : movement.getDirections()) {
                if (directionsStr.length() > 0) {
                    directionsStr.append("\n");
                }
                directionsStr.append(dir);
            }
            props.setProperty("directions", directionsStr.toString());
            
            props.setProperty("repeating", String.valueOf(movement.isRepeating()));
            props.setProperty("random", String.valueOf(movement.isRandom()));
            props.setProperty("reversible", String.valueOf(movement.isReversible()));
            props.setProperty("moveInterval", String.valueOf(movement.getMoveInterval()));
            
            try (FileOutputStream out = new FileOutputStream(filePath)) {
                props.store(out, "Movement: " + movement.getName());
            }
            return true;
        } catch (IOException e) {
            ErrorLogger.logError("Failed to save movement to " + filePath, e);
            return false;
        }
    }
    
    /**
     * Loads a Movement from a file.
     */
    public static Movement loadMovement(String filePath) {
        try {
            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream(filePath)) {
                props.load(in);
            }

            Movement movement = new Movement();
            movement.setName(props.getProperty("name", ""));
            
            // Split directions by newline
            String directionsStr = props.getProperty("directions", "");
            List<String> directions = new ArrayList<>();
            if (directionsStr != null && !directionsStr.isEmpty()) {
                for (String dir : directionsStr.split("\n")) {
                    if (!dir.trim().isEmpty()) {
                        directions.add(dir.trim());
                    }
                }
            }
            movement.setDirections(directions);
            
            // Load boolean properties
            movement.setRepeating(Boolean.parseBoolean(props.getProperty("repeating", "false")));
            movement.setRandom(Boolean.parseBoolean(props.getProperty("random", "false")));
            movement.setReversible(Boolean.parseBoolean(props.getProperty("reversible", "false")));
            
            // Load move interval with error handling
            try {
                movement.setMoveInterval(Integer.parseInt(props.getProperty("moveInterval", "1000")));
            } catch (NumberFormatException e) {
                movement.setMoveInterval(1000);
                ErrorLogger.logWarning("Error parsing moveInterval in file: " + filePath);
            }
            
            return movement;
        } catch (IOException e) {
            ErrorLogger.logError("Failed to load movement from " + filePath, e);
            return null;
        }
    }
    
    /**
     * Saves a Spawner to a file.
     */
    public static boolean saveSpawner(Spawner spawner, String filePath) {
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            
            // Write basic properties
            writer.write("name=" + spawner.getName() + "\n");
            writer.write("description=" + spawner.getDescription() + "\n");
            writer.write("imagePath=" + spawner.getImagePath() + "\n");
            
            // Write spawner-specific properties
            writer.write("maxSpawnCap=" + spawner.getMaxSpawnCap() + "\n");
            writer.write("spawnFrequency=" + spawner.getSpawnFrequency() + "\n");
            writer.write("isDirected=" + spawner.isDirected() + "\n");
            writer.write("direction=" + spawner.getDirection() + "\n");
            writer.write("randomOrientation=" + spawner.isRandomOrientation() + "\n");
            writer.write("objectType=" + spawner.getObjectType() + "\n");
            writer.write("objectTemplate=" + spawner.getObjectTemplate() + "\n");
            
            writer.close();
            return true;
        } catch (IOException e) {
            ErrorLogger.logError("Failed to save spawner to " + filePath, e);
            return false;
        }
    }
    
    /**
     * Loads a Spawner from a file.
     */
    public static Spawner loadSpawner(String filePath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            
            Spawner spawner = new Spawner();
            String line;
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0];
                    String value = parts[1];
                    
                    switch (key) {
                        case "name":
                            spawner.setName(value);
                            break;
                        case "description":
                            spawner.setDescription(value);
                            break;
                        case "imagePath":
                            spawner.setImagePath(value);
                            break;
                        case "maxSpawnCap":
                            spawner.setMaxSpawnCap(Integer.parseInt(value));
                            break;
                        case "spawnFrequency":
                            spawner.setSpawnFrequency(Integer.parseInt(value));
                            break;
                        case "isDirected":
                            spawner.setDirected(Boolean.parseBoolean(value));
                            break;
                        case "direction":
                            spawner.setDirection(value);
                            break;
                        case "randomOrientation":
                            spawner.setRandomOrientation(Boolean.parseBoolean(value));
                            break;
                        case "objectType":
                            spawner.setObjectType(value);
                            break;
                        case "objectTemplate":
                            spawner.setObjectTemplate(value);
                            break;
                    }
                }
            }
            
            reader.close();
            return spawner;
        } catch (IOException e) {
            ErrorLogger.logError("Failed to load spawner from " + filePath, e);
            return null;
        }
    }
}

    /**
     * Saves a Spawner to a file.
     */
    public static boolean saveSpawner(Spawner spawner, String filePath) {
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            
            // Write basic properties
            writer.write("name=" + spawner.getName() + "\n");
            writer.write("description=" + spawner.getDescription() + "\n");
            writer.write("imagePath=" + spawner.getImagePath() + "\n");
            
            // Write spawner-specific properties
            writer.write("maxSpawnCap=" + spawner.getMaxSpawnCap() + "\n");
            writer.write("spawnFrequency=" + spawner.getSpawnFrequency() + "\n");
            writer.write("isDirected=" + spawner.isDirected() + "\n");
            writer.write("direction=" + spawner.getDirection() + "\n");
            writer.write("randomOrientation=" + spawner.isRandomOrientation() + "\n");
            writer.write("objectType=" + spawner.getObjectType() + "\n");
            writer.write("objectTemplate=" + spawner.getObjectTemplate() + "\n");
            
            writer.close();
            return true;
        } catch (IOException e) {
            ErrorLogger.logError("Failed to save spawner to " + filePath, e);
            return false;
        }
    }
    
    /**
     * Loads a Spawner from a file.
     */
    public static Spawner loadSpawner(String filePath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            
            Spawner spawner = new Spawner();
            String line;
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0];
                    String value = parts[1];
                    
                    switch (key) {
                        case "name":
                            spawner.setName(value);
                            break;
                        case "description":
                            spawner.setDescription(value);
                            break;
                        case "imagePath":
                            spawner.setImagePath(value);
                            break;
                        case "maxSpawnCap":
                            spawner.setMaxSpawnCap(Integer.parseInt(value));
                            break;
                        case "spawnFrequency":
                            spawner.setSpawnFrequency(Integer.parseInt(value));
                            break;
                        case "isDirected":
                            spawner.setDirected(Boolean.parseBoolean(value));
                            break;
                        case "direction":
                            spawner.setDirection(value);
                            break;
                        case "randomOrientation":
                            spawner.setRandomOrientation(Boolean.parseBoolean(value));
                            break;
                        case "objectType":
                            spawner.setObjectType(value);
                            break;
                        case "objectTemplate":
                            spawner.setObjectTemplate(value);
                            break;
                    }
                }
            }
            
            reader.close();
            return spawner;
        } catch (IOException e) {
            ErrorLogger.logError("Failed to load spawner from " + filePath, e);
            return null;
        }

    /**
     * Saves a Spawner to a file.
     */
    public static boolean saveSpawner(Spawner spawner, String filePath) {
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            
            // Write basic properties
            writer.write("name=" + spawner.getName() + "\n");
            writer.write("description=" + spawner.getDescription() + "\n");
            writer.write("imagePath=" + spawner.getImagePath() + "\n");
            
            // Write spawner-specific properties
            writer.write("maxSpawnCap=" + spawner.getMaxSpawnCap() + "\n");
            writer.write("spawnFrequency=" + spawner.getSpawnFrequency() + "\n");
            writer.write("isDirected=" + spawner.isDirected() + "\n");
            writer.write("direction=" + spawner.getDirection() + "\n");
            writer.write("randomOrientation=" + spawner.isRandomOrientation() + "\n");
            writer.write("objectType=" + spawner.getObjectType() + "\n");
            writer.write("objectTemplate=" + spawner.getObjectTemplate() + "\n");
            
            writer.close();
            return true;
        } catch (IOException e) {
            ErrorLogger.logError("Failed to save spawner to " + filePath, e);
            return false;
        }
    }
    
    /**
     * Loads a Spawner from a file.
     */
    public static Spawner loadSpawner(String filePath) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filePath));
            
            Spawner spawner = new Spawner();
            String line;
            
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2);
                if (parts.length == 2) {
                    String key = parts[0];
                    String value = parts[1];
                    
                    switch (key) {
                        case "name":
                            spawner.setName(value);
                            break;
                        case "description":
                            spawner.setDescription(value);
                            break;
                        case "imagePath":
                            spawner.setImagePath(value);
                            break;
                        case "maxSpawnCap":
                            try {
                                spawner.setMaxSpawnCap(Integer.parseInt(value));
                            } catch (NumberFormatException e) { }
                            break;
                        case "spawnFrequency":
                            try {
                                spawner.setSpawnFrequency(Integer.parseInt(value));
                            } catch (NumberFormatException e) { }
                            break;
                        case "isDirected":
                            spawner.setDirected(Boolean.parseBoolean(value));
                            break;
                        case "direction":
                            spawner.setDirection(value);
                            break;
                        case "randomOrientation":
                            spawner.setRandomOrientation(Boolean.parseBoolean(value));
                            break;
                        case "objectType":
                            spawner.setObjectType(value);
                            break;
                        case "objectTemplate":
                            spawner.setObjectTemplate(value);
                            break;
                    }
                }
            }
            
            reader.close();
            return spawner;
        } catch (IOException e) {
            ErrorLogger.logError("Failed to load spawner from " + filePath, e);
            return null;
        }
    }
    
    /**
     * Saves a Spawner to a file.
     */
    public static boolean saveSpawner(Spawner spawner, String filePath) {
        try {
            File file = new File(filePath);
            file.getParentFile().mkdirs();
            
            Properties props = new Properties();
            props.setProperty("name", spawner.getName());
            props.setProperty("description", spawner.getDescription());
            props.setProperty("imagePath", spawner.getImagePath() != null ? spawner.getImagePath() : "");
            
            // Write spawner-specific properties
            props.setProperty("maxSpawnCap", String.valueOf(spawner.getMaxSpawnCap()));
            props.setProperty("spawnFrequency", String.valueOf(spawner.getSpawnFrequency()));
            props.setProperty("isDirected", String.valueOf(spawner.isDirected()));
            props.setProperty("direction", spawner.getDirection());
            props.setProperty("randomOrientation", String.valueOf(spawner.isRandomOrientation()));
            props.setProperty("objectType", spawner.getObjectType());
            props.setProperty("objectTemplate", spawner.getObjectTemplate());
            
            try (FileOutputStream out = new FileOutputStream(filePath)) {
                props.store(out, "Spawner: " + spawner.getName());
            }
            return true;
        } catch (IOException e) {
            ErrorLogger.logError("Failed to save spawner to " + filePath, e);
            return false;
        }
    }
    
    /**
     * Loads a Spawner from a file.
     */
    public static Spawner loadSpawner(String filePath) {
        try {
            Properties props = new Properties();
            try (FileInputStream in = new FileInputStream(filePath)) {
                props.load(in);
            }

            Spawner spawner = new Spawner();
            spawner.setName(props.getProperty("name", ""));
            spawner.setDescription(props.getProperty("description", ""));
            spawner.setImagePath(props.getProperty("imagePath", ""));
            
            // Load spawner-specific properties
            try {
                spawner.setMaxSpawnCap(Integer.parseInt(props.getProperty("maxSpawnCap", "1")));
            } catch (NumberFormatException e) {
                spawner.setMaxSpawnCap(1);
                ErrorLogger.logWarning("Error parsing maxSpawnCap in file: " + filePath);
            }
            
            try {
                spawner.setSpawnFrequency(Integer.parseInt(props.getProperty("spawnFrequency", "1")));
            } catch (NumberFormatException e) {
                spawner.setSpawnFrequency(1);
                ErrorLogger.logWarning("Error parsing spawnFrequency in file: " + filePath);
            }
            
            spawner.setDirected(Boolean.parseBoolean(props.getProperty("isDirected", "false")));
            spawner.setDirection(props.getProperty("direction", "none"));
            spawner.setRandomOrientation(Boolean.parseBoolean(props.getProperty("randomOrientation", "false")));
            spawner.setObjectType(props.getProperty("objectType", "item"));
            spawner.setObjectTemplate(props.getProperty("objectTemplate", ""));
            
            return spawner;
        } catch (IOException e) {
            ErrorLogger.logError("Failed to load spawner from " + filePath, e);
            return null;
        }
    }
}
