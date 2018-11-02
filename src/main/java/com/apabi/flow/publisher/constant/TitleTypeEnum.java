package com.apabi.flow.publisher.constant;

import com.apabi.flow.common.api.BaseEnum;
import com.apabi.flow.common.api.CodeBaseEnum;
import com.fasterxml.jackson.annotation.JsonCreator;

import java.util.HashMap;
import java.util.Map;

//名称类型
public enum TitleTypeEnum implements BaseEnum<TitleTypeEnum, String>,CodeBaseEnum {
    PRESENT("0", "现名"),
    ORIGINAL("1", "原名"),
    WHOLLY("2", "全资子公司"),
    SIDE("3", "副牌"),
    ZOBO("4", "正牌"),
    MERGE("5", "合并"),
    HIGHER("6", "上级机构"),
    CALLED("7", "又称"),
    INSTITUTION("8", "同一机构"),
    ABBREVIATION("9", "简称"),
    FULL("10", "全称"),
    CONTAIN("11", "包含"),
    BRAND("12", "同一出版社另一牌名");
    private String code;
    private String desc;
    TitleTypeEnum() {
    }

    TitleTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    static Map<String,TitleTypeEnum> enumMap=new HashMap<>();
    static{
        for(TitleTypeEnum type:TitleTypeEnum.values()){
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
    public static TitleTypeEnum getEnum(String code) {
        return enumMap.get(code);
    }
}
