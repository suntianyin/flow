package com.apabi.flow.newspaper.chinanews.task;

import com.apabi.flow.newspaper.chinanews.util.ChinanewsCrawlUtils;
import com.apabi.flow.newspaper.cnr.util.CnrIpPoolUtils;
import com.apabi.flow.newspaper.dao.NewspaperDao;
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
public class CrawlChinanewsUrlTask {
    @Autowired
    private NewspaperDao newspaperDao;

    @ResponseBody
    @RequestMapping("/crawl")
    public String crawl() {
        CnrIpPoolUtils cnrIpPoolUtils = new CnrIpPoolUtils();
        for (int j = 1; j <= 500; j++) {
            try {
                ChinanewsCrawlUtils.crawlByUrl("http://channel.chinanews.com/cns/s/channel:sh.shtml?pager=" + j + "&pagenum=100&_=1541137697828", cnrIpPoolUtils,newspaperDao);
            } catch (Exception e) {
            }
        }
        return "complete";
    }
}
