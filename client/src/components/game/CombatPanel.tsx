import React from 'react';
import { 
  Sword, 
  Shield, 
  RotateCcw, 
  Dices,
  ArrowRight
} from 'lucide-react';
import { Button } from '@/components/ui/button';
import { Progress } from '@/components/ui/progress';
import { Combat, Enemy, PlayerStats } from '@shared/schema';
import { ENEMY_STATS } from '@/lib/game/enemy';

interface CombatPanelProps {
  combat: Combat;
  playerStats: PlayerStats;
  onAttack: () => void;
  onFlee: () => void;
}

const CombatPanel: React.FC<CombatPanelProps> = ({
  combat,
  playerStats,
  onAttack,
  onFlee
}) => {
  if (!combat.inCombat || !combat.enemy) return null;
  
  const enemy = combat.enemy;
  const enemyTypeData = ENEMY_STATS[enemy.type];
  
  // Calculate health percentages
  const playerHealthPercent = (playerStats.currentStrength / playerStats.maxStrength) * 100;
  const enemyHealthPercent = (enemy.health / enemy.maxHealth) * 100;
  
  const getEnemyImageName = (type: string) => {
    return type.charAt(0).toUpperCase() + type.slice(1);
  };
  
  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/70">
      <div className="bg-white rounded-xl shadow-lg p-6 max-w-lg w-full space-y-6">
        <h2 className="text-2xl font-bold text-center">Combat Encounter!</h2>
        
        <div className="grid grid-cols-2 gap-6">
          {/* Player Side */}
          <div className="space-y-4">
            <div className="text-center">
              <div className="bg-blue-100 rounded-full h-24 w-24 mx-auto flex items-center justify-center">
                <div className="text-blue-500 text-4xl font-bold">YOU</div>
              </div>
              <p className="mt-2 font-semibold">Explorer</p>
            </div>
            
            <div className="space-y-2">
              <div className="flex justify-between text-sm">
                <span>Health:</span>
                <span>{playerStats.currentStrength}/{playerStats.maxStrength}</span>
              </div>
              <Progress value={playerHealthPercent} className="h-2" />
              
              <div className="flex justify-between mt-2">
                <div className="flex items-center gap-1">
                  <Sword className="h-4 w-4 text-red-500" />
                  <span>{playerStats.damage}</span>
                </div>
                <div className="flex items-center gap-1">
                  <Shield className="h-4 w-4 text-blue-500" />
                  <span>{playerStats.defense}</span>
                </div>
              </div>
            </div>
          </div>
          
          {/* Enemy Side */}
          <div className="space-y-4">
            <div className="text-center">
              <div className="bg-red-100 rounded-full h-24 w-24 mx-auto flex items-center justify-center">
                <div className="text-red-500 text-3xl font-bold">
                  {getEnemyImageName(enemy.type)}
                </div>
              </div>
              <p className="mt-2 font-semibold capitalize">{enemy.type}</p>
            </div>
            
            <div className="space-y-2">
              <div className="flex justify-between text-sm">
                <span>Health:</span>
                <span>{enemy.health}/{enemy.maxHealth}</span>
              </div>
              <Progress value={enemyHealthPercent} className="h-2 bg-gray-200">
                <div 
                  className="h-full bg-red-500 transition-all" 
                  style={{ width: `${enemyHealthPercent}%` }}
                />
              </Progress>
              
              <div className="flex justify-between mt-2">
                <div className="flex items-center gap-1">
                  <Sword className="h-4 w-4 text-red-500" />
                  <span>{enemy.damage}</span>
                </div>
                <div className="flex items-center gap-1">
                  <div className="flex items-center gap-1 text-xs text-amber-600">
                    <span>Reward:</span>
                    <span>{enemy.reward.amount} {enemy.reward.type}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        
        {/* Combat Log */}
        {(combat.playerDamage > 0 || combat.enemyDamage > 0) && (
          <div className="bg-gray-100 p-3 rounded-lg text-sm">
            <div className="flex items-center justify-center gap-4">
              {combat.playerDamage > 0 && (
                <div className="flex items-center">
                  <span>You dealt</span>
                  <span className="text-red-500 font-bold mx-1">{combat.playerDamage}</span>
                  <span>damage</span>
                </div>
              )}
              
              {combat.playerDamage > 0 && combat.enemyDamage > 0 && (
                <div className="text-gray-400">•</div>
              )}
              
              {combat.enemyDamage > 0 && (
                <div className="flex items-center">
                  <span>You took</span>
                  <span className="text-red-500 font-bold mx-1">{combat.enemyDamage}</span>
                  <span>damage</span>
                </div>
              )}
            </div>
          </div>
        )}
        
        {/* Combat Actions */}
        <div className="flex justify-center gap-4">
          <Button 
            onClick={onAttack} 
            className="flex items-center gap-2"
            variant="default"
            size="lg"
          >
            <Sword className="h-5 w-5" />
            Attack
          </Button>
          
          <Button 
            onClick={onFlee} 
            className="flex items-center gap-2"
            variant="outline"
            size="lg"
          >
            <RotateCcw className="h-5 w-5" />
            Try to Flee
          </Button>
        </div>
        
        <div className="text-center text-sm text-gray-500">
          <div className="flex items-center justify-center">
            <Dices className="h-4 w-4 mr-1" />
            <span>Combat turn: {combat.turnsLeft} turns left</span>
          </div>
        </div>
      </div>
    </div>
  );
};

export default CombatPanel;