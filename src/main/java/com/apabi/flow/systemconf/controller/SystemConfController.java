package com.apabi.flow.systemconf.controller;

import com.apabi.flow.common.CommEntity;
import com.apabi.flow.common.ResultEntity;
import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.systemconf.model.SystemConf;
import com.apabi.flow.systemconf.model.SystemConfMap;
import com.apabi.flow.systemconf.service.SystemConfService;
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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author: sunty
 * @date: 2018/11/07 10:16
 * @description:
 */
@Controller
@RequestMapping(value = "/systemConf")
public class SystemConfController {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final Integer DEFAULT_PAGESIZE = 10;
    private Logger log = LoggerFactory.getLogger(SystemConfController.class);

    @Autowired
    SystemConfService systemConfService;

    //系统参数信息
    @RequestMapping("/index")
    public String getSystemConf(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNum,
                               Model model) {
        try {
            long start = System.currentTimeMillis();
            PageHelper.startPage(pageNum, DEFAULT_PAGESIZE);
            Page<SystemConf> page = systemConfService.queryPage();
            if (page != null && !page.isEmpty()) {
                model.addAttribute("systemConfList", page.getResult());
                model.addAttribute("pages", page.getPages());
                model.addAttribute("pageNum", page.getPageNum());
                model.addAttribute("pageSize", DEFAULT_PAGESIZE);
                model.addAttribute("total", page.getTotal());
            } else {
                model.addAttribute("systemConfList", Collections.emptyList());
                model.addAttribute("pages", 1);
                model.addAttribute("pageNum", 1);
            }
            long end = System.currentTimeMillis();
            log.info("系统参数信息列表查询耗时：" + (end - start) + "毫秒");
            return "systemConf/conf";
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
        return null;
    }

    @GetMapping("/save")
    public String systemConf() {
        try {
            return "systemConf/addConf";
        } catch (Exception e) {
            log.warn("Exception {}", e);
        }
        return null;
    }

    @PostMapping("/dosave")
    public String addSystemConf(@RequestBody SystemConf systemConf) {
        try {
            long start = System.currentTimeMillis();
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Date date = new Date();
            systemConf.setId(UUIDCreater.nextId());
            systemConf.setCreateTime(date);
            systemConf.setOperator(userDetails.getUsername());
            int status = systemConfService.addSystemConf(systemConf);
            long end = System.currentTimeMillis();
            log.info("添加系统参数信息数据耗时：" + (end - start) + "毫秒");
            return "redirect:/systemConf/index";
        } catch (Exception e) {
            log.warn("Exception {}", e);
        }
        return null;
    }

    @GetMapping("/edit")
    public String editSystemConf(@RequestParam("id") String id, Model model) {
        try {
            long start = System.currentTimeMillis();
            SystemConf systemConf = systemConfService.selectdataById(id);
            model.addAttribute("systemConf", systemConf);
            long end = System.currentTimeMillis();
            log.info("修改系统参数信息列表数据耗时：" + (end - start) + "毫秒");
            return "systemConf/editConf";
        } catch (Exception e) {
            log.warn("Exception {}", e);
        }
        return null;
    }

    @PostMapping("/doedit")
    public String doEditSystemConf(@RequestBody SystemConf systemConf) {
        try {
            long start = System.currentTimeMillis();
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            systemConf.setUpdateTime(new Date());
            systemConf.setOperator(userDetails.getUsername());
            int status = systemConfService.updateSystemConf(systemConf);
            long end = System.currentTimeMillis();
            log.info("修改系统参数信息列表数据耗时：" + (end - start) + "毫秒");
            return "redirect:/systemConf/index";
        } catch (Exception e) {
            log.warn("Exception {}", e);
        }
        return null;
    }

    @GetMapping("/removeSystemConf")
    @ResponseBody
    public ResultEntity removeSystemConf(@RequestParam("id") String id){
        try{
            if (org.apache.commons.lang3.StringUtils.isBlank(id)){
                return new ResultEntity(400,"数据异常！");
            }

            if (systemConfService.deleteByPrimaryKey(id)>0){
                return new ResultEntity(200, "操作成功");
            }
            return new ResultEntity(500, "操作失败，请重新尝试或联系管理员！");
        }catch (Exception e){
            e.printStackTrace();
            return new ResultEntity(500, e.getMessage());
        }
    }

    //查看系统参数元数据详情
    @RequestMapping(value = "/systemConfShow")
    public String systemConfShow(@RequestParam("id") String id, Model model) {
        if (!StringUtils.isEmpty(id)) {
            long start = System.currentTimeMillis();
            List<CommEntity> entityList = new ArrayList<>();
            SystemConf systemConf = systemConfService.selectdataById(id);
            for (Field field : systemConf.getClass().getDeclaredFields()) {
                CommEntity commEntity = new CommEntity();
                //获取属性名
                commEntity.setFiledName(field.getName());
                //获取属性中文描述
                commEntity.setFiledDesc((String) SystemConfMap.map.get(field.getName()));
                if (systemConf != null) {
                    //获取属性值
                    Object value = getFieldValueByName(field.getName(), systemConf);
                    if (value != null) {
                        String type = value.getClass().getTypeName();
                        if (type.contains("java.util.Date")) {
                            value = sdf1.format(value);
                        }
                    }
                    commEntity.setFiledValue(value);
                }
                entityList.add(commEntity);
            }
            model.addAttribute("entityList", entityList);
            long end = System.currentTimeMillis();
            log.info("查询系统参数" + id + "耗时：" + (end - start) + "毫秒");
        }
        return "systemConf/confShow";
    }
    /*根据属性名获取属性值*/
    private static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(o, new Object[]{});
            return value;
        } catch (Exception e) {
            return null;
        }
    }
}
