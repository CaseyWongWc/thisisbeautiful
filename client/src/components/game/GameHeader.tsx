import React from 'react';
import { Button } from "@/components/ui/button";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";

interface GameHeaderProps {
  difficulty: "easy" | "medium" | "hard";
  onDifficultyChange: (difficulty: "easy" | "medium" | "hard") => void;
  onNewGame: () => void;
}

const GameHeader: React.FC<GameHeaderProps> = ({ 
  difficulty, 
  onDifficultyChange, 
  onNewGame 
}) => {
  return (
    <div className="mb-6 flex flex-col sm:flex-row sm:items-center sm:justify-between">
      <h1 className="text-3xl font-bold text-gray-800">Wilderness Navigation</h1>
      <div className="flex gap-2 mt-2 sm:mt-0">
        <Select
          value={difficulty}
          onValueChange={(value) => onDifficultyChange(value as "easy" | "medium" | "hard")}
        >
          <SelectTrigger className="w-[120px]">
            <SelectValue placeholder="Difficulty" />
          </SelectTrigger>
          <SelectContent>
            <SelectItem value="easy">Easy</SelectItem>
            <SelectItem value="medium">Medium</SelectItem>
            <SelectItem value="hard">Hard</SelectItem>
          </SelectContent>
        </Select>
        
        <Button 
          variant="default" 
          onClick={onNewGame}
        >
          New Game
        </Button>
      </div>
    </div>
  );
};

export default GameHeader;
