package com.apabi.flow.auth.controller;

import com.apabi.flow.auth.dao.CopyrightAgreementMapper;
import com.apabi.flow.auth.model.CopyrightAgreement;
import com.apabi.flow.auth.model.CopyrightOwner;
import com.apabi.flow.auth.service.AuthService;
import com.apabi.flow.auth.service.CopyrightOwnerService;
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

/**
 * 功能描述： <br>
 * <作者 controller层>
 *
 * @author supeng
 * @date 2018/8/27 16:47
 * @since 1.0.0
 */
@Controller
@RequestMapping("/auth")
public class AuthController {
    private Logger log = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private AuthService authService;

    @Autowired
    private CopyrightOwnerService copyrightOwnerService;

    @Autowired
    ApplicationConfig config;

    @Autowired
    CopyrightAgreementMapper copyrightAgreementMapper;

    @GetMapping("/index")
    public String index(@RequestParam(value = "copyrightOwnerId", required = false) String copyrightOwnerId,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate1,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
                        @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate1,
                        @RequestParam(value = "authType", required = false) Integer authType,
                        @RequestParam(value = "contentManagerName", required = false) String contentManagerName,
                        @RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNum,
                        @RequestParam(value = "pageSize", required = false, defaultValue = "10") Integer pageSize,
                        Model model) {
        try {
            List<CopyrightAgreement> lists = authService.findAll();
            Calendar cal = Calendar.getInstance();
            for (CopyrightAgreement copyrightAgreement : lists) {
                cal.setTime(copyrightAgreement.getEndDate());
                cal.add(Calendar.YEAR, copyrightAgreement.getYearNum());
                copyrightAgreement.setEndDate(cal.getTime());
                if (copyrightAgreement.getEndDate().compareTo(new Date()) >= 0) {
                    if (copyrightAgreement.getStatus() != 1) {
                        copyrightAgreement.setStatus(1);
                        authService.updateStatusByPrimaryKeySelective(copyrightAgreement);
                    }
                } else {
                    if (copyrightAgreement.getStatus() != 0) {
                        copyrightAgreement.setStatus(0);
                        authService.updateStatusByPrimaryKeySelective(copyrightAgreement);
                    }
                }
            }

            List<CopyrightOwner> copyrightOwners = copyrightOwnerService.findAll();
            long start = System.currentTimeMillis();

            //搜索保留
            model.addAttribute("copyrightOwnerId", copyrightOwnerId);
            model.addAttribute("startDate", startDate);
            model.addAttribute("startDate1", startDate1);
            model.addAttribute("endDate", endDate);
            model.addAttribute("endDate1", endDate1);
            model.addAttribute("authType", authType);
            model.addAttribute("contentManagerName", contentManagerName);
            model.addAttribute("copyrightOwners", copyrightOwners);
            PageHelper.startPage(pageNum, pageSize);
            Map<String, Object> paramsMap = new HashMap<>();
            ParamsUtils.checkParameterAndPut2Map(paramsMap, "copyrightOwnerId", copyrightOwnerId, "contentManagerName", contentManagerName);
            paramsMap.put("startDate", startDate);
            paramsMap.put("endDate", endDate);
            paramsMap.put("authType", authType);
            if (startDate1 != null) {
                paramsMap.put("startDate1", new Date(new DateTime(startDate1.getTime()).plusDays(1).getMillis()));
            }
            if (endDate1 != null) {
                paramsMap.put("endDate1", new Date(new DateTime(endDate1.getTime()).plusDays(1).getMillis()));
            }
            Page<CopyrightAgreement> page = authService.listCopyrightAgreement(paramsMap);
            if (page != null && !page.isEmpty()) {
                model.addAttribute("CopyrightAgreementList", page.getResult());
                model.addAttribute("pages", page.getPages());
                model.addAttribute("pageNum", page.getPageNum());
            } else {
                model.addAttribute("CopyrightAgreementList", Collections.emptyList());
                model.addAttribute("pages", 1);
                model.addAttribute("pageNum", 1);
            }
            long end = System.currentTimeMillis();
            log.info("版权信息列表查询耗时：" + (end - start) + "毫秒");
            return "auth/auth";
        } catch (Exception e) {
            log.error("Exception {}", e);
        }
        return "auth/auth";
    }

    private static Map<String, String> copyrightOwner = new HashMap<>();

    @RequestMapping("/add/index")
    public String addIndex(Model model) {
        List<CopyrightOwner> copyrightOwners = copyrightOwnerService.findAll();
        model.addAttribute("copyrightOwners", copyrightOwners);
        for (CopyrightOwner p : copyrightOwners) {
            copyrightOwner.put(p.getId(), p.getName());
        }
        return "/auth/addAuth";
    }

    @RequestMapping("/deleteById")
    public String deleteById(@RequestParam String caid) {
        int i = authService.deleteByPrimaryKey(caid);
        return "/auth/index";
    }

    @PostMapping("/add")
    public String add(@RequestBody CopyrightAgreement copyrightAgreement) {
        String s = UUIDCreater.nextId();
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        copyrightAgreement.setOperator(userDetails.getUsername());
        copyrightAgreement.setOperatedate(new Date());
        copyrightAgreement.setCaid(s);
        copyrightAgreement.setCopyrightOwner(copyrightOwner.get(copyrightAgreement.getCopyrightOwnerId()));
        //0:版权到期,1:版权期内
        Calendar cal = Calendar.getInstance();
        cal.setTime(copyrightAgreement.getEndDate());
        cal.add(Calendar.YEAR, copyrightAgreement.getYearNum());
        if (cal.getTime().compareTo(new Date()) >= 0) {
            copyrightAgreement.setStatus(1);
        } else {
            copyrightAgreement.setStatus(0);
        }
        int add = authService.add(copyrightAgreement);
        return "redirect:/auth/index";
    }

    @PostMapping("/update")
    public String update(@RequestBody CopyrightAgreement copyrightAgreement) {
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        copyrightAgreement.setOperator(userDetails.getUsername());
        copyrightAgreement.setOperatedate(new Date());
        copyrightAgreement.setCopyrightOwner(copyrightOwner.get(copyrightAgreement.getCopyrightOwnerId()));
        int add = authService.updateByPrimaryKeySelective(copyrightAgreement);
        return "redirect:/auth/index";
    }

    @RequestMapping("/edit/index")
    public String editIndex(@RequestParam String caid, Model model) throws ParseException {
        CopyrightAgreement copyrightAgreement = null;
        if (StringUtils.isNotBlank(caid)) {
            copyrightAgreement = authService.selectByPrimaryKey(caid);
        }
        model.addAttribute("startDate", copyrightAgreement.getStartDate().toString());
        model.addAttribute("endDate", copyrightAgreement.getEndDate().toString());
        model.addAttribute("signDate", copyrightAgreement.getSignDate().toString());
        model.addAttribute("obtainDate", copyrightAgreement.getObtainDate().toString());
        model.addAttribute("copyrightAgreement", copyrightAgreement);
        List<CopyrightOwner> copyrightOwners = copyrightOwnerService.findAll();
        model.addAttribute("copyrightOwners", copyrightOwners);
        for (CopyrightOwner p : copyrightOwners) {
            copyrightOwner.put(p.getId(), p.getName());
        }
        return "/auth/editAuth";
    }

    @RequestMapping("/authFileAdd")
    public String authFileAdd(@RequestParam String caid, Model model) {
        model.addAttribute("caid", caid);
        return "/auth/authFileAdd";
    }

    //上传版权协议文件
    @RequestMapping(value = "/authFileInsert", method = RequestMethod.POST)
    @ResponseBody
    public String authInsert(@RequestParam(value = "files", required = true) MultipartFile[] files,
                             @RequestParam(value = "caid", required = true) String caid) {
        if (files != null && StringUtils.isNotBlank(caid)) {
            String filePath = "";
            try {
                CopyrightAgreement copyrightAgreement = copyrightAgreementMapper.selectByPrimaryKey(caid);
                if (StringUtils.isNotBlank(copyrightAgreement.getAgreementFileName()) || StringUtils.isNotBlank(copyrightAgreement.getAgreementFilePath())) {
                    return "exist";
                }
                //上传图书到服务端指定目录
                long start = System.currentTimeMillis();
                List<String> fileList = new ArrayList<>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dirPath = config.getUpAuthFile() + File.separator + sdf.format(new Date()) + File.separator + caid;
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
                log.info(files.length + "版权协议文件！耗时：" + (end - start) + "毫秒");
                //解析xml文件
                start = System.currentTimeMillis();
                if (fileList != null && fileList.size() > 0) {
                    int res = 0;
                    for (String path : fileList) {
                        filePath = path;
                        res += authService.saveAgreementFileNameAndPath(caid, filePath);
                    }
                    if (res > 0) {
                        end = System.currentTimeMillis();
                        log.info(res + "版权协议文件保存数据库成功！耗时：" + (end - start) + "毫秒");
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

    //上传版权协议文件2
    @RequestMapping(value = "/authFileInsert2", method = RequestMethod.POST)
    @ResponseBody
    public String authInsert2(@RequestParam(value = "files", required = true) MultipartFile[] files,
                              @RequestParam(value = "caid", required = true) String caid) {
        if (files != null && StringUtils.isNotBlank(caid)) {
            String filePath = "";
            try {
                CopyrightAgreement copyrightAgreement = copyrightAgreementMapper.selectByPrimaryKey(caid);
                if (StringUtils.isNotBlank(copyrightAgreement.getAgreementFileName()) || StringUtils.isNotBlank(copyrightAgreement.getAgreementFilePath())) {
                    File file = new File(copyrightAgreement.getAgreementFilePath());
                    boolean delete = file.delete();
                    if (delete) {
                        log.info("删除版权协议id：{}的附件成功", caid);
                    } else {
                        log.info("删除版权协议id：{}的附件失败", caid);
                    }
                }
                //上传图书到服务端指定目录
                long start = System.currentTimeMillis();
                List<String> fileList = new ArrayList<>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dirPath = config.getUpAuthFile() + File.separator + sdf.format(new Date()) + File.separator + caid;
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
                log.info(files.length + "版权协议文件！耗时：" + (end - start) + "毫秒");
                //解析xml文件
                start = System.currentTimeMillis();
                if (fileList != null && fileList.size() > 0) {
                    int res = 0;
                    for (String path : fileList) {
                        filePath = path;
                        res += authService.saveAgreementFileNameAndPath(caid, filePath);
                    }
                    if (res > 0) {
                        end = System.currentTimeMillis();
                        log.info(res + "版权协议文件保存数据库成功！耗时：" + (end - start) + "毫秒");
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

    @RequestMapping("/authFileDownload")
    @ResponseBody
    public String authFileDownload(@RequestParam String caid, HttpServletResponse response) throws ParseException {
        try {
            CopyrightAgreement copyrightAgreement = copyrightAgreementMapper.selectByPrimaryKey(caid);
            if (StringUtils.isBlank(copyrightAgreement.getAgreementFileName()) || StringUtils.isBlank(copyrightAgreement.getAgreementFilePath())) {
                return "<script type='text/javascript'>alert('当前版权协议无附件！');history.back();</script>";
            }
            // 设置响应头
            response.setContentType("application/binary;charset=UTF-8");
            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            String path = copyrightAgreement.getAgreementFilePath();
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
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes(), "ISO8859-1"));
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
