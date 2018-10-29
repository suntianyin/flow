package com.apabi.flow.crawlTask.nlc;

import com.apabi.flow.crawlTask.util.IpPoolUtils;
import com.apabi.flow.douban.dao.ApabiBookMetaDataDao;
import com.apabi.flow.nlcmarc.dao.NlcBookMarcDao;
import com.apabi.flow.nlcmarc.model.NlcBookMarc;
import com.apabi.flow.nlcmarc.util.CrawlNlcMarcUtil;
import com.apabi.flow.nlcmarc.util.ParseMarcUtil;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * @Author pipi
 * @Date 2018/10/12 14:18
 **/
public class NlcMarcConsumer implements Runnable {
    private static Logger logger = LoggerFactory.getLogger(NlcMarcConsumer.class);
    private ArrayBlockingQueue<String> isbnQueue;
    private NlcBookMarcDao nlcBookMarcDao;
    private ApabiBookMetaDataDao apabiBookMetaDataDao;
    private IpPoolUtils ipPoolUtils;
    private CountDownLatch countDownLatch;

    public NlcMarcConsumer(ArrayBlockingQueue<String> isbnQueue, NlcBookMarcDao nlcBookMarcDao, ApabiBookMetaDataDao apabiBookMetaDataDao,IpPoolUtils ipPoolUtils, CountDownLatch countDownLatch) {
        this.isbnQueue = isbnQueue;
        this.nlcBookMarcDao = nlcBookMarcDao;
        this.apabiBookMetaDataDao = apabiBookMetaDataDao;
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
                    // 更新apabi_book_metadata中的数据
                    apabiBookMetaDataDao.updateNlcMarcId(nlcBookMarc.getNlcMarcId(),isbn);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date();
                    String time = simpleDateFormat.format(date);
                    logger.info(time + "  " + Thread.currentThread().getName() + "使用" + ip + ":" + port + "在nlc抓取" + nlcBookMarc.getIsbn() + "并添加至数据库成功，列表中剩余：" + countDownLatch.getCount() + "个数据...");
                }
            }
        } catch (InterruptedException e) {
        } catch (IOException e) {
        } finally {
            countDownLatch.countDown();
        }
    }
}
