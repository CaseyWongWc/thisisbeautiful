import React, { useState } from 'react';
import { Skull, ChevronDown, ChevronUp } from 'lucide-react';
import { TERRAIN_COLORS } from '@/lib/game';

const TerrainKey: React.FC = () => {
  const [showEnemies, setShowEnemies] = useState(false);
  
  return (
    <div className="mt-4">
      <div className="flex flex-wrap gap-3">
        <div className="flex items-center gap-1">
          <div className="w-3 h-3 bg-terrain-plains"></div>
          <span className="text-xs">Plains</span>
        </div>
        <div className="flex items-center gap-1">
          <div className="w-3 h-3 bg-terrain-mountain"></div>
          <span className="text-xs">Mountain</span>
        </div>
        <div className="flex items-center gap-1">
          <div className="w-3 h-3 bg-terrain-desert"></div>
          <span className="text-xs">Desert</span>
        </div>
        <div className="flex items-center gap-1">
          <div className="w-3 h-3 bg-terrain-swamp"></div>
          <span className="text-xs">Swamp</span>
        </div>
        <div className="flex items-center gap-1">
          <div className="w-3 h-3 bg-terrain-forest"></div>
          <span className="text-xs">Forest</span>
        </div>
        <div className="flex items-center gap-1">
          <div className="w-3 h-3 rounded-full bg-green-500"></div>
          <span className="text-xs">Food</span>
        </div>
        <div className="flex items-center gap-1">
          <div className="w-3 h-3 rounded-full bg-blue-500"></div>
          <span className="text-xs">Water</span>
        </div>
        <div className="flex items-center gap-1">
          <div className="w-3 h-3 rounded-full bg-yellow-500"></div>
          <span className="text-xs">Gold</span>
        </div>
      </div>
      
      <div className="mt-3">
        <button 
          onClick={() => setShowEnemies(!showEnemies)}
          className="flex items-center gap-1 text-xs text-gray-600 hover:text-gray-900"
        >
          <Skull className="w-3 h-3" />
          <span>Enemies</span>
          {showEnemies ? <ChevronUp className="w-3 h-3" /> : <ChevronDown className="w-3 h-3" />}
        </button>
        
        {showEnemies && (
          <div className="mt-2 grid grid-cols-2 gap-x-6 gap-y-2">
            <div className="flex items-center gap-1">
              <Skull className="w-3 h-3 text-gray-700" />
              <span className="text-xs">Wolf</span>
            </div>
            <div className="flex items-center gap-1">
              <Skull className="w-3 h-3 text-amber-700" />
              <span className="text-xs">Bear</span>
            </div>
            <div className="flex items-center gap-1">
              <Skull className="w-3 h-3 text-green-600" />
              <span className="text-xs">Snake</span>
            </div>
            <div className="flex items-center gap-1">
              <Skull className="w-3 h-3 text-yellow-600" />
              <span className="text-xs">Scorpion</span>
            </div>
            <div className="flex items-center gap-1">
              <Skull className="w-3 h-3 text-red-600" />
              <span className="text-xs">Bandit</span>
            </div>
          </div>
        )}
      </div>
    </div>
  );
};

export default TerrainKey;
