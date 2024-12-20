package com.warehouse.socks.util;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.FileOutputStream;
import java.io.IOException;

// генератор excel-файла
public class ExcelGenerator {
    public static void main(String[] args) {
        try (Workbook workbook = new XSSFWorkbook()) {

            Sheet sheet = workbook.createSheet("Носки");

            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("Цвет");
            headerRow.createCell(1).setCellValue("Процент хлопка");
            headerRow.createCell(2).setCellValue("Количество");

            Object[][] sampleData = {
                    {"черный", 80, 100},
                    {"белый", 90, 150},
                    {"серый", 85, 200},
                    {"синий", 95, 120},
                    {"красный", 75, 80}
            };

            for (int i = 0; i < sampleData.length; i++) {
                Row row = sheet.createRow(i + 1);
                row.createCell(0).setCellValue((String) sampleData[i][0]);
                row.createCell(1).setCellValue((Integer) sampleData[i][1]);
                row.createCell(2).setCellValue((Integer) sampleData[i][2]);
            }

            for (int i = 0; i < 3; i++) {
                sheet.autoSizeColumn(i);
            }

            try (FileOutputStream outputStream = new FileOutputStream("socks_batch.xlsx")) {
                workbook.write(outputStream);
                System.out.println("Excel файл успешно создан: socks_batch.xlsx");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
