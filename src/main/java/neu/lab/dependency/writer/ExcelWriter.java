package neu.lab.dependency.writer;

import neu.lab.dependency.vo.ExcelDataVO;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @author SUNJUNYAN
 */
public class ExcelWriter {

    private static List<String> CELL_HEADS;

    static {
        CELL_HEADS = new ArrayList<>();
        CELL_HEADS.add("Project");
        CELL_HEADS.add("Module Number");
        CELL_HEADS.add("Dependency Number");
        CELL_HEADS.add("Inconsist Dependency Number");
        CELL_HEADS.add("Inherit Depth");
    }

    public static Workbook exportData(ExcelDataVO data) {
        Workbook workbook = new SXSSFWorkbook();
        Sheet sheet = buildDataSheet(workbook);
        Row row = sheet.createRow(1);
        convertDataToRaw(data, row);
        return workbook;
    }

    public static void insertData(ExcelDataVO data, Workbook workbook) {
        Sheet sheet = workbook.getSheetAt(0);
        int line = sheet.getPhysicalNumberOfRows();
        Row row = sheet.createRow((short)line);
        convertDataToRaw(data, row);
    }

    public static Workbook getWorkBook(InputStream inputStream) {
        Workbook workbook = null;
        try {
            workbook = new XSSFWorkbook(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return workbook;
    }

    public static Sheet buildDataSheet(Workbook workbook) {
        Sheet sheet = workbook.createSheet();
        for (int i = 0; i < CELL_HEADS.size(); i++) {
            sheet.setColumnWidth(i, 4000);
        }

        sheet.setDefaultRowHeight((short) 400);
        CellStyle cellStyle = buildHeadsCellStyle(sheet.getWorkbook());
        Row head = sheet.createRow(0);
        for (int i = 0; i < CELL_HEADS.size(); i++) {
            Cell cell = head.createCell(i);
            cell.setCellValue(CELL_HEADS.get(i));
            cell.setCellStyle(cellStyle);
        }
        return sheet;
    }

    public static CellStyle buildHeadsCellStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        //对齐方式设置
        style.setAlignment(HorizontalAlignment.CENTER);
        //边框颜色和宽度设置
        // 下边框
        style.setBorderBottom(BorderStyle.THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        // 左边框
        style.setBorderLeft(BorderStyle.THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        // 右边框
        style.setBorderRight(BorderStyle.THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        // 上边框
        style.setBorderTop(BorderStyle.THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        //设置背景颜色
        style.setFillForegroundColor(IndexedColors.BRIGHT_GREEN.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //粗体字设置
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }

    public static void convertDataToRaw(ExcelDataVO data, Row row) {
        int cellNum = 0;
        Cell cell;
        cell = row.createCell(cellNum++);
        cell.setCellValue(data.getProjName());
        cell = row.createCell(cellNum++);
        cell.setCellValue(data.getModuleNum());
        cell = row.createCell(cellNum++);
        cell.setCellValue(data.getDepNum());
        cell = row.createCell(cellNum++);
        cell.setCellValue(data.getConflictNum());
        cell = row.createCell(cellNum++);
        cell.setCellValue(data.getInheritDepth());
    }
}
