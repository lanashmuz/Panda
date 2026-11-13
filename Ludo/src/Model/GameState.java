package Model;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

// manages the current positions of all game pieces
// supports Easy, Medium, and Hard difficulty levels
public class GameState {

	// represents the area where a piece is located
	public enum PieceZone {
		CASTLE, BOARD_PATH, HOME_PATH, FINISHED
	}

	// identifies the different board paths
	public enum BoardPathId {
		PATH_52(52), PATH_64(64), PATH_72(72);

		private final int length;

		BoardPathId(int length) {
			this.length = length;
		}

		public int getLength() {
			return length;
		}
	}

	private final GameConfiguration configuration; // stores the game configuration
	private Integer pendingDiceResult; // Stores the dice result awaiting a legal piece move. Null means no dice result is currently pending.
	private final Map<Piece, PiecePosition> piecePositions = new HashMap<>(); // maps each piece to its current position

	
	// represents the current position of a piece
	public static class PiecePosition {

		private final PieceZone zone;
		private final BoardPathId path;
		private final int index;

		private PiecePosition(PieceZone zone, BoardPathId path, int index) {

			this.zone = zone;
			this.path = path;
			this.index = index;
		}

		public PieceZone getZone() {
			return zone;
		}

		public BoardPathId getPath() {
			return path;
		}

		public int getIndex() {
			return index;
		}
	}

	
	// initializes the game state with all pieces in their castles
	public GameState(GameConfiguration configuration, List<Castle> castles) {

		if (configuration == null || castles == null) {
			throw new IllegalArgumentException("Configuration and castles cannot be null");
		}

		this.configuration = configuration;

		for (Castle castle : castles) {

			if (castle == null) {
				throw new IllegalArgumentException("Castle cannot be null");
			}

			for (Piece piece : castle.getPieces()) {

				piecePositions.put(piece, new PiecePosition(PieceZone.CASTLE, null, -1));
			}
		}
	}

	// returns the game configuration
	public GameConfiguration getConfiguration() {
		return configuration;
	}

	// Returns the dice result waiting to be used for movement.
	public Integer getPendingDiceResult() {
		return pendingDiceResult;
	}

	// Stores a valid dice result for the next piece movement.
	public void setPendingDiceResult(int result) {
		if (result < 1 || result > 6) {
			throw new IllegalArgumentException("Dice result must be between 1 and 6");
		}

		this.pendingDiceResult = result;
	}

	// Clears the pending dice result after the move or turn ends.
	public void clearPendingDiceResult() {
		this.pendingDiceResult = null;
	}

	// returns all pieces registered in the game
	public List<Piece> getPieces() {
		return List.copyOf(piecePositions.keySet());
	}

	// returns the current position of a piece
	public PiecePosition getPiecePosition(Piece piece) {

		PiecePosition position = piecePositions.get(piece);

		if (position == null) {
			throw new IllegalArgumentException("Piece does not belong to this game");
		}

		return position;
	}

	// checks whether a piece is currently in its castle
	public boolean isInCastle(Piece piece) {

		return getPiecePosition(piece).getZone() == PieceZone.CASTLE;
	}

	// checks whether a board path is allowed
	// for the selected difficulty level
	private boolean isPathAllowed(BoardPathId path) {

		if (path == null) {
			return false;
		}

		String difficulty = configuration.getDifficulty();

		if (difficulty == null) {
			throw new IllegalStateException("Game difficulty is not defined");
		}

		switch (difficulty.trim().toLowerCase(Locale.ROOT)) {

		case "easy":
			return path == BoardPathId.PATH_52;

		case "medium":
			return path == BoardPathId.PATH_52 || path == BoardPathId.PATH_64;

		case "hard":
			return true;

		default:
			throw new IllegalStateException("Unknown difficulty: " + difficulty);
		}
	}

	// updates a piece's position on a board path
	public void setBoardPathPosition(Piece piece, BoardPathId path, int index) {

		getPiecePosition(piece);

		if (!isPathAllowed(path)) {
			throw new IllegalArgumentException("Board path is not allowed for this difficulty");
		}

		if (index < 0 || index >= path.getLength()) {
			throw new IllegalArgumentException("Invalid board path index");
		}

		piecePositions.put(piece, new PiecePosition(PieceZone.BOARD_PATH, path, index));
	}

	// updates a piece's position on its home path
	public void setHomePathPosition(Piece piece, int index) {

		getPiecePosition(piece);

		if (index < 0 || index >= HomePath.LENGTH - 1) {
			throw new IllegalArgumentException("Invalid home path index");
		}

		piecePositions.put(piece, new PiecePosition(PieceZone.HOME_PATH, null, index));
	}

	// returns a piece to its castle
	public void returnToCastle(Piece piece) {

		getPiecePosition(piece);

		piecePositions.put(piece, new PiecePosition(PieceZone.CASTLE, null, -1));
	}

	// marks a piece as finished
	public void markPieceFinished(Piece piece) {

		getPiecePosition(piece);

		piecePositions.put(piece, new PiecePosition(PieceZone.FINISHED, null, -1));
	}
}
