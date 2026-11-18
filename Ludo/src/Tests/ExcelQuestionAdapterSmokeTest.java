
package Tests;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import Model.ExcelQuestionAdapter;
import Model.Question;
import Model.QuestionRepository;

//Verifies Excel question loading and difficulty filtering.
 
public class ExcelQuestionAdapterSmokeTest {

    public static void main(String[] args) throws Exception {

        Path filePath = Path.of("..", "question_Panda.xlsx");

        QuestionRepository repository = new ExcelQuestionAdapter(
                Files.newInputStream(filePath));

        List<Question> easy = repository.getQuestionsByDifficulty("Easy");
        List<Question> medium = repository.getQuestionsByDifficulty("Medium");
        List<Question> hard = repository.getQuestionsByDifficulty("Hard");

        // Verify the number of questions in each difficulty.
        if (easy.size() != 3 || medium.size() != 3 || hard.size() != 1) {
            throw new AssertionError("Incorrect question counts.");
        }

        System.out.println("PASS: Questions loaded and filtered");

        // Verify Excel data conversion for question Q01.
        Question q1 = easy.get(0);

        if (!q1.getQuestionId().equals("Q01")
                || q1.getOptions().size() != 4
                || q1.getCorrectAnswerIndex() != 1) {
            throw new AssertionError("Incorrect question data.");
        }

        System.out.println("PASS: Excel data converted correctly");
    }
}
