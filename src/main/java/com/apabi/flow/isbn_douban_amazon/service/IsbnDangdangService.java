package com.apabi.flow.isbn_douban_amazon.service;

/**
 * @Author pipi
 * @Date 2019-1-18 11:07
 **/
public interface IsbnDangdangService {
    void crawlDouban();
    void crawlAmazon();
    void crawlNlc();
}
