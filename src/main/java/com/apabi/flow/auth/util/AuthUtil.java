package com.apabi.flow.auth.util;

import com.apabi.flow.auth.model.CopyrightAgreement;
import com.apabi.flow.book.util.BookUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileOutputStream;
import java.util.List;

/**
 * @author guanpp
 * @date 2019/2/18 15:13
 * @description
 */
public class AuthUtil {

    private static final Logger log = LoggerFactory.getLogger(BookUtil.class);

    //生成版权协议有效性检查结果
    public static void exportExcelEmail(List<CopyrightAgreement> emailResults, String excelPath) {
        FileOutputStream fos;
        try {
            Workbook book = new SXSSFWorkbook();
            Sheet sheet = book.createSheet("sheet1");
            //创建行头
            Row rowHead = sheet.createRow(0);
            Cell cellHead = rowHead.createCell(0);
            Cell cellHead1 = rowHead.createCell(1);
            Cell cellHead2 = rowHead.createCell(2);
            Cell cellHead3 = rowHead.createCell(3);
            Cell cellHead4 = rowHead.createCell(4);

            cellHead.setCellValue("版权协议编号");
            cellHead1.setCellValue("内容合作经理");
            cellHead2.setCellValue("版权所有者");
            cellHead3.setCellValue("是否自动续延");
            cellHead4.setCellValue("续延时间（年）");
            // 在对应的Excel中建立一个分表
            for (int i = 0; i < emailResults.size(); i++) {
                Row row = sheet.createRow(i + 1);
                // 在所在的行设置所在的单元格（相当于列，初始从0开始,对应的就是A列）
                Cell cell = row.createCell(0);
                Cell cell1 = row.createCell(1);
                Cell cell2 = row.createCell(2);
                Cell cell3 = row.createCell(3);
                Cell cell4 = row.createCell(4);

                cell.setCellValue(emailResults.get(i).getAgreementNum());
                cell1.setCellValue(emailResults.get(i).getContentManagerName());
                cell2.setCellValue(emailResults.get(i).getCopyrightOwner());
                Integer tmp = emailResults.get(i).getIsAutoPostpone();
                if (tmp == 0) {
                    cell3.setCellValue("否");
                } else if (tmp == 1) {
                    cell3.setCellValue("是");
                }
                cell4.setCellValue(emailResults.get(i).getYearNum());

            }
            fos = new FileOutputStream(excelPath);
            book.write(fos);
            fos.close();
        } catch (Exception e) {
            log.warn("版权有效性检查结果，生成异常{}", e.getMessage());
        }
    }
}
