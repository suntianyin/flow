package com.apabi.flow.match_data.service;

import com.apabi.flow.douban.dao.ApabiBookMetaDataDao;
import com.apabi.flow.douban.model.ApabiBookMetaData;
import com.apabi.flow.nlcmarc.dao.ApabiBookMetadataAuthorDao;
import com.apabi.flow.nlcmarc.dao.ApabiBookMetadataTitleDao;
import com.apabi.flow.nlcmarc.dao.NlcBookMarcDao;
import com.apabi.flow.nlcmarc.model.ApabiBookMetadataAuthor;
import com.apabi.flow.nlcmarc.model.ApabiBookMetadataTitle;
import com.apabi.flow.nlcmarc.model.NlcBookMarc;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

/**
 * @Author pipi
 * @Date 2019-1-22 16:30
 **/
@RestController
@RequestMapping("apabiNlcCleaner")
public class ApabiBookMetaNlcCleanerService {
    private static final String TRANSLATOR_KEY_WORD = "译";
    @Autowired
    private ApabiBookMetaDataDao apabiBookMetaDataDao;
    @Autowired
    private ApabiBookMetadataTitleDao apabiBookMetadataTitleDao;
    @Autowired
    private ApabiBookMetadataAuthorDao apabiBookMetadataAuthorDao;
    @Autowired
    private NlcBookMarcDao nlcBookMarcDao;

    @RequestMapping("updateApabiBookMetaData")
    public void updateApabiBookMetaData() {
        int count = apabiBookMetaDataDao.countHasNLibraryIdAndShouldClean();
        int pageSize = 10000;
        int pageNum = (count / pageSize) + 1;
        for (int i = 1; i <= pageNum; i++) {
            PageHelper.startPage(i, pageSize);
            Page<ApabiBookMetaData> apabiBookMetaDataList = apabiBookMetaDataDao.findHasNLibraryIdAndShouldClean();
            for (ApabiBookMetaData apabiBookMetaData : apabiBookMetaDataList) {
                String nlibraryId = apabiBookMetaData.getNlibraryId();
                List<ApabiBookMetadataTitle> apabiBookMetadataTitleList = apabiBookMetadataTitleDao.findByNlcMarcIdentifier(nlibraryId);
                System.out.println(apabiBookMetadataTitleList);
                List<ApabiBookMetadataAuthor> apabiBookMetadataAuthorList = apabiBookMetadataAuthorDao.findByNlcMarcIdentifierOrderByPriority(nlibraryId);
                NlcBookMarc nlcBookMarc = nlcBookMarcDao.findByNlcMarcId(nlibraryId);
                // 利用国图的数据更新apabiBookMetaData的数据
                apabiBookMetaData = updateApabiBookMetadataByNlc(apabiBookMetaData, apabiBookMetadataTitleList, apabiBookMetadataAuthorList, nlcBookMarc);
                apabiBookMetaData.setUpdateTime(new Date());
                apabiBookMetaDataDao.updateHasCleaned("1");
                apabiBookMetaDataDao.update(apabiBookMetaData);
            }
        }
    }

    /**
     * 根据国图的数据更新meta表中对应的数据
     *
     * @param apabiBookMetaData
     * @param apabiBookMetadataTitleList
     * @param apabiBookMetadataAuthorList
     * @return
     */
    private ApabiBookMetaData updateApabiBookMetadataByNlc(ApabiBookMetaData apabiBookMetaData, List<ApabiBookMetadataTitle> apabiBookMetadataTitleList, List<ApabiBookMetadataAuthor> apabiBookMetadataAuthorList, NlcBookMarc nlcBookMarc) {
        if (apabiBookMetaData != null && nlcBookMarc != null) {
            ApabiBookMetadataTitle apabiBookMetadataTitle = null;
            if (apabiBookMetadataTitleList != null && apabiBookMetadataTitleList.size() > 0) {
                apabiBookMetadataTitle = apabiBookMetadataTitleList.get(0);
                if (apabiBookMetadataAuthorList != null && apabiBookMetadataAuthorList.size() > 0) {
                    // 标题信息
                    String title = apabiBookMetadataTitle.getTitle();
                    String subTitle = apabiBookMetadataTitle.getSubTitle();
                    String originTitle = apabiBookMetadataTitle.getParallelTitle();
                    String alternativeTitle = apabiBookMetadataTitle.getOtherVariantTitle();
                    if (StringUtils.isNotEmpty(title)) {
                        apabiBookMetaData.setTitle(title);
                    }
                    if (StringUtils.isNotEmpty(subTitle)) {
                        apabiBookMetaData.setSubTitle(subTitle);
                    }
                    if (StringUtils.isNotEmpty(originTitle)) {
                        apabiBookMetaData.setOriginTitle(originTitle);
                    }
                    if (StringUtils.isNotEmpty(alternativeTitle)) {
                        apabiBookMetaData.setAlternativeTitle(alternativeTitle);
                    }
                    // 作者信息
                    // TODO 对于作者的处理需要好好 DEBUG 一下逻辑
                    // 清洗authorType字段
                    apabiBookMetadataAuthorList = cleanAuthorType(apabiBookMetadataAuthorList);
                    // 如果有1个作者，直接设置为主要责任者
                    if (apabiBookMetadataAuthorList.size() == 1) {
                        ApabiBookMetadataAuthor apabiBookMetadataAuthor = apabiBookMetadataAuthorList.get(0);
                        String creator = apabiBookMetadataAuthor.getName();
                        String creatorWord = apabiBookMetadataAuthor.getAuthorType();
                        String contributor = null;
                        String contributorWord = null;
                        String translator = null;
                        if (StringUtils.isNotEmpty(creatorWord) && creatorWord.contains(TRANSLATOR_KEY_WORD)) {
                            translator = creator;
                        }
                        if (StringUtils.isNotEmpty(creator)) {
                            apabiBookMetaData.setCreator(creator);
                        }
                        if (StringUtils.isNotEmpty(creatorWord)) {
                            apabiBookMetaData.setCreatorWord(creatorWord);
                        }
                        if (StringUtils.isNotEmpty(contributor)) {
                            apabiBookMetaData.setContributor(contributor);
                        }
                        if (StringUtils.isNotEmpty(contributorWord)) {
                            apabiBookMetaData.setContributorWord(contributorWord);
                        }
                        if (StringUtils.isNotEmpty(translator)) {
                            apabiBookMetaData.setTranslator(translator);
                        }
                    }
                    // 如果有2个作者
                    else if (apabiBookMetadataAuthorList.size() == 2) {
                        ApabiBookMetadataAuthor apabiBookMetadataAuthor1 = apabiBookMetadataAuthorList.get(0);
                        ApabiBookMetadataAuthor apabiBookMetadataAuthor2 = apabiBookMetadataAuthorList.get(1);
                        String creator = null;
                        String creatorWord = null;
                        String contributor = null;
                        String contributorWord = null;
                        String translator = null;
                        // 如果第一个作者为译者，设置为译者；第二个作者为主要责任者
                        if (StringUtils.isNotEmpty(apabiBookMetadataAuthor1.getAuthorType()) && apabiBookMetadataAuthor1.getAuthorType().contains(TRANSLATOR_KEY_WORD)) {
                            translator = apabiBookMetadataAuthor1.getName();
                            creator = apabiBookMetadataAuthor2.getName();
                            creatorWord = apabiBookMetadataAuthor2.getAuthorType();
                        }
                        // 如果第一个作者不为译者
                        else if (StringUtils.isNotEmpty(apabiBookMetadataAuthor1.getAuthorType()) && !apabiBookMetadataAuthor1.getAuthorType().contains(TRANSLATOR_KEY_WORD)) {
                            // 第一个作者为主要责任者
                            creator = apabiBookMetadataAuthor1.getName();
                            creatorWord = apabiBookMetadataAuthor1.getAuthorType();
                            // 如果第二个作者为译者，则设置为译者
                            if (StringUtils.isNotEmpty(apabiBookMetadataAuthor2.getAuthorType()) && apabiBookMetadataAuthor2.getAuthorType().contains(TRANSLATOR_KEY_WORD)) {
                                translator = apabiBookMetadataAuthor2.getName();
                            }
                            // 如果第二个作者不为译者，则为次要责任者
                            else if (StringUtils.isNotEmpty(apabiBookMetadataAuthor2.getAuthorType()) && !apabiBookMetadataAuthor2.getAuthorType().contains(TRANSLATOR_KEY_WORD)) {
                                contributor = apabiBookMetadataAuthor2.getName();
                                contributorWord = apabiBookMetadataAuthor2.getAuthorType();
                            }
                        }
                        if (StringUtils.isNotEmpty(creator)) {
                            apabiBookMetaData.setCreator(creator);
                        }
                        if (StringUtils.isNotEmpty(creatorWord)) {
                            apabiBookMetaData.setCreatorWord(creatorWord);
                        }
                        if (StringUtils.isNotEmpty(contributor)) {
                            apabiBookMetaData.setContributor(contributor);
                        }
                        if (StringUtils.isNotEmpty(contributorWord)) {
                            apabiBookMetaData.setContributorWord(contributorWord);
                        }
                        if (StringUtils.isNotEmpty(translator)) {
                            apabiBookMetaData.setTranslator(translator);
                        }
                    }
                    // 如果有2个以上的作者
                    else {
                        String translator = null;
                        String creator = null;
                        String creatorWord = null;
                        String contributor = null;
                        String contributorWord = null;
                        // 设置译者
                        for (int i = 0; i < apabiBookMetadataAuthorList.size(); i++) {
                            if (apabiBookMetadataAuthorList.get(i).getAuthorType() != null && apabiBookMetadataAuthorList.get(i).getAuthorType().contains(TRANSLATOR_KEY_WORD)) {
                                translator = apabiBookMetadataAuthorList.get(i).getName();
                                break;
                            }
                        }
                        // 设置主要责任者
                        for (int i = 0; i < apabiBookMetadataAuthorList.size(); i++) {
                            if (apabiBookMetadataAuthorList.get(i).getAuthorType() != null && !apabiBookMetadataAuthorList.get(i).getAuthorType().contains(TRANSLATOR_KEY_WORD)) {
                                creator = apabiBookMetadataAuthorList.get(i).getName();
                                creatorWord = apabiBookMetadataAuthorList.get(i).getAuthorType();
                                break;
                            }
                        }
                        // 设置次要责任者
                        for (int i = 0; i < apabiBookMetadataAuthorList.size(); i++) {
                            if (apabiBookMetadataAuthorList.get(i).getAuthorType() != null && !apabiBookMetadataAuthorList.get(i).getAuthorType().contains(TRANSLATOR_KEY_WORD) && !apabiBookMetadataAuthorList.get(i).getName().equals(creator)) {
                                contributor = apabiBookMetadataAuthorList.get(i).getName();
                                contributorWord = apabiBookMetadataAuthorList.get(i).getAuthorType();
                                break;
                            }
                        }
                        if (StringUtils.isNotEmpty(creator)) {
                            apabiBookMetaData.setCreator(creator);
                        }
                        if (StringUtils.isNotEmpty(creatorWord)) {
                            apabiBookMetaData.setCreatorWord(creatorWord);
                        }
                        if (StringUtils.isNotEmpty(contributor)) {
                            apabiBookMetaData.setContributor(contributor);
                        }
                        if (StringUtils.isNotEmpty(contributorWord)) {
                            apabiBookMetaData.setContributorWord(contributorWord);
                        }
                        if (StringUtils.isNotEmpty(translator)) {
                            apabiBookMetaData.setTranslator(translator);
                        }
                    }
                    // nlcMarc信息
                    String reader = mappingNlcClassToReader(nlcBookMarc.getClass_());
                    String isbn = nlcBookMarc.getIsbn();
                    String isbn13 = null;
                    String isbn10 = null;
                    if (isbn != null && isbn.replaceAll("-", "").length() == 13) {
                        isbn13 = isbn.replaceAll("-", "");
                    } else if (isbn != null && isbn.replaceAll("-", "").length() == 10) {
                        isbn10 = isbn.replaceAll("-", "");
                    }
                    String relation = nlcBookMarc.getRelation();
                    String volume = null;
                    if (StringUtils.isNotEmpty(relation)) {
                        volume = nlcBookMarc.getVolume();
                    }
                    int isSeries = 0;
                    if (StringUtils.isEmpty(relation)) {
                        isSeries = 0;
                    } else {
                        if (relation.contains("套书")) {
                            isSeries = 1;
                        } else {
                            isSeries = 2;
                        }
                    }
                    if (StringUtils.isNotEmpty(reader)) {
                        apabiBookMetaData.setReader(reader);
                    }
                    if (StringUtils.isNotEmpty(isbn)) {
                        apabiBookMetaData.setIsbn(isbn);
                    }
                    if (StringUtils.isNotEmpty(isbn10)) {
                        apabiBookMetaData.setIsbn10(isbn10);
                    }
                    if (StringUtils.isNotEmpty(isbn13)) {
                        apabiBookMetaData.setIsbn13(isbn13);
                    }
                    if (StringUtils.isNotEmpty(relation)) {
                        apabiBookMetaData.setRelation(relation);
                    }
                    if (StringUtils.isNotEmpty(volume)) {
                        apabiBookMetaData.setVolume(volume);
                    }
                    apabiBookMetaData.setIsSeries(isSeries);
                }
            }
            return apabiBookMetaData;
        } else {
            return null;
        }
    }

    /**
     * 将nlcMarc数据中的class_映射为中文读者类型
     *
     * @param class_
     * @return
     */
    private String mappingNlcClassToReader(String class_) {
        String readerStr = null;
        if (StringUtils.isNotEmpty(class_)) {
            // 去除class字段中不合法字符
            String lowerCase = class_.toLowerCase();
            char[] chars = lowerCase.toCharArray();
            Set<Character> characterSet = new HashSet<>();
            for (char aChar : chars) {
                if (aChar == 'a' || aChar == 'b' || aChar == 'c' || aChar == 'd' || aChar == 'e' || aChar == 'k' || aChar == 'm' || aChar == 'u') {
                    characterSet.add(aChar);
                }
            }
            StringBuilder reader = new StringBuilder("");
            for (Character character : characterSet) {
                if (character == 'a') {
                    reader.append("普通青少年,");
                }
                if (character == 'b') {
                    reader.append("学龄前儿童,");
                }
                if (character == 'c') {
                    reader.append("小学生,");
                }
                if (character == 'd') {
                    reader.append("少年,");
                }
                if (character == 'e') {
                    reader.append("青年,");
                }
                if (character == 'k') {
                    reader.append("科研人员,");
                }
                if (character == 'm') {
                    reader.append("普通成人,");
                }
                if (character == 'u') {
                    reader.append("不详");
                }
            }
            readerStr = reader.toString();
            if (StringUtils.isNotEmpty(readerStr)) {
                readerStr = readerStr.substring(0, readerStr.length() - 1);
            } else {
                readerStr = null;
            }
        }
        return readerStr;
    }

    /**
     * 清洗authorType字段
     *
     * @param in
     * @return
     */
    private List<ApabiBookMetadataAuthor> cleanAuthorType(List<ApabiBookMetadataAuthor> in) {
        List<ApabiBookMetadataAuthor> out = null;
        if (in != null) {
            out = new ArrayList<>();
            for (ApabiBookMetadataAuthor apabiBookMetadataAuthor : in) {
                String authorType = apabiBookMetadataAuthor.getAuthorType();
                String result = null;
                if (StringUtils.isNotEmpty(authorType)) {
                    result = authorType.trim().replaceAll("'", "");
                }
                apabiBookMetadataAuthor.setAuthorType(result);
                out.add(apabiBookMetadataAuthor);
            }
        }
        return out;
    }
}