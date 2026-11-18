
package Tests;

import java.util.List;
import javax.swing.SwingUtilities;

import Model.Question;
import Model.QuestionService;
import View.ExitConfirmation;
import View.QuestionDialog;

/**
 * Tests question display, answer selection, and feedback.
 */
public class QuestionDialogSmokeTest {

	public static void main(String[] args) {

		SwingUtilities.invokeLater(() -> {

			Question question = new Question("Q01", "What does MVC stand for?",
					List.of("Main View Class", "Model View Controller", "Multiple View Connection",
							"Model Visual Code"),
					1, "MVC separates the application into three components.", "Easy", "Software Design", "Lecture");

			QuestionDialog dialog = new QuestionDialog(null, question);
			dialog.setVisible(true);

			int selectedIndex = dialog.getSelectedAnswerIndex();

			if (selectedIndex == -1) {
				throw new AssertionError("No answer was submitted.");
			}

			System.out.println("Selected answer index: " + selectedIndex);

			QuestionService service = new QuestionService(difficulty -> List.of(question));

			boolean correct = question.isCorrectAnswer(selectedIndex);
			int steps = service.calculateMovementSteps(question, selectedIndex);

			String stepWord = Math.abs(steps) == 1 ? " step" : " steps";
			String movement = (correct ? "Move forward " : "Move back ") + Math.abs(steps) + stepWord + ".";

			String message;

			if (correct) {
				message = "Your piece moves forward " + Math.abs(steps) + stepWord + ".";
			} else {
				message = "<html>Your piece moves back " + Math.abs(steps) + stepWord + "." + "<br>Correct answer: "
						+ question.getOptions().get(question.getCorrectAnswerIndex()) + "</html>";
			}

			// Display feedback using the game's existing dialog style.

			ExitConfirmation.showAnswerFeedback(dialog, correct, message);

			dialog.dispose();

		});
	}
}
