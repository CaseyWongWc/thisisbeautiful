import { Enemy, EnemyType, GameSettings, Position, PlayerStats } from "@shared/schema";
import { v4 as uuidv4 } from 'uuid';

// Enemy stats based on type
export const ENEMY_STATS: Record<EnemyType, {
  health: number;
  damage: number;
  reward: {
    type: "food" | "water" | "gold";
    amount: number;
  };
  terrainPreference: string[];
}> = {
  wolf: {
    health: 50,
    damage: 10,
    reward: {
      type: "food",
      amount: 20
    },
    terrainPreference: ["forest", "plains"]
  },
  bear: {
    health: 80,
    damage: 15,
    reward: {
      type: "food",
      amount: 30
    },
    terrainPreference: ["forest", "mountain"]
  },
  snake: {
    health: 30,
    damage: 20,
    reward: {
      type: "gold",
      amount: 10
    },
    terrainPreference: ["desert", "plains"]
  },
  scorpion: {
    health: 40,
    damage: 15,
    reward: {
      type: "gold",
      amount: 15
    },
    terrainPreference: ["desert", "mountain"]
  },
  bandit: {
    health: 60,
    damage: 12,
    reward: {
      type: "gold",
      amount: 25
    },
    terrainPreference: ["plains", "mountain", "forest"]
  }
};

// Enemy frequency based on difficulty
export const ENEMY_FREQUENCY = {
  easy: 0.05,
  medium: 0.1,
  hard: 0.2
};

// Generate enemies based on map and difficulty
export function generateEnemies(width: number, height: number, difficulty: "easy" | "medium" | "hard", terrainMap: string[][]): { enemy: Enemy, position: Position }[] {
  const enemies: { enemy: Enemy, position: Position }[] = [];
  const frequency = ENEMY_FREQUENCY[difficulty];
  
  // Create a set for player's starting area to avoid putting enemies there
  const safeZone = new Set<string>();
  const startY = Math.floor(height / 2);
  safeZone.add(`0,${startY}`);
  safeZone.add(`1,${startY}`);
  safeZone.add(`0,${startY-1}`);
  safeZone.add(`0,${startY+1}`);
  
  for (let y = 0; y < height; y++) {
    for (let x = 0; x < width; x++) {
      // Skip the starting area
      if (safeZone.has(`${x},${y}`)) continue;
      
      // Chance to place an enemy based on difficulty
      if (Math.random() < frequency) {
        const terrain = terrainMap[y][x];
        const enemyType = getEnemyTypeForTerrain(terrain as any);
        const stats = ENEMY_STATS[enemyType];
        
        // Create enemy with adjusted stats based on difficulty
        const enemy: Enemy = {
          id: uuidv4(),
          type: enemyType,
          health: adjustStatForDifficulty(stats.health, difficulty),
          maxHealth: adjustStatForDifficulty(stats.health, difficulty),
          damage: adjustStatForDifficulty(stats.damage, difficulty),
          reward: { ...stats.reward },
          isDefeated: false
        };
        
        enemies.push({
          enemy,
          position: { x, y }
        });
      }
    }
  }
  
  return enemies;
}

// Get appropriate enemy type for terrain
function getEnemyTypeForTerrain(terrain: string): EnemyType {
  // Find enemy types that prefer this terrain
  const possibleEnemies = Object.entries(ENEMY_STATS)
    .filter(([_, stats]) => stats.terrainPreference.includes(terrain))
    .map(([type]) => type as EnemyType);
  
  // If no match, return random enemy
  if (possibleEnemies.length === 0) {
    const allTypes = Object.keys(ENEMY_STATS) as EnemyType[];
    return allTypes[Math.floor(Math.random() * allTypes.length)];
  }
  
  // Return random enemy from possible list
  return possibleEnemies[Math.floor(Math.random() * possibleEnemies.length)];
}

// Adjust enemy stats based on difficulty
function adjustStatForDifficulty(baseStat: number, difficulty: "easy" | "medium" | "hard"): number {
  const multiplier = difficulty === "easy" ? 0.8 : difficulty === "medium" ? 1.0 : 1.2;
  return Math.round(baseStat * multiplier);
}

// Combat logic
export function simulateCombatTurn(playerStats: PlayerStats, enemy: Enemy): {
  playerStats: PlayerStats;
  enemy: Enemy;
  playerDamage: number;
  enemyDamage: number;
  combatResult: "ongoing" | "playerWon" | "playerLost";
} {
  // Calculate damage
  const playerDamage = Math.max(1, playerStats.damage - Math.floor(Math.random() * 3));
  const enemyDamage = Math.max(1, enemy.damage - Math.min(playerStats.defense, Math.floor(Math.random() * 5)));
  
  // Update health
  const newEnemyHealth = Math.max(0, enemy.health - playerDamage);
  const newPlayerStrength = Math.max(0, playerStats.currentStrength - enemyDamage);
  
  // Create copies of stats and enemy
  const newPlayerStats = { ...playerStats, currentStrength: newPlayerStrength };
  const newEnemy = { ...enemy, health: newEnemyHealth };
  
  // Determine combat result
  let combatResult: "ongoing" | "playerWon" | "playerLost" = "ongoing";
  if (newEnemyHealth <= 0) {
    combatResult = "playerWon";
    newEnemy.isDefeated = true;
  } else if (newPlayerStrength <= 0) {
    combatResult = "playerLost";
  }
  
  return {
    playerStats: newPlayerStats,
    enemy: newEnemy,
    playerDamage,
    enemyDamage,
    combatResult
  };
}

// Collect reward from defeated enemy
export function collectEnemyReward(playerStats: PlayerStats, enemy: Enemy): PlayerStats {
  const newStats = { ...playerStats };
  
  switch (enemy.reward.type) {
    case "food":
      newStats.currentFood = Math.min(newStats.maxFood, newStats.currentFood + enemy.reward.amount);
      break;
    case "water":
      newStats.currentWater = Math.min(newStats.maxWater, newStats.currentWater + enemy.reward.amount);
      break;
    case "gold":
      newStats.gold += enemy.reward.amount;
      break;
  }
  
  return newStats;
}