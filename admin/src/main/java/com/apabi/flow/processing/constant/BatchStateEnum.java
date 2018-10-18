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
    WAITING_INPUT(0, "待录入书单"),
    INPUT_COMPLETED(1, "书单录入完成"),
    AUDITED(2, "书单已审核"),
    AUDIT_FAILED(3, "书单审核失败"),
    DUPLICATE_CHECKED(4, "已查重"),
    PROCESSING(5, "制作中"),
    PROCESSED_WAITING_AUDIT(6, "制作完成待审核"),
    COMPLETED(7, "生产完成");

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
