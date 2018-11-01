package com.apabi.flow.publisher.util;

/**
 * @author: sunty
 * @date: 2018/10/30 15:07
 * @description:
 */

public class TransformPublisherFieldNameUtils {
    private static String[] fields = {
            "id,出版社id",
            "relatePublisherID,关联出版社",
            "isbn,isbn",
            "title,出版社名称",
            "titleType,名称类型",
            "startDate,开始使用时间",
            "endDate,结束使用时间",
            "nationalityCode,注册国国籍",
            "founderDate,创办时间",
            "classCode,出版社分类",
            "resourceType,出版文献资源类型",
            "publishingGroup,所属出版集团名称",
            "publishingGroupID,唯一标识",
            "president,社长姓名",
            "vicePresident,副社长姓名",
            "qualityLevel,图书质量等级",
            "summary,简介",
            "place,出版地",
            "operator,操作人",
            "createTime,创建时间",
            "updateTime,最后更新时间",
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
