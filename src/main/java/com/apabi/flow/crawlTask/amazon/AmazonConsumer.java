package com.apabi.flow.crawlTask.amazon;

import com.apabi.flow.crawlTask.util.IpPoolUtils;
import com.apabi.flow.douban.dao.AmazonMetaDao;
import com.apabi.flow.douban.model.AmazonMeta;
import com.apabi.flow.douban.util.CrawlAmazonUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * @Author pipi
 * @Date 2018/10/17 15:03
 **/
public class AmazonConsumer implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(AmazonConsumer.class);
    private ArrayBlockingQueue<String> idQueue;
    private AmazonMetaDao amazonMetaDao;
    private IpPoolUtils ipPoolUtils;
    private CountDownLatch countDownLatch;

    public AmazonConsumer(ArrayBlockingQueue<String> idQueue, AmazonMetaDao amazonMetaDao, IpPoolUtils ipPoolUtils, CountDownLatch countDownLatch) {
        this.idQueue = idQueue;
        this.amazonMetaDao = amazonMetaDao;
        this.ipPoolUtils = ipPoolUtils;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        String id = "";
        String ip = "";
        String port = "";
        AmazonMeta amazonMeta = null;
        try {
            id = idQueue.take();
            String host = ipPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            amazonMeta = CrawlAmazonUtils.crawlAmazonMetaById(id, ip, port);
            if (StringUtils.isNotEmpty(amazonMeta.getAmazonId())) {
                amazonMetaDao.addAmazonMeta(amazonMeta);
                // 记录插入时间
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String time = simpleDateFormat.format(date);
                logger.info(time + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在amazon抓取" + id + "并添加至数据库成功，列表中剩余：" + countDownLatch.getCount() + "个数据...");
            }
        } catch (InterruptedException e) {
            //e.printStackTrace();
        } finally {
            countDownLatch.countDown();
        }
        /*catch (IOException e) {
            e.printStackTrace();
        }*/
    }
}
