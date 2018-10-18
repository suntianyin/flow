package com.apabi.flow.isbn.service;

import com.apabi.flow.isbn.model.IsbnEntity;

import javax.servlet.ServletOutputStream;
import java.util.List;

/**
 * @Author pipi
 * @Date 2018/8/23 14:44
 **/
public interface IsbnService {
    List<List<IsbnEntity>> findIsbnEntityByIsbn(String isbn);
    String writeEmployeeListToExcel(String filePath, String[] excelTitle, List<List<IsbnEntity>> isbnEntity, String sheetName, ServletOutputStream outputStream);
}
