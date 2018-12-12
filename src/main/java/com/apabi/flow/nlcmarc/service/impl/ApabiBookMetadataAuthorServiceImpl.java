package com.apabi.flow.nlcmarc.service.impl;

import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.nlcmarc.dao.ApabiBookMetadataAuthorDao;
import com.apabi.flow.nlcmarc.model.ApabiBookMetadataAuthor;
import com.apabi.flow.nlcmarc.service.ApabiBookMetadataAuthorService;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author pipi
 * @Date 2018/9/26 16:56
 **/
@Service
public class ApabiBookMetadataAuthorServiceImpl implements ApabiBookMetadataAuthorService {
    private Logger log = LoggerFactory.getLogger(ApabiBookMetadataAuthorServiceImpl.class);
    // 表示分隔含义的字符数组
    private static final char[] NO_USE = {0x1E, 0x1D, 0x0D, 0x0A};
    // 记录分隔符
    private static final char RECORD_SEPARATOR = 0x1E;
    // 单元分隔符
    private static final char UNIT_SEPARATOR = 0x1F;

    @Autowired
    private ApabiBookMetadataAuthorDao apabiBookMetadataAuthorDao;

    @Override
    public void insert(ApabiBookMetadataAuthor apabiBookMetadataAuthor) {
        apabiBookMetadataAuthorDao.insert(apabiBookMetadataAuthor);
    }

    @Override
    public ApabiBookMetadataAuthor findById(String id) {
        ApabiBookMetadataAuthor apabiBookMetadataAuthor = null;
        if (StringUtils.isNotEmpty(id)) {
            apabiBookMetadataAuthor = apabiBookMetadataAuthorDao.findById(id);
        }
        return apabiBookMetadataAuthor;
    }

    @Override
    public List<ApabiBookMetadataAuthor> parseAuthor(String nlcBookContent) {
        // 作者列表
        List<ApabiBookMetadataAuthor> apabiBookMetadataAuthorList = new ArrayList<ApabiBookMetadataAuthor>();
        // 字段列表
        List<String> filedContentList = new ArrayList<>();
        try {
            // 将读取的字符串以记录分隔符拆分
            String[] fieldList = nlcBookContent.split(String.valueOf(RECORD_SEPARATOR));
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
            for (int i = 0; i < headerList.size(); i++) {
                String fieldContent = headerList.get(i) + "$$" + fieldList[i + 1];
                filedContentList.add(fieldContent);
            }

            // 定义公共信息
            // 国图marc信息
            String nlcMarcIdentifier = "";
            // 图书id
            String metaId = "";
            // 优先级
            Integer priority = 0;
            /*// ISBN：用来根据ISBN查询数据库获得metaId
            String isbn = "";*/
            for (String fieldContent : filedContentList) {
                // 解析国图marc信息
                if (fieldContent.startsWith("001$$")) {
                    String[] split = fieldContent.split("\\$\\$");
                    nlcMarcIdentifier = split[1];
                }

                /*// 解析ISBN，并获取metaId
                if (fieldContent.startsWith("010$$")) {
                    isbn = parseFieldContent(fieldContent, "a").replaceAll("-", "");
                    // 查询正式表，根据isbn获取metaId
                    ApabiBookMeta apabiBookMeta = null;
                    if (StringUtils.isEmpty(metaId)) {
                        apabiBookMeta = apabiBookMetaRepository.findApabiBookMetaByIsbn13Is(isbn);
                    }
                    if (apabiBookMeta != null) {
                        metaId = apabiBookMeta.getMetaId();
                    }
                }*/

                // 当以701开头时，解析作者信息
                if (fieldContent.startsWith("701$$")) {
                    ApabiBookMetadataAuthor apabiBookMetadataAuthor = new ApabiBookMetadataAuthor();
                    // 设置id
                    apabiBookMetadataAuthor.setId(UUIDCreater.nextId());
                    // 设置国图marc信息
                    apabiBookMetadataAuthor.setNlcMarcIdentifier(nlcMarcIdentifier);
                    // 设置图书id
                    apabiBookMetadataAuthor.setMetaId(metaId);
                    // 优先级+1
                    priority++;
                    apabiBookMetadataAuthor.setPriority(String.valueOf(priority));
                    // 解析责任者类型
                    String authorType = parseFieldContent(fieldContent, "4");
                    apabiBookMetadataAuthor.setAuthorType(authorType);
                    // 解析责任者名称
                    String name = parseFieldContent(fieldContent, "a");
                    name = name.replaceAll(",", "").replaceAll("，", "");
                    apabiBookMetadataAuthor.setName(name);
                    // 解析责任者名称拼音
                    String pinyin = parseFieldContent(fieldContent, "9");
                    apabiBookMetadataAuthor.setPinyin(pinyin);
                    // 解析责任者原名
                    String originalName = parseFieldContent(fieldContent, "g");
                    originalName = originalName.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("（", "").replaceAll("）", "");
                    apabiBookMetadataAuthor.setOriginalName(originalName);
                    // 解析附加信息
                    String addition = parseFieldContent_701c(fieldContent);
                    apabiBookMetadataAuthor.setAddition(addition);
                    // 解析年代
                    String period = parseFieldContent(fieldContent, "f");
                    // 清洗年代中的括号
                    if (period.contains("(")) {
                        period = period.replaceAll("\\(", "");
                    }
                    if (period.contains(")")) {
                        period = period.replaceAll("\\)", "");
                    }
                    apabiBookMetadataAuthor.setPeriod(period);
                    // 设置类型
                    apabiBookMetadataAuthor.setType("1");
                    // 设置操作人
                    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    String username = userDetails.getUsername();
                    apabiBookMetadataAuthor.setOperator(username);
                    // 设置更新时间
                    apabiBookMetadataAuthor.setUpdateTime(new Date());
                    // 设置创建时间
                    apabiBookMetadataAuthor.setCreateTime(new Date());
                    apabiBookMetadataAuthorList.add(apabiBookMetadataAuthor);
                }

                // 当以702开头时，解析作者信息
                if (fieldContent.startsWith("702$$")) {
                    ApabiBookMetadataAuthor apabiBookMetadataAuthor = new ApabiBookMetadataAuthor();
                    // 设置id
                    apabiBookMetadataAuthor.setId(UUIDCreater.nextId());
                    // 设置国图marc信息
                    apabiBookMetadataAuthor.setNlcMarcIdentifier(nlcMarcIdentifier);
                    // 设置图书id
                    apabiBookMetadataAuthor.setMetaId(metaId);
                    // 优先级+1
                    priority++;
                    apabiBookMetadataAuthor.setPriority(String.valueOf(priority));
                    // 解析责任者类型
                    String authorType = parseFieldContent(fieldContent, "4");
                    apabiBookMetadataAuthor.setAuthorType(authorType);
                    // 解析责任者名称
                    String name = parseFieldContent(fieldContent, "a");
                    name = name.replaceAll(",", "").replaceAll("，", "");
                    apabiBookMetadataAuthor.setName(name);
                    // 解析责任者名称拼音
                    String pinyin = parseFieldContent(fieldContent, "9");
                    apabiBookMetadataAuthor.setPinyin(pinyin);
                    // 解析责任者原名
                    String originalName = parseFieldContent(fieldContent, "g");
                    originalName = originalName.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("（", "").replaceAll("）", "");
                    apabiBookMetadataAuthor.setOriginalName(originalName);
                    // 解析附加信息
                    String addition = parseFieldContent_701c(fieldContent);
                    apabiBookMetadataAuthor.setAddition(addition);
                    // 解析年代
                    String period = parseFieldContent(fieldContent, "f");
                    // 清洗年代中的括号
                    if (period.contains("(")) {
                        period = period.replaceAll("\\(", "");
                    }
                    if (period.contains(")")) {
                        period = period.replaceAll("\\)", "");
                    }
                    apabiBookMetadataAuthor.setPeriod(period);
                    // 设置类型
                    apabiBookMetadataAuthor.setType("1");
                    // 设置操作人
                    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    String username = userDetails.getUsername();
                    apabiBookMetadataAuthor.setOperator(username);
                    // 设置更新时间
                    apabiBookMetadataAuthor.setUpdateTime(new Date());
                    // 设置创建时间
                    apabiBookMetadataAuthor.setCreateTime(new Date());
                    apabiBookMetadataAuthorList.add(apabiBookMetadataAuthor);
                }
                // 当以710开头时，解析团体信息
                if (fieldContent.startsWith("710$$")) {
                    ApabiBookMetadataAuthor apabiBookMetadataAuthor = new ApabiBookMetadataAuthor();
                    // 设置id
                    apabiBookMetadataAuthor.setId(UUIDCreater.nextId());
                    // 设置国图marc信息
                    apabiBookMetadataAuthor.setNlcMarcIdentifier(nlcMarcIdentifier);
                    // 设置图书id
                    apabiBookMetadataAuthor.setMetaId(metaId);
                    // 优先级+1
                    priority++;
                    apabiBookMetadataAuthor.setPriority(String.valueOf(priority));
                    // 解析责任者类型
                    String authorType = parseFieldContent(fieldContent, "4");
                    apabiBookMetadataAuthor.setAuthorType(authorType);
                    // 解析责任者名称
                    String name = parseFieldContent(fieldContent, "a");
                    name = name.replaceAll(",", "").replaceAll("，", "");
                    apabiBookMetadataAuthor.setName(name);
                    // 解析责任者名称拼音
                    String pinyin = parseFieldContent(fieldContent, "9");
                    apabiBookMetadataAuthor.setPinyin(pinyin);
                    // 解析责任者原名
                    String originalName = parseFieldContent(fieldContent, "g");
                    originalName = originalName.replaceAll("\\(", "").replaceAll("\\)", "").replaceAll("（", "").replaceAll("）", "");
                    apabiBookMetadataAuthor.setOriginalName(originalName);
                    // 解析附加信息
                    String addition = parseFieldContent_701c(fieldContent);
                    apabiBookMetadataAuthor.setAddition(addition);
                    // 解析年代
                    String period = parseFieldContent(fieldContent, "f");
                    // 清洗年代中的括号
                    if (period.contains("(")) {
                        period = period.replaceAll("\\(", "");
                    }
                    if (period.contains(")")) {
                        period = period.replaceAll("\\)", "");
                    }
                    apabiBookMetadataAuthor.setPeriod(period);
                    // 设置类型
                    apabiBookMetadataAuthor.setType("1");
                    // 设置操作人
                    UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                    String username = userDetails.getUsername();
                    apabiBookMetadataAuthor.setOperator(username);
                    // 设置更新时间
                    apabiBookMetadataAuthor.setUpdateTime(new Date());
                    // 设置创建时间
                    apabiBookMetadataAuthor.setCreateTime(new Date());
                    apabiBookMetadataAuthorList.add(apabiBookMetadataAuthor);
                }
            }
            log.info(nlcBookContent + "处理作者信息成功...");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(nlcBookContent + "处理作者信息失败...原因为" + e.getMessage());
        }
        return apabiBookMetadataAuthorList;
    }

    /**
     * 根据$a $b .... 解析子字段中的内容
     *
     * @param fieldContent
     * @param childInfo
     * @return
     */
    private String parseFieldContent(String fieldContent, String childInfo) {
        String fieldValue = "";
        if (fieldContent.contains(UNIT_SEPARATOR + childInfo)) {
            String[] split = fieldContent.split(String.valueOf(UNIT_SEPARATOR + childInfo));
            if (split.length > 1) {
                if (split[1].contains(String.valueOf(UNIT_SEPARATOR))) {
                    int index = split[1].indexOf(String.valueOf(UNIT_SEPARATOR));
                    fieldValue = split[1].substring(0, index);
                    return fieldValue;
                } else {
                    fieldValue = split[1];
                    return fieldValue;
                }
            }
        }
        return fieldValue;
    }

    /**
     * 701 $c字段特殊处理
     *
     * @param fieldContent
     * @return
     */
    private String parseFieldContent_701c(String fieldContent) {
        String fieldValue = "";
        String[] cFieldContentList = fieldContent.split(String.valueOf(UNIT_SEPARATOR));
        for (String cFieldContent : cFieldContentList) {
            if (cFieldContent.startsWith("c")) {
                cFieldContent = cFieldContent.substring(1, cFieldContent.length());
                fieldValue += (UNIT_SEPARATOR + "c" + cFieldContent);
            }
        }
        return fieldValue;
    }



    /*// 统计信息
    public List<ApabiBookMetadataAuthor> parseAuthor2(String nlcBookContent, Integer index) {
        Integer count = 0;
        // 作者列表
        List<ApabiBookMetadataAuthor> apabiBookMetadataAuthorList = new ArrayList<ApabiBookMetadataAuthor>();
        // 字段列表
        List<String> filedContentList = new ArrayList<>();
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
        for (int i = 0; i < headerList.size(); i++) {
            String fieldContent = headerList.get(i) + "$$" + fieldList[i + 1];
            filedContentList.add(fieldContent);
            if (fieldContent.startsWith("712")) {
                count++;
            }
        }
        if (count > 0) {
            System.out.println("第" + index + "共有" + count + "个712");
        }
        return null;
    }*/


}
