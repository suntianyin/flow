package com.apabi.flow.publisher.service.impl;

import com.apabi.flow.publisher.dao.PublisherDao;
import com.apabi.flow.publisher.model.Publisher;
import com.apabi.flow.publisher.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import com.github.pagehelper.Page;
import org.springframework.stereotype.Service;

/**
 * @author wuji
 * @date 2018/8/10 10:49
 * @description
 */
@Service
public class PublisherServiceImpl implements PublisherService {

    @Autowired
    private PublisherDao publisherDao;


    @Override
    public Page<Publisher> queryPage() {
        return publisherDao.queryPage();
    }

    @Override
    public int addPubliser(Publisher publisher) {
        return publisherDao.addPubliser(publisher);
    }

    @Override
    public Publisher selectdataById(String id) {
        return publisherDao.selectdataById(id);
    }

    @Override
    public int editPublisher(Publisher publisher) {
        return publisherDao.editPublisher(publisher);
    }
}
