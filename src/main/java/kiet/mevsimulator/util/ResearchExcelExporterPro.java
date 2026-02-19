package kiet.mevsimulator.util;

import org.apache.poi.xddf.usermodel.chart.XDDFChart;
import org.apache.poi.xddf.usermodel.chart.XDDFChartData;
import org.apache.poi.xddf.usermodel.chart.XDDFCategoryAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFValueAxis;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFNumericalDataSource;
import org.apache.poi.xddf.usermodel.chart.XDDFDataSourcesFactory;
import org.apache.poi.xddf.usermodel.chart.ChartTypes;
import org.apache.poi.xddf.usermodel.chart.AxisPosition;

import kiet.mevsimulator.simulation.Entity;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

import java.io.FileOutputStream;
import java.util.*;
import java.util.stream.Collectors;

public class ResearchExcelExporterPro {

    public static void export(List<Entity> defaultEntities,
                              List<Entity> normalizedEntities,
                              String fileName) {

        try (XSSFWorkbook workbook = new XSSFWorkbook()) {

            createRawSheet(workbook, "Raw_Default", defaultEntities);
            createRawSheet(workbook, "Raw_Normalized", normalizedEntities);

            createAggregateSheet(workbook, defaultEntities, normalizedEntities);

            Map<String, Double> categorySummary =
                    createCategorySummary(workbook, normalizedEntities);

            createCharts(workbook, categorySummary);

            FileOutputStream out = new FileOutputStream(fileName);
            workbook.write(out);
            out.close();

            System.out.println("🔥 Research-grade Excel created: " + fileName);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void createRawSheet(XSSFWorkbook wb,
                                       String name,
                                       List<Entity> entities) {

        XSSFSheet sheet = wb.createSheet(name);

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Name");
        header.createCell(1).setCellValue("Category");
        header.createCell(2).setCellValue("TotalStake");
        header.createCell(3).setCellValue("EffectiveStake");
        header.createCell(4).setCellValue("MEV");

        int row = 1;

        for (Entity e : entities) {

            Row r = sheet.createRow(row++);

            r.createCell(0).setCellValue(e.name);
            r.createCell(1).setCellValue(e.category);
            r.createCell(2).setCellValue(e.totalStake);
            r.createCell(3).setCellValue(e.effectiveStake);
            r.createCell(4).setCellValue(e.mevEarned);
        }
    }

    private static void createAggregateSheet(XSSFWorkbook wb,
                                             List<Entity> def,
                                             List<Entity> norm) {

        XSSFSheet sheet = wb.createSheet("Aggregate_Stats");

        double defMEV = def.stream().mapToDouble(e -> e.mevEarned).sum();
        double normMEV = norm.stream().mapToDouble(e -> e.mevEarned).sum();

        sheet.createRow(0).createCell(0).setCellValue("Default Total MEV");
        sheet.getRow(0).createCell(1).setCellValue(defMEV);

        sheet.createRow(1).createCell(0).setCellValue("Normalized Total MEV");
        sheet.getRow(1).createCell(1).setCellValue(normMEV);

        sheet.createRow(2).createCell(0).setCellValue("MEV Reduction");
        sheet.getRow(2).createCell(1).setCellValue(defMEV - normMEV);
    }

    private static Map<String, Double> createCategorySummary(
            XSSFWorkbook wb,
            List<Entity> entities) {

        XSSFSheet sheet = wb.createSheet("Category_Summary");

        Map<String, Double> grouped =
                entities.stream()
                        .collect(Collectors.groupingBy(
                                e -> e.category,
                                Collectors.summingDouble(e -> e.mevEarned)
                        ));

        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Category");
        header.createCell(1).setCellValue("Total MEV");

        int row = 1;

        for (String key : grouped.keySet()) {

            Row r = sheet.createRow(row++);

            r.createCell(0).setCellValue(key);
            r.createCell(1).setCellValue(grouped.get(key));
        }

        return grouped;
    }

//    private static void createCharts(XSSFWorkbook wb,
//                                     Map<String, Double> data) {
//
//        XSSFSheet sheet = wb.createSheet("Charts");
//
//        int row = 0;
//
//        for (Map.Entry<String, Double> entry : data.entrySet()) {
//
//            Row r = sheet.createRow(row++);
//
//            r.createCell(0).setCellValue(entry.getKey());
//            r.createCell(1).setCellValue(entry.getValue());
//        }
//
//        XSSFDrawing drawing = sheet.createDrawingPatriarch();
//
//        XSSFClientAnchor anchor =
//                drawing.createAnchor(0,0,0,0,3,1,10,20);
//
//        XSSFChart chart = drawing.createChart(anchor);
//
//        chart.setTitleText("MEV Distribution by Category");
//
//        XDDFChartData dataChart =
//                chart.createData(ChartTypes.BAR,
//                        chart.createCategoryAxis(AxisPosition.BOTTOM),
//                        chart.createValueAxis(AxisPosition.LEFT));
//
//        XDDFDataSource<String> categories =
//                XDDFDataSourcesFactory.fromStringCellRange(
//                        sheet, new CellRangeAddress(0, data.size()-1,0,0));
//
//        XDDFNumericalDataSource<Double> values =
//                XDDFDataSourcesFactory.fromNumericCellRange(
//                        sheet, new CellRangeAddress(0,data.size()-1,1,1));
//
//        XDDFChartData.Series series =
//                dataChart.addSeries(categories, values);
//
//        chart.plot(dataChart);
//    }
    //------------------------------------------------------------------------------------------------------
//private static void createCharts(XSSFWorkbook wb,
//                                 Map<String, Double> data) {
//
//    XSSFSheet sheet = wb.createSheet("Charts");
//
//    // Fill sheet with data (start at row 1 to leave room for chart anchor)
//    int rowNum = 1;
//    for (Map.Entry<String, Double> entry : data.entrySet()) {
//        Row row = sheet.createRow(rowNum++);
//        row.createCell(0).setCellValue(entry.getKey());
//        row.createCell(1).setCellValue(entry.getValue());
//    }
//
//    // Create drawing for chart
//    XSSFDrawing drawing = sheet.createDrawingPatriarch();
//
//    // Position chart: col1,row1 to col2,row2
//    XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 3, 1, 12, 20);
//
//    XSSFChart chart = drawing.createChart(anchor);
//    chart.setTitleText("MEV Distribution by Category");
//    chart.setTitleOverlay(false);
//
//    // Create axes
//    XDDFCategoryAxis bottomAxis = chart.createCategoryAxis(AxisPosition.BOTTOM);
//    bottomAxis.setTitle("Category");
//
//    XDDFValueAxis leftAxis = chart.createValueAxis(AxisPosition.LEFT);
//    leftAxis.setTitle("MEV Earned");
//
//    // Chart data ranges (skip header, so start at row 1)
//    XDDFDataSource<String> categories =
//            XDDFDataSourcesFactory.fromStringCellRange(
//                    sheet,
//                    new CellRangeAddress(1, data.size(), 0, 0) // rows 1..n, column 0
//            );
//
//    XDDFNumericalDataSource<Double> values =
//            XDDFDataSourcesFactory.fromNumericCellRange(
//                    sheet,
//                    new CellRangeAddress(1, data.size(), 1, 1) // rows 1..n, column 1
//            );
//
//    XDDFChartData dataChart = chart.createData(ChartTypes.BAR, bottomAxis, leftAxis);
//    XDDFChartData.Series series = dataChart.addSeries(categories, values);
//    series.setTitle("MEV Results", null);
//
//    chart.plot(dataChart);
//}
private static void createCharts(XSSFWorkbook wb,
                                 Map<String, Double> data) {

    XSSFSheet sheet = wb.createSheet("Charts");

    // --- Step 1: Fill sheet with data ---
    // Row 0 is header
    Row header = sheet.createRow(0);
    header.createCell(0).setCellValue("Category");
    header.createCell(1).setCellValue("MEV");

    int rowNum = 1;
    for (Map.Entry<String, Double> entry : data.entrySet()) {
        Row row = sheet.createRow(rowNum++);
        row.createCell(0).setCellValue(entry.getKey());
        row.createCell(1).setCellValue(entry.getValue());
    }

    // --- Step 2: Create drawing object ---
    XSSFDrawing drawing = sheet.createDrawingPatriarch();

    // --- BAR CHART ---
    XSSFClientAnchor barAnchor = drawing.createAnchor(0, 0, 0, 0, 3, 1, 12, 20);
    XSSFChart barChart = drawing.createChart(barAnchor);
    barChart.setTitleText("MEV Distribution - Bar Chart");
    barChart.setTitleOverlay(false);

    XDDFCategoryAxis barBottom = barChart.createCategoryAxis(AxisPosition.BOTTOM);
    barBottom.setTitle("Category");
    XDDFValueAxis barLeft = barChart.createValueAxis(AxisPosition.LEFT);
    barLeft.setTitle("MEV Earned");

    XDDFDataSource<String> barCategories = XDDFDataSourcesFactory.fromStringCellRange(
            sheet, new CellRangeAddress(1, data.size(), 0, 0));
    XDDFNumericalDataSource<Double> barValues = XDDFDataSourcesFactory.fromNumericCellRange(
            sheet, new CellRangeAddress(1, data.size(), 1, 1));

    XDDFChartData barData = barChart.createData(ChartTypes.BAR, barBottom, barLeft);
    XDDFChartData.Series barSeries = barData.addSeries(barCategories, barValues);
    barSeries.setTitle("MEV Results", null);
    barChart.plot(barData);

    // --- PIE CHART ---
    XSSFClientAnchor pieAnchor = drawing.createAnchor(0, 0, 0, 0, 3, 22, 12, 42); // below bar chart
    XSSFChart pieChart = drawing.createChart(pieAnchor);
    pieChart.setTitleText("MEV Distribution - Pie Chart");
    pieChart.setTitleOverlay(false);

    XDDFDataSource<String> pieCategories = XDDFDataSourcesFactory.fromStringCellRange(
            sheet, new CellRangeAddress(1, data.size(), 0, 0));
    XDDFNumericalDataSource<Double> pieValues = XDDFDataSourcesFactory.fromNumericCellRange(
            sheet, new CellRangeAddress(1, data.size(), 1, 1));

    XDDFChartData pieData = pieChart.createData(ChartTypes.PIE, null, null);
    pieData.addSeries(pieCategories, pieValues);
    pieChart.plot(pieData);

    // --- LINE CHART (optional) ---
    XSSFClientAnchor lineAnchor = drawing.createAnchor(0, 0, 0, 0, 3, 43, 12, 63); // below pie chart
    XSSFChart lineChart = drawing.createChart(lineAnchor);
    lineChart.setTitleText("MEV Trend - Line Chart");
    lineChart.setTitleOverlay(false);

    XDDFCategoryAxis lineBottom = lineChart.createCategoryAxis(AxisPosition.BOTTOM);
    lineBottom.setTitle("Category");
    XDDFValueAxis lineLeft = lineChart.createValueAxis(AxisPosition.LEFT);
    lineLeft.setTitle("MEV Earned");

    XDDFChartData lineData = lineChart.createData(ChartTypes.LINE, lineBottom, lineLeft);
    XDDFChartData.Series lineSeries = lineData.addSeries(barCategories, barValues);
    lineSeries.setTitle("MEV Trend", null);
    lineChart.plot(lineData);
}


}
