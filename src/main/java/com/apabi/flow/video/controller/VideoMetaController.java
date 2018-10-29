package com.apabi.flow.video.controller;

import com.apabi.flow.book.entity.BookMetaMap;
import com.apabi.flow.book.model.BookMeta;
import com.apabi.flow.common.CommEntity;
import com.apabi.flow.common.ResultEntity;
import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.publisher.controller.PublisherController;
import com.apabi.flow.publisher.model.Publisher;
import com.apabi.flow.publisher.service.PublisherService;
import com.apabi.flow.video.model.VideoMeta;
import com.apabi.flow.video.model.VideoMetaMap;
import com.apabi.flow.video.service.VideoMetaService;
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

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author: sunty
 * @date: 2018/10/23 16:26
 * @description:
 */
@Controller
@RequestMapping(value = "/videoMeta")
public class VideoMetaController {
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static final Integer DEFAULT_PAGESIZE = 10;
    private Logger log = LoggerFactory.getLogger(VideoMetaController.class);

    @Autowired
    VideoMetaService videoMetaService;

    //视频信息
    @RequestMapping("/index")
    public String getVideoMeta(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNum,
                               Model model) {
        try {
            long start = System.currentTimeMillis();
            PageHelper.startPage(pageNum, DEFAULT_PAGESIZE);
            Page<VideoMeta> page = videoMetaService.queryPage();
            if (page != null && !page.isEmpty()) {
                model.addAttribute("videoMetaList", page.getResult());
                model.addAttribute("pages", page.getPages());
                model.addAttribute("pageNum", page.getPageNum());
                model.addAttribute("pageSize", DEFAULT_PAGESIZE);
                model.addAttribute("total", page.getTotal());
            } else {
                model.addAttribute("videoMetaList", Collections.emptyList());
                model.addAttribute("pages", 1);
                model.addAttribute("pageNum", 1);
            }
            long end = System.currentTimeMillis();
            log.info("视频信息列表查询耗时：" + (end - start) + "毫秒");
            return "videoMeta/videoMeta";
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
        return null;
    }

    @GetMapping("/save")
    public String VideoMeta() {
        try {
            return "videoMeta/addVideoMeta";
        } catch (Exception e) {
            log.warn("Exception {}", e);
        }
        return null;
    }

    @PostMapping("/dosave")
    public String addVideoMeta(@RequestBody VideoMeta videoMeta) {
        try {
            long start = System.currentTimeMillis();
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Date date = new Date();
            videoMeta.setId(UUIDCreater.nextId());
            videoMeta.setInsertTime(date);
            videoMeta.setOperator(userDetails.getUsername());
            int status = videoMetaService.addVideoMeta(videoMeta);
            long end = System.currentTimeMillis();
            log.info("添加视频信息数据耗时：" + (end - start) + "毫秒");
            return "redirect:/videoMeta/index";
        } catch (Exception e) {
            log.warn("Exception {}", e);
        }
        return null;
    }

    @GetMapping("/edit")
    public String editVideoMeta(@RequestParam("id") String id, Model model) {
        try {
            long start = System.currentTimeMillis();
            VideoMeta videoMeta = videoMetaService.selectdataById(id);
            model.addAttribute("videoMeta", videoMeta);
            long end = System.currentTimeMillis();
            log.info("修改视频信息列表数据耗时：" + (end - start) + "毫秒");
            return "videoMeta/editVideoMeta";
        } catch (Exception e) {
            log.warn("Exception {}", e);
        }
        return null;
    }

    @PostMapping("/doedit")
    public String doEditVideoMeta(@RequestBody VideoMeta videoMeta) {
        try {
            long start = System.currentTimeMillis();
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            videoMeta.setUpdateTime(new Date());
            videoMeta.setOperator(userDetails.getUsername());
            int status = videoMetaService.updateVideoMeta(videoMeta);
            long end = System.currentTimeMillis();
            log.info("修改视频信息列表数据耗时：" + (end - start) + "毫秒");
            return "redirect:/videoMeta/index";
        } catch (Exception e) {
            log.warn("Exception {}", e);
        }
        return null;
    }

    @GetMapping("/removeVideoMeta")
    @ResponseBody
    public ResultEntity removeVideoMeta(@RequestParam("id") String id){
        try{
            if (org.apache.commons.lang3.StringUtils.isBlank(id)){
                return new ResultEntity(400,"数据异常！");
            }

            if (videoMetaService.deleteByPrimaryKey(id)>0){
                return new ResultEntity(200, "操作成功");
            }
            return new ResultEntity(500, "操作失败，请重新尝试或联系管理员！");
        }catch (Exception e){
            e.printStackTrace();
            return new ResultEntity(500, e.getMessage());
        }
    }

    //查看视频元数据详情
    @RequestMapping(value = "/videoMetaShow")
    public String videoMetaShow(@RequestParam("id") String id, Model model) {
        if (!StringUtils.isEmpty(id)) {
            long start = System.currentTimeMillis();
            List<CommEntity> entityList = new ArrayList<>();
            VideoMeta videoMeta = videoMetaService.selectdataById(id);
            for (Field field : videoMeta.getClass().getDeclaredFields()) {
                CommEntity commEntity = new CommEntity();
                //获取属性名
                commEntity.setFiledName(field.getName());
                //获取属性中文描述
                commEntity.setFiledDesc((String) VideoMetaMap.map.get(field.getName()));
                if (videoMeta != null) {
                    //获取属性值
                    Object value = getFieldValueByName(field.getName(), videoMeta);
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
            log.info("查询视频" + id + "耗时：" + (end - start) + "毫秒");
        }
        return "videoMeta/videoMetaShow";
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
