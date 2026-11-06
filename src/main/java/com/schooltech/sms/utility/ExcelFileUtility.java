package com.schooltech.sms.utility;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

 public class ExcelFileUtility {
         private static final DateTimeFormatter DATE_CELL_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        public static String getCellValue(Row row, int cellIndex, int rowIndex, String excelFileName) {
            Cell cell = row.getCell(cellIndex, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);

            switch (cell.getCellType()) {
                case STRING:
                    return cell.getStringCellValue().trim().toLowerCase();
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        LocalDate cellDate = cell.getLocalDateTimeCellValue().toLocalDate();
                        return cellDate.format(DATE_CELL_FORMAT);
                    }
                    return String.valueOf((long) cell.getNumericCellValue()).trim().toLowerCase(); // Convert to long to avoid decimals
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue()).trim().toLowerCase();
                case BLANK:
                    return null;
                default:
                    throw new RuntimeException("OPERATION ABORTED: " + excelFileName + "has Row type- " + cell.getCellType() + " not supported at row " + (rowIndex + 1));
            }
        }

    public static boolean isRowEmpty(Row row) {
        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            if (cell.getCellType() != CellType.BLANK) {
                return false; // Found data in this row
            }
        }
        return true; // Row is empty
    }

    public static Long parseLong(String value) {
        try {
            return (value != null && value.isEmpty()) ? null : Long.parseLong(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static Double parseDouble(String value) {
        try {
            return (value != null && value.isEmpty()) ? null : Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public static LocalDate parseDateDDhyphenMMhyphenYY(String dateStr) {
        DateTimeFormatter formatter =
                DateTimeFormatter.ofPattern("dd-MM-yyyy");

        return LocalDate.parse(dateStr, formatter);
    }


    public static Boolean parseBooleanNew(String value, String excelFileName, int rowIndex, String columnName) {
        if (value != null && (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes")))
            return true;
        else if (value != null && (value.equalsIgnoreCase("false") || value.equalsIgnoreCase("no")))
            return false;
        else
            throw new RuntimeException(
                    "OPERATION ABORTED: " + excelFileName +
                            " | Attribute \"" + columnName + "\"" +
                            " | Value: \"" + value + "\"" +
                            " | Reason: " + " Row should be either true/false at row"
            );
    }

    public static void throwExcelException(
            String fileName,
            int rowIndex,
            String columnName,
            String value,
            String message
    ) {
        throw new RuntimeException(
                "OPERATION ABORTED: " + fileName +
                        " | Row " + (rowIndex + 1) +
                        " | Column \"" + columnName + "\"" +
                        " | Value: \"" + value + "\"" +
                        " | Reason: " + message
        );
    }

}
