package com.apabi.flow.book.fetchPage;

import com.apabi.flow.book.dao.*;
import com.apabi.flow.book.model.*;
import com.apabi.flow.book.service.impl.BookMetaServiceImpl;
import com.apabi.flow.book.service.impl.BookPageServiceImpl;
import com.apabi.flow.book.util.ApabiIDUtils;
import com.apabi.flow.book.util.EbookUtil;
import com.apabi.flow.book.util.HttpUtils;
import com.apabi.flow.common.UUIDCreater;
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

public class FetchPageConsumer implements Runnable {
    private static Logger log = LoggerFactory.getLogger(FetchPageConsumer.class);
    private String confvalue;
    private ArrayBlockingQueue<String> idQueue;
    private BookMetaDao bookMetaDao;
    private BookPageMapper bookPageMapper;
    private PageCrawledQueueMapper pageCrawledQueueMapper;
    private PageAssemblyQueueMapper pageAssemblyQueueMapper;
    private BookLogMapper bookLogMapper;
    private CountDownLatch countDownLatch;
    private Integer sleepTime;
    private PageCrawledTempMapper pageCrawledTempMapper;
    private String width;
    private String height;



    public FetchPageConsumer(CountDownLatch countDownLatch, String confvalue, ArrayBlockingQueue<String> idQueue, BookMetaDao bookMetaDao, BookPageMapper bookPageMapper, PageCrawledQueueMapper pageCrawledQueueMapper, PageAssemblyQueueMapper pageAssemblyQueueMapper, BookLogMapper bookLogMapper,PageCrawledTempMapper pageCrawledTempMapper,Integer sleepTime,String width,
    String height) {
        this.confvalue = confvalue;
        this.idQueue = idQueue;
        this.bookMetaDao = bookMetaDao;
        this.bookPageMapper = bookPageMapper;
        this.pageCrawledQueueMapper = pageCrawledQueueMapper;
        this.pageAssemblyQueueMapper = pageAssemblyQueueMapper;
        this.bookLogMapper = bookLogMapper;
        this.countDownLatch=countDownLatch;
        this.sleepTime=sleepTime;
        this.pageCrawledTempMapper=pageCrawledTempMapper;
        this.height=height;
        this.width=width;
    }

    @Override
    public void run() {
        String metaId = "";
        //总页数
        int cebxPage = 0;
        //记录当前远程拉取次数
        int num = 0;
        int start = 1;
        int i1 = 0;
        int i2 = 0;
        try {
            metaId=idQueue.take();
            BookMeta meta = bookMetaDao.findBookMetaById(metaId);
            if (meta != null && meta.getCebxPage() != null) {
                cebxPage = Integer.parseInt(meta.getCebxPage());
            }
            if (cebxPage == 0) {
                log.info(" {} --获取元数据信息出错，无法得到书本页数信息，退出数据获取",metaId);
                return;
            }
            //页数 从 第一页开始，直到 总页数
            for (long i = start; i <= cebxPage; i++) {
                long a = System.currentTimeMillis();
                HttpEntity httpEntity = null;
                String url = null;
                try {
                    //计数需要放在最上面才能保证数据的正确性，在后或中间都有可能会执行不到
                    num++;
                    Thread.sleep(sleepTime);
                    url = EbookUtil.makePageUrl(confvalue, BookMetaServiceImpl.shuyuanOrgCode, metaId, BookMetaServiceImpl.urlType, BookMetaServiceImpl.serviceType, width, height, i);
                    httpEntity = HttpUtils.doGetEntity(url);
                    String tmp = EntityUtils.toString(httpEntity);
                    int word=0;
                    if(StringUtils.isBlank(tmp)){
                        log.info("获取图书：{} 的第{}页时出现错误，错误信息：内容为空将跳过", metaId, i);
                        PageCrawledTemp pageCrawledTemp=new PageCrawledTemp();
                        pageCrawledTemp.setId(metaId);
                        pageCrawledTemp.setDesce("当前页无信息");
                        pageCrawledTemp.setPage(i);
                        pageCrawledTempMapper.insert(pageCrawledTemp);
                        log.info("记录表pageCrawledTemp：{} 的第{}页时超时或出现错误", metaId, i);
                        tmp="<span></span>";
                    }else{
                        org.jsoup.nodes.Document doc;
                        doc = Jsoup.parse(tmp.toString());
                        word = doc.body().children().text().replaceAll("\\u3000|\\s*", "").length();
                    }
                    BookPage bookpage1 = bookPageMapper.findBookPageByMetaIdAndPageId(metaId, (int) i);
                    if (bookpage1 == null) {
                        BookPage bookPage = new BookPage();
                        bookPage.setId(metaId+"-p"+i);
                        bookPage.setMetaId(metaId);
                        bookPage.setPageId(i);
                        bookPage.setWordSum((long) word);
                        bookPage.setContent(tmp);
                        bookPage.setCreateTime(new Date());
                        bookPageMapper.insert(bookPage);
                        saveBookLog(metaId, DataType.PAGEINSERT, num, cebxPage, start, start + num - 1);
                    } else {
                        bookpage1.setContent(tmp);
                        bookpage1.setMetaId(metaId);
                        bookpage1.setWordSum((long) word);
                        bookpage1.setPageId(i);
                        bookpage1.setUpdateTime(new Date());
                        bookPageMapper.updateByPrimaryKeyWithBLOBs(bookpage1);
                        saveBookLog(metaId, DataType.PAGEUPDATE, num, cebxPage, start, start + num - 1);
                    }
                } catch (Exception e) {
                    log.info("获取图书：{} 的第{}页时超时或出现错误，错误信息：{}，将放弃发起请求", metaId, i, e);
                    try {
                        PageCrawledTemp pageCrawledTemp=new PageCrawledTemp();
                        pageCrawledTemp.setId(metaId);
                        pageCrawledTemp.setDesce("请求超时");
                        pageCrawledTemp.setPage(i);
                        pageCrawledTempMapper.insert(pageCrawledTemp);
                        log.info("记录表pageCrawledTemp：{} 的第{}页时超时或出现错误", metaId, i);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        num--;
                        continue;
                    }
                    num--;
                    continue;
                }
                long b = System.currentTimeMillis();
                log.info("gethtml请求耗时 {}， url = {}", b - a, url);
            }
            i1 = pageCrawledQueueMapper.deleteByPrimaryKey(metaId);
            if (i1 > 0) {
                log.info("删除pageCrawledQueue的id:{}成功", metaId);
            } else {
                log.info("删除pageCrawledQueue的id:{}失败", metaId);
            }
            PageAssemblyQueue pageAssemblyQueue = new PageAssemblyQueue();
            pageAssemblyQueue.setId(metaId);
            i2 = pageAssemblyQueueMapper.insert(pageAssemblyQueue);
            if (i2 > 0) {
                log.info("添加pageAssemblyQueue的id:{}成功", metaId);
            } else {
                log.info("添加pageAssemblyQueue的id:{}失败", metaId);
            }
            log.info(Thread.currentThread().getName() +  "在抓取" + metaId + "并添加至数据库成功，列表中剩余：" + (countDownLatch.getCount()-1) + "个数据...");
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            countDownLatch.countDown();
        }
    }

    private interface DataType {
        String PAGEINSERT = "pageinsert";
        String PAGEUPDATE = "pageupdate";
        String CHAPTERINSERT = "chapterinsert";
        String CHAPTERUPDATE = "chapterupdate";
        String SHARDINSERT = "shardinsert";
        String SHARDUPDATE = "shardupdate";
    }

    private void saveBookLog(String metaid, String dataType, Integer addedNum, Integer totals, Integer startIndex, Integer endIndex) {
        if (DataType.PAGEINSERT.equals(dataType)) {
            BookLog bookLog = new BookLog(UUIDCreater.nextId(), metaid, DataType.PAGEINSERT, addedNum, totals, startIndex, startIndex + addedNum - 1, new Date(), null);
            bookLogMapper.insert(bookLog);
            log.info("总页数：{}，从第 {} 页开始添加，本次共添加了 {} 页", totals, startIndex, addedNum);
        } else if (DataType.CHAPTERINSERT.equals(dataType)) {
            BookLog bookLog = new BookLog(UUIDCreater.nextId(), metaid, DataType.CHAPTERINSERT, addedNum, totals, startIndex, startIndex + addedNum - 1, new Date(), null);
            bookLogMapper.insert(bookLog);
            log.info("总章节数：{}，从第 {} 章开始添加，本次添加了 {} 章", totals, startIndex, addedNum);
        } else if (DataType.CHAPTERUPDATE.equals(dataType)) {
            BookLog bookLog = new BookLog(UUIDCreater.nextId(), metaid, DataType.CHAPTERUPDATE, addedNum, totals, startIndex, startIndex + addedNum - 1, new Date(), null);
            bookLogMapper.insert(bookLog);
            log.info("总章节数：{}，从第 {} 章开始修改，本次修改了 {} 章", totals, startIndex, addedNum);
        } else if (DataType.PAGEUPDATE.equals(dataType)) {
            BookLog bookLog = new BookLog(UUIDCreater.nextId(), metaid, DataType.PAGEUPDATE, addedNum, totals, startIndex, startIndex + addedNum - 1, new Date(), null);
            bookLogMapper.insert(bookLog);
            log.info("总页数：{}，从第 {} 页开始修改，本次共修改了 {} 页", totals, startIndex, addedNum);
        } else if (DataType.SHARDUPDATE.equals(dataType)) {
            BookLog bookLog = new BookLog(UUIDCreater.nextId(), metaid, DataType.SHARDUPDATE, addedNum, totals, startIndex, startIndex + addedNum - 1, new Date(), null);
            bookLogMapper.insert(bookLog);
            log.info("总分组数：{}，从第 {} 分组开始修改，本次修改了 {} 分组", totals, startIndex, addedNum);
        } else if (DataType.SHARDINSERT.equals(dataType)) {
            BookLog bookLog = new BookLog(UUIDCreater.nextId(), metaid, DataType.SHARDINSERT, addedNum, totals, startIndex, startIndex + addedNum - 1, new Date(), null);
            bookLogMapper.insert(bookLog);
            log.info("总分组数：{}，从第 {} 分组开始添加，本次共添加了 {} 分组", totals, startIndex, addedNum);
        }
    }

}
