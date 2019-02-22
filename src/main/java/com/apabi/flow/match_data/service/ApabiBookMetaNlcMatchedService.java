package com.apabi.flow.match_data.service;

import com.apabi.flow.match_data.dao.ApabiBookMetaNlcMatchedDao;
import com.apabi.flow.match_data.dao.ApabiBookMetaNlcMatcherDao;
import com.apabi.flow.match_data.model.ApabiBookMetaNlcMatched;
import com.apabi.flow.match_data.model.ApabiBookMetaNlcMatcher;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author pipi
 * @Date 2019-1-7 17:39
 **/
@RestController
@RequestMapping("apabiNlcMatched")
public class ApabiBookMetaNlcMatchedService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApabiBookMetaNlcMatchedService.class);
    @Autowired
    private ApabiBookMetaNlcMatcherDao apabiBookMetaNlcMatcherDao;
    @Autowired
    private ApabiBookMetaNlcMatchedDao apabiBookMetaNlcMatchedDao;

    /**
     * 根据matcher中的数据清洗apabiTitle和nlcMarcAuthor字段，将清洗出的值分别写入apabiTitleClean和nlcMarcAuthor字段
     * 并更新matcher数据
     *
     * @return
     */
    @RequestMapping("updateMatcher")
    public String matchIsbn() {
        int count = apabiBookMetaNlcMatcherDao.count();
        int pageSize = 10000;
        int pageNum = (count / pageSize) + 1;
        for (int i = 1; i <= pageNum; i++) {
            PageHelper.startPage(i, pageSize);
            Page<ApabiBookMetaNlcMatcher> apabiBookMetaNlcMatcherList = apabiBookMetaNlcMatcherDao.findByPage();
            for (ApabiBookMetaNlcMatcher apabiBookMetaNlcMatcher : apabiBookMetaNlcMatcherList) {
                String apabiTitle = apabiBookMetaNlcMatcher.getApabiTitle();
                String nlcMarcAuthor = apabiBookMetaNlcMatcher.getNlcMarcAuthor();
                if (StringUtils.isNotEmpty(apabiTitle)) {
                    String apabiTitleClean = cleanTitle(apabiTitle);
                    apabiBookMetaNlcMatcher.setApabiTitleClean(apabiTitleClean);
                }
                if (StringUtils.isNotEmpty(nlcMarcAuthor)) {
                    String nlcMarcAuthorClean = cleanAuthor(nlcMarcAuthor);
                    apabiBookMetaNlcMatcher.setNlcMarcAuthorClean(nlcMarcAuthorClean);
                }
                try {
                    apabiBookMetaNlcMatcherDao.update(apabiBookMetaNlcMatcher);
                } catch (Exception e) {
                }
            }
        }
        return "complete";
    }

    /**
     * 根据模糊匹配算法，将匹配上的数据写入到matched表中
     *
     * @return
     */
    @RequestMapping("insertMatched")
    public String insertMatched() {
        int count = apabiBookMetaNlcMatcherDao.count();
        int pageSize = 10000;
        int pageNum = (count / pageSize) + 1;
        for (int i = 1; i <= pageNum; i++) {
            PageHelper.startPage(i, pageSize);
            Page<ApabiBookMetaNlcMatcher> apabiBookMetaNlcMatcherList = apabiBookMetaNlcMatcherDao.findByPage();
            for (ApabiBookMetaNlcMatcher apabiBookMetaNlcMatcher : apabiBookMetaNlcMatcherList) {
                if (containsTitleAndContainsAuthor(apabiBookMetaNlcMatcher)) {
                    ApabiBookMetaNlcMatched apabiBookMetaNlcMatched = new ApabiBookMetaNlcMatched();

                    String metaId = apabiBookMetaNlcMatcher.getMetaId();
                    String nlcMarcId = apabiBookMetaNlcMatcher.getNlcMarcId();
                    String isbn = apabiBookMetaNlcMatcher.getIsbn();
                    String isbn10 = apabiBookMetaNlcMatcher.getIsbn10();
                    String isbn13 = apabiBookMetaNlcMatcher.getIsbn13();
                    String apabiTitle = apabiBookMetaNlcMatcher.getApabiTitle();
                    String apabiTitleClean = apabiBookMetaNlcMatcher.getApabiTitleClean();
                    String nlcMarcTitle = apabiBookMetaNlcMatcher.getNlcMarcTitle();
                    String apabiAuthor = apabiBookMetaNlcMatcher.getApabiAuthor();
                    String nlcMarcAuthor = apabiBookMetaNlcMatcher.getNlcMarcAuthor();
                    String nlcMarcAuthorClean = apabiBookMetaNlcMatcher.getNlcMarcAuthorClean();
                    String apabiPublisher = apabiBookMetaNlcMatcher.getApabiPublisher();
                    String nlcMarcPublisher = apabiBookMetaNlcMatcher.getNlcMarcPublisher();

                    apabiBookMetaNlcMatched.setMetaId(metaId);
                    apabiBookMetaNlcMatched.setNlcMarcId(nlcMarcId);
                    apabiBookMetaNlcMatched.setIsbn(isbn);
                    apabiBookMetaNlcMatched.setIsbn10(isbn10);
                    apabiBookMetaNlcMatched.setIsbn13(isbn13);
                    apabiBookMetaNlcMatched.setApabiTitle(apabiTitle);
                    apabiBookMetaNlcMatched.setApabiTitleClean(apabiTitleClean);
                    apabiBookMetaNlcMatched.setNlcMarcTitle(nlcMarcTitle);
                    apabiBookMetaNlcMatched.setApabiAuthor(apabiAuthor);
                    apabiBookMetaNlcMatched.setNlcMarcAuthor(nlcMarcAuthor);
                    apabiBookMetaNlcMatched.setNlcMarcAuthorClean(nlcMarcAuthorClean);
                    apabiBookMetaNlcMatched.setApabiPublisher(apabiPublisher);
                    apabiBookMetaNlcMatched.setNlcMarcPublisher(nlcMarcPublisher);
                    try {
                        apabiBookMetaNlcMatchedDao.insert(apabiBookMetaNlcMatched);
                        LOGGER.info(apabiBookMetaNlcMatched.getMetaId() + "插入到matched表中成功");
                    } catch (Exception e) {
                        e.printStackTrace();
                        LOGGER.info(apabiBookMetaNlcMatched.getMetaId() + "插入到matched表中失败，原因为：" + e.getMessage());
                    }
                }
            }
        }
        return "complete";
    }

    /**
     * 模糊匹配算法
     * 根据longTitle.contains(shortTitle) && longAuthor.contains(shortAuthor)
     *
     * @param apabiBookMetaNlcChecker
     * @return
     */
    private boolean containsTitleAndContainsAuthor(ApabiBookMetaNlcMatcher apabiBookMetaNlcChecker) {
        boolean flag = false;
        try {
            String apabiBookMetaTitleClean = apabiBookMetaNlcChecker.getApabiTitleClean().trim();
            String nlcTitle = apabiBookMetaNlcChecker.getNlcMarcTitle().trim();
            String nlcAuthorClean = apabiBookMetaNlcChecker.getNlcMarcAuthorClean();
            String apabiMetaAuthor = apabiBookMetaNlcChecker.getApabiAuthor();
            // 清洗title中的全角符改为半角符
            apabiBookMetaTitleClean = cleanPunctuationInTitle(apabiBookMetaTitleClean);
            nlcTitle = cleanPunctuationInTitle(nlcTitle);
            // 清洗author中的特殊符号
            nlcAuthorClean = cleanPunctuationInAuthor(nlcAuthorClean);
            apabiMetaAuthor = cleanPunctuationInAuthor(apabiMetaAuthor);
            // 取长度较长的author
            String shortAuthor = nlcAuthorClean.length() > apabiMetaAuthor.length() ? apabiMetaAuthor : nlcAuthorClean;
            String longAuthor = nlcAuthorClean.length() < apabiMetaAuthor.length() ? apabiMetaAuthor : nlcAuthorClean;
            // 取长度较长的title
            String shortTitle = nlcTitle.length() > apabiBookMetaTitleClean.length() ? apabiBookMetaTitleClean : nlcTitle;
            String longTitle = nlcTitle.length() < apabiBookMetaTitleClean.length() ? apabiBookMetaTitleClean : nlcTitle;
            // 第四轮判断
            if (longTitle.contains(shortTitle) && longAuthor.contains(shortAuthor)) {
                flag = true;
            }
        } catch (Exception e) {
        }
        return flag;
    }

    /**
     * 根据apabi_title清洗出apabi_title_clean算法实现
     *
     * @param apabiTitle
     * @return
     */
    private String cleanTitle(String apabiTitle) {
        String apabiTitleClean = null;
        if (StringUtils.isNotEmpty(apabiTitle)) {
            if (apabiTitle.contains(":")) {
                apabiTitleClean = apabiTitle.substring(0, apabiTitle.indexOf(":")).trim();
                if (apabiTitleClean.contains(" ")) {
                    apabiTitleClean = apabiTitleClean.substring(0, apabiTitleClean.lastIndexOf(" ")).trim();
                }
            } else if (apabiTitle.contains("：")) {
                apabiTitleClean = apabiTitle.substring(0, apabiTitle.indexOf("：")).trim();
                if (apabiTitleClean.contains(" ")) {
                    apabiTitleClean = apabiTitleClean.substring(0, apabiTitleClean.lastIndexOf(" ")).trim();
                }
            } else if (apabiTitle.contains(" ")) {
                apabiTitleClean = apabiTitle.substring(0, apabiTitle.indexOf(" ")).trim();
            }
            if (StringUtils.isEmpty(apabiTitleClean)) {
                apabiTitleClean = apabiTitle;
            }
        }
        return apabiTitleClean;
    }

    /**
     * 根据nlc_marc_author清洗出nlc_marc_author_clean算法实现
     *
     * @param nlcAuthor
     * @return
     */
    private String cleanAuthor(String nlcAuthor) {
        String nlcAuthorClean = null;
        if (StringUtils.isNotEmpty(nlcAuthor)) {
            nlcAuthorClean = nlcAuthor;
            // 替换全角空格
            nlcAuthorClean = nlcAuthorClean.replace((char) 12288, ' ');
            if (nlcAuthorClean.contains("编委会编著")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("编委会编著", "编委会").trim();
            }
            if (nlcAuthorClean.contains("办公室编著")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("办公室编著", "办公室").trim();
            }
            if (nlcAuthorClean.contains("研究会编著")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("研究会编著", "研究会").trim();
            }
            if (nlcAuthorClean.contains("编辑部编著")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("编辑部编著", "编辑部").trim();
            }
            if (nlcAuthor.contains("编委会编")) {
                nlcAuthorClean = nlcAuthor.replaceAll("编委会编", "编委会").trim();
            }
            if (nlcAuthorClean.contains("办公室编")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("办公室编", "办公室").trim();
            }
            if (nlcAuthorClean.contains("编辑部编")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("编辑部编", "编辑部").trim();
            }
            if (nlcAuthorClean.contains("研究会编")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("研究会编", "研究会").trim();
            }
            if (nlcAuthorClean.contains("改编/演奏")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("改编/演奏", "").trim();
            }
            if (nlcAuthorClean.contains("联合编纂")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("联合编纂", "").trim();
            }
            if (nlcAuthorClean.contains("等撰稿")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("等撰稿", "").trim();
            }
            if (nlcAuthorClean.contains("总主编")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("总主编", "").trim();
            }
            if (nlcAuthorClean.contains("等编写")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("等编写", "").trim();
            }
            if (nlcAuthorClean.contains("等主编")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("等主编", "").trim();
            }
            if (nlcAuthorClean.contains("等撰写")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("等撰写", "").trim();
            }
            if (nlcAuthorClean.contains("等编译")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("等编译", "").trim();
            }
            if (nlcAuthorClean.contains("等编文")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("等编文", "").trim();
            }
            if (nlcAuthorClean.contains("等撰文")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("等撰文", "").trim();
            }
            if (nlcAuthorClean.contains("等选注")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("等选注", "").trim();
            }
            if (nlcAuthorClean.contains("总编审")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("总编审", "").trim();
            }
            if (nlcAuthorClean.contains("总编辑")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("总编辑", "").trim();
            }
            if (nlcAuthorClean.contains("采写")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("采写", "").trim();
            }
            if (nlcAuthorClean.contains("主编")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("主编", "").trim();
            }
            if (nlcAuthorClean.contains("等编")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("等编", "").trim();
            }
            if (nlcAuthorClean.contains("等选")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("等选", "").trim();
            }
            if (nlcAuthorClean.contains("等著")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("等著", "").trim();
            }
            if (nlcAuthorClean.contains("等绘")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("等绘", "").trim();
            }
            if (nlcAuthorClean.contains("等译")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("等译", "").trim();
            }
            if (nlcAuthorClean.contains("编著")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("编著", "").trim();
            }
            if (nlcAuthorClean.contains("编配")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("编配", "").trim();
            }
            if (nlcAuthorClean.contains("选编")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("选编", "").trim();
            }
            if (nlcAuthorClean.contains("选辑")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("选辑", "").trim();
            }
            if (nlcAuthorClean.contains("编绘")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("编绘", "").trim();
            }
            if (nlcAuthorClean.contains("翻译")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("翻译", "").trim();
            }
            if (nlcAuthorClean.contains("撰稿")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("撰稿", "").trim();
            }
            if (nlcAuthorClean.contains("总编")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("总编", "").trim();
            }
            if (nlcAuthorClean.contains("主撰")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("主撰", "").trim();
            }
            if (nlcAuthorClean.contains("改编")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("改编", "").trim();
            }
            if (nlcAuthorClean.contains("[等编著]")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("\\[等编著\\]", "").trim();
            }
            if (nlcAuthorClean.contains("[等本卷]")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("\\[等本卷\\]", "").trim();
            }
            if (nlcAuthorClean.contains("[等编]")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("\\[等编\\]", "").trim();
            }
            if (nlcAuthorClean.contains("[改编]")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("\\[改编\\]", "").trim();
            }
            if (nlcAuthorClean.contains("[编写]")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("\\[编写\\]", "").trim();
            }
            if (nlcAuthorClean.contains("[编著]")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("\\[编写\\]", "").trim();
            }
            if (nlcAuthorClean.contains("[编]")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("\\[编\\]", "").trim();
            }
            if (nlcAuthorClean.contains("[著]")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("\\[著\\]", "").trim();
            }
            if (nlcAuthorClean.contains("[绘]")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("\\[绘\\]", "").trim();
            }
            if (nlcAuthorClean.contains("[等]")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("\\[等\\]", "").trim();
            }
            if (nlcAuthorClean.contains("[书]")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("\\[书\\]", "").trim();
            }
            if (nlcAuthorClean.contains("[摄]")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("\\[摄\\]", "").trim();
            }
            if (nlcAuthorClean.contains("[作]")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("\\[作\\]", "").trim();
            }
            if (nlcAuthorClean.contains("，")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("，", ";").trim();
            }
            if (nlcAuthorClean.contains(",")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll(",", ";").trim();
            }
            if (nlcAuthorClean.startsWith("[") && nlcAuthorClean.endsWith("]")) {
                nlcAuthorClean = nlcAuthorClean.replace("[", "");
                nlcAuthorClean = nlcAuthorClean.replace("]", "");
            }
            if (nlcAuthorClean.contains("[]")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("\\[\\]", "").trim();
            }
            if (nlcAuthorClean.contains("()")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("\\(\\)", "").trim();
            }
            if (nlcAuthorClean.endsWith("撰写/插图")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("撰写/插图"));
            }
            if (nlcAuthorClean.endsWith("设计绘图")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("设计绘图"));
            }
            if (nlcAuthorClean.endsWith("编写")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("编写"));
            }
            if (nlcAuthorClean.endsWith("编文")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("编文"));
            }
            if (nlcAuthorClean.endsWith("编辑")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("编辑"));
            }
            if (nlcAuthorClean.endsWith("注释")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("注释"));
            }
            if (nlcAuthorClean.endsWith("编译")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("编译"));
            }
            if (nlcAuthorClean.endsWith("编制")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("编制"));
            }
            if (nlcAuthorClean.endsWith("编选")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("编选"));
            }
            if (nlcAuthorClean.endsWith("摄影")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("摄影"));
            }
            if (nlcAuthorClean.endsWith("颁布")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("颁布"));
            }
            if (nlcAuthorClean.endsWith("编撰")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("编撰"));
            }
            if (nlcAuthorClean.endsWith("总纂")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("总纂"));
            }
            if (nlcAuthorClean.endsWith("编审")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("编审"));
            }
            if (nlcAuthorClean.endsWith("撰文")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("撰文"));
            }
            if (nlcAuthorClean.endsWith("翻译")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("翻译"));
            }
            if (nlcAuthorClean.endsWith("评析")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("评析"));
            }
            if (nlcAuthorClean.endsWith("编纂")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("编纂"));
            }
            if (nlcAuthorClean.endsWith("解译")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("解译"));
            }
            if (nlcAuthorClean.endsWith("主办")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("主办"));
            }
            if (nlcAuthorClean.endsWith("选注")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("选注"));
            }
            if (nlcAuthorClean.endsWith("原著")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("原著"));
            }
            if (nlcAuthorClean.endsWith("采编")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("采编"));
            }
            if (nlcAuthorClean.endsWith("作曲")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("作曲"));
            }
            if (nlcAuthorClean.endsWith("译诗")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("作曲"));
            }
            if (nlcAuthorClean.endsWith("/摄")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("/摄"));
            }
            if (nlcAuthorClean.endsWith("编")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("编"));
            }
            if (nlcAuthorClean.endsWith("著")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("著"));
            }
            if (nlcAuthorClean.endsWith("写")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("写"));
            }
            if (nlcAuthorClean.endsWith("等")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("等"));
            }
            if (nlcAuthorClean.endsWith("绘")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("绘"));
            }
            if (nlcAuthorClean.endsWith("撰")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("撰"));
            }
            if (nlcAuthorClean.endsWith("辑")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("辑"));
            }
            if (nlcAuthorClean.endsWith("摄")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("摄"));
            }
            if (nlcAuthorClean.endsWith("译")) {
                nlcAuthorClean = nlcAuthorClean.substring(0, nlcAuthorClean.lastIndexOf("译"));
            }
            if (nlcAuthorClean.contains(" ")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll(" ", "").trim();
            }
            if (nlcAuthorClean.contains("[]")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("\\[\\]", "").trim();
            }
            if (nlcAuthorClean.contains("（")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("（", "(");
            }
            if (nlcAuthorClean.contains("）")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("）", ")");
            }
            if (nlcAuthorClean.contains("］")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("］", "]");
            }
            if (nlcAuthorClean.contains("()")) {
                nlcAuthorClean = nlcAuthorClean.replaceAll("\\(\\)", "").trim();
            }
            try {
                while (nlcAuthorClean.contains("(")) {
                    int start = nlcAuthorClean.indexOf("(");
                    int end = nlcAuthorClean.indexOf(")");
                    String replace = nlcAuthorClean.substring(start, end + 1);
                    nlcAuthorClean = nlcAuthorClean.replace(replace, "");
                }
            } catch (Exception e) {
            }
            try {
                while (nlcAuthorClean.contains("[")) {
                    int start = nlcAuthorClean.indexOf("[");
                    int end = nlcAuthorClean.indexOf("]");
                    if (end == -1) {
                        break;
                    }
                    String replace = nlcAuthorClean.substring(start, end + 1);
                    nlcAuthorClean = nlcAuthorClean.replace(replace, "");
                }
            } catch (Exception e) {
            }
            nlcAuthorClean = nlcAuthorClean.trim();
            if (StringUtils.isEmpty(nlcAuthorClean)) {
                nlcAuthorClean = nlcAuthor;
            }
        }
        return nlcAuthorClean;
    }

    /**
     * 清洗author中的,，;；·.《》[]...(){}（）空格 等特殊字符内容
     *
     * @param nlcAuthorClean
     * @return
     */
    private String cleanPunctuationInAuthor(String nlcAuthorClean) {
        // 替换全角空格
        nlcAuthorClean = nlcAuthorClean.replace((char) 12288, ' ');
        nlcAuthorClean = nlcAuthorClean.replaceAll("\\,", "");
        nlcAuthorClean = nlcAuthorClean.replaceAll("\\，", "");
        nlcAuthorClean = nlcAuthorClean.replaceAll("\\;", "");
        nlcAuthorClean = nlcAuthorClean.replaceAll("\\；", "");
        nlcAuthorClean = nlcAuthorClean.replaceAll("\\·", "");
        nlcAuthorClean = nlcAuthorClean.replaceAll("\\.", "");
        nlcAuthorClean = nlcAuthorClean.replaceAll("\\《", "");
        nlcAuthorClean = nlcAuthorClean.replaceAll("\\》", "");
        nlcAuthorClean = nlcAuthorClean.replaceAll("\\[", "");
        nlcAuthorClean = nlcAuthorClean.replaceAll("\\]", "");
        nlcAuthorClean = nlcAuthorClean.replaceAll("\\(", "");
        nlcAuthorClean = nlcAuthorClean.replaceAll("\\)", "");
        nlcAuthorClean = nlcAuthorClean.replaceAll("\\{", "");
        nlcAuthorClean = nlcAuthorClean.replaceAll("\\}", "");
        nlcAuthorClean = nlcAuthorClean.replaceAll("\\（", "");
        nlcAuthorClean = nlcAuthorClean.replaceAll("\\）", "");
        nlcAuthorClean = nlcAuthorClean.replaceAll(" ", "");
        return nlcAuthorClean;
    }

    /**
     * 清洗title中的,，;；·.《》[]...(){}（）中全角符号转为半角符号
     *
     * @param apabiMetaTitleClean
     * @return
     */
    private String cleanPunctuationInTitle(String apabiMetaTitleClean) {
        // 替换全角空格
        apabiMetaTitleClean = apabiMetaTitleClean.replace((char) 12288, ' ');
        apabiMetaTitleClean = apabiMetaTitleClean.replaceAll(" ", "");
        apabiMetaTitleClean = apabiMetaTitleClean.replaceAll("\\，", "");
        apabiMetaTitleClean = apabiMetaTitleClean.replaceAll("\\,", "");
        apabiMetaTitleClean = apabiMetaTitleClean.replaceAll("\\；", "");
        apabiMetaTitleClean = apabiMetaTitleClean.replaceAll("\\;", "");
        apabiMetaTitleClean = apabiMetaTitleClean.replaceAll("\\（", "");
        apabiMetaTitleClean = apabiMetaTitleClean.replaceAll("\\(", "");
        apabiMetaTitleClean = apabiMetaTitleClean.replaceAll("\\）", "");
        apabiMetaTitleClean = apabiMetaTitleClean.replaceAll("\\)", "");
        apabiMetaTitleClean = apabiMetaTitleClean.replaceAll("\\。", "");
        apabiMetaTitleClean = apabiMetaTitleClean.replaceAll("\\.", "");
        apabiMetaTitleClean = apabiMetaTitleClean.replaceAll("\\？", "");
        apabiMetaTitleClean = apabiMetaTitleClean.replaceAll("\\?", "");
        apabiMetaTitleClean = apabiMetaTitleClean.replaceAll("\\！", "");
        apabiMetaTitleClean = apabiMetaTitleClean.replaceAll("\\!", "");
        apabiMetaTitleClean = apabiMetaTitleClean.replaceAll("\\《", "");
        apabiMetaTitleClean = apabiMetaTitleClean.replaceAll("\\<", "");
        apabiMetaTitleClean = apabiMetaTitleClean.replaceAll("\\》", "");
        apabiMetaTitleClean = apabiMetaTitleClean.replaceAll("\\>", "");
        apabiMetaTitleClean = apabiMetaTitleClean.replaceAll("\\“", "");
        apabiMetaTitleClean = apabiMetaTitleClean.replaceAll("\\'", "");
        apabiMetaTitleClean = apabiMetaTitleClean.replaceAll("\\”", "");
        apabiMetaTitleClean = apabiMetaTitleClean.replaceAll("\\：", "");
        apabiMetaTitleClean = apabiMetaTitleClean.replaceAll("\\:", "");
        return apabiMetaTitleClean;
    }
}