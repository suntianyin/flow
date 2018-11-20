package com.apabi.flow.book.fetchPage;

import com.apabi.flow.book.dao.BookPageMapper;
import com.apabi.flow.book.dao.PageCrawledTempMapper;
import com.apabi.flow.book.model.BookPage;
import com.apabi.flow.book.model.PageCrawledTemp;
import com.apabi.flow.book.service.impl.BookMetaServiceImpl;
import com.apabi.flow.book.util.EbookUtil;
import com.apabi.flow.book.util.HttpUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

/**
 * @author: sunty
 * @date: 2018/11/07 14:14
 * @description:
 */

public class FetchPageAgainConsumer implements Runnable {
    private static Logger log = LoggerFactory.getLogger(FetchPageAgainConsumer.class);
    private String confvalue;
    private ArrayBlockingQueue<PageCrawledTemp> idQueue;
    private BookPageMapper bookPageMapper;
    private CountDownLatch countDownLatch;
    private Integer sleepTime;
    private PageCrawledTempMapper pageCrawledTempMapper;
    private String width;
    private String height;


    public FetchPageAgainConsumer(CountDownLatch countDownLatch, String confvalue, ArrayBlockingQueue<PageCrawledTemp> idQueue, BookPageMapper bookPageMapper, PageCrawledTempMapper pageCrawledTempMapper, Integer sleepTime, String width,
                                  String height) {
        this.confvalue = confvalue;
        this.idQueue = idQueue;
        this.bookPageMapper = bookPageMapper;
        this.countDownLatch = countDownLatch;
        this.sleepTime = sleepTime;
        this.pageCrawledTempMapper = pageCrawledTempMapper;
        this.height = height;
        this.width = width;
    }

    @Override
    public void run() {
        HttpEntity httpEntity = null;
        String url = null;
        String metaId = null;
        long i = 1;
        int i1 = 0;
        try {
            PageCrawledTemp pageCrawledTemp = idQueue.take();
            metaId = pageCrawledTemp.getId();
            i = pageCrawledTemp.getPage();
            Thread.sleep(sleepTime);
            url = EbookUtil.makePageUrl(confvalue, BookMetaServiceImpl.shuyuanOrgCode, metaId, BookMetaServiceImpl.urlType, BookMetaServiceImpl.serviceType, width, height, i);
            long a = System.currentTimeMillis();
            httpEntity = HttpUtils.doGetEntity(url);
            String tmp = EntityUtils.toString(httpEntity);
            long b = System.currentTimeMillis();
            int word = 0;
            if (StringUtils.isBlank(tmp)) {
                tmp = "<span></span>";
            } else {
                org.jsoup.nodes.Document doc;
                doc = Jsoup.parse(tmp.toString());
                word = doc.body().children().text().replaceAll("\\u3000|\\s*", "").length();
            }
//            BookPage bookpage1 = bookPageMapper.findBookPageByMetaIdAndPageId(metaId, (int) i);
//            if (bookpage1 == null) {
//                BookPage bookPage = new BookPage();
//                bookPage.setId(metaId + "-p" + i);
//                bookPage.setMetaId(metaId);
//                bookPage.setPageId(i);
//                bookPage.setWordSum((long) word);
//                bookPage.setContent(tmp);
//                bookPage.setCreateTime(new Date());
//                bookPageMapper.insert(bookPage);
//                log.info("新增metaid：{}，页码：{}的数据成功", metaId, i);
//            } else {
//                bookpage1.setContent(tmp);
//                bookpage1.setMetaId(metaId);
//                bookpage1.setWordSum((long) word);
//                bookpage1.setPageId(i);
//                bookpage1.setUpdateTime(new Date());
//                bookPageMapper.updateByPrimaryKeyWithBLOBs(bookpage1);
//                log.info("修改metaid：{}，页码：{}的数据成功", metaId, i);
//            }
            BookPage bookPage = new BookPage();
            bookPage.setId(metaId + "-p" + i);
            bookPage.setMetaId(metaId);
            bookPage.setPageId(i);
            bookPage.setWordSum((long) word);
            bookPage.setContent(tmp);
            bookPage.setCreateTime(new Date());
            bookPage.setUpdateTime(new Date());
            bookPageMapper.updataOrInsertByMidAndPid(bookPage);
            long c = System.currentTimeMillis();
            log.info("新增或修改metaid：{}，页码：{}的数据成功,请求接口耗时：{}ms,插入数据库耗时：{}ms", metaId, i,b-a,c-b);
            i1 = pageCrawledTempMapper.deleteByIdAndPage(pageCrawledTemp);
            if (i1 > 0) {
                log.info("删除pageCrawledTemp的id:{},page:{}成功", metaId, i);
            } else {
                log.info("删除pageCrawledTemp的id:{},page:{}失败", metaId, i);
            }
        } catch (Exception e) {
            log.info("记录表pageCrawledTemp：{} 的第{}页重新抽取失败内容时出现错误{}", metaId, i, e);
        } finally {
            countDownLatch.countDown();
        }
    }


}
