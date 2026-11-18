
package Model;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

/**
 * Saves completed game results to a CSV file.
 * Validates the official CSV schema and prevents duplicate game IDs.
 */
public class GameResultCsvWriter {

    private static final String HEADER =
            "gameID,difficulty,timestamp(DD/MM/YYYY; HH:MM:SS),"
            + "1st_place_name,2nd_place_name,3rd_place_name,"
            + "4rd_place_name,game_duration(HH:MM:SS)";

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
        this.filePath = Objects.requireNonNull(
                filePath, "CSV file path cannot be null.");
    }

    /**
     * Appends a completed game result to the CSV file.
     * Creates the file and header if they do not exist.
     * Rejects duplicate game IDs and invalid CSV histories.
     *
     * @param result completed game result
     * @throws IOException if the file cannot be written,
     *                     the history is invalid, or the ID already exists
     */
    public void append(GameResult result) throws IOException {

        Objects.requireNonNull(
                result, "Game result cannot be null.");

        Path parent = filePath.toAbsolutePath().getParent();

        if (parent != null) {
            Files.createDirectories(parent);
        }

        boolean newFile = Files.notExists(filePath)
                || Files.size(filePath) == 0;

        /*
         * Read existing game IDs and validate the CSV history
         * before writing a new result.
         */
        Set<Long> existingIds = readStoredGameIds();

        if (existingIds.contains(result.getGameId())) {
            throw new IOException(
                    "Game ID already exists: " + result.getGameId());
        }

        // Prepare the eight columns required by the official schema.
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

        // Append the result without deleting previous game records.
        Files.writeString(
                filePath,
                content,
                StandardCharsets.UTF_8,
                StandardOpenOption.CREATE,
                StandardOpenOption.APPEND
        );
    }

    /**
     * Finds the next available game ID from the CSV history.
     * Uses the highest existing ID to preserve continuity
     * across application restarts.
     *
     * @return the next game ID, starting at 1 for empty history
     * @throws IOException if the history cannot be read or is invalid
     */
    public long getNextGameId() throws IOException {

        Set<Long> existingIds = readStoredGameIds();

        long maxId = 0;

        for (long id : existingIds) {
            maxId = Math.max(maxId, id);
        }

        if (maxId == Long.MAX_VALUE) {
            throw new IOException(
                    "Cannot generate another game ID.");
        }

        return maxId + 1;
    }

    /**
     * Reads existing game IDs from the CSV history.
     * Validates the header, game IDs, and quoted fields.
     * Supports quoted CSV fields spanning multiple lines.
     *
     * @return a set containing all stored game IDs
     * @throws IOException if the history is invalid
     *                     or cannot be read
     */
    private Set<Long> readStoredGameIds() throws IOException {

        Set<Long> ids = new HashSet<>();

        if (Files.notExists(filePath)
                || Files.size(filePath) == 0) {
            return ids;
        }

        try (BufferedReader reader = Files.newBufferedReader(
                filePath, StandardCharsets.UTF_8)) {

            if (!HEADER.equals(reader.readLine())) {
                throw new IOException(
                        "CSV header does not match the expected schema.");
            }

            boolean insideQuotes = false;
            String line;

            while ((line = reader.readLine()) != null) {

                /*
                 * A new CSV record begins only when the reader
                 * is not inside a quoted multiline field.
                 */
                if (!insideQuotes && !line.isBlank()) {

                    int separator = line.indexOf(',');

                    if (separator <= 0) {
                        throw new IOException("Invalid CSV record.");
                    }

                    long id;

                    try {
                        id = Long.parseLong(
                                line.substring(0, separator));
                    } catch (NumberFormatException e) {
                        throw new IOException("Invalid game ID.", e);
                    }

                    if (id <= 0) {
                        throw new IOException("Invalid game ID.");
                    }

                    // Reject duplicate IDs already present in history.
                    if (!ids.add(id)) {
                        throw new IOException(
                                "Duplicate game ID in CSV history: " + id);
                    }
                }

                // Track quoted fields that may span multiple lines.
                for (int i = 0; i < line.length(); i++) {

                    if (line.charAt(i) == '"') {

                        if (insideQuotes
                                && i + 1 < line.length()
                                && line.charAt(i + 1) == '"') {
                            i++;
                        } else {
                            insideQuotes = !insideQuotes;
                        }
                    }
                }
            }

            if (insideQuotes) {
                throw new IOException("Invalid CSV history.");
            }
        }

        return ids;
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
