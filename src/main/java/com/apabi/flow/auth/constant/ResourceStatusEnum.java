package com.apabi.flow.auth.constant;

import com.apabi.flow.common.api.BaseEnum;
import com.apabi.flow.common.api.CodeBaseEnum;

import java.util.HashMap;
import java.util.Map;

public enum ResourceStatusEnum implements BaseEnum<ResourceStatusEnum, Integer>,CodeBaseEnum {
    AUTHORIZATIONPERIOD(0, "未授权"),

    OUTSIDE(1, "已授权"),

    SHELVESDOWN(2, "特殊下架"),

    SHELVESUP(3, "特殊上架");



    private Integer code;
    private String desc;

    static Map<Integer, ResourceStatusEnum> enumMap=new HashMap<>();
    static{
        for(ResourceStatusEnum type: ResourceStatusEnum.values()){
            enumMap.put(type.getCode(), type);
        }
    }

    ResourceStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static ResourceStatusEnum getEnum(int code){
        return enumMap.get(code);
    }
}
