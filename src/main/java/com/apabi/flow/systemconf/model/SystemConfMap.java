package com.apabi.flow.systemconf.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: sunty
 * @date: 2018/11/07 10:38
 * @description:
 */

public class SystemConfMap {
    public final static Map map = new HashMap<String, String>() {{
        put("id", "编号");
        put("confName", "配置名称");
        put("confKey", "配置键");
        put("confValue", "配置值");
        put("description", "内容描述");
        put("createTime", "记录创建时间");
        put("operator", "记录创建人");
        put("updateTime", "记录修改时间");
    }};
}
