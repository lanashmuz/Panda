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
    
    // checks if the selected piece belongs to the given player
    public boolean belongsToPlayer(Piece piece, Player player) {
    	if (piece == null || player == null) {
    		return false;
    	}
    	return piece.getPlayerColor().equals(player.getColor());
    }
    
    // checks whether a player can select a piece from their castle
    public boolean canSelectCastlePiece(Piece piece, Player player, Castle castle, int diceResault) {
    	if (piece == null || player == null || castle == null) {
    		return false;
    	}
    	if (!canExitCastle(diceResault)) {
    		return false;
    	}
    	if (!belongsToPlayer(piece, player)) {
    		return false;
    	}
    	if (!castle.getPlayerColor().equals(player.getColor())) {
    		return false;
    	}
    	for (Piece castlePiece : castle.getPieces()) {
    		if (castlePiece == piece) {
    			return true;
    		}
    	}
    	return false;
    }

    // finds the starting square of the selected piece
    public Square findStartSquare(EasyBoard board, Piece piece) {

        if (board == null || piece == null) {
            throw new IllegalArgumentException(
                "Board and piece cannot be null"
            );
        }

        for (Square square : board.getCommonPath()) {
            if (square.getType() == SquareType.START
                    && piece.getPlayerColor()
                            .equals(square.getPlayerColor())) {
                return square;
            }
        }

        throw new IllegalStateException(
            "Starting square not found for "
                    + piece.getPlayerColor()
        );
    }

}
