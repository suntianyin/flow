package com.apabi.flow.audio.controller;

import com.apabi.flow.audio.model.AudioMeta;
import com.apabi.flow.audio.model.AudioMetaMap;
import com.apabi.flow.audio.service.AudioMetaService;
import com.apabi.flow.common.CommEntity;
import com.apabi.flow.common.ResultEntity;
import com.apabi.flow.common.UUIDCreater;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
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
 * @date: 2018/10/24 14:02
 * @description:
 */
@Controller
@RequestMapping(value = "/audioMeta")
public class AudioMetaController {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final Integer DEFAULT_PAGESIZE = 10;
    private Logger log = LoggerFactory.getLogger(AudioMetaController.class);

    @Autowired
    AudioMetaService audioMetaService;

    //音频信息
    @RequestMapping("/index")
    public String getAudioMeta(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNum,
                               Model model) {
        try {
            long start = System.currentTimeMillis();
            PageHelper.startPage(pageNum, DEFAULT_PAGESIZE);
            Page<AudioMeta> page = audioMetaService.queryPage();
            if (page != null && !page.isEmpty()) {
                model.addAttribute("audioMetaList", page.getResult());
                model.addAttribute("pages", page.getPages());
                model.addAttribute("pageNum", page.getPageNum());
                model.addAttribute("pageSize", DEFAULT_PAGESIZE);
                model.addAttribute("total", page.getTotal());
            } else {
                model.addAttribute("audioMetaList", Collections.emptyList());
                model.addAttribute("pages", 1);
                model.addAttribute("pageNum", 1);
            }
            long end = System.currentTimeMillis();
            log.info("音频信息列表查询耗时：" + (end - start) + "毫秒");
            return "audioMeta/audioMeta";
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
        return null;
    }

    @GetMapping("/save")
    public String AudioMeta() {
        try {
            return "audioMeta/addAudioMeta";
        } catch (Exception e) {
            log.warn("Exception {}", e);
        }
        return null;
    }

    @PostMapping("/dosave")
    public String addAudioMeta(@RequestBody AudioMeta audioMeta) {
        try {
            long start = System.currentTimeMillis();
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Date date = new Date();
            audioMeta.setId(UUIDCreater.nextId());
            audioMeta.setInsertTime(date);
            audioMeta.setOperator(userDetails.getUsername());
            int status = audioMetaService.addAudioMeta(audioMeta);
            long end = System.currentTimeMillis();
            log.info("添加音频信息数据耗时：" + (end - start) + "毫秒");
            return "redirect:/audioMeta/index";
        } catch (Exception e) {
            log.warn("Exception {}", e);
        }
        return null;
    }

    @GetMapping("/edit")
    public String editAudioMeta(@RequestParam("id") String id, Model model) {
        try {
            long start = System.currentTimeMillis();
            AudioMeta audioMeta = audioMetaService.selectdataById(id);
            model.addAttribute("audioMeta", audioMeta);
            long end = System.currentTimeMillis();
            log.info("修改音频信息列表数据耗时：" + (end - start) + "毫秒");
            return "audioMeta/editAudioMeta";
        } catch (Exception e) {
            log.warn("Exception {}", e);
        }
        return null;
    }

    @PostMapping("/doedit")
    public String doEditAudioMeta(@RequestBody AudioMeta audioMeta) {
        try {
            long start = System.currentTimeMillis();
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            audioMeta.setUpdateTime(new Date());
            audioMeta.setOperator(userDetails.getUsername());
            int status = audioMetaService.updateAudioMeta(audioMeta);
            long end = System.currentTimeMillis();
            log.info("修改音频信息列表数据耗时：" + (end - start) + "毫秒");
            return "redirect:/audioMeta/index";
        } catch (Exception e) {
            log.warn("Exception {}", e);
        }
        return null;
    }
    @GetMapping("/removeAudioMeta")
    @ResponseBody
    public ResultEntity removeAudioMeta(@RequestParam("id") String id){
        try{
            if (org.apache.commons.lang3.StringUtils.isBlank(id)){
                return new ResultEntity(400,"数据异常！");
            }

            if (audioMetaService.deleteByPrimaryKey(id)>0){
                return new ResultEntity(200, "操作成功");
            }
            return new ResultEntity(500, "操作失败，请重新尝试或联系管理员！");
        }catch (Exception e){
            e.printStackTrace();
            return new ResultEntity(500, e.getMessage());
        }
    }

    //查看音频元数据详情
    @RequestMapping(value = "/audioMetaShow")
    public String audioMetaShow(@RequestParam("id") String id, Model model) {
        if (!StringUtils.isEmpty(id)) {
            long start = System.currentTimeMillis();
            List<CommEntity> entityList = new ArrayList<>();
            AudioMeta audioMeta = audioMetaService.selectdataById(id);
            for (Field field : audioMeta.getClass().getDeclaredFields()) {
                CommEntity commEntity = new CommEntity();
                //获取属性名
                commEntity.setFiledName(field.getName());
                //获取属性中文描述
                commEntity.setFiledDesc((String) AudioMetaMap.map.get(field.getName()));
                if (audioMeta != null) {
                    //获取属性值
                    Object value = getFieldValueByName(field.getName(), audioMeta);
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
            log.info("查询音频" + id + "耗时：" + (end - start) + "毫秒");
        }
        return "audioMeta/audioMetaShow";
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
