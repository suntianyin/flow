package com.apabi.flow.book.service.impl;

import com.apabi.flow.book.dao.*;
import com.apabi.flow.book.model.*;
import com.apabi.flow.book.service.BookPageService;
import com.apabi.flow.book.service.BookShardService;
import com.apabi.flow.book.util.*;
import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.config.DataConfig;
import com.apabi.flow.systemconf.dao.SystemConfMapper;
import com.apabi.flow.systemconf.model.SystemConf;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.jsoup.Jsoup;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

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

    @Autowired
    private DataConfig dataConfig;

    @Autowired
    private BookLogMapper bookLogMapper;

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
    private BookPageMapper bookPageMapper;

    @Autowired
    private BookChapterDao bookChapterDao;

    @Autowired
    BookShardDao bookShardDao;

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

    /**
     * 根据data-config.yml 中的pageMetaIdList 的数组自动抓取多本书的流式分页内容
     * 更改为查PageAssemblyQueue
     *
     * @return
     */
    @Override
    public int autoFetchPageData() {
        int totalNum = 0;
        List<PageCrawledQueue> list = pageCrawledQueueMapper.findAll();
        for (PageCrawledQueue pageCrawledQueue : list) {
            totalNum += insertShuyuanPageData(pageCrawledQueue.getId());
        }
        log.info("拉取总页数为 - {}", totalNum);
        return totalNum;
    }

    /**
     * 根据data-config.yml 中的 chapterMetaIdList 的数组自动将多本图书从 页面内容 组合成 对应的章节内容
     * 更改为查PageAssemblyQueue
     *
     * @return
     */
    @Override
    public int autoProcessBookFromPage2Chapter() {
        int totalNum = 0;
        List<PageAssemblyQueue> list = pageAssemblyQueueMapper.findAll();
        for (PageAssemblyQueue pageAssemblyQueue : list) {
            try {
                totalNum += processBookFromPage2Chapter(pageAssemblyQueue.getId());
            } catch (Exception e) {
                log.error("metaid = {} 的图书组装出错，错误信息：{}", pageAssemblyQueue.getId(), e);
            }
        }
        log.info("成功组装图书数 - {}", totalNum);
        return totalNum;
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
     * 通过xml，获取书苑图书页码信息
     * 书苑流式内容采集
     *
     * @param metaId
     * @return 返回插入页数
     * @throws Exception
     */
    @Override
    public int insertShuyuanPageData(String metaId) {
        if (StringUtils.isNotBlank(metaId)) {
            //查询url每页内容前缀
            SystemConf systemConf = systemConfMapper.selectByConfKey("EpageContent");
            if (systemConf == null) {
                log.error("获取元数据信息出错，无法查询url每页内容前缀，退出数据获取");
                return 0;
            }
            String confvalue = systemConf.getConfKey();
            //总页数
            int cebxPage = 0;
            BookMeta meta = bookMetaDao.findBookMetaById(metaId);
            if (meta != null && meta.getBookPages() != null) {
                cebxPage = Integer.parseInt(meta.getBookPages());
            }
            int start = 1;
//            //当 maxPageid 处于 0 - cebxPage 之间时，将 maxPageid + 1 作为循环拉取的初始值，否则返回不符合循环条件的值 cebxPage + 1
//            if (maxPageid >= 0 && maxPageid <= cebxPage) {
//                start = maxPageid + 1;
//            } else {
//                start = cebxPage + 1;
//            }
            //记录当前远程拉取次数
            int num = 0;
            if (cebxPage == 0) {
                log.error("获取元数据信息出错，无法得到书本页数信息，退出数据获取");
                return num;
            }
            //页数 从 第一页开始，直到 总页数
            for (long i = start; i <= cebxPage; i++) {
                long a = System.currentTimeMillis();
                HttpEntity httpEntity = null;
                String url = null;
                try {
                    //计数需要放在最上面才能保证数据的正确性，在后或中间都有可能会执行不到
                    num++;
                    Thread.sleep(1000);
                    url = EbookUtil.makePageUrl(confvalue, BookMetaServiceImpl.shuyuanOrgCode, metaId, BookMetaServiceImpl.urlType, BookMetaServiceImpl.serviceType, "1920", "1080", i);
                    httpEntity = HttpUtils.doGetEntity(url);
                    String tmp = EntityUtils.toString(httpEntity);
                    org.jsoup.nodes.Document doc;
                    int word;
                    doc = Jsoup.parse(tmp.toString());
                    word = doc.body().children().text().replaceAll("\\u3000|\\s*", "").length();
                    BookPage bookpage1 = bookPageMapper.findBookPageByMetaIdAndPageId(metaId, (int) i);
                    if (bookpage1 == null) {
                        BookPage bookPage = new BookPage();
                        bookPage.setId(ApabiIDUtils.nextId());
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
                    log.info("获取图书：{} 的第{}页时超时或出现错误，错误信息：{}，将重新发起请求", metaId, i, e);
                    i--;
                    num--;
                    continue;
                }
                long b = System.currentTimeMillis();
                log.debug("gethtml请求耗时 {}， url = {}", b - a, url);
            }

            try {
                int i = 0;
                int i1 = 0;
                i = pageCrawledQueueMapper.deleteByPrimaryKey(metaId);
                if (i > 0) {
                    log.info("删除pageCrawledQueue的id:{}成功", metaId);
                } else {
                    log.info("删除pageCrawledQueue的id:{}失败", metaId);
                }
                PageAssemblyQueue pageAssemblyQueue = new PageAssemblyQueue();
                pageAssemblyQueue.setId(metaId);
                i1 = pageAssemblyQueueMapper.insert(pageAssemblyQueue);
                if (i1 > 0) {
                    log.info("添加pageAssemblyQueue的id:{}成功", metaId);
                } else {
                    log.info("添加pageAssemblyQueue的id:{}失败", metaId);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return num;
        }
        return 0;
    }

    /**
     * 通过获取书苑xml 章节目录，将本地图书分页数据拼装成章节数据
     * 书苑按页流式内容封装成流式章节内容
     *
     * @param metaId
     * @return
     * @throws Exception
     */
    @Override
    public int processBookFromPage2Chapter(String metaId) {
        try {
            if (StringUtils.isNotBlank(metaId)) {
                List<Integer> pageNums = new ArrayList<>();
                BookMeta meta = bookMetaDao.findBookMetaById(metaId);
                if (meta != null) {
                    String foamatCatalog = meta.getFoamatCatalog();
                    JSONArray jsonArray = JSONArray.fromObject(foamatCatalog);
                    getPageNums(jsonArray, pageNums);
                }
                // 从本地数据源中获取当前metaid 的所有页面内容信息列表
                //todo 是否需要验证数据的一致性 或者 合理性？
                List<BookPage> bookPageList = bookPageMapper.findAllByMetaIdOrderByPageIdWithContent(metaId);
                if (bookPageList == null) {
                    log.info("图书分页表中没有 metaid = {} 的数据列表", metaId);
                    saveBookLog(metaId, DataType.CHAPTERINSERT, 0, -1, 0, -1);
                    return 0;
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
                    log.info("总页数{} 小于 最后一章的页码{}，数据不合理，这里暂时不予 数据插入", bookPageList.size(), pageNums.get(pageNums.size() - 2));
                    saveBookLog(metaId, DataType.CHAPTERINSERT, chapterList.size(), chapterList.size(), 0, chapterList.size() - 1);
                    return 0;
                }
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
                    chapter.setWordSum(word);
                    //给获取内容为空的章节添加一个p标签
                    if (content.length() == 0) {
                        content.append("<p></p>");
                    }
                    //todo 这里需要添加 换行符？ 什么换行符<br> ?
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
                            saveBookLog(metaId, DataType.CHAPTERINSERT, num, chapterList.size(), 1, chapterList.size() - 1);
                        } else {
                            chapter.setUpdateTime(localSdf.parse(localSdf.format(new Date())));
                            bookChapterDao.updateBookChapter(chapter);
                            saveBookLog(metaId, DataType.CHAPTERUPDATE, num, chapterList.size(), 1, chapterList.size() - 1);
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
                    return num;
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
            return 0;
        }
        return 0;
    }

    public static void main(String[] args) throws Exception {
//            List<Integer> pageNums=new ArrayList<>();
//            String foamatCatalog = "[{\"chapterName\":\"封面页\",\"chapterNum\":0,\"children\":[],\"ebookPageNum\":1,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"书名页\",\"chapterNum\":1,\"children\":[],\"ebookPageNum\":2,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"版权页\",\"chapterNum\":2,\"children\":[],\"ebookPageNum\":3,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"前言页\",\"chapterNum\":3,\"children\":[],\"ebookPageNum\":4,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"目录页\",\"chapterNum\":4,\"children\":[],\"ebookPageNum\":8,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"第1章 成就卓越的科学泰斗\",\"chapterNum\":5,\"children\":[{\"chapterName\":\"相对论鼻祖阿尔伯特•爱因斯坦\",\"chapterNum\":6,\"children\":[],\"ebookPageNum\":13,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"从汤玛斯•爱迪生想到记忆力名人\",\"chapterNum\":7,\"children\":[],\"ebookPageNum\":17,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"科学女杰居里夫人\",\"chapterNum\":8,\"children\":[],\"ebookPageNum\":22,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"无线电发明者伽利尔摩•马可尼\",\"chapterNum\":9,\"children\":[],\"ebookPageNum\":26,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"飞机发明者莱特兄弟\",\"chapterNum\":10,\"children\":[],\"ebookPageNum\":29,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"名医格林菲尔\",\"chapterNum\":11,\"children\":[],\"ebookPageNum\":32,\"url\":\"\",\"wordSum\":0}],\"ebookPageNum\":12,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"第2章 巨擘操觚的文学巨匠\",\"chapterNum\":12,\"children\":[{\"chapterName\":\"戏剧大师威廉•莎士比亚\",\"chapterNum\":13,\"children\":[],\"ebookPageNum\":36,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"伟大作家列夫•尼古拉耶维奇•托尔斯泰\",\"chapterNum\":14,\"children\":[],\"ebookPageNum\":39,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"法国文豪大仲马\",\"chapterNum\":15,\"children\":[],\"ebookPageNum\":42,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"大文学家赫伯托•乔治•韦尔斯\",\"chapterNum\":16,\"children\":[],\"ebookPageNum\":46,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"天才诗人埃德加•爱伦•坡\",\"chapterNum\":17,\"children\":[],\"ebookPageNum\":50,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"短篇小说家欧•亨利\",\"chapterNum\":18,\"children\":[],\"ebookPageNum\":54,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"大戏剧家萧伯纳\",\"chapterNum\":19,\"children\":[],\"ebookPageNum\":57,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"英国文豪威廉•萨默赛特•毛姆\",\"chapterNum\":20,\"children\":[],\"ebookPageNum\":61,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"文坛怪杰西奥多•德莱塞\",\"chapterNum\":21,\"children\":[],\"ebookPageNum\":64,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"著名小说家南根里\",\"chapterNum\":22,\"children\":[],\"ebookPageNum\":67,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"《爱丽丝漫游仙境记》作者查理•都格森\",\"chapterNum\":23,\"children\":[],\"ebookPageNum\":70,\"url\":\"\",\"wordSum\":0}],\"ebookPageNum\":35,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"第3章 传扬不朽的艺坛怪杰\",\"chapterNum\":24,\"children\":[{\"chapterName\":\"卡通大师华特•迪士尼\",\"chapterNum\":25,\"children\":[],\"ebookPageNum\":73,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"“说谎大王”罗伯•利波里\",\"chapterNum\":26,\"children\":[],\"ebookPageNum\":76,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"幽默影星罗伊•罗吉尔\",\"chapterNum\":27,\"children\":[],\"ebookPageNum\":80,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"音乐家沃尔夫冈•阿玛迪乌斯•莫扎特\",\"chapterNum\":28,\"children\":[],\"ebookPageNum\":83,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"权威作曲家乔治•杰斯文\",\"chapterNum\":29,\"children\":[],\"ebookPageNum\":85,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"歌唱家恩瑞科•卡鲁索\",\"chapterNum\":30,\"children\":[],\"ebookPageNum\":88,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"“魔术之王”霍华德•瑟斯顿\",\"chapterNum\":31,\"children\":[],\"ebookPageNum\":92,\"url\":\"\",\"wordSum\":0}],\"ebookPageNum\":72,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"第4章 堪称楷模的名人大师\",\"chapterNum\":32,\"children\":[{\"chapterName\":\"印度救星莫罕达斯•卡拉姆昌德•甘地\",\"chapterNum\":33,\"children\":[],\"ebookPageNum\":96,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"英国首相温斯顿•丘吉尔\",\"chapterNum\":34,\"children\":[],\"ebookPageNum\":99,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"美国第26任总统西奥多•罗斯福\",\"chapterNum\":35,\"children\":[],\"ebookPageNum\":104,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"美国第28任总统威尔逊\",\"chapterNum\":36,\"children\":[],\"ebookPageNum\":108,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"为爱至死不渝的奥匈皇太子鲁道尔夫\",\"chapterNum\":37,\"children\":[],\"ebookPageNum\":111,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"讲话高手罗威尔•仲马斯\",\"chapterNum\":38,\"children\":[],\"ebookPageNum\":115,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"著名牧师派克斯•卡德门\",\"chapterNum\":39,\"children\":[],\"ebookPageNum\":118,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"评论权威辛泰尔\",\"chapterNum\":40,\"children\":[],\"ebookPageNum\":121,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"大律师克劳伦斯•丹诺\",\"chapterNum\":41,\"children\":[],\"ebookPageNum\":124,\"url\":\"\",\"wordSum\":0}],\"ebookPageNum\":95,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"第5章 举世闻名的实业大家\",\"chapterNum\":42,\"children\":[{\"chapterName\":\"钢铁大王安德鲁•卡内基\",\"chapterNum\":43,\"children\":[],\"ebookPageNum\":129,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"大银行家皮尔庞特•摩根\",\"chapterNum\":44,\"children\":[],\"ebookPageNum\":133,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"石油大王约翰•戴维森•洛克菲勒\",\"chapterNum\":45,\"children\":[],\"ebookPageNum\":137,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"大出版家爱德华•博克\",\"chapterNum\":46,\"children\":[],\"ebookPageNum\":140,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"报界王者约瑟夫•普利策\",\"chapterNum\":47,\"children\":[],\"ebookPageNum\":144,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"报业泰斗威廉•伦道夫•赫斯特\",\"chapterNum\":48,\"children\":[],\"ebookPageNum\":162,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"军火巨商巴兹尔•沙哈罗夫\",\"chapterNum\":49,\"children\":[],\"ebookPageNum\":165,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"钻石大王吉姆•布雷迪\",\"chapterNum\":50,\"children\":[],\"ebookPageNum\":168,\"url\":\"\",\"wordSum\":0}],\"ebookPageNum\":128,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"第6章 坚忍顽强的探险勇士\",\"chapterNum\":51,\"children\":[{\"chapterName\":\"“探险之王”克里斯托弗•哥伦布\",\"chapterNum\":52,\"children\":[],\"ebookPageNum\":172,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"“北极熊”史蒂文森\",\"chapterNum\":53,\"children\":[],\"ebookPageNum\":176,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"赛车手马尔科姆•坎贝尔\",\"chapterNum\":54,\"children\":[],\"ebookPageNum\":179,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"南极探险家罗伯特•福尔肯•斯科特\",\"chapterNum\":55,\"children\":[],\"ebookPageNum\":183,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"海军大将李屈林•拜德\",\"chapterNum\":56,\"children\":[],\"ebookPageNum\":187,\"url\":\"\",\"wordSum\":0},{\"chapterName\":\"非洲探险家马丁•约翰逊\",\"chapterNum\":57,\"children\":[],\"ebookPageNum\":190,\"url\":\"\",\"wordSum\":0}],\"ebookPageNum\":171,\"url\":\"\",\"wordSum\":0}]";
//            JSONArray jsonArray=JSONArray.fromObject(foamatCatalog);
//            pageNums = getPageNums(jsonArray, pageNums);
//            System.out.println(pageNums.toString());
        String url = "http://cebxol.apabi.com/command/htmlpage.ashx?ServiceType=htmlpage&objID=m.20121205-YPT-889-0306.ft.cebx.1&metaId=m.20121205-YPT-889-0306&OrgId=iyzhi&width=1920&height=1080&pageid=18&username=iyzhi&rights=1-0_00&time=2018-11-08+09%3A07%3A31&sign=A9F4C4E21607A90B7CAD587DF5894EE9";
        HttpEntity httpEntity = HttpUtils.doGetEntity(url);
        String tmp = EntityUtils.toString(httpEntity);
    }
}
