
package Tests;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import Model.GameResult;

/**
 * Verifies game result data, input validation,
 * and immutability of player rankings.
 */
public class GameResultSmokeTest {

    /**
     * Runs game result validation and immutability checks.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {

        LocalDateTime timestamp =
                LocalDateTime.of(2026, 11, 18, 14, 30);

        /*
         * Verify that a two-player game result stores
         * all required fields correctly.
         */
        GameResult twoPlayers = new GameResult(
                1, "Easy", timestamp,
                List.of("Alice", "Bob"), 600);

        check(twoPlayers.getGameId() == 1,
                "Invalid game ID.");

        check(twoPlayers.getDifficulty().equals("easy"),
                "Invalid difficulty.");

        check(twoPlayers.getTimestamp().equals(timestamp),
                "Invalid timestamp.");

        check(twoPlayers.getRankedPlayerNames().size() == 2,
                "Invalid player count.");

        check(twoPlayers.getDurationSeconds() == 600,
                "Invalid game duration.");

        System.out.println("PASS: Two-player game result");

        /*
         * Verify that a three-player game result
         * stores all ranked players.
         */
        GameResult threePlayers = new GameResult(
                2, "Medium", timestamp,
                List.of("Alice", "Bob", "Charlie"), 900);

        check(threePlayers.getRankedPlayerNames().size() == 3,
                "Three-player game was not stored correctly.");

        System.out.println("PASS: Three-player game result");

        /*
         * Verify that a four-player game result
         * stores all ranked players.
         */
        GameResult fourPlayers = new GameResult(
                3, "Hard", timestamp,
                List.of("Alice", "Bob", "Charlie", "Dana"), 1800);

        check(fourPlayers.getRankedPlayerNames().size() == 4,
                "Four-player game was not stored correctly.");

        System.out.println("PASS: Four-player game result");

        /*
         * Verify that invalid game IDs, difficulty levels,
         * player counts, and durations are rejected.
         */
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

        /*
         * Verify that the stored player ranking
         * cannot be modified through its getter.
         */
        try {
            twoPlayers.getRankedPlayerNames().add("Charlie");

            throw new AssertionError(
                    "Player ranking must be immutable.");

        } catch (UnsupportedOperationException expected) {
            System.out.println(
                    "PASS: Player ranking is immutable");
        }

        /*
         * Verify that blank difficulty values, blank player
         * names, and more than four players are rejected.
         */
        expectInvalid(() -> new GameResult(
                7, " ", timestamp,
                List.of("Alice", "Bob"), 600));

        expectInvalid(() -> new GameResult(
                8, "Easy", timestamp,
                List.of("Alice", " "), 600));

        expectInvalid(() -> new GameResult(
                9, "Easy", timestamp,
                List.of("Alice", "Bob", "Charlie", "Dana", "Eve"),
                600));

        System.out.println(
                "PASS: Additional invalid data rejected");

        /*
         * Verify that modifying the original player list
         * does not affect the ranking stored in GameResult.
         */
        List<String> originalNames =
                new ArrayList<>(List.of("Alice", "Bob"));

        GameResult copiedResult = new GameResult(
                10, "Easy", timestamp, originalNames, 600);

        originalNames.set(0, "Modified");

        check(copiedResult.getRankedPlayerNames()
                .get(0).equals("Alice"),
                "Game result must preserve the original ranking.");

        System.out.println(
                "PASS: Defensive copy of player ranking");

        // Print the success message after all tests have passed.
        System.out.println("ALL GAME RESULT TESTS PASSED");
    }

    /**
     * Verifies that an operation rejects invalid data.
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
     * Fails the test if a validation condition is false.
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
