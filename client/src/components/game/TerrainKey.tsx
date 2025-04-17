import React from 'react';
import { TERRAIN_COLORS } from '@/lib/game';

const TerrainKey: React.FC = () => {
  return (
    <div className="mt-4 flex flex-wrap gap-3">
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
  );
};

export default TerrainKey;
