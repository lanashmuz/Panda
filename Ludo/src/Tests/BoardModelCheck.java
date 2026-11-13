package Tests;

import Model.Castle;
import Model.EasyBoard;
import Model.GameConfiguration;
import Model.HomePath;
import Model.Piece;
import Model.Player;
import Model.Square;
import Model.SquareType;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BoardModelCheck {

	public static void main(String[] args) {
		String[] colors = { "Blue", "Red", "Yellow", "Green" };

		for (int playerCount = 2; playerCount <= 4; playerCount++) {
			List<Player> players = new ArrayList<>();
			Set<String> expectedColors = new HashSet<>();

			for (int i = 0; i < playerCount; i++) {
				players.add(new Player("Player " + i, colors[i]));
				expectedColors.add(colors[i]);
			}

			GameConfiguration configuration = new GameConfiguration(players, "Easy");

			Set<List<Integer>> safeLayouts = new HashSet<>();

			for (int run = 0; run < 100; run++) {
				EasyBoard board = new EasyBoard(configuration);
				safeLayouts.add(checkBoard(board, expectedColors));
			}

			check(safeLayouts.size() > 1, "Safe layout did not change across 100 boards.");

			System.out.println("PASS: " + playerCount + " players / 100 boards");
		}

		System.out.println("ALL MODEL CHECKS PASSED");
	}

	private static List<Integer> checkBoard(EasyBoard board, Set<String> expectedColors) {
		List<Square> path = board.getCommonPath();

		check(path.size() == 52, "Common path must contain 52 squares.");
		check(countType(path, SquareType.START) == 4, "Expected 4 START.");
		check(countType(path, SquareType.ARROW) == 4, "Expected 4 ARROW.");
		check(countType(path, SquareType.SAFE) == 4, "Expected 4 SAFE.");
		check(countType(path, SquareType.REGULAR) == 40, "Expected 40 REGULAR.");

		List<Integer> safeIndices = new ArrayList<>();

		for (int i = 0; i < path.size(); i++) {
			Square square = path.get(i);

			check(square.getIndex() == i, "Wrong square index at " + i);

			if (square.getType() == SquareType.SAFE) {
				safeIndices.add(i);
			}

			if (square.getType() == SquareType.REGULAR || square.getType() == SquareType.SAFE) {
				check(square.getPlayerColor() == null, "REGULAR/SAFE square must not belong to a player.");
			}
		}

		String[] fixedColors = { "Green", "Yellow", "Blue", "Red" };
		int[] starts = { 0, 13, 26, 39 };
		int[] arrows = { 50, 11, 24, 37 };

		for (int i = 0; i < fixedColors.length; i++) {
			checkFixed(path, starts[i], SquareType.START, fixedColors[i]);
			checkFixed(path, arrows[i], SquareType.ARROW, fixedColors[i]);
		}

		check(board.getCastles().size() == expectedColors.size(), "Wrong number of castles.");

		Set<String> castleColors = new HashSet<>();

		for (Castle castle : board.getCastles()) {
			String color = castle.getPlayerColor();

			check(expectedColors.contains(color) && castleColors.add(color), "Unexpected or duplicate castle color.");

			check(castle.getPieces().size() == 4, "Expected 4 castle pieces.");

			Set<Integer> pieceIds = new HashSet<>();

			for (Piece piece : castle.getPieces()) {
				check(color.equals(piece.getPlayerColor()), "Piece color does not match castle.");

				check(piece.getId() >= 0 && piece.getId() < 4 && pieceIds.add(piece.getId()),
						"Invalid or duplicate piece ID.");
			}
		}

		check(board.getHomePaths().size() == expectedColors.size(), "Wrong number of home paths.");

		Set<String> homeColors = new HashSet<>();

		for (HomePath homePath : board.getHomePaths()) {
			String color = homePath.getPlayerColor();

			check(expectedColors.contains(color) && homeColors.add(color), "Unexpected or duplicate home-path color.");

			check(homePath.getSquares().size() == 6, "Expected 6 HOME squares.");

			for (int i = 0; i < homePath.getSquares().size(); i++) {
				Square square = homePath.getSquares().get(i);

				check(square.getIndex() == i && square.getType() == SquareType.HOME
						&& color.equals(square.getPlayerColor()), "Invalid home square.");
			}
		}

		return List.copyOf(safeIndices);
	}

	private static int countType(List<Square> squares, SquareType type) {
		int count = 0;

		for (Square square : squares) {
			if (square.getType() == type) {
				count++;
			}
		}

		return count;
	}

	private static void checkFixed(List<Square> squares, int index, SquareType type, String color) {
		Square square = squares.get(index);

		check(square.getType() == type && color.equals(square.getPlayerColor()),
				"Wrong fixed square at index " + index);
	}

	private static void check(boolean condition, String message) {
		if (!condition) {
			throw new IllegalStateException(message);
		}
	}
}
