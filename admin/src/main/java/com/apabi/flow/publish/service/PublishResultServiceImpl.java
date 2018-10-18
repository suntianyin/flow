package com.apabi.flow.publish.service;

import com.apabi.flow.publish.dao.PublishResultDao;
import com.apabi.flow.publish.model.PublishResult;
import com.github.pagehelper.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @Author pipi
 * @Date 2018/9/14 11:00
 **/
@Service
public class PublishResultServiceImpl implements PublishResultService {
    @Autowired
    private PublishResultDao publishResultDao;

    @Override
    public Page<PublishResult> findPublishResultByPage(Map<String,String[]> params) {
        return publishResultDao.findPublishResultByPage(params);
    }
}
