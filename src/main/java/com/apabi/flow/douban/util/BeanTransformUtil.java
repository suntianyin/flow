package com.apabi.flow.douban.util;/**
 * Created by pipi on 2018/8/8.
 */

import com.apabi.flow.douban.model.AmazonMeta;
import com.apabi.flow.douban.model.ApabiBookMeta;
import com.apabi.flow.douban.model.ApabiBookMetaTemp;
import com.apabi.flow.douban.model.DoubanMeta;
import org.apache.commons.lang.StringUtils;

import java.util.Date;

/**
 * @author pipi
 * @date 2018/8/8 17:21
 * @description
 */
public class BeanTransformUtil {

    public static ApabiBookMeta transform2ApabiBookMeta(DoubanMeta doubanMeta) {
        ApabiBookMeta apabiBookMeta = new ApabiBookMeta();
        apabiBookMeta.setDoubanId(doubanMeta.getDoubanId());
        apabiBookMeta.setIsbn13(doubanMeta.getIsbn13());
        apabiBookMeta.setIsbn10(doubanMeta.getIsbn10());
        apabiBookMeta.setTitle(doubanMeta.getTitle());
        apabiBookMeta.setCreator(doubanMeta.getAuthor());
        apabiBookMeta.setPublisher(doubanMeta.getPublisher());
        apabiBookMeta.setAlternativeTitle(doubanMeta.getAltTitle());
        apabiBookMeta.setSubTitle(doubanMeta.getSubtitle());
        apabiBookMeta.setOriginTitle(doubanMeta.getOriginTitle());
        apabiBookMeta.setTranslator(doubanMeta.getTranslator());
        String issuedDate = StringToolUtil.issuedDateFormat(doubanMeta.getIssueddate());
        if (issuedDate != null && issuedDate.contains(" 00:00:00")) {
            issuedDate = issuedDate.replaceAll(" 00:00:00", "");
        }
        apabiBookMeta.setIssueddate(issuedDate);
        apabiBookMeta.setBookPages(doubanMeta.getPages());
        apabiBookMeta.setPaperPrice(doubanMeta.getPrice());
        apabiBookMeta.setEbookPrice(doubanMeta.getEbookPrice());
        apabiBookMeta.setBinding(doubanMeta.getBinding());
        apabiBookMeta.setRelation(doubanMeta.getSeries());
        apabiBookMeta.setAbstract_(doubanMeta.getSummary());
        apabiBookMeta.setAuthorIntro(doubanMeta.getAuthorIntro());
        apabiBookMeta.setFoamatCatalog(doubanMeta.getCatalog());
        apabiBookMeta.setTags(doubanMeta.getTags());
        apabiBookMeta.setThumImgUrl(doubanMeta.getSmallCover());
        apabiBookMeta.setMediumCover(doubanMeta.getMediumCover());
        apabiBookMeta.setCoverUrl(doubanMeta.getLargeCover());
        String metaid = StringToolUtil.metaidFormat(issuedDate);
        apabiBookMeta.setMetaId(metaid);
        return apabiBookMeta;
    }

    public static DoubanMeta transform2DoubanMeta(ApabiBookMeta apabiBookMeta) {
        DoubanMeta doubanMeta = new DoubanMeta();
        doubanMeta.setDoubanId(apabiBookMeta.getDoubanId());
        doubanMeta.setIsbn13(apabiBookMeta.getIsbn13());
        doubanMeta.setIsbn10(apabiBookMeta.getIsbn10());
        doubanMeta.setTitle(apabiBookMeta.getTitle());
        doubanMeta.setAuthor(apabiBookMeta.getCreator());
        doubanMeta.setPublisher(apabiBookMeta.getPublisher());
        doubanMeta.setAltTitle(apabiBookMeta.getAlternativeTitle());
        doubanMeta.setSubtitle(apabiBookMeta.getSubTitle());
        doubanMeta.setOriginTitle(apabiBookMeta.getOriginTitle());
        doubanMeta.setTranslator(apabiBookMeta.getTranslator());
        doubanMeta.setIssueddate(apabiBookMeta.getIssueddate());
        doubanMeta.setPages(apabiBookMeta.getBookPages());
        doubanMeta.setPrice(apabiBookMeta.getPaperPrice());
        doubanMeta.setEbookPrice(apabiBookMeta.getEbookPrice());
        doubanMeta.setBinding(apabiBookMeta.getBinding());
        doubanMeta.setSeries(apabiBookMeta.getRelation());
        doubanMeta.setSummary(apabiBookMeta.getAbstract_());
        doubanMeta.setAuthorIntro(apabiBookMeta.getAuthorIntro());
        doubanMeta.setCatalog(apabiBookMeta.getFoamatCatalog());
        doubanMeta.setTags(apabiBookMeta.getTags());
        doubanMeta.setSmallCover(apabiBookMeta.getThumImgUrl());
        doubanMeta.setMediumCover(apabiBookMeta.getMediumCover());
        doubanMeta.setLargeCover(apabiBookMeta.getCoverUrl());
        doubanMeta.setHasCrawled("1");
        if (apabiBookMeta.getCreateTime() == null) {
            doubanMeta.setCreateTime(new Date());
        } else {
            doubanMeta.setCreateTime(apabiBookMeta.getCreateTime());
        }
        doubanMeta.setUpdateTime(new Date());
        return doubanMeta;
    }

    public static ApabiBookMetaTemp transform2ApabiBookMetaTemp(DoubanMeta doubanMeta) {
        ApabiBookMetaTemp apabiBookMetaTemp = new ApabiBookMetaTemp();
        apabiBookMetaTemp.setDoubanId(doubanMeta.getDoubanId());
        String isbn = "";
        if (StringUtils.isNotEmpty(doubanMeta.getIsbn13())) {
            isbn = Isbn13ToIsbnUtil.transform(doubanMeta.getIsbn13());
        }
        apabiBookMetaTemp.setIsbn(isbn);
        apabiBookMetaTemp.setIsbn13(doubanMeta.getIsbn13());
        apabiBookMetaTemp.setIsbn10(doubanMeta.getIsbn10());
        apabiBookMetaTemp.setTitle(doubanMeta.getTitle());
        apabiBookMetaTemp.setCreator(doubanMeta.getAuthor());
        apabiBookMetaTemp.setPublisher(doubanMeta.getPublisher());
        apabiBookMetaTemp.setAlternativeTitle(doubanMeta.getAltTitle());
        apabiBookMetaTemp.setSubTitle(doubanMeta.getSubtitle());
        apabiBookMetaTemp.setOriginTitle(doubanMeta.getOriginTitle());
        apabiBookMetaTemp.setTranslator(doubanMeta.getTranslator());
        String issuedDate = StringToolUtil.issuedDateFormat(doubanMeta.getIssueddate());
        if (issuedDate != null && issuedDate.contains(" 00:00:00")) {
            issuedDate = issuedDate.replaceAll(" 00:00:00", "");
        }
        apabiBookMetaTemp.setIssueddate(issuedDate);
        apabiBookMetaTemp.setBookPages(doubanMeta.getPages());
        apabiBookMetaTemp.setPaperPrice(doubanMeta.getPrice());
        apabiBookMetaTemp.setEbookPrice(doubanMeta.getEbookPrice());
        apabiBookMetaTemp.setBinding(doubanMeta.getBinding());
        apabiBookMetaTemp.setRelation(doubanMeta.getSeries());
        apabiBookMetaTemp.setAbstract_(doubanMeta.getSummary());
        apabiBookMetaTemp.setAuthorIntro(doubanMeta.getAuthorIntro());
        apabiBookMetaTemp.setFoamatCatalog(doubanMeta.getCatalog());
        apabiBookMetaTemp.setTags(doubanMeta.getTags());
        apabiBookMetaTemp.setThumImgUrl(doubanMeta.getSmallCover());
        apabiBookMetaTemp.setMediumCover(doubanMeta.getMediumCover());
        apabiBookMetaTemp.setCoverUrl(doubanMeta.getLargeCover());
        String metaid = StringToolUtil.metaidFormat(issuedDate);
        apabiBookMetaTemp.setMetaId(metaid);
        return apabiBookMetaTemp;
    }

    public static ApabiBookMetaTemp mergeApabiBookMetaTempWithAmazon(ApabiBookMetaTemp apabiBookMetaTemp, AmazonMeta amazonMeta) {
        if (apabiBookMetaTemp.getAbstract_() == null || "".equalsIgnoreCase(apabiBookMetaTemp.getAbstract_())) {
            apabiBookMetaTemp.setAbstract_(amazonMeta.getAbstract_());
        }

        if (apabiBookMetaTemp.getFoamatCatalog() == null || "".equalsIgnoreCase(apabiBookMetaTemp.getFoamatCatalog())) {
            apabiBookMetaTemp.setFoamatCatalog(amazonMeta.getCatalog());
        }
        if (apabiBookMetaTemp.getPreface() == null || "".equalsIgnoreCase(apabiBookMetaTemp.getPreface())) {
            apabiBookMetaTemp.setPreface(amazonMeta.getPreface());
        }
        if (apabiBookMetaTemp.getPostScript() == null || "".equalsIgnoreCase(apabiBookMetaTemp.getPostScript())) {
            apabiBookMetaTemp.setPostScript(amazonMeta.getPostScript());
        }
        if (apabiBookMetaTemp.getAbstract_() == null || "".equalsIgnoreCase(apabiBookMetaTemp.getAbstract_())) {
            apabiBookMetaTemp.setAbstract_(amazonMeta.getAbstract_());
        }
        if (apabiBookMetaTemp.getIsbn13() == null || "".equalsIgnoreCase(apabiBookMetaTemp.getIsbn13())) {
            apabiBookMetaTemp.setIsbn13(amazonMeta.getIsbn13());
        }
        if (apabiBookMetaTemp.getIsbn10() == null || "".equalsIgnoreCase(apabiBookMetaTemp.getIsbn10())) {
            apabiBookMetaTemp.setIsbn10(amazonMeta.getIsbn10());
        }
        if (apabiBookMetaTemp.getPublisher() == null || "".equalsIgnoreCase(apabiBookMetaTemp.getPublisher())) {
            apabiBookMetaTemp.setPublisher(amazonMeta.getPublisher());
        }
        if (apabiBookMetaTemp.getOriginTitle() == null || "".equalsIgnoreCase(apabiBookMetaTemp.getOriginTitle())) {
            apabiBookMetaTemp.setOriginTitle(amazonMeta.getOriginTitle());
        }
        if (apabiBookMetaTemp.getPaperPrice() == null || "".equalsIgnoreCase(apabiBookMetaTemp.getPaperPrice())) {
            apabiBookMetaTemp.setPaperPrice(amazonMeta.getPaperPrice());
        }
        if (apabiBookMetaTemp.getEbookPrice() == null || "".equalsIgnoreCase(apabiBookMetaTemp.getEbookPrice())) {
            apabiBookMetaTemp.setEbookPrice(amazonMeta.getKindlePrice());
        }
        if (apabiBookMetaTemp.getBinding() == null || "".equalsIgnoreCase(apabiBookMetaTemp.getBinding())) {
            apabiBookMetaTemp.setBinding(amazonMeta.getBinding());
        }
        if (apabiBookMetaTemp.getBookPages() == null || "".equalsIgnoreCase(apabiBookMetaTemp.getBookPages())) {
            apabiBookMetaTemp.setBookPages(amazonMeta.getPages());
        }
        if (apabiBookMetaTemp.getAmazonId() == null || "".equalsIgnoreCase(apabiBookMetaTemp.getAmazonId())) {
            apabiBookMetaTemp.setAmazonId(amazonMeta.getAmazonId());
        }
        if (apabiBookMetaTemp.getIsbn() == null || "".equalsIgnoreCase(apabiBookMetaTemp.getIsbn())) {
            String isbn13 = amazonMeta.getIsbn13();
            String isbn = Isbn13ToIsbnUtil.transform(isbn13);
            apabiBookMetaTemp.setIsbn(isbn);
        }
        return apabiBookMetaTemp;
    }

    public static DoubanMeta transform2DoubanMetaFromAmazon(AmazonMeta amazonMeta) {
        DoubanMeta doubanMeta = new DoubanMeta();
        doubanMeta.setTitle(amazonMeta.getTitle());
        doubanMeta.setAuthor(amazonMeta.getAuthor());
        doubanMeta.setTranslator(amazonMeta.getTranslator());
        doubanMeta.setIsbn13(amazonMeta.getIsbn13());
        doubanMeta.setIsbn10(amazonMeta.getIsbn10());
        doubanMeta.setPublisher(amazonMeta.getPublisher());
        doubanMeta.setOriginTitle(amazonMeta.getOriginTitle());
        doubanMeta.setPrice(amazonMeta.getPaperPrice());
        doubanMeta.setEbookPrice(amazonMeta.getKindlePrice());
        doubanMeta.setIssueddate(amazonMeta.getIssuedDate());
        doubanMeta.setBinding(amazonMeta.getBinding());
        doubanMeta.setPages(amazonMeta.getPages());
        doubanMeta.setAuthorIntro(amazonMeta.getAuthorIntroduction());
        doubanMeta.setCatalog(amazonMeta.getCatalog());
        doubanMeta.setCreateTime(amazonMeta.getCreateTime());
        doubanMeta.setUpdateTime(amazonMeta.getUpdateTime());
        return doubanMeta;
    }

    public static DoubanMeta mergeDoubanWithAmazon(DoubanMeta doubanMeta, AmazonMeta amazonMeta) {
        if (doubanMeta.getIsbn13() == null || "".equalsIgnoreCase(doubanMeta.getIsbn13())) {
            doubanMeta.setIsbn13(amazonMeta.getIsbn13());
        }
        if (doubanMeta.getTitle() == null || "".equalsIgnoreCase(doubanMeta.getTitle())) {
            doubanMeta.setTitle(amazonMeta.getTitle());
        }
        if (doubanMeta.getAuthor() == null || "".equalsIgnoreCase(doubanMeta.getAuthor())) {
            doubanMeta.setAuthor(amazonMeta.getAuthor());
        }
        if (doubanMeta.getPublisher() == null || "".equalsIgnoreCase(doubanMeta.getPublisher())) {
            doubanMeta.setPublisher(amazonMeta.getPublisher());
        }
        if (doubanMeta.getTranslator() == null || "".equalsIgnoreCase(doubanMeta.getTranslator())) {
            doubanMeta.setTranslator(amazonMeta.getTranslator());
        }
        if (doubanMeta.getIssueddate() == null || "".equalsIgnoreCase(doubanMeta.getIssueddate())) {
            String issuedDate = "";
            if (amazonMeta.getIssuedDate() != null) {
                issuedDate = StringToolUtil.issuedDateFormat(amazonMeta.getIssuedDate());
                if (issuedDate.contains(" 00:00:00")) {
                    issuedDate = amazonMeta.getIssuedDate().replaceAll(" 00:00:00", "");
                }
                if (issuedDate.length() == 4) {
                    issuedDate += "-01-01";
                }
                if (issuedDate.length() == 7) {
                    issuedDate += "-01";
                }
            }
            doubanMeta.setIssueddate(issuedDate);
        }
        if (doubanMeta.getPages() == null || "".equalsIgnoreCase(doubanMeta.getPages())) {
            doubanMeta.setPages(amazonMeta.getPages());
        }
        if (doubanMeta.getPrice() == null || "".equalsIgnoreCase(doubanMeta.getPrice())) {
            doubanMeta.setPrice(amazonMeta.getPaperPrice());
        }
        if (doubanMeta.getBinding() == null || "".equalsIgnoreCase(doubanMeta.getBinding())) {
            doubanMeta.setBinding(amazonMeta.getBinding());
        }
        if (doubanMeta.getSeries() == null || "".equalsIgnoreCase(doubanMeta.getSeries())) {
            doubanMeta.setSeries(amazonMeta.getSeries());
        }
        if (doubanMeta.getSummary() == null || "".equalsIgnoreCase(doubanMeta.getSummary())) {
            doubanMeta.setSummary(amazonMeta.getSummary());
        }
        if (doubanMeta.getAuthorIntro() == null || "".equalsIgnoreCase(doubanMeta.getAuthorIntro())) {
            doubanMeta.setAuthorIntro(amazonMeta.getAuthorIntroduction());
        }
        if (doubanMeta.getCatalog() == null || "".equalsIgnoreCase(doubanMeta.getCatalog())) {
            doubanMeta.setCatalog(amazonMeta.getCatalog());
        }
        if (doubanMeta.getIsbn10() == null || "".equalsIgnoreCase(doubanMeta.getIsbn10())) {
            doubanMeta.setIsbn10(amazonMeta.getIsbn10());
        }
        if (doubanMeta.getOriginTitle() == null || "".equalsIgnoreCase(doubanMeta.getOriginTitle())) {
            doubanMeta.setOriginTitle(amazonMeta.getOriginTitle());
        }
        if (doubanMeta.getEbookPrice() == null || "".equalsIgnoreCase(doubanMeta.getEbookPrice())) {
            doubanMeta.setEbookPrice(amazonMeta.getKindlePrice());
        }
        return doubanMeta;
    }
}
