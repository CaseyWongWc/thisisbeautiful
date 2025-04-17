import { TerrainType, TerrainCost } from "@shared/schema";

// Define the movement and resource costs for each terrain type
export const TERRAIN_COSTS: Record<TerrainType, TerrainCost> = {
  plains: {
    movement: 1,
    water: 1,
    food: 1
  },
  mountain: {
    movement: 3,
    water: 2,
    food: 2
  },
  desert: {
    movement: 2,
    water: 3,
    food: 1
  },
  swamp: {
    movement: 2,
    water: 0,
    food: 2
  },
  forest: {
    movement: 2,
    water: 1,
    food: 0
  }
};

// Map terrain types to their colors
export const TERRAIN_COLORS: Record<TerrainType, string> = {
  plains: "bg-terrain-plains",
  mountain: "bg-terrain-mountain",
  desert: "bg-terrain-desert",
  swamp: "bg-terrain-swamp",
  forest: "bg-terrain-forest"
};

// Define terrain distribution based on difficulty
export const TERRAIN_DISTRIBUTION = {
  easy: {
    plains: 0.6,
    mountain: 0.1,
    desert: 0.1,
    swamp: 0.1,
    forest: 0.1
  },
  medium: {
    plains: 0.4,
    mountain: 0.15,
    desert: 0.15,
    swamp: 0.15,
    forest: 0.15
  },
  hard: {
    plains: 0.2,
    mountain: 0.2,
    desert: 0.2,
    swamp: 0.2,
    forest: 0.2
  }
};

// Get terrain type based on probability distribution
export function getRandomTerrainType(difficulty: "easy" | "medium" | "hard"): TerrainType {
  const distribution = TERRAIN_DISTRIBUTION[difficulty];
  const rand = Math.random();
  
  let cumulativeProbability = 0;
  for (const [terrain, probability] of Object.entries(distribution)) {
    cumulativeProbability += probability;
    if (rand <= cumulativeProbability) {
      return terrain as TerrainType;
    }
  }
  
  // Default to plains if something goes wrong
  return "plains";
}
