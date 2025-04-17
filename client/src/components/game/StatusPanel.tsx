import React from 'react';
import { User, Brain, RepeatIcon, ArrowDownIcon, Droplet, Utensils, Coins } from 'lucide-react';
import { PlayerStats, Decision, BrainStrategy, TradeOffer, Trader } from '@shared/schema';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Button } from "@/components/ui/button";
import { BRAIN_STRATEGIES } from '@/lib/game';

interface StatusPanelProps {
  playerStats: PlayerStats;
  decisions: Decision[];
  currentStrategy: BrainStrategy;
  onStrategyChange: (strategy: BrainStrategy) => void;
  visionRange: number;
  nearbyTrader: Trader | null;
  onTrade: (offerIndex: number) => void;
}

const StatusPanel: React.FC<StatusPanelProps> = ({
  playerStats,
  decisions,
  currentStrategy,
  onStrategyChange,
  visionRange,
  nearbyTrader,
  onTrade
}) => {
  // Format time difference for decision log
  const formatTimeDiff = (timestamp: number): string => {
    const now = Date.now();
    const diff = Math.floor((now - timestamp) / 1000);
    
    if (diff < 60) return `${diff}s ago`;
    if (diff < 3600) return `${Math.floor(diff / 60)}m ago`;
    return `${Math.floor(diff / 3600)}h ago`;
  };
  
  return (
    <div className="space-y-6">
      {/* Player Stats */}
      <div className="bg-white rounded-xl shadow-lg p-6">
        <h2 className="text-xl font-semibold mb-4 flex items-center">
          <User className="h-5 w-5 mr-2 text-gray-700" />
          Explorer Status
        </h2>
        <div className="space-y-4">
          {/* Strength */}
          <div>
            <div className="flex justify-between mb-1">
              <span className="text-gray-700 flex items-center">
                <ArrowDownIcon className="h-4 w-4 mr-1.5 text-red-600" />
                Strength
              </span>
              <span>{playerStats.currentStrength}/{playerStats.maxStrength}</span>
            </div>
            <div className="h-2 bg-gray-200 rounded-full overflow-hidden">
              <div 
                className="h-full bg-red-500 rounded-full" 
                style={{ width: `${(playerStats.currentStrength / playerStats.maxStrength) * 100}%` }}
              ></div>
            </div>
          </div>
          
          {/* Water */}
          <div>
            <div className="flex justify-between mb-1">
              <span className="text-gray-700 flex items-center">
                <Droplet className="h-4 w-4 mr-1.5 text-blue-600" />
                Water
              </span>
              <span>{playerStats.currentWater}/{playerStats.maxWater}</span>
            </div>
            <div className="h-2 bg-gray-200 rounded-full overflow-hidden">
              <div 
                className="h-full bg-blue-500 rounded-full" 
                style={{ width: `${(playerStats.currentWater / playerStats.maxWater) * 100}%` }}
              ></div>
            </div>
          </div>
          
          {/* Food */}
          <div>
            <div className="flex justify-between mb-1">
              <span className="text-gray-700 flex items-center">
                <Utensils className="h-4 w-4 mr-1.5 text-green-600" />
                Food
              </span>
              <span>{playerStats.currentFood}/{playerStats.maxFood}</span>
            </div>
            <div className="h-2 bg-gray-200 rounded-full overflow-hidden">
              <div 
                className="h-full bg-green-500 rounded-full" 
                style={{ width: `${(playerStats.currentFood / playerStats.maxFood) * 100}%` }}
              ></div>
            </div>
          </div>
          
          {/* Gold */}
          <div className="flex justify-between">
            <span className="text-gray-700 flex items-center">
              <Coins className="h-4 w-4 mr-1.5 text-yellow-600" />
              Gold
            </span>
            <span>{playerStats.gold}</span>
          </div>
        </div>
      </div>
      
      {/* Brain Decision System */}
      <div className="bg-white rounded-xl shadow-lg p-6">
        <h2 className="text-xl font-semibold mb-4 flex items-center">
          <Brain className="h-5 w-5 mr-2 text-gray-700" />
          Brain System
        </h2>
        
        <div className="space-y-3">
          <div className="flex justify-between items-center">
            <span className="text-gray-700">Current Strategy</span>
            <Select
              value={currentStrategy.name}
              onValueChange={(value) => {
                const strategy = Object.values(BRAIN_STRATEGIES).find(s => s.name === value);
                if (strategy) onStrategyChange(strategy);
              }}
            >
              <SelectTrigger className="w-[120px]">
                <SelectValue placeholder="Strategy" />
              </SelectTrigger>
              <SelectContent>
                {Object.values(BRAIN_STRATEGIES).map(strategy => (
                  <SelectItem key={strategy.name} value={strategy.name}>
                    {strategy.name}
                  </SelectItem>
                ))}
              </SelectContent>
            </Select>
          </div>
          
          <div className="flex justify-between items-center">
            <span className="text-gray-700">Vision Range</span>
            <span className="px-2 py-1 bg-gray-100 rounded text-sm">{visionRange} tiles</span>
          </div>
          
          <div className="border-t pt-3 mt-3">
            <h3 className="font-medium text-gray-800 mb-2">Decision Log</h3>
            <div className="space-y-2 max-h-48 overflow-y-auto">
              {decisions.map((decision, index) => (
                <div key={index} className="text-sm text-gray-600 bg-gray-50 p-2 rounded">
                  <div className="flex justify-between">
                    <span>{decision.action}</span>
                    <span className="text-xs text-gray-500">{formatTimeDiff(decision.timestamp)}</span>
                  </div>
                  <div className="text-xs text-gray-500 mt-1 flex gap-3">
                    <span>Strength: {Math.round(decision.strength)}%</span>
                    <span>Water: {Math.round(decision.water)}%</span>
                    <span>Food: {Math.round(decision.food)}%</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      </div>
      
      {/* Trading System */}
      <div className="bg-white rounded-xl shadow-lg p-6">
        <h2 className="text-xl font-semibold mb-4 flex items-center">
          <RepeatIcon className="h-5 w-5 mr-2 text-gray-700" />
          Trading Post
        </h2>
        
        <div className="mt-2">
          {!nearbyTrader ? (
            <div className="text-center py-4 text-gray-500">
              No traders in vision range
            </div>
          ) : (
            <>
              <div className="flex justify-between items-center mb-3">
                <span className="font-medium text-gray-800">{nearbyTrader.name}</span>
                <span className="text-xs bg-green-100 text-green-800 px-2 py-0.5 rounded-full">In Range</span>
              </div>
              
              <div className="space-y-3 border-t pt-3">
                {nearbyTrader.offers.map((offer, index) => (
                  <div key={index} className="flex justify-between items-center">
                    <div className="flex items-center">
                      <div className={`w-3 h-3 rounded-full ${offer.resource === 'water' ? 'bg-blue-500' : 'bg-green-500'} mr-2`}></div>
                      <span>{offer.resource === 'water' ? 'Water' : 'Food'} +{offer.amount}</span>
                    </div>
                    <div className="flex items-center">
                      <span>{offer.cost}</span>
                      <div className="w-3 h-3 rounded-full bg-yellow-500 ml-2"></div>
                    </div>
                  </div>
                ))}
                
                <div className="flex justify-center mt-4">
                  <Button 
                    variant="default" 
                    disabled={playerStats.gold < Math.min(...nearbyTrader.offers.map(o => o.cost))}
                    onClick={() => {
                      // Find cheapest offer player can afford
                      const affordableOffers = nearbyTrader.offers
                        .map((offer, index) => ({ ...offer, index }))
                        .filter(offer => offer.cost <= playerStats.gold);
                      
                      if (affordableOffers.length > 0) {
                        // Sort by cost (cheapest first)
                        affordableOffers.sort((a, b) => a.cost - b.cost);
                        onTrade(affordableOffers[0].index);
                      }
                    }}
                  >
                    Trade
                  </Button>
                </div>
              </div>
            </>
          )}
        </div>
      </div>
    </div>
  );
};

export default StatusPanel;
