package UsingExcelandDataProvider;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelUtils {

	private String filePath;

    public ExcelUtils(String filePath) {
        this.filePath = filePath;
    }

    public List<Map<String, Object>> getUserData(String sheetName) {
        List<Map<String, Object>> userDataList = new ArrayList<>();
        try (FileInputStream fis = new FileInputStream(filePath);
             Workbook workbook = new XSSFWorkbook(fis)) {

            Sheet sheet = workbook.getSheet(sheetName);
            int rowCount = sheet.getPhysicalNumberOfRows();

            for (int i = 1; i < rowCount; i++) { // Assuming first row is header
                Row row = sheet.getRow(i);
                Map<String, Object> userData = new HashMap<>();

                userData.put("user_first_name", getCellValue(row.getCell(0)));
                userData.put("user_last_name", getCellValue(row.getCell(1)));
                userData.put("user_contact_number", getCellValue(row.getCell(2)));
                userData.put("user_email_id", getCellValue(row.getCell(3)));

                Map<String, String> userAddress = new HashMap<>();
                userAddress.put("plotNumber", getCellValue(row.getCell(4)));
                userAddress.put("street", getCellValue(row.getCell(5)));
                userAddress.put("state", getCellValue(row.getCell(6)));
                userAddress.put("country", getCellValue(row.getCell(7)));
                userAddress.put("zipCode", getCellValue(row.getCell(8)));

                userData.put("userAddress", userAddress);
                userDataList.add(userData);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return userDataList;
    }

    private String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (int) numericValue) {
                        return String.valueOf((int) numericValue); // Convert to integer if it is a whole number
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            case BLANK:
                return "";
            default:
                return "";
        }
    }
}
