package com.apabi.flow.newspaper.crawl_url.service;

import com.apabi.flow.newspaper.crawl_url.model.NewspaperCrawlUrl;
import com.apabi.flow.newspaper.dao.NewspaperCrawlUrlDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

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
    @ResponseBody
    public String insert() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\pirui\\Desktop\\url.txt"), "UTF-8"));
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
            NewspaperCrawlUrl newspaperCrawlUrl = new NewspaperCrawlUrl();
            String url = line.split(";")[0];
            String pageNum = line.split(";")[1];
            int i = Integer.parseInt(pageNum);
            newspaperCrawlUrl.setSource("中国日报网");
            newspaperCrawlUrl.setIndexPage(url);
            newspaperCrawlUrl.setPageNum(Integer.parseInt(pageNum));
            newspaperCrawlUrlDao.insert(newspaperCrawlUrl);
        }
        return "complete";
    }
}
