
package Tests;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import Model.ExcelQuestionAdapter;
import Model.Question;
import Model.QuestionRepository;

// Verifies Excel question loading and difficulty filtering.
 
public class ExcelQuestionAdapterSmokeTest {

    public static void main(String[] args) throws Exception {

        Path filePath = Path.of("..", "question_Panda.xlsx");

        QuestionRepository repository = new ExcelQuestionAdapter(
                Files.newInputStream(filePath));

        for (String difficulty : List.of("Easy", "Medium", "Hard")) {

            List<Question> questions =
                    repository.getQuestionsByDifficulty(difficulty);

            if (questions.isEmpty()) {
                throw new AssertionError(
                        "No questions found for: " + difficulty);
            }

            // Validate the loaded questions.
            for (Question question : questions) {
                if (!question.getDifficulty().equalsIgnoreCase(difficulty)
                        || question.getOptions().size() != 4
                        || question.getCorrectAnswerIndex() < 0
                        || question.getCorrectAnswerIndex() > 3) {
                    throw new AssertionError(
                            "Invalid question: " + question.getQuestionId());
                }
            }

            System.out.println("PASS: " + difficulty + " questions validated");
        }

        System.out.println("PASS: Excel adapter validation completed");
    }
}
