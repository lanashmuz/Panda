
package Controller;

import java.awt.Window;
import java.util.Objects;

import Model.Question;
import Model.QuestionService;
import View.ExitConfirmation;
import View.QuestionDialog;

/**
 * Coordinates question presentation, answer validation,
 * and movement feedback.
 */
public class QuestionController {

    private final QuestionService questionService;

    /**
     * Creates a controller with the provided question service.
     *
     * @param questionService service responsible for question logic
     */
    public QuestionController(QuestionService questionService) {
        this.questionService = Objects.requireNonNull(questionService);
    }

    /**
     * Presents a question and returns the resulting movement adjustment.
     *
     * @param owner parent window of the question dialog
     * @param difficulty difficulty of the question tile
     * @return positive steps for a correct answer, negative for an incorrect answer
     */
    public int askQuestion(Window owner, String difficulty) {

        Question question = questionService.getRandomQuestion(difficulty);

        QuestionDialog dialog = new QuestionDialog(owner, question);
        dialog.setVisible(true);

        try {
            int selectedIndex = dialog.getSelectedAnswerIndex();

            if (selectedIndex == -1) {
                throw new IllegalStateException(
                        "Question closed without an answer.");
            }

            boolean correct = question.isCorrectAnswer(selectedIndex);

            int steps = questionService.calculateMovementSteps(
                    question, selectedIndex);

            String stepWord = Math.abs(steps) == 1
                    ? " step" : " steps";

            String message;

            if (correct) {
                message = "Your piece moves forward "
                        + Math.abs(steps) + stepWord + ".";
            } else {
                String correctAnswer = question.getOptions()
                        .get(question.getCorrectAnswerIndex());

                correctAnswer = correctAnswer
                        .replace("&", "&amp;")
                        .replace("<", "&lt;")
                        .replace(">", "&gt;");

                message = "<html>Your piece moves back "
                        + Math.abs(steps) + stepWord + "."
                        + "<br>Correct answer: "
                        + correctAnswer
                        + "</html>";
            }

            ExitConfirmation.showAnswerFeedback(
                    dialog, correct, message);

            return steps;

        } finally {
            dialog.dispose();
        }
    }
}
