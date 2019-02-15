package com.apabi.flow.jd.controller;

import com.apabi.flow.jd.model.JdMetadata;
import com.apabi.flow.jd.service.JdService;
import com.apabi.flow.jd.util.TransformJdFieldNameUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author pipi
 * @Date 2019-1-16 11:07
 **/
@Controller
@RequestMapping("jd")
public class JdController {
    private static final int DEFAULT_PAGE_SIZE = 20;

    @Autowired
    private JdService jdService;

    @RequestMapping("/searchIndex")
    public String index(Model model, HttpServletRequest request, @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNum) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String> params = new HashMap<>();
        String jdItemId = "";
        if (parameterMap.get("jdItemId") != null) {
            jdItemId = parameterMap.get("jdItemId")[0].trim();
        }
        params.put("jdItemId", jdItemId);

        String title = "";
        if (parameterMap.get("title") != null) {
            title = parameterMap.get("title")[0].trim();
        }
        params.put("title", title);

        String publisher = "";
        if (parameterMap.get("publisher") != null) {
            publisher = parameterMap.get("publisher")[0].trim();
        }
        params.put("publisher", publisher);

        String isbn13 = "";
        if (parameterMap.get("isbn13") != null) {
            isbn13 = parameterMap.get("isbn13")[0].trim();
        }
        params.put("isbn13", isbn13);

        PageHelper.startPage(pageNum, DEFAULT_PAGE_SIZE);
        Page<JdMetadata> page = null;
        if (parameterMap != null && parameterMap.size() > 0) {
            page = jdService.findJdMetaByPage(params);
        }
        if (page != null && !page.isEmpty()) {
            model.addAttribute("jdMetaModelList", page.getResult());
            model.addAttribute("pages", page.getPages());
            model.addAttribute("pageNum", page.getPageNum());
        } else {
            model.addAttribute("jdMetaModelList", Collections.emptyList());
            model.addAttribute("pages", 1);
            model.addAttribute("pageNum", 1);
        }
        model.addAttribute("jdItemId", jdItemId);
        model.addAttribute("title", title);
        model.addAttribute("publisher", publisher);
        model.addAttribute("isbn13", isbn13);
        model.addAttribute("page", page);
        return "jd/jdSearchIndex";
    }

    @RequestMapping("/jdDetail")
    public String searchDetail(@RequestParam String jdItemId, Model model) throws ClassNotFoundException, ParseException, NoSuchFieldException, IllegalAccessException {
        JdMetadata jdMetadata = null;
        if (StringUtils.isNotEmpty(jdItemId)) {
            jdMetadata = jdService.findById(jdItemId);
        }
        Class clazz = Class.forName("com.apabi.flow.jd.model.JdMetadata");
        Map<String, String> map = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        // 数据库中的数据 -> Date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
        // Date -> String
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Field field : fields) {
            // 字段名
            String key = field.getName();
            // 字段值
            String value = null;
            key = TransformJdFieldNameUtils.transform(key);
            if ("创建时间".equals(key)) {
                value = getFieldValueByFieldName(field.getName(), jdMetadata);
                // 将创建时间转为字符串
                Date parse = simpleDateFormat.parse(value);
                value = simpleDateFormat1.format(parse);
            } else if ("更新时间".equals(key)) {
                value = getFieldValueByFieldName(field.getName(), jdMetadata);
                // 将更新时间转为字符串
                Date parse = simpleDateFormat.parse(value);
                value = simpleDateFormat1.format(parse);
            } else {
                // 普通字段值
                value = getFieldValueByFieldName(field.getName(), jdMetadata);
            }
            map.put(key, value);
        }
        model.addAttribute("jdMetaMap", map);
        return "jd/jdMetaDetail";
    }

    /**
     * 编辑jd数据
     *
     * @param jdItemId
     * @param model
     * @return
     * @throws ClassNotFoundException
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     * @throws ParseException
     */
    @RequestMapping("/jdEdit")
    public String jdEdit(@RequestParam("jdItemId") String jdItemId, Model model) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, ParseException {
        JdMetadata jdMetadata = null;
        if (StringUtils.isNotEmpty(jdItemId)) {
            jdMetadata = jdService.findById(jdItemId);
        }
        Class clazz = Class.forName("com.apabi.flow.jd.model.JdMetadata");
        Map<String, String> map = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        // 数据库中的数据 -> Date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
        // Date -> String
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Field field : fields) {
            // 字段名
            String key = field.getName();
            // 字段值
            String value = null;
            key = TransformJdFieldNameUtils.transform(key);
            if ("创建时间".equals(key)) {
                value = getFieldValueByFieldName(field.getName(), jdMetadata);
                // 将创建时间转为字符串
                Date parse = simpleDateFormat.parse(value);
                value = simpleDateFormat1.format(parse);
            } else if ("更新时间".equals(key)) {
                // 将更新时间转为字符串
                Date parse = new Date();
                value = simpleDateFormat1.format(parse);
            } else {
                // 普通字段值
                value = getFieldValueByFieldName(field.getName(), jdMetadata);
            }
            map.put(key, value);
        }
        model.addAttribute("jdMetaMap", map);
        return "jd/jdMetaEdit";
    }

    /**
     * 提交修改后的jdMeta数据
     *
     * @param request
     * @return
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws ParseException
     */
    @RequestMapping("/updateJdMeta")
    public String updateJdMeta(HttpServletRequest request) throws InvocationTargetException, IllegalAccessException, ParseException {
        // 获取京东Id
        String jdItemId = request.getParameter("京东Id");
        Map<String, String[]> params = request.getParameterMap();
        JdMetadata jdMetadata = new JdMetadata();
        for (Map.Entry<String, String[]> param : params.entrySet()) {
            String key = param.getKey();
            String[] values = param.getValue();
            String value = null;
            if (values != null && values.length > 0) {
                value = values[0];
            }
            String field = TransformJdFieldNameUtils.reTransform(key);
            if ("createTime".equalsIgnoreCase(field)) {
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value);
                BeanUtils.setProperty(jdMetadata, field, date);
            } else if ("updateTime".equalsIgnoreCase(field)) {
                BeanUtils.setProperty(jdMetadata, field, new Date());
            } else {
                BeanUtils.setProperty(jdMetadata, field, value);
            }
        }
        jdService.updateJd(jdMetadata);
        return "searchIndex?jdItemId=" + jdItemId;
    }

    /**
     * 根据字段名获取字段值
     *
     * @param key
     * @param obj
     * @return
     * @throws NoSuchFieldException
     * @throws IllegalAccessException
     */
    private String getFieldValueByFieldName(String key, Object obj) throws NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = obj.getClass();
        Field field = clazz.getDeclaredField(key);
        field.setAccessible(true);
        Object object = field.get(obj);
        if (object != null) {
            return object.toString();
        } else {
            return null;
        }
    }
}