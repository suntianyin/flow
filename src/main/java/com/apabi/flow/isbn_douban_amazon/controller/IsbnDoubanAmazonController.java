package com.apabi.flow.isbn_douban_amazon.controller;

import com.apabi.flow.isbn_douban_amazon.service.IsbnDoubanAmazonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @Author pipi
 * @Date 2018-12-13 10:12
 **/
@Controller
@RequestMapping("ida")
public class IsbnDoubanAmazonController {

    @Autowired
    private IsbnDoubanAmazonService isbnDoubanAmazonService;

    @ResponseBody()
    @RequestMapping("crawl")
    public String crawl(){
        isbnDoubanAmazonService.crawl();
        return "success";
    }
}
