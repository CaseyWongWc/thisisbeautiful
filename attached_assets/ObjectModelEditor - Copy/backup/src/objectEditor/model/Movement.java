package objectEditor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a movement pattern in the game.
 */
public class Movement extends ObjectInstance {
    private static final long serialVersionUID = 1L;
    
    private List<String> directions;
    private boolean repeating;
    private boolean random;
    private boolean reversible;
    private int moveInterval;
    
    /**
     * Creates a new movement with default values.
     */
    public Movement() {
        super();
        setType("Movement");
        this.directions = new ArrayList<>();
        this.repeating = false;
        this.random = false;
        this.reversible = false;
        this.moveInterval = 1; // Default: move every turn
    }
    
    public List<String> getDirections() {
        return new ArrayList<>(directions);
    }
    
    public void setDirections(List<String> directions) {
        this.directions = new ArrayList<>(directions);
        setProperty("directions", directions);
    }
    
    public boolean isRepeating() {
        return repeating;
    }
    
    public void setRepeating(boolean repeating) {
        this.repeating = repeating;
        setProperty("repeating", repeating);
    }
    
    public boolean isRandom() {
        return random;
    }
    
    public void setRandom(boolean random) {
        this.random = random;
        setProperty("random", random);
    }
    
    public boolean isReversible() {
        return reversible;
    }
    
    public void setReversible(boolean reversible) {
        this.reversible = reversible;
        setProperty("reversible", reversible);
    }
    
    /**
     * Gets the movement interval - how many turns between movements.
     * 1 = move every turn, 2 = move every other turn, etc.
     * @return the movement interval
     */
    public int getMoveInterval() {
        return moveInterval;
    }
    
    /**
     * Sets the movement interval - how many turns between movements.
     * 1 = move every turn, 2 = move every other turn, etc.
     * @param moveInterval the movement interval to set
     */
    public void setMoveInterval(int moveInterval) {
        if (moveInterval < 1) {
            moveInterval = 1; // Ensure minimum value of 1
        }
        this.moveInterval = moveInterval;
        setProperty("moveInterval", moveInterval);
    }

    @Override
    public void setPropertyValue(String name, Object value) {
        switch (name) {
            case "directions":
                setDirections((List<String>) value);
                break;
            case "repeating":
                setRepeating((Boolean) value);
                break;
            case "random":
                setRandom((Boolean) value);
                break;
            case "reversible":
                setReversible((Boolean) value);
                break;
            case "moveInterval":
                try {
                    if (value instanceof Integer) {
                        setMoveInterval((Integer) value);
                    } else if (value instanceof String) {
                        setMoveInterval(Integer.parseInt((String) value));
                    }
                } catch (NumberFormatException e) {
                    // Default to 1 if can't parse
                    setMoveInterval(1);
                }
                break;
            default:
                super.setPropertyValue(name, value);
                break;
        }
    }

    @Override
    public Object clone() {
        Movement clone = (Movement) super.clone();
        clone.directions = new ArrayList<>(this.directions);
        clone.moveInterval = this.moveInterval;
        return clone;
    }
}
