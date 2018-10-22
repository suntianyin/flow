package com.apabi.flow.crawlTask.amazon;

import com.apabi.flow.douban.dao.AmazonMetaDao;
import com.apabi.flow.douban.model.AmazonMeta;
import com.apabi.flow.douban.util.CrawlAmazonUtils;
import com.apabi.flow.crawlTask.util.IpPoolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @Author pipi
 * @Date 2018/10/17 15:03
 **/
public class AmazonConsumer implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(AmazonConsumer.class);
    private ArrayBlockingQueue<String> idQueue;
    private AmazonMetaDao amazonMetaDao;

    public AmazonConsumer(ArrayBlockingQueue<String> idQueue, AmazonMetaDao amazonMetaDao) {
        this.idQueue = idQueue;
        this.amazonMetaDao = amazonMetaDao;
    }

    @Override
    public void run() {
        String id = "";
        String ip = "";
        String port = "";
        AmazonMeta amazonMeta = null;
        try {
            id = idQueue.take();
            String host = IpPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            amazonMeta = CrawlAmazonUtils.crawlAmazonMetaById(id, ip, port);
            amazonMetaDao.addAmazonMeta(amazonMeta);
            // 记录插入时间
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String time = simpleDateFormat.format(date);
            logger.info(time + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在amazon抓取" + id + "成功...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
