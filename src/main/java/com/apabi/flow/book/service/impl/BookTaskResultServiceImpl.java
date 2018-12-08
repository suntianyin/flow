package com.apabi.flow.book.service.impl;

import com.apabi.flow.book.dao.BookMetaDao;
import com.apabi.flow.book.dao.BookTaskMapper;
import com.apabi.flow.book.dao.BookTaskResultMapper;
import com.apabi.flow.book.model.BookMetaBatch;
import com.apabi.flow.book.model.BookTask;
import com.apabi.flow.book.model.BookTaskResult;
import com.apabi.flow.book.service.BookMetaService;
import com.apabi.flow.book.service.BookTaskResultService;
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
@Service("bookTaskResultService")
public class BookTaskResultServiceImpl implements BookTaskResultService {

    private static final Logger log = LoggerFactory.getLogger(BookTaskResultServiceImpl.class);

    @Autowired
    BookTaskResultMapper bookTaskResultMapper;

    //获取任务详情信息
    @Override
    public List<BookTaskResult> showTaskInfo(String taskId) {
        return bookTaskResultMapper.selectTaskInfoList(taskId);
    }
}
