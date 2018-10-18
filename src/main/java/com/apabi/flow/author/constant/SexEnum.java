package com.apabi.flow.author.constant;

import com.apabi.flow.common.api.BaseEnum;
import com.apabi.flow.common.api.CodeBaseEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述： <br>
 * <性别 枚举描述>
 *
 * @author supeng
 * @date 2018/8/30 16:20
 * @since 1.0.0
 */
public enum SexEnum implements BaseEnum<SexEnum, Integer>,CodeBaseEnum {

    MALE(0, "男"),
    FEMALE(1, "女"),
    UNKNOWN(2, "未知");

    private Integer code;
    private String desc;

    static Map<Integer,SexEnum> enumMap=new HashMap<>();
    static{
        for(SexEnum type:SexEnum.values()){
            enumMap.put(type.getCode(), type);
        }
    }

    SexEnum(Integer code, String desc) {
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

    public static SexEnum getEnum(int code) {
        return enumMap.get(code);
    }
}
