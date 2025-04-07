//package com.podigua.kafka.visark.home.task;
//
//import com.podigua.kafka.excel.ExcelUtils;
//import com.podigua.kafka.visark.home.entity.Message;
//import javafx.concurrent.Task;
//import org.apache.poi.ss.usermodel.*;
//import org.apache.poi.ss.util.CellRangeAddress;
//
//import java.io.File;
//import java.nio.file.Files;
//import java.util.Date;
//import java.util.List;
//
///**
// * Excel 输出任务
// *
// * @author podigua
// * @date 2024/12/06
// */
//public class ExcelOutputTask extends Task<Boolean> {
//    private final File target;
//    private boolean running = true;
//    private final List<Message> messages;
//
//    public ExcelOutputTask(File target, List<Message> messages) {
//        this.target = target;
//        this.messages = messages;
//    }
//
//    public void shutdown() {
//        this.running = false;
//    }
//
//    @Override
//    protected Boolean call() throws Exception {
//        Workbook workbook = WorkbookFactory.create(true);
//        Sheet sheet = workbook.createSheet("Message");
//        Row row = sheet.createRow(0);
//        writeTitle(row);
//        CellStyle dataStyle = data(workbook);
//        CellStyle leftStyle = left(workbook);
//        CellStyle dateStyle = date(workbook);
//        boolean exit = writeData(sheet, messages, dataStyle, leftStyle, dateStyle);
//        if (exit) {
//            return running;
//        }
//        sheet.setColumnWidth(0, ExcelUtils.getPixel(100));
//        sheet.setColumnWidth(1, ExcelUtils.getPixel(100));
//        sheet.setColumnWidth(2, ExcelUtils.getPixel(100));
//        sheet.setColumnWidth(3, ExcelUtils.getPixel(255));
//        sheet.setColumnWidth(4, ExcelUtils.getPixel(600));
//        sheet.setColumnWidth(5, ExcelUtils.getPixel(150));
//        sheet.createFreezePane(0, 1);
//        CellRangeAddress filter = new CellRangeAddress(0, 0, 0, 5);
//        sheet.setAutoFilter(filter);
//        workbook.write(Files.newOutputStream(target.toPath()));
//        return running;
//    }
//
//    private static void writeTitle(Row row) {
//        CellStyle style = title(row.getSheet().getWorkbook());
//        Cell index = row.createCell(0);
//        index.setCellValue("#");
//        index.setCellStyle(style);
//        Cell partition = row.createCell(1);
//        partition.setCellValue("Partition");
//        partition.setCellStyle(style);
//        Cell offset = row.createCell(2);
//        offset.setCellValue("Offset");
//        offset.setCellStyle(style);
//        Cell key = row.createCell(3);
//        key.setCellValue("Key");
//        key.setCellStyle(style);
//        Cell value = row.createCell(4);
//        value.setCellValue("Value");
//        value.setCellStyle(style);
//        Cell timestamp = row.createCell(5);
//        timestamp.setCellValue("Timestamp");
//        timestamp.setCellStyle(style);
//    }
//
//    private static CellStyle title(Workbook workbook) {
//        CellStyle result = workbook.createCellStyle();
//        result.setBorderTop(BorderStyle.THIN);
//        Font font = workbook.createFont();
//        font.setBold(true);
//        font.setFontHeightInPoints((short) 14);
//        result.setFont(font);
//        result.setBorderRight(BorderStyle.THIN);
//        result.setBorderBottom(BorderStyle.THIN);
//        result.setBorderLeft(BorderStyle.THIN);
//        result.setAlignment(HorizontalAlignment.CENTER);
//        result.setVerticalAlignment(VerticalAlignment.CENTER);
//        result.setFillPattern(FillPatternType.SOLID_FOREGROUND);
//        result.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
//        return result;
//    }
//
//
//    private CellStyle left(Workbook workbook) {
//        CellStyle result = data(workbook);
//        result.setAlignment(HorizontalAlignment.LEFT);
//        return result;
//    }
//
//    private CellStyle data(Workbook workbook) {
//        CellStyle result = workbook.createCellStyle();
//        result.setBorderTop(BorderStyle.THIN);
//        result.setBorderRight(BorderStyle.THIN);
//        result.setBorderBottom(BorderStyle.THIN);
//        result.setBorderLeft(BorderStyle.THIN);
//        result.setAlignment(HorizontalAlignment.CENTER);
//        result.setVerticalAlignment(VerticalAlignment.CENTER);
//        return result;
//    }
//
//
//    private boolean writeData(Sheet sheet, List<Message> messages, CellStyle dataStyle, CellStyle leftStyle, CellStyle dateStyle) {
//        int rowIndex = 0;
//        for (Message message : messages) {
//            if (!running) {
//                return true;
//            }
//            rowIndex++;
//            Row row = sheet.createRow(rowIndex);
//            writeData(row, message, dataStyle, leftStyle, dateStyle);
//        }
//        return false;
//    }
//
//    private CellStyle date(Workbook workbook) {
//        CellStyle result = data(workbook);
//        DataFormat dataFormat = workbook.createDataFormat();
//        result.setDataFormat(dataFormat.getFormat("yyyy-MM-dd HH:mm:ss"));
//        return result;
//    }
//
//    private static void writeData(Row row, Message message, CellStyle dataStyle, CellStyle leftStyle, CellStyle dateStyle) {
//        Cell index = row.createCell(0);
//        index.setCellValue(row.getRowNum() + 1);
//        index.setCellStyle(dataStyle);
//        Cell partition = row.createCell(1);
//        partition.setCellValue(message.partition().getValue());
//        partition.setCellStyle(dataStyle);
//        Cell offset = row.createCell(2);
//        offset.setCellValue(message.offset().longValue());
//        offset.setCellStyle(dataStyle);
//        Cell key = row.createCell(3);
//        key.setCellValue(message.key().getValue());
//        key.setCellStyle(dataStyle);
//        Cell value = row.createCell(4);
//        value.setCellValue(message.value().getValue());
//        value.setCellStyle(leftStyle);
//        Cell timestamp = row.createCell(5);
//        timestamp.setCellValue(new Date(message.millis()));
//        timestamp.setCellStyle(dateStyle);
//    }
//}
