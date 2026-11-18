
package Model;

/**
 * Defines the different types of squares on the Ludo board.
 */
public enum SquareType {

	REGULAR, START, ARROW, SAFE, HOME, QUESTION_EASY, QUESTION_MEDIUM, QUESTION_HARD;

	/**
	 * Checks whether this square triggers a question.
	 *
	 * @return true if the square is a question square
	 */
	public boolean isQuestionSquare() {
		return this == QUESTION_EASY || this == QUESTION_MEDIUM || this == QUESTION_HARD;
	}

	/**
	 * Returns the difficulty associated with a question square.
	 *
	 * @return Easy, Medium, or Hard
	 * @throws IllegalStateException if this is not a question square
	 */
	public String getQuestionDifficulty() {
		return switch (this) {
		case QUESTION_EASY -> "Easy";
		case QUESTION_MEDIUM -> "Medium";
		case QUESTION_HARD -> "Hard";
		default -> throw new IllegalStateException("Square does not contain a question.");
		};
	}
}