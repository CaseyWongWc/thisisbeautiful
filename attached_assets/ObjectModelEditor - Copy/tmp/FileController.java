package objectEditor.controller;

import java.io.*;

/**
 * Controller for file operations.
 */
public class FileController {
    
    /**
     * Loads data from a file.
     * 
     * @param file the file to load from
     * @return the loaded data
     * @throws IOException if an error occurs during loading
     * @throws ClassNotFoundException if a class in the file cannot be found
     */
    public Object[] loadFromFile(File file) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (Object[]) ois.readObject();
        }
    }
    
    /**
     * Saves data to a file.
     * 
     * @param file the file to save to
     * @param data the data to save
     * @throws IOException if an error occurs during saving
     */
    public void saveToFile(File file, Object[] data) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(data);
        }
    }
}
