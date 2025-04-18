import React, { useState, useEffect, useRef } from 'react';
import { useLocation } from 'wouter';
import GameHeader from '@/components/game/GameHeader';
import GameBoard from '@/components/game/GameBoard';
import StatusPanel from '@/components/game/StatusPanel';
import { Cell, Position, PlayerStats, Decision, BrainStrategy, GameSettings, Trader } from '@shared/schema';
import { 
  generateMap, 
  findPath, 
  findPathToEast,
  visualizePathOnMap,
  updatePlayerStats,
  createPlayerStats,
  getStartingPosition,
  consumeItem,
  hasReachedGoal,
  BasicBrain,
  BasicVision,
  BRAIN_STRATEGIES,
  collectItem
} from '@/lib/game';
import { getCell } from '@/lib/game/map';
import { useToast } from '@/hooks/use-toast';

const DEFAULT_WIDTH = 20;
const DEFAULT_HEIGHT = 15;

const Game: React.FC = () => {
  const [, setLocation] = useLocation();
  const { toast } = useToast();
  
  // Game state
  const [difficulty, setDifficulty] = useState<"easy" | "medium" | "hard">("medium");
  const [grid, setGrid] = useState<Cell[][]>([]);
  const [playerPosition, setPlayerPosition] = useState<Position>({ x: 0, y: 0 });
  const [playerStats, setPlayerStats] = useState<PlayerStats>({
    currentStrength: 0,
    maxStrength: 0,
    currentWater: 0,
    maxWater: 0,
    currentFood: 0,
    maxFood: 0,
    gold: 0
  });
  
  // Game systems
  const brainRef = useRef(new BasicBrain(BRAIN_STRATEGIES.balanced));
  const visionRef = useRef(new BasicVision(5));
  
  // UI state
  const [isRunning, setIsRunning] = useState(false);
  const [moveSpeed, setMoveSpeed] = useState(1.0);
  const [decisions, setDecisions] = useState<Decision[]>([]);
  const [nearbyTrader, setNearbyTrader] = useState<Trader | null>(null);
  const [gameWon, setGameWon] = useState(false);
  
  // Animation refs
  const lastTimeRef = useRef<number | null>(null);
  const animationRef = useRef<number | null>(null);
  
  // Initialize game
  useEffect(() => {
    // Get difficulty from URL or use default
    const params = new URLSearchParams(window.location.search);
    const diffParam = params.get('difficulty');
    if (diffParam && ['easy', 'medium', 'hard'].includes(diffParam)) {
      setDifficulty(diffParam as "easy" | "medium" | "hard");
    }
    
    initGame(difficulty);
  }, []);
  
  const initGame = (diff: "easy" | "medium" | "hard") => {
    // Stop any running animation
    if (animationRef.current) {
      cancelAnimationFrame(animationRef.current);
      animationRef.current = null;
    }
    
    const settings: GameSettings = {
      width: DEFAULT_WIDTH,
      height: DEFAULT_HEIGHT,
      difficulty: diff
    };
    
    // Generate map
    const newGrid = generateMap(settings);
    
    // Create player
    const newPlayerStats = createPlayerStats(diff);
    const startingPosition = getStartingPosition(settings.height);
    
    // Reset game state
    setGrid(newGrid);
    setPlayerPosition(startingPosition);
    setPlayerStats(newPlayerStats);
    setDecisions([]);
    setIsRunning(false);
    setGameWon(false);
    
    // Log initial decision
    addDecision("Game initialized", newPlayerStats);
  };
  
  // Add a decision to the log
  const addDecision = (action: string, stats: PlayerStats) => {
    const decision: Decision = {
      action,
      timestamp: Date.now(),
      strength: stats.currentStrength,
      water: stats.currentWater,
      food: stats.currentFood
    };
    
    setDecisions(prev => [decision, ...prev].slice(0, 10));
  };
  
  // Check for nearby trader
  const checkForTrader = (position: Position) => {
    const cell = getCell(grid, position);
    if (cell && cell.trader) {
      setNearbyTrader(cell.trader);
    } else {
      setNearbyTrader(null);
    }
  };
  
  // Handle trading
  const handleTrade = (offerIndex: number) => {
    if (!nearbyTrader) return;
    
    const offer = nearbyTrader.offers[offerIndex];
    if (!offer || playerStats.gold < offer.cost) return;
    
    // Update player stats
    const newStats = { ...playerStats };
    newStats.gold -= offer.cost;
    
    if (offer.resource === "food") {
      newStats.currentFood = Math.min(newStats.maxFood, newStats.currentFood + offer.amount);
    } else {
      newStats.currentWater = Math.min(newStats.maxWater, newStats.currentWater + offer.amount);
    }
    
    setPlayerStats(newStats);
    addDecision(`Traded ${offer.cost} gold for ${offer.amount} ${offer.resource}`, newStats);
  };
  
  // Game loop
  const gameLoop = (timestamp: number) => {
    if (!isRunning) return;
    
    // Initialize lastTime on first frame
    if (!lastTimeRef.current) {
      lastTimeRef.current = timestamp;
      animationRef.current = requestAnimationFrame(gameLoop);
      return;
    }
    
    // Calculate time delta
    const delta = timestamp - lastTimeRef.current;
    
    // Only update on suitable intervals based on move speed
    if (delta >= 1000 / moveSpeed) {
      lastTimeRef.current = timestamp;
      
      // Check win condition
      if (hasReachedGoal(playerPosition, grid[0].length)) {
        setIsRunning(false);
        setGameWon(true);
        toast({
          title: "Victory!",
          description: "You've reached the eastern edge of the wilderness!",
        });
        return;
      }
      
      // Check for game over
      if (
        playerStats.currentStrength <= 0 ||
        playerStats.currentWater <= 0 ||
        playerStats.currentFood <= 0
      ) {
        setIsRunning(false);
        toast({
          title: "Game Over",
          description: "You've run out of resources.",
          variant: "destructive"
        });
        return;
      }
      
      // Make a decision using the brain
      const { nextPosition, decision } = brainRef.current.makeDecision(
        grid,
        playerPosition,
        playerStats,
        visionRef.current
      );
      
      // Handle movement
      if (nextPosition.x !== playerPosition.x || nextPosition.y !== playerPosition.y) {
        // Get terrain type of next cell
        const nextCell = getCell(grid, nextPosition);
        if (nextCell) {
          // Update player stats based on terrain
          const newStats = updatePlayerStats(playerStats, nextCell.type);
          setPlayerStats(newStats);
          
          // Move player
          setPlayerPosition(nextPosition);
          
          // Add decision to log
          setDecisions(prev => [decision, ...prev].slice(0, 10));
          
          // Check for items at new position
          if (nextCell.item && !nextCell.item.collected) {
            // Update grid to mark item as collected
            const newGrid = [...grid];
            newGrid[nextPosition.y][nextPosition.x].item = {
              ...nextCell.item,
              collected: true
            };
            setGrid(newGrid);
            
            // Update player stats
            const updatedStats = consumeItem(newStats, nextCell.item.type, nextCell.item.amount);
            setPlayerStats(updatedStats);
            
            // Add decision to log
            addDecision(
              `Collected ${nextCell.item.amount} ${nextCell.item.type}`,
              updatedStats
            );
          }
          
          // Check for trader
          checkForTrader(nextPosition);
        }
      }
    }
    
    animationRef.current = requestAnimationFrame(gameLoop);
  };
  
  // Start/stop game loop
  useEffect(() => {
    if (isRunning) {
      lastTimeRef.current = null;
      animationRef.current = requestAnimationFrame(gameLoop);
    } else if (animationRef.current) {
      cancelAnimationFrame(animationRef.current);
      animationRef.current = null;
    }
    
    return () => {
      if (animationRef.current) {
        cancelAnimationFrame(animationRef.current);
      }
    };
  }, [isRunning, grid, playerPosition, playerStats, moveSpeed]);
  
  // Handle new game
  const handleNewGame = () => {
    initGame(difficulty);
  };
  
  // Handle difficulty change
  const handleDifficultyChange = (diff: "easy" | "medium" | "hard") => {
    setDifficulty(diff);
  };
  
  // Handle cell click
  const handleCellClick = (x: number, y: number) => {
    // Only allow clicking when game is not running
    if (isRunning) return;
    
    // Find path to clicked cell
    const targetPosition = { x, y };
    const path = findPath(grid, playerPosition, targetPosition);
    
    // If path exists, visualize it
    if (path.length > 0) {
      const newGrid = visualizePathOnMap(grid, path);
      setGrid(newGrid);
    }
  };
  
  // Handle strategy change
  const handleStrategyChange = (strategy: BrainStrategy) => {
    brainRef.current = new BasicBrain(strategy);
    addDecision(`Changed strategy to ${strategy.name}`, playerStats);
  };
  
  // Find path to east
  const handleFindPath = () => {
    const path = findPathToEast(grid, playerPosition);
    if (path.length > 0) {
      const newGrid = visualizePathOnMap(grid, path);
      setGrid(newGrid);
      addDecision("Found path to east edge", playerStats);
    } else {
      toast({
        title: "No Path Found",
        description: "Unable to find a path to the east edge.",
        variant: "destructive"
      });
    }
  };
  
  // Find closest food
  const handleFindFood = () => {
    const foodPosition = visionRef.current.findFood(grid, playerPosition);
    if (foodPosition) {
      const path = findPath(grid, playerPosition, foodPosition);
      if (path.length > 0) {
        const newGrid = visualizePathOnMap(grid, path);
        setGrid(newGrid);
        addDecision("Found path to food", playerStats);
      }
    } else {
      toast({
        title: "No Food Found",
        description: "No food within vision range.",
      });
    }
  };
  
  // Find closest water
  const handleFindWater = () => {
    const waterPosition = visionRef.current.findWater(grid, playerPosition);
    if (waterPosition) {
      const path = findPath(grid, playerPosition, waterPosition);
      if (path.length > 0) {
        const newGrid = visualizePathOnMap(grid, path);
        setGrid(newGrid);
        addDecision("Found path to water", playerStats);
      }
    } else {
      toast({
        title: "No Water Found",
        description: "No water within vision range.",
      });
    }
  };
  
  return (
    <div className="container mx-auto py-8 px-4">
      <GameHeader 
        difficulty={difficulty}
        onDifficultyChange={handleDifficultyChange}
        onNewGame={handleNewGame}
      />
      
      <div className="grid grid-cols-1 lg:grid-cols-[1fr,400px] gap-8">
        <GameBoard 
          grid={grid}
          playerPosition={playerPosition}
          isRunning={isRunning}
          toggleRunning={() => setIsRunning(!isRunning)}
          moveSpeed={moveSpeed}
          setMoveSpeed={setMoveSpeed}
          onCellClick={handleCellClick}
          onFindPath={handleFindPath}
          onFindFood={handleFindFood}
          onFindWater={handleFindWater}
        />
        
        <StatusPanel 
          playerStats={playerStats}
          decisions={decisions}
          currentStrategy={brainRef.current.strategy}
          onStrategyChange={handleStrategyChange}
          visionRange={visionRef.current.visionRange}
          nearbyTrader={nearbyTrader}
          onTrade={handleTrade}
        />
      </div>
      
      {gameWon && (
        <div className="mt-6 p-4 bg-green-100 text-green-800 rounded-lg text-center">
          <h2 className="text-xl font-bold mb-2">Congratulations!</h2>
          <p>You've successfully navigated through the wilderness to the eastern edge.</p>
          <Button
            className="mt-4"
            onClick={handleNewGame}
          >
            Play Again
          </Button>
        </div>
      )}
    </div>
  );
};

export default Game;
