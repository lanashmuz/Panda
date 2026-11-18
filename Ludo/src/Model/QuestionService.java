
package Model;

import java.util.List;
import java.util.Random;

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
            throw new IllegalStateException(
                    "No questions available for difficulty: " + difficulty);
        }

        return questions.get(random.nextInt(questions.size()));
    }
}
