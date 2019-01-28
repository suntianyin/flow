package com.apabi.flow.book.util;

import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;

/**
 * @author guanpp
 * @date 2018/8/2 13:56
 * @description
 */
public class DateUtil {

    public static String DEFAULT_DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";

    /**
     * 获取指定天数后的日期
     */
    public static Date getDateAfterDays(Date date, int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.DATE, calendar.get(Calendar.DATE) + days);
        return calendar.getTime();
    }

    public static String formatDate(Date date) {
        return formatDate(date, null);
    }

    public static String formatDate(Date date, String format) {
        if (format == null) {
            format = DEFAULT_DATE_FORMAT;
        }
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    //匹配出版社
    public static String getPublishDate(String date) {
        if (StringUtils.isNotBlank(date)) {
            Matcher matcher = BookConstant.REG_PUBLISH_DATE.matcher(date);
            if (matcher.find()) {
                return matcher.group();
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String date = "2019-01";
        date = getPublishDate(date);
        System.out.println("----" + date);
    }
}
