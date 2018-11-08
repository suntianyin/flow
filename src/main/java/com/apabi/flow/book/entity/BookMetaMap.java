package com.apabi.flow.book.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * @author guanpp
 * @date 2018/8/30 14:58
 * @description
 */
public class BookMetaMap {

    public final static Map map = new HashMap<String, String>() {{
        put("metaId", "图书id");
        put("idType", "标识类型");
        put("saleStatus", "上架状态");
        put("language", "语种");
        put("title", "书名");
        put("subtitle", "副标题");
        put("creator", "主要责任者");
        put("authorIntro", "作者简介");
        put("creatorWord", "责任关系词");
        put("creatorId", "主要责任者ID");
        put("contributor", "次要责任者");
        put("contributorWord", "次要责任责任关系词");
        put("contributorId", "次要责任者ID");
        put("translator", "翻译");
        put("translatorId", "翻译ID");
        put("originTitle", "原书名");
        put("alternativeTitle", "其它题名");
        put("editionOrder", "版次");
        put("editionNote", "版次说明");
        put("place", "出版地");
        put("publisher", "出版社");
        put("publisherId", "出版社ID");
        put("issuedDate", "出版日期");
        put("endIssuedDate", "结束出版日期");
        put("issuedDateDesc", "出版日期说明");
        put("abstract_", "内容提要");
        put("subject", "主题/关键词");
        put("preface", "序言");
        put("reader", "读者对象");
        put("classCode", "中图法分类号");
        put("apabiClass", "网站分类号");
        put("type", "类型");
        put("isbn", "ISBN");
        put("isbn10", "Isbn10");
        put("isbn13", "Isbn13");
        put("paperPrice", "纸书价格");
        put("ebookPrice", "电子书价格");
        put("foreignPrice", "外汇价格");
        put("foreignPriceType", "外汇价格类型");
        put("paperPriceDesc", "价格说明");
        put("binding", "装帧");
        put("illustration", "图表及其它细节");
        put("pressOrder", "印次");
        put("editor", "责任编辑");
        put("relation", "相关文献");
        put("relationId", "丛套书唯一标示符");
        put("volume", "分卷信息");
        put("isSeries", "是否为从套书");
        put("volumesCount", "总册数");
        put("isAllublished", "是否全部出版完成");
        put("notes", "附注");
        put("podPrice", "POD价格");
        put("podPriceType", "POD价格类型");
        put("podPricePageUnit", "POD页计价单位");
        put("drId", "书苑数据库id");
        put("doubanId", "豆瓣id");
        put("amazonId", "Amazon id");
        put("calisId", "Calis ID");
        put("nlibraryId", "国图id");
        put("dataSource", "数据来源");
        put("qualityRating", "书目著录的质量等级");
        put("qrType", "等级类型");
        put("cebxObjId", "cebx实体ID");
        put("cebxFileSize", "cebx文件大小");
        put("hasCebx", "是否有cebx");
        put("cebxPage", "cebx页数");
        put("complexOid", "complexOID");
        put("reditor", "reditor");
        put("departmentId", "部门ID");
        put("coverUrl", "封面url");
        put("imgHeigth", "封面高度");
        put("imgWidth", "封面宽度");
        put("covObjId", "封面实体ID");
        put("thumImgUrl", "封面缩略图url");
        put("thumImgSize", "封面文件大小");
        put("mediumCover", "中等封面");
        put("hasFlow", "是否有流式内容");
        put("isOptimize", "流式内容是否优化");
        put("donor", "投稿者/贡献者");
        put("libraryId", "图书馆ID");
        put("bookId", "图书号");
        put("styleClass", "样式class");
        put("styleUrl", "样式url");
        put("chapterNum", "章节数");
        put("bookPages", "页数");
        put("tags", "标签");
        put("createTime", "创建时间");
        put("updateTime", "更改时间");
        put("contentNum", "字数");
        put("foamatCatalog", "版式目录");
        put("streamCatalog", "流式目录");
        put("hasPublish", "是否发布");
        put("isPublicCopyRight", "是否有发布权限");
        put("isReadEpub", "是否epub阅读");
        put("isReadCebxFlow", "是否cebx流式阅读");
        put("flowSource", "流式内容来源");
    }};
}
