
package Tests;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import Model.ExcelQuestionAdapter;

// Tests how the Excel adapter handles invalid question data.
 
public class ExcelInvalidDataTest {

    public static void main(String[] args) throws Exception {

        ByteArrayOutputStream output = new ByteArrayOutputStream();

        // Create a temporary Excel file with an invalid correct answer.
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Questions");
            sheet.createRow(0).createCell(0).setCellValue("QuestionID");

            Row row = sheet.createRow(1);
            row.createCell(0).setCellValue("Q01");
            row.createCell(2).setCellValue(
                    "A. One\nB. Two\nC. Three\nD. Four");
            row.createCell(3).setCellValue("E");

            workbook.write(output);
        }

        ByteArrayInputStream input =
                new ByteArrayInputStream(output.toByteArray());

        try {
            new ExcelQuestionAdapter(input);
            throw new AssertionError("Invalid answer was accepted.");
        } catch (IllegalArgumentException e) {
            System.out.println("PASS: Invalid answer rejected");
        }
    }
}
