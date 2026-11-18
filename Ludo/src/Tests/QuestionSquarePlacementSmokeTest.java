
package Tests;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Model.QuestionSquarePlacement;
import Model.Square;
import Model.SquareType;

/**
 * Verifies question square placement and board preservation.
 */
public class QuestionSquarePlacementSmokeTest {

    /**
     * Runs placement checks for medium and hard game boards.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        QuestionSquarePlacement placement = new QuestionSquarePlacement();

        List<Square> original = createSampleSquares();
        List<Square> snapshot = List.copyOf(original);

        List<Square> medium = placement.placeQuestions(original, 2);
        verifyPlacement(snapshot, medium, 2);
        System.out.println("PASS: Medium - 6 question squares");

        List<Square> hard = placement.placeQuestions(original, 3);
        verifyPlacement(snapshot, hard, 3);
        System.out.println("PASS: Hard - 9 question squares");

        for (int i = 0; i < original.size(); i++) {
            check(original.get(i) == snapshot.get(i),
                    "Original board was modified.");
        }
        System.out.println("PASS: Original board unchanged");

        expectInvalid(() ->
                placement.placeQuestions(original.subList(0, 8), 2));
        System.out.println("PASS: Insufficient squares rejected");

        expectInvalid(() -> placement.placeQuestions(original, -1));
        expectInvalid(() -> placement.placeQuestions(null, 2));
        System.out.println("PASS: Invalid input rejected");

        System.out.println("ALL QUESTION PLACEMENT TESTS PASSED");
    }

    /**
     * Creates sample squares containing regular and special positions.
     *
     * @return sample board squares
     */
    private static List<Square> createSampleSquares() {

        List<Square> squares = new ArrayList<>();

        for (int i = 0; i < 24; i++) {

            SquareType type = switch (i) {
                case 0 -> SquareType.START;
                case 1 -> SquareType.ARROW;
                case 2 -> SquareType.SAFE;
                case 3 -> SquareType.HOME;
                default -> SquareType.REGULAR;
            };

            squares.add(new Square(i, type, null));
        }

        return squares;
    }

    /**
     * Verifies question counts and preservation of special squares.
     *
     * @param original original board layout
     * @param result board layout after placement
     * @param expectedPerDifficulty expected count for each question type
     */
    private static void verifyPlacement(
            List<Square> original,
            List<Square> result,
            int expectedPerDifficulty) {

        check(original.size() == result.size(),
                "Board size changed.");

        int easy = 0;
        int medium = 0;
        int hard = 0;

        for (int i = 0; i < original.size(); i++) {

            Square before = original.get(i);
            Square after = result.get(i);

            check(before.getIndex() == after.getIndex(),
                    "Square index changed.");

            check(Objects.equals(
                    before.getPlayerColor(), after.getPlayerColor()),
                    "Square color changed.");

            if (before.getType() != SquareType.REGULAR) {
                check(before.getType() == after.getType(),
                        "Special square was overwritten.");
            }

            switch (after.getType()) {
                case QUESTION_EASY -> easy++;
                case QUESTION_MEDIUM -> medium++;
                case QUESTION_HARD -> hard++;
                default -> { }
            }
        }

        check(easy == expectedPerDifficulty,
                "Incorrect easy question count.");

        check(medium == expectedPerDifficulty,
                "Incorrect medium question count.");

        check(hard == expectedPerDifficulty,
                "Incorrect hard question count.");
    }

    /**
     * Verifies that invalid placement requests are rejected.
     *
     * @param action placement operation expected to fail
     */
    private static void expectInvalid(Runnable action) {

        try {
            action.run();
        } catch (IllegalArgumentException expected) {
            return;
        }

        throw new AssertionError(
                "Expected IllegalArgumentException.");
    }

    /**
     * Fails the test when a condition is false.
     *
     * @param condition condition to verify
     * @param message failure description
     */
    private static void check(boolean condition, String message) {

        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
