package com.apabi.flow.processing.controller;

import com.apabi.flow.processing.dao.EncryptMapper;
import com.apabi.flow.processing.dao.EncryptResourceMapper;
import com.apabi.flow.processing.model.Encrypt;
import com.apabi.flow.processing.model.EncryptResource;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/processing/encrypt")
public class EncryptController {

    private static final Logger logger = LoggerFactory.getLogger(EncryptController.class);
    @Autowired
    private EncryptMapper encryptMapper;
    @Autowired
    private EncryptResourceMapper encryptResourceMapper;
    @RequestMapping("/index")
    public String index(
                        @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNum,
                        @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                        Model model) {
        try {
            long start = System.currentTimeMillis();
            PageHelper.startPage(pageNum, pageSize);
            Page<Encrypt> page = encryptMapper.pageFind();
            DecimalFormat df=new DecimalFormat("0.00");
            List<Encrypt> collect = page.getResult().stream().map(item -> {
                if (item.getFinishNum() != 0) {
                    item.setPre(Double.valueOf(df.format((double)item.getFinishNum()/item.getEncryptNum())));
                } else {
                    item.setPre(0.00);
                }
                return item;
            }).collect(Collectors.toList());
            model.addAttribute("pages", page.getPages());
            model.addAttribute("pageNum", page.getPageNum());
            model.addAttribute("pageSize", pageSize);
            model.addAttribute("total", page.getTotal());
            model.addAttribute("encryptList", collect);
            logger.info("加密任务查询耗时： {}", System.currentTimeMillis() - start);
        } catch (Exception e) {
            logger.error("Exception {}", e);
        }
        return "processing/encrypt";
    }
    @RequestMapping("/encryptResourceIndex")
    public String encryptResource(@RequestParam(value = "id") String id,@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNum,
                                  @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,Model model) {
        long start = System.currentTimeMillis();
        PageHelper.startPage(pageNum, pageSize);
        Page<EncryptResource> page = encryptResourceMapper.selectByEncryptId(id);
        model.addAttribute("pages", page.getPages());
        model.addAttribute("pageNum", page.getPageNum());
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("total", page.getTotal());
        model.addAttribute("encryptResourceList", page.getResult());
        logger.info("加密任务详情查询查询耗时： {}", System.currentTimeMillis() - start);
        return "processing/encryptResource";
    }

}
