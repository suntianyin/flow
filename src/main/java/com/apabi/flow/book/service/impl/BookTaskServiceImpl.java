package com.apabi.flow.book.service.impl;

import com.apabi.flow.book.dao.*;
import com.apabi.flow.book.model.*;
import com.apabi.flow.book.service.BookMetaService;
import com.apabi.flow.book.service.BookTaskService;
import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.douban.dao.ApabiBookMetaDataTempDao;
import com.apabi.flow.douban.model.ApabiBookMetaDataTemp;
import com.github.pagehelper.Page;
import oracle.jdbc.OracleDatabaseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.UncategorizedSQLException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author guanpp
 * @date 2018/12/8 10:25
 * @description
 */
@Service("bookTaskService")
public class BookTaskServiceImpl implements BookTaskService {

    private static final Logger log = LoggerFactory.getLogger(BookTaskServiceImpl.class);

    private static List<String> FILES = new ArrayList<>();

    private static List<String> XML_FILES = new ArrayList<>();

    private static List<String> CEBX_FILES = new ArrayList<>();

    private static final String EPUB_SUFFIX = "epub";

    private static final String CEBX_SUFFIX = "cebx";

    private static final String SPLIT_VALUE = "ISBN";

    private static final String CODE_VALUE = "UTF-8";

    @Autowired
    BookMetaDao bookMetaDao;

    @Autowired
    BookMetaService bookMetaService;

    @Autowired
    BookTaskMapper bookTaskMapper;

    @Autowired
    BookTaskResultMapper bookTaskResultMapper;

    @Autowired
    BookChapterDao bookChapterDao;

    @Autowired
    BookShardDao bookShardDao;

    @Autowired
    BookChapterBakDao bookChapterBakDao;

    @Autowired
    ApabiBookMetaDataTempDao bookMetaDataTempDao;

    //查看任务列表
    @Override
    public Page<BookTask> showTaskList(Map<String, Object> queryMap) {
        return bookTaskMapper.showTaskList(queryMap);
    }

    //创建扫描文件任务
    @Override
    @Async
    public void createBookTask(String dirPath, String fileType, Integer isCover) {
        if (!StringUtils.isEmpty(dirPath) && !StringUtils.isEmpty(fileType)) {
            BookTask bookTask = new BookTask();
            try {
                long start = System.currentTimeMillis();
                log.info("扫描任务{}开始", dirPath);
                List<BookMetaBatch> bookMetaList = new ArrayList<>();
                //创建任务列表
                bookTask.setId(UUIDCreater.nextId());
                bookTask.setTaskPath(dirPath);
                bookTask.setStatus(1);
                bookTask.setFileType(fileType);
                bookTask.setCreateTime(new Date());
                bookTaskMapper.insert(bookTask);
                if (fileType.toLowerCase().equals(EPUB_SUFFIX)) {
                    bookMetaList = bookMetaService.getBookMetaEpubBatch(dirPath);
                } else if (fileType.toLowerCase().equals(CEBX_SUFFIX)) {
                    bookMetaList = bookMetaService.getBookMetaCebxBatch(dirPath);
                }
                boolean res = true;
                if (!StringUtils.isEmpty(isCover) && isCover == 1) {
                    //备份章节内容
                    if (bookMetaList != null && bookMetaList.size() > 0) {
                        List<String> metaIds = new ArrayList<>();
                        for (BookMetaBatch bookMetaBatch : bookMetaList) {
                            metaIds.add(bookMetaBatch.getMetaId());
                        }
                        res = bakBookChapter(metaIds);
                    }
                }
                if (res) {
                    //扫描结果入库
                    List<BookTaskResult> taskResultList = createBookTask(bookMetaList, bookTask);
                    if (taskResultList != null && taskResultList.size() > 0) {
                        for (BookTaskResult result : taskResultList) {
                            //如果章节内容进行备份覆盖
                            if (!StringUtils.isEmpty(isCover) && isCover == 1) {
                                result.setHasFlow(0);
                            }
                            bookTaskResultMapper.insert(result);
                        }
                    }
                    //更改任务列表状态
                    bookTask.setStatus(2);
                    bookTask.setUpdateTime(new Date());
                    bookTaskMapper.updateByPrimaryKeySelective(bookTask);
                    long end = System.currentTimeMillis();
                    log.info("扫描任务{}已完成，耗时：{}毫秒", dirPath, (end - start));
                } else {
                    //将任务列表置成失败
                    bookTask.setStatus(0);
                    bookTask.setUpdateTime(new Date());
                    bookTaskMapper.updateByPrimaryKeySelective(bookTask);
                    log.info("扫描任务{}时，备份失败", dirPath);
                }
            } catch (DataAccessException e) {
                //将任务列表置成失败
                bookTask.setStatus(0);
                bookTask.setUpdateTime(new Date());
                bookTaskMapper.updateByPrimaryKeySelective(bookTask);
                log.info("扫描任务{}时，出现异常{}", dirPath, e.getMessage());
            }
        }
    }

    //转换为扫描文件任务
    private List<BookTaskResult> createBookTask(List<BookMetaBatch> bookMetaList, BookTask bookTask) {
        if (bookMetaList != null && bookMetaList.size() > 0) {
            List<BookTaskResult> bookTaskList = new ArrayList<>();
            for (BookMetaBatch bookMetaBatch : bookMetaList) {
                BookTaskResult taskResult = new BookTaskResult();
                taskResult.setId(UUIDCreater.nextId());
                taskResult.setTitle(bookMetaBatch.getTitle());
                taskResult.setTaskId(bookTask.getId());
                taskResult.setStatus(2);
                taskResult.setFileIsbn(bookMetaBatch.getFileIsbn());
                taskResult.setPublisher(bookMetaBatch.getPublisher());
                taskResult.setMetaId(bookMetaBatch.getMetaId());
                taskResult.setIsbn13(bookMetaBatch.getIsbn13());
                taskResult.setIsbn(bookMetaBatch.getIsbn());
                taskResult.setHasFlow(bookMetaBatch.getHasFlow());
                taskResult.setFileName(bookMetaBatch.getFileName());
                taskResult.setCreator(bookMetaBatch.getCreator());
                taskResult.setCreateTime(new Date());
                bookTaskList.add(taskResult);
            }
            return bookTaskList;
        }
        return null;
    }

    //获取任务
    @Override
    public BookTask selectBookTask(String id) {
        return bookTaskMapper.selectByPrimaryKey(id);
    }

    //删除任务
    @Override
    public int deleteBookTask(String id) {
        if (!StringUtils.isEmpty(id)) {
            //删除任务扫描结果
            bookTaskResultMapper.deleteByTaskId(id);
            //删除任务
            return bookTaskMapper.deleteByPrimaryKey(id);
        }
        return 0;
    }

    //备份图书章节内容
    private boolean bakBookChapter(List<String> metaIds) {
        if (metaIds != null && metaIds.size() > 0) {
            for (String metaId : metaIds) {
                long start = System.currentTimeMillis();
                //获取章节内容
                List<BookChapter> bookChapterOlds = bookChapterDao.findAllBookChapter(metaId);
                if (bookChapterOlds.size() > 0) {
                    //新增到备份数据库
                    for (BookChapter bookChapter : bookChapterOlds) {
                        BookChapterVo bookChapterBak = new BookChapterVo();
                        bookChapterBak.setId(UUIDCreater.nextId());
                        bookChapterBak.setComId(bookChapter.getComId());
                        bookChapterBak.setWordSum(bookChapter.getWordSum());
                        bookChapterBak.setShardSum(bookChapter.getShardSum());
                        bookChapterBak.setContent(bookChapter.getContent());
                        bookChapterBak.setChapterNum(bookChapter.getChapterNum());
                        bookChapterBak.setBodyClass(bookChapter.getBodyClass());
                        bookChapterBak.setCreateTime(new Date());
                        bookChapterBak.setUpdateTime(new Date());
                        bookChapterBakDao.insertBookChapterVo(bookChapterBak);
                    }
                    //删除原内容
                    bookChapterDao.deleteAllBookChapter(metaId);
                    bookShardDao.deleteAllBookShard(metaId);
                    //更新图书元数据
                    BookMeta bookMeta = new BookMeta();
                    bookMeta.setMetaId(metaId);
                    bookMeta.setChapterNum(0);
                    bookMeta.setStyleUrl("");
                    bookMeta.setContentNum(0);
                    bookMeta.setStreamCatalog("");
                    bookMeta.setHasFlow(0);
                    bookMeta.setIsOptimize(0);
                    bookMeta.setFlowSource("");
                    bookMeta.setUpdateTime(new Date());
                    bookMetaDao.updateBookMetaById(bookMeta);
                    //更新图书元数据，temp表
                    ApabiBookMetaDataTemp bookMetaDataTemp = new ApabiBookMetaDataTemp();
                    bookMetaDataTemp.setMetaId(metaId);
                    bookMetaDataTemp.setChapterNum(0);
                    bookMetaDataTemp.setStyleUrl("");
                    bookMetaDataTemp.setContentNum(0);
                    bookMetaDataTemp.setStreamCatalog("");
                    bookMetaDataTemp.setHasFlow(0);
                    bookMetaDataTemp.setIsOptimize(0);
                    bookMetaDataTemp.setFlowSource("");
                    bookMetaDataTemp.setUpdateTime(new Date());
                    bookMetaDataTempDao.update(bookMetaDataTemp);
                }
                long end = System.currentTimeMillis();
                log.info("图书{}章节内容备份成功，耗时{}", metaId, (end - start));
            }
            return true;
        }
        return false;
    }
}
