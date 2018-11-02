package com.apabi.flow.publisher.constant;

import com.apabi.flow.common.api.BaseEnum;
import com.apabi.flow.common.api.CodeBaseEnum;
import com.fasterxml.jackson.annotation.JsonCreator;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

//出版社分类
public enum ClassCodeEnum implements BaseEnum<ClassCodeEnum, String>,CodeBaseEnum<String> {
    COMPREHENSIVE("a", "综合"),
    CHILDREN("b", "少儿"),
    YOUTH("c", "青年"),
    UNIVERSITY("d", "大学"),
    NAME("e", "人名"),
    OTHER("z", "其他");
    private String code;
    private String desc;
    ClassCodeEnum() {
    }

    ClassCodeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
    static Map<String,ClassCodeEnum> enumMap=new HashMap<>();
    static{
        for(ClassCodeEnum type:ClassCodeEnum.values()){
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
    public static ClassCodeEnum getEnum(String code) {
        return enumMap.get(code);
    }

}
