import { Cell, Position, PlayerStats, Decision, BrainStrategy, Combat, Enemy } from "@shared/schema";
import { Vision } from "./vision";
import { findPath } from "./map";
import { canMoveToCell } from "./player";
import { simulateCombatTurn } from "./enemy";

export interface Brain {
  strategy: BrainStrategy;
  makeDecision: (
    map: Cell[][],
    playerPosition: Position,
    playerStats: PlayerStats,
    vision: Vision
  ) => { nextPosition: Position; decision: Decision };
  
  // New method for AI combat decisions
  makeCombatDecision: (
    playerStats: PlayerStats,
    enemy: Enemy,
    combat: Combat
  ) => { action: 'attack' | 'flee'; reason: string };
}

// Predefined strategies
export const BRAIN_STRATEGIES: Record<string, BrainStrategy> = {
  balanced: {
    name: "Balanced",
    prioritizeWater: 0.25,
    prioritizeFood: 0.25,
    prioritizeGold: 0.15,
    prioritizeEastward: 0.35,
    avoidEnemies: 0.5,
    combatAggression: 0.5,
    minHealthThreshold: 0.4
  },
  survival: {
    name: "Survival",
    prioritizeWater: 0.4,
    prioritizeFood: 0.4,
    prioritizeGold: 0.05,
    prioritizeEastward: 0.15,
    avoidEnemies: 0.8,
    combatAggression: 0.2,
    minHealthThreshold: 0.6
  },
  explorer: {
    name: "Explorer",
    prioritizeWater: 0.2,
    prioritizeFood: 0.2,
    prioritizeGold: 0.1,
    prioritizeEastward: 0.5,
    avoidEnemies: 0.4,
    combatAggression: 0.6,
    minHealthThreshold: 0.3
  },
  collector: {
    name: "Collector",
    prioritizeWater: 0.2,
    prioritizeFood: 0.2,
    prioritizeGold: 0.4,
    prioritizeEastward: 0.2,
    avoidEnemies: 0.3,
    combatAggression: 0.7,
    minHealthThreshold: 0.3
  },
  aggressive: {
    name: "Aggressive",
    prioritizeWater: 0.15,
    prioritizeFood: 0.15,
    prioritizeGold: 0.3,
    prioritizeEastward: 0.4,
    avoidEnemies: 0.1,
    combatAggression: 0.9,
    minHealthThreshold: 0.2
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
  
  /**
   * Decides whether to attack an enemy or flee based on strategy parameters,
   * player stats, and enemy stats.
   */
  makeCombatDecision(
    playerStats: PlayerStats,
    enemy: Enemy,
    combat: Combat
  ): { action: 'attack' | 'flee'; reason: string } {
    // Get current health percentage
    const healthPercentage = playerStats.currentStrength / playerStats.maxStrength;
    
    // Check if health is below minimum threshold for combat
    if (healthPercentage < this.strategy.minHealthThreshold) {
      return {
        action: 'flee',
        reason: `Health too low (${Math.round(healthPercentage * 100)}%), below threshold of ${Math.round(this.strategy.minHealthThreshold * 100)}%`
      };
    }
    
    // Simulate several combat turns to predict outcome
    let simulatedPlayerStats = { ...playerStats };
    let simulatedEnemy = { ...enemy };
    let playerWillWin = false;
    let turnsToWin = 0;
    let expectedHealthAfterCombat = playerStats.currentStrength;
    
    // Simulate up to 10 combat turns to see if player would win
    for (let i = 0; i < 10; i++) {
      const result = simulateCombatTurn(simulatedPlayerStats, simulatedEnemy);
      simulatedPlayerStats = result.updatedPlayerStats;
      simulatedEnemy = result.updatedEnemy;
      turnsToWin++;
      
      // If enemy is defeated in simulation
      if (simulatedEnemy.health <= 0) {
        playerWillWin = true;
        expectedHealthAfterCombat = simulatedPlayerStats.currentStrength;
        break;
      }
      
      // If player would die in simulation
      if (simulatedPlayerStats.currentStrength <= 0) {
        playerWillWin = false;
        expectedHealthAfterCombat = 0;
        break;
      }
    }
    
    // Calculate reward value based on enemy type and reward amount
    const rewardValue = enemy.reward.amount;
    
    // Calculate cost-benefit ratio of the combat
    const healthLost = playerStats.currentStrength - expectedHealthAfterCombat;
    const healthLostPercentage = healthLost / playerStats.maxStrength;
    
    // Reward-to-damage ratio (higher is better)
    const rewardToDamageRatio = playerWillWin ? rewardValue / Math.max(1, healthLost) : 0;
    
    // Apply combat aggression factor from strategy
    const aggressionModifier = this.strategy.combatAggression;
    
    // Make decision based on factors
    let shouldAttack = false;
    let reason = "";
    
    if (playerWillWin && healthLostPercentage < 0.1) {
      // Easy win with minimal damage
      shouldAttack = true;
      reason = `Easy win with minimal damage (${Math.round(healthLostPercentage * 100)}% health loss)`;
    } else if (playerWillWin && rewardToDamageRatio > 0.5) {
      // Good reward-to-damage ratio
      shouldAttack = true;
      reason = `Favorable reward (${rewardValue}) to damage (${Math.round(healthLost)}) ratio`;
    } else if (playerWillWin && aggressionModifier > 0.7) {
      // Aggressive strategy favors combat even if costly
      shouldAttack = true;
      reason = `Aggressive strategy chooses to fight despite risks`;
    } else if (playerWillWin && rewardToDamageRatio > 0.2 && aggressionModifier > 0.4) {
      // Moderate aggression with acceptable reward
      shouldAttack = true;
      reason = `Acceptable risk for reward with ${this.strategy.name} strategy`;
    } else if (!playerWillWin) {
      // Will lose the fight
      shouldAttack = false;
      reason = `Combat simulation predicts defeat`;
    } else {
      // Default - not worth the risk
      shouldAttack = false;
      reason = `Reward not worth the health loss`;
    }
    
    // Random chance to override based on strategy aggression 
    // (introduces some unpredictability to decisions)
    if (Math.random() < aggressionModifier * 0.2) {
      if (!shouldAttack && playerWillWin) {
        shouldAttack = true;
        reason = `Taking a calculated risk with ${this.strategy.name} strategy`;
      }
    }
    
    return {
      action: shouldAttack ? 'attack' : 'flee',
      reason
    };
  }
}
