
package com.hirehub.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ReportExportUtil {

    private ReportExportUtil() {
    }

    // Generate CSV report
    public static byte[] generateCsv(
            List<String> headers,
            List<List<String>> data) {

        StringBuilder csv = new StringBuilder();

        // UTF-8 BOM helps Excel recognize UTF-8 correctly
        csv.append('\uFEFF');

        csv.append(createCsvRow(headers));

        for (List<String> row : data) {
            csv.append(createCsvRow(row));
        }

        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    private static String createCsvRow(List<String> values) {

        StringBuilder row = new StringBuilder();

        for (int i = 0; i < values.size(); i++) {

         
            String value = sanitizeCsvValue(values.get(i));

            row.append("\"")
               .append(value.replace("\"", "\"\""))
               .append("\"");

            if (i < values.size() - 1) {
                row.append(",");
            }
        }

        row.append("\r\n");

        return row.toString();
    }

    // Generate Excel report
    public static byte[] generateExcel(
            String sheetName,
            List<String> headers,
            List<List<String>> data) throws IOException {

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream =
                     new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(sheetName);

            // Header styling
            Font font = workbook.createFont();
            font.setBold(true);

            CellStyle headerStyle = workbook.createCellStyle();
            headerStyle.setFont(font);

            // Create header row
            Row headerRow = sheet.createRow(0);

            for (int i = 0; i < headers.size(); i++) {

                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers.get(i));
                cell.setCellStyle(headerStyle);
            }

            // Create data rows
            for (int i = 0; i < data.size(); i++) {

                Row dataRow = sheet.createRow(i + 1);

                List<String> values = data.get(i);

                for (int j = 0; j < values.size(); j++) {

                    Cell cell = dataRow.createCell(j);

                    String value = values.get(j) == null
                            ? ""
                            : values.get(j);

                    cell.setCellValue(value);
                }
            }

            // Automatically adjust column widths
            for (int i = 0; i < headers.size(); i++) {
                sheet.autoSizeColumn(i);
            }

            workbook.write(outputStream);

            return outputStream.toByteArray();
        }
    }
    
    private static String sanitizeCsvValue(String value) {

        if (value == null || value.isEmpty()) {
            return "";
        }

        String trimmed = value.stripLeading();

        if (trimmed.startsWith("=")
                || trimmed.startsWith("+")
                || trimmed.startsWith("-")
                || trimmed.startsWith("@")) {

            return "'" + value;
        }

        return value;
    }
}
