package com.apabi.flow.book.task;

import com.apabi.flow.book.model.BookBatchRes;
import com.apabi.flow.book.model.EpubookMeta;
import com.apabi.flow.book.service.BookMetaService;
import com.apabi.flow.book.util.GetEpubookChapter;
import com.apabi.flow.book.util.ReadBook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author guanpp
 * @date 2018/11/27 9:46
 * @description
 */
public class ReadEpubook implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(ReadEpubook.class);

    private LinkedBlockingQueue<String> filePathQueue;

    private Map<String, String> fileInfoMap;

    private List<BookBatchRes> bookBatchResList;

    private BookMetaService bookMetaService;

    private GetEpubookChapter getEpubookChapter;

    public ReadEpubook(LinkedBlockingQueue<String> filePathQueue,
                       Map<String, String> fileInfoMap,
                       List<BookBatchRes> bookBatchResList,
                       BookMetaService bookMetaService,
                       GetEpubookChapter getEpubookChapter) {
        this.filePathQueue = filePathQueue;
        this.fileInfoMap = fileInfoMap;
        this.bookBatchResList = bookBatchResList;
        this.bookMetaService = bookMetaService;
        this.getEpubookChapter = getEpubookChapter;
    }

    @Override
    public void run() {
        String filePath = null;
        String metaId;
        BookBatchRes bookBatchRes = new BookBatchRes();
        try {
            filePath = filePathQueue.take();
            metaId = fileInfoMap.get(filePath);
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
                    EpubookMeta res = getEpubookChapter.insertEpubook(filePath, epubookMeta, file.getName());
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
        }
    }
}
