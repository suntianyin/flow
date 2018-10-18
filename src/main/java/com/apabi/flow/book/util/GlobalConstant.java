package com.apabi.flow.book.util;

import java.io.IOException;

/**
 * @author guanpp
 * @date 2018/8/2 13:30
 * @description
 */
public class GlobalConstant {

    public static String BOOK_DETAIL_URL;

    public static String SERVER_SOLR_URL;

    static {
        try {
            SERVER_SOLR_URL = PropertiesUtil.getValue("server.solr.url");
            BOOK_DETAIL_URL = PropertiesUtil.get("book.detail.url");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
