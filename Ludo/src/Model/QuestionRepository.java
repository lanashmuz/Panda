
package Model;

import java.util.List;

/**
 * Defines a common interface for accessing educational questions.
 */
public interface QuestionRepository {

    List<Question> getQuestionsByDifficulty(String difficulty);
}
