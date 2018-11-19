package com.apabi.flow.newspaper.task;

import com.apabi.flow.newspaper.cnr.util.CnrIpPoolUtils;
import com.apabi.flow.newspaper.dao.NewspaperDao;
import com.apabi.flow.newspaper.model.Newspaper;
import com.apabi.flow.newspaper.util.ParseHtmlMainBodyUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.*;

/**
 * @Author pipi
 * @Date 2018/11/7 14:08
 **/
@RestController
@RequestMapping("/crawlHtml")
public class CrawlHtmlContentTask {

    @Autowired
    private NewspaperDao newspaperDao;

    /**
     * 多线程抓取没有htmlContent的新闻报纸连接
     *
     * @return 当抓取完成后，返回"complete"
     */
    @RequestMapping("/execute")
    public String updateNoHtml() {
        int total = newspaperDao.countNoHtmlContent();
        int pageSize = 20000;
        int pageNum = (total / pageSize) + 1;
        LinkedBlockingQueue<String> urlQueue = new LinkedBlockingQueue<>(100);
        ExecutorService executorService = Executors.newFixedThreadPool(1000);
        for (int i = 0; i < pageNum; i++) {
            CnrIpPoolUtils cnrIpPoolUtils = new CnrIpPoolUtils();
            PageHelper.startPage(i, pageSize);
            Page<String> urlList = newspaperDao.findNoHtmlContentUrlsByPage();
            CountDownLatch countDownLatch = new CountDownLatch(urlList.size());
            CrawlHtmlContentProducer producer = new CrawlHtmlContentProducer(urlList, urlQueue);
            new Thread(producer).start();
            CrawlHtmlContentConsumer consumer = new CrawlHtmlContentConsumer(urlQueue, countDownLatch, cnrIpPoolUtils, newspaperDao);
            for (int j = 0; j < urlList.size(); j++) {
                executorService.execute(consumer);
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();
        return "complete";
    }

    /**
     * 解析新闻正文，并更新数据库
     *
     * @return 当解析完成后，返回"complete"
     */
    @RequestMapping("/parse")
    public String parseHtmlContent() {
        Integer count = newspaperDao.countWithHtmlContent();
        int pageSize = 5000;
        int pageNum = (count / pageSize) + 1;
        int success = 0;
        for (int i = 1; i <= pageNum; i++) {
            PageHelper.startPage(i, pageSize);
            Page<Newspaper> newspaperList = newspaperDao.findNewspaperWithoutMainBodyByPage();
            for (Newspaper newspaper : newspaperList) {
                String mainBody = ParseHtmlMainBodyUtil.parse(newspaper);
                newspaper.setMainBody(mainBody);
                if (mainBody != null) {
                    newspaperDao.update(newspaper);
                    System.out.println(newspaper.getTitle() + "插入数据库成功...");
                    //System.out.println(mainBody);
                    success++;
                }
            }
            System.out.println("第" + (i - 1) * 5000 + "到" + (i * 5000) + "条，共计成功了" + success);
        }
        System.out.println("成功解析了：" + success);
        return "complete";
    }
}