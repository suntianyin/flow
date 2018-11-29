package com.apabi.flow.book.service.impl;

import com.apabi.flow.book.dao.BookChapterDao;
import com.apabi.flow.book.dao.BookMetaDao;
import com.apabi.flow.book.dao.BookShardDao;
import com.apabi.flow.book.model.*;
import com.apabi.flow.book.service.BookChapterService;
import com.apabi.flow.book.task.DetectBookCode;
import com.apabi.flow.book.task.DetectBookSource;
import com.apabi.flow.book.util.BookConstant;
import com.apabi.flow.book.util.BookUtil;
import com.apabi.flow.book.util.EMailUtil;
import com.apabi.flow.config.ApplicationConfig;
import com.apabi.flow.config.DicWordData;
import com.apabi.flow.systemconf.dao.SystemConfMapper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author guanpp
 * @date 2018/7/31 14:13
 * @description
 */
@Service("bookChapterService")
public class BookChapterServiceImpl implements BookChapterService {

    private static final Logger log = LoggerFactory.getLogger(BookChapterServiceImpl.class);

    @Autowired
    BookMetaDao bookMetaDao;

    @Autowired
    BookChapterDao bookChapterDao;

    @Autowired
    BookShardDao bookShardDao;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    DicWordData dicWordData;

    @Autowired
    ApplicationConfig config;

    @Autowired
    SystemConfMapper systemConfMapper;

    private static String chapterNameDetect;

    //根据图书id和章节号，获取章节内容
    @Override
    public BookChapter selectChapterById(String metaid, Integer chapterNum) {
        if (!StringUtils.isEmpty(metaid)) {
            String comId = metaid + chapterNum;
            return bookChapterDao.findBookChapterByComId(comId);
        }
        return null;
    }

    //添加图书章节内容
    @Override
    public int insertBookChapter(List<BookChapter> bookChapterList) {
        if (bookChapterList != null && bookChapterList.size() > 0) {
            for (BookChapter bookChapter : bookChapterList) {
                try {
                    bookChapterDao.insertBookChapter(bookChapter);
                } catch (Exception e) {
                    log.warn(bookChapter.getComId() + e);
                }
            }
            return 1;
        }
        return 0;
    }

    //删除metaid下的所有内容
    @Override
    public int deleteAllChapterByMetaid(String metaid) {
        if (!StringUtils.isEmpty(metaid)) {
            List<BookChapter> bookChapterList = bookChapterDao.findAllBookChapter(metaid);
            //BookMetaVo bookMetaVo = bookMetaVoRepository.findBookMetaVoByMetaidIs(metaid);
            //比较章节总数和查询出来的章节条目
//            if (bookChapterList.size() == bookMetaVo.getChapterSum()) {
//                bookChapterRepository.deleteAll(bookChapterList);
//            }
            return 1;
        }
        return 0;
    }

    //获取图书章节及分组信息
    @Override
    public List<BookChapterSum> selectBookChapterSum(String metaid) {
        if (!StringUtils.isEmpty(metaid)) {
            //List<BookChapterSum> chapterSumList = bookChapterSumRepository.findAllByComIdStartingWith(metaid);
            List<BookChapterSum> chapterSumList = bookChapterDao.findAllBookChapterSum(metaid);
            List<BookChapterSum> chapterSums = chapterSumList.stream()
                    .sorted(Comparator.comparingInt(BookChapterSum::getChapterNum))
                    .collect(Collectors.toList());
            return chapterSums;
        }
        return null;
    }

    //更新图书章节内容
    @Override
    public int updateBookChapter(BookChapter bookChapter) {
        if (!StringUtils.isEmpty(bookChapter.getComId())) {
            //更新图书字数
            String content = bookChapter.getContent();
            String comid = bookChapter.getComId();
            BookChapter oChapter = bookChapterDao.findBookChapterByComId(bookChapter.getComId());
            int oword = oChapter.getWordSum();
            int chapterNum = bookChapter.getChapterNum();
            Element body = Jsoup.parse(content).body();
            int cword = body.children().text().replaceAll("\\u3000|\\s*", "").length();
            //更新图书总字数
            String metaid = comid.substring(0, comid.length()
                    - String.valueOf(chapterNum).length());
            //BookMetaVo bookMetaVo = bookMetaVoRepository.findByMetaidIs(metaid);
            BookMetaVo bookMetaVo = bookMetaDao.findBookMetaVoById(metaid);
            int wordSum = bookMetaVo.getWordSum();
            wordSum = wordSum + (cword - oword);
            bookMetaVo.setWordSum(wordSum);
            //更新入库
            bookChapter.setContent(content);
            bookChapter.setWordSum(cword);
            bookChapter.setUpdateTime(new Date());
            bookChapterDao.updateBookChapter(bookChapter);
            //bookMetaVoRepository.save(bookMetaVo);
            bookMetaDao.updateBookMetaVo(bookMetaVo);
            //更新分组内容
            updateBookShard(comid, content);
            return 1;
        }
        return 0;
    }

    //更新章节分组内容
    private void updateBookShard(String comid, String content) {
        if (!StringUtils.isEmpty(comid) && !StringUtils.isEmpty(content)) {
            Element body = Jsoup.parse(content).body();
            //对章节内容进行分片
            Elements children = body.children();
            List<String> tags = new ArrayList<>();
            String contentP = "";
            for (int j = 0; j < children.size(); j++) {
                contentP += children.get(j).outerHtml();
                boolean flag = (j != 0 && j % BookConstant.shardSize == 0) || (j == children.size() - 1);
                if (flag) {
                    tags.add(contentP);
                    contentP = "";
                }
            }
            //获取分片内容，更新分片内容
            String shardComid;
            String shardContent;
            int cword;
            for (int k = 0; k < tags.size(); k++) {
                shardComid = comid + k;
                shardContent = tags.get(k);
                cword = body.children().text().replaceAll("\\u3000|\\s*", "").length();
                BookShard bookShard = new BookShard();
                bookShard.setComId(shardComid);
                bookShard.setContent(shardContent);
                bookShard.setWordSum(cword);
                bookShard.setUpdateTime(new Date());
                bookShardDao.updateBookShard(bookShard);
            }
        }
    }

    //检查图书章节中乱码
    @Override
    @Async
    public void detectBookCode() {
        try {
            //获取总条数
            long start = System.currentTimeMillis();
            int total = bookChapterDao.getTotal();
            if (total > 0) {
                //获取字典
                String words = dicWordData.getWords();
                byte[] dicArray = createDic(words);
                //存放乱码
                //List<BookChapterDetect> detectList = new ArrayList<>();
                List<BookChapterDetect> detectList = new Vector<>();
                //获取日期格式转换
                ThreadLocal<DateFormat> threadLocal = new ThreadLocal<>();
                DateFormat df = threadLocal.get();
                if (df == null) {
                    df = new SimpleDateFormat("yyyyMMddHHmmss");
                    threadLocal.set(df);
                }
                //表格路径
                List<String> results = new ArrayList<>();
                int pageSize = config.getBatchSize();
                int pages = total / pageSize + 1;
                //将页码存入队列
                LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
                for (int i = 1; i < pages + 1; i++) {
                    queue.put(i);
                }
                //创建乱码检查任务
                DetectBookCode detectBookCode = new DetectBookCode(queue,
                        bookChapterDao,
                        bookMetaDao,
                        chapterNameDetect,
                        pages,
                        detectList,
                        config,
                        results,
                        dicArray,
                        df,
                        pageSize);
                //获取cpu的核数
                int cpuSum = Runtime.getRuntime().availableProcessors();
                ThreadPoolExecutor executor = new ThreadPoolExecutor(config.getThreadTime() * cpuSum,
                        config.getThreadTime() * cpuSum,
                        60,
                        TimeUnit.SECONDS,
                        new LinkedBlockingDeque<Runnable>());
                for (int i = 0; i < pages; i++) {
                    executor.execute(detectBookCode);
                }
                executor.shutdown();
                while (true) {
                    if (executor.isTerminated()) {
                        log.info("所有线程结束");
                        long end = System.currentTimeMillis();
                        log.info("检测图书章节内容乱码总耗时：{}", (end - start));
                        //生成检测结果
                        List<BookChapterDetect> bookChapterDetects = detectList.stream()
                                .sorted(Comparator.comparing(BookChapterDetect::getMetaId))
                                .collect(Collectors.toList());
                        String resultPath = config.getBookDetect() + File.separator + df.format(new Date()) + "code.xlsx";
                        BookUtil.exportExcel(bookChapterDetects, resultPath);
                        //保存表格路径到总表
                        results.add(resultPath);
                        log.info("检查乱码结果已生成到{}", config.getBookDetect());
                        //发送邮件
                        EMailUtil eMailUtil = new EMailUtil(systemConfMapper);
                        eMailUtil.createSender();
                        eMailUtil.sendAttachmentsMail(results, "检查乱码结果");
                        log.info("检查乱码结果已发送邮件");
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("乱码检查时出现异常{}", e.getMessage());
        }
    }

    //生成字典
    private byte[] createDic(String words) {
        if (org.apache.commons.lang3.StringUtils.isNotBlank(words)) {
            char[] dicWords = words.toCharArray();
            char max = 0;
            for (char word : dicWords) {
                if (word > max) {
                    max = word;
                }
            }
            //定义字典
            byte[] dicArray = new byte[max + 1];
            for (int i = 0; i < dicWords.length; i++) {
                dicArray[dicWords[i]] = 1;
            }
            return dicArray;
        }
        return null;
    }

    //检查图书中的公众号和qq号
    @Override
    @Async
    public void detectBookSource() {
        try {
            //获取总条数
            long start = System.currentTimeMillis();
            int total = bookChapterDao.getTotal();
            if (total > 0) {
                //获取正则匹配
                Pattern pattern = BookConstant.DETECT_SOURCE;
                //存放提取内容
                List<BookChapterDetect> detectList = new Vector<>();
                //获取日期格式转换
                ThreadLocal<DateFormat> threadLocal = new ThreadLocal<>();
                DateFormat df = threadLocal.get();
                if (df == null) {
                    df = new SimpleDateFormat("yyyyMMddHHmmss");
                    threadLocal.set(df);
                }
                //表格路径
                List<String> results = new ArrayList<>();
                int pageSize = config.getBatchSize();
                int pages = total / pageSize + 1;
                //将页码存入队列
                LinkedBlockingQueue<Integer> queue = new LinkedBlockingQueue<>();
                for (int i = 1; i < pages + 1; i++) {
                    queue.put(i);
                }
                //创建乱码检查任务
                DetectBookSource detectBookSource = new DetectBookSource(queue,
                        pageSize,
                        bookChapterDao,
                        bookMetaDao,
                        pattern,
                        detectList,
                        pages,
                        chapterNameDetect,
                        config,
                        results,
                        df);
                //获取cpu的核数
                int cpuSum = Runtime.getRuntime().availableProcessors();
                ThreadPoolExecutor executor = new ThreadPoolExecutor(config.getThreadTime() * cpuSum,
                        config.getThreadTime() * cpuSum,
                        60,
                        TimeUnit.SECONDS,
                        new LinkedBlockingDeque<Runnable>());
                for (int i = 0; i < pages; i++) {
                    executor.execute(detectBookSource);
                }
                executor.shutdown();
                while (true) {
                    if (executor.isTerminated()) {
                        log.info("所有线程结束");
                        long end = System.currentTimeMillis();
                        log.info("检测图书章节公众号和QQ总耗时：{}", (end - start));
                        //生成检测结果
                        List<BookChapterDetect> bookChapterDetects = detectList.stream()
                                .sorted(Comparator.comparing(BookChapterDetect::getMetaId))
                                .collect(Collectors.toList());
                        String resultPath = config.getBookDetect() + File.separator + df.format(new Date()) + "source.xlsx";
                        BookUtil.exportExcel(bookChapterDetects, resultPath);
                        //保存表格路径到总表
                        results.add(resultPath);
                        log.info("检查公众号和QQ结果已生成到{}", config.getBookDetect());
                        //发送邮件
                        EMailUtil eMailUtil = new EMailUtil(systemConfMapper);
                        eMailUtil.createSender();
                        eMailUtil.sendAttachmentsMail(results, "检查公众号和QQ结果");
                        log.info("检查公众号和QQ结果已发送邮件");
                        break;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("公众号和QQ检查时出现异常{}", e.getMessage());
        }
    }

    //乱码信息整合
    private List collectCodeInfo(Map<String, String> codeMap) {
        if (codeMap != null) {
            //存储乱码整合信息
            List<BookChapterDetect> detectList = new ArrayList<>();
            for (String comId : codeMap.keySet()) {
                try {
                    BookChapterSum bookChapterSum = bookChapterDao.findChapterByComId(comId);
                    //获取图书id
                    int chapterNum = bookChapterSum.getChapterNum();
                    String metaId = comId.substring(0, comId.lastIndexOf(String.valueOf(chapterNum)));
                    //获取章节标题
                    EpubookMeta epubookMeta = bookMetaDao.findEpubookMetaById(metaId);
                    String chapterName = getChapterName(epubookMeta.getStreamCatalog(), chapterNum);
                    //合成信息
                    BookChapterDetect bookChapterDetect = new BookChapterDetect();
                    bookChapterDetect.setMetaId(metaId);
                    bookChapterDetect.setTitle(epubookMeta.getTitle());
                    bookChapterDetect.setChapterNum(chapterNum);
                    bookChapterDetect.setChapterName(chapterName);
                    bookChapterDetect.setMessage(codeMap.get(comId));
                    detectList.add(bookChapterDetect);
                } catch (Exception e) {
                    log.warn("整合章节{}乱码信息异常：{}", comId, e.getMessage());
                }
            }
            return detectList;
        }
        return null;
    }

    //获取章节标题
    private String getChapterName(String cataLog, int chapterNum) {
        if (org.apache.commons.lang3.StringUtils.isNotBlank(cataLog)) {
            String tmp = String.valueOf(cataLog.charAt(0));
            if (tmp.equals("[")) {
                //获取层次目录
                JSONArray jsonArray = JSONArray.fromObject(cataLog);
                Iterator it = jsonArray.iterator();
                while (it.hasNext()) {
                    JSONObject jsonObject = (JSONObject) it.next();
                    createCataTree(jsonObject, chapterNum);
                }
                return chapterNameDetect;
            } else {
                //获取非层次目录
                List<String> cataRows = Arrays.asList(cataLog.split("},"));
                //生成目录
                JSONObject jsonObject;
                for (String cata : cataRows) {
                    jsonObject = JSONObject.fromObject(cata + "}");
                    BookCataRows bookCataRows = (BookCataRows) JSONObject.toBean(jsonObject, BookCataRows.class);
                    if (bookCataRows.getChapterNum() == chapterNum) {
                        return bookCataRows.getChapterName();
                    }
                }
            }
        }
        return null;
    }

    //遍历目录
    private void createCataTree(JSONObject jsonObject, int chapterNum) {
        if (jsonObject != null) {
            List<JSONObject> childE = jsonObject.getJSONArray("children");
            if (childE != null && childE.size() > 0) {
                if (jsonObject.getInt("chapterNum") == chapterNum) {
                    chapterNameDetect = jsonObject.getString("chapterName");
                }
                for (JSONObject child : childE) {
                    createCataTree(child, chapterNum);
                }
            } else {
                if (jsonObject.getInt("chapterNum") == chapterNum) {
                    chapterNameDetect = jsonObject.getString("chapterName");
                }
            }
        }
    }
}
