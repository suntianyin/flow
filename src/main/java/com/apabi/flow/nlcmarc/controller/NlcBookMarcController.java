package com.apabi.flow.nlcmarc.controller;

import com.apabi.flow.nlcmarc.model.ApabiBookMetadataAuthor;
import com.apabi.flow.nlcmarc.model.NlcBookMarc;
import com.apabi.flow.nlcmarc.service.ApabiBookMetadataAuthorService;
import com.apabi.flow.nlcmarc.service.NlcBookMarcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.*;
import java.util.Date;
import java.util.List;

/**
 * @Author pipi
 * @Date 2018/9/13 18:31
 **/
@Controller
@RequestMapping("/nlcBookMarc")
public class NlcBookMarcController {
    private Logger logger = LoggerFactory.getLogger(NlcBookMarcController.class);
    @Autowired
    NlcBookMarcService nlcBookMarcService;
    @Autowired
    private ApabiBookMetadataAuthorService apabiBookMetadataAuthorService;

    @RequestMapping("/parse")
    public void parse() throws IOException {
        String filePath = "C:\\Users\\pirui\\Desktop\\需要解析的marc数据\\total - 副本.iso";
        String charSet = "GBK";
        // 解析marc数据
        List<NlcBookMarc> nlcBookMarcList = nlcBookMarcService.parseNlcBookMarc(filePath, charSet);
        for (NlcBookMarc nlcBookMarc : nlcBookMarcList) {
            try {
                nlcBookMarcService.insertNlcBookMarc(nlcBookMarc);
                logger.info("处理成功：" + nlcBookMarc);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("处理失败：" + nlcBookMarc + ",原因为：" + e.getMessage());
            }
        }
        // 解析marc数据中作者信息
        BufferedReader reader2 = new BufferedReader(new InputStreamReader(new FileInputStream(new File(filePath)), charSet));
        String line2 = null;
        while ((line2 = reader2.readLine()) != null) {
            List<ApabiBookMetadataAuthor> apabiBookMetadataAuthorList = apabiBookMetadataAuthorService.parseAuthor(line2);
            for (ApabiBookMetadataAuthor apabiBookMetadataAuthor : apabiBookMetadataAuthorList) {
                try {
                    apabiBookMetadataAuthorService.insert(apabiBookMetadataAuthor);
                    logger.info("处理成功：" + apabiBookMetadataAuthor);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("处理失败：" + apabiBookMetadataAuthor + ",原因为：" + e.getMessage());
                }
            }
        }
        reader2.close();
    }

    @RequestMapping("/update")
    public void update() throws IOException {
        String filePath = "C:\\Users\\pirui\\Desktop\\需要解析的marc数据\\total - 副本.iso";
        String charSet = "GBK";
        // 解析marc数据
        List<NlcBookMarc> nlcBookMarcList = nlcBookMarcService.parseNlcBookMarc(filePath, charSet);
        for (NlcBookMarc nlcBookMarc : nlcBookMarcList) {
            // 设置创建时间
            Date createTime = nlcBookMarc.getCreateTime();
            if (createTime != null) {
                nlcBookMarc.setCreateTime(createTime);
            } else {
                nlcBookMarc.setCreateTime(new Date());
            }
            // 设置更新时间
            nlcBookMarc.setUpdateTime(new Date());
            try {
                nlcBookMarcService.updateNlcBookMarc(nlcBookMarc);
                logger.info("更新成功：" + nlcBookMarc);
            } catch (Exception e) {
                e.printStackTrace();
                logger.error("更新失败：" + nlcBookMarc + ",原因为：" + e.getMessage());
            }
        }
    }



    /*@RequestMapping("/parse")
    public String insert() throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(new File("C:\\Users\\pirui\\Desktop\\需要解析的marc数据\\total - 副本.iso")), "GBK"));
        String line = null;
        while ((line = reader.readLine()) != null) {
            List<ApabiBookMetadataAuthor> apabiBookMetadataAuthorList = apabiBookMetadataAuthorService.parseAuthor(line);
            for (ApabiBookMetadataAuthor apabiBookMetadataAuthor : apabiBookMetadataAuthorList) {
                apabiBookMetadataAuthorService.insert(apabiBookMetadataAuthor);
            }
        }
        return "";
    }*/

}
