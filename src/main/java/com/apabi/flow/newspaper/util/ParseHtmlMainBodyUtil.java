package com.apabi.flow.newspaper.util;

import com.apabi.flow.newspaper.model.Newspaper;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @Author pipi
 * @Date 2018/11/9 10:11
 **/
public class ParseHtmlMainBodyUtil {
    public static String parse(Newspaper newspaper) {
        String htmlContent = newspaper.getHtmlContent();
        String text = null;
        if (newspaper.getUrl().contains("cnr.cn")) {
            // 对央广网解析
            Document document = Jsoup.parse(htmlContent);
            text = document.select("div[class='Custom_UnionStyle']").text();
            if(StringUtils.isEmpty(text)){
               text = document.select("div[class='TRS_Editor']").text();
            }
            if(StringUtils.isEmpty(text)){
                text = document.select("div[class='btn']").text();
            }
        } else if (newspaper.getUrl().contains("www.chinanews.com")) {
            // 对中新网解析
            Document document = Jsoup.parse(htmlContent);
            text = document.select("div[class='left_zw']").text();
            if (StringUtils.isEmpty(text)) {
                text = document.select("strong").text();
            }
        }else {
            // 对中国日报解析
            Document document = Jsoup.parse(htmlContent);
            text = document.select("div[class='article']").text();
            if (StringUtils.isEmpty(text)) {
                text = document.select("div[class='arcBox']").text();
            }
            if (StringUtils.isEmpty(text)) {
                text = document.select("div[class='datu-a']").text();
            }
        }
        if (text != null) {
            text = text.trim();
        }
        if (StringUtils.isEmpty(text) || text.length() < 20) {
            text = null;
        }
        return text;
    }
}
