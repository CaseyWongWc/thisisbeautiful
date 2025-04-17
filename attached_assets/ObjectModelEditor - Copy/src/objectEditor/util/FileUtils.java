package objectEditor.util;

import objectEditor.model.Creature;
import objectEditor.model.Item;
import objectEditor.model.Terrain;
import objectEditor.model.ObjectInstance;
import objectEditor.model.MapCell;
import objectEditor.model.GameMap;
import objectEditor.model.Movement;
import objectEditor.model.Spawner;
import objectEditor.model.Trader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Utility class for file operations. Simplified version to allow compilation.
 */
public class FileUtils {
    private static final String DATA_DIR = "data";
    private static final String EXPORTS_DIR = "exports";
    
    // Simplified static maps to keep track of loaded objects
    private static Map<String, Object> lastSavedObjects = new HashMap<>();
    private static Map<String, Item> itemCache = new HashMap<>();
    private static Map<String, Creature> creatureCache = new HashMap<>();
    private static Map<String, Movement> movementCache = new HashMap<>();
    private static Map<String, Spawner> spawnerCache = new HashMap<>();
    private static Map<String, Trader> traderCache = new HashMap<>();
    private static Map<String, Terrain> terrainCache = new HashMap<>();
    private static Map<String, GameMap> mapCache = new HashMap<>();
    
    /**
     * Logs an error message.
     */
    private static void logError(String message, Exception e) {
        System.err.println("ERROR: " + message);
        if (e != null) {
            e.printStackTrace();
        }
    }
    
    /**
     * Logs a warning message.
     */
    private static void logWarning(String message) {
        System.out.println("WARNING: " + message);
    }

    /**
     * Saves an item to a file.
     */
    public static boolean saveItem(Item item, String filePath) {
        try {
            // Ensure the directory exists
            File directory = new File(filePath).getParentFile();
            if (directory != null && !directory.exists()) {
                directory.mkdirs();
            }
            
            Properties props = new Properties();
            props.setProperty("name", item.getName());
            props.setProperty("type", item.getType());
            props.setProperty("description", item.getDescription());
            props.setProperty("imagePath", item.getImagePath() != null ? item.getImagePath() : "");
            
            props.store(new FileOutputStream(filePath), "Item: " + item.getName());
            
            // Update cache
            itemCache.put(item.getName(), item);
            lastSavedObjects.put(filePath, item.hashCode());
            
            return true;
        } catch (IOException e) {
            logError("Error saving item to " + filePath, e);
            return false;
        }
    }
    
    /**
     * Loads an item from a file.
     */
    public static Item loadItem(String filePath) {
        // First check the cache
        String itemName = getNameFromFilePath(filePath);
        if (itemName != null && itemCache.containsKey(itemName)) {
            return itemCache.get(itemName);
        }
        
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(filePath));
            
            Item item = new Item();
            item.setName(props.getProperty("name"));
            item.setType(props.getProperty("type"));
            item.setDescription(props.getProperty("description"));
            item.setImagePath(props.getProperty("imagePath"));
            
            // Update cache
            itemCache.put(item.getName(), item);
            
            return item;
        } catch (IOException e) {
            logError("Error loading item from " + filePath, e);
            return null;
        }
    }
    
    /**
     * Saves a creature to a file.
     */
    public static boolean saveCreature(Creature creature, String filePath) {
        try {
            // Ensure the directory exists
            File directory = new File(filePath).getParentFile();
            if (directory != null && !directory.exists()) {
                directory.mkdirs();
            }
            
            Properties props = new Properties();
            props.setProperty("name", creature.getName());
            props.setProperty("type", creature.getType());
            props.setProperty("description", creature.getDescription());
            props.setProperty("imagePath", creature.getImagePath() != null ? creature.getImagePath() : "");
            
            props.store(new FileOutputStream(filePath), "Creature: " + creature.getName());
            
            // Update cache
            creatureCache.put(creature.getName(), creature);
            lastSavedObjects.put(filePath, creature.hashCode());
            
            return true;
        } catch (IOException e) {
            logError("Error saving creature to " + filePath, e);
            return false;
        }
    }
    
    /**
     * Loads a creature from a file.
     */
    public static Creature loadCreature(String filePath) {
        // First check the cache
        String creatureName = getNameFromFilePath(filePath);
        if (creatureName != null && creatureCache.containsKey(creatureName)) {
            return creatureCache.get(creatureName);
        }
        
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(filePath));
            
            Creature creature = new Creature();
            creature.setName(props.getProperty("name"));
            creature.setType(props.getProperty("type"));
            creature.setDescription(props.getProperty("description"));
            creature.setImagePath(props.getProperty("imagePath"));
            
            // Update cache
            creatureCache.put(creature.getName(), creature);
            
            return creature;
        } catch (IOException e) {
            logError("Error loading creature from " + filePath, e);
            return null;
        }
    }
    
    /**
     * Saves a movement pattern to a file.
     */
    public static boolean saveMovement(Movement movement, String filePath) {
        try {
            // Ensure the directory exists
            File directory = new File(filePath).getParentFile();
            if (directory != null && !directory.exists()) {
                directory.mkdirs();
            }
            
            Properties props = new Properties();
            props.setProperty("name", movement.getName());
            props.setProperty("type", movement.getType());
            props.setProperty("description", movement.getDescription());
            
            props.store(new FileOutputStream(filePath), "Movement: " + movement.getName());
            
            // Update cache
            movementCache.put(movement.getName(), movement);
            lastSavedObjects.put(filePath, movement.hashCode());
            
            return true;
        } catch (IOException e) {
            logError("Error saving movement to " + filePath, e);
            return false;
        }
    }
    
    /**
     * Loads a movement pattern from a file.
     */
    public static Movement loadMovement(String filePath) {
        // First check the cache
        String movementName = getNameFromFilePath(filePath);
        if (movementName != null && movementCache.containsKey(movementName)) {
            return movementCache.get(movementName);
        }
        
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(filePath));
            
            Movement movement = new Movement();
            movement.setName(props.getProperty("name"));
            movement.setType(props.getProperty("type"));
            movement.setDescription(props.getProperty("description"));
            
            // Update cache
            movementCache.put(movement.getName(), movement);
            
            return movement;
        } catch (IOException e) {
            logError("Error loading movement from " + filePath, e);
            return null;
        }
    }
    
    /**
     * Saves a spawner to a file.
     */
    public static boolean saveSpawner(Spawner spawner, String filePath) {
        try {
            // Ensure the directory exists
            File directory = new File(filePath).getParentFile();
            if (directory != null && !directory.exists()) {
                directory.mkdirs();
            }
            
            Properties props = new Properties();
            props.setProperty("name", spawner.getName());
            props.setProperty("type", spawner.getType());
            props.setProperty("description", spawner.getDescription());
            
            props.store(new FileOutputStream(filePath), "Spawner: " + spawner.getName());
            
            // Update cache
            spawnerCache.put(spawner.getName(), spawner);
            lastSavedObjects.put(filePath, spawner.hashCode());
            
            return true;
        } catch (IOException e) {
            logError("Error saving spawner to " + filePath, e);
            return false;
        }
    }
    
    /**
     * Loads a spawner from a file.
     */
    public static Spawner loadSpawner(String filePath) {
        // First check the cache
        String spawnerName = getNameFromFilePath(filePath);
        if (spawnerName != null && spawnerCache.containsKey(spawnerName)) {
            return spawnerCache.get(spawnerName);
        }
        
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(filePath));
            
            Spawner spawner = new Spawner();
            spawner.setName(props.getProperty("name"));
            spawner.setType(props.getProperty("type"));
            spawner.setDescription(props.getProperty("description"));
            
            // Update cache
            spawnerCache.put(spawner.getName(), spawner);
            
            return spawner;
        } catch (IOException e) {
            logError("Error loading spawner from " + filePath, e);
            return null;
        }
    }
    
    /**
     * Saves a trader to a file.
     */
    public static boolean saveTrader(Trader trader, String filePath) {
        try {
            // Ensure the directory exists
            File directory = new File(filePath).getParentFile();
            if (directory != null && !directory.exists()) {
                directory.mkdirs();
            }
            
            Properties props = new Properties();
            props.setProperty("name", trader.getName());
            props.setProperty("type", trader.getType());
            props.setProperty("description", trader.getDescription());
            
            props.store(new FileOutputStream(filePath), "Trader: " + trader.getName());
            
            // Update cache
            traderCache.put(trader.getName(), trader);
            lastSavedObjects.put(filePath, trader.hashCode());
            
            return true;
        } catch (IOException e) {
            logError("Error saving trader to " + filePath, e);
            return false;
        }
    }
    
    /**
     * Loads a trader from a file.
     */
    public static Trader loadTrader(String filePath) {
        // First check the cache
        String traderName = getNameFromFilePath(filePath);
        if (traderName != null && traderCache.containsKey(traderName)) {
            return traderCache.get(traderName);
        }
        
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(filePath));
            
            Trader trader = new Trader();
            trader.setName(props.getProperty("name"));
            trader.setType(props.getProperty("type"));
            trader.setDescription(props.getProperty("description"));
            
            // Update cache
            traderCache.put(trader.getName(), trader);
            
            return trader;
        } catch (IOException e) {
            logError("Error loading trader from " + filePath, e);
            return null;
        }
    }
    
    /**
     * Saves a terrain to a file.
     */
    public static boolean saveTerrain(Terrain terrain, String filePath) {
        try {
            // Ensure the directory exists
            File directory = new File(filePath).getParentFile();
            if (directory != null && !directory.exists()) {
                directory.mkdirs();
            }
            
            Properties props = new Properties();
            props.setProperty("name", terrain.getName());
            props.setProperty("type", terrain.getType());
            props.setProperty("description", terrain.getDescription());
            props.setProperty("imagePath", terrain.getImagePath() != null ? terrain.getImagePath() : "");
            
            props.store(new FileOutputStream(filePath), "Terrain: " + terrain.getName());
            
            // Update cache
            terrainCache.put(terrain.getName(), terrain);
            lastSavedObjects.put(filePath, terrain.hashCode());
            
            System.out.println("Saved terrain: " + terrain.getName() + " with image path: " + terrain.getImagePath());
            
            return true;
        } catch (IOException e) {
            logError("Error saving terrain to " + filePath, e);
            return false;
        }
    }
    
    /**
     * Loads a terrain from a file.
     */
    public static Terrain loadTerrain(String filePath) {
        // First check the cache
        String terrainName = getNameFromFilePath(filePath);
        if (terrainName != null && terrainCache.containsKey(terrainName)) {
            return terrainCache.get(terrainName);
        }
        
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(filePath));
            
            Terrain terrain = new Terrain();
            terrain.setName(props.getProperty("name"));
            terrain.setType(props.getProperty("type"));
            terrain.setDescription(props.getProperty("description"));
            terrain.setImagePath(props.getProperty("imagePath"));
            
            // Update cache
            terrainCache.put(terrain.getName(), terrain);
            
            System.out.println("Loaded terrain: " + terrain.getName() + " with image path: " + terrain.getImagePath());
            
            return terrain;
        } catch (IOException e) {
            logError("Error loading terrain from " + filePath, e);
            return null;
        }
    }
    
    /**
     * Saves a map to a file.
     */
    public static boolean saveMap(GameMap map, String filePath) {
        try {
            // Ensure the directory exists
            File directory = new File(filePath).getParentFile();
            if (directory != null && !directory.exists()) {
                directory.mkdirs();
            }
            
            Properties props = new Properties();
            props.setProperty("name", map.getName());
            props.setProperty("description", map.getDescription());
            props.setProperty("width", String.valueOf(map.getWidth()));
            props.setProperty("height", String.valueOf(map.getHeight()));
            
            // Save cell information
            for (int y = 0; y < map.getHeight(); y++) {
                for (int x = 0; x < map.getWidth(); x++) {
                    MapCell cell = map.getCell(x, y);
                    if (cell != null) {
                        String cellPrefix = "cell_" + x + "_" + y + "_";
                        
                        // Save terrain
                        if (cell.getTerrain() != null) {
                            props.setProperty(cellPrefix + "terrain", cell.getTerrain().getName());
                        }
                        
                        // Save entities
                        if (cell.getEntityCount() > 0) {
                            props.setProperty(cellPrefix + "entityCount", String.valueOf(cell.getEntityCount()));
                            
                            int entityIndex = 0;
                            for (ObjectInstance entity : cell.getEntities()) {
                                props.setProperty(cellPrefix + "entity_" + entityIndex + "_type", entity.getObjectType());
                                props.setProperty(cellPrefix + "entity_" + entityIndex + "_name", entity.getName());
                                entityIndex++;
                            }
                        }
                    }
                }
            }
            
            props.store(new FileOutputStream(filePath), "Map: " + map.getName());
            
            // Update cache
            mapCache.put(map.getName(), map);
            lastSavedObjects.put(filePath, map.hashCode());
            
            return true;
        } catch (IOException e) {
            logError("Error saving map to " + filePath, e);
            return false;
        }
    }
    
    /**
     * Loads a map from a file.
     */
    public static GameMap loadMap(String filePath) {
        // First check the cache
        String mapName = getNameFromFilePath(filePath);
        if (mapName != null && mapCache.containsKey(mapName)) {
            return mapCache.get(mapName);
        }
        
        try {
            Properties props = new Properties();
            props.load(new FileInputStream(filePath));
            
            String name = props.getProperty("name");
            String description = props.getProperty("description", "");
            
            int width;
            try {
                width = Integer.parseInt(props.getProperty("width"));
            } catch (NumberFormatException e) {
                logWarning("Error parsing map dimensions in file: " + filePath);
                width = 10; // Default width
            }
            
            int height;
            try {
                height = Integer.parseInt(props.getProperty("height"));
            } catch (NumberFormatException e) {
                logWarning("Error parsing map dimensions in file: " + filePath);
                height = 10; // Default height
            }
            
            GameMap map = new GameMap(name, width, height);
            map.setDescription(description);
            
            // Load cells
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    String cellPrefix = "cell_" + x + "_" + y + "_";
                    
                    // Load terrain
                    String terrainName = props.getProperty(cellPrefix + "terrain");
                    if (terrainName != null && !terrainName.isEmpty()) {
                        Terrain terrain = terrainCache.get(terrainName);
                        if (terrain != null) {
                            map.setTerrain(x, y, terrain);
                        }
                    }
                    
                    // Load entities
                    String entityCountStr = props.getProperty(cellPrefix + "entityCount");
                    if (entityCountStr != null && !entityCountStr.isEmpty()) {
                        try {
                            int entityCount = Integer.parseInt(entityCountStr);
                            
                            for (int i = 0; i < entityCount; i++) {
                                String entityType = props.getProperty(cellPrefix + "entity_" + i + "_type");
                                String entityName = props.getProperty(cellPrefix + "entity_" + i + "_name");
                                
                                if (entityType != null && entityName != null) {
                                    ObjectInstance entity = null;
                                    
                                    if ("Item".equals(entityType)) {
                                        entity = itemCache.get(entityName);
                                    } else if ("Creature".equals(entityType)) {
                                        entity = creatureCache.get(entityName);
                                    } else if ("Trader".equals(entityType)) {
                                        entity = traderCache.get(entityName);
                                    } else if ("Spawner".equals(entityType)) {
                                        entity = spawnerCache.get(entityName);
                                    }
                                    
                                    if (entity != null) {
                                        map.addEntity(x, y, entity);
                                    }
                                }
                            }
                        } catch (NumberFormatException e) {
                            logWarning("Error parsing entity count in cell (" + x + "," + y + ") in file: " + filePath);
                        }
                    }
                }
            }
            
            // Update cache
            mapCache.put(map.getName(), map);
            
            return map;
        } catch (IOException e) {
            logError("Error loading map from " + filePath, e);
            return null;
        }
    }
    
    /**
     * Gets all available terrains.
     */
    public static List<Terrain> getAllTerrains() {
        loadAllTerrains();
        return new ArrayList<>(terrainCache.values());
    }
    
    /**
     * Gets all available items.
     */
    public static List<Item> getAllItems() {
        loadAllItems();
        return new ArrayList<>(itemCache.values());
    }
    
    /**
     * Gets all available creatures.
     */
    public static List<Creature> getAllCreatures() {
        loadAllCreatures();
        return new ArrayList<>(creatureCache.values());
    }
    
    /**
     * Gets all available traders.
     */
    public static List<Trader> getAllTraders() {
        loadAllTraders();
        return new ArrayList<>(traderCache.values());
    }
    
    /**
     * Gets all available spawners.
     */
    public static List<Spawner> getAllSpawners() {
        loadAllSpawners();
        return new ArrayList<>(spawnerCache.values());
    }
    
    /**
     * Gets all available movements.
     */
    public static List<Movement> getAllMovements() {
        loadAllMovements();
        return new ArrayList<>(movementCache.values());
    }
    
    /**
     * Gets all available maps.
     */
    public static List<GameMap> getAllMaps() {
        loadAllMaps();
        return new ArrayList<>(mapCache.values());
    }
    
    /**
     * Loads all terrains from the exports directory.
     */
    private static void loadAllTerrains() {
        File dir = new File(EXPORTS_DIR + "/terrain");
        
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".txt"));
            
            if (files != null) {
                for (File file : files) {
                    try {
                        loadTerrain(file.getPath());
                    } catch (Exception e) {
                        logError("Error loading terrain: " + file.getName(), e);
                    }
                }
            }
        }
    }
    
    /**
     * Loads all items from the exports directory.
     */
    private static void loadAllItems() {
        File dir = new File(EXPORTS_DIR + "/item");
        
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".txt"));
            
            if (files != null) {
                for (File file : files) {
                    try {
                        loadItem(file.getPath());
                    } catch (Exception e) {
                        logError("Error loading item: " + file.getName(), e);
                    }
                }
            }
        }
    }
    
    /**
     * Loads all creatures from the exports directory.
     */
    private static void loadAllCreatures() {
        File dir = new File(EXPORTS_DIR + "/creature");
        
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".txt"));
            
            if (files != null) {
                for (File file : files) {
                    try {
                        loadCreature(file.getPath());
                    } catch (Exception e) {
                        logError("Error loading creature: " + file.getName(), e);
                    }
                }
            }
        }
    }
    
    /**
     * Loads all traders from the exports directory.
     */
    private static void loadAllTraders() {
        File dir = new File(EXPORTS_DIR + "/trader");
        
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".txt"));
            
            if (files != null) {
                for (File file : files) {
                    try {
                        loadTrader(file.getPath());
                    } catch (Exception e) {
                        logError("Error loading trader: " + file.getName(), e);
                    }
                }
            }
        }
    }
    
    /**
     * Loads all spawners from the exports directory.
     */
    private static void loadAllSpawners() {
        File dir = new File(EXPORTS_DIR + "/spawner");
        
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".txt"));
            
            if (files != null) {
                for (File file : files) {
                    try {
                        loadSpawner(file.getPath());
                    } catch (Exception e) {
                        logError("Error loading spawner: " + file.getName(), e);
                    }
                }
            }
        }
    }
    
    /**
     * Loads all movements from the exports directory.
     */
    private static void loadAllMovements() {
        File dir = new File(EXPORTS_DIR + "/movement");
        
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".txt"));
            
            if (files != null) {
                for (File file : files) {
                    try {
                        loadMovement(file.getPath());
                    } catch (Exception e) {
                        logError("Error loading movement: " + file.getName(), e);
                    }
                }
            }
        }
    }
    
    /**
     * Loads all maps from the exports directory.
     */
    private static void loadAllMaps() {
        File dir = new File(EXPORTS_DIR + "/map");
        
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles((d, name) -> name.toLowerCase().endsWith(".txt"));
            
            if (files != null) {
                for (File file : files) {
                    try {
                        loadMap(file.getPath());
                    } catch (Exception e) {
                        logError("Error loading map: " + file.getName(), e);
                    }
                }
            }
        }
    }
    
    /**
     * Extracts the object name from a file path.
     */
    private static String getNameFromFilePath(String filePath) {
        try {
            File file = new File(filePath);
            String fileName = file.getName();
            
            // Remove extension
            int lastIndex = fileName.lastIndexOf(".");
            if (lastIndex > 0) {
                fileName = fileName.substring(0, lastIndex);
            }
            
            return fileName;
        } catch (Exception e) {
            return null;
        }
    }
}
