
package Tests;

import java.util.List;
import Model.Question;

public class QuestionSmokeTest {

    public static void main(String[] args) {

        Question question = new Question(
                "Q01",
                "What does MVC stand for?",
                List.of("Main View Class", "Model View Controller",
                        "Multiple View Connection", "Model Visual Code"),
                1,
                "MVC separates the application into three components.",
                "Easy",
                "Software Design",
                "Lecture"
        );
        

     // Verify correct and incorrect answers.
     if (!question.isCorrectAnswer(1) || question.isCorrectAnswer(0)) {
         throw new AssertionError("Answer validation failed.");
     }

     System.out.println("PASS: Answer validation");


        // Verify that the question stores the correct data.
        if (!question.getQuestionId().equals("Q01")
                || question.getCorrectAnswerIndex() != 1
                || question.getOptions().size() != 4) {
            throw new AssertionError("Question data is incorrect.");
        }

        System.out.println("PASS: Valid question");

        // Verify that invalid answer options are rejected.
        try {
            new Question("Q02", "Test question",
                    List.of("A", "B", "C"), 0,
                    "", "Easy", "Test", "Test");

            throw new AssertionError("Invalid question was accepted.");
        } catch (IllegalArgumentException e) {
            System.out.println("PASS: Invalid options rejected");
        }
    }
}
