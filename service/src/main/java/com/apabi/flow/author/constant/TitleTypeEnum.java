package com.apabi.flow.author.constant;

import com.apabi.flow.common.api.BaseEnum;
import com.apabi.flow.common.api.CodeBaseEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述： <br>
 * <姓名类型>
 *
 * @author supeng
 * @date 2018/8/24 14:38
 * @since 1.0.0
 */
public enum TitleTypeEnum implements BaseEnum<TitleTypeEnum, Integer>,CodeBaseEnum {
    // 0- 姓名
    FULL_NAME(0, "姓名"),

    // 1- 字
    COURTESY_NAME(1, "字"),

    // 2- 号
    LITERARY_NAME(2, "号"),

    // 3- 别名
    ALIAS(3, "别名"),

    // 4- 笔名
    PEN_NAME(4, "笔名"),

    // 5- 艺名
    STAGE_NAME(5, "艺名"),

    // 6- 网名
    NET_NAME(6, "网名");

    private Integer code;
    private String desc;

    static Map<Integer,TitleTypeEnum> enumMap=new HashMap<>();
    static{
        for(TitleTypeEnum type:TitleTypeEnum.values()){
            enumMap.put(type.getCode(), type);
        }
    }

    TitleTypeEnum(int code,String desc) {
        this.code=code;
        this.desc=desc;
    }

    @Override
    public Integer getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public static TitleTypeEnum getEnum(int code) {
        return enumMap.get(code);
    }
}
