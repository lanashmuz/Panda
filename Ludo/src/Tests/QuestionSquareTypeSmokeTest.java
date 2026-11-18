
package Tests;

import Model.Square;
import Model.SquareType;

/**
 * Tests question square identification and difficulty mapping.
 */
public class QuestionSquareTypeSmokeTest {

	/**
	 * Verifies question square types and rejects regular squares.
	 *
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {

		checkQuestionSquare(SquareType.QUESTION_EASY, "Easy");
		checkQuestionSquare(SquareType.QUESTION_MEDIUM, "Medium");
		checkQuestionSquare(SquareType.QUESTION_HARD, "Hard");

		Square regularSquare = new Square(10, SquareType.REGULAR, null);

		if (regularSquare.getType().isQuestionSquare()) {
			throw new AssertionError("Regular square must not trigger a question.");
		}

		boolean rejected = false;

		try {
			regularSquare.getType().getQuestionDifficulty();
		} catch (IllegalStateException expected) {
			rejected = true;
		}

		if (!rejected) {
			throw new AssertionError("Regular square must not have question difficulty.");
		}

		System.out.println("ALL QUESTION SQUARE TESTS PASSED");
	}

	/**
	 * Verifies that a question square has the expected difficulty.
	 *
	 * @param type               square type to validate
	 * @param expectedDifficulty expected question difficulty
	 */
	private static void checkQuestionSquare(SquareType type, String expectedDifficulty) {

		Square square = new Square(10, type, null);

		if (!square.getType().isQuestionSquare()) {
			throw new AssertionError(type + " was not identified as a question square.");
		}

		String actualDifficulty = square.getType().getQuestionDifficulty();

		if (!expectedDifficulty.equals(actualDifficulty)) {
			throw new AssertionError("Expected " + expectedDifficulty + ", got " + actualDifficulty);
		}

		System.out.println("PASS: " + type);
	}
}