package com.apabi.flow.publisher.controller;

import com.apabi.flow.author.util.EntityInfo;
import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.douban.util.StringToolUtil;
import com.apabi.flow.publisher.model.Publisher;
import com.apabi.flow.publisher.service.PublisherService;
import com.apabi.flow.publisher.util.TransformPublisherFieldNameUtils;
import com.apabi.flow.thematic.util.ReadExcelUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by wuji on 2018/8/10.
 */

@Controller
@RequestMapping(value = "/publisher")
public class PublisherController {
    public static final Integer DEFAULT_PAGESIZE = 10;
    private Logger log = LoggerFactory.getLogger(PublisherController.class);
    private Map<String, String> mapr;
    @Autowired
    PublisherService publisherService;

    @RequestMapping("/getRelatePublisherMap")
    @ResponseBody
    public Object getRelatePublisherMap() {
        List<Publisher> all = publisherService.findAll();
        HashSet<String> set = new HashSet();
        HashMap<String, String> map = new HashMap<>();
        for (Publisher p : all) {
            if (p.getRelatePublisherID() != null)
                set.add(p.getRelatePublisherID());
        }
        for (String s : set) {
            Publisher p = publisherService.selectdataById(s);
            if (p.getRelatePublisherID() != null)
                map.put(p.getTitle(), p.getId());
        }
        this.mapr = map;
        List<EntityInfo> list = new ArrayList<>();
        for (String key : map.keySet()) {
            EntityInfo entityInfo = new EntityInfo();
            entityInfo.setFieldName(map.get(key));
            entityInfo.setMetaValue(key);
            list.add(entityInfo);
        }
        return list;
    }

    //出版社展示信息
    @RequestMapping("/index")
    public String getPublisherMessage(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNum,
                                      @RequestParam(value = "id", required = false) String id,
                                      @RequestParam(value = "title", required = false) String title,
                                      @RequestParam(value = "relatePublisherID", required = false) String relatePublisherID,
                                      Model model) {
        try {
            long start = System.currentTimeMillis();
            HashSet<String> set = new HashSet();
//            List<String> list=new ArrayList<>();
            HashMap<String, String> map = new HashMap<>();
            PageHelper.startPage(pageNum, DEFAULT_PAGESIZE);
            String a = null;
            if (StringUtils.isNotEmpty(relatePublisherID)) {
                a = this.mapr.get(relatePublisherID);
            }
            Page<Publisher> page = publisherService.queryPage(id, title, a);
            List<Publisher> all = publisherService.findAll();
            for (Publisher p : all) {
                if (p.getRelatePublisherID() != null)
                    set.add(p.getRelatePublisherID());
            }
            for (String s : set) {
                Publisher p = publisherService.selectdataById(s);
                if (p.getRelatePublisherID() != null)
                    map.put(p.getId(), p.getTitle());
            }

//            list.addAll(set);
            //搜索保留
            model.addAttribute("id", id);
            model.addAttribute("title", title);
            model.addAttribute("relatePublisherID", relatePublisherID);
            if (page != null && !page.isEmpty()) {
                model.addAttribute("publisherList", page.getResult());
                model.addAttribute("pages", page.getPages());
                model.addAttribute("pageNum", page.getPageNum());
                model.addAttribute("pageSize", DEFAULT_PAGESIZE);
                model.addAttribute("total", page.getTotal());
//                model.addAttribute("RelatePublisherIDset",list );
                model.addAttribute("map", map);
            } else {
                model.addAttribute("publisherList", Collections.emptyList());
                model.addAttribute("pages", 1);
                model.addAttribute("pageNum", 1);
//                model.addAttribute("RelatePublisherIDset", Collections.emptyList());
                model.addAttribute("map", Collections.emptyMap());
            }
            long end = System.currentTimeMillis();
            log.info("出版社信息列表查询耗时：" + (end - start) + "毫秒");
            return "publisher/publisher";
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
        return null;
    }

    @GetMapping("/findRelatePublisherID")
    @ResponseBody
    public Object findRelatePublisherID() {
        HashSet<String> set = new HashSet();
        HashMap<String, String> map = new HashMap<>();
        List<Publisher> all = publisherService.findAll();
        for (Publisher p : all) {
            if (p.getRelatePublisherID() != null)
                set.add(p.getRelatePublisherID());
        }
        for (String s : set) {
            Publisher p = publisherService.selectdataById(s);
            if (p.getRelatePublisherID() != null)
                map.put(p.getId(), p.getTitle());
        }
        return map;
    }
//    @GetMapping("/list")
//    public List<Publisher> listPublishersByIdAndTitleAndRelatePublisherID(@RequestParam(value = "id", required = false) String id,
//                                                                          @RequestParam(value = "title", required = false) String title ,
//                                                                          @RequestParam(value = "relatePublisherID", required = false) String relatePublisherID) {
//
//        if (StringUtils.isBlank(id) && StringUtils.isBlank(title)&&StringUtils.isBlank(relatePublisherID)){
//            log.error("参数列表 id，title,relatePublisherID 不能同时为空");
//            return null;
//        }
//        List<Publisher> list = publisherService.listPublishersByIdAndTitleAndRelatePublisherID(id, title,relatePublisherID);
//        return list;
//    }

    @GetMapping("/save")
    public String PublisherMessage(HttpServletRequest request, Model model) {
        try {
            return "publisher/addPublisher";
        } catch (Exception e) {
            log.warn("Exception {}", e);
        }
        return null;
    }

    @PostMapping("/dosave")
    public String addPublisherMessage(HttpServletRequest request, @RequestBody Publisher publisher) {
        try {
            long start = System.currentTimeMillis();
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            Date date = new Date();
            publisher.setId(UUIDCreater.nextId());
            publisher.setCreateTime(date);
            publisher.setOperator(userDetails.getUsername());
            int status = publisherService.addPubliser(publisher);
            long end = System.currentTimeMillis();
            log.info("添加出版社信息数据耗时：" + (end - start) + "毫秒");
            return "redirect:/publisher/index";
        } catch (Exception e) {
            log.warn("Exception {}", e);
        }
        return null;
    }

//    @GetMapping("/edit")
//    public String editPublisherMessage(@RequestParam("id") String id, Model model) {
//        try {
//            long start = System.currentTimeMillis();
//            Publisher publisher = publisherService.selectdataById(id);
//            model.addAttribute("publisher", publisher);
//            long end = System.currentTimeMillis();
//            log.info("修改出版社信息列表数据耗时：" + (end - start) + "毫秒");
//            return "publisher/editPublisher";
//        } catch (Exception e) {
//            log.warn("Exception {}", e);
//        }
//        return null;
//    }

    @PostMapping("/doedit")
    public String doEditPublisherMessage(@RequestBody Publisher publisher) {
        try {
            long start = System.currentTimeMillis();
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            publisher.setUpdateTime(new Date());
            publisher.setOperator(userDetails.getUsername());
            int status = publisherService.editPublisher(publisher);
            long end = System.currentTimeMillis();
            log.info("修改出版社信息列表数据耗时：" + (end - start) + "毫秒");
            return "redirect:/publisher/index";
        } catch (Exception e) {
            log.warn("Exception {}", e);
        }
        return null;
    }

    /**
     * 跳转编辑页
     *
     * @param id
     * @param model
     * @return
     */
    @GetMapping("/edit")
    public String editPublisherMessage(@RequestParam("id") String id, Model model) {
        try {
            long start = System.currentTimeMillis();
            Publisher publisher = null;
            if (StringUtils.isNotEmpty(id)) {
                publisher = publisherService.selectdataById(id);
            }
            Class clazz = Class.forName("com.apabi.flow.publisher.model.Publisher");

            List<EntityInfo> list = new ArrayList<>();
            Field[] fields = clazz.getDeclaredFields();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss Z yyyy", Locale.UK);
            SimpleDateFormat simpleDateFormat1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            for (Field field : fields) {
                // 字段名
                EntityInfo entityInfo = new EntityInfo();
                String key = field.getName();
                entityInfo.setFieldName(key);
                // 字段值
                String value = null;
                key = TransformPublisherFieldNameUtils.transform(key);
                if ("创建时间".equals(key)) {
                    value = getFieldValueByFieldName(field.getName(), publisher);
                    // 将创建时间转为字符串
                    if (StringUtils.isNotEmpty(value)) {
                        Date parse = simpleDateFormat.parse(value);
                        value = simpleDateFormat1.format(parse);
                    }
                } else if ("最后更新时间".equals(key)) {
                    value = getFieldValueByFieldName(field.getName(), publisher);
                    // 将更新时间转为字符串
                    if (StringUtils.isNotEmpty(value)) {
                        Date parse = simpleDateFormat.parse(value);
                        value = simpleDateFormat1.format(parse);
                    }
                } else if ("出版社分类".equals(key)) {
                    if (publisher.getClassCode() != null) {
                        value = publisher.getClassCode().getDesc();
                    }
                } else if ("出版文献资源类型".equals(key)) {
                    if (publisher.getResourceType() != null) {
                        value = publisher.getResourceType().getDesc();
                    }
                } else if ("名称类型".equals(key)) {
                    if (publisher.getTitleType() != null) {
                        value = publisher.getTitleType().getDesc();
                    }
                } else {
                    // 普通字段值
                    value = getFieldValueByFieldName(field.getName(), publisher);
                }
                entityInfo.setInfo(key);
                entityInfo.setMetaValue(value);
                list.add(entityInfo);
            }
            model.addAttribute("entityInfoList", list);
            long end = System.currentTimeMillis();
            log.info("查询出版社信息耗时：" + (end - start) + "毫秒");
            return "publisher/edit";
        } catch (Exception e) {
            log.warn("Exception {}", e);
        }
        return null;
    }

    // 从excel中导入数据到数据库
    @RequestMapping("/doImport")
    public void importFromExcel() {
        ReadExcelUtils readExcelUtils = new ReadExcelUtils("C:\\Users\\pirui\\Desktop\\import.xlsx");
        Map<Integer, Map<Object, Object>> data = readExcelUtils.getData();
        for (Map.Entry<Integer, Map<Object, Object>> entry : data.entrySet()) {
            Publisher publisher = new Publisher();
            publisher.setId(UUIDCreater.nextId());
            Map<Object, Object> value = entry.getValue();
            for (Map.Entry<Object, Object> dataEntry : value.entrySet()) {
                if ("出版社".equalsIgnoreCase(dataEntry.getKey().toString())) {
                    String title = dataEntry.getValue().toString();
                    publisher.setTitle(title);
                }
                if ("起止时间".equalsIgnoreCase(dataEntry.getKey().toString())) {
                    String date = dataEntry.getValue().toString();
                    String[] split = date.split("-");
                    if (split.length == 2) {
                        String startTime = split[0];
                        String endTime = split[1];
                        if (StringUtils.isNotEmpty(startTime)) {
                            startTime = StringToolUtil.issuedDateFormat(startTime);
                            startTime = startTime.replaceAll(" 00:00:00", "");
                        }
                        if (StringUtils.isNotEmpty(endTime)) {
                            endTime = StringToolUtil.issuedDateFormat(endTime);
                            endTime = endTime.replaceAll(" 00:00:00", "");
                        }
                        publisher.setStartDate(startTime);
                        publisher.setEndDate(endTime);
                    } else {
                        String startTime = split[0];
                        if (StringUtils.isNotEmpty(startTime)) {
                            startTime = StringToolUtil.issuedDateFormat(startTime);
                            startTime = startTime.replaceAll(" 00:00:00", "");
                        }
                        publisher.setStartDate(startTime);
                    }
                }
                if ("ISBN".equalsIgnoreCase(dataEntry.getKey().toString())) {
                    String isbn = dataEntry.getValue().toString();
                    publisher.setIsbn(isbn);
                }
                if ("出版地".equalsIgnoreCase(dataEntry.getKey().toString())) {
                    String place = dataEntry.getValue().toString();
                    publisher.setPlace(place);
                }
                if ("简介".equalsIgnoreCase(dataEntry.getKey().toString())) {
                    String summary = dataEntry.getValue().toString();
                    publisher.setSummary(summary);
                }
            }
            publisher.setCreateTime(new Date());
            publisher.setUpdateTime(new Date());
            publisherService.addPubliser(publisher);
        }

    }

    // 查看出版社详细数据
    @RequestMapping("/publisherDetail")
    public String publisherDetail(@RequestParam("id") String id, Model model) throws ClassNotFoundException, NoSuchFieldException, IllegalAccessException, ParseException {
        Publisher publisher = null;
        if (StringUtils.isNotEmpty(id)) {
            publisher = publisherService.selectdataById(id);
        }
        Class clazz = Class.forName("com.apabi.flow.publisher.model.Publisher");
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
            key = TransformPublisherFieldNameUtils.transform(key);
            if ("创建时间".equals(key)) {
                value = getFieldValueByFieldName(field.getName(), publisher);
                // 将创建时间转为字符串
                if (StringUtils.isNotEmpty(value)) {
                    Date parse = simpleDateFormat.parse(value);
                    value = simpleDateFormat1.format(parse);
                }
            } else if ("最后更新时间".equals(key)) {
                value = getFieldValueByFieldName(field.getName(), publisher);
                // 将更新时间转为字符串
                if (StringUtils.isNotEmpty(value)) {
                    Date parse = simpleDateFormat.parse(value);
                    value = simpleDateFormat1.format(parse);
                }
            } else if ("出版社分类".equals(key)) {
                if (publisher.getClassCode() != null) {
                    value = publisher.getClassCode().getDesc();
                }
            } else if ("名称类型".equals(key)) {
                if (publisher.getTitleType() != null) {
                    value = publisher.getTitleType().getDesc();
                }
            } else if ("出版文献资源类型".equals(key)) {
                if (publisher.getResourceType() != null) {
                    value = publisher.getResourceType().getDesc();
                }
            } else {
                // 普通字段值
                value = getFieldValueByFieldName(field.getName(), publisher);
            }
            map.put(key, value);
        }
        model.addAttribute("publisherMap", map);
        model.addAttribute("id", id);
        return "publisher/publisherDetail";
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


    @RequestMapping("compare")
    public void compareStandardWithDB(){
        publisherService.compareStandardWithDB();
    }
}
