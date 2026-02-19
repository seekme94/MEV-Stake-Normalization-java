package kiet.mevsimulator.util;

import kiet.mevsimulator.simulation.Entity;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class ResearchExcelExporter {

    public static void export(
            List<Entity> defaultEntities,
            List<Entity> normalizedEntities,
            String fileName) {

        try (Workbook workbook = new XSSFWorkbook()) {

            // =========================
            // SHEET 1: RAW DEFAULT
            // =========================

            createRawSheet(workbook, "Raw_Default", defaultEntities);

            // =========================
            // SHEET 2: RAW NORMALIZED
            // =========================

            createRawSheet(workbook, "Raw_Normalized", normalizedEntities);

            // =========================
            // SHEET 3: AGGREGATE STATS
            // =========================

            createStatsSheet(workbook, defaultEntities, normalizedEntities);

            // =========================
            // SHEET 4: PIVOT DATA
            // =========================

            createPivotDataSheet(workbook, normalizedEntities);

            FileOutputStream fileOut = new FileOutputStream(fileName);
            workbook.write(fileOut);
            fileOut.close();

            System.out.println("Research Excel created: " + fileName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createRawSheet(Workbook wb, String name, List<Entity> entities) {

        Sheet sheet = wb.createSheet(name);

        Row header = sheet.createRow(0);

        header.createCell(0).setCellValue("Name");
        header.createCell(1).setCellValue("Category");
        header.createCell(2).setCellValue("TotalStake");
        header.createCell(3).setCellValue("EffectiveStake");
        header.createCell(4).setCellValue("MEV");

        int rowNum = 1;

        for (Entity e : entities) {

            Row row = sheet.createRow(rowNum++);

            row.createCell(0).setCellValue(e.name);
            row.createCell(1).setCellValue(e.category);
            row.createCell(2).setCellValue(e.totalStake);
            row.createCell(3).setCellValue(e.effectiveStake);
            row.createCell(4).setCellValue(e.mevEarned);
        }
    }

    private static void createStatsSheet(
            Workbook wb,
            List<Entity> defaultEntities,
            List<Entity> normalizedEntities) {

        Sheet stats = wb.createSheet("Aggregate_Stats");

        double defaultMEV = defaultEntities.stream().mapToDouble(e -> e.mevEarned).sum();
        double normalizedMEV = normalizedEntities.stream().mapToDouble(e -> e.mevEarned).sum();

        stats.createRow(0).createCell(0).setCellValue("Default Total MEV");
        stats.getRow(0).createCell(1).setCellValue(defaultMEV);

        stats.createRow(1).createCell(0).setCellValue("Normalized Total MEV");
        stats.getRow(1).createCell(1).setCellValue(normalizedMEV);
    }

    private static void createPivotDataSheet(Workbook wb, List<Entity> entities) {

        Sheet pivot = wb.createSheet("Pivot_Data");

        Row header = pivot.createRow(0);
        header.createCell(0).setCellValue("Category");
        header.createCell(1).setCellValue("MEV");

        int row = 1;

        for (Entity e : entities) {

            Row r = pivot.createRow(row++);

            r.createCell(0).setCellValue(e.category);
            r.createCell(1).setCellValue(e.mevEarned);
        }
    }
}
