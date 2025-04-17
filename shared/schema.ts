import { pgTable, text, serial, integer, boolean } from "drizzle-orm/pg-core";
import { createInsertSchema } from "drizzle-zod";
import { z } from "zod";

// Define basic user schema (required by template)
export const users = pgTable("users", {
  id: serial("id").primaryKey(),
  username: text("username").notNull().unique(),
  password: text("password").notNull(),
});

export const insertUserSchema = createInsertSchema(users).pick({
  username: true,
  password: true,
});

export type InsertUser = z.infer<typeof insertUserSchema>;
export type User = typeof users.$inferSelect;

// Game specific schemas
export const terrainTypeSchema = z.enum([
  "plains",
  "mountain",
  "desert",
  "swamp",
  "forest"
]);

export type TerrainType = z.infer<typeof terrainTypeSchema>;

export const enemyTypeSchema = z.enum([
  "wolf",
  "bear",
  "snake",
  "scorpion",
  "bandit"
]);

export type EnemyType = z.infer<typeof enemyTypeSchema>;

export interface TerrainCost {
  movement: number;
  water: number;
  food: number;
}

export interface Position {
  x: number;
  y: number;
}

export interface Cell {
  type: TerrainType;
  position: Position;
  item?: Item;
  trader?: Trader;
  enemy?: Enemy;
  isPath?: boolean;
}

export interface Item {
  id: string;
  type: "food" | "water" | "gold";
  amount: number;
  isRepeating: boolean;
  collected: boolean;
}

export interface Trader {
  id: string;
  name: string;
  offers: TradeOffer[];
}

export interface TradeOffer {
  resource: "food" | "water";
  amount: number;
  cost: number;
}

export interface Enemy {
  id: string;
  type: EnemyType;
  health: number;
  maxHealth: number;
  damage: number;
  reward: {
    type: "food" | "water" | "gold";
    amount: number;
  };
  isDefeated: boolean;
}

export interface Combat {
  inCombat: boolean;
  enemy?: Enemy;
  playerDamage: number;
  enemyDamage: number;
  turnsLeft: number;
}

export interface PlayerStats {
  currentStrength: number;
  maxStrength: number;
  currentWater: number;
  maxWater: number;
  currentFood: number;
  maxFood: number;
  gold: number;
  damage: number;
  defense: number;
}

export interface GameSettings {
  width: number;
  height: number;
  difficulty: "easy" | "medium" | "hard";
}

export interface Decision {
  action: string;
  timestamp: number;
  strength: number;
  water: number;
  food: number;
}

export interface BrainStrategy {
  name: string;
  prioritizeWater: number;
  prioritizeFood: number;
  prioritizeGold: number;
  prioritizeEastward: number;
  avoidEnemies: number;
}
