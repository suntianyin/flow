package com.apabi.flow.crawlTask.nlc;

import com.apabi.flow.nlcmarc.dao.NlcBookMarcDao;
import com.apabi.flow.nlcmarc.model.NlcBookMarc;
import com.apabi.flow.nlcmarc.util.CrawlNlcMarcUtil;
import com.apabi.flow.crawlTask.util.IpPoolUtils;
import com.apabi.flow.nlcmarc.util.ParseMarcUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @Author pipi
 * @Date 2018/10/12 14:18
 **/
public class NlcMarcConsumer implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(NlcMarcConsumer.class);
    private ArrayBlockingQueue<String> isbnQueue;
    private NlcBookMarcDao nlcBookMarcDao;
    private IpPoolUtils ipPoolUtils;

    public NlcMarcConsumer(ArrayBlockingQueue<String> isbnQueue, NlcBookMarcDao nlcBookMarcDao,IpPoolUtils ipPoolUtils) {
        this.isbnQueue = isbnQueue;
        this.nlcBookMarcDao = nlcBookMarcDao;
        this.ipPoolUtils = ipPoolUtils;
    }

    @Override
    public void run() {
        String isbn = "";
        String ip = "";
        String port = "";
        String isoContent = "";
        NlcBookMarc nlcBookMarc = null;
        try {
            // 从阻塞队列中获取isbn
            isbn = isbnQueue.take();
            // 根据ip配置文件解析ip和port
            String host = ipPoolUtils.getIp();
            ip = host.split(":")[0];
            port = host.split(":")[1];
            // 从国图抓取iso内容
            isoContent = CrawlNlcMarcUtil.crawlNlcMarc(isbn, ip, port);
            // 解析marc数据
            nlcBookMarc = ParseMarcUtil.parseNlcBookMarc(isoContent);
            // 将解析好的NlcBookMarc数据插入到数据库
            nlcBookMarcDao.insertNlcMarc(nlcBookMarc);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date();
            String time = simpleDateFormat.format(date);
            logger.info(time+"  "+Thread.currentThread().getName() + "使用" + ip + ":" + port + "在nlc抓取" + isbn + "成功...");
        } catch (InterruptedException e) {
            logger.info(Thread.currentThread().getName() + "使用" + ip + ":" + port + "在nlc抓取" + isbn + "失败...");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
