package com.apabi.flow.book.task;

import com.apabi.flow.book.dao.BookChapterDao;
import com.apabi.flow.book.dao.BookMetaDao;
import com.apabi.flow.book.model.*;
import com.apabi.flow.book.service.impl.BookChapterServiceImpl;
import com.apabi.flow.book.util.BookUtil;
import com.apabi.flow.config.ApplicationConfig;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.DateFormat;
import java.util.*;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.stream.Collectors;

/**
 * @author guanpp
 * @date 2018/11/23 14:23
 * @description
 */
public class DetectBookCode implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(DetectBookCode.class);

    private LinkedBlockingQueue<Integer> pageNumqQueue;

    private BookChapterDao bookChapterDao;

    private BookMetaDao bookMetaDao;

    private String chapterNameCode;

    private int pages;

    private List<BookChapterDetect> detectList;

    private ApplicationConfig config;

    private List<String> results;

    private DateFormat df;

    private byte[] dicArray;

    private Integer pageSize;

    public DetectBookCode(LinkedBlockingQueue<Integer> pageNumqQueue,
                          BookChapterDao bookChapterDao,
                          BookMetaDao bookMetaDao,
                          String chapterNameCode,
                          int pages,
                          List<BookChapterDetect> detectList,
                          ApplicationConfig config,
                          List<String> results,
                          byte[] dicArray,
                          DateFormat df,
                          Integer pageSize) {
        this.pageNumqQueue = pageNumqQueue;
        this.bookChapterDao = bookChapterDao;
        this.bookMetaDao = bookMetaDao;
        this.chapterNameCode = chapterNameCode;
        this.pages = pages;
        this.detectList = detectList;
        this.config = config;
        this.results = results;
        this.df = df;
        this.dicArray = dicArray;
        this.pageSize = pageSize;
    }

    @Override
    public void run() {
        int pageNum = 0;
        try {
            pageNum = pageNumqQueue.take();
            long start1 = System.currentTimeMillis();
            Map<String, String> codeMap = new HashMap<>();
            PageHelper.startPage(pageNum, pageSize,"CREATETIME");
            Page<BookChapter> page = bookChapterDao.findBookChapterByPage();
            PageHelper.clearPage();
            List<BookChapter> chapterList = page.getResult();
            //检测乱码
            for (BookChapter chapter : chapterList) {
                try {
                    String content = chapter.getContent();
                    if (org.apache.commons.lang3.StringUtils.isNotBlank(content)) {
                        char[] chars = content.toCharArray();
                        for (int j = 0; j < chars.length; j++) {
                            if (dicArray[chars[j]] == 0) {
                                if (codeMap.containsKey(chapter.getComId())) {
                                    String code = codeMap.get(chapter.getComId());
                                    code += String.valueOf(chars[j]);
                                    codeMap.put(chapter.getComId(), code);
                                } else {
                                    codeMap.put(chapter.getComId(), String.valueOf(chars[j]));
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    log.warn("检测章节{}乱码信息异常：{}", chapter.getComId(), e.getMessage());
                }
            }
            List<BookChapterDetect> tmp = collectCodeInfo(codeMap);
            detectList.addAll(tmp);
            long end1 = System.currentTimeMillis();
            log.info("总批次为：{}，已完成批次：{}，耗时：{}", pages, pageNum, (end1 - start1));
           /* if (pageNum == pages) {
                //生成表格
                List<BookChapterDetect> bookChapterDetects = detectList.stream()
                        .sorted(Comparator.comparing(BookChapterDetect::getMetaId))
                        .collect(Collectors.toList());
                String resultPath = config.getBookDetect() + File.separator + df.format(new Date()) + "code.xls";
                BookUtil.exportExcel(bookChapterDetects, resultPath);
                //保存表格路径到总表
                results.add(resultPath);
                log.info("检查结果已生成到{}", config.getBookDetect());
            } else if (detectList.size() > 60000) {
                //如果行数大于60000，则输出
                //生成表格
                List<BookChapterDetect> bookChapterDetects = detectList.stream()
                        .sorted(Comparator.comparing(BookChapterDetect::getMetaId))
                        .collect(Collectors.toList());
                String resultPath = config.getBookDetect() + File.separator + df.format(new Date()) + "code.xls";
                BookUtil.exportExcel(bookChapterDetects, resultPath);
                //清空乱码结果总表
                detectList.clear();
                //保存表格路径到总表
                results.add(resultPath);
                log.info("检查结果已生成到{}", config.getBookDetect());
            }*/
        } catch (Exception e) {
            log.warn("检查第{}批次，出现异常{}", pageNum, e.getMessage());
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
                return chapterNameCode;
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
                    chapterNameCode = jsonObject.getString("chapterName");
                }
                for (JSONObject child : childE) {
                    createCataTree(child, chapterNum);
                }
            } else {
                if (jsonObject.getInt("chapterNum") == chapterNum) {
                    chapterNameCode = jsonObject.getString("chapterName");
                }
            }
        }
    }
}
