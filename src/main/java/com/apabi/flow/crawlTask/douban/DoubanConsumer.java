package com.apabi.flow.crawlTask.douban;

import com.apabi.flow.douban.dao.DoubanMetaDao;
import com.apabi.flow.douban.model.DoubanMeta;
import com.apabi.flow.douban.util.CrawlDoubanUtil;
import com.apabi.flow.nlcmarc.util.IpPoolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ArrayBlockingQueue;

/**
 * @Author pipi
 * @Date 2018/10/16 16:19
 **/
public class DoubanConsumer implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(DoubanConsumer.class);
    private ArrayBlockingQueue<String> idQueue;
    private DoubanMetaDao doubanMetaDao;

    public DoubanConsumer(ArrayBlockingQueue<String> idQueue, DoubanMetaDao doubanMetaDao) {
        this.idQueue = idQueue;
        this.doubanMetaDao = doubanMetaDao;
    }

    @Override
    public void run() {
        String id = "";
        String ip = "";
        String port = "";
        try {
            id = idQueue.take();
            String host = IpPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            DoubanMeta doubanMeta = CrawlDoubanUtil.crawlDoubanMetaById(id, ip, port);
            doubanMetaDao.insert(doubanMeta);
            logger.info(Thread.currentThread().getName() + "使用" + ip + ":" + port + "在douban抓取" + id + "成功...");
        } catch (InterruptedException e) {
            logger.info(Thread.currentThread().getName() + "使用" + ip + ":" + port + "在douban抓取" + id + "失败...");
            e.printStackTrace();
        }
    }
}
