
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
 * Verifies CSV game result persistence and schema validation.
 */
public class GameResultCsvWriterSmokeTest {

    /**
     * Tests CSV creation, appending, ranking fields, and invalid headers.
     *
     * @param args command-line arguments
     * @throws IOException if a test file operation fails
     */
    public static void main(String[] args) throws IOException {

        Path directory = Files.createTempDirectory("panda-history-test-");
        Path csvFile = directory.resolve("history.csv");
        Path invalidFile = directory.resolve("invalid.csv");

        try {
            LocalDateTime timestamp =
                    LocalDateTime.of(2026, 11, 18, 15, 0);

            GameResultCsvWriter writer =
                    new GameResultCsvWriter(csvFile);

            writer.append(new GameResult(
                    1, "Easy", timestamp,
                    List.of("Alice", "Bob"), 600));

            check(Files.exists(csvFile),
                    "CSV file was not created.");

            System.out.println("PASS: CSV file created");

            // A new writer simulates reopening the file in another run.
            writer = new GameResultCsvWriter(csvFile);

            writer.append(new GameResult(
                    2, "Medium", timestamp,
                    List.of("Alice", "Bob", "Charlie"), 900));

            writer.append(new GameResult(
                    3, "Hard", timestamp,
                    List.of("Alice, Jr.", "Bob", "Charlie", "Dana"), 1800));

            List<String> lines = Files.readAllLines(
                    csvFile, StandardCharsets.UTF_8);

            check(lines.size() == 4,
                    "Expected one header and three game records.");

            check(lines.get(0).contains("4th_place_name"),
                    "Incorrect fourth-place column name.");

            System.out.println("PASS: CSV schema verified");
            System.out.println("PASS: Results appended successfully");

            String[] twoPlayerFields = lines.get(1).split(",", -1);
            String[] threePlayerFields = lines.get(2).split(",", -1);

            check(twoPlayerFields.length == 8,
                    "Incorrect CSV column count.");

            check(twoPlayerFields[5].isEmpty()
                    && twoPlayerFields[6].isEmpty(),
                    "Unused player ranks must be empty.");

            check(threePlayerFields[6].isEmpty(),
                    "Fourth place must be empty for three players.");

            check(lines.get(3).contains("\"Alice, Jr.\""),
                    "CSV special characters were not escaped.");

            System.out.println("PASS: Two-to-four player rankings");
            System.out.println("PASS: CSV special characters escaped");

            Files.writeString(
                    invalidFile, "wrong_header\n",
                    StandardCharsets.UTF_8);

            boolean rejected = false;

            try {
                new GameResultCsvWriter(invalidFile).append(
                        new GameResult(
                                4, "Easy", timestamp,
                                List.of("Alice", "Bob"), 300));
            } catch (IOException expected) {
                rejected = true;
            }

            check(rejected, "Invalid CSV header was accepted.");

            System.out.println("PASS: Invalid CSV header rejected");
            System.out.println("ALL CSV WRITER TESTS PASSED");

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
     * @param message failure description
     */
    private static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }
}
