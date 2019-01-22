package com.apabi.maker;

import com.apabi.flow.processing.service.impl.BibliothecaServiceImpl;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author guanpp
 * @date 2019/1/8 9:18
 * @description
 */
public class MakerUtil {

    private static final Logger logger = LoggerFactory.getLogger(MakerUtil.class);

    //生成maker的job文件
    public static Map createJobXml(String dirPath,
                                   String targetPath,
                                   String jobPath,
                                   Map<String, String[]> fileMap) {
        if (StringUtils.isNotBlank(dirPath)
                && StringUtils.isNotBlank(targetPath)
                && StringUtils.isNotBlank(jobPath)
                && fileMap != null) {
            long start = System.currentTimeMillis();
            logger.info("生成路径{}的job文件已开始", dirPath);
            //遍历fileMap
            if (fileMap.size() > 0) {
                //List<String> jobList = new ArrayList<>();
                Map<String, String> jobMap = new HashMap<>();
                //创建单个文件任务节点
                for (Map.Entry entry : fileMap.entrySet()) {
                    jobMap.put((String) entry.getKey(), "");
                    Document doc = DocumentHelper.createDocument();
                    //创建根节点
                    Element root = doc.addElement("Convert");
                    Element cebItem = root.addElement("CEBItem");
                    cebItem.addAttribute("Type", "true");
                    Element cebFile = cebItem.addElement("CEBFile");
                    Element psFile1 = cebItem.addElement("PSFile");
                    Element pageNumberStart = cebItem.addElement("PageNumberStart");
                    pageNumberStart.setText("1");
                    Element pageNumberEnd = cebItem.addElement("PageNumberEnd");
                    pageNumberEnd.setText("-1");
                    Element psItem = root.addElement("PSItem");
                    Element psFile2 = psItem.addElement("PSFile");
                    Element jobParameter = psItem.addElement("JobParameter");
                    jobParameter.setText("CEBX");
                    //获取文件信息
                    String[] fileInfo = (String[]) entry.getValue();
                    String filePath = dirPath + File.separator + fileInfo[0];
                    //判断父文件夹是否存在
                    String cebxParentPath = targetPath + File.separator + fileInfo[1];
                    File parent = new File(cebxParentPath);
                    if (!parent.exists()) {
                        parent.mkdirs();
                    }
                    String cebxPath = cebxParentPath + File.separator + fileInfo[2] + ".CEBX";
                    cebFile.setText(cebxPath);
                    psFile1.setText(filePath);
                    psFile2.setText(filePath);
                    //生成xml文件
                    File file = new File(jobPath + File.separator + fileInfo[2] + "job.xml");
                    XMLWriter writer;
                    try {
                        //配置属性
                        OutputFormat format = OutputFormat.createPrettyPrint();
                        //设置编码格式
                        format.setEncoding("UTF-8");
                        writer = new XMLWriter(new FileOutputStream(file), format);
                        //设置是否转义，默认使用转义字符
                        writer.setEscapeText(false);
                        writer.write(doc);
                        writer.close();
                    } catch (IOException e) {
                        logger.warn("生成job文件{}时，出现异常{}", file.getPath() + e.getMessage());
                    }
                    jobMap.put((String) entry.getKey(), file.getPath());
                    logger.info("生成job文件{}", file.getPath());
                }
                long end = System.currentTimeMillis();
                logger.info("路径{}的job文件已全部生成，耗时{}", dirPath, (end - start));
                return jobMap;
            }
        }
        return null;
    }

    public static void main(String[] args) {
//        String dirPath = "C:\\Users\\guanpp\\Desktop\\test\\pdf";
//        String targetPath = "C:\\Users\\guanpp\\Desktop\\test\\cebx";
//        String jobPath = "C:\\Users\\guanpp\\Desktop\\test\\job";
//        Map<String, String[]> fileMap = new HashMap<>();
//        String[] fileInfo = new String[3];
//        fileInfo[0] = "index.pdf";
//        fileInfo[1] = "20180205";
//        fileInfo[2] = "m.201244d115dfaf";
//        fileMap.put("18201244d115dfaf", fileInfo);
//        fileInfo[0] = "linkmap.pdf";
//        fileInfo[1] = "20180205";
//        fileInfo[2] = "m.201344d115dfaf";
//        fileMap.put("18201344d115dfaf", fileInfo);
//        fileInfo[0] = "recipes.pdf";
//        fileInfo[1] = "20180203";
//        fileInfo[2] = "m.201444d115dfaf";
//        fileMap.put("18201444d115dfaf", fileInfo);
//        Map jobMap = createJobXml(dirPath, targetPath, jobPath, fileMap);
//        System.out.println(jobMap.toString());
        String f = "1/ddd.pdf";
        System.out.println(f.substring(0, f.lastIndexOf("/")));
    }
}
