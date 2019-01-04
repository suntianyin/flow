package com.apabi.flow.processing.constant;

import com.apabi.flow.common.api.BaseEnum;
import com.apabi.flow.common.api.CodeBaseEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述： <br>
 * <批次状态 枚举类>
 *
 * @author supeng
 * @date 2018/9/5 10:00
 * @since 1.0.0
 */
public enum BatchStateEnum implements BaseEnum<BatchStateEnum, Integer>,CodeBaseEnum {
    WAITING_DISTRIBUTION(0, "待分配"),
    WAITING_INPUT(1, "待书单"),
    BEGIN_SCANNING(7, "正在扫描书目"),
    FINISH_SCANNING(8, "书目扫描完成"),
    WAITING_CHECKED(2, "待查重"),
    WAITING_PRODUCTION(3, "待排产"),
    PRODUCTION(4, "已排产"),
    PROCESSING(5, "制作中"),
    COMPLETED(6, "已完成");


    private Integer code;
    private String desc;

    static Map<Integer, BatchStateEnum> enumMap = new HashMap<>();
    static {
        for (BatchStateEnum stateEnum: BatchStateEnum.values()){
            enumMap.put(stateEnum.getCode(), stateEnum);
        }
    }

    BatchStateEnum(Integer code, String desc) {
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

    public static BatchStateEnum getEnum(int code){
        return enumMap.get(code);
    }
}
