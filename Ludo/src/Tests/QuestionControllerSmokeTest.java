
package Tests;

import java.util.List;
import javax.swing.SwingUtilities;

import Controller.QuestionController;
import Model.Question;
import Model.QuestionService;

/**
 * Tests the question controller's interaction flow
 * and resulting movement adjustment.
 */
public class QuestionControllerSmokeTest {

    /**
     * Runs a question and verifies the movement result.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            Question question = new Question(
                    "Q01",
                    "What does MVC stand for?",
                    List.of(
                            "Main View Class",
                            "Model View Controller",
                            "Multiple View Connection",
                            "Model Visual Code"
                    ),
                    1,
                    "MVC separates the application into three components.",
                    "Easy",
                    "Software Design",
                    "Lecture"
            );

            QuestionService service = new QuestionService(
                    difficulty -> List.of(question));

            QuestionController controller = new QuestionController(service);

            int steps = controller.askQuestion(null, "Easy");

            if (Math.abs(steps) != 1) {
                throw new AssertionError(
                        "Expected movement of 1 step, got: " + steps);
            }

            System.out.println("Question controller test passed.");
            System.out.println("Movement adjustment: " + steps);
        });
    }
}
