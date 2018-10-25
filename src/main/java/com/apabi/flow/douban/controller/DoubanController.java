package com.apabi.flow.douban.controller;

import com.apabi.flow.common.ResultEntity;
import com.apabi.flow.douban.model.ApabiBookMetaTemp;
import com.apabi.flow.douban.model.DoubanMeta;
import com.apabi.flow.douban.service.DoubanMetaService;
import com.apabi.flow.douban.util.TransformDoubanFieldNameUtils;
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
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

/**
 * @author pipi
 * @date 2018/8/8 15:42
 * @description
 */
@Controller
@RequestMapping(value = "/douban")
public class DoubanController {
    private Logger log = LoggerFactory.getLogger(DoubanController.class);
    public static final Integer DEFAULT_PAGE_SIZE = 20;
    @Autowired
    private DoubanMetaService doubanMetaService;

    @RequestMapping("/index")
    public String forward() {
        return "douban/doubanMeta";
    }

    @RequestMapping("/getBookMeta")
    @ResponseBody
    public List<ResultEntity> getDoubanMeta(@RequestParam(value = "isbn13") String isbn13) {
        Instant start = Instant.now();
        // 结果List
        List<ResultEntity> resultEntityList = new ArrayList<>();

        System.out.println(isbn13);
        String[] isbn13List = isbn13.split(",");
        for (String i : isbn13List) {
            i = i.trim();
            try {
                List<ApabiBookMetaTemp> doubanMetaList = doubanMetaService.searchMetaDataTempsByISBN(i);
                for (ApabiBookMetaTemp doubanMeta : doubanMetaList) {
                    ResultEntity resultEntity = new ResultEntity();
                    resultEntity.setMsg("success");
                    resultEntity.setStatus(0);
                    resultEntity.setBody(doubanMeta);
                    resultEntityList.add(resultEntity);
                }
            } catch (Exception e) {
                e.printStackTrace();
                ResultEntity resultEntity = new ResultEntity();
                resultEntity.setMsg("fail");
                resultEntity.setStatus(1);
                resultEntity.setBody("查询" + i + "出错，原因为：" + e.getMessage());
                resultEntityList.add(resultEntity);
            }
        }
        Instant end = Instant.now();
        log.info(String.valueOf("查询ISBN：" + isbn13 + "花费的时间为：" + Duration.between(start, end).toMillis()) + "ms");
        return resultEntityList;
    }

    // douban数据查询功能
    @RequestMapping("/searchIndex")
    public String searchDouban(Model model, HttpServletRequest request, @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNum) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String> params = new HashMap<String, String>();

        String doubanId = "";
        if (parameterMap.get("doubanId") != null) {
            doubanId = parameterMap.get("doubanId")[0].trim();
        }
        params.put("doubanId", doubanId);

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
        PageHelper.startPage(pageNum, DEFAULT_PAGE_SIZE);
        Page<DoubanMeta> page = null;
        if (parameterMap != null && parameterMap.size() > 0) {
            page = doubanMetaService.searchDoubanMetaByPage(params);
        }
        if (page != null && !page.isEmpty()) {
            model.addAttribute("doubanMetaModelList", page.getResult());
            model.addAttribute("pages", page.getPages());
            model.addAttribute("pageNum", page.getPageNum());
        } else {
            model.addAttribute("doubanMetaModelList", Collections.emptyList());
            model.addAttribute("pages", 1);
            model.addAttribute("pageNum", 1);
        }

        model.addAttribute("doubanId", doubanId);
        model.addAttribute("title", title);
        model.addAttribute("publisher", publisher);
        model.addAttribute("isbnVal", isbnVal);
        model.addAttribute("isbn", isbn);
        model.addAttribute("page", page);
        model.addAttribute("author", author);

        return "douban/doubanSearchIndex";
    }

    // 查看豆瓣详细数据
    @RequestMapping("/doubanDetail")
    public String doubanDetail(@RequestParam("doubanId") String doubanId, Model model) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, ParseException {
        DoubanMeta doubanMeta = null;
        if (StringUtils.isNotEmpty(doubanId)) {
            doubanMeta = doubanMetaService.searchDoubanMetaById(doubanId);
        }
        Class clazz = Class.forName("com.apabi.flow.douban.model.DoubanMeta");
        Map<String, String> map = new HashMap<>();
        Field[] fields = clazz.getDeclaredFields();
        /*for (Field field : fields) {
            String key = field.getName();
            String value = getFieldValueByFieldName(field.getName(), doubanMeta);
            key = TransformDoubanFieldNameUtils.transform(key);
            map.put(key, value);
        }*/
        // 数据库中的数据 -> Date
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
        // Date -> String
        SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Field field : fields) {
            // 字段名
            String key = field.getName();
            // 字段值
            String value = null;
            key = TransformDoubanFieldNameUtils.transform(key);
            if ("创建时间".equals(key)) {
                value = getFieldValueByFieldName(field.getName(), doubanMeta);
                // 将创建时间转为字符串
                Date parse = simpleDateFormat.parse(value);
                value = simpleDateFormat1.format(parse);
            } else if ("更新时间".equals(key)) {
                value = getFieldValueByFieldName(field.getName(), doubanMeta);
                // 将更新时间转为字符串
                Date parse = simpleDateFormat.parse(value);
                value = simpleDateFormat1.format(parse);
            } else {
                // 普通字段值
                value = getFieldValueByFieldName(field.getName(), doubanMeta);
            }
            if (!("hasPublish".equalsIgnoreCase(key)) && !("metaId".equalsIgnoreCase(key))) {
                map.put(key, value);
            }
        }
        model.addAttribute("doubanMetaMap", map);
        return "douban/doubanMetaDetail";
    }

    // 编辑豆瓣数据
    @RequestMapping("/doubanEdit")
    public String doubanEdit(@RequestParam("doubanId") String doubanId, Model model) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, ParseException {
        DoubanMeta doubanMeta = null;
        if (StringUtils.isNotEmpty(doubanId)) {
            doubanMeta = doubanMetaService.searchDoubanMetaById(doubanId);
        }
        Class clazz = Class.forName("com.apabi.flow.douban.model.DoubanMeta");
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
            key = TransformDoubanFieldNameUtils.transform(key);
            if ("创建时间".equals(key)) {
                value = getFieldValueByFieldName(field.getName(), doubanMeta);
                // 将创建时间转为字符串
                Date parse = simpleDateFormat.parse(value);
                value = simpleDateFormat1.format(parse);
            } else if ("更新时间".equals(key)) {
                // 将更新时间转为字符串
                Date parse = new Date();
                value = simpleDateFormat1.format(parse);
            } else {
                // 普通字段值
                value = getFieldValueByFieldName(field.getName(), doubanMeta);
            }
            if (!("hasPublish".equalsIgnoreCase(key)) && !("metaId".equalsIgnoreCase(key))) {
                map.put(key, value);
            }
        }
        model.addAttribute("doubanMetaMap", map);
        return "douban/doubanMetaEdit";
    }

    // 提交修改后的doubanMeta数据
    @RequestMapping("/updateDoubanMeta")
    public String updateDoubanMeta(HttpServletRequest request) throws InvocationTargetException, IllegalAccessException, ParseException {
        // 获取豆瓣Id
        String doubanId = request.getParameter("豆瓣Id");
        Map<String, String[]> params = request.getParameterMap();
        DoubanMeta doubanMeta = new DoubanMeta();
        for (Map.Entry<String, String[]> param : params.entrySet()) {
            String key = param.getKey();
            String[] values = param.getValue();
            String value = null;
            if (values != null && values.length > 0) {
                value = values[0];
            }
            String field = TransformDoubanFieldNameUtils.reTransform(key);
            if ("createTime".equalsIgnoreCase(field)) {
                Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value);
                BeanUtils.setProperty(doubanMeta, field, date);
            } else if ("updateTime".equalsIgnoreCase(field)) {
                BeanUtils.setProperty(doubanMeta, field, new Date());
            } else {
                BeanUtils.setProperty(doubanMeta, field, value);
            }
        }
        doubanMetaService.updateDoubanMeta(doubanMeta);
        return "searchIndex?doubanId=" + doubanId;
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