package com.apabi.flow.match_data.service;

import com.apabi.flow.douban.dao.ApabiBookMetaDataDao;
import com.apabi.flow.douban.model.ApabiBookMetaData;
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

/**
 * apabi_book_meta_nlc_checker数据清洗逻辑：
 * <p>
 * 国图与apabi_book_metadata数据清洗流程：
 * 1) 一轮：当 apabi_book_metadata.creator != nlc_book_marc.author && apabi_book_metadata.title != nlc_book_marc.title 时，认为未匹配，
 * 将未匹配的数据写入到apabi_book_meta_nlc_checker表中。
 * 2) 二轮：对nlc_author和apabi_meta_title字段进行清洗，分别生成nlc_author_clean和apabi_meta_title_clean字段的值，
 * 当nlc_author_clean == apabi_meta_author and nlc_title == apabi_meta_title_clean 匹配成功时，删除数据
 * 3) 三轮：将nlc_author_clean和apabi_meta_author中的特殊符号全部替换，并取nlc_author_clean和apabi_meta_author中较长的数据进行contains匹配，
 * 当匹配成功且apabi_meta_title_clean == nlc_title时，删除数据(注意：由于分页原因，需要删除多次)
 * 4) 四轮：apabi_meta_title_clean和nlc_title中的全角字符转换为半角字符，并取apabi_meta_title_clean和nlc_title中较长的数据进行contains匹配，
 * 当匹配成功且nlc_author_clean和apabi_meta_author中较长的数据contains匹配成功时，删除数据(注意：由于分页原因，需要删除多次)
 *
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
     * 第一轮入库：
     * 对apabi_book_meta_title字段与nlc_author字段进行清洗生成apabi_book_meta_title_clean字段和nlc_author_clean字段的值
     * 把数据写入到apabi_book_meta_nlc_checker表中
     *
     * @return
     */
    @RequestMapping("firstCheck")
    public String firstCheck() {
        int count = apabiBookMetaDataDao.countHasNLibraryId();
        int processCount = 0;
        int pageSize = 10000;
        int pageNum = (count / pageSize) + 1;
        for (int i = 82; i <= pageNum; i++) {
            PageHelper.startPage(i, pageSize);
            Page<ApabiBookMetaData> apabiBookMetaDataList = apabiBookMetaDataDao.findApabiBookMetaDataWithNlibraryId();
            for (ApabiBookMetaData apabiBookMetaData : apabiBookMetaDataList) {
                processCount++;
                System.out.println("共" + count + "个，正在处理第" + (i * pageSize + processCount) + "个");
                try {
                    NlcBookMarc nlcBookMarc = nlcBookMarcDao.findByNlcMarcId(apabiBookMetaData.getNlibraryId());
                    String nlcBookMarcTitle = nlcBookMarc.getTitle();
                    String apabiBookMetaDataTitle = apabiBookMetaData.getTitle();
                    String nlcBookMarcAuthor = nlcBookMarc.getAuthor();
                    String apabiBookMetaDataCreator = apabiBookMetaData.getCreator();
                    if (nlcBookMarcTitle != null) {
                        nlcBookMarcTitle = nlcBookMarcTitle.trim();
                    }
                    if (apabiBookMetaDataTitle != null) {
                        apabiBookMetaDataTitle = apabiBookMetaDataTitle.trim();
                    }
                    if (nlcBookMarcAuthor != null) {
                        nlcBookMarcAuthor = nlcBookMarcAuthor.trim();
                    }
                    if (apabiBookMetaDataCreator != null) {
                        apabiBookMetaDataCreator = apabiBookMetaDataCreator.trim();
                    }

                    if (StringUtils.isEmpty(nlcBookMarcTitle) || StringUtils.isEmpty(nlcBookMarcAuthor) || (!nlcBookMarcTitle.equals(apabiBookMetaDataTitle) && !nlcBookMarcAuthor.equals(apabiBookMetaDataCreator))) {
                        ApabiBookMetaNlcChecker apabiBookMetaNlcChecker = new ApabiBookMetaNlcChecker();
                        // metaId和nLibraryId肯定不为null
                        apabiBookMetaNlcChecker.setNlibraryId(nlcBookMarc.getNlcMarcId().trim());
                        apabiBookMetaNlcChecker.setMetaId(apabiBookMetaData.getMetaId().trim());
                        // 处理可能为null的字段
                        apabiBookMetaNlcChecker.setNlcTitle(nlcBookMarcTitle == null ? null : nlcBookMarcTitle.trim());
                        apabiBookMetaNlcChecker.setApabiMetaTitle(apabiBookMetaDataTitle == null ? null : apabiBookMetaDataTitle.trim());
                        apabiBookMetaNlcChecker.setNlcAuthor(nlcBookMarcAuthor == null ? null : nlcBookMarcAuthor.trim());
                        apabiBookMetaNlcChecker.setApabiMetaAuthor(apabiBookMetaDataCreator == null ? null : apabiBookMetaDataCreator.trim());
                        apabiBookMetaNlcChecker.setNlcPublisher(nlcBookMarc.getPublisher() == null ? null : nlcBookMarc.getPublisher().trim());
                        apabiBookMetaNlcChecker.setApabiMetaPublisher(apabiBookMetaData.getPublisher() == null ? null : apabiBookMetaData.getPublisher().trim());
                        // 清洗apabi_book_meta_title和nlc_author数据
                        String apabiTitleClean = cleanTitle(apabiBookMetaDataTitle);
                        apabiTitleClean = apabiTitleClean == null ? null : apabiTitleClean.trim();
                        apabiBookMetaNlcChecker.setApabiMetaTitleClean(apabiTitleClean);
                        String nlcAuthorClean = cleanAuthor(nlcBookMarcAuthor);
                        nlcAuthorClean = nlcAuthorClean == null ? null : nlcAuthorClean.trim();
                        apabiBookMetaNlcChecker.setNlcAuthorClean(nlcAuthorClean);
                        // TODO 插入数据
                        try {
                            apabiBookMetaNlcCheckerDao.insert(apabiBookMetaNlcChecker);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "第一轮数据入库完毕...";
    }

    /**
     * 第二轮清洗：
     * 删除符合以下条件的数据：
     * nlc_author_clean = apabi_book_meta_author and nlc_title = apabi_book_meta_title_clean
     *
     * @return
     */
    @RequestMapping("secondCheck")
    @ResponseBody
    public String secondCheck() {
        int count = apabiBookMetaNlcCheckerDao.deleteHasSameAuthorAndTitle();
        return "第二轮清洗数据共删除了" + count + "条数据...";
    }

    /**
     * 第三轮清洗：
     * 在内存中将nlc_author_clean和apabi_meta_author中的特殊符号全部替换，
     * 并取nlc_author_clean和apabi_meta_author中较长的数据进行contains匹配，
     * 当匹配成功且apabi_meta_title_clean == nlc_title时，删除数据
     * (注意：由于分页原因，需要删除多次)
     *
     * @return
     */
    @RequestMapping("thirdCheck")
    public String thirdCheck() {
        // 删除的轮数
        int roundCount = 0;
        // 删除的总数量
        int totalCount = 0;
        while (true) {
            // 轮数+1
            roundCount++;
            int count = apabiBookMetaNlcCheckerDao.count();
            int pageSize = 10000;
            int pageNum = (count / pageSize) + 1;
            // 本轮删除的数据量
            int hitCount = 0;
            for (int i = 1; i <= pageNum; i++) {
                PageHelper.startPage(i, pageSize);
                Page<ApabiBookMetaNlcChecker> apabiBookMetaNlcCheckerList = apabiBookMetaNlcCheckerDao.findByPage();
                for (ApabiBookMetaNlcChecker apabiBookMetaNlcChecker : apabiBookMetaNlcCheckerList) {
                    if (checkTitleAndContainsAuthor(apabiBookMetaNlcChecker)) {
                        try {
                            hitCount++;
                            apabiBookMetaNlcCheckerDao.delete(apabiBookMetaNlcChecker.getNlibraryId());
                        } catch (Exception e) {
                        }
                    }
                }
            }
            // 统计总数
            totalCount += hitCount;
            // 如果该轮数据删除了0条，代表全部删除完了
            if (hitCount == 0) {
                break;
            }
            // 防止死循环，如果超过20轮删除，则跳出循环
            if (roundCount > 20) {
                break;
            }
        }
        return "第三轮清洗数据共删除了" + roundCount + "轮，共计" + totalCount + "条数据...";
    }

    /**
     * 第四轮清洗：
     * apabi_meta_title_clean和nlc_title中的特殊符号全部替换，并取apabi_meta_title_clean和nlc_title
     * 中较长的数据进行contains匹配，当匹配成功且nlc_author_clean和apabi_meta_author中较长的数据contains匹配成功时，删除数据
     * (注意：由于分页原因，需要删除多次)
     *
     * @return
     */
    @RequestMapping("fourthCheck")
    public String fourthCheck() {
        int totalCount = 0;
        int roundCount = 0;
        while (true) {
            roundCount++;
            int count = apabiBookMetaNlcCheckerDao.count();
            int pageSize = 10000;
            int pageNum = (count / pageSize) + 1;
            int hitCount = 0;
            for (int i = 1; i <= pageNum; i++) {
                PageHelper.startPage(i, pageSize);
                Page<ApabiBookMetaNlcChecker> apabiBookMetaNlcCheckerList = apabiBookMetaNlcCheckerDao.findByPage();
                for (ApabiBookMetaNlcChecker apabiBookMetaNlcChecker : apabiBookMetaNlcCheckerList) {
                    if (containsTitleAndContainsAuthor(apabiBookMetaNlcChecker)) {
                        try {
                            hitCount++;
                            apabiBookMetaNlcCheckerDao.delete(apabiBookMetaNlcChecker.getNlibraryId());
                        } catch (Exception e) {
                        }
                    }
                }
            }
            // 统计总数
            totalCount += hitCount;
            // 如果该轮删除了0条，代表全部删完了
            if (hitCount == 0) {
                break;
            }
            // 防止死循环，如果超过20轮删除，则跳出循环
            if (roundCount > 20) {
                break;
            }
        }
        return "第四轮清洗数据共删除了"+roundCount+"轮，共计" + totalCount + "条数据...";
    }

    /**
     * 第五轮清洗：
     * 对于经过四轮清洗之后在apabi_book_meta_nlc_checker表中未匹配apabi_book_metadata的数据设置nlibraryid的字段为NULL值。
     *
     * @return
     */
    @RequestMapping("fifthCheck")
    public String fifthCheck() {
        int hitCount = 0;
        int count = apabiBookMetaNlcCheckerDao.count();
        int pageSize = 10000;
        int pageNum = (count / pageSize) + 1;
        for (int i = 1; i <= pageNum; i++) {
            PageHelper.startPage(i, pageSize);
            Page<ApabiBookMetaNlcChecker> apabiBookMetaNlcCheckerList = apabiBookMetaNlcCheckerDao.findByPage();
            for (ApabiBookMetaNlcChecker apabiBookMetaNlcChecker : apabiBookMetaNlcCheckerList) {
                String metaId = apabiBookMetaNlcChecker.getMetaId();
                ApabiBookMetaData apabiBookMetaData = apabiBookMetaDataDao.findById(metaId);
                if (apabiBookMetaData != null) {
                    apabiBookMetaData.setNlibraryId(null);
                    apabiBookMetaDataDao.update(apabiBookMetaData);
                    hitCount++;
                }
            }
        }
        return "第五轮清洗更新了apabi_book_metadata表中nlibraryid的数据" + hitCount + "条...";
    }

    /**
     * 根据apabi_meta_title清洗出apabi_meta_title_clean算法实现
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
     * 根据nlc_author清洗出nlc_author_clean算法实现
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
     * 三轮清洗算法
     * 根据 apabi_book_meta_title_clean = nlc_title 和 nlc_author_clean.contains(apabi_meta_author) 或者 apabi_meta_author.contains(nlc_author_clean)
     *
     * @param apabiBookMetaNlcChecker
     * @return
     */
    private boolean checkTitleAndContainsAuthor(ApabiBookMetaNlcChecker apabiBookMetaNlcChecker) {
        boolean flag = false;
        try {
            String apabiBookMetaTitleClean = apabiBookMetaNlcChecker.getApabiMetaTitleClean().trim();
            String nlcTitle = apabiBookMetaNlcChecker.getNlcTitle().trim();
            String nlcAuthorClean = apabiBookMetaNlcChecker.getNlcAuthorClean();
            String apabiMetaAuthor = apabiBookMetaNlcChecker.getApabiMetaAuthor();
            // 清洗title中的全角符改为半角符
            apabiBookMetaTitleClean = cleanPunctuationInTitle(apabiBookMetaTitleClean);
            nlcTitle = cleanPunctuationInTitle(nlcTitle);
            // 清洗author中的特殊符号
            nlcAuthorClean = cleanPunctuationInAuthor(nlcAuthorClean);
            apabiMetaAuthor = cleanPunctuationInAuthor(apabiMetaAuthor);
            // 取长度较长的author
            String shortAuthor = nlcAuthorClean.length() > apabiMetaAuthor.length() ? apabiMetaAuthor : nlcAuthorClean;
            String longAuthor = nlcAuthorClean.length() < apabiMetaAuthor.length() ? apabiMetaAuthor : nlcAuthorClean;
            // 第三轮判断
            if (apabiBookMetaTitleClean.trim().equals(nlcTitle.trim()) && longAuthor.contains(shortAuthor)) {
                flag = true;
            }
        } catch (Exception e) {
        }
        return flag;
    }

    /**
     * 四轮清洗算法
     * 根据longTitle.contains(shortTitle) && longAuthor.contains(shortAuthor)
     *
     * @param apabiBookMetaNlcChecker
     * @return
     */
    private boolean containsTitleAndContainsAuthor(ApabiBookMetaNlcChecker apabiBookMetaNlcChecker) {
        boolean flag = false;
        try {
            String apabiBookMetaTitleClean = apabiBookMetaNlcChecker.getApabiMetaTitleClean().trim();
            String nlcTitle = apabiBookMetaNlcChecker.getNlcTitle().trim();
            String nlcAuthorClean = apabiBookMetaNlcChecker.getNlcAuthorClean();
            String apabiMetaAuthor = apabiBookMetaNlcChecker.getApabiMetaAuthor();
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