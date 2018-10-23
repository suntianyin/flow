package com.apabi.flow.crawlTask.douban;

import com.apabi.flow.crawlTask.util.IpPoolUtils;
import com.apabi.flow.douban.util.CrawlDoubanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author pipi
 * @Date 2018/10/22 14:52
 **/
public class DoubanIdProducer implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(DoubanIdProducer.class);
    private String url;
    private List<String> idList;

    public DoubanIdProducer(String url, List<String> idList) {
        this.url = url;
        this.idList = idList;
    }

    @Override
    public void run() {
        long startTime = System.currentTimeMillis();
        // 随机指定代理ip抓取doubanId
        String ip = "";
        String port = "";
        String host = IpPoolUtils.getIp();
        ip = host.split(":")[0];
        port = host.split(":")[1];
        List<String> doubanIdList = new ArrayList<>();
        try {
            doubanIdList = CrawlDoubanUtil.crawlDoubanIdList(url, ip, port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        long endTime = System.currentTimeMillis();
        logger.info(Thread.currentThread().getName() + "使用" + ip + ":" + port + "提取url列表：" + url + "，列表大小为：" + doubanIdList.size() + ";耗时为：" + (endTime - startTime) / 1000 + "秒");
        for (String id : doubanIdList) {
            idList.add(id);
        }
    }
}
