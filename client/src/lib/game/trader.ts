import { Trader, Position, TradeOffer } from "@shared/schema";
import { v4 as uuidv4 } from "uuid";

export interface TraderWithPosition {
  trader: Trader;
  position: Position;
}

const TRADER_NAMES = [
  "Mountain Trader",
  "Desert Nomad",
  "Forest Merchant",
  "Swamp Dealer",
  "Plains Peddler"
];

export function generateTraders(
  width: number,
  height: number,
  difficulty: "easy" | "medium" | "hard"
): TraderWithPosition[] {
  const traders: TraderWithPosition[] = [];
  
  // Determine number of traders based on difficulty
  const traderCount = difficulty === "easy" ? 5 : difficulty === "medium" ? 3 : 2;
  
  for (let i = 0; i < traderCount; i++) {
    traders.push(createTrader(width, height));
  }
  
  return traders;
}

export function createTrader(width: number, height: number): TraderWithPosition {
  // Generate random position, ensuring not at the edges
  const padding = 2;
  const x = padding + Math.floor(Math.random() * (width - 2 * padding));
  const y = padding + Math.floor(Math.random() * (height - 2 * padding));
  
  // Generate random offers
  const offers: TradeOffer[] = [];
  
  // First offer - water for gold
  offers.push({
    resource: "water",
    amount: 20 + Math.floor(Math.random() * 20), // 20-40
    cost: 10 + Math.floor(Math.random() * 10) // 10-20
  });
  
  // Second offer - food for gold
  offers.push({
    resource: "food",
    amount: 15 + Math.floor(Math.random() * 20), // 15-35
    cost: 8 + Math.floor(Math.random() * 12) // 8-20
  });
  
  // Create trader
  const trader: Trader = {
    id: uuidv4(),
    name: TRADER_NAMES[Math.floor(Math.random() * TRADER_NAMES.length)],
    offers
  };
  
  return {
    trader,
    position: { x, y }
  };
}

// Make a trade with a trader
export function trade(
  trader: Trader,
  offerIndex: number,
  playerGold: number
): { success: boolean; newGold: number; resource: "food" | "water"; amount: number } {
  if (offerIndex < 0 || offerIndex >= trader.offers.length) {
    return { success: false, newGold: playerGold, resource: "food", amount: 0 };
  }
  
  const offer = trader.offers[offerIndex];
  
  if (playerGold < offer.cost) {
    return { success: false, newGold: playerGold, resource: offer.resource, amount: 0 };
  }
  
  return {
    success: true,
    newGold: playerGold - offer.cost,
    resource: offer.resource,
    amount: offer.amount
  };
}
