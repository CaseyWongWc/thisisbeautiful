package objectEditor.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for logging errors and warnings.
 */
public class ErrorLogger {
    
    private static final String LOG_FILE = "error_log.txt";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    /**
     * Logs an error message with the associated exception.
     * 
     * @param message The error message
     * @param e The exception
     */
    public static void logError(String message, Exception e) {
        logToFile("ERROR", message);
        if (e != null) {
            System.err.println(message + ": " + e.getMessage());
            e.printStackTrace();
            
            // Log stack trace to file
            try (PrintWriter writer = new PrintWriter(new FileWriter(LOG_FILE, true))) {
                writer.println("Stack trace:");
                e.printStackTrace(writer);
                writer.println();
            } catch (IOException ex) {
                System.err.println("Failed to write stack trace to log file: " + ex.getMessage());
            }
        } else {
            System.err.println(message);
        }
    }
    
    /**
     * Logs an error message without an exception.
     * 
     * @param message The error message
     */
    public static void logError(String message) {
        logError(message, null);
    }
    
    /**
     * Logs a warning message.
     * 
     * @param message The warning message
     */
    public static void logWarning(String message) {
        logToFile("WARNING", message);
        System.out.println("WARNING: " + message);
    }
    
    /**
     * Logs an informational message.
     * 
     * @param message The info message
     */
    public static void logInfo(String message) {
        logToFile("INFO", message);
        System.out.println("INFO: " + message);
    }
    
    /**
     * Writes a log entry to the log file.
     * 
     * @param level The log level (ERROR, WARNING, INFO)
     * @param message The message to log
     */
    private static void logToFile(String level, String message) {
        try {
            File logFile = new File(LOG_FILE);
            boolean fileExists = logFile.exists();
            
            try (PrintWriter writer = new PrintWriter(new FileWriter(logFile, true))) {
                // Write header if file is new
                if (!fileExists) {
                    writer.println("# Object Editor Error Log");
                    writer.println("# Created: " + DATE_FORMAT.format(new Date()));
                    writer.println("# Format: [DATE] [LEVEL] MESSAGE");
                    writer.println();
                }
                
                writer.println("[" + DATE_FORMAT.format(new Date()) + "] [" + level + "] " + message);
            }
        } catch (IOException e) {
            System.err.println("Failed to write to log file: " + e.getMessage());
        }
    }
}
