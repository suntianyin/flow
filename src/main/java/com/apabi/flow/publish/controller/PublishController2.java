package com.apabi.flow.publish.controller;

import cn.org.rapid_framework.page.PageRequest;
import com.apabi.flow.common.PageRequestFactory;
import com.apabi.flow.common.ResultEntity;
import com.apabi.flow.douban.model.CompareEntity;
import com.apabi.flow.publish.model.ApabiBookMetaPublish2;
import com.apabi.flow.publish.model.ApabiBookMetaTempPublish2;
import com.apabi.flow.publish.service.ApabiBookMetaPublishService2;
import com.apabi.flow.publish.util.TransformFieldNameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author pipi
 * @Date 2018/8/15 18:48
 **/
@Controller
@RequestMapping(value = "/publish2")
public class PublishController2 {
    private Logger log = LoggerFactory.getLogger(PublishController2.class);
    // 页面默认展示的项目数量
    private static final Integer DEFAULT_PAGESIZE = 20;
    @Autowired
    private ApabiBookMetaPublishService2 apabiBookMetaPublishService;

    // 查询数据
    @RequestMapping("/search")
    public String publishSearch(HttpServletRequest request, Model model) {
        long start = System.currentTimeMillis();
        PageRequest pageRequest = new PageRequest();
        PageRequestFactory.bindPageRequest(pageRequest, request);
        Map<String, Object> params = (Map<String, Object>) pageRequest.getFilters();
        String metaId = "";
        if (params.get("metaId") != null) {
            metaId = params.get("metaId").toString();
        }
        String title = "";
        if (params.get("title") != null) {
            title = params.get("title").toString();
        }
        String creator = "";
        if (params.get("creator") != null) {
            creator = params.get("creator").toString();
        }
        String publisher = "";
        if (params.get("publisher") != null) {
            publisher = params.get("publisher").toString();
        }
        String isbn13 = "";
        if (params.get("isbn13") != null) {
            isbn13 = params.get("isbn13").toString();
        }
        String isbn = "";
        if (params.get("isbn") != null) {
            isbn = params.get("isbn").toString();
        }
        String isbnVal = "";
        if (params.get("isbnVal") != null) {
            isbnVal = params.get("isbnVal").toString();
        }
        String issuedDate = "";
        if (params.get("issuedDate") != null) {
            issuedDate = params.get("issuedDate").toString();
        }
        String createTime = "";
        if (params.get("createTime") != null) {
            createTime = params.get("createTime").toString();
        }
        String updateTime = "";
        if (params.get("updateTime") != null) {
            updateTime = params.get("updateTime").toString();
        }
        String paperPrice = "";
        if (params.get("paperPrice") != null) {
            paperPrice = params.get("paperPrice").toString();
        }

        Page<ApabiBookMetaTempPublish2> page = apabiBookMetaPublishService.queryPage(params, pageRequest.getPageNumber(), DEFAULT_PAGESIZE);
        if (page == null) {
            model.addAttribute("apabiTempList", null);
            model.addAttribute("page", null);
        } else {
            model.addAttribute("apabiTempList", page.getContent());
            model.addAttribute("page", page);
        }
        model.addAttribute("metaId", metaId);
        model.addAttribute("title", title);
        model.addAttribute("isbn13", isbn13);
        model.addAttribute("isbn", isbn);
        model.addAttribute("isbnVal", isbnVal);
        model.addAttribute("creator", creator);
        model.addAttribute("publisher", publisher);
        model.addAttribute("issuedDate", issuedDate);
        model.addAttribute("createTime", createTime);
        model.addAttribute("updateTime", updateTime);
        model.addAttribute("paperPrice", paperPrice);
        long end = System.currentTimeMillis();
        log.info("page的信息：" + page + "具体为：" + metaId + "数据发布列表查询耗时：" + (end - start) + "毫秒");
        return "publish/apabiTempPublish";
    }

    // 发布数据首页展示
    @RequestMapping(value = "/homeIndex")
    public String publishPublishApabiBookTempHomeIndex(HttpServletRequest request, Model model) {
        long start = System.currentTimeMillis();
        PageRequest pageRequest = new PageRequest();
        PageRequestFactory.bindPageRequest(pageRequest, request);

        Map<String, Object> params = (Map<String, Object>) pageRequest.getFilters();
        String metaId = "";
        if (params.get("metaId") != null) {
            metaId = params.get("metaId").toString();
        }
        String title = "";
        if (params.get("title") != null) {
            title = params.get("title").toString();
        }
        String creator = "";
        if (params.get("creator") != null) {
            creator = params.get("creator").toString();
        }
        String publisher = "";
        if (params.get("publisher") != null) {
            publisher = params.get("publisher").toString();
        }
        String isbn13 = "";
        if (params.get("isbn13") != null) {
            isbn13 = params.get("isbn13").toString();
        }
        String isbn = "";
        if (params.get("isbn") != null) {
            isbn = params.get("isbn").toString();
        }
        String isbnVal = "";
        if (params.get("isbnVal") != null) {
            isbnVal = params.get("isbnVal").toString();
        }
        String issuedDate = "";
        if (params.get("issuedDate") != null) {
            issuedDate = params.get("issuedDate").toString();
        }
        String createTime = "";
        if (params.get("createTime") != null) {
            createTime = params.get("createTime").toString();
        }
        String updateTime = "";
        if (params.get("updateTime") != null) {
            updateTime = params.get("updateTime").toString();
        }
        String paperPrice = "";
        if (params.get("paperPrice") != null) {
            paperPrice = params.get("paperPrice").toString();
        }
        Page<ApabiBookMetaTempPublish2> page = null;
        List<ApabiBookMetaTempPublish2> content = null;
        if (params.size() > 0) {
            page = apabiBookMetaPublishService.queryPage(params, pageRequest.getPageNumber(), DEFAULT_PAGESIZE);
            content = page.getContent();
        }
        model.addAttribute("apabiTempList", content);
        model.addAttribute("metaId", metaId);
        model.addAttribute("title", title);
        model.addAttribute("isbn13", isbn13);
        model.addAttribute("isbn", isbn);
        model.addAttribute("isbnVal", isbnVal);
        model.addAttribute("creator", creator);
        model.addAttribute("publisher", publisher);
        model.addAttribute("issuedDate", issuedDate);
        model.addAttribute("createTime", createTime);
        model.addAttribute("updateTime", updateTime);
        model.addAttribute("paperPrice", paperPrice);
        model.addAttribute("page", page);
        long end = System.currentTimeMillis();
        log.info("page的信息：" + page + "具体为：" + metaId + "数据发布列表查询耗时：" + (end - start) + "毫秒");
        return "publish/apabiTempPublish";
    }

    // 对比数据
    @RequestMapping("/compareTempAndStandard")
    public String publishCompareTempAndStandard(@RequestParam("metaId") String metaId, Model model) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        // 利用反射获取ApabiBookMetaTempPublish.class对象
        Class<?> apabiBookMetaTempPublishClass = Class.forName("com.apabi.flow.publish.model.ApabiBookMetaTempPublish");
        // 利用反射获取所有字段
        Field[] apabiBookMetaTempPublishClassFields = apabiBookMetaTempPublishClass.getDeclaredFields();
        // 查询数据库根据metaId获取ApabiBookMetaTempPublish和ApabiBookMetaPublish对应的值
        ApabiBookMetaTempPublish2 apabiBookMetaTempPublish = apabiBookMetaPublishService.findApabiBookMetaTempPublish(metaId);
        ApabiBookMetaPublish2 apabiBookMetaPublish = apabiBookMetaPublishService.findApabiBookMetaPublish(metaId);
        // 如果数据库中没有apabiBookMetaPublish则创建一个空对象
        if (apabiBookMetaPublish == null) {
            apabiBookMetaPublish = new ApabiBookMetaPublish2();
        }
        List<CompareEntity> compareEntityList = new ArrayList<>();
        for (Field field : apabiBookMetaTempPublishClassFields) {
            CompareEntity compareEntity = new CompareEntity();
            // 字段名
            String fieldName = field.getName();
            // temp库中对应的值
            String tempValue = getFieldValueByFieldName(field.getName(), apabiBookMetaTempPublish);
            if (tempValue == null) {
                tempValue = "";
            }
            // metadata库中对应的值
            String metaValue = getFieldValueByFieldName(field.getName(), apabiBookMetaPublish);
            if (metaValue == null) {
                metaValue = "";
            }
            fieldName = TransformFieldNameUtils.transform(fieldName);
            // 清洗数据
            if (tempValue.contains("\n") || tempValue.contains("\n\r") || tempValue.contains("\t")) {
                tempValue = tempValue.trim().replaceAll("\n\r", "");
                tempValue = tempValue.trim().replaceAll("\n", "");
                tempValue = tempValue.trim().replaceAll("\t", "");
            }
            if (metaValue.contains("\n") || metaValue.contains("\n\r") || metaValue.contains("\t")) {
                metaValue = metaValue.trim().replaceAll("\n\r", "");
                metaValue = metaValue.trim().replaceAll("\n", "");
                metaValue = metaValue.trim().replaceAll("\t", "");
            }
            if ("样式url".equalsIgnoreCase(fieldName)) {
                if (metaValue.contains("<")) {
                    metaValue = metaValue.replaceAll("<", "&lt;");
                }
                if (metaValue.contains(">")) {
                    metaValue = metaValue.replaceAll(">", "&gt;");
                }
                if (tempValue.contains("<")) {
                    tempValue = tempValue.replaceAll("<", "&lt;");
                }
                if (tempValue.contains(">")) {
                    tempValue = tempValue.replaceAll(">", "&gt;");
                }
            }
            compareEntity.setFieldName(fieldName);
            compareEntity.setMetaValue(metaValue);
            compareEntity.setTempValue(tempValue);
            // 如果两个库中相同字段的值相等，则将info设置为1，否则设置为0
            if (tempValue.equalsIgnoreCase(metaValue)) {
                compareEntity.setInfo("1");
            } else {
                compareEntity.setInfo("0");
            }
            compareEntityList.add(compareEntity);
        }
        model.addAttribute("compareEntityList", compareEntityList);
        model.addAttribute("metaId", metaId);
        System.out.println(apabiBookMetaTempPublish);
        return "publish/compareTempAndStandard";
    }


    // 发布数据
    @RequestMapping("/commit")
    @ResponseBody
    public ResultEntity publishCommit(@RequestParam("metaId") String metaId) throws IOException {
        System.out.println("发布数据" + metaId);
        ResultEntity resultEntity = new ResultEntity();
        try {
            apabiBookMetaPublishService.publishApabiBookMetaTemp(metaId);
            resultEntity.setStatus(1);
            resultEntity.setMsg("上传成功");
        } catch (Exception e) {
            resultEntity.setStatus(0);
            resultEntity.setMsg(e.getLocalizedMessage());
        }
        return resultEntity;
    }

    // 修改数据
    @RequestMapping(value = "/edit")
    public String publishEdit(@RequestParam("metaId") String metaId, Model model) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        // 利用反射获取ApabiBookMetaTempPublish.class对象
        Class<?> apabiBookMetaTempPublishClass = Class.forName("com.apabi.flow.publish.model.ApabiBookMetaTempPublish");
        // 利用反射获取所有字段
        Field[] apabiBookMetaTempPublishClassFields = apabiBookMetaTempPublishClass.getDeclaredFields();
        // 查询数据库根据metaId获取ApabiBookMetaTempPublish和ApabiBookMetaPublish对应的值
        ApabiBookMetaTempPublish2 apabiBookMetaTempPublish = apabiBookMetaPublishService.findApabiBookMetaTempPublish(metaId);
        ApabiBookMetaPublish2 apabiBookMetaPublish = apabiBookMetaPublishService.findApabiBookMetaPublish(metaId);
        // 如果数据库中没有apabiBookMetaPublish则创建一个空对象
        if (apabiBookMetaPublish == null) {
            apabiBookMetaPublish = new ApabiBookMetaPublish2();
        }
        List<CompareEntity> compareEntityList = new ArrayList<>();
        for (Field field : apabiBookMetaTempPublishClassFields) {
            CompareEntity compareEntity = new CompareEntity();
            // 字段名
            String fieldName = field.getName();
            // temp库中对应的值
            String tempValue = getFieldValueByFieldName(field.getName(), apabiBookMetaTempPublish);
            if (tempValue == null) {
                tempValue = "";
            }
            // metadata库中对应的值
            String metaValue = getFieldValueByFieldName(field.getName(), apabiBookMetaPublish);
            if (metaValue == null) {
                metaValue = "";
            }
            fieldName = TransformFieldNameUtils.transform(fieldName);
            // 清洗数据
            if (tempValue.contains("\n") || tempValue.contains("\n\r") || tempValue.contains("\t")) {
                tempValue = tempValue.trim().replaceAll("\n\r", "");
                tempValue = tempValue.trim().replaceAll("\n", "");
                tempValue = tempValue.trim().replaceAll("\t", "");
            }
            if (metaValue.contains("\n") || metaValue.contains("\n\r") || metaValue.contains("\t")) {
                metaValue = metaValue.trim().replaceAll("\n\r", "");
                metaValue = metaValue.trim().replaceAll("\n", "");
                metaValue = metaValue.trim().replaceAll("\t", "");
            }
            if ("样式url".equalsIgnoreCase(fieldName)) {
                if (metaValue.contains("<")) {
                    metaValue = metaValue.replaceAll("<", "&lt;");
                }
                if (metaValue.contains(">")) {
                    metaValue = metaValue.replaceAll(">", "&gt;");
                }
                if (tempValue.contains("<")) {
                    tempValue = tempValue.replaceAll("<", "&lt;");
                }
                if (tempValue.contains(">")) {
                    tempValue = tempValue.replaceAll(">", "&gt;");
                }
            }
            compareEntity.setFieldName(fieldName);
            compareEntity.setMetaValue(metaValue);
            compareEntity.setTempValue(tempValue);
            // 如果两个库中相同字段的值相等，则将info设置为1，否则设置为0
            if (tempValue.equalsIgnoreCase(metaValue)) {
                compareEntity.setInfo("1");
            } else {
                compareEntity.setInfo("0");
            }
            compareEntityList.add(compareEntity);
        }
        model.addAttribute("compareEntityList", compareEntityList);
        model.addAttribute("metaId", metaId);
        return "publish/compareAndEdit";
    }

    // 将修改的数据更新到数据库
    @RequestMapping(value = "/update", method = {RequestMethod.POST})
    public String publishUpdate(HttpServletRequest request) throws ClassNotFoundException, IllegalAccessException, ParseException {
        Map<String, String[]> parameterMap = request.getParameterMap();
        ApabiBookMetaTempPublish2 apabiBookMetaTempPublish = new ApabiBookMetaTempPublish2();
        Class<?> clazz = Class.forName("com.apabi.flow.publish.model.ApabiBookMetaTempPublish");
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            for (Map.Entry<String, String[]> entry : parameterMap.entrySet()) {
                String fieldName = field.getName();
                // 逆向转换字段名
                String fieldValue = entry.getValue()[0];
                if (fieldName.equalsIgnoreCase(TransformFieldNameUtils.reTransform(entry.getKey()))) {
                    if (field.getType().getName().equalsIgnoreCase("java.lang.Integer")) {
                        Integer fieldIntValue = null;
                        if (!"".equalsIgnoreCase(fieldValue)) {
                            fieldIntValue = Integer.parseInt(fieldValue);
                        }
                        field.set(apabiBookMetaTempPublish, fieldIntValue);
                    } else if (field.getType().getName().equalsIgnoreCase("java.util.Date")) {
                        if ("updateTime".equalsIgnoreCase(fieldName)) {
                            field.set(apabiBookMetaTempPublish, new Date());
                        } else {
                            Date date = null;
                            if (!"".equalsIgnoreCase(fieldValue)) {
                                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                date = simpleDateFormat.parse(fieldValue);
                            }
                            field.set(apabiBookMetaTempPublish, date);
                        }
                    } else {
                        if ("".equalsIgnoreCase(fieldValue)) {
                            fieldValue = null;
                        }
                        if (fieldValue != null) {
                            fieldValue.trim().replaceAll("\n", "");
                            fieldValue.trim().replaceAll("\t", "");
                            fieldValue.trim().replaceAll("\n\r", "");
                        }
                        field.set(apabiBookMetaTempPublish, fieldValue);
                    }
                }
            }
        }
        System.out.println(apabiBookMetaTempPublish);
        apabiBookMetaPublishService.updateApabiBookMetaTemp(apabiBookMetaTempPublish);
        return "homeIndex";
    }

    // 根据字段名获取字段值
    private String getFieldValueByFieldName(String key, Object obj) throws NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = obj.getClass();
        if (("hasPublish".equalsIgnoreCase(key)) && (obj instanceof ApabiBookMetaPublish2)) {
            return null;
        }
        Field field = clazz.getDeclaredField(key);
        field.setAccessible(true);
        Object o = field.get(obj);
        if (o != null) {
            return o.toString();
        } else {
            return null;
        }
    }

    @GetMapping("/byId/{metaId}")
    @ResponseBody
    public ApabiBookMetaPublish2 getById(@PathVariable("metaId") String metaId){
        return apabiBookMetaPublishService.findApabiBookMetaPublish(metaId);
    }

    @GetMapping("/add")
    public void testAdd(){
        ApabiBookMetaPublish2 apabiBookMetaPublish2 = new ApabiBookMetaPublish2();
        apabiBookMetaPublish2.setMetaId("test");
        apabiBookMetaPublishService.addApabiBookMetaTemp(apabiBookMetaPublish2);
    }
}
