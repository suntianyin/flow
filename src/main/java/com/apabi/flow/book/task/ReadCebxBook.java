package com.apabi.flow.book.task;

import com.apabi.flow.book.model.BookBatchRes;
import com.apabi.flow.book.model.EpubookMeta;
import com.apabi.flow.book.service.BookMetaService;
import com.apabi.flow.book.util.GetCebxChapter;
import com.apabi.flow.book.util.GetEpubookChapter;
import com.apabi.flow.config.ApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author guanpp
 * @date 2018/11/28 9:16
 * @description
 */
public class ReadCebxBook implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ReadCebxBook.class);

    private LinkedBlockingQueue<String> filePathQueue;

    private Map<String, String> fileInfoMap;

    private List<BookBatchRes> bookBatchResList;

    ApplicationConfig config;

    private BookMetaService bookMetaService;

    private GetCebxChapter getCebxChapter;

    public ReadCebxBook(LinkedBlockingQueue<String> filePathQueue,
                        Map<String, String> fileInfoMap,
                        List<BookBatchRes> bookBatchResList,
                        BookMetaService bookMetaService,
                        GetCebxChapter getCebxChapter,
                        ApplicationConfig config) {
        this.filePathQueue = filePathQueue;
        this.fileInfoMap = fileInfoMap;
        this.bookBatchResList = bookBatchResList;
        this.bookMetaService = bookMetaService;
        this.getCebxChapter = getCebxChapter;
        this.config = config;
    }

    @Override
    public void run() {
        String filePath = null;
        String metaId;
        BookBatchRes bookBatchRes = new BookBatchRes();
        try {
            /*filePath = filePathQueue.take();
            metaId = fileInfoMap.get(filePath);*/
            metaId = filePathQueue.take();
            filePath = fileInfoMap.get(metaId);
            File file = new File(filePath);
            bookBatchRes.setFileName(file.getName());
            bookBatchRes.setMetaId(metaId);
            if (!StringUtils.isEmpty(filePath) && !StringUtils.isEmpty(metaId)) {
                if (file != null) {
                    EpubookMeta epubookMeta = bookMetaService.selectEpubookMetaById(metaId);
                    //判断是否生成章节内容
                    boolean flag = (epubookMeta.getChapterNum() != null) && (epubookMeta.getChapterNum() > 0);
                    if (flag) {
                        bookBatchRes.setStatus(0);
                        return;
                    }
                    EpubookMeta res = getCebxChapter.insertCebx(filePath, epubookMeta, file.getName());
                    if (res != null && res.getMetaid() != null) {
                        bookBatchRes.setStatus(1);
                    } else {
                        bookBatchRes.setStatus(0);
                    }
                } else {
                    bookBatchRes.setStatus(0);
                    log.warn(filePath + "：该路径下没有文件！");
                }
            } else {
                bookBatchRes.setStatus(0);
            }
        } catch (Exception e) {
            bookBatchRes.setStatus(0);
            log.warn("解析图书：" + filePath + "时{}" + e);
            e.printStackTrace();
        } finally {
            synchronized (bookBatchResList) {
                bookBatchResList.add(bookBatchRes);
            }
            /*File dir = new File(config.getTargetCebxDir());
            File[] fileList = dir.listFiles();
            deleteFiles(fileList);
            log.info("cebx的html文件删除成功");*/
        }
    }

    //删除文件
    private void deleteFiles(File[] files) {
        if (files != null && files.length > 0) {
            for (File file : files) {
                File[] children = file.listFiles();
                if (children != null && children.length > 0) {
                    deleteFiles(children);
                } else {
                    if (file.exists()) {
                        file.delete();
                    }
                }
            }
        }
    }
}
