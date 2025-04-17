import React from 'react';
import { useLocation } from 'wouter';
import { Button } from "@/components/ui/button";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";
import { Card, CardContent } from "@/components/ui/card";
import { Map, Compass, Mountain, Waves, Network } from 'lucide-react';

const Home: React.FC = () => {
  const [, setLocation] = useLocation();
  const [difficulty, setDifficulty] = React.useState<"easy" | "medium" | "hard">("medium");
  
  return (
    <div className="min-h-screen bg-gray-50 flex flex-col items-center justify-center p-4">
      <div className="max-w-md w-full space-y-8">
        <div className="text-center">
          <Map className="mx-auto h-12 w-12 text-indigo-600" />
          <h1 className="mt-6 text-3xl font-extrabold text-gray-900">Wilderness Survival System</h1>
          <p className="mt-2 text-sm text-gray-600">
            Navigate through the wilderness, manage resources, and reach the eastern edge
          </p>
        </div>
        
        <Card>
          <CardContent className="pt-6">
            <div className="space-y-6">
              <div>
                <h2 className="text-lg font-medium text-gray-900">Game Settings</h2>
                <p className="mt-1 text-sm text-gray-500">
                  Choose your difficulty level to start the game
                </p>
              </div>
              
              <div className="space-y-4">
                <div>
                  <label className="block text-sm font-medium text-gray-700">Difficulty</label>
                  <Select
                    value={difficulty}
                    onValueChange={(value) => setDifficulty(value as "easy" | "medium" | "hard")}
                  >
                    <SelectTrigger className="w-full mt-1">
                      <SelectValue placeholder="Select difficulty" />
                    </SelectTrigger>
                    <SelectContent>
                      <SelectItem value="easy">Easy</SelectItem>
                      <SelectItem value="medium">Medium</SelectItem>
                      <SelectItem value="hard">Hard</SelectItem>
                    </SelectContent>
                  </Select>
                </div>
                
                <Button 
                  className="w-full"
                  onClick={() => setLocation('/game?difficulty=' + difficulty)}
                >
                  Start Game
                </Button>
              </div>
            </div>
          </CardContent>
        </Card>
        
        <div className="mt-8 space-y-4">
          <h2 className="text-xl font-semibold text-gray-800 text-center">Terrain Types</h2>
          <div className="grid grid-cols-2 sm:grid-cols-3 gap-4">
            <TerrainCard 
              name="Plains" 
              icon={<Compass className="h-5 w-5 text-green-600" />} 
              color="bg-terrain-plains"
              description="Easy to traverse with low resource cost"
            />
            <TerrainCard 
              name="Mountains" 
              icon={<Mountain className="h-5 w-5 text-gray-600" />} 
              color="bg-terrain-mountain"
              description="Difficult terrain with high movement cost"
            />
            <TerrainCard 
              name="Desert" 
              icon={<Waves className="h-5 w-5 text-yellow-600" />} 
              color="bg-terrain-desert"
              description="High water consumption"
            />
            <TerrainCard 
              name="Swamp" 
              icon={<Waves className="h-5 w-5 text-green-800" />} 
              color="bg-terrain-swamp"
              description="Moderate movement but good water supply"
            />
            <TerrainCard 
              name="Forest" 
              icon={<Network className="h-5 w-5 text-green-700" />} 
              color="bg-terrain-forest"
              description="Good for food but slower movement"
            />
          </div>
        </div>
      </div>
    </div>
  );
};

interface TerrainCardProps {
  name: string;
  icon: React.ReactNode;
  color: string;
  description: string;
}

const TerrainCard: React.FC<TerrainCardProps> = ({ name, icon, color, description }) => {
  return (
    <div className="bg-white rounded-lg shadow p-3 flex items-start">
      <div className={`w-8 h-8 ${color} rounded-full flex items-center justify-center mr-3`}>
        {icon}
      </div>
      <div>
        <h3 className="font-medium text-gray-900">{name}</h3>
        <p className="text-xs text-gray-500">{description}</p>
      </div>
    </div>
  );
};

export default Home;
