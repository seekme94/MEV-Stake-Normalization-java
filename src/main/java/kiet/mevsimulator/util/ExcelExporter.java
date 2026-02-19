package kiet.mevsimulator.util;

import kiet.mevsimulator.simulation.Entity;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.util.List;

public class ExcelExporter {

    public static void export(List<Entity> entities, String fileName) {

        try (Workbook workbook = new XSSFWorkbook()) {

            // =====================
            // SHEET 1: RAW DATA
            // =====================
            Sheet rawSheet = workbook.createSheet("Raw_Data");

            Row header = rawSheet.createRow(0);
            header.createCell(0).setCellValue("Name");
            header.createCell(1).setCellValue("Category");
            header.createCell(2).setCellValue("Total Stake");
            header.createCell(3).setCellValue("Effective Stake");
            header.createCell(4).setCellValue("MEV Earned");

            int rowNum = 1;

            for (Entity e : entities) {

                Row row = rawSheet.createRow(rowNum++);

                row.createCell(0).setCellValue(e.name);
                row.createCell(1).setCellValue(e.category);
                row.createCell(2).setCellValue(e.totalStake);
                row.createCell(3).setCellValue(e.effectiveStake);
                row.createCell(4).setCellValue(e.mevEarned);
            }

            // =====================
            // SHEET 2: AGGREGATE STATS
            // =====================

            Sheet stats = workbook.createSheet("Summary_Stats");

            double totalMEV = 0;
            int totalStake = 0;

            for (Entity e : entities) {
                totalMEV += e.mevEarned;
                totalStake += e.totalStake;
            }

            stats.createRow(0).createCell(0).setCellValue("Total Stake");
            stats.getRow(0).createCell(1).setCellValue(totalStake);

            stats.createRow(1).createCell(0).setCellValue("Total MEV");
            stats.getRow(1).createCell(1).setCellValue(totalMEV);

            stats.createRow(2).createCell(0).setCellValue("Average MEV");
            stats.getRow(2).createCell(1).setCellValue(totalMEV / entities.size());

            // =====================
            // AUTO SIZE COLUMNS
            // =====================

            for(int i=0;i<5;i++) {
                rawSheet.autoSizeColumn(i);
            }

            // =====================
            // SAVE FILE
            // =====================

            FileOutputStream fileOut = new FileOutputStream(fileName);
            workbook.write(fileOut);
            fileOut.close();

            System.out.println("Excel file created: " + fileName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
