
package Tests;

import java.time.LocalDateTime;
import java.util.List;

import Model.GameResult;

/**
 * Verifies game result data and input validation.
 */
public class GameResultSmokeTest {

    /**
     * Runs game result validation checks.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        LocalDateTime timestamp =
                LocalDateTime.of(2026, 11, 18, 14, 30);

        GameResult twoPlayers = new GameResult(
                1, "Easy", timestamp,
                List.of("Alice", "Bob"), 600);

        check(twoPlayers.getGameId() == 1, "Invalid game ID.");
        check(twoPlayers.getDifficulty().equals("easy"),
                "Invalid difficulty.");
        check(twoPlayers.getTimestamp().equals(timestamp),
                "Invalid timestamp.");
        check(twoPlayers.getRankedPlayerNames().size() == 2,
                "Invalid player count.");
        check(twoPlayers.getDurationSeconds() == 600,
                "Invalid game duration.");

        System.out.println("PASS: Two-player game result");

        GameResult threePlayers = new GameResult(
                2, "Medium", timestamp,
                List.of("Alice", "Bob", "Charlie"), 900);

        check(threePlayers.getRankedPlayerNames().size() == 3,
                "Three-player game was not stored correctly.");

        System.out.println("PASS: Three-player game result");

        GameResult fourPlayers = new GameResult(
                3, "Hard", timestamp,
                List.of("Alice", "Bob", "Charlie", "Dana"), 1800);

        check(fourPlayers.getRankedPlayerNames().size() == 4,
                "Four-player game was not stored correctly.");

        System.out.println("PASS: Four-player game result");

        expectInvalid(() -> new GameResult(
                0, "Easy", timestamp,
                List.of("Alice", "Bob"), 600));

        expectInvalid(() -> new GameResult(
                4, "Unknown", timestamp,
                List.of("Alice", "Bob"), 600));

        expectInvalid(() -> new GameResult(
                5, "Easy", timestamp,
                List.of("Alice"), 600));

        expectInvalid(() -> new GameResult(
                6, "Easy", timestamp,
                List.of("Alice", "Bob"), -10));

        System.out.println("PASS: Invalid data rejected");

        try {
            twoPlayers.getRankedPlayerNames().add("Charlie");
            throw new AssertionError(
                    "Player ranking must be immutable.");
        } catch (UnsupportedOperationException expected) {
            System.out.println("PASS: Player ranking is immutable");
        }

        System.out.println("ALL GAME RESULT TESTS PASSED");
    }

    /**
     * Verifies that invalid data is rejected.
     *
     * @param action operation expected to fail
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
     * Fails when a validation condition is false.
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
