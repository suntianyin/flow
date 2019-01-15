package com.apabi.flow.publisher.service.impl;

import com.apabi.flow.publisher.dao.PublisherDao;
import com.apabi.flow.publisher.model.Publisher;
import com.apabi.flow.publisher.service.PublisherService;
import org.springframework.beans.factory.annotation.Autowired;
import com.github.pagehelper.Page;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    public Page<Publisher> queryPage(String id, String title, String relatePublisherID) {
        return publisherDao.queryPage(id, title, relatePublisherID);
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

    @Override
    public List<Publisher> listPublishersByIdAndTitleAndRelatePublisherID(String id, String title, String relatePublisherID) {
        return publisherDao.listPublishersByIdAndTitleAndRelatePublisherID(id, title, relatePublisherID);
    }

    @Override
    public List<Publisher> findAll() {
        return publisherDao.findAll();
    }

    @Override
    public void compareStandardWithDB() {
        List<String> publisherList = new ArrayList<>();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("C:\\Users\\pirui\\Desktop\\出版社.txt"));
            String publisher = "";
            while ((publisher = bufferedReader.readLine()) != null) {
                publisherList.add(publisher);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        for (String publisherInText : publisherList) {
            int flag = 0;
            String companyAmbiguousInText = publisherInText.substring(0, 4);
            List<Publisher> publisherFindList = publisherDao.findAmbiguousByTitle(companyAmbiguousInText);
            if (publisherFindList != null && publisherFindList.size() > 0) {
                flag = 1;
            }
            if (flag == 0) {
                List<Publisher> byTitle = publisherDao.findByTitle(publisherInText);
                if (byTitle != null && byTitle.size() > 0) {
                    flag = 1;
                }
            }
            if (flag == 0) {
                System.out.println(publisherInText);
            }
        }
    }
}
