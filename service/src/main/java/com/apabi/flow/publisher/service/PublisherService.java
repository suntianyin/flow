package com.apabi.flow.publisher.service;

import com.apabi.flow.publisher.model.Publisher;
import com.github.pagehelper.Page;

/**
 * @author guanpp
 * @date 2018/8/1 10:48
 * @description
 */
public interface PublisherService {

    //分页查询
    Page<Publisher> queryPage();

    //添加数据
    int addPubliser(Publisher publisher);

    //根据id查询数据
    Publisher selectdataById(String id);

    //编辑数据
    int editPublisher(Publisher publisher);
}
