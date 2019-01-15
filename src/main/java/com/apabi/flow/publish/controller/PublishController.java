package com.apabi.flow.publish.controller;

import com.apabi.flow.common.ResultEntity;
import com.apabi.flow.douban.model.ApabiBookMetaDataTemp;
import com.apabi.flow.douban.model.CompareEntity;
import com.apabi.flow.publish.model.ApabiBookMetaPublish;
import com.apabi.flow.publish.model.ApabiBookMetaTempPublish;
import com.apabi.flow.publish.service.ApabiBookMetaPublishService;
import com.apabi.flow.publish.util.TransformFieldNameUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Author pipi
 * @Date 2018/8/15 18:48
 **/
@Controller
@RequestMapping(value = "/publish")
public class PublishController {
    private Logger log = LoggerFactory.getLogger(PublishController.class);
    // 页面默认展示的项目数量
    private static final Integer DEFAULT_PAGESIZE = 20;
    @Autowired
    private ApabiBookMetaPublishService apabiBookMetaPublishService;

    // 查询数据
    @RequestMapping("/search")
    public String publishSearch(HttpServletRequest request, Model model,@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNum) {
        long start = System.currentTimeMillis();
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> params = new HashMap<>();
        String metaId = "";
        if (parameterMap.get("metaId") != null) {
            metaId = parameterMap.get("metaId")[0].trim();
        }
        params.put("metaId", metaId);
        String title = "";
        if (parameterMap.get("title") != null) {
            title = parameterMap.get("title")[0].trim();
        }
        params.put("title", title);

        String creator = "";
        if (parameterMap.get("creator") != null) {
            creator = parameterMap.get("creator")[0].trim();
        }
        params.put("creator", creator);

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
        if ("isbn".equalsIgnoreCase(isbn)) {
            params.put("isbn", isbnVal);
        }
        if ("isbn10".equalsIgnoreCase(isbn)) {
            params.put("isbn10", isbnVal);
        }
        if ("isbn13".equalsIgnoreCase(isbn)) {
            params.put("isbn13", isbnVal);
        }

        String hasCebx = "";
        if (parameterMap.get("hasCebx") != null) {
            hasCebx = parameterMap.get("hasCebx")[0].trim();
        }
        params.put("hasCebx", hasCebx);

        String hasFlow = "";
        if (parameterMap.get("hasFlow") != null) {
            hasFlow = parameterMap.get("hasFlow")[0].trim();
        }
        params.put("hasFlow", hasFlow);

        String isPublicCopyRight = "";
        if (parameterMap.get("isPublicCopyRight") != null) {
            isPublicCopyRight = parameterMap.get("isPublicCopyRight")[0].trim();
        }
        params.put("isPublicCopyRight", isPublicCopyRight);

        String saleStatus = "";
        if (parameterMap.get("saleStatus") != null) {
            saleStatus = parameterMap.get("saleStatus")[0].trim();
        }
        params.put("saleStatus", saleStatus);

        String flowSource = "";
        if (parameterMap.get("flowSource") != null) {
            flowSource = parameterMap.get("flowSource")[0].trim();
        }
        params.put("flowSource", flowSource);

        if (isbnVal == null || "".equalsIgnoreCase(isbnVal)) {
            params.put("isbn", "");
            params.put("isbn10", "");
            params.put("isbn13", "");
        }

        long totalCount = 0;
        long totalPageNum = 1;
        PageHelper.startPage(pageNum, DEFAULT_PAGESIZE);
        Page<ApabiBookMetaDataTemp> page = null;
        if (parameterMap.size() > 0) {
            page = apabiBookMetaPublishService.queryPage(params);
            totalCount = page.getTotal();
            totalPageNum = page.getPages();
        }

        if (page != null && !page.isEmpty()) {
            model.addAttribute("pageNum", page.getPageNum());
        } else {
            model.addAttribute("pages", 1);
            model.addAttribute("pageNum", 1);
        }

        model.addAttribute("metaId", metaId);
        model.addAttribute("title", title);
        model.addAttribute("creator", creator);
        model.addAttribute("flowSource", flowSource);
        model.addAttribute("publisher", publisher);
        model.addAttribute("isbnVal", isbnVal);
        model.addAttribute("isbn", isbn);
        model.addAttribute("hasCebx", hasCebx);
        model.addAttribute("hasFlow", hasFlow);
        model.addAttribute("isPublicCopyRight", isPublicCopyRight);
        model.addAttribute("saleStatus", saleStatus);
        model.addAttribute("page", page);
        model.addAttribute("totalCount", totalCount);
        model.addAttribute("totalPage", totalPageNum);
        model.addAttribute("apabiTempList", page.getResult());


        long end = System.currentTimeMillis();
        log.info("page的信息：" + page + "具体为：" + metaId + "数据发布列表查询耗时：" + (end - start) + "毫秒");
        return "publish/apabiTempPublish";
    }

    // 发布数据首页展示
    @RequestMapping(value = "/homeIndex")
    public String publishPublishApabiBookTempHomeIndex(HttpServletRequest request, Model model) {
        long start = System.currentTimeMillis();
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, Object> params = new HashMap<>();
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
        String isbn10 = "";
        if (params.get("isbn10") != null) {
            isbn10 = params.get("isbn10").toString();
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
        Page<ApabiBookMetaDataTemp> page = null;
        List<ApabiBookMetaDataTemp> content = null;
        if (params.size() > 0) {
            page = apabiBookMetaPublishService.queryPage(params);
            content = page.getResult();
        }
        model.addAttribute("apabiTempList", content);
        model.addAttribute("metaId", metaId);
        model.addAttribute("title", title);
        model.addAttribute("isbn", isbn);
        model.addAttribute("isbn10", isbn10);
        model.addAttribute("isbn13", isbn13);
        model.addAttribute("isbnVal", isbnVal);
        model.addAttribute("creator", creator);
        model.addAttribute("publisher", publisher);
        model.addAttribute("issuedDate", issuedDate);
        model.addAttribute("createTime", createTime);
        model.addAttribute("updateTime", updateTime);
        model.addAttribute("paperPrice", paperPrice);
        model.addAttribute("page", page);
        if (page != null) {
            model.addAttribute("totalPage", page.getTotal());
            model.addAttribute("pageNum", page.getPageNum());
        }
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
        ApabiBookMetaTempPublish apabiBookMetaTempPublish = apabiBookMetaPublishService.findApabiBookMetaTempPublish(metaId);
        ApabiBookMetaPublish apabiBookMetaPublish = apabiBookMetaPublishService.findApabiBookMetaPublish(metaId);
        // 如果数据库中没有apabiBookMetaPublish则创建一个空对象
        if (apabiBookMetaPublish == null) {
            apabiBookMetaPublish = new ApabiBookMetaPublish();
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

    // 批量发布数据
    @RequestMapping("/batchCommit")
    @ResponseBody
    public ResultEntity batchPublishCommit(@RequestParam("metaIds") String metaIds) {
        System.out.println("发布数据" + metaIds);
        ResultEntity resultEntity = new ResultEntity();
        try {
            String[] metaIdList = metaIds.split(",");
            for (String metaId : metaIdList) {
                apabiBookMetaPublishService.publishApabiBookMetaTemp(metaId);
            }
            resultEntity.setStatus(1);
            resultEntity.setMsg("批量发布成功");
        } catch (Exception e) {
            e.printStackTrace();
            resultEntity.setStatus(0);
            resultEntity.setMsg("批量发布失败");
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
        ApabiBookMetaTempPublish apabiBookMetaTempPublish = apabiBookMetaPublishService.findApabiBookMetaTempPublish(metaId);
        ApabiBookMetaPublish apabiBookMetaPublish = apabiBookMetaPublishService.findApabiBookMetaPublish(metaId);
        // 如果数据库中没有apabiBookMetaPublish则创建一个空对象
        if (apabiBookMetaPublish == null) {
            apabiBookMetaPublish = new ApabiBookMetaPublish();
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
        String metaId = parameterMap.get("阿帕比图书metaid")[0];
        request.setAttribute("metaId", metaId);
        System.out.println(metaId);
        ApabiBookMetaTempPublish apabiBookMetaTempPublish = new ApabiBookMetaTempPublish();
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
                            fieldValue.trim().replaceAll("\n\r", "");
                            fieldValue.trim().replaceAll("\n", "");
                            fieldValue.trim().replaceAll("\t", "");
                        }
                        field.set(apabiBookMetaTempPublish, fieldValue);
                    }
                }
            }
        }
        System.out.println(apabiBookMetaTempPublish);
        apabiBookMetaPublishService.updateApabiBookMetaTemp(apabiBookMetaTempPublish);
        return "/publish/search?pageNumber=1&s_metaId=" + metaId + "&s_title=&s_creator=&s_publisher=&s_isbn=isbn&s_isbnVal=";
    }

    // 根据字段名获取字段值
    private String getFieldValueByFieldName(String key, Object obj) throws NoSuchFieldException, IllegalAccessException {
        Class<?> clazz = obj.getClass();
        if (("hasPublish".equalsIgnoreCase(key)) && (obj instanceof ApabiBookMetaPublish)) {
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
}
