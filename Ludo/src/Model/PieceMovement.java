package Model;

import java.util.HashMap;
import java.util.Map;

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
    
    // Finds the arrow leading to a player's home path.
    private Square findHomeArrow(EasyBoard board, Piece piece) {

        for (Square square : board.getCommonPath()) {
            if (square.getType() == SquareType.ARROW
                && piece.getPlayerColor().equals(
                    square.getPlayerColor())) {
                return square;
            }
        }

        throw new IllegalStateException(
            "Home arrow not found");
    }

    // Checks for an opponent's double block on a square.
    private boolean hasOpposingDoubleBlock(
            Piece piece, int squareIndex, GameState state) {

        Map<String, Integer> counts = new HashMap<>();

        for (Piece other : state.getPieces()) {

            if (other.getPlayerColor().equals(
                    piece.getPlayerColor())) {
                continue;
            }

            GameState.PiecePosition position =
                state.getPiecePosition(other);

            if (position.getZone()
                    == GameState.PieceZone.BOARD_PATH
                && position.getPath()
                    == GameState.BoardPathId.PATH_52
                && position.getIndex() == squareIndex) {

                int count = counts.merge(
                    other.getPlayerColor(), 1, Integer::sum);

                if (count >= 2) {
                    return true;
                }
            }
        }

        return false;
    }
    
    // Moves a piece according to the Easy game rules.
    // Returns true if the movement was performed.
    public boolean movePieceEasy(
            Piece piece,
            Player currentPlayer,
            int diceResult,
            EasyBoard board,
            GameState state) {

        if (piece == null || currentPlayer == null
            || board == null || state == null) {
            return false;
        }

        if (!isValidDiceResult(diceResult)) {
            return false;
        }

        if (!"Easy".equalsIgnoreCase(
                state.getConfiguration()
                    .getDifficulty().trim())) {
            return false;
        }

        if (!belongsToPlayer(piece, currentPlayer)) {
            return false;
        }

        if (!state.getPieces().contains(piece)) {
            return false;
        }

        GameState.PiecePosition position =
            state.getPiecePosition(piece);

        int steps = calculateEasySteps(diceResult);

        switch (position.getZone()) {

            case CASTLE:
                return moveFromCastle(piece, steps, board, state);

            case BOARD_PATH:
                return moveOnBoard(piece, steps, board, state);

            case HOME_PATH:
                return moveOnHomePath(piece, steps, state);

            case FINISHED:
                return false;

            default:
                return false;
        }
    }

    // Moves a piece from its castle to its starting square.
    private boolean moveFromCastle(
            Piece piece,
            int steps,
            EasyBoard board,
            GameState state) {

        if (!canExitCastle(steps)) {
            return false;
        }

        Square startSquare = findStartSquare(board, piece);

        if (hasOpposingDoubleBlock(
                piece, startSquare.getIndex(), state)) {
            return false;
        }

        state.setBoardPathPosition(
            piece,
            GameState.BoardPathId.PATH_52,
            startSquare.getIndex()
        );

        return true;
    }

    // Moves a piece clockwise on the Easy board.
    private boolean moveOnBoard(
            Piece piece,
            int steps,
            EasyBoard board,
            GameState state) {

        GameState.PiecePosition position =
            state.getPiecePosition(piece);

        if (position.getPath()
                != GameState.BoardPathId.PATH_52) {
            return false;
        }

        int currentIndex = position.getIndex();

        Square homeArrow = findHomeArrow(board, piece);

        int distanceToArrow = Math.floorMod(
            homeArrow.getIndex() - currentIndex,
            EasyBoard.PATH_LENGTH
        );

        // Movement remains on the common path.
        if (steps <= distanceToArrow) {

            int newIndex = (
                currentIndex + steps
            ) % EasyBoard.PATH_LENGTH;

            state.setBoardPathPosition(
                piece,
                GameState.BoardPathId.PATH_52,
                newIndex
            );

            return true;
        }

        // Movement continues into the home path.
        int remainingSteps = steps - distanceToArrow;

        if (remainingSteps <= HomePath.LENGTH) {

            state.setHomePathPosition(
                piece,
                remainingSteps - 1
            );

            return true;
        }

        // Exact number of steps to finish.
        if (remainingSteps == HomePath.LENGTH + 1) {
            state.markPieceFinished(piece);
            return true;
        }

        // Overshooting the finish is not allowed.
        return false;
    }

    // Moves a piece along its personal home path.
    private boolean moveOnHomePath(
            Piece piece,
            int steps,
            GameState state) {

        int currentIndex = state.getPiecePosition(piece)
            .getIndex();

        int newIndex = currentIndex + steps;

        if (newIndex < HomePath.LENGTH) {
            state.setHomePathPosition(piece, newIndex);
            return true;
        }

        if (newIndex == HomePath.LENGTH) {
            state.markPieceFinished(piece);
            return true;
        }

        return false;
    }
}
