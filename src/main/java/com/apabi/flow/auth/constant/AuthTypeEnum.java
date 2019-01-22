package com.apabi.flow.auth.constant;

import com.apabi.flow.common.api.BaseEnum;
import com.apabi.flow.common.api.CodeBaseEnum;

import java.util.HashMap;
import java.util.Map;

public enum AuthTypeEnum implements BaseEnum<AuthTypeEnum, Integer>,CodeBaseEnum {
    ONLY2B(0, "仅2B"),

    TOWBYUN(1, "2B+云联盟"),

    TOWB2C(2, "2B2C"),

    UNKNOWN(3, "未知");



    private Integer code;
    private String desc;

    static Map<Integer,AuthTypeEnum> enumMap=new HashMap<>();
    static{
        for(AuthTypeEnum type:AuthTypeEnum.values()){
            enumMap.put(type.getCode(), type);
        }
    }

    AuthTypeEnum(Integer code, String desc) {
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

    public static AuthTypeEnum getEnum(int code){
        return enumMap.get(code);
    }
}
