package objectEditor.model;

/**
 * Represents a creature in the system.
 */
public class Creature extends ObjectInstance {
    private static final long serialVersionUID = 1L;

    private String id;
    private int strengthPenalty;
    private int waterPenalty;
    private int goldPenalty;
    private Item itemDrop;
    private Movement movement;
    private String difficulties;

    /**
     * Creates a new creature with default values.
     */
    public Creature() {
        super();
        this.id = java.util.UUID.randomUUID().toString();
        this.strengthPenalty = 0;
        this.waterPenalty = 0;
        this.goldPenalty = 0;
        this.itemDrop = null; // Initialize to null
        this.movement = null; // Initialize to null
        this.difficulties = "";
    }

    /**
     * Creates a new creature with an ID and name.
     */
    public Creature(String id, String name) {
        super();
        this.id = id;
        setName(name);
        this.strengthPenalty = 0;
        this.waterPenalty = 0;
        this.goldPenalty = 0;
        this.itemDrop = null; // Initialize to null
        this.movement = null; // Initialize to null
        this.difficulties = "";
    }

    @Override
    public String getObjectType() {
        return "Creature";
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getStrengthPenalty() {
        return strengthPenalty;
    }

    public void setStrengthPenalty(int strengthPenalty) {
        this.strengthPenalty = strengthPenalty;
        setProperty("strengthPenalty", strengthPenalty);
    }

    public int getWaterPenalty() {
        return waterPenalty;
    }

    public void setWaterPenalty(int waterPenalty) {
        this.waterPenalty = waterPenalty;
        setProperty("waterPenalty", waterPenalty);
    }

    public int getGoldPenalty() {
        return goldPenalty;
    }

    public void setGoldPenalty(int goldPenalty) {
        this.goldPenalty = goldPenalty;
        setProperty("goldPenalty", goldPenalty);
    }

    public Item getItemDrop() {
        return itemDrop;
    }

    public void setItemDrop(Item itemDrop) {
        this.itemDrop = itemDrop;
        setProperty("itemDrop", itemDrop);
    }

    /**
     * Gets the movement pattern for this creature.
     *
     * @return the movement pattern, or null if none is set
     */
    public Movement getMovement() {
        return movement;
    }

    /**
     * Sets the movement pattern for this creature.
     *
     * @param movement the movement pattern to set
     */
    public void setMovement(Movement movement) {
        this.movement = movement;
        setProperty("movement", movement);
    }

    public String getDifficulties() {
        return difficulties;
    }

    public String getDifficultiesAsString() {
        return difficulties == null ? "" : difficulties;
    }

    public void setDifficulties(String difficulties) {
        this.difficulties = difficulties;
        setProperty("difficulties", difficulties);
    }

    public ClassDefinition getClassDefinition() {
        return ClassRegistry.getInstance().getClassDefinition("Creature");
    }

    @Override
    public void setPropertyValue(String name, Object value) {
        switch (name) {
            case "strengthPenalty":
                setStrengthPenalty((Integer) value);
                break;
            case "waterPenalty":
                setWaterPenalty((Integer) value);
                break;
            case "goldPenalty":
                setGoldPenalty((Integer) value);
                break;
            case "itemDrop":
                setItemDrop((Item) value); // Cast to Item
                break;
            case "movement":
                setMovement((Movement) value); // Cast to Movement
                break;
            case "difficulties":
                setDifficulties((String) value);
                break;
            default:
                super.setPropertyValue(name, value);
                break;
        }
    }
    
    /**
     * Creates a deep copy of this creature.
     * 
     * @return a clone of this creature
     */
    @Override
    public Object clone() {
        Creature clone = (Creature) super.clone();
        clone.id = java.util.UUID.randomUUID().toString(); // Generate new ID for clone
        clone.strengthPenalty = this.strengthPenalty;
        clone.waterPenalty = this.waterPenalty;
        clone.goldPenalty = this.goldPenalty;
        
        // Handle the item drop reference - we don't clone the item, just reference the same item
        clone.itemDrop = this.itemDrop;
        
        // Handle the movement reference - we don't clone the movement, just reference the same movement
        clone.movement = this.movement;
        
        clone.difficulties = this.difficulties;
        
        return clone;
    }
}
