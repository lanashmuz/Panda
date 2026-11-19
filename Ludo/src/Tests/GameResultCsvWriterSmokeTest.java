
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
 * Verifies CSV game result persistence, schema validation, ranking fields, and
 * duplicate game ID prevention.
 */
public class GameResultCsvWriterSmokeTest {

	/**
	 * Tests CSV creation, appending, ranking fields, invalid headers, and duplicate
	 * game IDs.
	 *
	 * @param args command-line arguments
	 * @throws IOException if a test file operation fails
	 */
	public static void main(String[] args) throws IOException {

		Path directory = Files.createTempDirectory("panda-history-test-");

		Path csvFile = directory.resolve("history.csv");
		Path invalidFile = directory.resolve("invalid.csv");

		try {
			LocalDateTime timestamp = LocalDateTime.of(2026, 11, 18, 15, 0);

			GameResultCsvWriter writer = new GameResultCsvWriter(csvFile);

			/*
			 * Verify that the writer creates a new CSV file when no previous game history
			 * exists.
			 */
			writer.append(new GameResult(1, "Easy", timestamp, List.of("Alice", "Bob"), 600));

			check(Files.exists(csvFile), "CSV file was not created.");

			System.out.println("PASS: CSV file created");

			/*
			 * Verify that reopening the writer preserves previous results and appends new
			 * game records.
			 */
			writer = new GameResultCsvWriter(csvFile);

			writer.append(new GameResult(2, "Medium", timestamp, List.of("Alice", "Bob", "Charlie"), 900));

			writer.append(new GameResult(3, "Hard", timestamp, List.of("Alice, Jr.", "Bob", "Charlie", "Dana"), 1800));

			List<String> lines = Files.readAllLines(csvFile, StandardCharsets.UTF_8);

			check(lines.size() == 4, "Expected one header and three game records.");

			/*
			 * Verify that the CSV header exactly matches the official schema, including
			 * column order.
			 */
			check(lines.get(0).equals("gameID,difficulty,timestamp(DD/MM/YYYY; HH:MM:SS),"
					+ "1st_place_name,2nd_place_name,3rd_place_name," + "4rd_place_name,game_duration(HH:MM:SS)"),
					"CSV header does not match the official schema.");

			System.out.println("PASS: CSV schema verified");
			System.out.println("PASS: Results appended successfully");

			/*
			 * Verify timestamp formatting, game duration, and unused ranking fields for two
			 * and three players.
			 */
			String[] twoPlayerFields = lines.get(1).split(",", -1);

			String[] threePlayerFields = lines.get(2).split(",", -1);

			check(twoPlayerFields[2].equals("18/11/2026;15:00:00"), "Incorrect timestamp format.");

			check(twoPlayerFields[7].equals("00:10:00"), "Incorrect game duration format.");

			check(threePlayerFields[7].equals("00:15:00"), "Incorrect duration for second game.");

			check(twoPlayerFields.length == 8, "Incorrect CSV column count.");

			check(twoPlayerFields[5].isEmpty() && twoPlayerFields[6].isEmpty(), "Unused player ranks must be empty.");

			check(threePlayerFields[6].isEmpty(), "Fourth place must be empty for three players.");

			/*
			 * Verify that player names containing commas are correctly escaped in the CSV
			 * output.
			 */
			check(lines.get(3).contains("\"Alice, Jr.\""), "CSV special characters were not escaped.");

			System.out.println("PASS: Two-to-four player rankings");
			System.out.println("PASS: CSV special characters escaped");

			/*
			 * Verify that an existing CSV file with an invalid header is rejected.
			 */
			Files.writeString(invalidFile, "wrong_header\n", StandardCharsets.UTF_8);

			boolean rejected = false;

			try {
				new GameResultCsvWriter(invalidFile)
						.append(new GameResult(4, "Easy", timestamp, List.of("Alice", "Bob"), 300));

			} catch (IOException expected) {
				rejected = true;
			}

			check(rejected, "Invalid CSV header was accepted.");

			System.out.println("PASS: Invalid CSV header rejected");

			/*
			 * Verify that attempting to save an existing game ID is rejected without
			 * modifying the CSV history.
			 */
			String originalContent = Files.readString(csvFile, StandardCharsets.UTF_8);

			boolean duplicateRejected = false;

			try {
				writer.append(new GameResult(2, "Easy", timestamp, List.of("Alice", "Bob"), 300));

			} catch (IOException expected) {
				duplicateRejected = true;
			}

			check(duplicateRejected, "Duplicate game ID must be rejected.");

			check(Files.readString(csvFile, StandardCharsets.UTF_8).equals(originalContent),
					"Duplicate ID attempt must not modify the CSV history.");

			System.out.println("PASS: Duplicate game ID rejected");

			Path noNewlineFile = directory.resolve("no-newline.csv");

// Verify appending when the existing file has no final newline.
			Files.writeString(noNewlineFile, lines.get(0) + "\n" + lines.get(1), StandardCharsets.UTF_8);

			GameResultCsvWriter noNewlineWriter = new GameResultCsvWriter(noNewlineFile);

			noNewlineWriter.append(new GameResult(4, "Easy", timestamp, List.of("Alice", "Bob"), 300));

			List<String> updatedLines = Files.readAllLines(noNewlineFile, StandardCharsets.UTF_8);

			check(updatedLines.size() == 3, "New game record was merged with the previous record.");

			check(updatedLines.get(2).startsWith("4,"), "New game record was not written on a separate line.");

			System.out.println("PASS: Appending without final newline");

			// Print success only after all tests have passed.
			System.out.println("ALL CSV WRITER TESTS PASSED");

		} finally {
			// Delete temporary files created during the test.
			Files.deleteIfExists(directory.resolve("no-newline.csv"));
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
