package com.apabi.flow.bookSearch.controller;

import com.apabi.flow.bookSearch.model.BookSearchModel;
import com.apabi.flow.bookSearch.service.BookSearchModelService;
import com.apabi.flow.publish.util.TransformFieldNameUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @Author pipi
 * @Date 2018/9/10 9:54
 **/
@Controller
@RequestMapping("/bookSearch")
public class BookSearchController {
    @Autowired
    private BookSearchModelService bookSearchModelService;
    public static final Integer DEFAULT_PAGE_SIZE = 20;

    @RequestMapping("/index")
    public String indexBook(HttpServletRequest request, Model model, @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNum) {
        Map<String, String[]> parameterMap = request.getParameterMap();
        Map<String, String> params = new HashMap<>();
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
        PageHelper.startPage(pageNum, DEFAULT_PAGE_SIZE);
        Page<BookSearchModel> page = null;
        if (parameterMap.size() > 0) {
            page = bookSearchModelService.findBookSearchByPage(params);
            totalCount = page.getTotal();
            totalPageNum = page.getPages();
        }else {
            totalCount = bookSearchModelService.count();
        }

        if (page != null && !page.isEmpty()) {
            model.addAttribute("bookSearchModelList", page.getResult());
            model.addAttribute("pages", page.getPages());
            model.addAttribute("pageNum", page.getPageNum());
        } else {
            model.addAttribute("bookSearchModelList", Collections.emptyList());
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
        model.addAttribute("totalPageNum", totalPageNum);
        return "bookSearch/bookSearchIndex";
    }

    @RequestMapping("/detail")
    public String bookDetail(@RequestParam("metaId") String metaId, Model model) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException {
        BookSearchModel bookSearchModel = bookSearchModelService.findBookSearchByMetaId(metaId);
        Class<?> clazz = Class.forName("com.apabi.flow.bookSearch.model.BookSearchModel");
        Field[] fields = clazz.getDeclaredFields();
        Map<String, String> bookSearchModelMap = new HashMap<String, String>();
        for (Field field : fields) {
            String key = field.getName();
            String value = getFieldValueByFieldName(field.getName(), bookSearchModel);
            key = TransformFieldNameUtils.transform(key);
            bookSearchModelMap.put(key, value);
        }
        model.addAttribute("bookSearchModelMap", bookSearchModelMap);
        return "bookSearch/bookSearchDetail";
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
