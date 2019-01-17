package com.apabi.flow.match_data.service;

import com.apabi.flow.douban.dao.ApabiBookMetaDataDao;
import com.apabi.flow.douban.model.ApabiBookMetaData;
import com.apabi.flow.douban.util.Isbn13ToIsbnUtil;
import com.apabi.flow.match_data.dao.ApabiBookMetaNlcCheckerDao;
import com.apabi.flow.match_data.model.ApabiBookMetaNlcChecker;
import com.apabi.flow.nlcmarc.dao.NlcBookMarcDao;
import com.apabi.flow.nlcmarc.model.NlcBookMarc;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;

/**
 * @Author pipi
 * @Date 2019-1-9 11:16
 **/
@RestController
@RequestMapping("apabiNlcChecker")
public class ApabiBookMetaNlcCheckerService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ApabiBookMetaNlcCheckerService.class);
    @Autowired
    private NlcBookMarcDao nlcBookMarcDao;
    @Autowired
    private ApabiBookMetaDataDao apabiBookMetaDataDao;
    @Autowired
    private ApabiBookMetaNlcCheckerDao apabiBookMetaNlcCheckerDao;

    /**
     * 如果作者和出版社都不一样，则认为nlc_book_marc和apabi_book_meta数据不是同一本书
     * 进入把这种数据写入到apabi_book_meta_nlc_checker表中
     *
     * @return
     */
    @RequestMapping("check")
    public String check() {
        int count = apabiBookMetaDataDao.countHasNLibraryId();
        int pageSize = 10000;
        int pageNum = (count / pageSize) + 1;
        for (int i = 1; i <= pageNum; i++) {
            PageHelper.startPage(i, pageSize);
            Page<ApabiBookMetaData> apabiBookMetaDataList = apabiBookMetaDataDao.findApabiBookMetaDataWithNlibraryId();
            for (ApabiBookMetaData apabiBookMetaData : apabiBookMetaDataList) {
                try {
                    NlcBookMarc nlcBookMarc = nlcBookMarcDao.findByNlcMarcId(apabiBookMetaData.getNlibraryId());
                    if (!nlcBookMarc.getTitle().trim().equals(apabiBookMetaData.getTitle().trim()) && !nlcBookMarc.getAuthor().trim().equals(apabiBookMetaData.getCreator().trim())) {
                        ApabiBookMetaNlcChecker apabiBookMetaNlcChecker = new ApabiBookMetaNlcChecker();
                        apabiBookMetaNlcChecker.setNlibraryId(nlcBookMarc.getNlcMarcId().trim());
                        apabiBookMetaNlcChecker.setMetaId(apabiBookMetaData.getMetaId().trim());
                        apabiBookMetaNlcChecker.setNlcTitle(nlcBookMarc.getTitle().trim());
                        apabiBookMetaNlcChecker.setApabiMetaTitle(apabiBookMetaData.getTitle().trim());
                        apabiBookMetaNlcChecker.setNlcAuthor(nlcBookMarc.getAuthor().trim());
                        apabiBookMetaNlcChecker.setApabiMetaAuthor(apabiBookMetaData.getCreator().trim());
                        apabiBookMetaNlcChecker.setNlcPublisher(nlcBookMarc.getPublisher().trim());
                        apabiBookMetaNlcChecker.setApabiMetaPublisher(apabiBookMetaData.getPublisher().trim());
                        apabiBookMetaNlcCheckerDao.insert(apabiBookMetaNlcChecker);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "success";
    }

    /**
     * 根据isbn13查询，重新更新apabi_book_metadata中nlibraryId更新失败的数据
     *
     * @return
     * @throws IOException
     */
    @RequestMapping("checkIsbn13")
    @ResponseBody
    public String checkIsbn13() {
        List<String> isbn13List = apabiBookMetaDataDao.findIsbn13WithoutNlibraryId();
        for (String isbn13 : isbn13List) {
            if (StringUtils.isNotEmpty(isbn13)) {
                String isbn = Isbn13ToIsbnUtil.transform(isbn13);
                List<NlcBookMarc> nlcBookMarcList = nlcBookMarcDao.findByIsbn(isbn);
                List<ApabiBookMetaData> apabiBookMetaDataList = apabiBookMetaDataDao.findByIsbn13(isbn13);
                if (nlcBookMarcList != null && nlcBookMarcList.size() == 1 && apabiBookMetaDataList.size() == 1) {
                    try {
                        String nlcMarcId = nlcBookMarcList.get(0).getNlcMarcId();
                        ApabiBookMetaData apabiBookMetaData = apabiBookMetaDataList.get(0);
                        apabiBookMetaData.setNlibraryId(nlcMarcId);
                        apabiBookMetaDataDao.update(apabiBookMetaData);
                        System.out.println(apabiBookMetaData.getIsbn13());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return "success";
    }

    /**
     * 清洗apabi_book_meta_nlc_checker表中的apabi_book_meta_title字段与nlc_title进行比对算法
     *
     * @return
     */
    @RequestMapping("cleanTitleAndAuthor")
    @ResponseBody
    public String cleanTitleAndAuthor() {
        int count = apabiBookMetaNlcCheckerDao.count();
        int pageSize = 10000;
        int pageNum = (count / pageSize) + 1;
        for (int i = 1; i <= pageNum; i++) {
            PageHelper.startPage(i, pageSize);
            Page<ApabiBookMetaNlcChecker> apabiBookMetaNlcCheckerList = apabiBookMetaNlcCheckerDao.findByPage();
            for (ApabiBookMetaNlcChecker apabiBookMetaNlcChecker : apabiBookMetaNlcCheckerList) {
                String apabiTitle = apabiBookMetaNlcChecker.getApabiMetaTitle();
                String apabiTitleClean = cleanTitle(apabiTitle);
                apabiBookMetaNlcChecker.setApabiMetaTitleClean(apabiTitleClean);
                apabiBookMetaNlcCheckerDao.update(apabiBookMetaNlcChecker);
                String nlcAuthor = apabiBookMetaNlcChecker.getNlcAuthor();
                String nlcAuthorClean = cleanAuthor(nlcAuthor);
                apabiBookMetaNlcChecker.setNlcAuthorClean(nlcAuthorClean);
                apabiBookMetaNlcCheckerDao.update(apabiBookMetaNlcChecker);
            }
        }
        return "success";
    }

    /**
     * 根据apabi_meta_title清洗出apabi_meta_title_clean
     *
     * @param apabiTitle
     * @return
     */
    private String cleanTitle(String apabiTitle) {
        String apabiTitleClean = "";
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
        return apabiTitleClean;
    }

    /**
     * 根据nlc_author清洗出nlc_author_clean
     *
     * @param nlcAuthor
     * @return
     */
    private String cleanAuthor(String nlcAuthor) {
        String nlcAuthorClean = nlcAuthor;
        if (nlcAuthor.contains("编委会编")) {
            nlcAuthorClean = nlcAuthor.replaceAll("编委会编", "编委会").trim();
        }
        if (nlcAuthorClean.contains("编委会编著")) {
            nlcAuthorClean = nlcAuthorClean.replaceAll("编委会编著", "编委会").trim();
        }
        if (nlcAuthorClean.contains("办公室编著")) {
            nlcAuthorClean = nlcAuthorClean.replaceAll("办公室编著", "办公室").trim();
        }
        if (nlcAuthorClean.contains("办公室编")) {
            nlcAuthorClean = nlcAuthorClean.replaceAll("办公室编", "办公室").trim();
        }
        if (nlcAuthorClean.contains("编辑部编著")) {
            nlcAuthorClean = nlcAuthorClean.replaceAll("编辑部编著", "编辑部").trim();
        }
        if (nlcAuthorClean.contains("编辑部编")) {
            nlcAuthorClean = nlcAuthorClean.replaceAll("编辑部编", "编辑部").trim();
        }
        if (nlcAuthorClean.contains("研究会编著")) {
            nlcAuthorClean = nlcAuthorClean.replaceAll("研究会编著", "研究会").trim();
        }
        if (nlcAuthorClean.contains("改编/演奏")) {
            nlcAuthorClean = nlcAuthorClean.replaceAll("改编/演奏", "").trim();
        }
        if (nlcAuthorClean.contains("研究会编")) {
            nlcAuthorClean = nlcAuthorClean.replaceAll("研究会编", "研究会").trim();
        }
        if (nlcAuthorClean.contains("联合编纂")) {
            nlcAuthorClean = nlcAuthorClean.replaceAll("联合编纂", "研究会").trim();
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
        if (nlcAuthorClean.contains("[等编著]")) {
            nlcAuthorClean = nlcAuthorClean.replaceAll("\\[等编著\\]", "").trim();
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
        if (nlcAuthorClean.contains("()")) {
            nlcAuthorClean = nlcAuthorClean.replaceAll("\\(\\)", "").trim();
        }
        if (StringUtils.isEmpty(nlcAuthorClean)) {
            nlcAuthorClean = nlcAuthor;
        }
        return nlcAuthorClean;
    }

    /*public static void main(String[] args) {
        ApabiBookMetaNlcCheckerService apabiBookMetaNlcCheckerService = new ApabiBookMetaNlcCheckerService();
        String sj = apabiBookMetaNlcCheckerService.cleanAuthor("李鉴踪选编");
        System.out.println(sj);
    }*/

}