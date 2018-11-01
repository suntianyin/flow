package com.apabi.flow.author.util;

/**
 * @author: sunty
 * @date: 2018/10/31 16:16
 * @description:
 */

public class TransformAuthorFieldNameUtils {
    private static String[] fields = {
            "id,作者id",
            "title,作者姓名",
            "titleType,姓名类型",
            "startDate,开始使用时间",
            "endDate,结束使用时间",
            "nationalityCode,国籍",
            "personId,身份证号",
            "birthday,出生日期",
            "deathDay,死亡日期",
            "sexCode,性别",
            "type,作者类型",
            "nationalCode,民族",
            "qualificationCode,学历",
            "dynastyName,所在朝代名称",
            "originCode,籍贯",
            "careerClassCode,职业分类名称",
            "serviceAgency,任职机构名称",
            "headPortraitPath,头像",
            "summary,简介",
            "dieOver50,是否卒于50年",
            "nlcAuthorId,国图作者id",
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
