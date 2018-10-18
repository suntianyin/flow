package com.apabi.flow.isbn.controller;

import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.isbn.model.IsbnEntity;
import com.apabi.flow.isbn.service.IsbnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @Author pipi
 * @Date 2018/8/23 14:50
 **/
@Controller("isbnController")
@RequestMapping("/isbn")
public class IsbnController {
    @Autowired
    private IsbnService isbnService;

    @RequestMapping("/isbnSearchIndex")
    public String isbnIndex() {
        return "isbn/isbnSearch";
    }

    @RequestMapping("/isbnSearch")
    @ResponseBody
    public List<List<IsbnEntity>> findIsbnEntityByIsbn(@RequestParam("isbn") String isbn) {
        List<List<IsbnEntity>> isbnEntity = isbnService.findIsbnEntityByIsbn(isbn);
        return isbnEntity;
    }

    /*@RequestMapping("/exportData")
    public ResponseEntity<byte[]> exportData(@RequestParam("isbn") String isbn, HttpServletResponse response) throws IOException {
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HHmmss");
        List<List<IsbnEntity>> isbnEntity = isbnService.findIsbnEntityByIsbn(isbn);
        String time = simpleDateFormat.format(date);
        File file = new File(time + "-" + UUIDCreater.nextId() + ".xls");
        if (!file.exists()) {
            file.createNewFile();
        }
        // 下载显示的文件名，解决中文名称乱码问题
        String downloadFileName = new String(file.getName().getBytes("UTF-8"), "ISO-8859-1");
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDispositionFormData("attachment", downloadFileName);
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

        String[] excelTitle = {"ISBN", "ISBN10", "ISBN13", "metaId", "书名", "作者", "出版单位", "是否有cebx", "是否是流式"};
        ResponseEntity<byte[]> entity = null;
        try {
            isbnService.writeEmployeeListToExcel(file.getName(), excelTitle, isbnEntity, "sheet1");
            entity = new ResponseEntity<>(FileUtils.readFileToByteArray(file), headers, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            file.delete();
        }
        return entity;
    }*/

    @RequestMapping("/exportData")
    @ResponseBody
    public String exportData(@RequestParam("isbn") String isbn, HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // 设置响应头
            response.setContentType("application/binary;charset=UTF-8");
            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            // 获取所有的isbn列表
            List<List<IsbnEntity>> isbnEntity = isbnService.findIsbnEntityByIsbn(isbn);
            // 获取输出流
            ServletOutputStream out = response.getOutputStream();
            // 设置文件名
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HHmmss");
            String time = simpleDateFormat.format(date);
            File file = new File(time + "-" + UUIDCreater.nextId() + ".xls");
            String fileName = new String(file.getName().getBytes("UTF-8"), "ISO-8859-1");
            // 设置响应头
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            // 设置excel的表头
            String[] excelTitle = {"ISBN", "ISBN10", "ISBN13", "metaId", "书名", "作者", "出版单位", "是否有cebx", "是否是流式"};
            // 将内存中读取到的内容写入到excel
            isbnService.writeEmployeeListToExcel(fileName, excelTitle, isbnEntity, "sheet1", out);
            return "success";
        } catch (Exception e) {
            e.printStackTrace();
            return "导出信息失败";
        }
    }
}
