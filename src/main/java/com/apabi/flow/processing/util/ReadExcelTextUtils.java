package com.apabi.flow.processing.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFDateUtil;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author supeng
 */
public class ReadExcelTextUtils {
    private Logger logger = LogManager.getLogger(ReadExcelTextUtils.class);
    private Workbook wb;
    private Sheet sheet;
    private Row row;
    private String filepath;


    // 构造方法
    public ReadExcelTextUtils(String filepath) {
        if (filepath == null) {
            return;
        }
        this.filepath = filepath;
        String ext = filepath.substring(filepath.lastIndexOf("."));
        try {
            InputStream is = new FileInputStream(filepath);
            if (".xls".equals(ext)) {
                wb = new HSSFWorkbook(is);
            } else if (".xlsx".equals(ext)) {
                wb = new XSSFWorkbook(is);
            } else {
                wb = null;
            }
        } catch (FileNotFoundException e) {
            logger.error("FileNotFoundException", e);
        } catch (IOException e) {
            logger.error("IOException", e);
        }
    }

    // 构造方法
    public ReadExcelTextUtils(InputStream in, String fileName) {
        if (in == null) {
            return;
        }
        String ext = fileName.substring(fileName.lastIndexOf("."));
        try {
            if (".xls".equals(ext)) {
                wb = new HSSFWorkbook(in);
            } else if (".xlsx".equals(ext)) {
                wb = new XSSFWorkbook(in);
            } else {
                wb = null;
            }
        } catch (FileNotFoundException e) {
            logger.error("FileNotFoundException", e);
        } catch (IOException e) {
            logger.error("IOException", e);
        }
    }

    /**
     * 读取Excel表格表头的内容
     *
     * @return String 表头内容的数组
     * @author pipi
     */
    public List<String> readExcelTitle() throws Exception {
        if (wb == null) {
            throw new Exception("Workbook对象为空！");
        }
        sheet = wb.getSheetAt(0);
        row = sheet.getRow(0);
        // 标题总列数
        int colNum = row.getPhysicalNumberOfCells();
        String[] title = new String[colNum];
        List<String> titleList = new ArrayList<String>();
        for (int i = 0; i < colNum; i++) {
            // title[i] = getStringCellValue(row.getCell((short) i));
            //title[i] = row.getCell(i).getCellFormula();
            if (!"".equalsIgnoreCase(title[i])) {
                title[i] = row.getCell(i).getStringCellValue();
                titleList.add(title[i]);
            }
        }
        return titleList;
    }

    /**
     * 读取Excel数据内容
     *
     * @return Map 包含单元格数据内容的Map对象
     * @author pipi
     */
    public Map<Integer, Map<Object, Object>> readExcelContent() throws Exception {
        if (wb == null) {
            throw new Exception("Workbook对象为空！");
        }
        Map<Integer, Map<Object, Object>> content = new HashMap<>();
        sheet = wb.getSheetAt(0);
        // 得到总行数
        int rowNum = sheet.getLastRowNum();
        row = sheet.getRow(0);
        int colNum = row.getPhysicalNumberOfCells();
        List<String> titles = readExcelTitle();
        // 正文内容应该从第二行开始,第一行为表头的标题
        for (int i = 1; i <= rowNum; i++) {
            row = sheet.getRow(i);
            int j = 0;
            Map<Object, Object> cellValue = new HashMap<Object, Object>();
            while (j < colNum) {
                if (row == null) {
                    j++;
                    break;
                }
                Object obj = getCellStringValue(titles.get(j), row.getCell(j));
                cellValue.put(titles.get(j), obj);
                j++;
            }
            content.put(i, cellValue);
        }
        return content;
    }

    private Object getCellStringValue(String title, Cell cell) {
        Object cellvalue = "";
        if (cell != null) {
            // 判断当前Cell的Type
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC:// 如果当前Cell的Type为NUMERIC
//                    String value = cell.toString();
//                    system.out.println(value);
                    //dd-MM月-yyyy
                    if (title.equals("出版时间")) {
                        if (cell.toString().contains("月")){
                            Date date = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            cellvalue = dateFormat.format(date);
                        }else{
                            cellvalue = cell.toString();
                        }
                        break;
                    }
                    if (title.equals("纸书价格")) {
                        DecimalFormat df = new DecimalFormat("2");
                        cellvalue = df.format(cell.getNumericCellValue());
                        break;
                    }
                    DecimalFormat df = new DecimalFormat("0");
                    cellvalue = df.format(cell.getNumericCellValue());
                    //cellvalue = cell.toString();
                    break;
                case Cell.CELL_TYPE_FORMULA: {
                    // 判断当前的cell是否为Date
                    if (DateUtil.isCellDateFormatted(cell)) {
                        // 如果是Date类型则，转化为 String 格式
                        // data格式是带时分秒的：2013-7-10 0:00:00
                        // cellvalue = cell.getDateCellValue().toLocaleString();
                        // data格式是不带带时分秒的：2013-7-10
                        Date date = cell.getDateCellValue();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        cellvalue = dateFormat.format(date);
                    } else {// 如果是纯数字
                        // 取得当前Cell的数值
//                        String result = cell.getStringCellValue();
                        String result = String.valueOf(cell.getNumericCellValue());
                        cellvalue = result.substring(0, result.lastIndexOf("."));
                    }
                    break;
                }
                case Cell.CELL_TYPE_STRING:// 如果当前Cell的Type为STRING
                    // 取得当前的Cell字符串
                    cellvalue = cell.getRichStringCellValue().getString();
                    break;
                default:// 默认的Cell值
                    cellvalue = "";
            }
        } else {
            cellvalue = "";
        }
        return cellvalue;
    }

    /**
     * 根据Cell类型设置数据
     *
     * @param cell
     * @return
     * @author pipi
     */
    private Object getCellFormatValue(Cell cell) {
        Object cellvalue = "";
        if (cell != null) {
            // 判断当前Cell的Type
            switch (cell.getCellType()) {
                case Cell.CELL_TYPE_NUMERIC:// 如果当前Cell的Type为NUMERIC
//                    String value = cell.toString();
//                    system.out.println(value);
                    //dd-MM月-yyyy
                    if (cell.toString().contains("月")) {
                        Date date = HSSFDateUtil.getJavaDate(cell.getNumericCellValue());
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        cellvalue = dateFormat.format(date);
                        break;
                    }
                    DecimalFormat df = new DecimalFormat("0");
                    cellvalue = df.format(cell.getNumericCellValue());
                    //cellvalue = cell.toString();
                    break;
                case Cell.CELL_TYPE_FORMULA: {
                    // 判断当前的cell是否为Date
                    if (DateUtil.isCellDateFormatted(cell)) {
                        // 如果是Date类型则，转化为 String 格式
                        // data格式是带时分秒的：2013-7-10 0:00:00
                        // cellvalue = cell.getDateCellValue().toLocaleString();
                        // data格式是不带带时分秒的：2013-7-10
                        Date date = cell.getDateCellValue();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                        cellvalue = dateFormat.format(date);
                    } else {// 如果是纯数字
                        // 取得当前Cell的数值
//                        String result = cell.getStringCellValue();
                        String result = String.valueOf(cell.getNumericCellValue());
                        cellvalue = result.substring(0, result.lastIndexOf("."));
                    }
                    break;
                }
                case Cell.CELL_TYPE_STRING:// 如果当前Cell的Type为STRING
                    // 取得当前的Cell字符串
                    cellvalue = cell.getRichStringCellValue().getString();
                    break;
                default:// 默认的Cell值
                    cellvalue = "";
            }
        } else {
            cellvalue = "";
        }
        return cellvalue;
    }

    public Map<Integer, Map<Object, Object>> getData() {
        try {
            ReadExcelTextUtils excelReader = new ReadExcelTextUtils(filepath);
            Map<Integer, Map<Object, Object>> map = excelReader.readExcelContent();
            return map;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<Integer, Map<Object, Object>> getDataByFilePath() {
        try {
            ReadExcelTextUtils excelReader = new ReadExcelTextUtils(filepath);
            Map<Integer, Map<Object, Object>> map = excelReader.readExcelContent();
            return map;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<Integer, Map<Object, Object>> getDataByInputStream() {
        try {
            Map<Integer, Map<Object, Object>> map = readExcelContent();
            return map;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
