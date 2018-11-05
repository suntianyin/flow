package com.apabi.flow.newspaper.chinanews.task;

import com.apabi.flow.newspaper.chinanews.util.NewChinanewsCrawlUtils;
import com.apabi.flow.newspaper.cnr.util.CnrIpPoolUtils;
import com.apabi.flow.newspaper.dao.NewspaperDao;
import org.apache.http.impl.client.CloseableHttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author pipi
 * @Date 2018/11/5 12:32
 **/
@Controller
@RequestMapping("/chinanews")
public class CrawlChinanewsTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(CrawlChinanewsTask.class);
    @Autowired
    private NewspaperDao newspaperDao;

    @ResponseBody
    @RequestMapping("/crawl")
    public String crawl() {
        CnrIpPoolUtils cnrIpPoolUtils = new CnrIpPoolUtils();
        for (int j = 1; j <= 42; j++) {
            try {
                String host = cnrIpPoolUtils.getIp();
                String ip = host.split(":")[0];
                String port = host.split(":")[1];
                CloseableHttpClient httpClient = NewChinanewsCrawlUtils.getCloseableHttpClient(ip, port);
                NewChinanewsCrawlUtils.crawlByUrl("http://channel.chinanews.com/cns/s/channel:gn.shtml?pager=" + j + "&pagenum=1000&_=1541136651338", httpClient, newspaperDao);
            } catch (Exception e) {
            }
        }
        return "complete";
    }


}
