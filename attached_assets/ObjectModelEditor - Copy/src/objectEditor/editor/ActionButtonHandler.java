package objectEditor.editor;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Handler for trade simulation button actions.
 */
public class ActionButtonHandler implements ActionListener {
    private final TradeSimulationPanel panel;
    private final String action;
    
    /**
     * Creates a new action button handler.
     * 
     * @param panel The trade simulation panel
     * @param action The action to perform (accept, decline, steal, leave)
     */
    public ActionButtonHandler(TradeSimulationPanel panel, String action) {
        this.panel = panel;
        this.action = action;
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        switch (action.toLowerCase()) {
            case "accept":
                panel.acceptTrade();
                break;
            case "decline":
                panel.declineTrade();
                break;
            case "steal":
                panel.attemptSteal();
                break;
            case "leave":
                panel.leaveTrade();
                break;
            default:
                // Unknown action
                break;
        }
    }
}
