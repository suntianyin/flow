package com.apabi.flow.douban.controller;

import com.apabi.flow.douban.model.AmazonMeta;
import com.apabi.flow.douban.service.AmazonMetaService;
import com.apabi.flow.douban.util.TransformAmazonFieldNameUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * @Date 2018/9/4 11:02
 **/
@Controller
@RequestMapping("/amazon")
public class AmazonMetaSearchController {
    private Logger logger = LoggerFactory.getLogger(AmazonMetaSearchController.class);
    public static final Integer DEFAULT_PAGE_SIZE = 20;
    @Autowired
    private AmazonMetaService amazonMetaService;

    @RequestMapping("/searchIndex")
    public String index(Model model, HttpServletRequest request, @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNum) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String> params = new HashMap<>();

        String amazonId = "";
        if (parameterMap.get("amazonId") != null) {
            amazonId = parameterMap.get("amazonId")[0].trim();
        }
        params.put("amazonId", amazonId);

        String title = "";
        if (parameterMap.get("title") != null) {
            title = parameterMap.get("title")[0].trim();
        }
        params.put("title", title);

        String author = "";
        if (parameterMap.get("author") != null) {
            author = parameterMap.get("author")[0].trim();
        }
        params.put("author", author);

        String publisher = "";
        if (parameterMap.get("publisher") != null) {
            publisher = parameterMap.get("publisher")[0].trim();
        }
        params.put("publisher", publisher);

        String isbnVal = "";
        if (parameterMap.get("isbnVal") != null) {
            isbnVal = parameterMap.get("isbnVal")[0];
        }
        params.put("isbnVal", isbnVal);

        String isbn = "";
        if (parameterMap.get("isbn") != null) {
            isbn = parameterMap.get("isbn")[0];
        }
        if ("isbn10".equalsIgnoreCase(isbn)) {
            params.put("isbn10", isbnVal);
        }
        if ("isbn13".equalsIgnoreCase(isbn)) {
            params.put("isbn13", isbnVal);
        }
        if (isbnVal == null || "".equalsIgnoreCase(isbnVal)) {
            params.put("isbn10", "");
            params.put("isbn13", "");
        }
        int total = amazonMetaService.countTotal();
        int totalPageNum = (total / DEFAULT_PAGE_SIZE) + 1;
        PageHelper.startPage(pageNum, DEFAULT_PAGE_SIZE);
        Page<AmazonMeta> page = null;
        if (parameterMap != null && parameterMap.size() > 0) {
            page = amazonMetaService.findAmazonMetaByPageOrderByUpdateTime(params);
        }
        if (page != null && !page.isEmpty()) {
            model.addAttribute("amazonMetaModelList", page.getResult());
            model.addAttribute("pages", page.getPages());
            model.addAttribute("pageNum", page.getPageNum());
        } else {
            model.addAttribute("amazonMetaModelList", Collections.emptyList());
            model.addAttribute("pages", 1);
            model.addAttribute("pageNum", 1);
        }
        model.addAttribute("total", total);
        model.addAttribute("totalPageNum", totalPageNum);
        model.addAttribute("amazonId", amazonId);
        model.addAttribute("title", title);
        model.addAttribute("publisher", publisher);
        model.addAttribute("isbnVal", isbnVal);
        model.addAttribute("isbn", isbn);
        model.addAttribute("page", page);
        model.addAttribute("author", author);

        return "amazon/amazonSearchIndex";
    }

    @RequestMapping("/amazonDetail")
    public String searchDetail(@RequestParam String amazonId, Model model) throws ClassNotFoundException, ParseException, NoSuchFieldException, IllegalAccessException {
        AmazonMeta amazonMeta = null;
        if (StringUtils.isNotEmpty(amazonId)) {
            amazonMeta = amazonMetaService.findById(amazonId);
        }
        Class clazz = Class.forName("com.apabi.flow.douban.model.AmazonMeta");
        Map<String, String> map = new LinkedHashMap<>();
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
            key = TransformAmazonFieldNameUtils.transform(key);
            if ("创建时间".equals(key)) {
                value = getFieldValueByFieldName(field.getName(), amazonMeta);
                // 将创建时间转为字符串
                Date parse = simpleDateFormat.parse(value);
                value = simpleDateFormat1.format(parse);
            } else if ("更新时间".equals(key)) {
                value = getFieldValueByFieldName(field.getName(), amazonMeta);
                // 将更新时间转为字符串
                Date parse = simpleDateFormat.parse(value);
                value = simpleDateFormat1.format(parse);
            } else {
                // 普通字段值
                value = getFieldValueByFieldName(field.getName(), amazonMeta);
            }
            map.put(key, value);
        }
        model.addAttribute("amazonMetaMap", map);
        return "amazon/amazonMetaDetail";
    }

    // 编辑amazon数据
    @RequestMapping("/amazonEdit")
    public String amazonEdit(@RequestParam("amazonId") String amazonId, Model model) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, ParseException {
        AmazonMeta amazonMeta = null;
        if (StringUtils.isNotEmpty(amazonId)) {
            amazonMeta = amazonMetaService.findById(amazonId);
        }
        Class clazz = Class.forName("com.apabi.flow.douban.model.AmazonMeta");
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
            key = TransformAmazonFieldNameUtils.transform(key);
            if ("创建时间".equals(key)) {
                value = getFieldValueByFieldName(field.getName(), amazonMeta);
                // 将创建时间转为字符串
                Date parse = simpleDateFormat.parse(value);
                value = simpleDateFormat1.format(parse);
            } else if ("更新时间".equals(key)) {
                // 将更新时间转为字符串
                Date parse = new Date();
                value = simpleDateFormat1.format(parse);
            } else {
                // 普通字段值
                value = getFieldValueByFieldName(field.getName(), amazonMeta);
            }
            map.put(key, value);
        }
        model.addAttribute("amazonMetaMap", map);
        return "amazon/amazonMetaEdit";
    }

    // 提交修改后的doubanMeta数据
    @RequestMapping("/updateAmazonMeta")
    public String updateAmazonMeta(HttpServletRequest request) throws InvocationTargetException, IllegalAccessException, ParseException {
        // 获取亚马逊Id
        String amazonId = request.getParameter("亚马逊Id");
        Map<String, String[]> params = request.getParameterMap();
        AmazonMeta amazonMeta = new AmazonMeta();
        for (Map.Entry<String, String[]> param : params.entrySet()) {
            String key = param.getKey();
            String[] values = param.getValue();
            String value = null;
            if (values != null && values.length > 0) {
                value = values[0];
            }
            String field = TransformAmazonFieldNameUtils.reTransform(key);
            if ("createTime".equalsIgnoreCase(field)) {
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value);
                BeanUtils.setProperty(amazonMeta, field, date);
            } else if ("updateTime".equalsIgnoreCase(field)) {
                BeanUtils.setProperty(amazonMeta, field, new Date());
            } else {
                BeanUtils.setProperty(amazonMeta, field, value);
            }
        }
        amazonMetaService.updateAmazon(amazonMeta);
        return "searchIndex?amazonId=" + amazonId;
    }

    // 根据字段名获取字段值
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
