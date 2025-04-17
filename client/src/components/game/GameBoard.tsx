import React, { useState, useRef } from 'react';
import { Play, Pause, Timer, Eye, Skull } from 'lucide-react';
import { Button } from "@/components/ui/button";
import { Cell, Position } from '@shared/schema';
import { TERRAIN_COLORS } from '@/lib/game';
import TerrainKey from './TerrainKey';

interface GameBoardProps {
  grid: Cell[][];
  playerPosition: Position;
  isRunning: boolean;
  toggleRunning: () => void;
  moveSpeed: number;
  setMoveSpeed: (speed: number) => void;
  onCellClick: (x: number, y: number) => void;
  onFindPath: () => void;
  onFindFood: () => void;
  onFindWater: () => void;
}

const GameBoard: React.FC<GameBoardProps> = ({
  grid,
  playerPosition,
  isRunning,
  toggleRunning,
  moveSpeed,
  setMoveSpeed,
  onCellClick,
  onFindPath,
  onFindFood,
  onFindWater
}) => {
  // Calculate grid template columns based on grid width
  const gridTemplateColumns = grid.length > 0 ? `repeat(${grid[0].length}, 2rem)` : '';
  
  // Helper function to determine enemy color
  const getEnemyColor = (type: string): string => {
    switch (type) {
      case 'wolf': return 'text-gray-700';
      case 'bear': return 'text-amber-700';
      case 'snake': return 'text-green-600';
      case 'scorpion': return 'text-yellow-600';
      case 'bandit': return 'text-red-600';
      default: return 'text-gray-700';
    }
  };
  
  return (
    <div className="bg-white rounded-xl shadow-lg p-6 overflow-hidden">
      <div className="flex justify-between mb-4">
        <div className="flex items-center gap-2">
          <Button
            size="icon"
            variant="default"
            onClick={toggleRunning}
            className="rounded-full"
          >
            {isRunning ? <Pause className="h-5 w-5" /> : <Play className="h-5 w-5" />}
          </Button>
          <div className="flex items-center bg-gray-100 rounded-lg p-1.5">
            <Timer className="h-4 w-4 text-gray-600 mr-1.5" />
            <input 
              type="range" 
              min="0.5" 
              max="2" 
              step="0.1" 
              value={moveSpeed} 
              onChange={(e) => setMoveSpeed(parseFloat(e.target.value))} 
              className="w-20 h-2 bg-gray-300 rounded-lg appearance-none cursor-pointer" 
            />
            <span className="ml-1.5 text-xs text-gray-600">{moveSpeed.toFixed(1)}x</span>
          </div>
        </div>
        <div className="flex space-x-2">
          <Button
            variant="outline"
            size="sm"
            onClick={onFindFood}
          >
            Find Food
          </Button>
          <Button
            variant="outline"
            size="sm"
            onClick={onFindWater}
          >
            Find Water
          </Button>
          <Button
            variant="default"
            size="sm"
            onClick={onFindPath}
          >
            Find Path
          </Button>
        </div>
      </div>

      <div 
        className="grid gap-px bg-gray-200 border border-gray-300 rounded overflow-auto max-w-full"
        style={{ 
          gridTemplateColumns,
          gridAutoRows: '2rem'
        }}
      >
        {grid.map((row, y) => 
          row.map((cell, x) => (
            <div 
              key={`${x}-${y}`}
              className={`relative ${TERRAIN_COLORS[cell.type]} ${cell.isPath ? 'ring-2 ring-blue-500' : ''}`}
              onClick={() => onCellClick(x, y)}
            >
              {/* Player position */}
              {playerPosition.x === x && playerPosition.y === y && (
                <div className="absolute inset-0 flex items-center justify-center">
                  <div className="w-6 h-6 bg-blue-500 rounded-full"></div>
                </div>
              )}
              
              {/* Resources */}
              {cell.item && !cell.item.collected && (
                <div className="absolute inset-0 flex items-center justify-center">
                  <div 
                    className={`w-4 h-4 rounded-full ${
                      cell.item.type === 'food' ? 'bg-green-500' : 
                      cell.item.type === 'water' ? 'bg-blue-500' : 
                      'bg-yellow-500'
                    }`}
                  ></div>
                </div>
              )}
              
              {/* Trader */}
              {cell.trader && (
                <div className="absolute inset-0 flex items-center justify-center">
                  <div className="w-5 h-5 bg-purple-500 rounded-full flex items-center justify-center">
                    <Eye className="w-3 h-3 text-white" />
                  </div>
                </div>
              )}
              
              {/* Enemy */}
              {cell.enemy && !cell.enemy.isDefeated && (
                <div className="absolute inset-0 flex items-center justify-center">
                  <div className="relative">
                    <Skull className={`w-5 h-5 ${getEnemyColor(cell.enemy.type)}`} />
                    
                    {/* Health bar for enemies */}
                    <div className="absolute -top-1 left-0 right-0 h-1 bg-gray-200 rounded-full overflow-hidden">
                      <div 
                        className="h-full bg-red-500"
                        style={{ width: `${(cell.enemy.health / cell.enemy.maxHealth) * 100}%` }}
                      />
                    </div>
                  </div>
                </div>
              )}
            </div>
          ))
        )}
      </div>
      
      <TerrainKey />
    </div>
  );
};

export default GameBoard;
