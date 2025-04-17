import { Enemy, EnemyType, PlayerStats, TerrainType } from "@shared/schema";
import { v4 as uuidv4 } from 'uuid';

// Enemy type statistics
export const ENEMY_STATS = {
  wolf: {
    health: { min: 20, max: 35 },
    damage: { min: 5, max: 10 },
    reward: { min: 5, max: 15 }
  },
  bear: {
    health: { min: 45, max: 70 },
    damage: { min: 8, max: 15 },
    reward: { min: 15, max: 25 }
  },
  snake: {
    health: { min: 15, max: 25 },
    damage: { min: 7, max: 12 },
    reward: { min: 5, max: 15 }
  },
  scorpion: {
    health: { min: 10, max: 20 },
    damage: { min: 10, max: 18 },
    reward: { min: 10, max: 20 }
  },
  bandit: {
    health: { min: 30, max: 50 },
    damage: { min: 6, max: 12 },
    reward: { min: 20, max: 35 }
  }
};

// Enemy terrain preferences
const ENEMY_TERRAIN_PREFERENCES: Record<string, TerrainType[]> = {
  wolf: ["forest", "plains"],
  bear: ["forest", "mountain"],
  snake: ["desert", "swamp"],
  scorpion: ["desert"],
  bandit: ["mountain", "plains"]
};

// Generate enemies based on map and difficulty
export function generateEnemies(
  width: number, 
  height: number, 
  difficulty: "easy" | "medium" | "hard",
  terrainMap: string[][]
): { enemy: Enemy; position: { x: number; y: number } }[] {
  const enemies: { enemy: Enemy; position: { x: number; y: number } }[] = [];
  
  // Determine enemy count based on difficulty and map size
  const mapArea = width * height;
  let enemyCount = 0;
  
  switch (difficulty) {
    case "easy":
      enemyCount = Math.floor(mapArea * 0.03); // ~3% of map
      break;
    case "medium":
      enemyCount = Math.floor(mapArea * 0.05); // ~5% of map
      break;
    case "hard":
      enemyCount = Math.floor(mapArea * 0.08); // ~8% of map
      break;
  }
  
  // Calculate probability for each enemy type based on difficulty
  const enemyTypes = ["wolf", "bear", "snake", "scorpion", "bandit"];
  const enemyTypeDistribution: Record<string, number> = {
    wolf: 0.3,
    bear: 0.15,
    snake: 0.25,
    scorpion: 0.15,
    bandit: 0.15
  };
  
  // Adjust distribution based on difficulty
  if (difficulty === "hard") {
    enemyTypeDistribution.wolf = 0.2;
    enemyTypeDistribution.bear = 0.25;
    enemyTypeDistribution.bandit = 0.25;
  }
  
  // Generate enemies
  for (let i = 0; i < enemyCount; i++) {
    // Find a suitable position
    let x = 0;
    let y = 0;
    let terrain: TerrainType = "plains"; // Default
    let attempts = 0;
    const maxAttempts = 10;
    let validPosition = false;
    
    while (attempts < maxAttempts && !validPosition) {
      x = Math.floor(Math.random() * width);
      y = Math.floor(Math.random() * height);
      
      // Check bounds to prevent accessing undefined cells
      if (y >= 0 && y < terrainMap.length && x >= 0 && x < terrainMap[y].length) {
        terrain = terrainMap[y][x] as TerrainType;
        
        // Check if position is at the starting area or too close to other enemies
        const isTooClose = 
          x < 3 || // Don't place near left edge
          enemies.some(e => Math.abs(e.position.x - x) < 2 && Math.abs(e.position.y - y) < 2);
          
        if (!isTooClose) {
          validPosition = true;
        }
      }
      
      attempts++;
    }
    
    if (attempts >= maxAttempts) continue;
    
    // Determine enemy type based on weighted probability and terrain
    let selectedType: EnemyType = "wolf"; // Default to wolf if nothing else is selected
    
    // First, check which enemy types prefer this terrain
    const suitableTypes = enemyTypes.filter(type => 
      ENEMY_TERRAIN_PREFERENCES[type].includes(terrain)
    );
    
    if (suitableTypes.length > 0) {
      // Choose from suitable types
      const rand = Math.random();
      let cumulativeProbability = 0;
      
      for (const type of suitableTypes) {
        cumulativeProbability += enemyTypeDistribution[type];
        if (rand <= cumulativeProbability) {
          selectedType = type as EnemyType;
          break;
        }
      }
      
      // If we didn't select a type (unlikely but possible)
      if (selectedType === "wolf" && suitableTypes[0] !== "wolf") {
        selectedType = suitableTypes[0] as EnemyType;
      }
    } else {
      // If no suitable types, choose any type
      const rand = Math.random();
      let cumulativeProbability = 0;
      
      for (const type of enemyTypes) {
        cumulativeProbability += enemyTypeDistribution[type];
        if (rand <= cumulativeProbability) {
          selectedType = type as EnemyType;
          break;
        }
      }
    }
    
    // Create the enemy
    const stats = ENEMY_STATS[selectedType as keyof typeof ENEMY_STATS];
    
    const health = Math.floor(
      stats.health.min + Math.random() * (stats.health.max - stats.health.min)
    );
    
    const damage = Math.floor(
      stats.damage.min + Math.random() * (stats.damage.max - stats.damage.min)
    );
    
    const rewardAmount = Math.floor(
      stats.reward.min + Math.random() * (stats.reward.max - stats.reward.min)
    );
    
    // Increase stats slightly for harder difficulties
    const difficultyModifier = difficulty === "easy" ? 0.8 : 
                              difficulty === "medium" ? 1 : 1.2;
    
    // Create enemy object with unique ID
    const finalHealth = Math.floor(health * difficultyModifier);
    const enemy: Enemy = {
      id: uuidv4(),
      type: selectedType as EnemyType,
      health: finalHealth,
      maxHealth: finalHealth,
      damage: Math.floor(damage * difficultyModifier),
      isDefeated: false,
      reward: {
        type: "gold", // All enemies reward gold for now
        amount: Math.floor(rewardAmount * difficultyModifier)
      }
    };
    
    enemies.push({ enemy, position: { x, y } });
  }
  
  return enemies;
}

// Calculate combat results for a single turn
export function simulateCombatTurn(
  playerStats: PlayerStats,
  enemy: Enemy
): {
  updatedPlayerStats: PlayerStats;
  updatedEnemy: Enemy;
  playerDamage: number;
  enemyDamage: number;
} {
  // Calculate player damage to enemy
  const playerDamageBase = playerStats.damage;
  
  // Add some randomness to player damage (±20%)
  const randomFactor = 0.8 + Math.random() * 0.4; // 0.8 to 1.2
  const playerDamage = Math.max(1, Math.floor(playerDamageBase * randomFactor));
  
  // Calculate enemy damage to player
  const enemyDamageBase = Math.max(0, enemy.damage - playerStats.defense * 0.5);
  
  // Add some randomness to enemy damage (±20%)
  const enemyRandomFactor = 0.8 + Math.random() * 0.4; // 0.8 to 1.2
  const enemyDamage = Math.max(1, Math.floor(enemyDamageBase * enemyRandomFactor));
  
  // Update enemy health
  const updatedEnemy = {
    ...enemy,
    health: Math.max(0, enemy.health - playerDamage)
  };
  
  // Update player health
  const updatedPlayerStats = {
    ...playerStats,
    currentStrength: Math.max(0, playerStats.currentStrength - enemyDamage)
  };
  
  return {
    updatedPlayerStats,
    updatedEnemy,
    playerDamage,
    enemyDamage
  };
}

// Add enemy rewards to player stats
export function collectEnemyReward(
  playerStats: PlayerStats,
  enemy: Enemy
): PlayerStats {
  const updatedStats = { ...playerStats };
  
  if (enemy.reward.type === "gold") {
    updatedStats.gold += enemy.reward.amount;
  } else if (enemy.reward.type === "food") {
    updatedStats.currentFood = Math.min(
      updatedStats.maxFood,
      updatedStats.currentFood + enemy.reward.amount
    );
  } else if (enemy.reward.type === "water") {
    updatedStats.currentWater = Math.min(
      updatedStats.maxWater,
      updatedStats.currentWater + enemy.reward.amount
    );
  }
  
  return updatedStats;
}