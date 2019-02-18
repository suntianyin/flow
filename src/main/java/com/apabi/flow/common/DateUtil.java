package com.apabi.flow.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @author guanpp
 * @date 2019/2/18 13:56
 * @description
 */
public class DateUtil {

    //计算两个日期之间相差的天数
    public static int betweenDays(Date startDate, Date endDate) {
        if (startDate != null && endDate != null) {
            if (endDate.getTime() < startDate.getTime()) {
                return -1;
            } else {
                Calendar calendar = Calendar.getInstance();

                calendar.setTime(startDate);
                int yearStart = calendar.get(Calendar.YEAR);
                int dayStart = calendar.get(Calendar.DAY_OF_YEAR);

                calendar.setTime(endDate);
                int yearEnd = calendar.get(Calendar.YEAR);
                int dayEnd = calendar.get(Calendar.DAY_OF_YEAR);
                //不同年
                if (yearStart != yearEnd) {
                    int year = 0;
                    for (int i = yearStart; i < yearEnd; i++) {
                        if ((i % 4 == 0 && i % 100 != 0) || (i % 400 == 0)) {
                            year += 366;
                        } else {
                            year += 365;
                        }
                    }
                    return year + (dayEnd - dayStart);
                } else {
                    return dayEnd - dayStart;
                }
            }
        }
        return -1;
    }

    public static void main(String[] args) throws ParseException {
        String date = "2019-02-02";
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        Date endDate = sd.parse(date);
        Date startDate = new Date();
        System.out.println("dddddd：" + betweenDays(startDate, endDate));
    }
}
