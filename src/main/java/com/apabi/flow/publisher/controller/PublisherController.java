package com.apabi.flow.publisher.controller;

import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.douban.util.StringToolUtil;
import com.apabi.flow.publisher.model.Publisher;
import com.apabi.flow.publisher.service.PublisherService;
import com.apabi.flow.thematic.util.ReadExcelUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
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
import java.util.Map;

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
    @RequestMapping("/index")
    public String getPublisherMessage(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNum,
                                      Model model) {
        try {
            long start = System.currentTimeMillis();
            PageHelper.startPage(pageNum, DEFAULT_PAGESIZE);
            Page<Publisher> page = publisherService.queryPage();
            if (page != null && !page.isEmpty()) {
                model.addAttribute("publisherList", page.getResult());
                model.addAttribute("pages", page.getPages());
                model.addAttribute("pageNum", page.getPageNum());
                model.addAttribute("pageSize", DEFAULT_PAGESIZE);
                model.addAttribute("total", page.getTotal());
            } else {
                model.addAttribute("publisherList", Collections.emptyList());
                model.addAttribute("pages", 1);
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
            log.warn("Exception {}", e);
        }
        return null;
    }

    @PostMapping("/dosave")
    public String addPublisherMessage(HttpServletRequest request, @RequestBody Publisher publisher) {
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
            return "redirect:/publisher/index";
        } catch (Exception e) {
            log.warn("Exception {}", e);
        }
        return null;
    }

    @GetMapping("/edit")
    public String editPublisherMessage(@RequestParam("id") String id, Model model) {
        try {
            long start = System.currentTimeMillis();
            Publisher publisher = publisherService.selectdataById(id);
            model.addAttribute("publisher", publisher);
            long end = System.currentTimeMillis();
            log.info("修改出版社信息列表数据耗时：" + (end - start) + "毫秒");
            return "publisher/editPublisher";
        } catch (Exception e) {
            log.warn("Exception {}", e);
        }
        return null;
    }

    @PostMapping("/doedit")
    public String doEditPublisherMessage(@RequestBody Publisher publisher) {
        try {
            long start = System.currentTimeMillis();
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            publisher.setUpdateTime(new Date());
            publisher.setOperator(userDetails.getUsername());
            int status = publisherService.editPublisher(publisher);
            long end = System.currentTimeMillis();
            log.info("修改出版社信息列表数据耗时：" + (end - start) + "毫秒");
            return "redirect:/publisher/index";
        } catch (Exception e) {
            log.warn("Exception {}", e);
        }
        return null;
    }

    // 从excel中导入数据到数据库
    @RequestMapping("/doImport")
    public void importFromExcel() {
        ReadExcelUtils readExcelUtils = new ReadExcelUtils("C:\\Users\\pirui\\Desktop\\import.xlsx");
        Map<Integer, Map<Object, Object>> data = readExcelUtils.getData();
        for (Map.Entry<Integer, Map<Object, Object>> entry : data.entrySet()) {
            Publisher publisher = new Publisher();
            publisher.setId(UUIDCreater.nextId());
            Map<Object, Object> value = entry.getValue();
            for (Map.Entry<Object, Object> dataEntry : value.entrySet()) {
                if ("出版社".equalsIgnoreCase(dataEntry.getKey().toString())) {
                    String title = dataEntry.getValue().toString();
                    publisher.setTitle(title);
                }
                if ("起止时间".equalsIgnoreCase(dataEntry.getKey().toString())) {
                    String date = dataEntry.getValue().toString();
                    String[] split = date.split("-");
                    if (split.length == 2) {
                        String startTime = split[0];
                        String endTime = split[1];
                        if (StringUtils.isNotEmpty(startTime)) {
                            startTime = StringToolUtil.issuedDateFormat(startTime);
                            startTime = startTime.replaceAll(" 00:00:00", "");
                        }
                        if (StringUtils.isNotEmpty(endTime)) {
                            endTime = StringToolUtil.issuedDateFormat(endTime);
                            endTime = endTime.replaceAll(" 00:00:00", "");
                        }
                        publisher.setStartDate(startTime);
                        publisher.setEndDate(endTime);
                    } else {
                        String startTime = split[0];
                        if (StringUtils.isNotEmpty(startTime)) {
                            startTime = StringToolUtil.issuedDateFormat(startTime);
                            startTime = startTime.replaceAll(" 00:00:00", "");
                        }
                        publisher.setStartDate(startTime);
                    }
                }
                if ("ISBN".equalsIgnoreCase(dataEntry.getKey().toString())) {
                    String isbn = dataEntry.getValue().toString();
                    publisher.setIsbn(isbn);
                }
                if ("出版地".equalsIgnoreCase(dataEntry.getKey().toString())) {
                    String place = dataEntry.getValue().toString();
                    publisher.setPlace(place);
                }
                if ("简介".equalsIgnoreCase(dataEntry.getKey().toString())) {
                    String summary = dataEntry.getValue().toString();
                    publisher.setSummary(summary);
                }
            }
            publisher.setCreateTime(new Date());
            publisher.setUpdateTime(new Date());
            publisherService.addPubliser(publisher);
        }

    }
}
