package com.apabi.flow.newspaper.crawl_url.service;

import com.apabi.flow.newspaper.crawl_url.model.NewspaperCrawlUrl;
import com.apabi.flow.newspaper.dao.NewspaperCrawlUrlDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @Author pipi
 * @Date 2018/11/7 10:19
 **/
@Controller
@RequestMapping("/crawlUrl")
public class NewspaperCrawlUrlService {

    @Autowired
    private NewspaperCrawlUrlDao newspaperCrawlUrlDao;

    @RequestMapping("/insert")
    public String insert() {

        return "complete";
    }
}
