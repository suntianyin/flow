package com.apabi.flow.isbn.service.impl;

import com.apabi.flow.isbn.dao.IsbnDao;
import com.apabi.flow.isbn.model.IsbnEntity;
import com.apabi.flow.isbn.service.IsbnService;
import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author pipi
 * @Date 2018/8/23 14:45
 **/
@Service
public class IsbnServiceImpl implements IsbnService {
    @Autowired
    IsbnDao isbnDao;

    @Override
    public List<List<IsbnEntity>> findIsbnEntityByIsbn(String isbn) {
        if (!StringUtils.isEmpty(isbn)) {
            List<List<IsbnEntity>> entityList = new ArrayList<List<IsbnEntity>>();
            String[] isbnList = isbn.split(",");
            for (String singleIsbn : isbnList) {
                singleIsbn = singleIsbn.trim();
                List<IsbnEntity> isbnEntityList = new ArrayList<>();
                if (singleIsbn.contains("-")) {
                    isbnEntityList = isbnDao.findIsbnEntityByIsbn(singleIsbn);
                    for (IsbnEntity entity : isbnEntityList) {
//                        entity.setIsbn(singleIsbn);
                        if (entity.getIsbn() != null && !"".equalsIgnoreCase(entity.getIsbn())) {
                            entity.setIsbn(entity.getIsbn());
                        }
                        if (entity.getIsbn10() != null && !"".equalsIgnoreCase(entity.getIsbn10())) {
                            entity.setIsbn10(entity.getIsbn10());
                        }
                        if (entity.getIsbn13() != null && !"".equalsIgnoreCase(entity.getIsbn13())) {
                            entity.setIsbn13(entity.getIsbn13());
                        }
                    }
                    if (isbnEntityList.size() == 0) {
                        IsbnEntity entity = new IsbnEntity();
                        entity.setIsbn(singleIsbn);
                        entity.setIsbn10("---");
                        entity.setIsbn13("---");
                        entity.setMetaId("---");
                        entity.setTitle("---");
                        entity.setAuthor("---");
                        entity.setPublisher("----");
                        entity.setHasCebx(0);
                        entity.setHasFlow(0);
                        isbnEntityList.add(entity);
                    }
                    entityList.add(isbnEntityList);
                    continue;
                }
                if (singleIsbn.length() == 13) {
                    isbnEntityList = isbnDao.findIsbnEntityByIsbn13(singleIsbn);
                    for (IsbnEntity entity : isbnEntityList) {
//                        entity.setIsbn(singleIsbn);
                        if (entity.getIsbn() != null && !"".equalsIgnoreCase(entity.getIsbn())) {
                            entity.setIsbn(entity.getIsbn());
                        }
                        if (entity.getIsbn10() != null && !"".equalsIgnoreCase(entity.getIsbn10())) {
                            entity.setIsbn10(entity.getIsbn10());
                        }
                        if (entity.getIsbn13() != null && !"".equalsIgnoreCase(entity.getIsbn13())) {
                            entity.setIsbn13(entity.getIsbn13());
                        }
                    }
                    if (isbnEntityList.size() == 0) {
                        IsbnEntity entity = new IsbnEntity();
                        entity.setIsbn("---");
                        entity.setIsbn10("---");
                        entity.setIsbn13(singleIsbn);
                        entity.setMetaId("---");
                        entity.setTitle("---");
                        entity.setAuthor("---");
                        entity.setPublisher("----");
                        entity.setHasCebx(0);
                        entity.setHasFlow(0);
                        isbnEntityList.add(entity);
                    }
                    entityList.add(isbnEntityList);
                    continue;
                }
                if (singleIsbn.length() == 10) {
                    isbnEntityList = isbnDao.findIsbnEntityByIsbn10(singleIsbn);
                    for (IsbnEntity entity : isbnEntityList) {
//                        entity.setIsbn(singleIsbn);
                        if (entity.getIsbn() != null && !"".equalsIgnoreCase(entity.getIsbn())) {
                            entity.setIsbn(entity.getIsbn());
                        }
                        if (entity.getIsbn10() != null && !"".equalsIgnoreCase(entity.getIsbn10())) {
                            entity.setIsbn10(entity.getIsbn10());
                        }
                        if (entity.getIsbn13() != null && !"".equalsIgnoreCase(entity.getIsbn13())) {
                            entity.setIsbn13(entity.getIsbn13());
                        }
                    }
                    if (isbnEntityList.size() == 0) {
                        IsbnEntity entity = new IsbnEntity();
                        entity.setIsbn("---");
                        entity.setIsbn10(singleIsbn);
                        entity.setIsbn13("---");
                        entity.setMetaId("---");
                        entity.setTitle("---");
                        entity.setAuthor("---");
                        entity.setPublisher("----");
                        entity.setHasCebx(0);
                        entity.setHasFlow(0);
                        isbnEntityList.add(entity);
                    }
                    entityList.add(isbnEntityList);
                    continue;
                }
            }
            return entityList;
        }
        return null;
    }

    @Override
    public String writeEmployeeListToExcel(String filePath, String[] excelTitle, List<List<IsbnEntity>> isbnEntity, String sheetName, ServletOutputStream outputStream) {
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
            rowIndex++;
            //循环写入主表数据
            for (List<IsbnEntity> entities : isbnEntity) {
                for (IsbnEntity entity : entities) {
                    //create sheet row
                    Row row = sheet.createRow(rowIndex);
                    //create sheet coluum(单元格)
                    Cell cell0 = row.createCell(0);
                    String isbn = entity.getIsbn();
                    if (isbn == null) {
                        isbn = "";
                    }
                    cell0.setCellValue(isbn);
                    Cell cell1 = row.createCell(1);
                    String isbn10 = entity.getIsbn10();
                    if (isbn10 == null) {
                        isbn10 = "";
                    }
                    cell1.setCellValue(isbn10);
                    Cell cell2 = row.createCell(2);
                    String isbn13 = entity.getIsbn13();
                    if (isbn13 == null) {
                        isbn13 = "";
                    }
                    cell2.setCellValue(isbn13);
                    Cell cell3 = row.createCell(3);
                    String metaId = entity.getMetaId();
                    if (metaId == null) {
                        metaId = "";
                    }
                    cell3.setCellValue(metaId);
                    Cell cell4 = row.createCell(4);
                    String title = entity.getTitle();
                    if (title == null) {
                        title = "";
                    }
                    cell4.setCellValue(title);
                    Cell cell5 = row.createCell(5);
                    String author = entity.getAuthor();
                    if (author == null) {
                        author = "";
                    }
                    cell5.setCellValue(author);
                    Cell cell6 = row.createCell(6);
                    String publisher = entity.getPublisher();
                    if (publisher == null) {
                        publisher = "";
                    }
                    cell6.setCellValue(publisher);
                    Cell cell7 = row.createCell(7);
                    Integer hasCebx = entity.getHasCebx();
                    if (hasCebx == null) {
                        hasCebx = 0;
                    }
                    cell7.setCellValue(hasCebx);
                    Cell cell8 = row.createCell(8);
                    Integer hasFlow = entity.getHasFlow();
                    if (hasFlow == null) {
                        hasFlow = 0;
                    }
                    cell8.setCellValue(hasFlow);
                    rowIndex++;
                }
            }
            workbook.write(outputStream);
            outputStream.flush();
            return filePath;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }
}
