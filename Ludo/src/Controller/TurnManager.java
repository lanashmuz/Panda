package Controller;

import java.util.List;

import Model.EasyBoard;
import Model.GameState;
import Model.PieceMovement;
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

    public boolean canRollDice(Player player) {
        return waitingForRoll && player.equals(getActivePlayer());
    }

    // Process the dice result and check consecutive sixes
    public boolean processDiceRoll(int stepsDieResult) {
        if (!waitingForRoll) return false;

        if (stepsDieResult == 6) {
            consecutiveSixes++;
            if (consecutiveSixes == 3) {
                System.out.println("Third consecutive six! Turn canceled.");
                passTurn();
                return false; // Turn is forfeited
            }
        } else {
            consecutiveSixes = 0;
        }

        waitingForRoll = false;
        return true; // Roll is valid, check for legal moves next
    }

    public void finalizeTurn(int stepsDieResult) {
        if (stepsDieResult == 6 && consecutiveSixes < 3) {
            System.out.println("Rolled a 6! " + getActivePlayer().getName() + " gets another roll.");
            waitingForRoll = true;
        } else {
            passTurn();
        }
    }

    public void passTurn() {
        consecutiveSixes = 0;
        waitingForRoll = true;
        
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } while (getActivePlayer().hasFinishedGame());
        
        System.out.println("Turn passed. Active player is now: " + getActivePlayer().getName());
    }

    public boolean hasLegalMove(Player activePlayer, int diceResult, EasyBoard board, GameState gameState, PieceMovement pieceMovement) {
        return gameState.getPieces().stream()
                .anyMatch(piece -> pieceMovement.canMoveEasy(piece, activePlayer, diceResult, board, gameState));
    }
}