    /**
     * Increments the turn counter
     */
    public void incrementTurn() {
        currentTurn++;
        repaint();
    }
    
    /**
     * Resets the turn counter to 0
     */
    public void resetTurnCounter() {
        currentTurn = 0;
        repaint();
    }
    
    /**
     * Determines if movement should occur based on the current turn and move interval
     * @param moveInterval the number of turns between movements
     * @return true if movement should occur, false otherwise
     */
    public boolean shouldMove(int moveInterval) {
        // Should move when the turn counter reaches the move interval
        // Using modulo to handle recurring intervals
        return moveInterval <= 1 || currentTurn % moveInterval == 0;
    }
