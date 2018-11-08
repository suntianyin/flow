package com.apabi.flow.publish.util;

/**
 * @Author pipi
 * @Date 2018/8/29 13:48
 **/
public class TransformFieldNameUtils {

    private static String[] fields = {
            "metaId,阿帕比图书metaid",
            "idType,标识类型",
            "saleStatus,上架状态",
            "language,语言",
            "title,书名",
            "subtitle,副标题",
            "creator,主要责任人",
            "authorIntro,作者简介",
            "creatorWord,责任关系词",
            "creatorId,主要责任者ID",
            "contributor,次要责任者",
            "contributorWord,次要责任责任关系词",
            "contributorId,次要责任者ID",
            "translator,翻译",
            "translatorId,翻译ID",
            "originTitle,原书名",
            "alternativeTitle,其它题名",
            "editionOrder,版次",
            "editionNote,版次说明",
            "place,出版地",
            "publisher,出版社",
            "publisherId,出版社ID",
            "issuedDate,出版日期",
            "endIssuedDate,结束出版日期",
            "issuedDateDesc,出版日期说明",
            "abstract_,内容提要",
            "subject,主题/关键词",
            "preface,序言",
            "reader,读者对象",
            "classCode,中图法分类号",
            "apabiClass,网站分类号",
            "type,类型",
            "isbn,ISBN",
            "isbn10,ISBN10",
            "isbn13,ISBN13",
            "paperPrice,纸书价格",
            "ebookPrice,电子书价格",
            "foreignPrice,外汇价格",
            "foreignPriceType,外汇价格类型",
            "paperPriceDesc,价格说明",
            "binding,装帧",
            "illustration,图表及其它细节",
            "pressOrder,印次",
            "editor,责任编辑",
            "relation,相关文献",
            "relationType,相关文献与本文献的联系",
            "relationId,丛套书唯一标示符",
            "volume,分卷信息",
            "isSeries,是否为丛套书",
            "volumesCount,总册数",
            "isAllublished,是否全部出版完成",
            "notes,附注",
            "podPrice,POD价格",
            "podPriceType,POD价格类型",
            "podPricePageUnit,POD页计价单位",
            "drId,书苑数据库id",
            "doubanId,豆瓣id",
            "amazonId,Amazon id",
            "calisId,Calis ID",
            "nlibraryId,国图 id",
            "dataSource,数据来源",
            "qualityRating,书目著录的质量等级",
            "qrType,等级类型",
            "cebxObjId,cebx实体ID",
            "cebxFileSize,cebx文件大小",
            "hasCebx,是否有cebx",
            "cebxPage,cebx页数",
            "complexOid,complexOID",
            "reditor,reditor(再编辑)",
            "departmentId,部门ID",
            "coverUrl,封面url",
            "imgHeigth,封面高度",
            "imgWidth,封面宽度",
            "covObjId,封面实体ID",
            "thumImgUrl,封面缩略图url",
            "thumImgSize,封面文件大小",
            "mediumCover,中等封面",
            "foamatCatalog,版式目录",
            "streamCatalog,流式目录",
            "postScript,后记",
            "hasFlow,是否有流式内容",
            "isOptimize,流式内容是否优化",
            "donor,投稿者/贡献者",
            "libraryId,图书馆ID",
            "bookId,图书号",
            "styleClass,样式class",
            "styleUrl,样式url",
            "chapterNum,章节数",
            "bookPages,页数",
            "tags,标签",
            "createTime,创建时间",
            "updateTime,更新时间",
            "contentNum,字数",
            "hasPublish,是否发布",
            "isPublicCopyRight,是否公版",
            "isReadCebxFlow,是否cebx流式阅读",
            "flowSource,流式内容来源",
            "isReadEpub,是否epub阅读"
    };

    public static String transform(String fieldName) {
        for (String singleFieldName : fields) {
            String[] originalFieldName = singleFieldName.split(",");
            if (fieldName.equalsIgnoreCase(originalFieldName[0])) {
                return originalFieldName[1];
            }
        }
        return fieldName;
    }

    public static String reTransform(String fieldName) {
        for (String singleFieldName : fields) {
            String[] originalFieldName = singleFieldName.split(",");
            if(fieldName.equalsIgnoreCase(originalFieldName[1])){
                return originalFieldName[0];
            }
        }
        return fieldName;
    }
}
