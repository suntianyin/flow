package com.apabi.flow.video.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: sunty
 * @date: 2018/10/24 10:45
 * @description:
 */

public class VideoMetaMap {
    public final static Map map = new HashMap<String, String>() {{
        put("id", "视频编号");
        put("chTitle", "中文名称");
        put("enTitle", "英文名称");
        put("series", "所属系列");
        put("keyWord", "关键词");
        put("human", "主要人物");
        put("description", "内容描述（类似剧情摘要）");
        put("creator", "责任者");
        put("subject", "分类");
        put("label", "标签");
        put("source", "来源");
        put("scene", "场景");
        put("userAge", "适应年龄段（或人群）");
        put("dialect", "方言");
        put("dialogueLanguage", "配音语种");
        put("captionLanguages", "字幕语种");
        put("codeFormat", "编码格式");
        put("rightOwner", "版权方");
        put("authorizationPeriod", "授权期限");
        put("type", "视频格式");
        put("videoTime", "视频时长");
        put("score", "评分");
        put("publicationCountry", "出版国家");
        put("publicationCompany", "出版公司");
        put("publicationYear", "出版年份");
        put("videoSize", "大小");
        put("color", "色彩");
        put("createTime", "制作日期");
        put("savePath", "存放路径");
        put("previewPath", "预览图路径");
        put("playPath", "播放路径");
        put("note", "其他信息");
        put("insertTime", "记录创建时间");
        put("operator", "记录创建人");
        put("updateTime", "记录修改时间");
    }};
}
