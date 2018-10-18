package com.apabi.flow.douban.util;

/**
 * @Author pipi
 * @Date 2018/8/29 13:48
 **/
public class TransformDoubanFieldNameUtils {
    private static String[] fields = {
            "doubanId,豆瓣Id",
            "isbn13,ISBN13",
            "title,书名",
            "author,作者",
            "publisher,出版社",
            "altTitle,原著名",
            "translator,译者",
            "issueddate,出版日期",
            "pages,页数",
            "price,定价",
            "binding,装帧",
            "series,丛书",
            "average,豆瓣评分",
            "summary,内容简介",
            "authorIntro,作者简介",
            "catalog,目录",
            "tags,豆瓣成员常用的标签",
            "hasCrawled,是否已经抓取",
            "isbn10,ISBN10",
            "subTitle,副标题",
            "originTitle,原作名",
            "ebookPrice,电子书定价",
            "createTime,创建时间",
            "updateTime,更新时间",
            "smallCover,封面小图",
            "mediumCover,封面中图",
            "largeCover,封面大图",
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
