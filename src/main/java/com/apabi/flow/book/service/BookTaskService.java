package com.apabi.flow.book.service;

import com.apabi.flow.book.model.BookTask;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

/**
 * @author guanpp
 * @date 2018/12/8 10:23
 * @description
 */
public interface BookTaskService {

    //创建扫描文件任务
    void createBookTask(String dirPath, String fileType);

    //查看任务列表
    Page<BookTask> showTaskList(Map<String, Object> queryMap);

    //获取任务
    BookTask selectBookTask(String id);

    //删除任务
    int deleteBookTask(String id);
}
