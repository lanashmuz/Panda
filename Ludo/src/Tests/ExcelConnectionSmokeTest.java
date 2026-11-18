
package Tests;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

//Verifies that the Excel question file can be opened using Apache POI.
 
public class ExcelConnectionSmokeTest {

    public static void main(String[] args) throws Exception {

        Path filePath = Path.of("..", "question_Panda.xlsx");

        try (InputStream input = Files.newInputStream(filePath);
             Workbook workbook = WorkbookFactory.create(input)) {

            Sheet sheet = workbook.getSheetAt(0);

            System.out.println("PASS: Excel file opened");
            System.out.println("Question rows: " + sheet.getLastRowNum());
        }
    }
}
