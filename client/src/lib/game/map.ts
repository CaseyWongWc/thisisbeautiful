import { Cell, Position, TerrainType, Item, Trader, GameSettings } from "@shared/schema";
import { getRandomTerrainType } from "./terrain";
import { generateItems } from "./item";
import { generateTraders } from "./trader";
import { generateEnemies } from "./enemy";

export function generateMap(settings: GameSettings): Cell[][] {
  const { width, height, difficulty } = settings;
  const map: Cell[][] = [];
  const terrainMap: string[][] = [];

  // Generate terrain
  for (let y = 0; y < height; y++) {
    const row: Cell[] = [];
    const terrainRow: string[] = [];
    
    for (let x = 0; x < width; x++) {
      // Always create an easier path on the horizontal middle
      let type: TerrainType;
      const midY = Math.floor(height / 2);
      if (y >= midY - 1 && y <= midY + 1 && difficulty !== "hard") {
        // Higher chance of plains in the middle path
        type = Math.random() < 0.7 ? "plains" : getRandomTerrainType(difficulty);
      } else {
        type = getRandomTerrainType(difficulty);
      }
      
      terrainRow.push(type);
      row.push({
        type,
        position: { x, y },
        isPath: false,
      });
    }
    map.push(row);
    terrainMap.push(terrainRow);
  }

  // Add items to the map
  const items = generateItems(width, height, difficulty);
  items.forEach(item => {
    const { x, y } = item.position;
    if (map[y] && map[y][x]) {
      map[y][x].item = item;
    }
  });

  // Add traders to the map
  const traders = generateTraders(width, height, difficulty);
  traders.forEach(trader => {
    const { x, y } = trader.position;
    if (map[y] && map[y][x]) {
      map[y][x].trader = trader.trader;
    }
  });
  
  // Add enemies to the map
  const enemies = generateEnemies(width, height, difficulty, terrainMap);
  enemies.forEach(({ enemy, position }) => {
    const { x, y } = position;
    if (map[y] && map[y][x] && !map[y][x].trader && !map[y][x].item) {
      map[y][x].enemy = enemy;
    }
  });

  return map;
}

export function getCell(map: Cell[][], position: Position): Cell | null {
  const { x, y } = position;
  if (x < 0 || y < 0 || y >= map.length || x >= map[0].length) {
    return null;
  }
  return map[y][x];
}

export function findPathToEast(map: Cell[][], startPosition: Position): Position[] {
  const visited: boolean[][] = Array(map.length)
    .fill(false)
    .map(() => Array(map[0].length).fill(false));
  const parent: (Position | null)[][] = Array(map.length)
    .fill(null)
    .map(() => Array(map[0].length).fill(null));

  const queue: Position[] = [startPosition];
  visited[startPosition.y][startPosition.x] = true;

  // Destination is any cell on the eastern edge
  const isDestination = (pos: Position): boolean => pos.x === map[0].length - 1;

  while (queue.length > 0) {
    const current = queue.shift()!;

    if (isDestination(current)) {
      // Reconstruct path
      const path: Position[] = [];
      let pos: Position | null = current;
      while (pos !== null) {
        path.unshift(pos);
        pos = parent[pos.y][pos.x];
      }
      return path;
    }

    // Explore neighbors (4-directional)
    const neighbors: Position[] = [
      { x: current.x + 1, y: current.y }, // East
      { x: current.x, y: current.y - 1 }, // North
      { x: current.x, y: current.y + 1 }, // South
      { x: current.x - 1, y: current.y }  // West
    ];

    for (const neighbor of neighbors) {
      const { x, y } = neighbor;
      // Skip if out of bounds or already visited
      if (x < 0 || y < 0 || y >= map.length || x >= map[0].length || visited[y][x]) {
        continue;
      }

      visited[y][x] = true;
      parent[y][x] = current;
      queue.push(neighbor);
    }
  }

  // No path found
  return [];
}

export function visualizePathOnMap(map: Cell[][], path: Position[]): Cell[][] {
  const newMap = JSON.parse(JSON.stringify(map)) as Cell[][];
  
  path.forEach(position => {
    const { x, y } = position;
    if (newMap[y] && newMap[y][x]) {
      newMap[y][x].isPath = true;
    }
  });
  
  return newMap;
}

export function findClosestItemOfType(
  map: Cell[][],
  position: Position,
  type: "food" | "water" | "gold",
  visionRange: number
): Position | null {
  let closestItem: Position | null = null;
  let closestDistance = Infinity;

  // Search within vision range
  for (let y = Math.max(0, position.y - visionRange); y <= Math.min(map.length - 1, position.y + visionRange); y++) {
    for (let x = Math.max(0, position.x - visionRange); x <= Math.min(map[0].length - 1, position.x + visionRange); x++) {
      const cell = map[y][x];
      if (cell.item && cell.item.type === type && !cell.item.collected) {
        const distance = Math.sqrt(Math.pow(x - position.x, 2) + Math.pow(y - position.y, 2));
        if (distance < closestDistance) {
          closestDistance = distance;
          closestItem = { x, y };
        }
      }
    }
  }

  return closestItem;
}

// Find closest trader within vision range
export function findClosestTrader(
  map: Cell[][],
  position: Position,
  visionRange: number
): Position | null {
  let closestTrader: Position | null = null;
  let closestDistance = Infinity;

  // Search within vision range
  for (let y = Math.max(0, position.y - visionRange); y <= Math.min(map.length - 1, position.y + visionRange); y++) {
    for (let x = Math.max(0, position.x - visionRange); x <= Math.min(map[0].length - 1, position.x + visionRange); x++) {
      const cell = map[y][x];
      if (cell.trader) {
        const distance = Math.sqrt(Math.pow(x - position.x, 2) + Math.pow(y - position.y, 2));
        if (distance < closestDistance) {
          closestDistance = distance;
          closestTrader = { x, y };
        }
      }
    }
  }

  return closestTrader;
}

// Find path between two positions using A* algorithm
export function findPath(
  map: Cell[][],
  start: Position,
  end: Position
): Position[] {
  // Implementation of A* algorithm
  const openSet: Position[] = [start];
  const closedSet: Position[] = [];
  const cameFrom: Record<string, Position> = {};
  const gScore: Record<string, number> = {};
  const fScore: Record<string, number> = {};

  // Helper to get key from position
  const getKey = (pos: Position): string => `${pos.x},${pos.y}`;
  
  // Initialize scores
  gScore[getKey(start)] = 0;
  fScore[getKey(start)] = heuristic(start, end);

  while (openSet.length > 0) {
    // Find node with lowest fScore in openSet
    let current = openSet[0];
    let lowestScore = fScore[getKey(current)];
    let currentIndex = 0;

    for (let i = 1; i < openSet.length; i++) {
      const score = fScore[getKey(openSet[i])];
      if (score < lowestScore) {
        current = openSet[i];
        lowestScore = score;
        currentIndex = i;
      }
    }

    // If we reached the goal
    if (current.x === end.x && current.y === end.y) {
      const path: Position[] = [current];
      while (getKey(current) in cameFrom) {
        current = cameFrom[getKey(current)];
        path.unshift(current);
      }
      return path;
    }

    // Move current from openSet to closedSet
    openSet.splice(currentIndex, 1);
    closedSet.push(current);

    // Check neighbors
    const neighbors: Position[] = [
      { x: current.x + 1, y: current.y },
      { x: current.x - 1, y: current.y },
      { x: current.x, y: current.y + 1 },
      { x: current.x, y: current.y - 1 }
    ];

    for (const neighbor of neighbors) {
      // Skip if out of bounds
      if (
        neighbor.x < 0 ||
        neighbor.y < 0 ||
        neighbor.y >= map.length ||
        neighbor.x >= map[0].length
      ) {
        continue;
      }

      // Skip if already evaluated
      if (closedSet.some(pos => pos.x === neighbor.x && pos.y === neighbor.y)) {
        continue;
      }

      // Calculate tentative gScore
      const tentativeGScore = gScore[getKey(current)] + getCellCost(map, neighbor);

      // Add to open set if not there
      const inOpenSet = openSet.some(pos => pos.x === neighbor.x && pos.y === neighbor.y);
      if (!inOpenSet) {
        openSet.push(neighbor);
      } else if (tentativeGScore >= (gScore[getKey(neighbor)] || Infinity)) {
        // This is not a better path
        continue;
      }

      // This path is the best so far
      cameFrom[getKey(neighbor)] = current;
      gScore[getKey(neighbor)] = tentativeGScore;
      fScore[getKey(neighbor)] = gScore[getKey(neighbor)] + heuristic(neighbor, end);
    }
  }

  // No path found
  return [];
}

// Heuristic function for A* (Manhattan distance)
function heuristic(a: Position, b: Position): number {
  return Math.abs(a.x - b.x) + Math.abs(a.y - b.y);
}

// Get cost of moving to a cell (can be expanded with terrain costs)
function getCellCost(map: Cell[][], position: Position): number {
  const cell = map[position.y][position.x];
  // Basic implementation - could be expanded with terrain costs
  return 1;
}
