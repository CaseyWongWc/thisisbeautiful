    private void startAnimation(boolean infinite) {
        if (animationTimer != null) {
            animationTimer.cancel();
        }
        
        playInfinitely = infinite;
        visualizer.resetTurnCounter(); // Reset the turn counter when starting animation
        
        animationTimer = new Timer();
        animationTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                // Get current move interval from the slider
                int moveInterval = moveIntervalSlider.getValue();
                
                // Increment turn counter and check if we should move
                visualizer.incrementTurn();
                
                // Only step forward when the current turn reaches the move interval
                if (visualizer.shouldMove(moveInterval)) {
                    if (playInfinitely) {
                        // If we're at the end but playing infinitely, wrap around to the beginning
                        if (visualizer.isAtEnd()) {
                            visualizer.reset();
                        }
                        visualizer.stepForward();
                    } else {
                        // Only step forward if not at the end
                        if (!visualizer.isAtEnd()) {
                            visualizer.stepForward();
                        } else {
                            // We reached the end, stop the animation
                            SwingUtilities.invokeLater(() -> {
                                stopAnimation();
                                isAnimating = false;
                                // Find the play button and update its text
                                // This is a bit hacky - in a real app, we'd store a reference to the button
                                for (Component comp : SwingUtilities.getWindowAncestor(visualizer).getComponents()) {
                                    if (comp instanceof JButton && ((JButton)comp).getText().equals("Pause")) {
                                        ((JButton)comp).setText("Play");
                                        break;
                                    }
                                }
                            });
                        }
                    }
                } else {
                    // Still update the visualizer to show the turn counter even if we don't move
                    visualizer.repaint();
                }
            }
        }, 500, 500);
    }
