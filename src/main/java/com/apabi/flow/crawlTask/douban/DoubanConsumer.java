package com.apabi.flow.crawlTask.douban;

import com.apabi.flow.crawlTask.util.IpPoolUtils;
import com.apabi.flow.douban.dao.DoubanMetaDao;
import com.apabi.flow.douban.model.DoubanMeta;
import com.apabi.flow.douban.util.CrawlDoubanUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * @Author pipi
 * @Date 2018/10/16 16:19
 **/
public class DoubanConsumer implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(DoubanConsumer.class);
    private ArrayBlockingQueue<String> idQueue;
    private DoubanMetaDao doubanMetaDao;
    private IpPoolUtils ipPoolUtils;
    private CountDownLatch countDownLatch;

    public DoubanConsumer(ArrayBlockingQueue<String> idQueue, DoubanMetaDao doubanMetaDao, IpPoolUtils ipPoolUtils, CountDownLatch countDownLatch) {
        this.idQueue = idQueue;
        this.doubanMetaDao = doubanMetaDao;
        this.ipPoolUtils = ipPoolUtils;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        String id = "";
        String ip = "";
        String port = "";
        DoubanMeta doubanMeta = null;
        try {
            id = idQueue.take();
            String host = ipPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            doubanMeta = CrawlDoubanUtil.crawlDoubanMetaById(id, ip, port);
            if (StringUtils.isNotEmpty(doubanMeta.getDoubanId())) {
                doubanMetaDao.insert(doubanMeta);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                Date date = new Date();
                String time = simpleDateFormat.format(date);
                logger.info(time + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在douban抓取" + id + "并添加至数据库成功，列表中剩余：" + countDownLatch.getCount() + "个数据...");
            }
        } catch (InterruptedException e) {
            //e.printStackTrace();
        } finally {
            countDownLatch.countDown();
        }
    }
}
