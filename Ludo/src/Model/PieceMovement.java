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
			throw new IllegalArgumentException("Dice result must be between 1 and 6");
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

	// finds the starting square of the selected piece
	public Square findStartSquare(EasyBoard board, Piece piece) {

		if (board == null || piece == null) {
			throw new IllegalArgumentException("Board and piece cannot be null");
		}

		for (Square square : board.getCommonPath()) {
			if (square.getType() == SquareType.START && piece.getPlayerColor().equals(square.getPlayerColor())) {
				return square;
			}
		}

		throw new IllegalStateException("Starting square not found for " + piece.getPlayerColor());
	}

	// Finds the arrow leading to a player's home path.
	private Square findHomeArrow(EasyBoard board, Piece piece) {

		for (Square square : board.getCommonPath()) {
			if (square.getType() == SquareType.ARROW && piece.getPlayerColor().equals(square.getPlayerColor())) {
				return square;
			}
		}

		throw new IllegalStateException("Home arrow not found");
	}

	// Checks for an opponent's double block on a square.
	private boolean hasOpposingDoubleBlock(Piece piece, int squareIndex, GameState state) {

		Map<String, Integer> counts = new HashMap<>();

		for (Piece other : state.getPieces()) {

			if (other.getPlayerColor().equals(piece.getPlayerColor())) {
				continue;
			}

			GameState.PiecePosition position = state.getPiecePosition(other);

			if (position.getZone() == GameState.PieceZone.BOARD_PATH
					&& position.getPath() == GameState.BoardPathId.PATH_52 && position.getIndex() == squareIndex) {

				int count = counts.merge(other.getPlayerColor(), 1, Integer::sum);

				if (count >= 2) {
					return true;
				}
			}
		}

		return false;
	}

	private static class MoveTarget {

		private final GameState.PieceZone zone;
		private final int index;

		private MoveTarget(GameState.PieceZone zone, int index) {
			this.zone = zone;
			this.index = index;
		}
	}

	private MoveTarget resolveEasyMove(Piece piece, Player currentPlayer, int diceResult, EasyBoard board,
			GameState state) {

		if (piece == null || currentPlayer == null || board == null || state == null) {
			return null;
		}

		if (!isValidDiceResult(diceResult)) {
			return null;
		}

		if (!"Easy".equalsIgnoreCase(state.getConfiguration().getDifficulty().trim())) {
			return null;
		}

		if (!belongsToPlayer(piece, currentPlayer)) {
			return null;
		}

		if (!state.getPieces().contains(piece)) {
			return null;
		}

		GameState.PiecePosition position = state.getPiecePosition(piece);

		int steps = calculateEasySteps(diceResult);

		switch (position.getZone()) {

		case CASTLE:

			if (!canExitCastle(steps)) {
				return null;
			}

			Square startSquare = findStartSquare(board, piece);

			if (hasOpposingDoubleBlock(piece, startSquare.getIndex(), state)) {
				return null;
			}

			return new MoveTarget(GameState.PieceZone.BOARD_PATH, startSquare.getIndex());

		case BOARD_PATH:

			if (position.getPath() != GameState.BoardPathId.PATH_52) {
				return null;
			}

			int currentIndex = position.getIndex();
			Square homeArrow = findHomeArrow(board, piece);

			int distanceToArrow = Math.floorMod(homeArrow.getIndex() - currentIndex, EasyBoard.PATH_LENGTH);

			if (steps <= distanceToArrow) {
				int newIndex = (currentIndex + steps) % EasyBoard.PATH_LENGTH;

				return new MoveTarget(GameState.PieceZone.BOARD_PATH, newIndex);
			}

			int remainingSteps = steps - distanceToArrow;

			if (remainingSteps < HomePath.LENGTH) {
				return new MoveTarget(GameState.PieceZone.HOME_PATH, remainingSteps - 1);
			}

			if (remainingSteps == HomePath.LENGTH) {
				return new MoveTarget(GameState.PieceZone.FINISHED, -1);
			}

			return null;

		case HOME_PATH:

			int newHomeIndex = position.getIndex() + steps;

			if (newHomeIndex < HomePath.LENGTH - 1) {
				return new MoveTarget(GameState.PieceZone.HOME_PATH, newHomeIndex);
			}

			if (newHomeIndex == HomePath.LENGTH - 1) {
				return new MoveTarget(GameState.PieceZone.FINISHED, -1);
			}

			return null;

		case FINISHED:
		default:
			return null;
		}
	}

	//Checks whether the piece can move without changing its position.
	public boolean canMoveEasy(Piece piece, Player currentPlayer, int diceResult, EasyBoard board, GameState state) {

		return resolveEasyMove(piece, currentPlayer, diceResult, board, state) != null;
	}
	//Performs a legal Easy move and updates GameState.
	public boolean movePieceEasy(Piece piece, Player currentPlayer, int diceResult, EasyBoard board, GameState state) {

		MoveTarget target = resolveEasyMove(piece, currentPlayer, diceResult, board, state);

		if (target == null) {
			return false;
		}

		switch (target.zone) {

		case BOARD_PATH:
			state.setBoardPathPosition(piece, GameState.BoardPathId.PATH_52, target.index);
			break;

		case HOME_PATH:
			state.setHomePathPosition(piece, target.index);
			break;

		case FINISHED:
			state.markPieceFinished(piece);
			break;

		default:
			return false;
		}

		return true;
	}

}
