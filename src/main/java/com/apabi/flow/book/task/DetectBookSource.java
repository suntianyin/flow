package com.apabi.flow.book.task;

import com.apabi.flow.book.dao.BookChapterDao;
import com.apabi.flow.book.dao.BookMetaDao;
import com.apabi.flow.book.model.*;
import com.apabi.flow.config.ApplicationConfig;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author guanpp
 * @date 2018/11/26 13:35
 * @description
 */
public class DetectBookSource implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(DetectBookSource.class);

    private LinkedBlockingQueue<Integer> pageNumqQueue;

    private Integer pageSize;

    private BookChapterDao bookChapterDao;

    private BookMetaDao bookMetaDao;

    private Pattern pattern;

    private List<BookChapterDetect> detectList;

    private int pages;

    private String chapterName;

    private ApplicationConfig config;

    private List<String> results;

    private DateFormat df;


    public DetectBookSource(LinkedBlockingQueue<Integer> pageNumqQueue,
                            Integer pageSize,
                            BookChapterDao bookChapterDao,
                            BookMetaDao bookMetaDao,
                            Pattern pattern,
                            List<BookChapterDetect> detectList,
                            int pages,
                            String chapterName,
                            ApplicationConfig config,
                            List<String> results,
                            DateFormat df) {
        this.pageNumqQueue = pageNumqQueue;
        this.bookChapterDao = bookChapterDao;
        this.bookMetaDao = bookMetaDao;
        this.pattern = pattern;
        this.chapterName = chapterName;
        this.pages = pages;
        this.detectList = detectList;
        this.config = config;
        this.results = results;
        this.df = df;
        this.pageSize = pageSize;

    }

    @Override
    public void run() {
        int pageNum = 0;
        try {
            pageNum = pageNumqQueue.take();
            long start = System.currentTimeMillis();
            Map<String, String> codeMap = new HashMap<>();
            PageHelper.startPage(pageNum, pageSize, "CREATETIME");
            Page<BookChapter> page = bookChapterDao.findBookChapterByPage();
            PageHelper.clearPage();
            List<BookChapter> chapterList = page.getResult();
            //检测乱码
            for (BookChapter chapter : chapterList) {
                try {
                    String content = chapter.getContent();
                    if (org.apache.commons.lang3.StringUtils.isNotBlank(content)) {
                        Matcher matcher = pattern.matcher(content);
                        while (matcher.find()) {
                            if (codeMap.containsKey(chapter.getComId())) {
                                String code = codeMap.get(chapter.getComId());
                                code += matcher.group();
                                codeMap.put(chapter.getComId(), code);
                            } else {
                                codeMap.put(chapter.getComId(), matcher.group());
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("检测章节{}公众号和QQ异常：{}", chapter.getComId(), e.getMessage());
                }
            }
            List<BookChapterDetect> tmp = collectCodeInfo(codeMap);
            synchronized (detectList) {
                detectList.addAll(tmp);
            }
            long end = System.currentTimeMillis();
            log.info("检查公众号和QQ总批次为：{}，已完成批次：{}，耗时：{}", pages, pageNum, (end - start));
        } catch (Exception e) {
            log.warn("检查公众号和QQ第{}批次，出现异常{}", pageNum, e.getMessage());
        }
    }

    //公众号和QQ检查结果信息整合
    private List collectCodeInfo(Map<String, String> codeMap) {
        if (codeMap != null) {
            //存储结果整合信息
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
                    log.warn("整合章节{}公众号和QQ检查结果信息异常：{}", comId, e.getMessage());
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
                return chapterName;
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
                    chapterName = jsonObject.getString("chapterName");
                }
                for (JSONObject child : childE) {
                    createCataTree(child, chapterNum);
                }
            } else {
                if (jsonObject.getInt("chapterNum") == chapterNum) {
                    chapterName = jsonObject.getString("chapterName");
                }
            }
        }
    }
}
