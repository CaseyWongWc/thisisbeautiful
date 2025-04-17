package objectEditor.util;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

/**
 * Utility class for caching images to improve performance.
 */
public class ImageCache {
    private static final Map<String, BufferedImage> imageCache = new HashMap<>();
    private static final int MAX_ATTEMPTS = 3; // Maximum number of attempts to load an image
    
    /**
     * Gets an image from the cache or loads it if not cached.
     * 
     * @param path the image path
     * @return the image, or null if it could not be loaded
     */
    public static BufferedImage getImage(String path) {
        if (path == null || path.isEmpty()) {
            return null;
        }
        
        // Check if image is already cached
        BufferedImage image = imageCache.get(path);
        if (image != null) {
            return image;
        }
        
        // Try to load the image
        for (int attempt = 0; attempt < MAX_ATTEMPTS; attempt++) {
            try {
                File file = new File(path);
                if (file.exists()) {
                    image = ImageIO.read(file);
                    if (image != null) {
                        // Cache the image for future use
                        imageCache.put(path, image);
                        return image;
                    }
                }
                
                // If file not found or couldn't be read, try with resources prefix
                if (!path.startsWith("resources/")) {
                    file = new File("resources/" + path);
                    if (file.exists()) {
                        image = ImageIO.read(file);
                        if (image != null) {
                            // Cache the image for future use
                            imageCache.put(path, image);
                            return image;
                        }
                    }
                }
                
                // Wait a bit before the next attempt
                if (attempt < MAX_ATTEMPTS - 1) {
                    Thread.sleep(100);
                }
            } catch (IOException e) {
                // Log the error but continue trying
                ErrorLogger.logError("Error loading image: " + path + " (attempt " + (attempt + 1) + ")", e);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                ErrorLogger.logError("Image loading interrupted", e);
                return null;
            }
        }
        
        // If we reached this point, all attempts to load the image have failed
        ErrorLogger.logError("WARNING: Image not found after all attempts: " + path, null);
        return null;
    }
    
    /**
     * Clears the image cache to free memory.
     */
    public static void clearCache() {
        imageCache.clear();
        System.gc(); // Request garbage collection
    }
}
