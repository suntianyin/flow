package com.apabi.flow.nlcmarc.controller;

import com.apabi.flow.nlcmarc.model.ApabiBookMetadataAuthor;
import com.apabi.flow.nlcmarc.model.NlcBookMarc;
import com.apabi.flow.nlcmarc.service.ApabiBookMetadataAuthorService;
import com.apabi.flow.nlcmarc.service.NlcBookMarcService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * @Author pipi
 * @Date 2018/9/26 17:23
 **/
@Controller
@RequestMapping("/nlcAuthor")
public class ApabiBookMetadataAuthorController {
    @Autowired
    private ApabiBookMetadataAuthorService apabiBookMetadataAuthorService;
    @Autowired
    private NlcBookMarcService nlcBookMarcService;
    private static int pageSize = 10000;

    @RequestMapping("/parse")
    public String insert() {
        int totalCount = nlcBookMarcService.getTotalCount();
        int pageNum = totalCount / pageSize + 1;
        for (int i = 1; i <= pageNum; i++) {
            PageHelper.startPage(i, pageSize);
            Page<NlcBookMarc> page = nlcBookMarcService.findByPage();
            for(NlcBookMarc nlcBookMarc:page){
                List<ApabiBookMetadataAuthor> apabiBookMetadataAuthorList = apabiBookMetadataAuthorService.parseAuthor(nlcBookMarc.getIsoContent());
                for(ApabiBookMetadataAuthor apabiBookMetadataAuthor:apabiBookMetadataAuthorList){
                    apabiBookMetadataAuthorService.insert(apabiBookMetadataAuthor);
                }
            }
        }
        return "";
    }
}