import java.io.FileNotFoundException;
import java.io.PrintStream;

public class ConsoleToFile 
{
    public static void consoletofile(String[] args) 
    {
        try {
            // Redirect console output to a file
            PrintStream out = new PrintStream("console_output.txt");
            System.setOut(out);

            // Example console output
            System.out.println("This text will be written to the file.");
            System.out.println("Another line for the file.");

            // Remember to close the PrintStream when you're done
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
