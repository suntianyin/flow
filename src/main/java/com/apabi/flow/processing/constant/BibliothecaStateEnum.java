package com.apabi.flow.processing.constant;

import com.apabi.flow.common.api.BaseEnum;
import com.apabi.flow.common.api.CodeBaseEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述： <br>
 * <书目状态 枚举类>
 *
 * @author supeng
 * @date 2018/9/5 10:00
 * @since 1.0.0
 */
public enum BibliothecaStateEnum implements BaseEnum<BibliothecaStateEnum, Integer>,CodeBaseEnum {
    NEW(0, "新建"),
    REPEAT(1, "重复"),
    NOREPEAT(2, "不重复"),
    SORTING(3, "已分拣"),
    INFORMATION_NO(4, "信息不全"),
    HAS_PROCESS(5, "已排产"),
    MAKESUC(6, "制作完成"),
    MAKEFAIL(7, "制作失败");


    private Integer code;
    private String desc;

    static Map<Integer, BibliothecaStateEnum> enumMap = new HashMap<>();
    static {
        for (BibliothecaStateEnum stateEnum: BibliothecaStateEnum.values()){
            enumMap.put(stateEnum.getCode(), stateEnum);
        }
    }

    BibliothecaStateEnum(Integer code, String desc) {
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

    public static BibliothecaStateEnum getEnum(int code){
        return enumMap.get(code);
    }
}
