package com.apabi.flow.author.constant;

import com.apabi.flow.common.api.BaseEnum;
import com.apabi.flow.common.api.CodeBaseEnum;

import java.util.HashMap;
import java.util.Map;

/**
 * 功能描述： <br>
 * <作者类型 枚举类>
 *
 * @author supeng
 * @date 2018/8/24 15:04
 * @since 1.0.0
 */
public enum AuthorTypeEnum implements BaseEnum<AuthorTypeEnum, Integer>,CodeBaseEnum {

    PERSONAL(0, "个人"),

    ORGANIZATION(1, "团体"),

    CONFERENCE(2, "会议"),

    BUDDHIST(3, "佛道人物"),

    EMPEROR(4, "古代帝王");

    private Integer code;
    private String desc;

    static Map<Integer,AuthorTypeEnum> enumMap=new HashMap<>();
    static{
        for(AuthorTypeEnum type:AuthorTypeEnum.values()){
            enumMap.put(type.getCode(), type);
        }
    }

    AuthorTypeEnum(Integer code, String desc) {
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

    public static AuthorTypeEnum getEnum(int code){
        return enumMap.get(code);
    }
}
