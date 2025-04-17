
package objectEditor.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a trader in the game.
 */
public class Trader extends ObjectInstance {
    private static final long serialVersionUID = 1L;

    private String encounterDialogue;
    private String tradeEventDialogue;
    private String positiveDialogue;
    private String leaveTradeDialogue;
    private String aggroDialogue;
    private List<Item> tradeOffers;
    private int maxOffersBeforeDecline;
    private boolean isAggro;
    private boolean aggroOnMaxReject;
    private Movement passiveMovement;
    private Movement aggroMovement;
    private int maxAggroDuration;
    private float stealSuccessRate;
    private double minPlayerResourcePercentage;
    private double maxPlayerResourcePercentage;
    private int strengthPenalty;
    private int waterPenalty;
    private int foodPenalty;

    /**
     * Creates a new trader with default values.
     */
    public Trader() {
        super();
        setType("Trader");
        this.tradeOffers = new ArrayList<>();
        this.encounterDialogue = "";
        this.tradeEventDialogue = "";
        this.positiveDialogue = "";
        this.leaveTradeDialogue = "";
        this.aggroDialogue = "";
        this.maxOffersBeforeDecline = 3;
        this.isAggro = false;
        this.aggroOnMaxReject = false;
        this.passiveMovement = null;
        this.aggroMovement = null;
        this.maxAggroDuration = 60;
        this.stealSuccessRate = 0.0f;
        this.minPlayerResourcePercentage = 0.0;
        this.maxPlayerResourcePercentage = 1.0;
        this.strengthPenalty = 0;
        this.waterPenalty = 0;
        this.foodPenalty = 0;
    }

    // Getters and Setters
    public String getEncounterDialogue() { return encounterDialogue; }
    public void setEncounterDialogue(String dialogue) {
        this.encounterDialogue = dialogue;
        setProperty("encounterDialogue", dialogue);
    }

    public String getTradeEventDialogue() { return tradeEventDialogue; }
    public void setTradeEventDialogue(String dialogue) {
        this.tradeEventDialogue = dialogue;
        setProperty("tradeEventDialogue", dialogue);
    }

    public String getPositiveDialogue() { return positiveDialogue; }
    public void setPositiveDialogue(String dialogue) {
        this.positiveDialogue = dialogue;
        setProperty("positiveDialogue", dialogue);
    }

    public String getLeaveTradeDialogue() { return leaveTradeDialogue; }
    public void setLeaveTradeDialogue(String dialogue) {
        this.leaveTradeDialogue = dialogue;
        setProperty("leaveTradeDialogue", dialogue);
    }

    public String getAggroDialogue() { return aggroDialogue; }
    public void setAggroDialogue(String dialogue) {
        this.aggroDialogue = dialogue;
        setProperty("aggroDialogue", dialogue);
    }

    public List<Item> getTradeOffers() { 
        // Return empty list if trader is aggro
        if (isAggro) {
            return new ArrayList<>();
        }
        return new ArrayList<>(tradeOffers); 
    }
    
    public void setTradeOffers(List<Item> offers) {
        this.tradeOffers = new ArrayList<>(offers);
        setProperty("tradeOffers", offers);
    }

    public int getMaxOffersBeforeDecline() { return maxOffersBeforeDecline; }
    public void setMaxOffersBeforeDecline(int max) {
        this.maxOffersBeforeDecline = max;
        setProperty("maxOffersBeforeDecline", max);
    }

    public boolean isAggro() { return isAggro; }
    public void setAggro(boolean aggro) {
        this.isAggro = aggro;
        setProperty("isAggro", aggro);
    }

    public boolean isAggroOnMaxReject() { return aggroOnMaxReject; }
    public void setAggroOnMaxReject(boolean aggro) {
        this.aggroOnMaxReject = aggro;
        setProperty("aggroOnMaxReject", aggro);
    }

    public Movement getPassiveMovement() { return passiveMovement; }
    public void setPassiveMovement(Movement movement) {
        this.passiveMovement = movement;
        setProperty("passiveMovement", movement);
    }

    public Movement getAggroMovement() { return aggroMovement; }
    public void setAggroMovement(Movement movement) {
        this.aggroMovement = movement;
        setProperty("aggroMovement", movement);
    }

    public int getMaxAggroDuration() { return maxAggroDuration; }
    public void setMaxAggroDuration(int duration) {
        this.maxAggroDuration = duration;
        setProperty("maxAggroDuration", duration);
    }

    public float getStealSuccessRate() { return stealSuccessRate; }
    public void setStealSuccessRate(float rate) {
        this.stealSuccessRate = Math.max(0.0f, Math.min(1.0f, rate));
        setProperty("stealSuccessRate", rate);
    }

    public double getMinPlayerResourcePercentage() { return minPlayerResourcePercentage; }
    public void setMinPlayerResourcePercentage(double percentage) {
        this.minPlayerResourcePercentage = Math.max(0.0, Math.min(1.0, percentage));
        setProperty("minPlayerResourcePercentage", percentage);
    }

    public double getMaxPlayerResourcePercentage() { return maxPlayerResourcePercentage; }
    public void setMaxPlayerResourcePercentage(double percentage) {
        this.maxPlayerResourcePercentage = Math.max(0.0, Math.min(1.0, percentage));
        setProperty("maxPlayerResourcePercentage", percentage);
    }

    public int getStrengthPenalty() { return strengthPenalty; }
    public void setStrengthPenalty(int penalty) {
        this.strengthPenalty = penalty;
        setProperty("strengthPenalty", penalty);
    }

    public int getWaterPenalty() { return waterPenalty; }
    public void setWaterPenalty(int penalty) {
        this.waterPenalty = penalty;
        setProperty("waterPenalty", penalty);
    }

    public int getFoodPenalty() { return foodPenalty; }
    public void setFoodPenalty(int penalty) {
        this.foodPenalty = penalty;
        setProperty("foodPenalty", penalty);
    }

    @Override
    public void setPropertyValue(String name, Object value) {
        switch (name) {
            case "encounterDialogue":
                setEncounterDialogue((String) value);
                break;
            case "tradeEventDialogue":
                setTradeEventDialogue((String) value);
                break;
            case "positiveDialogue":
                setPositiveDialogue((String) value);
                break;
            case "leaveTradeDialogue":
                setLeaveTradeDialogue((String) value);
                break;
            case "aggroDialogue":
                setAggroDialogue((String) value);
                break;
            case "tradeOffers":
                setTradeOffers((List<Item>) value);
                break;
            case "maxOffersBeforeDecline":
                setMaxOffersBeforeDecline((Integer) value);
                break;
            case "isAggro":
                setAggro((Boolean) value);
                break;
            case "aggroOnMaxReject":
                setAggroOnMaxReject((Boolean) value);
                break;
            case "passiveMovement":
                setPassiveMovement((Movement) value);
                break;
            case "aggroMovement":
                setAggroMovement((Movement) value);
                break;
            case "maxAggroDuration":
                setMaxAggroDuration((Integer) value);
                break;
            case "stealSuccessRate":
                setStealSuccessRate((Float) value);
                break;
            case "minPlayerResourcePercentage":
                setMinPlayerResourcePercentage((Double) value);
                break;
            case "maxPlayerResourcePercentage":
                setMaxPlayerResourcePercentage((Double) value);
                break;
            case "strengthPenalty":
                setStrengthPenalty((Integer) value);
                break;
            case "waterPenalty":
                setWaterPenalty((Integer) value);
                break;
            case "foodPenalty":
                setFoodPenalty((Integer) value);
                break;
            default:
                super.setPropertyValue(name, value);
                break;
        }
    }

    @Override
    public Object clone() {
        Trader clone = (Trader) super.clone();
        clone.tradeOffers = new ArrayList<>(this.tradeOffers);
        // Movement and Item references are not deep cloned
        return clone;
    }
}
