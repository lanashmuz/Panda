
package Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Randomly places question squares on available board positions.
 */
public class QuestionSquarePlacement {

    /**
     * Places an equal number of easy, medium, and hard question squares.
     * Existing special squares remain unchanged.
     *
     * @param squares board squares to process
     * @param countPerDifficulty number of squares for each question difficulty
     * @return a new list containing the question squares
     * @throws IllegalArgumentException if there are insufficient available squares
     */
    public List<Square> placeQuestions(
            List<Square> squares, int countPerDifficulty) {

        if (squares == null || countPerDifficulty < 0) {
            throw new IllegalArgumentException(
                    "Invalid question placement parameters.");
        }

        List<Square> result = new ArrayList<>(squares);
        List<Integer> available = new ArrayList<>();

        for (int i = 0; i < result.size(); i++) {
            if (result.get(i).getType() == SquareType.REGULAR) {
                available.add(i);
            }
        }

        int required = countPerDifficulty * 3;

        if (available.size() < required) {
            throw new IllegalArgumentException(
                    "Not enough regular squares for questions.");
        }

        Collections.shuffle(available);

        SquareType[] questionTypes = {
                SquareType.QUESTION_EASY,
                SquareType.QUESTION_MEDIUM,
                SquareType.QUESTION_HARD
        };

        int nextPosition = 0;

        for (SquareType type : questionTypes) {
            for (int i = 0; i < countPerDifficulty; i++) {

                int listIndex = available.get(nextPosition++);
                Square original = result.get(listIndex);

                result.set(listIndex, new Square(
                        original.getIndex(),
                        type,
                        original.getPlayerColor()
                ));
            }
        }

        return List.copyOf(result);
    }
}
