package com.apabi.flow.book.service.impl;

import com.apabi.flow.book.dao.*;
import com.apabi.flow.book.fetchPage.FetchPageAgainConsumer;
import com.apabi.flow.book.fetchPage.FetchPageAgainProducer;
import com.apabi.flow.book.fetchPage.FetchPageConsumer;
import com.apabi.flow.book.fetchPage.FetchPageProducer;
import com.apabi.flow.book.model.*;
import com.apabi.flow.book.service.BookPageService;
import com.apabi.flow.book.service.BookShardService;
import com.apabi.flow.book.util.BookConstant;
import com.apabi.flow.book.util.CebxUtils;
import com.apabi.flow.book.util.GetCebxChapter;
import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.config.DataConfig;
import com.apabi.flow.douban.dao.ApabiBookMetaDataTempDao;
import com.apabi.flow.douban.model.ApabiBookMetaDataTemp;
import com.apabi.flow.processing.constant.BizException;
import com.apabi.flow.systemconf.dao.SystemConfMapper;
import com.apabi.flow.systemconf.model.SystemConf;
import net.sf.json.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * 功能描述： <br>
 * <拉取分页数据、拼装章节>
 *
 * @author supeng
 * @date 2018/8/23 16:34
 * @since 1.0.0
 */
@Service
public class BookPageServiceImpl implements BookPageService {

    private static final Logger log = LoggerFactory.getLogger(BookPageServiceImpl.class);
    private static ExecutorService executorService;

    @Autowired
    private DataConfig dataConfig;

    @Autowired
    private BookLogMapper bookLogMapper;

    @Autowired
    private AssemblyResultMapper assemblyResultMapper;

    @Autowired
    private BookChapterBakDao bookChapterBakDao;

    @Autowired
    private PageAssemblyQueueMapper pageAssemblyQueueMapper;

    @Autowired
    private PageCrawledQueueMapper pageCrawledQueueMapper;

    @Autowired
    BookShardService bookShardService;

    @Autowired
    SystemConfMapper systemConfMapper;

    @Autowired
    private BookMetaDao bookMetaDao;

    @Autowired

    private ApabiBookMetaDataTempDao apabiBookMetaDataTempDao;

    @Autowired
    private BookPageMapper bookPageMapper;

    @Autowired
    private BookChapterDao bookChapterDao;

    @Autowired
    BookShardDao bookShardDao;
    @Autowired
    GetCebxChapter getCebxChapter;

    @Autowired
    private PageCrawledTempMapper pageCrawledTempMapper;

    public BookPageServiceImpl() throws Exception {
    }

    /**
     * 借道，内部类，常量
     */
    private interface DataType {
        String PAGEINSERT = "pageinsert";
        String PAGEUPDATE = "pageupdate";
        String CHAPTERINSERT = "chapterinsert";
        String CHAPTERUPDATE = "chapterupdate";
        String SHARDINSERT = "shardinsert";
        String SHARDUPDATE = "shardupdate";
    }

    /**
     * 记录 本次bookLog 的日志信息
     *
     * @param metaid
     * @param dataType
     * @param addedNum
     * @param totals
     * @param startIndex
     * @param endIndex
     */
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


    public int shutdownNow() {
        executorService.shutdownNow();
        return 1;
    }

    /**
     * 流式内容抓取
     *
     * @return
     */
    @Override
    public int autoFetchPageData() {
        try {
            log.info("开始执行流式分页抓取....");
            // 定义队列大小
            int queueSize = 100;
            ArrayBlockingQueue idQueue = new ArrayBlockingQueue(queueSize);
            List<String> idList = new ArrayList<>();
            List<PageCrawledQueue> list1 = pageCrawledQueueMapper.findAll();
            if (list1.size() == 0) {
                return 2;
            }
            list1.stream().filter(v -> {
                boolean flag = !idList.contains(v.getId());
                if (flag) {
                    idList.add(v.getId());
                }
                return flag;
            }).collect(Collectors.toList());
            FetchPageProducer fetchPageProducer = new FetchPageProducer(idQueue, "fetchPageProducer", idList);
            new Thread(fetchPageProducer).start();
            // id列表中数据计数器
            CountDownLatch idCountDownLatch = new CountDownLatch(idList.size());
            SystemConf systemConf = systemConfMapper.selectByConfKey("EpageContent");
            if (systemConf == null) {
                log.error("获取系统参数信息出错，无法查询url每页内容前缀");
                return 0;
            }
            String confvalue = systemConf.getConfValue();
            SystemConf systemConf2 = systemConfMapper.selectByConfKey("sleepTime");
            if (systemConf2 == null) {
                log.error("获取系统参数信息出错，无法查询睡眠时间");
                return 0;
            }
            Integer sleepTime = Integer.parseInt(systemConf2.getConfValue());
            SystemConf systemConf3 = systemConfMapper.selectByConfKey("width");
            if (systemConf3 == null) {
                log.error("获取系统参数信息出错，无法查询图片宽度");
                return 0;
            }
            String width = systemConf3.getConfValue();
            SystemConf systemConf4 = systemConfMapper.selectByConfKey("height");
            if (systemConf4 == null) {
                log.error("获取系统参数信息出错，无法查询图片高度");
                return 0;
            }
            String height = systemConf4.getConfValue();
            SystemConf systemConf5 = systemConfMapper.selectByConfKey("threadNum");
            if (systemConf5 == null) {
                log.error("获取系统参数信息出错，无法查询线程数");
                return 0;
            }

            Integer threadNum = Integer.parseInt(systemConf5.getConfValue());

            new Thread(() -> {
                // 创建消费者对象
                FetchPageConsumer consumer = new FetchPageConsumer(idCountDownLatch, confvalue, idQueue, bookMetaDao, bookPageMapper, pageCrawledQueueMapper, pageAssemblyQueueMapper, bookLogMapper, pageCrawledTempMapper, sleepTime, width, height);
                // 创建线程池对象
                executorService = Executors.newFixedThreadPool(threadNum);
                // 开启threadAmount个线程消费者线程，让线程池管理消费者线程
                for (int i = 0; i < idList.size(); i++) {
                    // 执行线程中的任务
                    executorService.execute(consumer);
                }
                try {
                    idCountDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 关闭线程池
                executorService.shutdown();
                //前端多次点击按钮控制
                SystemConf systemConf6 = systemConfMapper.selectByConfKey("switch");
                systemConf6.setConfValue("0");
                systemConfMapper.updateByPrimaryKey(systemConf6);

                // ******************多线程抓取page结束******************
                log.info(" 流式分页抓取执行完毕....----修改系统状态值为0");
            }).start();

        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    /**
     * 失败数据重新抓取
     *
     * @return 状态值
     */
    @Override
    public int autoFetchPageDataAgain() {
        try {
            List<PageCrawledTemp> all = pageCrawledTempMapper.findAll();
            if (all.size() == 0) {
                return 2;
            }
            //从系统表获取参数
            SystemConf systemConf = systemConfMapper.selectByConfKey("EpageContent");
            if (systemConf == null) {
                log.error("获取系统参数信息出错，无法查询url每页内容前缀");
                return 0;
            }
            String confvalue = systemConf.getConfValue();

            SystemConf systemConf2 = systemConfMapper.selectByConfKey("sleepTime");
            if (systemConf2 == null) {
                log.error("获取系统参数信息出错，无法查询睡眠时间");
                return 0;
            }
            Integer sleepTime = Integer.parseInt(systemConf2.getConfValue());

            SystemConf systemConf3 = systemConfMapper.selectByConfKey("width");
            if (systemConf3 == null) {
                log.error("获取系统参数信息出错，无法查询图片宽度");
                return 0;
            }
            String width = systemConf3.getConfValue();

            SystemConf systemConf4 = systemConfMapper.selectByConfKey("height");
            if (systemConf4 == null) {
                log.error("获取系统参数信息出错，无法查询图片高度");
                return 0;
            }
            String height = systemConf4.getConfValue();

            SystemConf systemConf5 = systemConfMapper.selectByConfKey("threadNum");
            if (systemConf5 == null) {
                log.error("获取系统参数信息出错，无法查询线程数");
                return 0;
            }
            Integer threadNum = Integer.parseInt(systemConf5.getConfValue());

            new Thread(() -> {
                //阻塞队列
                ArrayBlockingQueue<PageCrawledTemp> idQueue = new ArrayBlockingQueue(100);
                //生产者
                FetchPageAgainProducer fetchPageAgainProducer = new FetchPageAgainProducer(idQueue, "fetchPageAgainProducer", all);
                new Thread(fetchPageAgainProducer).start();
                //计数器
                CountDownLatch idCountDownLatch = new CountDownLatch(all.size());
                //线程池
                executorService = Executors.newFixedThreadPool(threadNum);
                //消费者
                FetchPageAgainConsumer fetchPageAgainConsumer = new FetchPageAgainConsumer(idCountDownLatch, confvalue, idQueue, bookPageMapper, pageCrawledTempMapper, sleepTime, width, height);
                // 开启threadAmount个线程消费者线程，让线程池管理消费者线程
                for (int i = 0; i < all.size(); i++) {
                    // 执行线程中的任务
                    executorService.execute(fetchPageAgainConsumer);
                }
                try {
                    idCountDownLatch.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 关闭线程池
                executorService.shutdown();
                //前端多次点击按钮控制 更改系统参数控制值
                SystemConf systemConf6 = systemConfMapper.selectByConfKey("switch");
                systemConf6.setConfValue("0");
                systemConfMapper.updateByPrimaryKey(systemConf6);
                log.info("流式分页重新抓取执行完毕，----修改系统状态值为0");
            }).start();
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    /**
     * 根据data-config.yml 中的 chapterMetaIdList 的数组自动将多本图书从 页面内容 组合成 对应的章节内容
     * 更改为查PageAssemblyQueue
     *
     * @return
     */
    @Override
    public int autoProcessBookFromPage2Chapter(int isCover) {
        List<String> idList = new ArrayList<>();
        List<PageAssemblyQueue> list1 = pageAssemblyQueueMapper.findAll();
        List<PageAssemblyQueue> list = list1.stream().filter(v -> {
            boolean flag = !idList.contains(v.getId());
            if (flag) {
                idList.add(v.getId());
            }
            return flag;
        }).collect(Collectors.toList());
        log.info("开始章节拼装,拼装列表有{}条数据",list.size());
        if (list.size() == 0) {
            return -1;
        }
        new Thread(() -> {
            //子线程去干自己的事情
            int totalNum = 0;
            for (PageAssemblyQueue pageAssemblyQueue : list) {
                try {
                    log.info("metaId：{}章节拼装开始",pageAssemblyQueue.getId());
                    long a = System.currentTimeMillis();
                    totalNum += processBookFromPage2Chapter(pageAssemblyQueue.getId(),isCover);
                    long b = System.currentTimeMillis();
                    log.info("metaId：{},章节拼装一共耗时{}ms", pageAssemblyQueue.getId(), b - a);
                } catch (Exception e) {
                    log.info("metaid = {} 的图书组装出错，错误信息：{}", pageAssemblyQueue.getId(), e);
                }
            }

        }).start();

        return 1;
    }

    /**
     * 批量上传excel中metaId到抓取表
     *
     * @param data
     * @return
     * @throws Exception
     */
    @Override
    public int batchAddCrawFromFile(Map<Integer, Map<Object, Object>> data) throws Exception {
        int i = 0;
        HashSet<String> set = new HashSet<>();
        try {
            for (Map.Entry<Integer, Map<Object, Object>> entry : data.entrySet()) {
                String id = (String) entry.getValue().get("ID");
                if (StringUtils.isBlank(id)) {
                    throw new BizException("请确认以ID为起始行，请检查数据重新导入！");
                } else {
                    set.add(id);
                }
            }
            for (String id : set) {
                PageCrawledQueue pageCrawledQueue = new PageCrawledQueue();
                pageCrawledQueue.setId(id);
                i += pageCrawledQueueMapper.insert(pageCrawledQueue);
            }
        } catch (Exception e) {
            if (e instanceof BizException) {
                throw new Exception(e.getMessage());
            } else {
                throw new Exception("数据解析异常，请检查文件格式是否正确或联系管理员！");
            }
        }
        return i;
    }

    @Override
    public int batchAddAssemblyFromFile(Map<Integer, Map<Object, Object>> data) throws Exception {
        int i = 0;
        HashSet<String> set = new HashSet<>();
        try {
            for (Map.Entry<Integer, Map<Object, Object>> entry : data.entrySet()) {
                String id = (String) entry.getValue().get("ID");
                if (StringUtils.isBlank(id)) {
                    throw new BizException("请确认以ID为起始行，请检查数据重新导入！");
                } else {
                    set.add(id);
                }
            }
            for (String id : set) {
                PageAssemblyQueue pageAssemblyQueue = new PageAssemblyQueue();
                pageAssemblyQueue.setId(id);
                try {
                i += pageAssemblyQueueMapper.insert(pageAssemblyQueue);
                } catch (Exception e) {

                }
            }
        } catch (Exception e) {
            if (e instanceof BizException) {
                throw new Exception(e.getMessage());
            } else {
                throw new Exception("数据解析异常，请检查文件格式是否正确或联系管理员！");
            }
        }
        return i;
    }

    /**
     * 获取 当前 metaId 下的 所有 页码列表信息
     *
     * @param metaId
     * @return 返回页码列表
     */
    @Override
    public List<BookPage> queryAllBookPagesByMetaId(String metaId) {
        List<BookPage> list = bookPageMapper.findAllByMetaIdOrderByPageId(metaId);
        if (list == null || list.size() == 0) {
            return list;
        }
        return list;
    }

    /**
     * 查询特定 metaid 下的某一页 内容信息
     *
     * @param metaid
     * @param pageid
     * @return 返回 bookPage 页面信息实体
     */
    @Override
    public BookPage getBookPageContentByMetaidAndPageid(String metaid, Integer pageid) {
        BookPage bookPage = null;
        if (StringUtils.isNotBlank(metaid) && pageid != null && pageid > 0) {
            bookPage = bookPageMapper.findBookPageByMetaIdAndPageId(metaid, pageid);
        }
        return bookPage;
    }

    //从目录递归获取endpage页码
    public static List<Integer> getPageNums(JSONArray jsonArray, List<Integer> pageNums) {
        if (jsonArray.size() > 0) {
            for (int i = 0; i < jsonArray.size(); i++) {
                String pageNum = jsonArray.getJSONObject(i).getString("ebookPageNum");
                String children = jsonArray.getJSONObject(i).getString("children");
                pageNums.add(Integer.parseInt(pageNum));
                JSONArray jsonArray1 = JSONArray.fromObject(children);
                getPageNums(jsonArray1, pageNums);
            }
        }
        return pageNums;
    }


    /**
     * 通过获取书苑xml 章节目录，将本地图书分页数据拼装成章节数据
     * 书苑按页流式内容封装成流式章节内容
     *
     * @param metaId isCover 0否 1是
     * @return
     * @throws Exception
     */
    public int processBookFromPage2Chapter(String metaId,int isCover) {
        try {
            if (StringUtils.isNotBlank(metaId)) {
                List<Integer> pageNums = new ArrayList<>();
                BookMeta meta = bookMetaDao.findBookMetaById(metaId);
                if (meta == null) {
                    log.info("图书meta表中 metaid = {} 的不存在", metaId);
                    return 0;
                }
                ApabiBookMetaDataTemp metaDataTemp = apabiBookMetaDataTempDao.findById(metaId);
                // 从本地数据源中获取当前metaid 的所有页面内容信息列表
                List<BookPage> bookPageList = bookPageMapper.findAllByMetaIdOrderByPageIdWithContent(metaId);
                int cebxPage = 0;
                if (StringUtils.isBlank(meta.getCebxPage())||meta.getCebxPage().equals("null")) {
                    log.info("图书meta表中 metaid = {} 的cebxPage不存在", metaId);
                    cebxPage = CebxUtils.getCebxPage(metaId, BookMetaServiceImpl.shuyuanOrgCode);
                    if (cebxPage == 0) {
                        log.info(" {} --获取元数据信息出错，无法得到书本页数信息，退出数据获取", metaId);
                        return 0;
                    }
                } else {
                    cebxPage = Integer.parseInt(meta.getCebxPage());
                }
                if (bookPageList == null) {
                    log.info("图书分页表中没有 metaid = {} 的数据列表", metaId);
                    saveBookLog(metaId, DataType.CHAPTERINSERT, 0, -1, 0, -1);
                    return 0;
                }
                //meta表和page表总页数求和对比不一致跳出  证明有页数缺失
                int n = (int) bookPageList.stream().mapToLong(BookPage::getPageId).sum();
                int sum = 0;
                for (int i = 1; i <= cebxPage; i++) {
                    sum += i;
                }
                if (n != sum) {
                    log.info("图书分页表中 metaid = {} 的每页数据不全", metaId);
                    return 0;
                }
                String streamCatalog = "";
                //递归获取目录结构的页码
                if (meta != null) {
                    streamCatalog = meta.getStreamCatalog();
                    if (StringUtils.isBlank(streamCatalog)||meta.getStreamCatalog().equals("null")) {
                        log.info("图书meta表中 metaid = {} 的目录结构不存在", metaId);
                        streamCatalog = CebxUtils.getStreamCatalog(metaId, BookMetaServiceImpl.shuyuanOrgCode);
                        if ("[]".equalsIgnoreCase(streamCatalog) || StringUtils.isBlank(streamCatalog)) {
                            log.info("书院接口获取 metaid = {} 的目录结构不存在", metaId);
                            return 0;
                        }
                    }
                    JSONArray jsonArray = JSONArray.fromObject(streamCatalog);
                    getPageNums(jsonArray, pageNums);
                }
                pageNums.add(bookPageList.size());
                //从书苑获取图书内容，并分章节
                org.jsoup.nodes.Document doc;
                int word;
                int start = 1;
                int chapterNum = 0;
                //按章节存放
                List<BookChapter> chapterList = new ArrayList<>();
                //对章节进行分组存放
                List<BookShard> chapterShardList = new ArrayList<>();

                //判断，如果本地数据库中没有取出数据，两种解决方案: 1. 将 pageNums 清空，则一条数据都不插入 2. 尝试从书苑接口中获取数据（不建议，缺少流程步骤）
                if (bookPageList.size() < pageNums.get(pageNums.size() - 2)) {
                    //总页数 小于 最后一章的页码，数据不合理，这里暂时不予 数据插入
                    log.info("总页数{} 小于最后一章的页码{}，数据不合理，这里暂时不予数据插入", bookPageList.size(), pageNums.get(pageNums.size() - 2));
                    saveBookLog(metaId, DataType.CHAPTERINSERT, chapterList.size(), chapterList.size(), 0, chapterList.size() - 1);
                    return 0;
                }
                //总字数  1 3 5 5
                int wordSum = 0;
                for (Integer pageNum : pageNums) {
                    if (pageNum == 1) {
                        continue;
                    }
                    BookChapter chapter = new BookChapter();
                    chapter.setComId(metaId + chapterNum);
                    chapter.setChapterNum(chapterNum);
                    StringBuilder content = new StringBuilder("");

                    for (int i = start; i < pageNum; i++) {
                        content.append(bookPageList.get(i - 1).getContent());
                    }
                    start = pageNum;
                    //获取章节字数
                    doc = Jsoup.parse(content.toString());
                    word = doc.body().children().text().replaceAll("\\u3000|\\s*", "").length();
                    wordSum += word;
                    chapter.setWordSum(word);
                    //给获取内容为空的章节添加一个p标签
                    if (content.length() == 0) {
                        content.append("<p></p>");
                    }
                    chapter.setContent(content.toString());
                    //替换章节末尾引用文献序号
                    org.jsoup.nodes.Element body = doc.body();
                    Elements elements = body.select("aside");
                    for (org.jsoup.nodes.Element element : elements) {
                        element.select("a").first().before(element.val() + "\u3000");
                    }
                    body.select("aside").remove();
                    body.select("section").append(elements.outerHtml());
                    chapter.setBodyClass(body.attr("class"));
                    //对章节内容进行分片
                    Elements children = body.children();
                    List<String> tags = new ArrayList<>();
                    String contentP = "";
                    for (int j = 0; j < children.size(); j++) {
                        contentP += children.get(j).outerHtml();
                        if ((j != 0 && j % BookConstant.shardSize == 0) || (j == children.size() - 1)) {
                            tags.add(contentP);
                            contentP = "";
                        }
                    }
                    //添加分组数
                    chapter.setShardSum(tags.size());
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    for (int k = 0; k < tags.size(); k++) {
                        BookShard chapterShard = new BookShard();
                        chapterShard.setComId(metaId + chapterNum + "S" + k);
                        chapterShard.setChapterNum(chapterNum);
                        chapterShard.setIndex(k);
                        chapterShard.setBodyClass(body.attr("class"));
//                        chapterShard.setCreateTime(sdf.parse(sdf.format(new Date())));
                        chapterShard.setUpdateTime(sdf.parse(sdf.format(new Date())));
                        org.jsoup.nodes.Document shards = Jsoup.parse(tags.get(k), "utf-8");
                        int wordS = shards.text().replaceAll("\\u3000|\\s*", "").length();
                        chapterShard.setWordSum(wordS);
                        String contentS = tags.get(k);
                                        /*String contentS = tags.get(k);
                                        contentS = contentS.replaceAll("..(?i)/images/", BookConstant.BASE_URL + styleUrl + "/imgs/");*/
                        chapterShard.setContent(contentS);
                        chapterShardList.add(chapterShard);
                    }
                    chapterNum++;
                    //添加章节信息
                    chapterList.add(chapter);
                }
                AssemblyResult assemblyResult1=new AssemblyResult();
                assemblyResult1.setId(UUIDCreater.nextId());
                assemblyResult1.setMetaid(metaId);
                if (!chapterList.isEmpty()) {
                    SimpleDateFormat localSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    int num = 0;
                    for (BookChapter chapter : chapterList) {
                        BookChapter chapter1 = null;
                        num++;
                        if (chapter.getComId() != null) {
                            chapter1 = bookChapterDao.findBookChapterByComId(chapter.getComId());
                        }
                        if (chapter1 == null) {
                            chapter.setCreateTime(localSdf.parse(localSdf.format(new Date())));
                            chapter.setUpdateTime(localSdf.parse(localSdf.format(new Date())));
                            bookChapterDao.insertBookChapter(chapter);
                            assemblyResult1.setMessage("新增");
                            assemblyResult1.setResult(1);
                            saveBookLog(metaId, DataType.CHAPTERINSERT, num, chapterList.size(), 1, chapterList.size() - 1);
                        } else {
                            //0否
                            if(isCover==0){
                                int i = pageAssemblyQueueMapper.deleteByPrimaryKey(metaId);
                                AssemblyResult assemblyResult=new AssemblyResult();
                                assemblyResult.setId(UUIDCreater.nextId());
                                assemblyResult.setCreateTime(new Date());
                                assemblyResult.setMetaid(metaId);
                                assemblyResult.setResult(0);
                                assemblyResult.setMessage("chapter表已经存在数据，未覆盖");
                                assemblyResultMapper.insert(assemblyResult);
                                log.info("metaid:{}的chapter表已经存在数据，未覆盖",metaId);
                                return 0;
                            }else if(isCover==1){
                                BookChapterVo bookChapterVo = new BookChapterVo();
                                bookChapterVo.setId(UUIDCreater.nextId());
                                bookChapterVo.setBodyClass(chapter1.getBodyClass());
                                bookChapterVo.setChapterNum(chapter1.getChapterNum());
                                bookChapterVo.setComId(chapter1.getComId());
                                bookChapterVo.setContent(chapter1.getContent());
                                bookChapterVo.setCreateTime(chapter1.getCreateTime());
                                bookChapterVo.setShardSum(chapter1.getShardSum());
                                bookChapterVo.setUpdateTime(chapter1.getUpdateTime());
                                bookChapterVo.setWordSum(chapter1.getWordSum());
                                bookChapterBakDao.insertBookChapterVo(bookChapterVo);
                                chapter.setUpdateTime(localSdf.parse(localSdf.format(new Date())));
                                bookChapterDao.updateBookChapter(chapter);
                                saveBookLog(metaId, DataType.CHAPTERUPDATE, num, chapterList.size(), 1, chapterList.size() - 1);
                                assemblyResult1.setMessage("重复覆盖");
                                assemblyResult1.setResult(1);
                            }
                        }
                    }
                    if (chapterShardList != null && chapterShardList.size() > 0) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        int a = 0;
                        for (BookShard bookShard : chapterShardList) {
                            try {
                                BookShard bookShard1 = bookShardDao.findBookShardByComId(bookShard.getComId());
                                a++;
                                if (bookShard1 != null) {
                                    bookShard.setCreateTime(bookShard1.getCreateTime());
                                    bookShardDao.updateBookShard(bookShard);
                                    saveBookLog(metaId, DataType.SHARDUPDATE, a, chapterShardList.size(), 1, chapterShardList.size() - 1);
                                } else {
                                    bookShard.setCreateTime(sdf.parse(sdf.format(new Date())));
                                    bookShardDao.insertBookShard(bookShard);
                                    saveBookLog(metaId, DataType.SHARDINSERT, a, chapterShardList.size(), 1, chapterShardList.size() - 1);
                                }
                            } catch (Exception e) {
                                log.warn(bookShard.getComId() + e);
                            }
                        }
                    }
                    int i = pageAssemblyQueueMapper.deleteByPrimaryKey(metaId);
                    if (i > 0) {
                        log.info("删除pageAssemblyQueue的id：{}成功", metaId);
                    } else {
                        log.info("删除pageAssemblyQueue的id：{}失败", metaId);
                    }
                    meta.setHasFlow(1);
                    meta.setContentNum(wordSum);
                    meta.setChapterNum(chapterNum);
                    meta.setStreamCatalog(streamCatalog);
                    meta.setUpdateTime(new Date());
                    meta.setCebxPage(Integer.toString(cebxPage));
                    meta.setFlowSource("cebx");

                    metaDataTemp.setHasFlow(1);
                    metaDataTemp.setContentNum(wordSum);
                    metaDataTemp.setChapterNum(chapterNum);
                    metaDataTemp.setStreamCatalog(streamCatalog);
                    metaDataTemp.setUpdateTime(new Date());
                    metaDataTemp.setCebxPage(Integer.toString(cebxPage));
                    metaDataTemp.setFlowSource("cebx");
                    //修改metaData和metaDataTemp的状态值
                    int i1 = bookMetaDao.updateBookMetaById(meta);
                    apabiBookMetaDataTempDao.update(metaDataTemp);
                    if (i1 > 0) {
                        log.info("修改Bookmeta的id：{}成功", metaId);
                    } else {
                        log.info("修改Bookmeta的id：{}失败", metaId);
                    }
                    assemblyResult1.setCreateTime(new Date());
                    int insert = assemblyResultMapper.insert(assemblyResult1);
                    if (insert > 0) {
                        log.info("记录assembly_result表的metaid：{}成功", metaId);
                    } else {
                        log.info("记录assembly_result表的metaid：{}失败", metaId);
                    }
                    return num;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            log.info("章节拼装的id：{}出现异常：{}", metaId, e);
            return 0;
        }
        return 0;
    }


}
