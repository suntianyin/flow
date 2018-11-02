package com.apabi.flow.publisher.constant;

import com.apabi.flow.common.api.BaseEnum;
import com.apabi.flow.common.api.CodeBaseEnum;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.HashMap;
import java.util.Map;

//出版文献资源类型
public enum ResourceTypeEnum implements BaseEnum<ResourceTypeEnum, String>,CodeBaseEnum {
    EBOOK("Ebook", "图书"),
    REFBOOK("RefBook", "工具书"),
    YEARBOOK("YearBook", "年鉴"),
    PICLIB("PicLib", "图片库"),
    NEWSPAPER("Newspaper", "报纸"),
    VIDEO("video", "视频"),
    AUDIO("audio", "音频");
    private String code;
    private String desc;
    ResourceTypeEnum() {
    }

    ResourceTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    static Map<String,ResourceTypeEnum> enumMap=new HashMap<>();
    static{
        for(ResourceTypeEnum type:ResourceTypeEnum.values()){
            enumMap.put(type.getCode(), type);
        }
    }
    @Override
    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
    @JsonCreator
    public static ResourceTypeEnum getEnum(String code) {
        return enumMap.get(code);
    }
}
