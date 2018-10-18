package com.apabi.flow.processing.constant;

import com.apabi.flow.common.api.BaseEnum;
import com.apabi.flow.common.api.CodeBaseEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述： <br>
 * <删除标识 枚举类>
 *
 * @author supeng
 * @date 2018/9/5 9:23
 * @since 1.0.0
 */
public enum DeleteFlagEnum implements BaseEnum<DeleteFlagEnum, Integer>,CodeBaseEnum {

    DELETED(0, "已删除"),

    NORMAL(1, "正常");

    private Integer code;
    private String desc;

    static Map<Integer,DeleteFlagEnum> enumMap=new HashMap<>();
    static{
        for(DeleteFlagEnum flagEnum:DeleteFlagEnum.values()){
            enumMap.put(flagEnum.getCode(), flagEnum);
        }
    }

    DeleteFlagEnum(Integer code, String desc) {
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

    public static DeleteFlagEnum getEnum(int code){
        return enumMap.get(code);
    }
}
