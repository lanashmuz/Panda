
package Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Saves completed game results to a CSV file.
 */
public class GameResultCsvWriter {

    private static final String HEADER =
            "gameID,difficulty,timestamp(DD/MM/YYYY; HH:MM:SS),"
            + "1st_place_name,2nd_place_name,3rd_place_name,"
            + "4th_place_name,game_duration(HH:MM:SS)";

    private static final DateTimeFormatter TIMESTAMP_FORMAT =
            DateTimeFormatter.ofPattern(
                    "dd/MM/uuuu;HH:mm:ss", Locale.ROOT);

    private final Path filePath;

    /**
     * Creates a writer for the specified CSV file.
     *
     * @param filePath destination file path
     */
    public GameResultCsvWriter(Path filePath) {
        this.filePath = Objects.requireNonNull(filePath);
    }

    /**
     * Appends a completed game result to the CSV file.
     * Creates the file and header if they do not exist.
     *
     * @param result completed game result
     * @throws IOException if the file cannot be written or has an invalid header
     */
    public void append(GameResult result) throws IOException {

        Objects.requireNonNull(result, "Game result cannot be null.");

        Path parent = filePath.toAbsolutePath().getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        boolean newFile = Files.notExists(filePath)
                || Files.size(filePath) == 0;

        if (!newFile) {
            try (BufferedReader reader = Files.newBufferedReader(
                    filePath, StandardCharsets.UTF_8)) {

                if (!HEADER.equals(reader.readLine())) {
                    throw new IOException(
                            "CSV header does not match the expected schema.");
                }
            }
        }

        List<String> columns = new ArrayList<>();

        columns.add(Long.toString(result.getGameId()));
        columns.add(result.getDifficulty());
        columns.add(TIMESTAMP_FORMAT.format(result.getTimestamp()));

        List<String> players = result.getRankedPlayerNames();

        for (int i = 0; i < 4; i++) {
            columns.add(i < players.size()
                    ? escapeCsv(players.get(i)) : "");
        }

        columns.add(formatDuration(result.getDurationSeconds()));

        StringBuilder content = new StringBuilder();

        if (newFile) {
            content.append(HEADER).append('\n');
        }

        content.append(String.join(",", columns)).append('\n');

        Files.writeString(
                filePath,
                content,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }

    /**
     * Escapes characters that require quoting in CSV fields.
     *
     * @param value field value
     * @return escaped CSV value
     */
    private static String escapeCsv(String value) {

        if (value.contains(",")
                || value.contains("\"")
                || value.contains("\n")
                || value.contains("\r")) {

            return "\"" + value.replace("\"", "\"\"") + "\"";
        }

        return value;
    }

    /**
     * Formats elapsed seconds as HH:MM:SS.
     *
     * @param seconds game duration in seconds
     * @return formatted duration
     */
    private static String formatDuration(long seconds) {

        return String.format(
                Locale.ROOT,
                "%02d:%02d:%02d",
                seconds / 3600,
                (seconds / 60) % 60,
                seconds % 60
        );
    }
}
