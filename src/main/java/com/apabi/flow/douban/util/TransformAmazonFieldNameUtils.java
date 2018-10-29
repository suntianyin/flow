package com.apabi.flow.douban.util;

/**
 * @Author pipi
 * @Date 2018/10/25 16:32
 **/
public class TransformAmazonFieldNameUtils {
    private static String[] fields = {
            "amazonId,亚马逊Id",
            "title,书名",
            "author,作者",
            "translator,译者",
            "isbn13,isbn13",
            "isbn10,isbn10",
            "publisher,出版社",
            "originTitle,原书名",
            "paperPrice,定价",
            "kindlePrice,电子书定价",
            "editionOrder,版次",
            "issuedDate,出版日期",
            "originSeries,外文丛书",
            "binding,装帧",
            "pages,页数",
            "language,语种",
            "series,丛书",
            "format,开本",
            "productSize,商品尺寸",
            "commodityWeight,商品重量",
            "brand,品牌",
            "asin,ASIN",
            "classification,分类",
            "editRecommend,编辑推荐",
            "celebrityRecommend,名人推荐",
            "mediaRecommendation,媒体推荐",
            "authorIntroduction,作者简介",
            "catalog,目录",
            "preface,序言",
            "abstract_,文摘",
            "summary,内容简介",
            "poster,海报",
            "readerObject,读者对象",
            "hasCrawled,是否已抓取数据",
            "createTime,创建时间",
            "updateTime,更新时间",
            "postScript,后记"
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


