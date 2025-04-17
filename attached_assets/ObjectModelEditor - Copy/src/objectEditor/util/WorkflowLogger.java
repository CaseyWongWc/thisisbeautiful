
package objectEditor.util;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for logging workflow execution output to a file.
 */
public class WorkflowLogger {
    private static final String WORKFLOW_LOG_FILE = "workflow_log.txt";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static PrintStream originalOut;
    private static PrintStream workflowOutput;
    
    /**
     * Start logging workflow output to file.
     * @param workflowName The name of the workflow being executed
     */
    public static void startLogging(String workflowName) {
        try {
            // Store the original System.out
            originalOut = System.out;
            
            // Create a new PrintStream that writes to both file and console
            FileOutputStream fos = new FileOutputStream(WORKFLOW_LOG_FILE, true);
            workflowOutput = new PrintStream(new MultiOutputStream(System.out, fos));
            
            // Redirect System.out to our custom PrintStream
            System.setOut(workflowOutput);
            
            // Write workflow start header
            String header = "\n=== Workflow '" + workflowName + "' started at " + 
                          dateFormat.format(new Date()) + " ===\n";
            System.out.println(header);
            
        } catch (IOException e) {
            ErrorLogger.logError("Failed to start workflow logging", e);
        }
    }
    
    /**
     * Stop logging and restore original System.out
     */
    public static void stopLogging() {
        if (workflowOutput != null) {
            System.out.println("\n=== Workflow completed at " + 
                             dateFormat.format(new Date()) + " ===\n");
            workflowOutput.close();
            System.setOut(originalOut);
        }
    }
    
    /**
     * Helper class to write output to multiple streams
     */
    private static class MultiOutputStream extends OutputStream {
        private final OutputStream[] outputs;
        
        public MultiOutputStream(OutputStream... outputs) {
            this.outputs = outputs;
        }
        
        @Override
        public void write(int b) throws IOException {
            for (OutputStream output : outputs) {
                output.write(b);
                output.flush();
            }
        }
        
        @Override
        public void write(byte[] b) throws IOException {
            for (OutputStream output : outputs) {
                output.write(b);
                output.flush();
            }
        }
        
        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            for (OutputStream output : outputs) {
                output.write(b, off, len);
                output.flush();
            }
        }
        
        @Override
        public void flush() throws IOException {
            for (OutputStream output : outputs) {
                output.flush();
            }
        }
        
        @Override
        public void close() throws IOException {
            for (OutputStream output : outputs) {
                output.close();
            }
        }
    }
}
