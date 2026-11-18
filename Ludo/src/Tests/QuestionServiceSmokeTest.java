
package Tests;

import java.util.List;
import Model.Question;
import Model.QuestionRepository;
import Model.QuestionService;

public class QuestionServiceSmokeTest {

    public static void main(String[] args) {

        Question q1 = new Question(
                "Q01", "What does MVC stand for?",
                List.of("A", "B", "C", "D"),
                1, "MVC explanation", "Easy", "MVC", "Lecture");

        Question q2 = new Question(
                "Q02", "What is Agile?",
                List.of("A", "B", "C", "D"),
                0, "Agile explanation", "Easy", "Agile", "Lecture");

        // Use a temporary repository to test the service without Excel.
        QuestionRepository repository = new QuestionRepository() {
            @Override
            public List<Question> getQuestionsByDifficulty(String difficulty) {
                if ("Easy".equalsIgnoreCase(difficulty)) {
                    return List.of(q1, q2);
                }
                return List.of();
            }
        };

        QuestionService service = new QuestionService(repository);

        // Verify that only questions of the requested difficulty are selected.
        for (int i = 0; i < 20; i++) {
            Question selected = service.getRandomQuestion("Easy");

            if (!selected.getDifficulty().equals("Easy")) {
                throw new AssertionError("Incorrect question difficulty.");
            }
        }

        System.out.println("PASS: Questions selected by difficulty");

        // Verify that an empty question pool is handled correctly.
        try {
            service.getRandomQuestion("Hard");
            throw new AssertionError("Empty question pool was accepted.");
        } catch (IllegalStateException e) {
            System.out.println("PASS: Empty question pool rejected");
        }
    }
}
