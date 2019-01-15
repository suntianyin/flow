package com.apabi.flow.publisher.dao;


import com.apabi.flow.publisher.model.Publisher;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * @author  wuji
 * @date 2018/8/10 10:44
 * @description
 */
@Repository
@Mapper
public interface PublisherDao {

    List<Publisher> findAmbiguousByTitle(String title);

    List<Publisher> findByTitle(String title);

    Page<Publisher> queryPage(@Param("id")String id, @Param("title")String title, @Param("relatePublisherID")String relatePublisherID);

    int addPubliser(Publisher publisher);

    Publisher selectdataById(String id);

    int editPublisher(Publisher publisher);

    List<Publisher> findAll();

    List<Publisher> listPublishersByIdAndTitleAndRelatePublisherID(@Param("id")String id, @Param("title")String title, @Param("relatePublisherID")String relatePublisherID);

    String findIdByTitle(String title);
}
