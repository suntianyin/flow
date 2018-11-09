package com.apabi.flow.book.util;

import com.apabi.flow.book.model.BookBatchRes;
import com.apabi.flow.book.model.EpubookMeta;
import com.apabi.flow.book.service.BookMetaService;
import com.apabi.flow.config.ApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author guanpp
 * @date 2018/8/1 16:10
 * @description
 */
@Service("readBook")
public class ReadBook {

    private static final Logger log = LoggerFactory.getLogger(GetEpubookChapter.class);

    @Autowired
    GetEpubookChapter getEpubookChapter;

    @Autowired
    GetCebxChapter getCebxChapter;

    @Autowired
    BookMetaService bookMetaService;

    @Autowired
    ApplicationConfig config;

    //读取源文件
    public int readEpubook(String filePath, String metId) throws Exception {
        if (!StringUtils.isEmpty(filePath) && !StringUtils.isEmpty(metId)) {
            File file = new File(filePath);
            if (file != null) {
                List<EpubookMeta> epubookMetas = new ArrayList<>();
                EpubookMeta epubookMeta = bookMetaService.selectEpubookMetaById(metId);
                //判断是否生成章节内容
                boolean flag = (epubookMeta.getChapterNum() != null) && (epubookMeta.getChapterNum() > 0);
                if (flag) {
                    return -1;
                }
                try {
                    //epubookMeta.setFileName(file.getName());
                    EpubookMeta res = getEpubookChapter.insertEpubook(filePath, epubookMeta, file.getName());
                    if (res != null && res.getMetaid() != null) {
                        epubookMetas.add(res);
                        return 1;
                    }
                } catch (IOException e) {
                    log.warn("解析图书：" + file + "时{}" + e);
                }
                //生成文件名和图书id映射表
                //BookUtil.exportExcel(epubookMetas);
            } else {
                log.warn(filePath + "：该路径下没有文件！");
            }
        }
        return 0;
    }

    //批量发布Epub
    public List<BookBatchRes> batchChapterEpub(String fileInfo, String filePath) throws Exception {
        if (!StringUtils.isEmpty(fileInfo) && !StringUtils.isEmpty(filePath)) {
            String[] fileInfos = fileInfo.split(";");
            if (fileInfos != null && fileInfos.length > 0) {
                List<BookBatchRes> bookBatchResList = new ArrayList<>();
                for (String file : fileInfos) {
                    String[] fileId = file.split(",");
                    if (fileId != null && fileId.length == 2) {
                        //传入文件路径和图书metaId
                        int res = readEpubook(filePath + File.separator + fileId[1], fileId[0]);
                        BookBatchRes bookBatchRes = new BookBatchRes();
                        bookBatchRes.setFileName(fileId[1]);
                        bookBatchRes.setMetaId(fileId[0]);
                        if (res > 0) {
                            bookBatchRes.setStatus(1);
                        } else {
                            bookBatchRes.setStatus(0);
                        }
                        bookBatchResList.add(bookBatchRes);
                    }
                }
                return bookBatchResList;
            }
        }
        return null;
    }

    //读取cebx源文件
    public int readCebxBook(String filePath, String metId) throws Exception {
        if (!StringUtils.isEmpty(filePath) && !StringUtils.isEmpty(metId)) {
            File file = new File(filePath);
            if (file != null) {
                List<EpubookMeta> epubookMetas = new ArrayList<>();
                EpubookMeta epubookMeta = bookMetaService.selectEpubookMetaById(metId);
                //判断是否生成章节内容
                boolean flag = (epubookMeta.getChapterNum() != null) && (epubookMeta.getChapterNum() > 0);
                if (flag) {
                    return -1;
                }
                try {
                    EpubookMeta res = getCebxChapter.insertCebx(filePath, epubookMeta, file.getName());
                    if (res != null && res.getMetaid() != null) {
                        epubookMetas.add(res);
                        return 1;
                    }
                } catch (Exception e) {
                    log.warn("解析图书失败：" + file + "时{}" + e);
                } finally {
                    File dir = new File(config.getTargetCebxDir());
                    String[] fileList = dir.list();
                    deleteFiles(fileList);
                }
                //生成文件名和图书id映射表
                //BookUtil.exportExcel(epubookMetas);
            } else {
                log.warn(filePath + "：该路径下没有文件！");
            }
        }
        return 0;
    }

    //删除文件
    private void deleteFiles(String[] filePaths) {
        if (filePaths != null && filePaths.length > 0) {
            for (String filePath : filePaths) {
                File file = new File(config.getTargetCebxDir() + File.separator + filePath);
                if (file.exists()) {
                    file.delete();
                }
            }
            log.info("cebx的html文件删除成功");
        }
    }

    //批量发布Cebx
    public List<BookBatchRes> batchChapterCebx(String fileInfo, String filePath) throws Exception {
        if (!StringUtils.isEmpty(fileInfo) && !StringUtils.isEmpty(filePath)) {
            String[] fileInfos = fileInfo.split(";");
            if (fileInfos != null && fileInfos.length > 0) {
                List<BookBatchRes> bookBatchResList = new ArrayList<>();
                for (String file : fileInfos) {
                    String[] fileId = file.split(",");
                    if (fileId != null && fileId.length == 2) {
                        //传入文件路径和图书metaId
                        int res = readCebxBook(filePath + File.separator + fileId[1], fileId[0]);
                        BookBatchRes bookBatchRes = new BookBatchRes();
                        bookBatchRes.setFileName(fileId[1]);
                        bookBatchRes.setMetaId(fileId[0]);
                        if (res > 0) {
                            bookBatchRes.setStatus(1);
                        } else {
                            bookBatchRes.setStatus(0);
                        }
                        bookBatchResList.add(bookBatchRes);
                    }
                }
                return bookBatchResList;
            }
        }
        return null;
    }

}
