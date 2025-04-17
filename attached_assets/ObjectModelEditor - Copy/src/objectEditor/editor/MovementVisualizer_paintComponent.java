            g2d.drawString("Move Interval: Every " + moveIntervalSlider.getValue() + 
                          (moveIntervalSlider.getValue() == 1 ? " turn" : " turns"), 10, 50);
                          
            // Display current turn within the move interval
            int moveInterval = moveIntervalSlider.getValue();
            if (moveInterval > 1) {
                g2d.drawString("Current Turn: " + (currentTurn % moveInterval + 1) + " of " + moveInterval +
                              (moveInterval > 1 && currentTurn % moveInterval == 0 ? " (Moving!)" : ""), 10, 70);
            }
