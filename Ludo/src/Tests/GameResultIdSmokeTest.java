
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
 * Verifies game ID generation across CSV history sessions.
 */
public class GameResultIdSmokeTest {

	/**
	 * Tests ID generation, persistence, and invalid history handling.
	 *
	 * @param args command-line arguments
	 * @throws IOException if a test file operation fails
	 */
	public static void main(String[] args) throws IOException {

		Path directory = Files.createTempDirectory("panda-game-id-");
		Path csvFile = directory.resolve("history.csv");
		Path invalidFile = directory.resolve("invalid.csv");

		try {
			GameResultCsvWriter writer = new GameResultCsvWriter(csvFile);

			check(writer.getNextGameId() == 1, "First game ID must be 1.");

			System.out.println("PASS: First game ID");

			LocalDateTime timestamp = LocalDateTime.of(2026, 11, 18, 15, 30);

			writer.append(new GameResult(1, "Easy", timestamp, List.of("Alice", "Bob"), 600));

			check(writer.getNextGameId() == 2, "Next game ID must be 2.");

			System.out.println("PASS: Sequential game ID");

			writer.append(new GameResult(7, "Medium", timestamp, List.of("Alice", "Bob", "Charlie"), 900));

			GameResultCsvWriter reopenedWriter = new GameResultCsvWriter(csvFile);

			check(reopenedWriter.getNextGameId() == 8, "Next ID must follow the highest existing ID.");

			System.out.println("PASS: ID after reopening history");
			System.out.println("PASS: Gaps in game IDs handled");

			Files.writeString(invalidFile, "wrong_header\n", StandardCharsets.UTF_8);

			boolean rejected = false;

			try {
				new GameResultCsvWriter(invalidFile).getNextGameId();
			} catch (IOException expected) {
				rejected = true;
			}

			check(rejected, "Invalid CSV header must be rejected.");

			System.out.println("PASS: Invalid history rejected");
			System.out.println("ALL GAME ID TESTS PASSED");

		} finally {
			Files.deleteIfExists(csvFile);
			Files.deleteIfExists(invalidFile);
			Files.deleteIfExists(directory);
		}
	}

	/**
	 * Fails the test if a condition is false.
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