package com.apabi.flow.isbn_douban_amazon.service.impl;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.isbn_douban_amazon.dao.IsbnDangdangDao;
import com.apabi.flow.isbn_douban_amazon.model.IsbnDangdang;
import com.apabi.flow.nlcmarc.dao.NlcBookMarcDao;
import com.apabi.flow.nlcmarc.model.NlcBookMarc;
import com.apabi.flow.nlcmarc.util.CrawlNlcMarcUtil;
import com.apabi.flow.nlcmarc.util.ParseMarcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @Author pipi
 * @Date 2019-1-18 15:36
 **/
public class IsbnDangdangCrawlNlcConsumer implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(IsbnDangdangCrawlNlcConsumer.class);
    private LinkedBlockingQueue<IsbnDangdang> isbnDangdangQueue;
    private CountDownLatch countDownLatch;
    private NlcIpPoolUtils nlcIpPoolUtils;
    private NlcBookMarcDao nlcBookMarcDao;
    private IsbnDangdangDao isbnDangdangDao;

    public IsbnDangdangCrawlNlcConsumer(LinkedBlockingQueue<IsbnDangdang> isbnDangdangQueue, CountDownLatch countDownLatch, NlcIpPoolUtils nlcIpPoolUtils, NlcBookMarcDao nlcBookMarcDao, IsbnDangdangDao isbnDangdangDao) {
        this.isbnDangdangQueue = isbnDangdangQueue;
        this.countDownLatch = countDownLatch;
        this.nlcIpPoolUtils = nlcIpPoolUtils;
        this.nlcBookMarcDao = nlcBookMarcDao;
        this.isbnDangdangDao = isbnDangdangDao;
    }

    @Override
    public void run() {
        IsbnDangdang isbnDangdang = null;
        String ip = "";
        String port = "";
        String isbn = "";
        try {
            String host = nlcIpPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            isbnDangdang = isbnDangdangQueue.take();
            isbn = isbnDangdang.getIsbn();
            try {
                NlcBookMarc nlcBookMarc = ParseMarcUtil.parseNlcBookMarc(CrawlNlcMarcUtil.crawlNlcMarc(isbn, ip, port));
                if (nlcBookMarc != null) {
                    isbnDangdangDao.updateNlcHasCrawled(isbn);
                    try {
                        nlcBookMarcDao.insertNlcMarc(nlcBookMarc);
                        LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "在nlc抓取" + isbn + "并插入数据库成功，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                    } catch (Exception e) {
                        LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "在nlc抓取" + isbn + "在数据库中已存在，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                    }
                }
            } catch (Exception e) {
                LOGGER.info(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "  " + Thread.currentThread().getName() + "在nlc抓取" + isbn + "失败，列表中剩余：" + countDownLatch.getCount() + "个数据...");
            }
        } catch (Exception e) {
        } finally {
            countDownLatch.countDown();
        }
    }
}
