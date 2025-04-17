package objectEditor;

import objectEditor.editor.CreatureEditorPanel;
import objectEditor.editor.ItemEditorPanel;
import objectEditor.editor.MovementEditorPanel;
import objectEditor.editor.SpawnerEditorPanel;
import objectEditor.editor.TraderEditorPanel;

import javax.swing.*;
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        System.setProperty("DISPLAY", "0.0.0.0:0.0");

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Object Editor");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(800, 600);

            JTabbedPane tabbedPane = new JTabbedPane();

            ItemEditorPanel itemPanel = new ItemEditorPanel();
            tabbedPane.addTab("Items", itemPanel);

            CreatureEditorPanel creaturePanel = new CreatureEditorPanel();
            tabbedPane.addTab("Creatures", creaturePanel);

            MovementEditorPanel movementPanel = new MovementEditorPanel();
            tabbedPane.addTab("Movement", movementPanel);
            
            TraderEditorPanel traderPanel = new TraderEditorPanel();
            tabbedPane.addTab("Traders", traderPanel);
            
            SpawnerEditorPanel spawnerPanel = new SpawnerEditorPanel();
            tabbedPane.addTab("Spawners", spawnerPanel);

            frame.getContentPane().add(tabbedPane, BorderLayout.CENTER);
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
