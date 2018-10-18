package com.apabi.flow.publisher.controller;

import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.publisher.model.Publisher;
import com.apabi.flow.publisher.service.PublisherService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.Page;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Date;

/**
 * Created by wuji on 2018/8/10.
 */

@Controller
@RequestMapping(value = "/publisher")
public class PublisherController {

    public static final Integer DEFAULT_PAGESIZE = 10;

    private Logger log = LoggerFactory.getLogger(PublisherController.class);

    @Autowired
    PublisherService publisherService;

    //出版社展示信息
    @RequestMapping("/")
    public String getPublisherMessage( @RequestParam(value = "page", required = false, defaultValue = "1")Integer pageNum,
                                       @RequestParam(value = "pageSize", required = false, defaultValue = "10")Integer pageSize,
                                        Model model) {
        try {
            long start = System.currentTimeMillis();

            PageHelper.startPage(pageNum, pageSize);
            Page<Publisher> page = publisherService.queryPage();
            if (page != null && !page.isEmpty()){
                model.addAttribute("publisherList", page.getResult());
                model.addAttribute("pages",page.getPages());
                model.addAttribute("pageNum", page.getPageNum());
                model.addAttribute("pageSize", pageSize);
                model.addAttribute("total", page.getTotal());
            }else {
                model.addAttribute("publisherList", Collections.emptyList());
                model.addAttribute("pages",1);
                model.addAttribute("pageNum", 1);
            }
            long end = System.currentTimeMillis();
            log.info("出版社信息列表查询耗时：" + (end - start) + "毫秒");
            return "publisher/publisher";
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
        return null;
    }

    @GetMapping("/save")
    public String PublisherMessage(HttpServletRequest request, Model model) {
        try {
            return "publisher/addPublisher";
        } catch (Exception e) {
            log.warn("Exception {}" , e);
        }
        return null;
    }

    @PostMapping("/dosave")
    public String addPublisherMessage(HttpServletRequest request,@RequestBody Publisher publisher) {
        try {
            long start = System.currentTimeMillis();
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Date date = new Date();
            publisher.setId(UUIDCreater.nextId());
            publisher.setCreateTime(date);
            publisher.setOperator(userDetails.getUsername());
            int status = publisherService.addPubliser(publisher);
            long end = System.currentTimeMillis();
            log.info("添加出版社信息数据耗时：" + (end - start) + "毫秒");
            return "redirect:/publisher/";
        } catch (Exception e) {
            log.warn("Exception {}" , e);
        }
        return null;
    }

    @GetMapping ("/edit")
    public String editPublisherMessage(@RequestParam("id") String id,Model model) {
        try {
            long start = System.currentTimeMillis();
            Publisher publisher = publisherService.selectdataById(id);
            model.addAttribute("publisher",publisher);
            long end = System.currentTimeMillis();
            log.info("修改出版社信息列表数据耗时：" + (end - start) + "毫秒");
            return "publisher/editPublisher";
        } catch (Exception e) {
            log.warn("Exception {}" , e);
        }
        return null;
    }

    @PostMapping ("/doedit")
    public String doEditPublisherMessage(@RequestBody Publisher publisher) {
        try {
            long start = System.currentTimeMillis();
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            publisher.setUpdateTime(new Date());
            publisher.setOperator(userDetails.getUsername());
            int status = publisherService.editPublisher(publisher);
            long end = System.currentTimeMillis();
            log.info("修改出版社信息列表数据耗时：" + (end - start) + "毫秒");
            return "redirect:/publisher/";
        } catch (Exception e) {
            log.warn("Exception {}" , e);
        }
        return null;
    }
}
