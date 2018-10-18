package com.apabi.flow.nlcmarc.util;

import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.nlcmarc.model.ApabiBookMetadataTitle;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author pipi
 * @Date 2018/8/10 14:44
 **/
public class ParseMarcTitleUtil {
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

    public static ApabiBookMetadataTitle parseMarcTitle(String nlcBookContent) throws IOException {
        ApabiBookMetadataTitle apabiBookMetadataTitle = new ApabiBookMetadataTitle();
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

            // id
            apabiBookMetadataTitle.setId(UUIDCreater.nextId());

            // metaId
            apabiBookMetadataTitle.setMetaId(null);

            // nlcMarcIdentifier
            if ("001".equals(headerList.get(i - 1))) {
                String nlcmarcid = fieldList[i];
                apabiBookMetadataTitle.setNlcMarcIdentifier(nlcmarcid);
            }

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
                apabiBookMetadataTitle.setTitle(title);
            }

            // subTitle
            if ("200".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "e")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "e"));
                String subTitle = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    subTitle = split[1].substring(0, index);
                } else {
                    subTitle = split[1];
                }
                apabiBookMetadataTitle.setSubTitle(subTitle);
            }

            // volume200
            if ("200".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "h")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "h"));
                String volume200 = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    volume200 = split[1].substring(0, index);
                } else {
                    volume200 = split[1];
                }
                apabiBookMetadataTitle.setVolume200(volume200);
            }

            // volume200Title
            if ("200".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "i")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "i"));
                String volume200Title = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    volume200Title = split[1].substring(0, index);
                } else {
                    volume200Title = split[1];
                }
                apabiBookMetadataTitle.setVolume200Title(volume200Title);
            }

            // titlePinyin
            if ("200".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "9")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "9"));
                String titlePinyin = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    titlePinyin = split[1].substring(0, index).trim();
                } else {
                    titlePinyin = split[1].trim();
                }
                apabiBookMetadataTitle.setTitlePinyin(titlePinyin);
            }

            // 当225字段有多个时，取第一条
            // seriesTitle
            if ("225".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "a")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "a"));
                String seriesTitle = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    seriesTitle = split[1].substring(0, index);
                } else {
                    seriesTitle = split[1];
                }
                if (StringUtils.isEmpty(apabiBookMetadataTitle.getSeriesTitle())) {
                    apabiBookMetadataTitle.setSeriesTitle(seriesTitle);
                }
            }

            // parallelSeriesTitle
            if ("225".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "d")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "d"));
                String parallelSeriesTitle = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    parallelSeriesTitle = split[1].substring(0, index);
                } else {
                    parallelSeriesTitle = split[1];
                }
                if (StringUtils.isEmpty(apabiBookMetadataTitle.getSeriesTitle())) {
                    apabiBookMetadataTitle.setParallelSeriesTitle(parallelSeriesTitle);
                }
            }

            // seriesSubTitle
            if ("225".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "e")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "e"));
                String seriesSubTitle = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    seriesSubTitle = split[1].substring(0, index);
                } else {
                    seriesSubTitle = split[1];
                }
                if (StringUtils.isEmpty(apabiBookMetadataTitle.getSeriesTitle())) {
                    apabiBookMetadataTitle.setSeriesTitle(seriesSubTitle);
                }
            }

            // volume
            if ("225".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "h")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "h"));
                String volume = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    volume = split[1].substring(0, index);
                } else {
                    volume = split[1];
                }
                apabiBookMetadataTitle.setVolume(volume);
            }

            // volumeTitle
            if ("225".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "i")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "i"));
                String volumeTitle = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    volumeTitle = split[1].substring(0, index);
                } else {
                    volumeTitle = split[1];
                }
                if (StringUtils.isEmpty(apabiBookMetadataTitle.getSeriesTitle())) {
                    apabiBookMetadataTitle.setVolumeTitle(volumeTitle);
                }
            }

            // volume500
            if ("500".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "h")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "h"));
                String volume500 = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    volume500 = split[1].substring(0, index);
                } else {
                    volume500 = split[1];
                }
                apabiBookMetadataTitle.setVolume500(volume500);
            }

            // volume500Title
            if ("500".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "i")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "i"));
                String volume500Title = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    volume500Title = split[1].substring(0, index);
                } else {
                    volume500Title = split[1];
                }
                apabiBookMetadataTitle.setVolume500Title(volume500Title);
            }

            // uniformTitle
            if ("500".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "a")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "a"));
                String uniformTitle = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    uniformTitle = split[1].substring(0, index);
                } else {
                    uniformTitle = split[1];
                }
                apabiBookMetadataTitle.setUniformTitle(uniformTitle);
            }

            // parallelTitle
            if ("510".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "a")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "a"));
                String parallelTitle = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    parallelTitle = split[1].substring(0, index);
                } else {
                    parallelTitle = split[1];
                }
                apabiBookMetadataTitle.setParallelTitle(parallelTitle);
            }

            // coverTitle
            if ("512".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "a")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "a"));
                String coverTitle = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    coverTitle = split[1].substring(0, index);
                } else {
                    coverTitle = split[1];
                }
                apabiBookMetadataTitle.setCoverTitle(coverTitle);
            }

            // addedPageTitle
            if ("513".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "a")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "a"));
                String addedPageTitle = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    addedPageTitle = split[1].substring(0, index);
                } else {
                    addedPageTitle = split[1];
                }
                apabiBookMetadataTitle.setAddedPageTitle(addedPageTitle);
            }

            // captionTitle
            if ("514".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "a")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "a"));
                String captionTitle = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    captionTitle = split[1].substring(0, index);
                } else {
                    captionTitle = split[1];
                }
                apabiBookMetadataTitle.setCaptionTitle(captionTitle);
            }

            // runningTitle
            if ("515".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "a")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "a"));
                String runningTitle = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    runningTitle = split[1].substring(0, index);
                } else {
                    runningTitle = split[1];
                }
                apabiBookMetadataTitle.setRunningTitle(runningTitle);
            }

            // spineTitle
            if ("516".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "a")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "a"));
                String spineTitle = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    spineTitle = split[1].substring(0, index);
                } else {
                    spineTitle = split[1];
                }
                apabiBookMetadataTitle.setSpineTitle(spineTitle);
            }

            // otherVariantTitle
            if ("517".equals(headerList.get(i - 1)) && fieldList[i].contains(unitSeparator + "a")) {
                String[] split = fieldList[i].split(String.valueOf(unitSeparator + "a"));
                String otherVariantTitle = "";
                if (split[1].contains(String.valueOf(unitSeparator))) {
                    int index = split[1].indexOf(String.valueOf(unitSeparator));
                    otherVariantTitle = split[1].substring(0, index);
                } else {
                    otherVariantTitle = split[1];
                }
                apabiBookMetadataTitle.setOtherVariantTitle(otherVariantTitle);
            }
        }
        // operator
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = userDetails.getUsername();
        apabiBookMetadataTitle.setOperator(username);
        // createTime
        apabiBookMetadataTitle.setCreateTime(new Date());
        // updateTime
        apabiBookMetadataTitle.setUpdateTime(new Date());
        return apabiBookMetadataTitle;
    }
}
