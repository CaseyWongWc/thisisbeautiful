import { Cell, Position } from "@shared/schema";
import { findClosestItemOfType, findClosestTrader } from "./map";

export interface Vision {
  visionRange: number;
  findFood: (map: Cell[][], position: Position) => Position | null;
  findWater: (map: Cell[][], position: Position) => Position | null;
  findGold: (map: Cell[][], position: Position) => Position | null;
  findTrader: (map: Cell[][], position: Position) => Position | null;
  canSeeEastEdge: (map: Cell[][], position: Position) => boolean;
}

// Basic vision implementation
export class BasicVision implements Vision {
  visionRange: number;
  
  constructor(visionRange: number) {
    this.visionRange = visionRange;
  }
  
  findFood(map: Cell[][], position: Position): Position | null {
    return findClosestItemOfType(map, position, "food", this.visionRange);
  }
  
  findWater(map: Cell[][], position: Position): Position | null {
    return findClosestItemOfType(map, position, "water", this.visionRange);
  }
  
  findGold(map: Cell[][], position: Position): Position | null {
    return findClosestItemOfType(map, position, "gold", this.visionRange);
  }
  
  findTrader(map: Cell[][], position: Position): Position | null {
    return findClosestTrader(map, position, this.visionRange);
  }
  
  canSeeEastEdge(map: Cell[][], position: Position): boolean {
    return position.x + this.visionRange >= map[0].length - 1;
  }
}

// Enhanced vision with larger range
export class EnhancedVision implements Vision {
  visionRange: number;
  
  constructor() {
    this.visionRange = 8; // Larger vision range
  }
  
  findFood(map: Cell[][], position: Position): Position | null {
    return findClosestItemOfType(map, position, "food", this.visionRange);
  }
  
  findWater(map: Cell[][], position: Position): Position | null {
    return findClosestItemOfType(map, position, "water", this.visionRange);
  }
  
  findGold(map: Cell[][], position: Position): Position | null {
    return findClosestItemOfType(map, position, "gold", this.visionRange);
  }
  
  findTrader(map: Cell[][], position: Position): Position | null {
    return findClosestTrader(map, position, this.visionRange);
  }
  
  canSeeEastEdge(map: Cell[][], position: Position): boolean {
    return position.x + this.visionRange >= map[0].length - 1;
  }
}

// Focus vision that prioritizes certain resources
export class FocusVision implements Vision {
  visionRange: number;
  focusType: "food" | "water" | "gold";
  
  constructor(focusType: "food" | "water" | "gold") {
    this.visionRange = 6;
    this.focusType = focusType;
  }
  
  findFood(map: Cell[][], position: Position): Position | null {
    const rangeBoost = this.focusType === "food" ? 2 : 0;
    return findClosestItemOfType(map, position, "food", this.visionRange + rangeBoost);
  }
  
  findWater(map: Cell[][], position: Position): Position | null {
    const rangeBoost = this.focusType === "water" ? 2 : 0;
    return findClosestItemOfType(map, position, "water", this.visionRange + rangeBoost);
  }
  
  findGold(map: Cell[][], position: Position): Position | null {
    const rangeBoost = this.focusType === "gold" ? 2 : 0;
    return findClosestItemOfType(map, position, "gold", this.visionRange + rangeBoost);
  }
  
  findTrader(map: Cell[][], position: Position): Position | null {
    return findClosestTrader(map, position, this.visionRange);
  }
  
  canSeeEastEdge(map: Cell[][], position: Position): boolean {
    return position.x + this.visionRange >= map[0].length - 1;
  }
}
