
package objectEditor.model;

/**
 * Represents a terrain cell with associated costs.
 */
public class Terrain extends ObjectInstance {
    private static final long serialVersionUID = 1L;
    
    private int strengthCost;
    private int movementCost;
    private int hungerCost;
    private int thirstCost;
    private int goldCost;
    
    /**
     * Creates a new terrain with default values.
     */
    public Terrain() {
        super();
        this.strengthCost = 0;
        this.movementCost = 0;
        this.hungerCost = 0;
        this.thirstCost = 0;
        this.goldCost = 0;
    }
    
    public int getStrengthCost() {
        return strengthCost;
    }
    
    public void setStrengthCost(int strengthCost) {
        this.strengthCost = strengthCost;
    }
    
    public int getMovementCost() {
        return movementCost;
    }
    
    public void setMovementCost(int movementCost) {
        this.movementCost = movementCost;
    }
    
    public int getHungerCost() {
        return hungerCost;
    }
    
    public void setHungerCost(int hungerCost) {
        this.hungerCost = hungerCost;
    }
    
    public int getThirstCost() {
        return thirstCost;
    }
    
    public void setThirstCost(int thirstCost) {
        this.thirstCost = thirstCost;
    }
    
    public int getGoldCost() {
        return goldCost;
    }
    
    public void setGoldCost(int goldCost) {
        this.goldCost = goldCost;
    }
    
    @Override
    public String getObjectType() {
        return "Terrain";
    }
}
