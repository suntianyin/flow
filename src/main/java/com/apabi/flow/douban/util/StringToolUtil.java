package com.apabi.flow.douban.util;

import org.apache.commons.lang.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by pipi on 2018/8/7.
 */
public class StringToolUtil {
    public static String timeFormat(String data) {
        String date = "";
        String importFileRole = "(\\d{6,8})";
        if (StringUtils.isNotBlank(data)) {
            if (data.substring(0, 1).toCharArray()[0] >= 'A' && data.substring(0, 1).toCharArray()[0] <= 'Z') {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy", Locale.US);
                TimeZone tz = TimeZone.getTimeZone("GMT+8");
                sdf.setTimeZone(tz);
                String str = sdf.format(Calendar.getInstance().getTime());
                System.out.println(str);
                Date s;
                try {
                    s = sdf.parse(data.replace(",", ""));
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                    return sdf.format(s);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                Pattern p = Pattern.compile(importFileRole);
                Matcher m = p.matcher(data);//进行匹配
                if (m.find()) {//判断正则表达式是否匹配到
                    if (m.group(1).length() == 8) {
                        date = m.group(1).substring(0, 4) + "-" + m.group(1).substring(4, 6) + "-" + m.group(1).substring(6, 8) + "-";
                    } else if (m.group(1).length() == 6) {
                        date = m.group(1).substring(0, 4) + "-" + m.group(1).substring(4, 6) + "-";
                    }
                } else {
                    importFileRole = "(\\d{0,4})[\\S\\s]?(\\d{0,2})[\\S\\s]?(\\d{0,2})";
                    p = Pattern.compile(importFileRole);
                    m = p.matcher(data);//进行匹配
                    if (m.find()) {//判断正则表达式是否匹配到
                        for (int i = 1; i <= m.groupCount(); i++) {
                            if (!m.group(i).equals("")) {
                                if (m.group(i).length() < 2) {
                                    date += "0" + m.group(i) + "-";
                                } else {
                                    date += m.group(i) + "-";//通过group来获取每个分组的值，group(0)代表正则表达式匹配到的所有内容，1代表第一个分组
                                }
                            }
                        }
                    }
                }
                try {
                    date = date.substring(0, date.length() - 1);
                } catch (StringIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                return date;
            }
        }
        return null;
    }

    public static String metaidFormat(String data) {
        String date = "";
        String importFileRole = "(\\d{6,8})";
        if (StringUtils.isNotBlank(data)) {
            if (data.substring(0, 1).toCharArray()[0] >= 'A' && data.substring(0, 1).toCharArray()[0] <= 'Z') {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy", Locale.US);
                TimeZone tz = TimeZone.getTimeZone("GMT+8");
                sdf.setTimeZone(tz);
                String str = sdf.format(Calendar.getInstance().getTime());
                System.out.println(str);
                Date s;
                try {
                    s = sdf.parse(data.replace(",", ""));
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                    date = sdf.format(s).replace("-", "");
                    return "m." + date + System.currentTimeMillis();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                Pattern p = Pattern.compile(importFileRole);
                Matcher m = p.matcher(data);//进行匹配
                if (m.find()) {//判断正则表达式是否匹配到
                    if (m.group(1).length() == 8) {
                        date = m.group(1);
                    } else if (m.group(1).length() == 6) {
                        date = m.group(1) + "01";
                    }
                } else {
                    importFileRole = "(\\d{0,4})[\\S\\s]?(\\d{0,2})[\\S\\s]?(\\d{0,2})";
                    p = Pattern.compile(importFileRole);
                    m = p.matcher(data);//进行匹配
                    if (m.find()) {//判断正则表达式是否匹配到
                        for (int i = 1; i <= m.groupCount(); i++) {
                            if (!m.group(i).equals("")) {
                                if (m.group(i).length() < 2) {
                                    date += "0" + m.group(i);
                                } else {
                                    date += m.group(i);//通过group来获取每个分组的值，group(0)代表正则表达式匹配到的所有内容，1代表第一个分组
                                }
                            }
                        }
                    }
                }
                if (date.length() == 7) {
                    date = date + "-01";
                }
                if (date.length() == 4) {
                    date = date + "-01-01";
                }
                return "m." + date + System.currentTimeMillis();
            }

        }
        return null;
    }

    public static String issuedDateFormat(String data) {
        String date = "";
        String importFileRole = "(\\d{6,8})";
        if (StringUtils.isNotBlank(data)) {
            if (data.substring(0, 1).toCharArray()[0] >= 'A' && data.substring(0, 1).toCharArray()[0] <= 'Z') {
                SimpleDateFormat sdf = new SimpleDateFormat("MMM dd yyyy", Locale.US);
                TimeZone tz = TimeZone.getTimeZone("GMT+8");
                sdf.setTimeZone(tz);
                String str = sdf.format(Calendar.getInstance().getTime());
                System.out.println(str);
                Date s;
                try {
                    s = sdf.parse(data.replace(",", ""));
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String result = sdf.format(s);
                    if (result.length() == 7) {
                        result += "-01 00:00:00";
                    } else if (result.length() == 10) {
                        result += " 00:00:00";
                    }
                    return result;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                Pattern p = Pattern.compile(importFileRole);
                Matcher m = p.matcher(data);//进行匹配
                if (m.find()) {//判断正则表达式是否匹配到
                    if (m.group(1).length() == 8) {
                        date = m.group(1).substring(0, 4) + "-" + m.group(1).substring(4, 6) + "-" + m.group(1).substring(6, 8) + "-";
                    } else if (m.group(1).length() == 6) {
                        date = m.group(1).substring(0, 4) + "-" + m.group(1).substring(4, 6) + "-";
                    }
                } else {
                    importFileRole = "(\\d{0,4})[\\S\\s]?(\\d{0,2})[\\S\\s]?(\\d{0,2})";
                    p = Pattern.compile(importFileRole);
                    m = p.matcher(data);//进行匹配
                    if (m.find()) {//判断正则表达式是否匹配到
                        for (int i = 1; i <= m.groupCount(); i++) {
                            if (!m.group(i).equals("")) {
                                if (m.group(i).length() < 2) {
                                    date += "0" + m.group(i) + "-";
                                } else {
                                    date += m.group(i) + "-";//通过group来获取每个分组的值，group(0)代表正则表达式匹配到的所有内容，1代表第一个分组
                                }
                            }
                        }
                    }
                }
                try {
                    date = date.substring(0, date.length() - 1);
                } catch (StringIndexOutOfBoundsException e) {
                    e.printStackTrace();
                }
                if (date.length() == 7) {
                    date += "-01 00:00:00";
                } else if (date.length() == 10) {
                    date += " 00:00:00";
                }

                if (date.length() == 4) {
                    date += "-01-01";
                }
                return date;
            }
        }
        return null;
    }

    public static void main(String[] args) {
//        String result = metaidFormat("sdfsd");
//        System.out.println(result);

        String result2 = issuedDateFormat("2018-1-1");
        System.out.println(result2);

    }
}
