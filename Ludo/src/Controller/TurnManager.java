package Controller;

import java.util.List;

import Model.Player;

public class TurnManager {
    private List<Player> players;
    private int currentPlayerIndex;
    private int consecutiveSixes;
    private boolean waitingForRoll;

    public TurnManager(List<Player> players) {
        this.players = players;
        this.currentPlayerIndex = 0;
        this.consecutiveSixes = 0;
        this.waitingForRoll = true;
    }

    public Player getActivePlayer() {
        return players.get(currentPlayerIndex);
    }

    // Controls dice rolling permissions based on active player and turn state
    public boolean canRollDice(Player player) {
        return waitingForRoll && player.equals(getActivePlayer());
    }

    // Core turn logic to be called right after the Steps Die is rolled
    public void handleDiceRoll(int stepsDieResult) {
        if (!waitingForRoll) return;

        // Track consecutive sixes (FR 8.6)
        if (stepsDieResult == 6) {
            consecutiveSixes++;
            if (consecutiveSixes == 3) {
                System.out.println("Third consecutive six! Turn canceled.");
                passTurn();
                return;
            }
        } else {
            consecutiveSixes = 0;
        }

        waitingForRoll = false;

        // Check for available legal moves (FR 8.8 & 8.9)
        // Note: hasLegalMove() should be implemented in conjunction with Hila's pieceMovement class
        if (hasLegalMove(getActivePlayer(), stepsDieResult)) {
            System.out.println("Legal moves available. Waiting for player to move a piece.");
            // The UI should now allow the player to select a piece.
            // After the piece moves, finalizeTurn(stepsDieResult) must be called.
        } else {
            System.out.println("No legal moves available.");
            finalizeTurn(stepsDieResult);
        }
    }

    // Called after a player successfully moves a piece OR if they had no legal moves
    public void finalizeTurn(int stepsDieResult) {
        // Implement an additional roll when the Steps Die result is six (FR 8.5 & FR 8.9)
        if (stepsDieResult == 6 && consecutiveSixes < 3) {
            System.out.println("Rolled a 6! " + getActivePlayer().getName() + " gets another roll.");
            waitingForRoll = true;
        } else {
            passTurn();
        }
    }

    // Implement active player tracking and turn rotation for 2-4 players
    private void passTurn() {
        consecutiveSixes = 0;
        waitingForRoll = true;
        
        // Loop to find the next eligible player (skipping completed players if implemented)
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } while (getActivePlayer().hasFinishedGame()); // Assuming hasFinishedGame() exists in Player.java
        
        System.out.println("Turn passed. Active player is now: " + getActivePlayer().getName());
        // Trigger UI update here to display the active player
    }

    // Placeholder for integration with Hila's pieceMovement logic
    private boolean hasLegalMove(Player activePlayer, int diceResult) {
        // TODO: Integrate with pieceMovement class to validate if any piece can move
        return true; 
    }
}
