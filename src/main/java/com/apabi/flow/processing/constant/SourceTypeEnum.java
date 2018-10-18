package com.apabi.flow.processing.constant;

import com.apabi.flow.common.api.BaseEnum;
import com.apabi.flow.common.api.CodeBaseEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述： <br>
 * <资源类型 枚举类>
 *
 * @author supeng
 * @date 2018/9/5 9:36
 * @since 1.0.0
 */
public enum SourceTypeEnum implements BaseEnum<SourceTypeEnum, Integer>,CodeBaseEnum {
    E_BOOK(0, "电子书"),
    PAPER_BOOK(1, "纸质书");

    private Integer code;
    private String desc;

    static Map<Integer, SourceTypeEnum> enumMap = new HashMap<>();
    static {
        for (SourceTypeEnum typeEnum: SourceTypeEnum.values()){
            enumMap.put(typeEnum.getCode(), typeEnum);
        }
    }

    SourceTypeEnum(Integer code, String desc) {
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

    public static SourceTypeEnum getEnum(int code){
        return enumMap.get(code);
    }
}
