package com.apabi.flow.auth.controller;

import com.apabi.flow.auth.dao.BookListMapper;
import com.apabi.flow.auth.model.BookList;
import com.apabi.flow.auth.service.BookListService;
import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.common.util.ParamsUtils;
import com.apabi.flow.config.ApplicationConfig;
import com.apabi.flow.publisher.model.Publisher;
import com.apabi.flow.publisher.service.PublisherService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


@Controller
@RequestMapping("/bookList")
public class BookListController {
    private Logger log = LoggerFactory.getLogger(BookListController.class);

    @Autowired
    private BookListService bookListService;

    @Autowired
    private PublisherService publisherService;

    @Autowired
    BookListMapper bookListMapper;

    @Autowired
    ApplicationConfig config;


    @GetMapping("/index")
    public String index(@RequestParam(value = "copyrightOwnerId", required = false) String copyrightOwnerId,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date authEndDate,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date authEndDate1,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date submitDate,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date submitDate1,
                        @RequestParam(value = "bookListNum", required = false) Integer bookListNum,
                        @RequestParam(value = "authorizeNum", required = false) Integer authorizeNum,
                        @RequestParam(value = "coopertor", required = false) String coopertor,
                        @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNum,
                        @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                        Model model) {
        try {

            long start = System.currentTimeMillis();
            List<Publisher> publishers = publisherService.findAll();
            //搜索保留
            model.addAttribute("copyrightOwnerId", copyrightOwnerId);
            model.addAttribute("authEndDate", authEndDate);
            model.addAttribute("authEndDate1", authEndDate1);
            model.addAttribute("submitDate", submitDate);
            model.addAttribute("submitDate1", submitDate1);
            model.addAttribute("bookListNum", bookListNum);
            model.addAttribute("authorizeNum", authorizeNum);
            model.addAttribute("coopertor", coopertor);
            model.addAttribute("publishers", publishers);
            PageHelper.startPage(pageNum, pageSize);
            Map<String, Object> paramsMap = new HashMap<>();
            ParamsUtils.checkParameterAndPut2Map(paramsMap, "copyrightOwnerId", copyrightOwnerId);
            paramsMap.put("authEndDate", authEndDate);
            paramsMap.put("submitDate", submitDate);
            paramsMap.put("bookListNum", bookListNum);
            paramsMap.put("authorizeNum", authorizeNum);
            if (authEndDate1 != null) {
                paramsMap.put("authEndDate1", new Date(new DateTime(authEndDate1.getTime()).plusDays(1).getMillis()));
            }
            if (submitDate1 != null) {
                paramsMap.put("submitDate1", new Date(new DateTime(submitDate1.getTime()).plusDays(1).getMillis()));
            }
            Page<BookList> page = bookListService.listBookList(paramsMap);
            if (page != null && !page.isEmpty()) {
                model.addAttribute("bookList", page.getResult());
                model.addAttribute("pages", page.getPages());
                model.addAttribute("pageNum", page.getPageNum());
            } else {
                model.addAttribute("bookList", Collections.emptyList());
                model.addAttribute("pages", 1);
                model.addAttribute("pageNum", 1);
            }
            long end = System.currentTimeMillis();
            log.info("书单信息列表查询耗时：" + (end - start) + "毫秒");
            return "auth/bookList";
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
        return "auth/bookList";
    }

    private static Map<String, String> publisher = new HashMap<>();

    @RequestMapping("/add/index")
    public String addIndex(Model model) {
        List<Publisher> publishers = publisherService.findAll();
        model.addAttribute("publishers", publishers);
        for (Publisher p : publishers) {
            publisher.put(p.getId(), p.getTitle());
        }
        return "/auth/addBookList";
    }

    @PostMapping("/add")
    public String add(@RequestBody BookList bookList) {
        String s = UUIDCreater.nextId();
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        bookList.setOpertor(userDetails.getUsername());
        bookList.setOperteDate(new Date());
        bookList.setId(s);
        bookList.setCopyrightOwner(publisher.get(bookList.getCopyrightOwnerId()));
        int add = bookListService.add(bookList);
        return "redirect:/bookList/index";
    }

    @RequestMapping("/edit/index")
    public String editIndex(@RequestParam String id, Model model) {
        BookList bookList = null;
        if (StringUtils.isNotBlank(id)) {
            bookList = bookListService.selectByPrimaryKey(id);
        }
        List<Publisher> publishers = publisherService.findAll();
        model.addAttribute("publishers", publishers);
        for (Publisher p : publishers) {
            publisher.put(p.getId(), p.getTitle());
        }
        model.addAttribute("submitDate", bookList.getSubmitDate().toString());
        model.addAttribute("authEndDate", bookList.getAuthEndDate().toString());
        model.addAttribute("obtainDate", bookList.getObtainDate().toString());
        model.addAttribute("bookList", bookList);
        return "/auth/editBookList";
    }

    @PostMapping("/update")
    public String update(@RequestBody BookList bookList) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        bookList.setOpertor(userDetails.getUsername());
        bookList.setOperteDate(new Date());
        bookList.setCopyrightOwner(publisher.get(bookList.getCopyrightOwnerId()));
        int add = bookListService.updateByPrimaryKeySelective(bookList);
        return "redirect:/bookList/index";
    }

    @RequestMapping("/deleteById")
    public String deleteById(@RequestParam String id) {
        int i = bookListService.deleteByPrimaryKey(id);
        return "redirect:/bookList/index";
    }

    @RequestMapping("/bookListFileAdd")
    public String bookListFileAdd(@RequestParam String id,Model model) {
        model.addAttribute("id",id);
        return "/auth/bookListFileAdd";
    }
    //上传授权书单文件
    @RequestMapping(value = "/bookListFileInsert", method = RequestMethod.POST)
    @ResponseBody
    public String bookListInsert(@RequestParam(value = "files", required = true) MultipartFile[] files,
                                @RequestParam(value = "id",required = true)String id) {
        if (files != null&&StringUtils.isNotBlank(id)) {
            String filePath = "";
            try {
                BookList bookList = bookListMapper.selectByPrimaryKey(id);
                if(StringUtils.isNotBlank(bookList.getFileName())||StringUtils.isNotBlank(bookList.getFilePath())){
                    return "exist";
                }
                //上传图书到服务端指定目录
                long start = System.currentTimeMillis();
                List<String> fileList = new ArrayList<>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dirPath = config.getUpAuthFile() + File.separator +sdf.format(new Date())+File.separator +id;
                File dir = new File(dirPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                for (MultipartFile file : files) {
                    File newFile = new File(dir, file.getOriginalFilename());
                    file.transferTo(newFile);
                    fileList.add(newFile.getPath());
                }
                long end = System.currentTimeMillis();
                log.info(files.length + "授权书单文件！耗时：" + (end - start) + "毫秒");
                //解析xml文件
                start = System.currentTimeMillis();
                if (fileList != null && fileList.size() > 0) {
                    int res = 0;
                    for (String path : fileList) {
                        filePath = path;
                        res += bookListService.saveAgreementFileNameAndPath(id,filePath);
                    }
                    if (res > 0) {
                        end = System.currentTimeMillis();
                        log.info(res + "授权书单文件保存数据库成功！耗时：" + (end - start) + "毫秒");
                        return "success";
                    } else if (res < 0) {
                        return "exist";
                    }
                }
            } catch (Exception e) {
                log.warn("上传授权书单文件：" + e);
            }
        }
        return "error";
    }
    //上传授权书单文件2
    @RequestMapping(value = "/bookListFileInsert2", method = RequestMethod.POST)
    @ResponseBody
    public String bookListFileInsert2(@RequestParam(value = "files", required = true) MultipartFile[] files,
                                @RequestParam(value = "id",required = true)String id) {
        if (files != null&&StringUtils.isNotBlank(id)) {
            String filePath = "";
            try {
                BookList bookList = bookListMapper.selectByPrimaryKey(id);
                if(StringUtils.isNotBlank(bookList.getFileName())||StringUtils.isNotBlank(bookList.getFilePath())){
                    File file=new File(bookList.getFilePath());
                    boolean delete = file.delete();
                    if(delete){
                        log.info("删除授权书单id：{}的附件成功",id);
                    }else{
                        log.info("删除授权书单id：{}的附件失败",id);
                    }
                }
                //上传图书到服务端指定目录
                long start = System.currentTimeMillis();
                List<String> fileList = new ArrayList<>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dirPath = config.getUpAuthFile() + File.separator +sdf.format(new Date())+File.separator +id;
                File dir = new File(dirPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                for (MultipartFile file : files) {
                    File newFile = new File(dir, file.getOriginalFilename());
                    file.transferTo(newFile);
                    fileList.add(newFile.getPath());
                }
                long end = System.currentTimeMillis();
                log.info(files.length + "授权书单文件！耗时：" + (end - start) + "毫秒");
                //解析xml文件
                start = System.currentTimeMillis();
                if (fileList != null && fileList.size() > 0) {
                    int res = 0;
                    for (String path : fileList) {
                        filePath = path;
                        res += bookListService.saveAgreementFileNameAndPath(id,filePath);
                    }
                    if (res > 0) {
                        end = System.currentTimeMillis();
                        log.info(res + "授权书单文件保存数据库成功！耗时：" + (end - start) + "毫秒");
                        return "success";
                    } else if (res < 0) {
                        return "exist";
                    }
                }
            } catch (Exception e) {
                log.warn("上传版权协议文件：" + e);
            }
        }
        return "error";
    }
    @RequestMapping("/bookListFileDownload")
    @ResponseBody
    public String bookListFileDownload(@RequestParam String id, HttpServletResponse response) throws ParseException {
        try {
            BookList bookList = bookListMapper.selectByPrimaryKey(id);
            if(StringUtils.isBlank(bookList.getFileName())||StringUtils.isBlank(bookList.getFilePath())){
                return "<script type='text/javascript'>alert('当前授权书单无附件！');history.back();</script>";
            }
            // 设置响应头
            response.setContentType("application/binary;charset=UTF-8");
            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            String path=bookList.getFilePath();
            // path是指欲下载的文件的路径。
            File file = new File(path);
            // 取得文件名。
            String filename = file.getName();
            // 取得文件的后缀名。
//            String ext = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();

            // 以流的形式下载文件。
            InputStream fis = new BufferedInputStream(new FileInputStream(path));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes(),"ISO8859-1"));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
//            response.setContentType("application/octet-stream");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "<script type='text/javascript'>alert('下载成功！');history.back();</script>";
    }
}
