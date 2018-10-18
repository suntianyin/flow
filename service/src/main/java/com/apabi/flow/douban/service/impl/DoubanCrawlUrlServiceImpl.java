package com.apabi.flow.douban.service.impl;

import com.apabi.flow.douban.dao.DoubanCrawlUrlDao;
import com.apabi.flow.douban.service.DoubanCrawlUrlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author pipi
 * @Date 2018/10/15 17:29
 **/
@Service
public class DoubanCrawlUrlServiceImpl implements DoubanCrawlUrlService {
    @Autowired
    private DoubanCrawlUrlDao doubanCrawlUrlDao;
    @Override
    public List<String> findAllUrl() {
        return doubanCrawlUrlDao.findAllUrl();
    }
}
