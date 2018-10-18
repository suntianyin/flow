package com.apabi.flow.douban.util;

/**
 * @Author pipi
 * @Date 2018/9/3 10:46
 **/
public class Isbn13ToIsbnUtil {
    public static String transform(String isbn13) {
        String isbn = null;
        if (!isbn13.contains("-")) {
            if (isbn13.startsWith("97870")) {
                String part1 = isbn13.substring(0, 3);
                String part2 = isbn13.substring(3, 4);
                String part3 = isbn13.substring(4, 6);
                String part4 = isbn13.substring(6, 12);
                String part5 = isbn13.substring(12, 13);
                isbn = part1 + "-" + part2 + "-" + part3 + "-" + part4 + "-" + part5;
            }
            if (isbn13.startsWith("97871")) {
                String part1 = isbn13.substring(0, 3);
                String part2 = isbn13.substring(3, 4);
                String part3 = isbn13.substring(4, 7);
                String part4 = isbn13.substring(7, 12);
                String part5 = isbn13.substring(12, 13);
                isbn = part1 + "-" + part2 + "-" + part3 + "-" + part4 + "-" + part5;
            }
            if (isbn13.startsWith("97872")) {
                String part1 = isbn13.substring(0, 3);
                String part2 = isbn13.substring(3, 4);
                String part3 = isbn13.substring(4, 7);
                String part4 = isbn13.substring(7, 12);
                String part5 = isbn13.substring(12, 13);
                isbn = part1 + "-" + part2 + "-" + part3 + "-" + part4 + "-" + part5;
            }
            if (isbn13.startsWith("97873")) {
                String part1 = isbn13.substring(0, 3);
                String part2 = isbn13.substring(3, 4);
                String part3 = isbn13.substring(4, 7);
                String part4 = isbn13.substring(7, 12);
                String part5 = isbn13.substring(12, 13);
                isbn = part1 + "-" + part2 + "-" + part3 + "-" + part4 + "-" + part5;
            }
            if (isbn13.startsWith("97875")) {
                String part1 = isbn13.substring(0, 3);
                String part2 = isbn13.substring(3, 4);
                String part3 = isbn13.substring(4, 8);
                String part4 = isbn13.substring(8, 12);
                String part5 = isbn13.substring(12, 13);
                isbn = part1 + "-" + part2 + "-" + part3 + "-" + part4 + "-" + part5;
            }
            if (isbn13.startsWith("97878")) {
                String part1 = isbn13.substring(0, 3);
                String part2 = isbn13.substring(3, 4);
                String part3 = isbn13.substring(4, 9);
                String part4 = isbn13.substring(9, 12);
                String part5 = isbn13.substring(12, 13);
                isbn = part1 + "-" + part2 + "-" + part3 + "-" + part4 + "-" + part5;
            }
        }
        return isbn;
    }
}
