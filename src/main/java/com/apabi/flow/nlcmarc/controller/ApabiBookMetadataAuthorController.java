package com.apabi.flow.nlcmarc.controller;

import com.apabi.flow.nlcmarc.model.ApabiBookMetadataAuthor;
import com.apabi.flow.nlcmarc.model.NlcBookMarc;
import com.apabi.flow.nlcmarc.service.ApabiBookMetadataAuthorService;
import com.apabi.flow.nlcmarc.service.NlcBookMarcService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author pipi
 * @Date 2018/9/26 17:23
 **/
@RestController
@RequestMapping("/nlcAuthor")
public class ApabiBookMetadataAuthorController {
    @Autowired
    private ApabiBookMetadataAuthorService apabiBookMetadataAuthorService;
    @Autowired
    private NlcBookMarcService nlcBookMarcService;
    private static int pageSize = 20000;

    @RequestMapping("/parse")
    public String insert() {
        int totalCount = nlcBookMarcService.getTotalCount();
        int pageNum = totalCount / pageSize + 1;
        for (int i = 1; i <= pageNum; i++) {
            PageHelper.startPage(i, pageSize);
            Page<NlcBookMarc> page = nlcBookMarcService.findByPage();
            for (NlcBookMarc nlcBookMarc : page) {
                try {
                    String nlcMarcId = nlcBookMarc.getNlcMarcId();
                    List<ApabiBookMetadataAuthor> apabiBookMetadataAuthorList = apabiBookMetadataAuthorService.findByNlcMarcIdentifier(nlcMarcId);
                    if (apabiBookMetadataAuthorList == null || apabiBookMetadataAuthorList.size() == 0) {
                        List<ApabiBookMetadataAuthor> apabiBookMetadataAuthorResultList = apabiBookMetadataAuthorService.parseAuthor(nlcBookMarc.getIsoContent());
                        for (ApabiBookMetadataAuthor apabiBookMetadataAuthor : apabiBookMetadataAuthorResultList) {
                            apabiBookMetadataAuthorService.insert(apabiBookMetadataAuthor);
                            System.out.println("正在解析："+apabiBookMetadataAuthor);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return "nlc解析作者结束";
    }
}