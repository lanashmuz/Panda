package Model;

// handles the basic movement rules 
public class PieceMovement {

    // checks if the dice result allows a piece to leave the castle
    public boolean canExitCastle(int diceResult) {
        return diceResult == 6;
    }

    // checks if the dice result is between 1 and 6
    public boolean isValidDiceResult(int diceResult) {
        return diceResult >= 1 && diceResult <= 6;
    }

    // calculates the number of movement steps in Easy mode
    public int calculateEasySteps(int diceResult) {
        if (!isValidDiceResult(diceResult)) {
            throw new IllegalArgumentException(
                "Dice result must be between 1 and 6"
            );
        }

        return diceResult;
    }
}
