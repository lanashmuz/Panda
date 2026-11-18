
package Model;

import java.util.List;
import java.util.Random;
import java.util.Locale;
// Handles question selection based on difficulty.

public class QuestionService {

	private final QuestionRepository repository;
	private final Random random;

	public QuestionService(QuestionRepository repository) {
		this.repository = repository;
		this.random = new Random();
	}

	public Question getRandomQuestion(String difficulty) {
		List<Question> questions = repository.getQuestionsByDifficulty(difficulty);

		if (questions == null || questions.isEmpty()) {
			throw new IllegalStateException("No questions available for difficulty: " + difficulty);
		}

		return questions.get(random.nextInt(questions.size()));
	}

	// Calculates movement steps based on the question difficulty and whether the selected answer is correct.
	
	public int calculateMovementSteps(Question question, int selectedAnswerIndex) {

		if (question == null) {
			throw new IllegalArgumentException("Question cannot be null.");
		}

		if (selectedAnswerIndex < 0 || selectedAnswerIndex >= question.getOptions().size()) {
			throw new IllegalArgumentException("Invalid answer index.");
		}

		int steps = switch (question.getDifficulty().trim().toLowerCase(Locale.ROOT)) {
		case "easy" -> 1;
		case "medium" -> 2;
		case "hard" -> 3;
		default -> throw new IllegalArgumentException("Invalid question difficulty.");
		};

		return question.isCorrectAnswer(selectedAnswerIndex) ? steps : -steps;
	}

}
