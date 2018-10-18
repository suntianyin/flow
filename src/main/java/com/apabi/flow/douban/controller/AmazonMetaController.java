package com.apabi.flow.douban.controller;

import com.apabi.flow.common.ResultEntity;
import com.apabi.flow.douban.model.AmazonMeta;
import com.apabi.flow.douban.service.AmazonMetaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author pipi
 * @Date 2018/9/4 11:02
 **/
@Controller
@RequestMapping("/bookmeta")
public class AmazonMetaController {
    private Logger logger = LoggerFactory.getLogger(AmazonMetaController.class);

    @Autowired
    private AmazonMetaService amazonMetaService;

    @GetMapping("/amazon/{isbn}")
    @ResponseBody
    public ResultEntity getAmazonByIsbn(@PathVariable("isbn") String isbn) {
        ResultEntity entity = new ResultEntity();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            AmazonMeta amazonMeta = amazonMetaService.findOrCrawlAmazonMetaByIsbn(isbn);
            if (amazonMeta == null) {
                entity.setBody(null);
                entity.setStatus(404);
                entity.setMsg("Amazon没有找到该图书");
                String time = simpleDateFormat.format(new Date());
                logger.error(time + "-" + isbn + "-" + "没有找到该图书");
            } else {
                entity.setBody(amazonMeta);
                entity.setStatus(200);
                entity.setMsg("查询成功");
                String time = simpleDateFormat.format(new Date());
                logger.info(time + "-" + isbn + "-" + "查询成功");
            }
        } catch (NullPointerException e) {
            entity.setBody(null);
            entity.setStatus(404);
            entity.setMsg("Amazon没有找到该图书");
            String time = simpleDateFormat.format(new Date());
            logger.error(time + "-" + isbn + "-" + "没有找到该图书");
        } catch (Exception e) {
            entity.setBody(null);
            entity.setStatus(500);
            entity.setMsg("爬取过程出现异常");
            String time = simpleDateFormat.format(new Date());
            logger.error(time + "-" + isbn + "-" + "爬取过程出现异常");
        }
        return entity;
    }

    @RequestMapping("/update")
    public void testUpdate() {
        amazonMetaService.updateAmazon(null);
    }
}
