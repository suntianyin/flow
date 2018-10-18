package com.apabi.flow.nlcmarc.util;

import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.nlcmarc.model.ApabiBookMetadataAuthor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author pipi
 * @Date 2018/8/10 14:44
 **/
public class ParseMarcAuthorUtil {
    /*
    表示分隔含义的十六进制字符：
        0D CR (carriage return) 回车键
        0E SO (shift out) 不用切换
        1D GS (group separator) 分组符
        1E RS (record separator) 记录分隔符
        1F US (unit separator) 单元分隔符

        父字段之间 -> 记录分隔符
        父字段中子字段之间 -> 单元分隔符
     */
    // 表示分隔含义的字符数组
    private static final char[] noUse = {0x1E, 0x1D, 0x0D, 0x0A};
    // 记录分隔符
    private static final char recordSeparator = 0x1E;
    // 单元分隔符
    private static final char unitSeparator = 0x1F;

    public static ApabiBookMetadataAuthor parseNlcBookMarc(String nlcBookContent) throws IOException {
        ApabiBookMetadataAuthor apabiBookMetadataAuthor = new ApabiBookMetadataAuthor();
        // 将读取的字符串以记录分隔符拆分
        String[] fieldList = nlcBookContent.split(String.valueOf(recordSeparator));
        // 头部信息和偏移量
        String header = fieldList[0];
        // Marc数据中前24位为头部信息，24位到末尾为字段编号+每个字段的偏移量
        String moves = header.substring(24, header.length());

        // 创建字段编号数组
        List<String> headerList = new ArrayList<>();
        for (int j = 0; j < moves.length(); j += 12) {
            // 获取字段编号
            String move = moves.substring(j, j + 3);
            // 将字段编号添加到数组中
            headerList.add(move);
        }

        String authorType = "";
        String name = "";
        String pinyin = "";
        String originalName = "";
        String nlcMarcId = "";
        String addition = "";
        String priority = "";

        // Marc信息已经以记录分隔符拆分父字段，对拆分完毕的父字段内容进行解析
        for (int i = 1; i < fieldList.length - 1; i++) {

            // authorType
            if ("701".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "4")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "4"));
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    authorType += split[1].substring(0, index) + ",";
                } else {
                    authorType += split[1] + ",";
                }
            }
            apabiBookMetadataAuthor.setAuthorType(authorType);

            // name
            if ("701".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "a")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "a"));
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    name += split[1].substring(0, index) + ",";
                } else {
                    name += split[1] + ",";
                }
            }
            apabiBookMetadataAuthor.setName(name);

            // pinyin
            if ("701".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "9")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "9"));
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    pinyin += split[1].substring(0, index) + ",";
                } else {
                    pinyin += split[1] + ",";
                }
            }

            // originalName
            if ("701".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "g")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "g"));
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    originalName += split[1].substring(0, index) + ",";
                } else {
                    originalName += split[1] + ",";
                }
            }

            // nclMarcIdentifier
            if ("001".equals(headerList.get(i - 1))) {
                nlcMarcId = fieldList[i];
            }

            // addition
            if ("701".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "c")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "c"));
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    addition += split[1].substring(0, index) + ",";
                } else {
                    addition += split[1] + ",";
                }
            }
            // type
            // metaId
            // apabiAuthorID
            // nlcAuthorID
            // priority

        }
        if (authorType.endsWith(",")) {
            authorType = authorType.substring(0, authorType.length() - 1);
        }
        if (name.endsWith(",")) {
            name = name.substring(0, name.length() - 1);
        }
        if (pinyin.endsWith(",")) {
            pinyin = pinyin.substring(0, pinyin.length() - 1);
        }
        if (originalName.endsWith(",")) {
            originalName = originalName.substring(0, originalName.length() - 1);
        }
        if (nlcMarcId.endsWith(",")) {
            nlcMarcId = nlcMarcId.substring(0, nlcMarcId.length() - 1);
        }
        if (addition.endsWith(",")) {
            addition = addition.substring(0, addition.length() - 1);
        }
        if (name.endsWith(",")) {
            name = name.substring(0, name.length() - 1);
            if (name.contains(",")) {
                int length = name.split(",").length;
                for (int i = 1; i <= length; i++) {
                    priority = "" + i;
                }
            } else {
                priority = "1";
            }
        }

        System.out.println("authorType:" + authorType);
        System.out.println("name:" + name);
        System.out.println("pinyin:" + pinyin);
        System.out.println("originalName:" + originalName);
        System.out.println("nlcMarcId:" + nlcMarcId);
        System.out.println("addition:" + addition);
        System.out.println("priority:" + priority);

        // id
        apabiBookMetadataAuthor.setId(UUIDCreater.nextId());
        // operator
//        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
//        String username = userDetails.getUsername();
//        apabiBookMetadataAuthor.setOperator(username);
        // createTime
        apabiBookMetadataAuthor.setCreateTime(new Date());
        // updateTime
        apabiBookMetadataAuthor.setUpdateTime(new Date());
        return apabiBookMetadataAuthor;
    }
}
