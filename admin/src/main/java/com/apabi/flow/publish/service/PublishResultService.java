package com.apabi.flow.publish.service;

import com.apabi.flow.publish.model.PublishResult;
import com.github.pagehelper.Page;

import java.util.Map;

/**
 * Created by pipi on 2018/9/14.
 */
public interface PublishResultService {
    public Page<PublishResult> findPublishResultByPage(Map<String,String[]> params);
}
