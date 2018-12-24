package com.apabi.flow.nlcmarc.util;

import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.subject.model.ApabiBookMetadataSubject;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author pipi
 * @Date 2018/8/10 14:44
 **/
public class ParseMarcSubjectUtil {
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

    /**
     * 从iso文件中解析出来subject主题信息
     *
     * @param nlcBookContent
     * @return
     * @throws IOException
     */
    public static ApabiBookMetadataSubject parseNlcBookMarcSubject(String nlcBookContent) {
        ApabiBookMetadataSubject apabiBookMetadataSubject = null;
        if (StringUtils.isNotEmpty(nlcBookContent)) {
            apabiBookMetadataSubject = new ApabiBookMetadataSubject();
            // 设置id
            apabiBookMetadataSubject.setId(UUIDCreater.nextId());
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

                // nlcMarcId
                if ("001".equals(headerList.get(i - 1))) {
                    String nlcMarcId = fieldList[i];
                    apabiBookMetadataSubject.setNlcMarcIdentifier(nlcMarcId);
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
                    apabiBookMetadataSubject.setTitle(title);
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
                    apabiBookMetadataSubject.setAuthor(author);
                }

                // 款目要素
                if ("600".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "a")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "a"));
                    String topic600 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        topic600 = split[1].substring(0, index);
                    } else {
                        topic600 = split[1];
                    }
                    apabiBookMetadataSubject.setTopic600(topic600);
                }

                // 名称其余部分
                if ("600".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "b")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "b"));
                    String other600 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        other600 = split[1].substring(0, index);
                    } else {
                        other600 = split[1];
                    }
                    apabiBookMetadataSubject.setOther600(other600);
                }

                // 首字母
                if ("600".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "g")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "g"));
                    String firstLetter600 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        firstLetter600 = split[1].substring(0, index);
                    } else {
                        firstLetter600 = split[1];
                    }
                    apabiBookMetadataSubject.setFirstLetter600(firstLetter600);
                }

                // 年代
                if ("600".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "f")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "f"));
                    String time600 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        time600 = split[1].substring(0, index);
                    } else {
                        time600 = split[1];
                    }
                    apabiBookMetadataSubject.setFirstLetter600(time600);
                }

                // 形式复分
                if ("600".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "j")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "j"));
                    String form600 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        form600 = split[1].substring(0, index);
                    } else {
                        form600 = split[1];
                    }
                    apabiBookMetadataSubject.setForm600(form600);
                }

                // 论题复分
                if ("600".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "x")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "x"));
                    String period600 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        period600 = split[1].substring(0, index);
                    } else {
                        period600 = split[1];
                    }
                    apabiBookMetadataSubject.setPeriod600(period600);
                }

                // 年代复分
                if ("600".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "z")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "z"));
                    String period600 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        period600 = split[1].substring(0, index);
                    } else {
                        period600 = split[1];
                    }
                    apabiBookMetadataSubject.setPeriod600(period600);
                }

                // 任职机构
                if ("600".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "p")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "p"));
                    String organization600 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        organization600 = split[1].substring(0, index);
                    } else {
                        organization600 = split[1];
                    }
                    apabiBookMetadataSubject.setOrganization600(organization600);
                }

                // 系统代码
                if ("600".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "2")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "2"));
                    String code600 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        code600 = split[1].substring(0, index);
                    } else {
                        code600 = split[1];
                    }
                    apabiBookMetadataSubject.setCode600(code600);
                }

                // 款目要素
                if ("601".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "a")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "a"));
                    String topic601 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        topic601 = split[1].substring(0, index);
                    } else {
                        topic601 = split[1];
                    }
                    apabiBookMetadataSubject.setTopic601(topic601);
                }

                // 次级部分
                if ("601".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "b")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "b"));
                    String other601 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        other601 = split[1].substring(0, index);
                    } else {
                        other601 = split[1];
                    }
                    apabiBookMetadataSubject.setOther601(other601);
                }

                // 会议届次
                if ("601".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "d")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "d"));
                    String meeting601 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        meeting601 = split[1].substring(0, index);
                    } else {
                        meeting601 = split[1];
                    }
                    apabiBookMetadataSubject.setMeeting601(meeting601);
                }

                // 会议地点
                if ("601".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "e")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "e"));
                    String meetingPlace601 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        meetingPlace601 = split[1].substring(0, index);
                    } else {
                        meetingPlace601 = split[1];
                    }
                    apabiBookMetadataSubject.setMeetingPlace601(meetingPlace601);
                }

                // 会议日期
                if ("601".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "f")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "f"));
                    String meetingPlaceDate601 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        meetingPlaceDate601 = split[1].substring(0, index);
                    } else {
                        meetingPlaceDate601 = split[1];
                    }
                    apabiBookMetadataSubject.setMeetingDate601(meetingPlaceDate601);
                }

                // 形式复分
                if ("601".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "j")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "j"));
                    String form601 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        form601 = split[1].substring(0, index);
                    } else {
                        form601 = split[1];
                    }
                    apabiBookMetadataSubject.setForm601(form601);
                }

                // 论题复分
                if ("601".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "x")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "x"));
                    String subject601 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        subject601 = split[1].substring(0, index);
                    } else {
                        subject601 = split[1];
                    }
                    apabiBookMetadataSubject.setSubject601(subject601);
                }

                // 年代复分
                if ("601".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "z")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "z"));
                    String period601 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        period601 = split[1].substring(0, index);
                    } else {
                        period601 = split[1];
                    }
                    apabiBookMetadataSubject.setPeriod601(period601);
                }

                // 系统代码
                if ("601".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "2")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "2"));
                    String code601 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        code601 = split[1].substring(0, index);
                    } else {
                        code601 = split[1];
                    }
                    apabiBookMetadataSubject.setCode601(code601);
                }

                // 款目要素
                if ("604".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "a")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "a"));
                    String topic604 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        topic604 = split[1].substring(0, index);
                    } else {
                        topic604 = split[1];
                    }
                    apabiBookMetadataSubject.setTopic604(topic604);
                }

                // 论题复分
                if ("604".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "x")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "x"));
                    String subject604 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        subject604 = split[1].substring(0, index);
                    } else {
                        subject604 = split[1];
                    }
                    apabiBookMetadataSubject.setSubject604(subject604);
                }

                // 系统代码
                if ("604".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "2")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "2"));
                    String code604 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        code604 = split[1].substring(0, index);
                    } else {
                        code604 = split[1];
                    }
                    apabiBookMetadataSubject.setCode604(code604);
                }

                // 款目要素
                if ("605".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "a")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "a"));
                    String topic605 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        topic605 = split[1].substring(0, index);
                    } else {
                        topic605 = split[1];
                    }
                    apabiBookMetadataSubject.setTopic605(topic605);
                }

                // 论题复分
                if ("605".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "x")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "x"));
                    String subject605 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        subject605 = split[1].substring(0, index);
                    } else {
                        subject605 = split[1];
                    }
                    apabiBookMetadataSubject.setSubject605(subject605);
                }

                // 系统代码
                if ("605".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "2")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "2"));
                    String code605 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        code605 = split[1].substring(0, index);
                    } else {
                        code605 = split[1];
                    }
                    apabiBookMetadataSubject.setCode605(code605);
                }

                // 款目要素
                if ("606".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "a")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "a"));
                    String topic606 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        topic606 = split[1].substring(0, index);
                    } else {
                        topic606 = split[1];
                    }
                    apabiBookMetadataSubject.setTopic606(topic606);
                }

                // 论题复分
                if ("606".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "x")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "x"));
                    String subject606 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        subject606 = split[1].substring(0, index);
                    } else {
                        subject606 = split[1];
                    }
                    apabiBookMetadataSubject.setSubject606(subject606);
                }

                // 年代复分
                if ("606".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "z")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "z"));
                    String period606 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        period606 = split[1].substring(0, index);
                    } else {
                        period606 = split[1];
                    }
                    apabiBookMetadataSubject.setPeriod606(period606);
                }

                // 地理复分
                if ("606".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "y")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "y"));
                    String geographical606 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        geographical606 = split[1].substring(0, index);
                    } else {
                        geographical606 = split[1];
                    }
                    apabiBookMetadataSubject.setGeographical606(geographical606);
                }

                // 形式复分
                if ("606".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "j")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "j"));
                    String form606 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        form606 = split[1].substring(0, index);
                    } else {
                        form606 = split[1];
                    }
                    apabiBookMetadataSubject.setForm606(form606);
                }

                // 款目要素
                if ("607".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "a")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "a"));
                    String topic607 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        topic607 = split[1].substring(0, index);
                    } else {
                        topic607 = split[1];
                    }
                    apabiBookMetadataSubject.setTopic607(topic607);
                }

                // 论题复分
                if ("607".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "x")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "x"));
                    String subject607 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        subject607 = split[1].substring(0, index);
                    } else {
                        subject607 = split[1];
                    }
                    apabiBookMetadataSubject.setSubject607(subject607);
                }

                // 地理复分
                if ("607".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "y")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "y"));
                    String subject607 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        subject607 = split[1].substring(0, index);
                    } else {
                        subject607 = split[1];
                    }
                    apabiBookMetadataSubject.setSubject607(subject607);
                }

                // 系统代码
                if ("607".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "2")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "2"));
                    String code607 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        code607 = split[1].substring(0, index);
                    } else {
                        code607 = split[1];
                    }
                    apabiBookMetadataSubject.setCode607(code607);
                }

                // 款目要素
                if ("608".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "a")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "a"));
                    String topic608 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        topic608 = split[1].substring(0, index);
                    } else {
                        topic608 = split[1];
                    }
                    apabiBookMetadataSubject.setTopic608(topic608);
                }

                // 论题复分
                if ("608".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "x")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "x"));
                    String subject608 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        subject608 = split[1].substring(0, index);
                    } else {
                        subject608 = split[1];
                    }
                    apabiBookMetadataSubject.setSubject608(subject608);
                }

                // 年代复分
                if ("608".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "z")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "z"));
                    String period608 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        period608 = split[1].substring(0, index);
                    } else {
                        period608 = split[1];
                    }
                    apabiBookMetadataSubject.setPeriod608(period608);
                }

                // 系统代码
                if ("608".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "2")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "2"));
                    String code608 = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        code608 = split[1].substring(0, index);
                    } else {
                        code608 = split[1];
                    }
                    apabiBookMetadataSubject.setCode608(code608);
                }
            }
            // 创建时间
            apabiBookMetadataSubject.setCreateTime(new Date());
            // 更新时间
            apabiBookMetadataSubject.setUpdateTime(new Date());
        }
        return apabiBookMetadataSubject;
    }

    public static void main(String[] args) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream("C:\\Users\\pirui\\Desktop\\繁体.iso"),"GBK"));
        String line = bufferedReader.readLine();
        ApabiBookMetadataSubject apabiBookMetadataSubject = ParseMarcSubjectUtil.parseNlcBookMarcSubject(line);
        System.out.println(apabiBookMetadataSubject);
    }
}
