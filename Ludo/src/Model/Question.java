
package Model;

import java.util.List;

/**
 * Represents an educational question in the Ludo game.
 */
public class Question {

	private String questionId;
	private String questionText;
	private List<String> options;
	private int correctAnswerIndex;
	private String explanation;
	private String difficulty;
	private String topic;
	private String source;

	public Question(String questionId, String questionText, List<String> options, int correctAnswerIndex,
			String explanation, String difficulty, String topic, String source) {
		// Validates that the question has exactly four options and a valid correct answer index
		if (options == null || options.size() != 4) {
			throw new IllegalArgumentException("A question must have exactly four answer options.");
		}

		if (correctAnswerIndex < 0 || correctAnswerIndex > 3) {
			throw new IllegalArgumentException("Correct answer index must be between 0 and 3.");
		}

		this.questionId = questionId;
		this.questionText = questionText;
		this.options = List.copyOf(options);
		this.correctAnswerIndex = correctAnswerIndex;
		this.explanation = explanation;
		this.difficulty = difficulty;
		this.topic = topic;
		this.source = source;
	}
	

public String getQuestionId() {
    return questionId;
}

public String getQuestionText() {
    return questionText;
}

public List<String> getOptions() {
    return options;
}

public int getCorrectAnswerIndex() {
    return correctAnswerIndex;
}

public String getExplanation() {
    return explanation;
}

public String getDifficulty() {
    return difficulty;
}

public String getTopic() {
    return topic;
}

public String getSource() {
    return source;
}


}
