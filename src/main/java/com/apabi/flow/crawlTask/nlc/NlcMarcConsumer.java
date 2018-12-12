package com.apabi.flow.crawlTask.nlc;

import com.apabi.flow.crawlTask.exception.NoSuchIsbnException;
import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.nlcmarc.dao.NlcBookMarcDao;
import com.apabi.flow.nlcmarc.dao.NlcCrawlIsbnDao;
import com.apabi.flow.nlcmarc.model.NlcBookMarc;
import com.apabi.flow.nlcmarc.util.CrawlNlcMarcUtil;
import com.apabi.flow.nlcmarc.util.ParseMarcUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2018/10/12 14:18
 **/
public class NlcMarcConsumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(NlcMarcConsumer.class);
    private LinkedBlockingQueue<String> isbnQueue;
    private NlcBookMarcDao nlcBookMarcDao;
    private NlcCrawlIsbnDao nlcCrawlIsbnDao;
    private NlcIpPoolUtils ipPoolUtils;
    private CountDownLatch countDownLatch;

    public NlcMarcConsumer(LinkedBlockingQueue<String> isbnQueue, NlcBookMarcDao nlcBookMarcDao, NlcCrawlIsbnDao nlcCrawlIsbnDao, NlcIpPoolUtils ipPoolUtils, CountDownLatch countDownLatch) {
        this.isbnQueue = isbnQueue;
        this.nlcBookMarcDao = nlcBookMarcDao;
        this.nlcCrawlIsbnDao = nlcCrawlIsbnDao;
        this.ipPoolUtils = ipPoolUtils;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        String isbn = "";
        String ip = "";
        String port = "";
        String isoContent = "";
        NlcBookMarc nlcBookMarc = null;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            // 从阻塞队列中获取isbn
            isbn = isbnQueue.take();
            // 根据ip配置文件解析ip和port
            String host = ipPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            // 从国图抓取iso内容
            isoContent = CrawlNlcMarcUtil.crawlNlcMarc(isbn, ip, port);
            if (StringUtils.isNotEmpty(isoContent)) {
                // 解析marc数据
                nlcBookMarc = ParseMarcUtil.parseNlcBookMarc(isoContent);
                if (nlcBookMarc != null && nlcBookMarc.getNlcMarcId() != null) {
                    // 将解析好的NlcBookMarc数据插入到数据库
                    nlcBookMarcDao.insertNlcMarc(nlcBookMarc);
                    // 在nlc_crawl_isbn中把该isbn标记为hasCrawled = 1
                    nlcCrawlIsbnDao.updateHasCrawled(isbn);
                    Date date = new Date();
                    String time = simpleDateFormat.format(date);
                    LOGGER.info(time + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在nlc抓取" + nlcBookMarc.getIsbn() + "并添加至数据库成功，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                }
            }
        } catch (InterruptedException e) {
        } catch (IOException e) {
        } catch (NoSuchIsbnException e) {
            // 如果国图数据库中没有该条isbn，则把该数据标记为可疑数据
            nlcCrawlIsbnDao.markIsbnSuspect(isbn);
            Date date = new Date();
            String time = simpleDateFormat.format(date);
            LOGGER.info(time + "  " + Thread.currentThread().getName() + "国图中可能没有" + isbn + "这条数据...");
        } finally {
            countDownLatch.countDown();
        }
    }
}
