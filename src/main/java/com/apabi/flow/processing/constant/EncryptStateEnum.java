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
public enum EncryptStateEnum implements BaseEnum<EncryptStateEnum, Integer>,CodeBaseEnum {
    UNBEGIN(0, "未加密"),
    BEGIN(1, "正在加密"),
    FINISH(2, "加密完成"),
    PAUSE(3, "暂停");


    private Integer code;
    private String desc;

    static Map<Integer, EncryptStateEnum> enumMap = new HashMap<>();
    static {
        for (EncryptStateEnum stateEnum: EncryptStateEnum.values()){
            enumMap.put(stateEnum.getCode(), stateEnum);
        }
    }

    EncryptStateEnum(Integer code, String desc) {
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

    public static EncryptStateEnum getEnum(int code){
        return enumMap.get(code);
    }
}
