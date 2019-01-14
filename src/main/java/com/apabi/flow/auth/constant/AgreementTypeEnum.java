package com.apabi.flow.auth.constant;

import com.apabi.flow.common.api.BaseEnum;
import com.apabi.flow.common.api.CodeBaseEnum;

import java.util.HashMap;
import java.util.Map;

public enum AgreementTypeEnum implements BaseEnum<AgreementTypeEnum, Integer>,CodeBaseEnum {
    E_BOOK(0, "电子书"),
    YEARBOOK(1, "年鉴"),
    REFERENCE_BOOKS(2, "工具书"),
    FEATURED_RESOURCE(3, "特色资源"),
    AUTHOR_AGREEMENT(4, "作者签约协议"),
    NEWSPAPER(5, "报纸"),
    GALLERY(6, "图片库");



    private Integer code;
    private String desc;

    static Map<Integer, AgreementTypeEnum> enumMap=new HashMap<>();
    static{
        for(AgreementTypeEnum type: AgreementTypeEnum.values()){
            enumMap.put(type.getCode(), type);
        }
    }

    AgreementTypeEnum(Integer code, String desc) {
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

    public static AgreementTypeEnum getEnum(int code){
        return enumMap.get(code);
    }
}
