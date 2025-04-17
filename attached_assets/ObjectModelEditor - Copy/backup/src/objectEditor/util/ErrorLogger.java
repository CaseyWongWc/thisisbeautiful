package objectEditor.util;

import java.io.*;
import java.util.Date;
import java.text.SimpleDateFormat;

/**
 * Utility class for logging errors to a file.
 */
public class ErrorLogger {
    private static final String ERROR_LOG_FILE = "error_log.txt";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Log an error message with exception details.
     */
    public static void logError(String message, Exception e) {
        try (FileWriter fw = new FileWriter(ERROR_LOG_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
             
            out.println("--- " + dateFormat.format(new Date()) + " ---");
            out.println("ERROR: " + message);
            if (e != null) {
                out.println("Exception: " + e.getClass().getName() + ": " + e.getMessage());
                out.println("Stack trace:");
                e.printStackTrace(out);
            }
            out.println();
            
        } catch (IOException ex) {
            System.err.println("Failed to write to error log: " + ex.getMessage());
        }
    }
    
    /**
     * Log an error message without exception details.
     */
    public static void logError(String message) {
        logError(message, null);
    }
    
    /**
     * Log a warning message.
     */
    public static void logWarning(String message) {
        try (FileWriter fw = new FileWriter(ERROR_LOG_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
             
            out.println("--- " + dateFormat.format(new Date()) + " ---");
            out.println("WARNING: " + message);
            out.println();
            
        } catch (IOException ex) {
            System.err.println("Failed to write to error log: " + ex.getMessage());
        }
    }
    
    /**
     * Log an info message.
     */
    public static void logInfo(String message) {
        try (FileWriter fw = new FileWriter(ERROR_LOG_FILE, true);
             BufferedWriter bw = new BufferedWriter(fw);
             PrintWriter out = new PrintWriter(bw)) {
             
            out.println("--- " + dateFormat.format(new Date()) + " ---");
            out.println("INFO: " + message);
            out.println();
            
        } catch (IOException ex) {
            System.err.println("Failed to write to error log: " + ex.getMessage());
        }
    }
    
    /**
     * Clear the error log file.
     */
    public static void clearLog() {
        try (FileWriter fw = new FileWriter(ERROR_LOG_FILE, false)) {
            fw.write("Error log created on " + dateFormat.format(new Date()) + "\n\n");
        } catch (IOException ex) {
            System.err.println("Failed to clear error log: " + ex.getMessage());
        }
    }
}
