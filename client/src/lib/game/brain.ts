import { Cell, Position, PlayerStats, Decision, BrainStrategy } from "@shared/schema";
import { Vision } from "./vision";
import { findPath } from "./map";
import { canMoveToCell } from "./player";

export interface Brain {
  strategy: BrainStrategy;
  makeDecision: (
    map: Cell[][],
    playerPosition: Position,
    playerStats: PlayerStats,
    vision: Vision
  ) => { nextPosition: Position; decision: Decision };
}

// Predefined strategies
export const BRAIN_STRATEGIES: Record<string, BrainStrategy> = {
  balanced: {
    name: "Balanced",
    prioritizeWater: 0.25,
    prioritizeFood: 0.25,
    prioritizeGold: 0.15,
    prioritizeEastward: 0.35
  },
  survival: {
    name: "Survival",
    prioritizeWater: 0.4,
    prioritizeFood: 0.4,
    prioritizeGold: 0.05,
    prioritizeEastward: 0.15
  },
  explorer: {
    name: "Explorer",
    prioritizeWater: 0.2,
    prioritizeFood: 0.2,
    prioritizeGold: 0.1,
    prioritizeEastward: 0.5
  },
  collector: {
    name: "Collector",
    prioritizeWater: 0.2,
    prioritizeFood: 0.2,
    prioritizeGold: 0.4,
    prioritizeEastward: 0.2
  }
};

export class BasicBrain implements Brain {
  strategy: BrainStrategy;
  
  constructor(strategy: BrainStrategy = BRAIN_STRATEGIES.balanced) {
    this.strategy = strategy;
  }
  
  makeDecision(
    map: Cell[][],
    playerPosition: Position,
    playerStats: PlayerStats,
    vision: Vision
  ): { nextPosition: Position; decision: Decision } {
    // Default to moving east if possible
    let nextPosition = { ...playerPosition };
    let decisionText = "Waiting...";
    
    // Emergency survival mode if resources are critically low
    const isWaterCritical = playerStats.currentWater < playerStats.maxWater * 0.2;
    const isFoodCritical = playerStats.currentFood < playerStats.maxFood * 0.2;
    
    if (isWaterCritical) {
      const waterPosition = vision.findWater(map, playerPosition);
      if (waterPosition) {
        const path = findPath(map, playerPosition, waterPosition);
        if (path.length > 1) {
          nextPosition = path[1]; // Next step in the path
          decisionText = "Moving towards water - critically low";
        }
      }
    } else if (isFoodCritical) {
      const foodPosition = vision.findFood(map, playerPosition);
      if (foodPosition) {
        const path = findPath(map, playerPosition, foodPosition);
        if (path.length > 1) {
          nextPosition = path[1];
          decisionText = "Moving towards food - critically low";
        }
      }
    } else {
      // Normal decision making based on strategy
      
      // Calculate resource and goal scores
      let waterScore = 0;
      let foodScore = 0;
      let goldScore = 0;
      let eastScore = 0;
      
      // Water score
      const waterPosition = vision.findWater(map, playerPosition);
      if (waterPosition) {
        const waterDistance = Math.sqrt(
          Math.pow(waterPosition.x - playerPosition.x, 2) + 
          Math.pow(waterPosition.y - playerPosition.y, 2)
        );
        const normalizedWaterLevel = playerStats.currentWater / playerStats.maxWater;
        waterScore = (1 - normalizedWaterLevel) * this.strategy.prioritizeWater * (1 / waterDistance);
      }
      
      // Food score
      const foodPosition = vision.findFood(map, playerPosition);
      if (foodPosition) {
        const foodDistance = Math.sqrt(
          Math.pow(foodPosition.x - playerPosition.x, 2) + 
          Math.pow(foodPosition.y - playerPosition.y, 2)
        );
        const normalizedFoodLevel = playerStats.currentFood / playerStats.maxFood;
        foodScore = (1 - normalizedFoodLevel) * this.strategy.prioritizeFood * (1 / foodDistance);
      }
      
      // Gold score
      const goldPosition = vision.findGold(map, playerPosition);
      if (goldPosition) {
        const goldDistance = Math.sqrt(
          Math.pow(goldPosition.x - playerPosition.x, 2) + 
          Math.pow(goldPosition.y - playerPosition.y, 2)
        );
        goldScore = this.strategy.prioritizeGold * (1 / goldDistance);
      }
      
      // Eastward score (goal-oriented)
      eastScore = this.strategy.prioritizeEastward * (playerPosition.x / map[0].length);
      
      // Determine the best move based on scores
      let highestScore = 0;
      let bestTarget: Position | null = null;
      
      if (waterScore > highestScore) {
        highestScore = waterScore;
        bestTarget = waterPosition;
        decisionText = "Moving towards water";
      }
      
      if (foodScore > highestScore) {
        highestScore = foodScore;
        bestTarget = foodPosition;
        decisionText = "Moving towards food";
      }
      
      if (goldScore > highestScore) {
        highestScore = goldScore;
        bestTarget = goldPosition;
        decisionText = "Moving towards gold";
      }
      
      if (eastScore > highestScore && vision.canSeeEastEdge(map, playerPosition)) {
        highestScore = eastScore;
        bestTarget = { x: map[0].length - 1, y: playerPosition.y };
        decisionText = "Moving eastward towards goal";
      }
      
      // If we found a target, calculate path
      if (bestTarget) {
        const path = findPath(map, playerPosition, bestTarget);
        if (path.length > 1) {
          // Check if we can move to the next position
          const nextCellInPath = map[path[1].y][path[1].x];
          if (canMoveToCell(playerStats, nextCellInPath.type)) {
            nextPosition = path[1];
          } else {
            decisionText = "Cannot move due to insufficient resources";
          }
        }
      }
    }
    
    // Create decision object
    const decision: Decision = {
      action: decisionText,
      timestamp: Date.now(),
      strength: playerStats.currentStrength,
      water: playerStats.currentWater,
      food: playerStats.currentFood
    };
    
    return { nextPosition, decision };
  }
}
