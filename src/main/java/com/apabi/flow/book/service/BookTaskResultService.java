package com.apabi.flow.book.service;

import com.apabi.flow.book.model.BookTask;
import com.apabi.flow.book.model.BookTaskResult;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

/**
 * @author guanpp
 * @date 2018/12/8 10:23
 * @description
 */
public interface BookTaskResultService {

    //获取任务详细信息
    List<BookTaskResult> showTaskInfo(String taskId);
}
