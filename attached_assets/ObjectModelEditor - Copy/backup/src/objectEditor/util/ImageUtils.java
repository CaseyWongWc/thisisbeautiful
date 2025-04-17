package objectEditor.util;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Enhanced utility class for image operations.
 * Provides robust image loading with multiple fallback paths.
 */
public class ImageUtils {
    // Base directory for storing uploaded images
    private static final String BASE_IMAGE_DIR = "resources/images/";
    private static final String ITEMS_DIR = BASE_IMAGE_DIR + "items/";
    private static final String ITEM_DIR = BASE_IMAGE_DIR + "item/"; // Singular for backward compatibility
    private static final String CREATURES_DIR = BASE_IMAGE_DIR + "creatures/";
    private static final String NPCS_DIR = BASE_IMAGE_DIR + "npcs/";
    private static final String TRADERS_DIR = BASE_IMAGE_DIR + "traders/";
    private static final String WEAPONS_DIR = BASE_IMAGE_DIR + "weapons/";
    private static final String MOVEMENT_PATTERNS_DIR = BASE_IMAGE_DIR + "movementpatterns/";
    
    // Cache for storing loaded images to improve performance
    private static final Map<String, ImageIcon> imageCache = new HashMap<>();
    
    // Additional fallback directories to check for images
    private static final String[] FALLBACK_PATHS = {
        "sprites/",
        "resources/sprites/",
        "resources/images/",
        "last-guardian-sprites/",
        "./",
        ""
    };
    
    static {
        System.out.println("Initializing ImageUtils - Current working directory: " + System.getProperty("user.dir"));
        
        // Print out contents of the resources directory if it exists
        File resourcesDir = new File("resources");
        if (resourcesDir.exists() && resourcesDir.isDirectory()) {
            System.out.println("Contents of resources directory:");
            File[] files = resourcesDir.listFiles();
            if (files != null) {
                for (File file : files) {
                    System.out.println("  - " + file.getName());
                }
            }
            
            // Also check the traders directory
            File tradersDir = new File("resources/images/traders");
            if (tradersDir.exists() && tradersDir.isDirectory()) {
                System.out.println("Contents of traders directory:");
                File[] traderFiles = tradersDir.listFiles();
                if (traderFiles != null) {
                    for (File file : traderFiles) {
                        System.out.println("  - " + file.getName());
                    }
                }
            } else {
                System.out.println("Traders directory not found at: " + tradersDir.getAbsolutePath());
            }
        } else {
            System.out.println("Resources directory not found at: " + resourcesDir.getAbsolutePath());
        }
        
        // Ensure all directories exist
        createDirectories(BASE_IMAGE_DIR, ITEMS_DIR, ITEM_DIR, CREATURES_DIR, NPCS_DIR, 
                         TRADERS_DIR, WEAPONS_DIR, MOVEMENT_PATTERNS_DIR);
    }
    
    /**
     * Create directories if they don't exist
     */
    private static void createDirectories(String... dirs) {
        for (String dir : dirs) {
            try {
                Files.createDirectories(Paths.get(dir));
            } catch (IOException e) {
                ErrorLogger.logError("Failed to create directory: " + dir, e);
            }
        }
    }
    
    /**
     * Loads an image from the specified path and returns it as an ImageIcon.
     * Uses caching to improve performance.
     */
    /**
     * Loads an image from the specified path and returns it as an ImageIcon.
     * Uses caching to improve performance.
     */
    public static ImageIcon loadImageIcon(String path) {
        if (path == null || path.trim().isEmpty()) {
            System.out.println("loadImageIcon called with null or empty path");
            return null;
        }
        
        System.out.println("loadImageIcon called with path: " + path);
        
        // Check if image is already in cache
        if (imageCache.containsKey(path)) {
            System.out.println("Image found in cache: " + path);
            return imageCache.get(path);
        }
        
        try {
            // Try with the original path
            File file = new File(path);
            
            // If file doesn't exist, try common variations of the path
            if (!file.exists()) {
                String fileName = new File(path).getName();
                
                // Try different paths in order of likelihood
                String[] possiblePaths = {
                    // Original directory with forward slash
                    path.replace('\\', '/'),
                    // Items folder (plural)
                    ITEMS_DIR + fileName,
                    // Item folder (singular)
                    ITEM_DIR + fileName,
                    // Base images folder
                    BASE_IMAGE_DIR + fileName,
                    // Traders folder
                    TRADERS_DIR + fileName,
                    // Creatures folder
                    CREATURES_DIR + fileName,
                    // Just the filename
                    fileName,
                    // Sprites folder - common case
                    "sprites/" + fileName,
                    // Resources/sprites folder
                    "resources/sprites/" + fileName,
                    // Last Guardian Sprites folder
                    "last-guardian-sprites/" + fileName,
                    // Absolute path using resources
                    path.contains("/images/") ? "resources" + path.substring(path.indexOf("/images/")) : path,
                    // Without resources prefix if it's there
                    path.startsWith("resources/") ? path.substring(10) : path,
                    // Just resources + filename
                    "resources/" + fileName
                };
                
                for (String tryPath : possiblePaths) {
                    file = new File(tryPath);
                    if (file.exists()) {
                        System.out.println("Image found at: " + file.getAbsolutePath());
                        break;
                    }
                }
                
                // If still not found, try additional fallback paths
                if (!file.exists()) {
                    for (String fallbackPath : FALLBACK_PATHS) {
                        String tryPath = fallbackPath + fileName;
                        file = new File(tryPath);
                        if (file.exists()) {
                            System.out.println("Image found at fallback path: " + file.getAbsolutePath());
                            break;
                        }
                    }
                }
                
                // Try with alternative file extensions
                if (!file.exists()) {
                    String baseName = fileName.contains(".") ? 
                        fileName.substring(0, fileName.lastIndexOf('.')) : fileName;
                    
                    String[] extensions = {".png", ".jpg", ".jpeg", ".svg", ".gif"};
                    
                    for (String ext : extensions) {
                        // Try in different common directories
                        String[] tryPaths = {
                            "resources/images/traders/" + baseName + ext,
                            "resources/" + baseName + ext,
                            baseName + ext
                        };
                        
                        for (String tryPath : tryPaths) {
                            file = new File(tryPath);
                            if (file.exists()) {
                                System.out.println("Image found with alternate extension: " + file.getAbsolutePath());
                                break;
                            }
                        }
                        
                        if (file.exists()) break;
                    }
                }
            }
            
            // If still not found, return default icon
            if (!file.exists()) {
                System.out.println("Image not found after all attempts: " + path);
                ErrorLogger.logWarning("Image not found after all attempts: " + path);
                return null;
            }
            
            System.out.println("Loading image from: " + file.getAbsolutePath());
            
            // Special handling for SVG files
            if (file.getName().toLowerCase().endsWith(".svg")) {
                System.out.println("SVG file detected, creating placeholder");
                // Create a placeholder for SVG files which aren't supported by Java's ImageIO
                BufferedImage img = new BufferedImage(200, 200, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = img.createGraphics();
                g2d.setColor(new Color(230, 230, 250)); // Lavender background
                g2d.fillRect(0, 0, img.getWidth(), img.getHeight());
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("SansSerif", Font.BOLD, 14));
                g2d.drawString("SVG Image:", 10, 30);
                g2d.drawString(file.getName(), 10, 50);
                g2d.drawRect(0, 0, 199, 199);
                g2d.dispose();
                
                ImageIcon icon = new ImageIcon(img);
                imageCache.put(path, icon);
                return icon;
            }
            
            BufferedImage image = ImageIO.read(file);
            if (image != null) {
                ImageIcon icon = new ImageIcon(image);
                // Store in cache for future use
                imageCache.put(path, icon);
                System.out.println("Successfully loaded image: " + file.getAbsolutePath());
                return icon;
            }
            
            System.out.println("Failed to read image as BufferedImage: " + file.getAbsolutePath());
            ErrorLogger.logWarning("Failed to read image as BufferedImage: " + path);
            return null;
        } catch (Exception e) {
            System.out.println("Error loading image: " + path + " - " + e.getMessage());
            ErrorLogger.logError("Error loading image: " + path, e);
            return null;
        }
    }
    
    /**
     * Loads an image from the specified path and returns it as a scaled ImageIcon.
     */
    public static ImageIcon loadScaledImageIcon(String path, int width, int height) {
        try {
            // Check if we already have a cached version of this specific scale
            String scaledKey = path + "_" + width + "x" + height;
            if (imageCache.containsKey(scaledKey)) {
                return imageCache.get(scaledKey);
            }
            
            ImageIcon icon = loadImageIcon(path);
            if (icon == null) {
                return createDefaultIcon(width, height);
            }
            
            Image img = icon.getImage();
            // Use SCALE_SMOOTH for better quality scaling
            Image scaledImg = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImg);
            
            // Cache the scaled version
            imageCache.put(scaledKey, scaledIcon);
            return scaledIcon;
        } catch (Exception e) {
            ErrorLogger.logError("Error scaling image: " + path, e);
            return createDefaultIcon(width, height);
        }
    }
    
    /**
     * Creates a default icon with a placeholder.
     */
    private static ImageIcon createDefaultIcon(int width, int height) {
        // Check if we already have this default icon size cached
        String defaultKey = "default_" + width + "x" + height;
        if (imageCache.containsKey(defaultKey)) {
            return imageCache.get(defaultKey);
        }
        
        BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = img.createGraphics();
        
        // Set better rendering hints for smoother lines
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        
        // Fill with light background
        g2d.setColor(new Color(240, 240, 240));
        g2d.fillRect(0, 0, width, height);
        
        // Draw border
        g2d.setColor(Color.GRAY);
        g2d.drawRect(0, 0, width - 1, height - 1);
        
        // Draw X pattern
        g2d.drawLine(0, 0, width, height);
        g2d.drawLine(0, height, width, 0);
        
        // Draw text
        g2d.setColor(Color.BLACK);
        Font font = new Font("Sans-Serif", Font.PLAIN, Math.max(10, width / 10));
        g2d.setFont(font);
        FontMetrics metrics = g2d.getFontMetrics(font);
        String text = "No Image";
        int textWidth = metrics.stringWidth(text);
        int textHeight = metrics.getHeight();
        g2d.drawString(text, (width - textWidth) / 2, (height + textHeight) / 2);
        
        g2d.dispose();
        ImageIcon defaultIcon = new ImageIcon(img);
        
        // Cache the default icon
        imageCache.put(defaultKey, defaultIcon);
        return defaultIcon;
    }
    
    /**
     * Saves an image to the appropriate directory based on the object type.
     */
    public static String saveImage(File file, String objectType) {
        try {
            String targetDir = getDirectoryForType(objectType);
            Path dirPath = Paths.get(targetDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            String fileName = UUID.randomUUID().toString() + getFileExtension(file.getName());
            Path targetPath = dirPath.resolve(fileName);
            Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return targetDir + fileName;
        } catch (IOException e) {
            ErrorLogger.logError("Error saving image for " + objectType, e);
            return null;
        }
    }
    
    /**
     * For backward compatibility - saves to original item directory
     */
    public static String saveItemImage(File file) {
        try {
            String targetDir = ITEM_DIR;
            Path dirPath = Paths.get(targetDir);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }
            String fileName = UUID.randomUUID().toString() + getFileExtension(file.getName());
            Path targetPath = dirPath.resolve(fileName);
            Files.copy(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            return targetDir + fileName;
        } catch (IOException e) {
            ErrorLogger.logError("Error saving item image", e);
            return null;
        }
    }
    
    /**
     * Writes an image to a file with the specified format.
     */
    public static boolean writeImage(BufferedImage image, File file, String format) {
        try {
            // Ensure parent directory exists
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            return ImageIO.write(image, format, file);
        } catch (Exception e) {
            ErrorLogger.logError("Error writing image to " + file.getPath(), e);
            return false;
        }
    }
    
    private static String getDirectoryForType(String objectType) {
        if (objectType == null) {
            return ITEMS_DIR;
        }
        switch (objectType.toLowerCase()) {
            case "item": return ITEM_DIR; // Changed to singular for consistency
            case "creature": return CREATURES_DIR;
            case "npc": return NPCS_DIR;
            case "trader": return TRADERS_DIR;
            case "weapon": return WEAPONS_DIR;
            case "movementpattern": return MOVEMENT_PATTERNS_DIR;
            default: return BASE_IMAGE_DIR;
        }
    }
    
    private static String getFileExtension(String fileName) {
        int lastIndexOf = fileName.lastIndexOf(".");
        if (lastIndexOf == -1) {
            return ".png";
        }
        return fileName.substring(lastIndexOf);
    }
    
    private static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = resizedImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g.dispose();
        return resizedImage;
    }
    
    /**
     * Creates a scaled ImageIcon from the given image path.
     * This is an alias for loadScaledImageIcon for backward compatibility.
     */
    public static ImageIcon createScaledImageIcon(String path, int width, int height) {
        return loadScaledImageIcon(path, width, height);
    }
}
