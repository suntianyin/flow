package com.apabi.flow.auth.controller;

import com.apabi.flow.auth.model.CopyrightAgreement;
import com.apabi.flow.auth.model.CopyrightOwner;
import com.apabi.flow.auth.service.CopyrightOwnerService;
import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.common.util.ParamsUtils;
import com.apabi.flow.publisher.model.Publisher;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.util.*;

/**
 * 功能描述： <br>
 * <作者 controller层>
 *
 * @author supeng
 * @date 2018/8/27 16:47
 * @since 1.0.0
 */
@Controller
@RequestMapping("/copyrightOwner")
public class CopyrightOwnerController {
    private Logger log = LoggerFactory.getLogger(CopyrightOwnerController.class);

    @Autowired
    private CopyrightOwnerService copyrightOwnerService;
    @GetMapping("/index")
    public String index(@RequestParam(value = "name", required = false) String name,
                        @RequestParam(value = "pinyin", required = false) String pinyin,
                        @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNum,
                        @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                        Model model) {
        try {
            long start = System.currentTimeMillis();
            //搜索保留
            model.addAttribute("name", name);
            model.addAttribute("pinyin", pinyin);
            PageHelper.startPage(pageNum, pageSize);
            Map<String, Object> paramsMap = new HashMap<>();
            ParamsUtils.checkParameterAndPut2Map(paramsMap, "name", name, "pinyin", pinyin);
            Page<CopyrightOwner> page = copyrightOwnerService.listCopyrightOwner(paramsMap);
            if (page != null && !page.isEmpty()) {
                model.addAttribute("CopyrightOwnerList", page.getResult());
                model.addAttribute("pages", page.getPages());
                model.addAttribute("pageNum", page.getPageNum());
                model.addAttribute("total", page.getTotal());
            } else {
                model.addAttribute("CopyrightOwnerList", Collections.emptyList());
                model.addAttribute("pages", 1);
                model.addAttribute("pageNum", 1);
            }
            long end = System.currentTimeMillis();
            log.info("版权所有者信息列表查询耗时：" + (end - start) + "毫秒");
            return "auth/copyrightOwner";
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
        return "auth/copyrightOwner";
    }
    @RequestMapping("/add/index")
    public String addIndex() {
        return "/auth/addCopyrightOwner";
    }

    @RequestMapping("/deleteById")
    public String deleteById(@RequestParam String id) {
        int i = copyrightOwnerService.deleteByPrimaryKey(id);
        return "redirect:/copyrightOwner/index";
    }

    @PostMapping("/add")
    public String add(@RequestBody CopyrightOwner copyrightOwner) {
        String s = UUIDCreater.nextId();
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        copyrightOwner.setOperator(userDetails.getUsername());
        copyrightOwner.setOperateDate(new Date());
        copyrightOwner.setId(s);
        copyrightOwner.setStatus(0);
        int add = copyrightOwnerService.insert(copyrightOwner);
        return "redirect:/copyrightOwner/index";
    }
    @PostMapping("/update")
    public String update(@RequestBody CopyrightOwner copyrightOwner) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        copyrightOwner.setOperator(userDetails.getUsername());
        copyrightOwner.setOperateDate(new Date());
        int add = copyrightOwnerService.updateByPrimaryKeySelective(copyrightOwner);
        return "redirect:/copyrightOwner/index";
    }

    @RequestMapping("/edit/index")
    public String editIndex(@RequestParam String id, Model model) throws ParseException {
        CopyrightOwner copyrightOwner = null;
        if (StringUtils.isNotBlank(id)) {
            copyrightOwner = copyrightOwnerService.selectByPrimaryKey(id);
        }
        model.addAttribute("copyrightOwner", copyrightOwner);
        return "/auth/editCopyrightOwner";
    }


}
