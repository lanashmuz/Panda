
package Model;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

//Adapts Excel question data to the QuestionRepository interface.
 
public class ExcelQuestionAdapter implements QuestionRepository {

    private final List<Question> questions = new ArrayList<>();

    public ExcelQuestionAdapter(InputStream input) throws IOException {
        if (input == null) {
            throw new IllegalArgumentException("Excel input cannot be null.");
        }

        DataFormatter formatter = new DataFormatter();

        try (InputStream source = input;
             Workbook workbook = WorkbookFactory.create(source)) {

            Sheet sheet = workbook.getSheetAt(0);

            // Skip the header row and load each question.
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);

                if (row == null) {
                    continue;
                }

                String id = formatter.formatCellValue(row.getCell(0)).trim();
                if (id.isEmpty()) {
                    continue;
                }

                String optionsText = formatter.formatCellValue(row.getCell(2));
                List<String> options = parseOptions(optionsText);

                String answer = formatter.formatCellValue(row.getCell(3))
                        .trim().toUpperCase(Locale.ROOT);

                if (!answer.matches("[A-D]")) {
                    throw new IllegalArgumentException(
                            "Invalid correct answer at Excel row " + (i + 1));
                }

                Question question = new Question(
                        id,
                        formatter.formatCellValue(row.getCell(1)),
                        options,
                        answer.charAt(0) - 'A',
                        formatter.formatCellValue(row.getCell(4)),
                        formatter.formatCellValue(row.getCell(5)).trim(),
                        formatter.formatCellValue(row.getCell(6)),
                        formatter.formatCellValue(row.getCell(7))
                );

                questions.add(question);
            }
        }
    }

    private List<String> parseOptions(String optionsText) {
        String[] lines = optionsText.split("\\R");
        List<String> options = new ArrayList<>();

        if (lines.length != 4) {
            throw new IllegalArgumentException(
                    "A question must contain four answer options.");
        }

        for (int i = 0; i < lines.length; i++) {
            String option = lines[i].trim();
            String prefix = (char) ('A' + i) + ".";

            if (!option.startsWith(prefix)) {
                throw new IllegalArgumentException(
                        "Invalid answer option format: " + option);
            }

            options.add(option.substring(2).trim());
        }

        return options;
    }

    @Override
    public List<Question> getQuestionsByDifficulty(String difficulty) {
        if (difficulty == null) {
            return List.of();
        }

        return questions.stream()
                .filter(q -> q.getDifficulty()
                        .equalsIgnoreCase(difficulty.trim()))
                .toList();
    }
}
