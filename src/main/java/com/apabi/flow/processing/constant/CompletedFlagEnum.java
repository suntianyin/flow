package com.apabi.flow.processing.constant;

import com.apabi.flow.common.api.BaseEnum;
import com.apabi.flow.common.api.CodeBaseEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述： <br>
 * <是否制作完成 枚举类>
 *
 * @author supeng
 * @date 2018/9/5 9:23
 * @since 1.0.0
 */
public enum CompletedFlagEnum implements BaseEnum<CompletedFlagEnum, Integer>,CodeBaseEnum {

    NO(0, "否"),

    YES(1, "是");

    private Integer code;
    private String desc;

    static Map<Integer,CompletedFlagEnum> enumMap=new HashMap<>();
    static{
        for(CompletedFlagEnum flagEnum: CompletedFlagEnum.values()){
            enumMap.put(flagEnum.getCode(), flagEnum);
        }
    }

    CompletedFlagEnum(Integer code, String desc) {
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

    public static CompletedFlagEnum getEnum(int code){
        return enumMap.get(code);
    }
}
