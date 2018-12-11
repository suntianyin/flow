package com.apabi.flow.book.dao;

import com.apabi.flow.book.model.BookTaskResult;

import java.util.List;

public interface BookTaskResultMapper {
    int deleteByPrimaryKey(String id);

    int insert(BookTaskResult record);

    int insertSelective(BookTaskResult record);

    BookTaskResult selectByPrimaryKey(String id);

    int updateByPrimaryKeySelective(BookTaskResult record);

    int updateByPrimaryKey(BookTaskResult record);

    //根据任务id，获取任务详情
    List<BookTaskResult> selectTaskInfoList(String id);

    //根据metaId，更改上传结果状态
    void updateTaskByMetaId(BookTaskResult record);

    //根据任务id，删除相关数据
    int deleteByTaskId(String id);
}