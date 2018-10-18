package com.apabi.flow.nlcmarc.controller;

import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.nlcmarc.model.ApabiBookSeries;
import com.apabi.flow.nlcmarc.model.ApabiBookSeriesData;
import com.apabi.flow.nlcmarc.model.NlcBookMarc;
import com.apabi.flow.nlcmarc.service.ApabiBookSeriesDataService;
import com.apabi.flow.nlcmarc.service.ApabiBookSeriesService;
import com.apabi.flow.nlcmarc.service.NlcBookMarcService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Author pipi
 * @Date 2018/10/10 17:02
 **/
@Controller
@RequestMapping("/nlcSeriesData")
public class ApabiBookSeriesDataController {
    private static Logger logger = LoggerFactory.getLogger(ApabiBookSeriesDataController.class);
    private static int pageSize = 10000;
    @Autowired
    private ApabiBookSeriesDataService apabiBookSeriesDataService;
    @Autowired
    private ApabiBookSeriesService apabiBookSeriesService;
    @Autowired
    private NlcBookMarcService nlcBookMarcService;


    @RequestMapping("/parse")
    public void parse() {
        // 获取nlc_book_marc表中数据总数
        int totalCount = nlcBookMarcService.getTotalCount();
        // 获取页数
        int pageNum = totalCount / pageSize + 1;
        for (int i = 1; i <= pageNum; i++) {
            PageHelper.startPage(i, pageSize);
            Page<NlcBookMarc> page = nlcBookMarcService.findByPage();
            for (NlcBookMarc nlcBookMarc : page) {
                try {
                    List<ApabiBookSeriesData> apabiBookSeriesDataList = apabiBookSeriesDataService.parse(nlcBookMarc.getIsoContent());
                    for (ApabiBookSeriesData apabiBookSeriesData : apabiBookSeriesDataList) {
                        // 如果丛书数据列表不为空，则尝试将丛书数据信息插入到apabi_book_series表中
                        if (apabiBookSeriesDataList != null && apabiBookSeriesDataList.size() > 0) {
                            List<String> titles = apabiBookSeriesService.findAllTitles();
                            // 以标题作为判断依据，决定是否将该数据插入到apabi_book_series表中
                            if (!titles.contains(apabiBookSeriesData.getSeriesTitle())) {
                                ApabiBookSeries apabiBookSeries = new ApabiBookSeries();
                                apabiBookSeries.setTitle(apabiBookSeriesData.getSeriesTitle());
                                apabiBookSeries.setDataSource("nlcMarc");
                                UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                                String username = userDetails.getUsername();
                                apabiBookSeries.setOperator(username);
                                apabiBookSeries.setCreateTime(new Date());
                                apabiBookSeries.setUpdateTime(new Date());
                                apabiBookSeries.setId(UUIDCreater.nextId());
                                apabiBookSeriesService.insert(apabiBookSeries);
                                logger.info(apabiBookSeries+"插入成功...");
                            }
                        }
                        // 将数据插入到apabi_book_series_data表中
                        apabiBookSeriesDataService.insert(apabiBookSeriesData);
                        logger.info(apabiBookSeriesData+"插入成功...");
                    }
                } catch (IOException e) {
                    logger.error(nlcBookMarc+"解析失败...");
                    e.printStackTrace();
                }
            }
        }
    }
}
