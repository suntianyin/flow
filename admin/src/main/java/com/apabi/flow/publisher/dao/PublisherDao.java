package com.apabi.flow.publisher.dao;


import com.apabi.flow.publisher.model.Publisher;
import com.github.pagehelper.Page;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author  wuji
 * @date 2018/8/10 10:44
 * @description
 */
@Repository
public interface PublisherDao {

    Page<Publisher> queryPage();

    int addPubliser(Publisher publisher);

    Publisher selectdataById(String id);

    int editPublisher(Publisher publisher);

    List<Publisher> findAll();
}
