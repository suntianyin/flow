package com.apabi.flow.nlcmarc.util;

import com.apabi.flow.douban.util.StringToolUtil;
import com.apabi.flow.nlcmarc.model.NlcBookMarc;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author pipi
 * @Date 2018/8/10 14:44
 **/
public class ParseMarcUtil {
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

    public static NlcBookMarc parseNlcBookMarc(String nlcBookContent) throws IOException {
        NlcBookMarc nlcBookMarc = new NlcBookMarc();
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

        // Marc信息已经以记录分隔符拆分父字段，对拆分完毕的父字段内容进行解析
        for (int i = 1; i < fieldList.length - 1; i++) {

            // nlcmarcid
            if ("001".equals(headerList.get(i - 1))) {
                String nlcmarcid = fieldList[i];
                nlcBookMarc.setNlcMarcId(nlcmarcid);
            }

            // 正题名
            if ("200".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "a")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "a"));
                String title = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    title = split[1].substring(0, index);
                } else {
                    title = split[1];
                }
                nlcBookMarc.setTitle(title);
            }

            // 作者
            if ("200".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "f")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "f"));
                String author = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    author = split[1].substring(0, index);
                } else {
                    author = split[1];
                }
                nlcBookMarc.setAuthor(author);
            }
            // 面向对象
            if ("100".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "a")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "a"));
                String class_ = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    class_ = split[1].substring(0, index).trim();
                } else {
                    class_ = split[1].trim();
                }
                class_ = class_.substring(17,20).trim();
                nlcBookMarc.setClass_(class_);
            }
            // ISBN
            if ("010".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "a")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "a"));
                String isbn = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    isbn = split[1].substring(0, index);
                } else {
                    isbn = split[1];
                }
                nlcBookMarc.setIsbn(isbn);
            }
            // 出版社
            if ("210".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "c")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "c"));
                String publisher = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    publisher = split[1].substring(0, index);
                } else {
                    publisher = split[1];
                }
                nlcBookMarc.setPublisher(publisher);
            }
            // 正题名拼音
            if ("200".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "9")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "9"));
                String titlePinyin = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    titlePinyin = split[1].substring(0, index);
                } else {
                    titlePinyin = split[1];
                }
                nlcBookMarc.setTitlePinyin(titlePinyin);
            }
            // 副题名、其他题名
            if ("200".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "e")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "e"));
                String subTitle = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    subTitle = split[1].substring(0, index);
                } else {
                    subTitle = split[1];
                }
                nlcBookMarc.setSubTitle(subTitle);
            }
            // 副题名、其他题名拼音
            if ("200".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "E")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "E"));
                String subTitlePinyin = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    subTitlePinyin = split[1].substring(0, index);
                } else {
                    subTitlePinyin = split[1];
                }
                nlcBookMarc.setSubTitlePinyin(subTitlePinyin);
            }
            // 主要作者拼音
            if ("200".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "F")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "F"));
                String authorPinyin = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    authorPinyin = split[1].substring(0, index);
                } else {
                    authorPinyin = split[1];
                }
                nlcBookMarc.setAuthorPinyin(authorPinyin);
            }
            // 其他责任者
            if ("200".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "g")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "g"));
                String contributor = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    contributor = split[1].substring(0, index);
                } else {
                    contributor = split[1];
                }
                nlcBookMarc.setContributor(contributor);
            }
            // 出版日期
            if ("210".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "d")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "d"));
                String issuedDate = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    issuedDate = split[1].substring(0, index);
                } else {
                    issuedDate = split[1];
                }
                // 格式化出版日期
                if (StringUtils.isNotEmpty(issuedDate)) {
                    issuedDate = StringToolUtil.issuedDateFormat(issuedDate);
                    if (issuedDate.contains(" 00:00:00")) {
                        issuedDate.replace(" 00:00:00", "");
                    }
                }
                nlcBookMarc.setIssuedDate(issuedDate);
            }
            // 相关文献
            if ("225".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "a")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "a"));
                String relation = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    relation = split[1].substring(0, index);
                } else {
                    relation = split[1];
                }
                nlcBookMarc.setRelation(relation);
            }
            // 分卷信息
            if ("200".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "h")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "h"));
                String volume = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    volume = split[1].substring(0, index);
                } else {
                    volume = split[1];
                }
                nlcBookMarc.setVolume(volume);
            }
            // 分卷名
            if ("200".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "i")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "i"));
                String volumeTitle = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    volumeTitle = split[1].substring(0, index);
                } else {
                    volumeTitle = split[1];
                }
                nlcBookMarc.setVolumeTitle(volumeTitle);
            }
            // 分卷名拼音
            if ("200".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "I")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "I"));
                String volumeTitlePinyin = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    volumeTitlePinyin = split[1].substring(0, index);
                } else {
                    volumeTitlePinyin = split[1];
                }
                nlcBookMarc.setVolumeTitlePinyin(volumeTitlePinyin);
            }
            // 卷册号标识
            if ("200".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "v")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "v"));
                String volumeId = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    volumeId = split[1].substring(0, index);
                } else {
                    volumeId = split[1];
                }
                nlcBookMarc.setVolumeId(volumeId);
            }
        }
        // 创建时间
        nlcBookMarc.setCreateTime(new Date());
        // 更新时间
        nlcBookMarc.setUpdateTime(new Date());
        // ISO文件内容
        nlcBookMarc.setIsoContent(nlcBookContent);
        return nlcBookMarc;
    }
}
