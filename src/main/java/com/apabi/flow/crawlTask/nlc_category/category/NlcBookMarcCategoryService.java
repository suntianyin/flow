package com.apabi.flow.crawlTask.nlc_category.category;

import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.nlcmarc.dao.NlcBookMarcDao;
import com.apabi.flow.nlcmarc_category.dao.NlcBookMarcCategoryDao;
import com.apabi.flow.nlcmarc_category.model.NlcBookMarcCategory;
import com.apabi.flow.nlcmarc_category.util.CrawlNlcMarcCategoryUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * @Author pipi
 * @Date 2018/11/28 15:11
 **/
@Controller
@RequestMapping("/nlcCategory")
public class NlcBookMarcCategoryService {

    @Autowired
    private NlcBookMarcCategoryDao nlcBookMarcCategoryDao;
    @Autowired
    private NlcBookMarcDao nlcBookMarcDao;

    /**
     * 手动访问该链接，执行国图分类抓取
     *
     * @return
     */
    @RequestMapping("/crawl")
    @ResponseBody
    public String categoryParse() {
        List<NlcBookMarcCategory> categoryList = nlcBookMarcCategoryDao.findCategoryMoreThan2PagesAndNotCrawled();
        for (int i = 0; i < categoryList.size(); i++) {
            final NlcIpPoolUtils nlcIpPoolUtils = new NlcIpPoolUtils();
            try {
                CrawlNlcMarcCategoryUtil.crawlNlcBookMarcByCategoryCode(categoryList.get(i).getId(), categoryList.get(i).getPage(), nlcIpPoolUtils, nlcBookMarcDao);
                nlcBookMarcCategoryDao.update(categoryList.get(i).setStatus("1"));
            } catch (Exception e) {
                nlcBookMarcCategoryDao.update(categoryList.get(i).setStatus("0"));
                e.printStackTrace();
            }
        }
        return "complete";
    }
}
