package objectEditor.model;

/**
 * Represents an item in the game.
 */
public class Item extends ObjectInstance {
    private static final long serialVersionUID = 1L;

    private int goldValue;
    private int foodValue;  
    private int waterValue; 
    private String difficulties;

    /**
     * Creates a new item with default values.
     */
    public Item() {
        super();
        setType("Item");
        goldValue = 0;
        foodValue = 0;
        waterValue = 0;
        difficulties = "";
    }

    /**
     * @return the gold value of the item
     */
    public int getGoldValue() {
        return goldValue;
    }

    /**
     * Sets the gold value of the item.
     * 
     * @param goldValue the gold value to set
     */
    public void setGoldValue(int goldValue) {
        this.goldValue = goldValue;
        setProperty("goldValue", goldValue);
    }

    /**
     * @return the food value of the item
     */
    public int getFoodValue() {
        return foodValue;
    }

    /**
     * Sets the food value of the item.
     * 
     * @param foodValue the food value to set
     */
    public void setFoodValue(int foodValue) {
        this.foodValue = foodValue;
        setProperty("foodValue", foodValue);
    }

    /**
     * @return the water value of the item
     */
    public int getWaterValue() {
        return waterValue;
    }

    /**
     * Sets the water value of the item.
     * 
     * @param waterValue the water value to set
     */
    public void setWaterValue(int waterValue) {
        this.waterValue = waterValue;
        setProperty("waterValue", waterValue);
    }

    /**
     * @return the difficulties this item appears in
     */
    public String getDifficulties() {
        return difficulties;
    }

    /**
     * Sets the difficulties this item appears in.
     * 
     * @param difficulties the difficulties to set
     */
    public void setDifficulties(String difficulties) {
        this.difficulties = difficulties;
        setProperty("difficulties", difficulties);
    }

    /**
     * Sets a property value by name.
     * 
     * @param name the name of the property
     * @param value the value to set
     */
    @Override
    public void setPropertyValue(String name, Object value) {
        switch (name) {
            case "goldValue":
                setGoldValue((Integer) value);
                break;
            case "foodValue":
                setFoodValue((Integer) value);
                break;
            case "waterValue":
                setWaterValue((Integer) value);
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
     * Creates a deep copy of this item.
     * 
     * @return a clone of this item
     */
    @Override
    public Object clone() {
        Item clone = (Item) super.clone();
        // No need to deep copy primitive types or immutable String
        return clone;
    }
}
