package com.apabi.flow.nlcmarc.util;

import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.nlcmarc.model.ApabiBookSeriesData;
import org.apache.commons.beanutils.BeanUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author pipi
 * @Date 2018/8/10 14:44
 **/
public class ParseMarcSeriesDataUtil {
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

    public static List<ApabiBookSeriesData> parseMarcTitle(String nlcBookContent) throws IOException {
        List<ApabiBookSeriesData> apabiBookSeriesDataList = new ArrayList<ApabiBookSeriesData>();
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
// ------------------------------------------------------------------ //

        // 获取225字段出现的次数
    /*
        int count225Field = 0;
        for (String headerField : headerList) {
            if ("225".equals(headerField)) {
                count225Field++;
            }
        }
        if (count225Field > 2) {
            System.out.println(nlcBookContent);
        }
    */

// ------------------------------------------------------------------ //

        // 建立一个公用属性bean，将除了225字段的其他信息填充到commonBean中。
        ApabiBookSeriesData commonBean = new ApabiBookSeriesData();
        // Marc信息已经以记录分隔符拆分父字段，对拆分完毕的父字段内容进行解析
        for (int i = 1; i < fieldList.length - 1; i++) {
            // nlcMarcIdentifier
            if ("001".equals(headerList.get(i - 1))) {
                String nlcmarcid = fieldList[i];
                commonBean.setNlcMarcIdentifier(nlcmarcid);
            }

            // isbn
            if ("010".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "a")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "a"));
                String isbn = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    isbn = split[1].substring(0, index);
                } else {
                    isbn = split[1];
                }
                commonBean.setIsbn(isbn);
            }

            // metaId
            commonBean.setMetaId(null);

            // relationId
            commonBean.setRelationId(null);

            // title
            if ("200".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "a")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "a"));
                String title = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    title = split[1].substring(0, index);
                } else {
                    title = split[1];
                }
                commonBean.setTitle(title);
            }

            // author
            if ("200".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "f")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "f"));
                String author = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    author = split[1].substring(0, index);
                } else {
                    author = split[1];
                }
                commonBean.setAuthor(author);
            }
        }
        commonBean.setCreateTime(new Date());
        commonBean.setUpdateTime(new Date());

        for (int i = 1; i < fieldList.length - 1; i++) {
            if ("225".equals(headerList.get(i - 1))) {
                ApabiBookSeriesData apabiBookSeriesData = new ApabiBookSeriesData();
                try {
                    // 将commonBean中的属性拷贝到apabiBookSeriesData中
                    BeanUtils.copyProperties(apabiBookSeriesData, commonBean);
                    // id
                    apabiBookSeriesData.setId(UUIDCreater.nextId());
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }

                // seriesTitle
                if (fieldList[i].contains(unitSeparator + "a")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "a"));
                    String seriesTitle = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        seriesTitle = split[1].substring(0, index);
                    } else {
                        seriesTitle = split[1];
                    }
                    apabiBookSeriesData.setSeriesTitle(seriesTitle);
                }

                // parallelSeriesTitle
                if (fieldList[i].contains(unitSeparator + "d")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "d"));
                    String parallelSeriesTitle = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        parallelSeriesTitle = split[1].substring(0, index);
                    } else {
                        parallelSeriesTitle = split[1];
                    }
                    apabiBookSeriesData.setParallelSeriesTitle(parallelSeriesTitle);
                }

                // seriesSubTitle
                if (fieldList[i].contains(unitSeparator + "e")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "e"));
                    String seriesSubTitle = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        seriesSubTitle = split[1].substring(0, index);
                    } else {
                        seriesSubTitle = split[1];
                    }
                    apabiBookSeriesData.setSeriesSubTitle(seriesSubTitle);
                }

                // seriesAuthor
                if (fieldList[i].contains(unitSeparator + "f")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "f"));
                    String seriesAuthor = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        seriesAuthor = split[1].substring(0, index);
                    } else {
                        seriesAuthor = split[1];
                    }
                    apabiBookSeriesData.setSeriesAuthor(seriesAuthor);
                }

                // volume
                if (fieldList[i].contains(unitSeparator + "h")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "h"));
                    String volume = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        volume = split[1].substring(0, index);
                    } else {
                        volume = split[1];
                    }
                    apabiBookSeriesData.setVolume(volume);
                }

                // volumeTitle
                if (fieldList[i].contains(unitSeparator + "i")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "i"));
                    String volumeTitle = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        volumeTitle = split[1].substring(0, index);
                    } else {
                        volumeTitle = split[1];
                    }
                    apabiBookSeriesData.setVolumeTitle(volumeTitle);
                }

                // volumeId
                if (fieldList[i].contains(unitSeparator + "v")) {
                    String[] split = fieldList[i].split(String.valueOf(unitSeparator + "v"));
                    String volumeId = "";
                    if (split[1].contains(String.valueOf(unitSeparator))) {
                        int index = split[1].indexOf(String.valueOf(unitSeparator));
                        volumeId = split[1].substring(0, index);
                    } else {
                        volumeId = split[1];
                    }
                    apabiBookSeriesData.setVolumeId(volumeId);
                }

                // updateTime
                apabiBookSeriesData.setUpdateTime(new Date());

                // createTime
                apabiBookSeriesData.setCreateTime(new Date());
                // 把解析好的数据添加到list中
                apabiBookSeriesDataList.add(apabiBookSeriesData);
            }
        }
        return apabiBookSeriesDataList;
    }
}
