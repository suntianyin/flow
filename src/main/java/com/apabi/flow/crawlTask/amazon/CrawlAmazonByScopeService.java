package com.apabi.flow.crawlTask.amazon;

import com.apabi.flow.douban.dao.AmazonCrawlUrlDao;
import com.apabi.flow.douban.model.AmazonCrawlUrl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

/**
 * @Author pipi
 * @Date 2018/11/19 14:03
 **/
@Controller
@RequestMapping("/crawlAmazonByScope")
public class CrawlAmazonByScopeService {

    @Autowired
    private AmazonCrawlUrlDao amazonCrawlUrlDao;

    @RequestMapping("/insertUrl")
    @ResponseBody
    public String insertUrl() throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader("C:\\Users\\pirui\\Desktop\\crawl.txt"));
        String line = "";
        while ((line = bufferedReader.readLine()) != null) {
            AmazonCrawlUrl amazonCrawlUrl = new AmazonCrawlUrl();
            String description = line.split(",")[0];
            String url = line.split(",")[1];
            amazonCrawlUrl.setDescription(description);
            amazonCrawlUrl.setUrl(url);
            amazonCrawlUrl.setCreateTime(new Date());
            amazonCrawlUrlDao.insert(amazonCrawlUrl);
        }
        return "complete";
    }
}
