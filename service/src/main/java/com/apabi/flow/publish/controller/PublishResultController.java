package com.apabi.flow.publish.controller;

import com.apabi.flow.publish.model.PublishResult;
import com.apabi.flow.publish.service.PublishResultService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Map;

/**
 * @Author pipi
 * @Date 2018/9/14 11:12
 **/
@Controller
@RequestMapping("/publishResult")
public class PublishResultController {
    public static final Integer DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private PublishResultService publishResultService;

    @RequestMapping("/index")
    public String index(HttpServletRequest request,Model model,@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNum){
        Map<String, String[]> params = request.getParameterMap();
        PageHelper.startPage(pageNum, DEFAULT_PAGE_SIZE);
        Page<PublishResult> page = publishResultService.findPublishResultByPage(params);
        if (page != null && !page.isEmpty()) {
            model.addAttribute("publishResultList", page.getResult());
            model.addAttribute("pages", page.getPages());
            model.addAttribute("pageNum", page.getPageNum());
        } else {
            model.addAttribute("publishResultList", Collections.emptyList());
            model.addAttribute("pages", 1);
            model.addAttribute("pageNum", 1);
        }
        model.addAttribute("page", page);
        return "/publish/publishResultIndex";
    }
}
