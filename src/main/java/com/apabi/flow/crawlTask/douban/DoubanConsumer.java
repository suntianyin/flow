package com.apabi.flow.crawlTask.douban;

import com.apabi.flow.douban.dao.DoubanMetaDao;
import com.apabi.flow.douban.model.DoubanMeta;
import com.apabi.flow.douban.util.CrawlDoubanUtil;
import com.apabi.flow.crawlTask.util.IpPoolUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
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
        DoubanMeta doubanMeta = null;
        try {
            id = idQueue.take();
            String host = IpPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            doubanMeta = CrawlDoubanUtil.crawlDoubanMetaById(id, ip, port);
            doubanMetaDao.insert(doubanMeta);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String time = simpleDateFormat.format(date);
            logger.info(time+"  "+Thread.currentThread().getName() + "使用" + ip + ":" + port + "在douban抓取" + id + "成功...");
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
