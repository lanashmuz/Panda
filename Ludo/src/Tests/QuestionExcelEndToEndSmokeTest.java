
package Tests;

import java.io.FileInputStream;
import java.io.IOException;

import javax.swing.SwingUtilities;

import Controller.QuestionController;
import Model.ExcelQuestionAdapter;
import Model.QuestionRepository;
import Model.QuestionService;

/**
 * Tests the complete question flow using the Excel repository.
 */
public class QuestionExcelEndToEndSmokeTest {

    /**
     * Loads questions from Excel and verifies the controller result.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            try (FileInputStream input =
                    new FileInputStream("../question_Panda.xlsx")) {

                QuestionRepository repository =
                        new ExcelQuestionAdapter(input);

                QuestionService service =
                        new QuestionService(repository);

                QuestionController controller =
                        new QuestionController(service);

                int steps = controller.askQuestion(null, "Easy");

                if (Math.abs(steps) != 1) {
                    throw new AssertionError(
                            "Expected a movement adjustment of 1 step.");
                }

                System.out.println("PASS: Excel question controller integration");
                System.out.println("Movement adjustment: " + steps);

            } catch (IOException e) {
                throw new IllegalStateException(
                        "Failed to load the Excel question repository.", e);
            }
        });
    }
}
