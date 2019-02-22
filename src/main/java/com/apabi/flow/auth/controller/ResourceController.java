package com.apabi.flow.auth.controller;

import com.apabi.flow.auth.dao.CopyrightAgreementMapper;
import com.apabi.flow.auth.dao.CopyrightOwnerMapper;
import com.apabi.flow.auth.model.CopyrightAgreement;
import com.apabi.flow.auth.model.CopyrightOwner;
import com.apabi.flow.auth.model.Resource;
import com.apabi.flow.auth.service.AuthService;
import com.apabi.flow.auth.service.CopyrightOwnerService;
import com.apabi.flow.auth.service.ResourceService;
import com.apabi.flow.common.ResultEntity;
import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.common.util.ParamsUtils;
import com.apabi.flow.config.ApplicationConfig;
import com.apabi.flow.processing.model.Bibliotheca;
import com.apabi.flow.processing.model.BibliothecaExcelModel;
import com.apabi.flow.processing.service.BibliothecaService;
import com.apabi.flow.processing.util.ReadExcelTextUtils;
import com.apabi.flow.publisher.model.Publisher;
import com.apabi.flow.publisher.service.PublisherService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 功能描述： <br>
 * <作者 controller层>
 *
 * @author supeng
 * @date 2018/8/27 16:47
 * @since 1.0.0
 */
@Controller
@RequestMapping("/resource")
public class ResourceController {
    private Logger log = LoggerFactory.getLogger(ResourceController.class);

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private PublisherService publisherService;
    @Autowired
    private CopyrightOwnerService copyrightOwnerService;

    private static Map<String ,String> publisherMap=new HashMap<>();
    private static Map<String ,String> copyrightOwnerMap=new HashMap<>();

    @GetMapping("/index")
    public String index(@RequestParam(value = "booklistNum", required = false) String booklistNum,
                        @RequestParam(value = "title", required = false) String title,
                        @RequestParam(value = "creator", required = false) String creator,
                        @RequestParam(value = "metaId", required = false) String metaId,
                        @RequestParam(value = "copyrightOwner", required = false) String copyrightOwner,
                        @RequestParam(value = "isbn", required = false) String isbn,
                        @RequestParam(value = "status", required = false) Integer status,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") String startDate,
//                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate1,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") String endDate,
//                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate1,
                        @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNum,
                        @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                        Model model) {
        try {
            List<CopyrightOwner> all = copyrightOwnerService.findAll();
            model.addAttribute("CopyrightOwner",all );
            for (CopyrightOwner copyrightOwner1:all){
                copyrightOwnerMap.put(copyrightOwner1.getId(),copyrightOwner1.getName());
            }
            List<Publisher> publishers = publisherService.findAll();
//            model.addAttribute("publishers", publishers);
            for (Publisher publisher1:publishers) {
                publisherMap.put(publisher1.getId(),publisher1.getTitle());
            }

            long start = System.currentTimeMillis();
            //搜索保留
            model.addAttribute("booklistNum", booklistNum);
            model.addAttribute("title", title);
            model.addAttribute("creator", creator);
            model.addAttribute("metaId", metaId);
            model.addAttribute("copyrightOwner", copyrightOwner);
            model.addAttribute("isbn", isbn);
            model.addAttribute("status", status);
            model.addAttribute("startDate", startDate);
//            model.addAttribute("startDate1", startDate1);
            model.addAttribute("endDate", endDate);
//            model.addAttribute("endDate1", endDate1);
            PageHelper.startPage(pageNum, pageSize);
            Map<String, Object> paramsMap = new HashMap<>();
            ParamsUtils.checkParameterAndPut2Map(paramsMap, "booklistNum", booklistNum, "title", title,"creator",creator,"metaId",metaId,"copyrightOwner",copyrightOwner,"isbn", isbn);
            paramsMap.put("startDate", startDate);
            paramsMap.put("endDate", endDate);
            paramsMap.put("status", status);
//            if (startDate1 != null) {
//                paramsMap.put("startDate1", new Date(new DateTime(startDate1.getTime()).plusDays(1).getMillis()));
//            }
//            if (endDate1 != null) {
//                paramsMap.put("endDate1", new Date(new DateTime(endDate1.getTime()).plusDays(1).getMillis()));
//            }
            Page<Resource> page = resourceService.listResource(paramsMap);
//            page.forEach(r->r.setPublisher(publisherMap.get(r.getPublisher())));
//            page.forEach(r->r.setCopyrightOwner(copyrightOwnerMap.get(r.getCopyrightOwner())));
            if (page != null && !page.isEmpty()) {
                model.addAttribute("ResourceList", page.getResult());
                model.addAttribute("pages", page.getPages());
                model.addAttribute("pageNum", page.getPageNum());
                model.addAttribute("pageSize", page.getPageSize());
                model.addAttribute("total", page.getTotal());
            } else {
                model.addAttribute("ResourceList", Collections.emptyList());
                model.addAttribute("pages", 1);
                model.addAttribute("pageNum", 1);
            }
            long end = System.currentTimeMillis();
            log.info("协议资源信息列表查询耗时：" + (end - start) + "毫秒");
            return "auth/resource";
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
        return "auth/resource";
    }

//    @RequestMapping("/add/index")
//    public String addIndex(Model model) {
//        return "/auth/addResource";
//    }
//    @PostMapping("/add")
//    public String add(@RequestBody Resource resource) {
//        String s = UUIDCreater.nextId();
//        resource.setResrId(s);
//        int add = resourceService.insert(resource);
//        return "redirect:/resource/index";
//    }

    @RequestMapping("/deleteById")
    public String deleteById(@RequestParam String resrId) {
        Integer id=Integer.parseInt(resrId.replace(",",""));
        int i = resourceService.deleteByPrimaryKey(id);
        return "redirect:/resource/index";
    }


    @PostMapping("/update")
    public String update(@RequestBody Resource resource) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        resource.setOperator(userDetails.getUsername());
        resource.setOperateDate(new Date());
        if(StringUtils.isNotBlank(resource.getPublisher())){
            String[] split = resource.getPublisher().split(",");
            resource.setPublisher(split[1]);
            resource.setPublisherId(split[0]);
        }
        int add = resourceService.updateByPrimaryKeySelective(resource);
        return "redirect:/resource/index";
    }

    @RequestMapping("/edit/index")
    public String editIndex(@RequestParam String resrId, Model model) throws ParseException {
        Resource resource = null;
//        if (StringUtils.isNotBlank(resrId)) {
        Integer id=Integer.parseInt(resrId.replace(",",""));
            resource  = resourceService.selectByPrimaryKey(id);
//        }
        List<Publisher> publishers = publisherService.findAll();
        model.addAttribute("publishers", publishers);
        model.addAttribute("resource", resource);
        model.addAttribute("issuedDate", resource.getIssuedDate().toString());
        return "/auth/editResource";
    }

    @RequestMapping("exportData")
    @ResponseBody
    public String exportData(@RequestParam(value = "booklistNum", required = false) String booklistNum,
                             @RequestParam(value = "title", required = false) String title,
                             @RequestParam(value = "creator", required = false) String creator,
                             @RequestParam(value = "metaId", required = false) String metaId,
                             @RequestParam(value = "publisher", required = false) String publisher,
                             @RequestParam(value = "isbn", required = false) String isbn,
                             @RequestParam(value = "copyrightOwner", required = false) String copyrightOwner,
                             @RequestParam(value = "status", required = false) Integer status,
                             @RequestParam(value = "startDate", required = false) String startDate,
                             @RequestParam(value = "endDate", required = false) String endDate,
                             HttpServletResponse response) throws IOException {
        try {
            // 设置响应头
            response.setContentType("application/binary;charset=UTF-8");
            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            // 获取所有的isbn列表
            Map<String, Object> paramsMap = new HashMap<>();
            ParamsUtils.checkParameterAndPut2Map(paramsMap, "booklistNum", booklistNum, "title", title,"creator",creator,"metaId",metaId,"publisher",publisher,"isbn", isbn,"copyrightOwner",copyrightOwner);
            paramsMap.put("startDate", startDate);
            paramsMap.put("endDate", endDate);
            paramsMap.put("status", status);
//            if (startDate1 != null) {
//                paramsMap.put("startDate1", new Date(new DateTime(startDate1.getTime()).plusDays(1).getMillis()));
//            }
//            if (endDate1 != null) {
//                paramsMap.put("endDate1", new Date(new DateTime(endDate1.getTime()).plusDays(1).getMillis()));
//            }
            List<Resource> list = resourceService.listResource1(paramsMap);
            if (list == null || list.isEmpty()){
                return "<script type='text/javascript'>alert('当前条件查询结果为空！');history.back();</script>";
            }
            else if(list.size()>=10000){
                return "<script type='text/javascript'>alert('当前条件查询结果大于1000条,不提供导出！');history.back();</script>";
            }
            // 设置文件名
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HHmmss");
            String time = simpleDateFormat.format(date);
            File file = new File(time + "-" + UUIDCreater.nextId() + ".xls");
            String fileName = new String(file.getName().getBytes("UTF-8"), "ISO-8859-1");
            // 设置excel的表头
            String[] excelTitle = {"序号","编号", "书号", "书名", "作者", "出版社", "出版日期", "纸书价格（元）", "电子书价格（元）", "版权期限起始时间",
                    "版权期限终止时间","唯一标识（MetaId）","批次号"};
            // 将内存中读取到的内容写入到excel
            resourceService.writeData2Excel(2,fileName, excelTitle, list, "sheet1", response);
            return "<script type='text/javascript'>alert('导出成功！');history.back();</script>";
        } catch (Exception e) {
            e.printStackTrace();
            return "<script type='text/javascript'>alert('" + e.getMessage() + "');history.back();</script>";
        }
    }

    @PostMapping("/dataImport")
    @ResponseBody
    public ResultEntity dataImport(@RequestParam("file")MultipartFile file){
        // 读取Excel工具类
        Map<Integer, Map<Object, Object>> data = null;
        try(InputStream inputStream = file.getInputStream()){
            String fileName = file.getOriginalFilename();
            ReadExcelTextUtils readExcelTextUtils = new ReadExcelTextUtils(inputStream, fileName);
            // 读取Excel中的内容
            data = readExcelTextUtils.getDataByInputStream();
            if (data == null || data.isEmpty()){
                throw new Exception();
            }
        }catch (IOException e){
            e.printStackTrace();
            return new ResultEntity(403, "文件读取出错，请重新尝试或联系管理员！");
        }catch (Exception e){
            e.printStackTrace();
            return new ResultEntity(500, "文件出错，请检查文件格式是否正确或内容是否完整！");
        }

        //对数据进行转换
        try {
            List<Resource> list = resourceService.resolveResource(data);

            if (list == null || list.isEmpty()){
                return new ResultEntity(500, "数据为空，导入失败！");
            }

            if (resourceService.updateByBatchNumAndMetaId(list)){
                return new ResultEntity(200, "导入成功！");
            }
            return new ResultEntity(500, "导入失败！");
        }catch (Exception e){
            return new ResultEntity(500, e.getMessage());
        }
    }
}
