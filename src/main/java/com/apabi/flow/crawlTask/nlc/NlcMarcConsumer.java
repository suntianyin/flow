package com.apabi.flow.crawlTask.nlc;

import com.apabi.flow.nlcmarc.dao.NlcBookMarcDao;
import com.apabi.flow.nlcmarc.model.NlcBookMarc;
import com.apabi.flow.nlcmarc.util.CrawlNlcMarcUtil;
import com.apabi.flow.nlcmarc.util.IpPoolUtils;
import com.apabi.flow.nlcmarc.util.ParseMarcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @Author pipi
 * @Date 2018/10/12 14:18
 **/
public class NlcMarcConsumer implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(NlcMarcConsumer.class);
    private ArrayBlockingQueue<String> isbnQueue;
    private NlcBookMarcDao nlcBookMarcDao;

    public NlcMarcConsumer(ArrayBlockingQueue<String> isbnQueue, NlcBookMarcDao nlcBookMarcDao) {
        this.isbnQueue = isbnQueue;
        this.nlcBookMarcDao = nlcBookMarcDao;
    }

    @Override
    public void run() {
        String isbn = "";
        String ip = "";
        String port = "";
        try {
            // 从阻塞队列中获取isbn
            isbn = isbnQueue.take();
            // 根据ip配置文件解析ip和port
            String host = IpPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            // 从国图抓取iso内容
            String isoContent = CrawlNlcMarcUtil.crawlNlcMarc(isbn, ip, port);
            // 解析marc数据
            NlcBookMarc nlcBookMarc = ParseMarcUtil.parseNlcBookMarc(isoContent);
            // 将解析好的NlcBookMarc数据插入到数据库
            nlcBookMarcDao.insertNlcMarc(nlcBookMarc);
            logger.info(Thread.currentThread().getName() + "使用" + ip + ":" + port + "在nlc抓取" + isbn + "成功...");
        } catch (InterruptedException e) {
            logger.error(Thread.currentThread().getName() + "使用" + ip + ":" + port + "在nlc抓取" + isbn + "失败...");
            e.printStackTrace();
        } catch (IOException e) {
            logger.error(Thread.currentThread().getName() + "使用" + ip + ":" + port + "在nlc抓取" + isbn + "失败...");
            e.printStackTrace();
        }
    }
}
