package com.apabi.flow.book.service.impl;

import com.apabi.flow.admin.util.EmailUtil;
import com.apabi.flow.book.dao.BookChapterDao;
import com.apabi.flow.book.dao.BookMetaDao;
import com.apabi.flow.book.dao.BookShardDao;
import com.apabi.flow.book.model.*;
import com.apabi.flow.book.service.BookChapterService;
import com.apabi.flow.book.util.BookConstant;
import com.apabi.flow.book.util.BookUtil;
import com.apabi.flow.book.util.EMailUtil;
import com.apabi.flow.config.ApplicationConfig;
import com.apabi.flow.config.DicWordData;
import com.apabi.flow.systemconf.dao.SystemConfMapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
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

    private static String chapterNameCode;

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
    public void detectBookChapter() {
        //获取总条数
        long start = System.currentTimeMillis();
        int total = bookChapterDao.getTotal();
        if (total > 0) {
            //获取字典
            String words = dicWordData.getWords();
            byte[] dicArray = createDic(words);
            //存放乱码
            List<BookChapterDetect> detectList = new ArrayList<>();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
            List<String> results = new ArrayList<>();
            int pageSize = config.getBatchSize();
            int pages = total / pageSize + 1;
            for (int i = 1; i < pages + 1; i++) {
                long start1 = System.currentTimeMillis();
                Map<String, String> codeMap = new HashMap<>();
                PageHelper.startPage(i, pageSize);
                Page<BookChapter> page = bookChapterDao.findBookChapterByPage();
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
                log.info("总批次为：{}，已完成批次：{}，耗时：{}", pages, i, (end1 - start1));
                if (i == pages) {
                    //生成表格
                    List<BookChapterDetect> bookChapterDetects = detectList.stream()
                            .sorted(Comparator.comparing(BookChapterDetect::getMetaId))
                            .collect(Collectors.toList());
                    String resultPath = config.getBookDetect() + File.separator + sdf.format(new Date()) + "code.xls";
                    BookUtil.exportExcel(bookChapterDetects, resultPath);
                    //保存表格路径到总表
                    results.add(resultPath);
                    log.info("检查结果已生成到{}", config.getBookDetect());
                }
                //如果行数大于60000，则输出
                if (detectList.size() > 60000) {
                    //生成表格
                    List<BookChapterDetect> bookChapterDetects = detectList.stream()
                            .sorted(Comparator.comparing(BookChapterDetect::getMetaId))
                            .collect(Collectors.toList());
                    String resultPath = config.getBookDetect() + File.separator + sdf.format(new Date()) + "code.xls";
                    BookUtil.exportExcel(bookChapterDetects, resultPath);
                    //清空乱码结果总表
                    detectList.clear();
                    //保存表格路径到总表
                    results.add(resultPath);
                    log.info("检查结果已生成到{}", config.getBookDetect());
                }
            }
            long end = System.currentTimeMillis();
            log.info("检测图书章节内容乱码总耗时：{}", (end - start));
            //发送邮件
            EMailUtil eMailUtil = new EMailUtil(systemConfMapper);
            eMailUtil.createSender();
            eMailUtil.sendAttachmentsMail(results);
            log.info("检查结果已发送邮件");
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
}
