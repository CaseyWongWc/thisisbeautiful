import { Item, Position } from "@shared/schema";
import { v4 as uuidv4 } from "uuid";

export interface ItemWithPosition extends Item {
  position: Position;
}

export function generateItems(
  width: number,
  height: number,
  difficulty: "easy" | "medium" | "hard"
): ItemWithPosition[] {
  const items: ItemWithPosition[] = [];
  
  // Determine number of items based on difficulty
  const itemCounts = {
    food: difficulty === "easy" ? 20 : difficulty === "medium" ? 15 : 10,
    water: difficulty === "easy" ? 20 : difficulty === "medium" ? 15 : 10,
    gold: difficulty === "easy" ? 15 : difficulty === "medium" ? 10 : 5
  };
  
  // Generate food items
  for (let i = 0; i < itemCounts.food; i++) {
    items.push(createItem("food", width, height));
  }
  
  // Generate water items
  for (let i = 0; i < itemCounts.water; i++) {
    items.push(createItem("water", width, height));
  }
  
  // Generate gold items
  for (let i = 0; i < itemCounts.gold; i++) {
    items.push(createItem("gold", width, height));
  }
  
  return items;
}

export function createItem(
  type: "food" | "water" | "gold",
  width: number,
  height: number
): ItemWithPosition {
  // Generate random position, avoiding edges for more interesting gameplay
  const padding = 1;
  const x = padding + Math.floor(Math.random() * (width - 2 * padding));
  const y = padding + Math.floor(Math.random() * (height - 2 * padding));
  
  // Determine item amount based on type
  let amount = 0;
  let isRepeating = false;
  
  switch (type) {
    case "food":
      amount = 10 + Math.floor(Math.random() * 20); // 10-30
      isRepeating = Math.random() < 0.2; // 20% chance of repeating
      break;
    case "water":
      amount = 10 + Math.floor(Math.random() * 20); // 10-30
      isRepeating = Math.random() < 0.2; // 20% chance of repeating
      break;
    case "gold":
      amount = 5 + Math.floor(Math.random() * 10); // 5-15
      isRepeating = false; // Gold is never repeating
      break;
  }
  
  return {
    id: uuidv4(),
    type,
    amount,
    isRepeating,
    collected: false,
    position: { x, y }
  };
}

export function collectItem(items: ItemWithPosition[], position: Position): ItemWithPosition | null {
  const itemIndex = items.findIndex(
    item => item.position.x === position.x && 
            item.position.y === position.y && 
            !item.collected
  );
  
  if (itemIndex === -1) {
    return null;
  }
  
  const item = { ...items[itemIndex] };
  
  // Mark as collected
  item.collected = true;
  
  // If it's a repeating item, reset after a delay
  if (item.isRepeating) {
    setTimeout(() => {
      items[itemIndex].collected = false;
    }, 30000); // Respawn after 30 seconds
  }
  
  return item;
}
