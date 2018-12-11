package com.apabi.flow.book.service.impl;

import com.apabi.flow.book.dao.BookMetaDao;
import com.apabi.flow.book.dao.BookTaskMapper;
import com.apabi.flow.book.dao.BookTaskResultMapper;
import com.apabi.flow.book.model.BookMetaBatch;
import com.apabi.flow.book.model.BookTask;
import com.apabi.flow.book.model.BookTaskResult;
import com.apabi.flow.book.service.BookMetaService;
import com.apabi.flow.book.service.BookTaskService;
import com.apabi.flow.common.UUIDCreater;
import com.github.pagehelper.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    //查看任务列表
    @Override
    public Page<BookTask> showTaskList(Map<String, Object> queryMap) {
        return bookTaskMapper.showTaskList(queryMap);
    }

    //创建扫描文件任务
    @Override
    @Async
    public void createBookTask(String dirPath, String fileType) {
        if (!StringUtils.isEmpty(dirPath) && !StringUtils.isEmpty(fileType)) {
            BookTask bookTask = new BookTask();
            try {
                long start = System.currentTimeMillis();
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
                //扫描结果入库
                List<BookTaskResult> taskResultList = createBookTask(bookMetaList, bookTask);
                if (taskResultList != null && taskResultList.size() > 0) {
                    for (BookTaskResult result : taskResultList) {
                        bookTaskResultMapper.insert(result);
                    }
                }
                //更改任务列表状态
                bookTask.setStatus(2);
                bookTask.setUpdateTime(new Date());
                bookTaskMapper.updateByPrimaryKeySelective(bookTask);
                long end = System.currentTimeMillis();
                log.info("扫描任务{}已完成，耗时：{}毫秒", dirPath, (end - start));
            } catch (Exception e) {
                //将任务列表置成失败
                bookTask.setStatus(0);
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
}
