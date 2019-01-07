package com.apabi.flow.publisher.service;

import com.apabi.flow.publisher.model.Publisher;
import com.github.pagehelper.Page;

import java.util.List;

/**
 * @author guanpp
 * @date 2018/8/1 10:48
 * @description
 */
public interface PublisherService {

    //分页查询
    Page<Publisher> queryPage(String id, String title, String relatePublisherID);

    //添加数据
    int addPubliser(Publisher publisher);

    //根据id查询数据
    Publisher selectdataById(String id);

    //编辑数据
    int editPublisher(Publisher publisher);
    //条件查询
    List<Publisher> listPublishersByIdAndTitleAndRelatePublisherID(String id, String title, String relatePublisherID);
    //查询所有
    List<Publisher> findAll();

    void compareStandardWithDB();
}
