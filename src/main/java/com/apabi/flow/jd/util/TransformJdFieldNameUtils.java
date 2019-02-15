package com.apabi.flow.jd.util;

/**
 * @Author pipi
 * @Date 2018/8/29 13:48
 **/
public class TransformJdFieldNameUtils {
    private static String[] fields = {
            "jdItemId,京东Id",
            "title,书名",
            "isbn13,ISBN13",
            "publisher,出版社",
            "editionOrder,版次",
            "issuedDate,出版日期",
            "binding,装帧",
            "pages,页数",
            "language,语言",
            "format,开本",
            "brand,品牌",
            "metaId,metaId",
            "createTime,创建时间",
            "updateTime,更新时间"
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
