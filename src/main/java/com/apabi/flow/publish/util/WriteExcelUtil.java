package com.apabi.flow.publish.util;

import com.apabi.flow.isbn.model.IsbnEntity;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * @Author pipi
 * @Date 2018/9/11 16:15
 **/

public class WriteExcelUtil {

    /**
     * 将List集合数据写入excel（单个sheet）
     *
     * @param filePath   文件路径
     * @param excelTitle 文件表头
     * @param sheetName  sheet名称
     */
    public static String writeEmployeeListToExcel(String filePath, String[] excelTitle, List<List<IsbnEntity>> isbnEntity,String sheetName) {
        System.out.println("开始写入文件>>>>>>>>>>>>");
        Workbook workbook = null;
        if (filePath.toLowerCase().endsWith("xls")) {//2003
            workbook = new XSSFWorkbook();
        } else if (filePath.toLowerCase().endsWith("xlsx")) {//2007
            workbook = new HSSFWorkbook();
        } else {
//			logger.debug("invalid file name,should be xls or xlsx");
        }
        //create sheet
        Sheet sheet = workbook.createSheet(sheetName);
        int rowIndex = 0;//标识位，用于标识sheet的行号
        //遍历数据集，将其写入excel中
        try {
            //写表头数据
            Row titleRow = sheet.createRow(rowIndex);
            for (int i = 0; i < excelTitle.length; i++) {
                //创建表头单元格,填值
                titleRow.createCell(i).setCellValue(excelTitle[i]);
            }
            System.out.println("表头写入完成>>>>>>>>");
            rowIndex++;
            //循环写入主表数据
            for(List<IsbnEntity> entities: isbnEntity){
                for(IsbnEntity entity:entities){
                    //create sheet row
                    Row row = sheet.createRow(rowIndex);
                    //create sheet coluum(单元格)
                    Cell cell0 = row.createCell(0);
                    cell0.setCellValue(entity.getIsbn());
                    Cell cell1 = row.createCell(1);
                    cell1.setCellValue(entity.getIsbn10());
                    Cell cell2 = row.createCell(2);
                    cell2.setCellValue(entity.getIsbn13());
                    Cell cell3 = row.createCell(3);
                    cell3.setCellValue(entity.getMetaId());
                    Cell cell4 = row.createCell(4);
                    cell4.setCellValue(entity.getTitle());
                    Cell cell5 = row.createCell(5);
                    cell5.setCellValue(entity.getAuthor());
                    Cell cell6 = row.createCell(6);
                    cell6.setCellValue(entity.getPublisher());
                    Cell cell7 = row.createCell(7);
                    cell7.setCellValue(entity.getHasCebx());
                    Cell cell8 = row.createCell(8);
                    cell8.setCellValue(entity.getHasFlow());
                    rowIndex++;
                }
            }
            System.out.println("主表数据写入完成>>>>>>>>");
            FileOutputStream fos = new FileOutputStream(filePath);
            workbook.write(fos);
            fos.close();
            System.out.println(filePath + "写入文件成功>>>>>>>>>>>");
            return filePath;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    //主函数
   /* public static void main(String[] args) {
        String[] excelTitle = {"表头1", "表头2"};
        writeEmployeeListToExcel("C:\\Users\\pirui\\Desktop\\test.xls", excelTitle, null,"test");
    }*/
}





