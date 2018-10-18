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
    EDITABLE(0, "可编辑"),
    WAITING_AUDIT(1, "待审核"),
    AUDITED(2, "已审核"),
    EXCLUDED(3, "已排除"),
    DUPLICATE_CHECKED(4, "已查重"),
    WAITING_PROCESS(5, "待加工"),
    COMPLETED(6, "加工完成");

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
