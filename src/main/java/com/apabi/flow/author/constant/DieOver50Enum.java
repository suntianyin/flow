package com.apabi.flow.author.constant;

import com.apabi.flow.common.api.BaseEnum;
import com.apabi.flow.common.api.CodeBaseEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述： <br>
 * <是否卒于五十年前>
 *
 * @author supeng
 * @date 2018/8/30 17:42
 * @since 1.0.0
 */
public enum DieOver50Enum implements BaseEnum<DieOver50Enum, Integer>,CodeBaseEnum {

    NO(0, "否"),

    YES(1, "是");

    private Integer code;
    private String desc;

    static Map<Integer,DieOver50Enum> enumMap=new HashMap<>();
    static{
        for(DieOver50Enum type:DieOver50Enum.values()){
            enumMap.put(type.getCode(), type);
        }
    }

    DieOver50Enum(Integer code, String desc) {
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

    public static DieOver50Enum getEnum(int code){
        return enumMap.get(code);
    }
}
