import { PlayerStats, Position, TerrainType, Cell } from "@shared/schema";
import { TERRAIN_COSTS } from "./terrain";

// Create the initial player stats based on difficulty
export function createPlayerStats(difficulty: "easy" | "medium" | "hard"): PlayerStats {
  const baseStats = {
    maxStrength: 100,
    maxWater: 100,
    maxFood: 100
  };
  
  // Adjust starting values based on difficulty
  let startingValues;
  switch (difficulty) {
    case "easy":
      startingValues = {
        currentStrength: 100,
        currentWater: 100,
        currentFood: 100,
        gold: 30,
        damage: 15,
        defense: 10
      };
      break;
    case "medium":
      startingValues = {
        currentStrength: 80,
        currentWater: 70,
        currentFood: 70,
        gold: 20,
        damage: 12,
        defense: 8
      };
      break;
    case "hard":
      startingValues = {
        currentStrength: 60,
        currentWater: 50,
        currentFood: 50,
        gold: 10,
        damage: 10,
        defense: 5
      };
      break;
  }
  
  return {
    ...baseStats,
    ...startingValues
  };
}

// Get starting position (always on the west edge)
export function getStartingPosition(height: number): Position {
  return {
    x: 0,
    y: Math.floor(height / 2) // Start in the middle of the west edge
  };
}

// Update player stats based on movement and terrain
export function updatePlayerStats(
  stats: PlayerStats,
  terrain: TerrainType
): PlayerStats {
  const costs = TERRAIN_COSTS[terrain];
  
  // Calculate new values
  const newStats = { ...stats };
  
  // Reduce resources based on terrain costs
  newStats.currentStrength = Math.max(0, stats.currentStrength - costs.movement);
  newStats.currentWater = Math.max(0, stats.currentWater - costs.water);
  newStats.currentFood = Math.max(0, stats.currentFood - costs.food);
  
  return newStats;
}

// Check if the player can move to a cell
export function canMoveToCell(stats: PlayerStats, terrain: TerrainType): boolean {
  const costs = TERRAIN_COSTS[terrain];
  
  // Check if player has enough resources to move
  return (
    stats.currentStrength >= costs.movement &&
    stats.currentWater >= costs.water &&
    stats.currentFood >= costs.food
  );
}

// Check if player has reached the goal (east edge)
export function hasReachedGoal(position: Position, mapWidth: number): boolean {
  return position.x === mapWidth - 1;
}

// Consume an item to update player stats
export function consumeItem(
  stats: PlayerStats,
  itemType: "food" | "water" | "gold",
  amount: number
): PlayerStats {
  const newStats = { ...stats };
  
  switch (itemType) {
    case "food":
      newStats.currentFood = Math.min(newStats.maxFood, newStats.currentFood + amount);
      break;
    case "water":
      newStats.currentWater = Math.min(newStats.maxWater, newStats.currentWater + amount);
      break;
    case "gold":
      newStats.gold += amount;
      break;
  }
  
  return newStats;
}

// Upgrade player equipment with gold
export function upgradePlayer(
  stats: PlayerStats, 
  upgradeType: "damage" | "defense"
): PlayerStats {
  const newStats = { ...stats };
  const baseCost = upgradeType === "damage" ? 15 : 10;
  
  // Calculate cost based on current level
  const currentLevel = upgradeType === "damage" ? stats.damage : stats.defense;
  const cost = baseCost + Math.floor(currentLevel / 2) * 5;
  
  if (stats.gold >= cost) {
    newStats.gold -= cost;
    
    if (upgradeType === "damage") {
      newStats.damage += 2;
    } else {
      newStats.defense += 1;
    }
  }
  
  return newStats;
}

// Check if player can afford an upgrade
export function canAffordUpgrade(
  stats: PlayerStats,
  upgradeType: "damage" | "defense"
): boolean {
  const baseCost = upgradeType === "damage" ? 15 : 10;
  const currentLevel = upgradeType === "damage" ? stats.damage : stats.defense;
  const cost = baseCost + Math.floor(currentLevel / 2) * 5;
  
  return stats.gold >= cost;
}

// Get upgrade cost
export function getUpgradeCost(
  stats: PlayerStats,
  upgradeType: "damage" | "defense"
): number {
  const baseCost = upgradeType === "damage" ? 15 : 10;
  const currentLevel = upgradeType === "damage" ? stats.damage : stats.defense;
  return baseCost + Math.floor(currentLevel / 2) * 5;
}
