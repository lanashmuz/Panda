
package Model;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Represents the final result of a completed Ludo game.
 */
public class GameResult {

    private final long gameId;
    private final String difficulty;
    private final LocalDateTime timestamp;
    private final List<String> rankedPlayerNames;
    private final long durationSeconds;

    /**
     * Creates an immutable game result with players ordered by rank.
     *
     * @param gameId unique game identifier
     * @param difficulty game difficulty
     * @param timestamp game completion time
     * @param rankedPlayerNames player names ordered from first to last
     * @param durationSeconds total game duration in seconds
     */
    public GameResult(long gameId, String difficulty,
            LocalDateTime timestamp, List<String> rankedPlayerNames,
            long durationSeconds) {

        if (gameId <= 0) {
            throw new IllegalArgumentException(
                    "Game ID must be positive.");
        }

        if (difficulty == null || difficulty.isBlank()) {
            throw new IllegalArgumentException(
                    "Game difficulty is required.");
        }

        String normalizedDifficulty =
                difficulty.trim().toLowerCase(Locale.ROOT);

        if (!List.of("easy", "medium", "hard")
                .contains(normalizedDifficulty)) {
            throw new IllegalArgumentException(
                    "Invalid game difficulty.");
        }

        Objects.requireNonNull(timestamp,
                "Completion timestamp is required.");

        if (rankedPlayerNames == null
                || rankedPlayerNames.size() < 2
                || rankedPlayerNames.size() > 4) {
            throw new IllegalArgumentException(
                    "A game must have 2 to 4 ranked players.");
        }

        for (String name : rankedPlayerNames) {
            if (name == null || name.isBlank()) {
                throw new IllegalArgumentException(
                        "Player names cannot be empty.");
            }
        }

        if (durationSeconds < 0) {
            throw new IllegalArgumentException(
                    "Game duration cannot be negative.");
        }

        this.gameId = gameId;
        this.difficulty = normalizedDifficulty;
        this.timestamp = timestamp;
        this.rankedPlayerNames = List.copyOf(rankedPlayerNames);
        this.durationSeconds = durationSeconds;
    }

    /** Returns the unique game identifier. */
    public long getGameId() {
        return gameId;
    }

    /** Returns the game difficulty. */
    public String getDifficulty() {
        return difficulty;
    }

    /** Returns the game completion timestamp. */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /** Returns player names ordered by final ranking. */
    public List<String> getRankedPlayerNames() {
        return rankedPlayerNames;
    }

    /** Returns the game duration in seconds. */
    public long getDurationSeconds() {
        return durationSeconds;
    }
}
