package com.apabi.flow.common;

import cn.org.rapid_framework.page.PageRequest;

/**
 * @author guanpp
 * @date 2018/8/3 10:36
 * @description
 */
public class BaseQuery extends PageRequest implements java.io.Serializable {
    private static final long serialVersionUID = -360860474471966681L;
    public static final int DEFAULT_PAGE_SIZE = 20;

    static {
        System.out.println("BaseQuery.DEFAULT_PAGE_SIZE=" + DEFAULT_PAGE_SIZE);
    }

    public BaseQuery() {
        setPageSize(DEFAULT_PAGE_SIZE);
    }
}
