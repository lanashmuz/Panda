
package Tests;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import Model.GameResult;
import Model.GameResultCsvWriter;

/**
 * Verifies CSV history persistence across separate Java executions.
 */
public class GameResultRestartSmokeTest {

	private static final Path TEST_FILE = Path.of("game-history-restart-smoke.csv");

	/**
	 * Saves a game on the first run and verifies it on the second run.
	 *
	 * @param args command-line arguments
	 * @throws IOException if a file operation fails
	 */
	public static void main(String[] args) throws IOException {

		GameResultCsvWriter writer = new GameResultCsvWriter(TEST_FILE);

		LocalDateTime timestamp = LocalDateTime.of(2026, 11, 19, 10, 0);

		if (Files.notExists(TEST_FILE)) {

			check(writer.getNextGameId() == 1, "First game ID must be 1.");

			writer.append(new GameResult(1, "Easy", timestamp, List.of("Alice", "Bob"), 600));

			check(Files.exists(TEST_FILE), "CSV history was not created.");

			System.out.println("PASS: First execution saved game 1");
			System.out.println("Run the test again to verify persistence.");
			return;
		}

		// Verify the previous result survived a separate execution.
		List<String> previousLines = Files.readAllLines(TEST_FILE, StandardCharsets.UTF_8);

		check(previousLines.size() == 2, "Expected one header and one saved game.");

		check(previousLines.get(1).startsWith("1,easy,"), "Previously saved game was not found.");

		check(writer.getNextGameId() == 2, "Game ID sequence was not preserved.");

		System.out.println("PASS: History survived application restart");
		System.out.println("PASS: Game ID sequence preserved");

		writer.append(new GameResult(2, "Medium", timestamp, List.of("Charlie", "Dana"), 900));

		List<String> updatedLines = Files.readAllLines(TEST_FILE, StandardCharsets.UTF_8);

		check(updatedLines.size() == 3, "Expected two saved games.");

		check(updatedLines.get(1).equals(previousLines.get(1)), "Previously saved result was modified.");

		check(updatedLines.get(2).startsWith("2,medium,"), "Second result was not appended correctly.");

		System.out.println("PASS: Previous game preserved");
		System.out.println("PASS: New game appended after restart");

		Files.delete(TEST_FILE);

		System.out.println("ALL RESTART PERSISTENCE TESTS PASSED");
	}

	/**
	 * Fails the test when a condition is false.
	 *
	 * @param condition condition to verify
	 * @param message   failure description
	 */
	private static void check(boolean condition, String message) {

		if (!condition) {
			throw new AssertionError(message);
		}
	}
}
