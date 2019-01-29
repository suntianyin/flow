package com.apabi.flow.nlcmarc.controller;

import com.apabi.flow.nlcmarc.model.ApabiBookMetadataTitle;
import com.apabi.flow.nlcmarc.model.NlcBookMarc;
import com.apabi.flow.nlcmarc.service.ApabiBookMetadataTitleService;
import com.apabi.flow.nlcmarc.service.NlcBookMarcService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Author pipi
 * @Date 2018/10/10 13:34
 **/
@Controller
@RequestMapping("/nlcTitle")
public class ApabiBookMetadataTitleController {
    private static Logger logger = LoggerFactory.getLogger(ApabiBookMetadataTitleController.class);
    private static int pageSize = 20000;

    @Autowired
    private ApabiBookMetadataTitleService apabiBookMetadataTitleService;
    @Autowired
    private NlcBookMarcService nlcBookMarcService;

    @RequestMapping("/parse")
    public String parse() {
        // 获取nlcBookMarc表中数据个数
        int nlcBookMarcTotalCount = nlcBookMarcService.getTotalCount();
        // 获取到批次
        int pageNum = (nlcBookMarcTotalCount / pageSize) + 1;
        for (int i = 1; i <= pageNum; i++) {
            PageHelper.startPage(i, pageSize);
            Page<NlcBookMarc> nlcBookMarcPage = nlcBookMarcService.findByPage();
            for (NlcBookMarc nlcBookMarc : nlcBookMarcPage) {
                try {
                    String nlcMarcIdentifier = nlcBookMarc.getNlcMarcId();
                    List<ApabiBookMetadataTitle> apabiBookMetadataTitleList = apabiBookMetadataTitleService.findByNlcMarcIdentifier(nlcMarcIdentifier);
                    if (apabiBookMetadataTitleList == null || apabiBookMetadataTitleList.size() == 0) {
                        ApabiBookMetadataTitle apabiBookMetadataTitle = apabiBookMetadataTitleService.parseTitle(nlcBookMarc.getIsoContent());
                        apabiBookMetadataTitleService.insert(apabiBookMetadataTitle);
                        System.out.println("正在解析：" + apabiBookMetadataTitle);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "nlc解析题名结束";
    }
}
