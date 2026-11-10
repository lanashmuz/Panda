package Controller;

import Model.Player;
import Model.EasyBoard;
import Model.GameState;
import Model.PieceMovement;
import java.util.List;

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

    // Core turn logic called right after the Steps Die is rolled
    public void handleDiceRoll(int stepsDieResult, EasyBoard board, GameState gameState, PieceMovement pieceMovement) {
        if (!waitingForRoll) return;

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

        // Integration with Hila's piece movement validation
        if (hasLegalMove(getActivePlayer(), stepsDieResult, board, gameState, pieceMovement)) {
            System.out.println("Legal moves available. Waiting for player to move a piece.");
        } else {
            System.out.println("No legal moves available. Turn forfeited.");
            finalizeTurn(stepsDieResult);
        }
    }

    public void finalizeTurn(int stepsDieResult) {
        if (stepsDieResult == 6 && consecutiveSixes < 3) {
            System.out.println("Rolled a 6! " + getActivePlayer().getName() + " gets another roll.");
            waitingForRoll = true;
        } else {
            passTurn();
        }
    }

    private void passTurn() {
        consecutiveSixes = 0;
        waitingForRoll = true;
        
        do {
            currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        } while (getActivePlayer().hasFinishedGame());
        
        System.out.println("Turn passed. Active player is now: " + getActivePlayer().getName());
    }

    private boolean hasLegalMove(Player activePlayer, int diceResult, EasyBoard board, GameState gameState, PieceMovement pieceMovement) {
        return gameState.getPieces().stream()
                .anyMatch(piece -> pieceMovement.canMoveEasy(piece, activePlayer, diceResult, board, gameState));
    }
}