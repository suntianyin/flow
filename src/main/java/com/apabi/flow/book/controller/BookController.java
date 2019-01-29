package com.apabi.flow.book.controller;

import com.apabi.flow.book.dao.BookPageMapper;
import com.apabi.flow.book.dao.PageAssemblyQueueMapper;
import com.apabi.flow.book.dao.PageCrawledQueueMapper;
import com.apabi.flow.book.dao.PageCrawledTempMapper;
import com.apabi.flow.book.entity.BookMetaMap;
import com.apabi.flow.book.model.*;
import com.apabi.flow.book.service.*;
import com.apabi.flow.book.util.BookUtil;
import com.apabi.flow.book.util.HttpUtils;
import com.apabi.flow.book.util.ReadBook;
import com.apabi.flow.book.util.ReadLog;
import com.apabi.flow.common.CommEntity;
import com.apabi.flow.common.ResultEntity;
import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.common.util.ParamsUtils;
import com.apabi.flow.config.ApplicationConfig;
import com.apabi.flow.douban.dao.ApabiBookMetaDataTempDao;
import com.apabi.flow.douban.model.ApabiBookMetaDataTemp;
import com.apabi.flow.douban.util.StringToolUtil;
import com.apabi.flow.processing.constant.BizException;
import com.apabi.flow.processing.model.Bibliotheca;
import com.apabi.flow.processing.util.ReadExcelTextUtils;
import com.apabi.flow.systemconf.dao.SystemConfMapper;
import com.apabi.flow.systemconf.model.SystemConf;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/*import org.springframework.data.domain.Page;*/

/**
 * @author guanpp
 * @date 2018/7/31 14:57
 * @description
 */
@Controller("book")
@RequestMapping(value = "/book")
public class BookController {

    private Logger log = LoggerFactory.getLogger(BookController.class);

    public static final Integer DEFAULT_PAGESIZE = 10;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //存储批量上传结果
    private static String RES_LIST;

    @Autowired
    BookChapterService bookChapterService;

    @Autowired
    BookShardService bookShardService;

    @Autowired
    BookMetaService bookMetaService;

    @Autowired
    ReadBook readBook;

    @Autowired
    BookPageMapper bookPageMapper;

    @Autowired
    private BookPageService bookPageService;

    @Autowired
    MongoBookService mongoBookService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    PageCrawledQueueMapper pageCrawledQueueMapper;

    @Autowired
    PageAssemblyQueueMapper pageAssemblyQueueMapper;

    @Autowired
    PageCrawledTempMapper pageCrawledTempMapper;

    @Autowired
    ApplicationConfig config;
    @Autowired
    SystemConfMapper systemConfMapper;
    @Autowired
    ApabiBookMetaDataTempDao apabiBookMetaDataTempDao;

    @RequestMapping(value = "/index")
    public String index() {
        return "index";
    }

    //批量上传结果
    @RequestMapping(value = "/batchChapterShow")
    public String batchChapterShow(Model model) {
        if (!StringUtils.isEmpty(RES_LIST)) {
            String[] books = RES_LIST.split(";");
            if (books != null && books.length > 0) {
                List<BookBatchRes> batchResList = new ArrayList<>();
                for (String book : books) {
                    String[] bookInfo = book.split(",");
                    if (bookInfo != null && bookInfo.length > 2) {
                        BookBatchRes bookBatchRes = new BookBatchRes();
                        bookBatchRes.setFileName(bookInfo[0]);
                        bookBatchRes.setMetaId(bookInfo[1]);
                        bookBatchRes.setStatus(Integer.valueOf(bookInfo[2]));
                        batchResList.add(bookBatchRes);
                    }
                }
                model.addAttribute("batchResList", batchResList);
            }
            return "book/flowBookBatchShow";
        }
        return null;
    }

    //批量抽取章节内容
    @RequestMapping(value = "/batchChapter", method = RequestMethod.POST)
    @ResponseBody
    public String batchChapter(@RequestParam("fileInfo") String fileInfo,
                               @RequestParam("filePath") String filePath,
                               @RequestParam("fileType") String fileType) {
        if (!StringUtils.isEmpty(fileInfo)) {
            if (!StringUtils.isEmpty(filePath)) {
                long start = System.currentTimeMillis();
                try {
                    List<BookBatchRes> bookBatchResList = new ArrayList<>();
                    if (fileType.equals("epub")) {
                        //bookBatchResList = readBook.batchChapterEpub(fileInfo, filePath);
                        bookBatchResList = readBook.batchEpub(fileInfo, filePath);
                    } else if (fileType.equals("cebx")) {
                        //bookBatchResList = readBook.batchChapterCebx(fileInfo, filePath);
                        bookBatchResList = readBook.batchCebx(fileInfo, filePath);
                    }
                    if (bookBatchResList.size() > 0) {
                        RES_LIST = "";
                        for (BookBatchRes bookBatchRes : bookBatchResList) {
                            RES_LIST += bookBatchRes.toString();
                        }
                        long end = System.currentTimeMillis();
                        log.info("发布" + bookBatchResList.size() + "本图书，耗时：" + (end - start) + "毫秒");
                        return "success";
                    } else {
                        return "exist";
                    }
                } catch (Exception e) {
                    log.warn(e.getMessage());
                }
            } else {
                return "path_null";
            }
        } else {
            return "id_null";
        }
        return "error";
    }

    //图书元数据
    @RequestMapping(value = "/flowBookBatch", method = RequestMethod.GET)
    public String flowBookBatch(String filePath, String fileType, Model model) {
        try {
            long start = System.currentTimeMillis();
            List<BookMetaBatch> bookMetaList = new ArrayList<>();
            List<BookMetaBatch> bookMetas = new ArrayList<>();
            if (!StringUtils.isEmpty(fileType)) {
                if (fileType.toLowerCase().equals("epub")) {
                    bookMetaList = bookMetaService.getBookMetaEpubBatch(filePath);
                } else if (fileType.toLowerCase().equals("cebx")) {
                    bookMetaList = bookMetaService.getBookMetaCebxBatch(filePath);
                }
            }
            if (bookMetaList != null && bookMetaList.size() > 0) {
                bookMetas = bookMetaList.stream()
                        .sorted(Comparator.comparing(BookMetaBatch::getFileName))
                        .collect(Collectors.toList());
            }
            model.addAttribute("bookMetaList", bookMetas);
            model.addAttribute("filePath", filePath);
            model.addAttribute("fileType", fileType);
            long end = System.currentTimeMillis();
            log.info("图书元数据列表查询耗时：" + (end - start) + "毫秒");
            return "book/flowBookBatch";
        } catch (Exception e) {
            log.warn("Exception {}" + e);
        }
        return null;
    }

    //分页查询，获取所有图书meta数据
    @RequestMapping(value = "/bookMeta")
    public String bookMeta(HttpServletRequest request, Model model,
                           @RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNum) {
        try {
            long start = System.currentTimeMillis();
            Map<String, String[]> params = request.getParameterMap();
            Map<String, Object> queryMap = new HashMap<>();
            String metaId = "";
            if (!StringUtils.isEmpty(params.get("metaId"))) {
                metaId = params.get("metaId")[0].trim();
                queryMap.put("metaId", metaId);
            }
            String title = "";
            if (!StringUtils.isEmpty(params.get("title"))) {
                title = params.get("title")[0].trim();
                queryMap.put("title", title);
            }
            String creator = "";
            if (!StringUtils.isEmpty(params.get("creator"))) {
                creator = params.get("creator")[0].trim();
                queryMap.put("creator", creator);
            }
            String publisher = "";
            if (!StringUtils.isEmpty(params.get("publisher"))) {
                publisher = params.get("publisher")[0].trim();
                queryMap.put("publisher", publisher);
            }
            String isbn = "";
            if (!StringUtils.isEmpty(params.get("isbn"))) {
                isbn = params.get("isbn")[0].trim();
            }
            String isbnVal = "";
            if (!StringUtils.isEmpty(params.get("isbnVal"))) {
                isbnVal = params.get("isbnVal")[0].trim();
                if ("isbn".equalsIgnoreCase(isbn)) {
                    queryMap.put("isbn", isbnVal);
                }
                if ("isbn10".equalsIgnoreCase(isbn)) {
                    queryMap.put("isbn10", isbnVal);
                }
                if ("isbn13".equalsIgnoreCase(isbn)) {
                    queryMap.put("isbn13", isbnVal);
                }
            }
            Integer hasCebx = null;
            if (!StringUtils.isEmpty(params.get("hasCebx"))) {
                if (!StringUtils.isEmpty(params.get("hasCebx")[0])) {
                    hasCebx = Integer.valueOf(params.get("hasCebx")[0]);
                    queryMap.put("hasCebx", hasCebx);
                }
            }
            Integer hasFlow = null;
            if (!StringUtils.isEmpty(params.get("hasFlow"))) {
                if (!StringUtils.isEmpty(params.get("hasFlow")[0])) {
                    hasFlow = Integer.valueOf(params.get("hasFlow")[0]);
                    queryMap.put("hasFlow", hasFlow);
                }
            }
            Integer isPublicCopyRight = null;
            if (!StringUtils.isEmpty(params.get("isPublicCopyRight"))) {
                if (!StringUtils.isEmpty(params.get("isPublicCopyRight")[0])) {
                    isPublicCopyRight = Integer.valueOf(params.get("isPublicCopyRight")[0]);
                    queryMap.put("isPublicCopyRight", isPublicCopyRight);
                }
            }
            Integer saleStatus = null;
            if (!StringUtils.isEmpty(params.get("saleStatus"))) {
                if (!StringUtils.isEmpty(params.get("saleStatus")[0])) {
                    saleStatus = Integer.valueOf(params.get("saleStatus")[0]);
                    queryMap.put("saleStatus", saleStatus);
                }
            }
            String flowSource = "";
            if (!StringUtils.isEmpty(params.get("flowSource"))) {
                flowSource = params.get("flowSource")[0].trim();
                queryMap.put("flowSource", flowSource);
            }
            Integer drid = null;
            if (!StringUtils.isEmpty(params.get("drid"))) {
                if (!StringUtils.isEmpty(params.get("drid")[0])) {
                    drid = Integer.valueOf(params.get("drid")[0]);
                    queryMap.put("drid", drid);
                }
            }
            PageHelper.startPage(pageNum, DEFAULT_PAGESIZE);
            Page<BookMetaVo> page = null;
            if (params.size() > 0) {
                page = bookMetaService.findBookMetaVoByPage(queryMap);
            }
            if (page == null) {
                model.addAttribute("bookMetaList", null);
            } else {
                model.addAttribute("bookMetaList", page.getResult());
                model.addAttribute("pages", page.getPages());
                model.addAttribute("pageNum", page.getPageNum());
            }
            model.addAttribute("page", page);
            model.addAttribute("metaId", metaId);
            model.addAttribute("title", title);
            model.addAttribute("creator", creator);
            model.addAttribute("publisher", publisher);
            model.addAttribute("isbn", isbn);
            model.addAttribute("isbnVal", isbnVal);
            model.addAttribute("hasCebx", hasCebx);
            model.addAttribute("hasFlow", hasFlow);
            model.addAttribute("isPublicCopyRight", isPublicCopyRight);
            model.addAttribute("saleStatus", saleStatus);
            model.addAttribute("flowSource", flowSource);
            model.addAttribute("drid", drid);
            long end = System.currentTimeMillis();
            log.info("图书元数据列表查询耗时：" + (end - start) + "毫秒");
            return "book/bookMeta";
        } catch (Exception e) {
            log.warn("Exception {}" + e);
        }
        return null;
    }

    //查看图书元数据详情
    @RequestMapping(value = "/bookMetaShow")
    public String bookMetaShow(@RequestParam("metaid") String metaid, Model model) {
        if (!StringUtils.isEmpty(metaid)) {
            long start = System.currentTimeMillis();
            List<CommEntity> entityList = new ArrayList<>();
            BookMeta bookMeta = bookMetaService.selectBookMetaDetailById(metaid);
            for (Field field : bookMeta.getClass().getDeclaredFields()) {
                CommEntity commEntity = new CommEntity();
                //获取属性名
                commEntity.setFiledName(field.getName());
                //获取属性中文描述
                commEntity.setFiledDesc((String) BookMetaMap.map.get(field.getName()));
                if (bookMeta != null) {
                    //获取属性值
                    Object value = getFieldValueByName(field.getName(), bookMeta);
                    if (value != null) {
                        String type = value.getClass().getTypeName();
                        if (type.contains("java.util.Date")) {
                            value = sdf1.format(value);
                        }
                    }
                    commEntity.setFiledValue(value);
                }
                entityList.add(commEntity);
            }
            model.addAttribute("entityList", entityList);
            long end = System.currentTimeMillis();
            log.info("查询图书" + metaid + "耗时：" + (end - start) + "毫秒");
        }
        return "book/bookMetaShow";
    }

    /*根据属性名获取属性值*/
    private static Object getFieldValueByName(String fieldName, Object o) {
        try {
            String firstLetter = fieldName.substring(0, 1).toUpperCase();
            String getter = "get" + firstLetter + fieldName.substring(1);
            Method method = o.getClass().getMethod(getter, new Class[]{});
            Object value = method.invoke(o, new Object[]{});
            return value;
        } catch (Exception e) {
            return null;
        }
    }

    //发布到爱读爱看跳转
    @RequestMapping(value = "/batchPub")
    public String batchPub(@RequestParam("ids") String ids, Model model) {
        model.addAttribute("ids", ids);
        return "book/batchAiduaikan";
    }

    //发布到爱读爱看
    @RequestMapping(value = "/batchPubInsert", method = RequestMethod.POST)
    @ResponseBody
    public String batchPubInsert(@RequestParam("ids") String ids,
                                 @RequestParam("original") String original,
                                 @RequestParam("optimized") String optimized,
                                 @RequestParam("platform") String platform,
                                 @RequestParam("libId") String libId) {
        if (!StringUtils.isEmpty(ids)) {
            long start = System.currentTimeMillis();
            Map<String, String> conds = new HashMap<>();
            conds.put("original", original);
            conds.put("optimized", optimized);
            conds.put("platform", platform);
            conds.put("libId", libId);
            try {
                int sum = bookMetaService.insertBookMeta2Mongo(ids, conds);
                long end = System.currentTimeMillis();
                log.info("发布" + sum + "本图书到爱读爱看耗时：" + (end - start) + "毫秒");
                return String.valueOf(sum);
            } catch (Exception e) {
                log.warn("爱读爱看发布失败：{}" + e.getMessage());
            }
        }
        return "error";
    }


    //解析xml文件调转
    @RequestMapping(value = "/bookXmlAdd")
    public String bookXmlAdd() {
        return "book/bookXmlAdd";
    }

    //解析xml文件
    @RequestMapping(value = "/bookXmlInsert", method = RequestMethod.POST)
    @ResponseBody
    public String bookXmlInsert(@RequestParam(value = "files", required = false) MultipartFile[] files,
                                HttpServletRequest req) {
        if (files != null) {
            String filePath = "";
            try {
                //上传图书到服务端指定目录
                long start = System.currentTimeMillis();
                List<String> fileList = new ArrayList<>();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                String dirPath = config.getUploadXml() + File.separator + sdf.format(new Date());
                File dir = new File(dirPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                for (MultipartFile file : files) {
                    File newFile = new File(dir, file.getOriginalFilename());
                    file.transferTo(newFile);
                    fileList.add(newFile.getPath());
                    //bookService.saveXmlBookMeta(newFile.getPath());
                }
                long end = System.currentTimeMillis();
                log.info(files.length + "本图书上传成功！耗时：" + (end - start) + "毫秒");
                //解析xml文件
                start = System.currentTimeMillis();
                if (fileList != null && fileList.size() > 0) {
                    int res = 0;
                    for (String path : fileList) {
                        filePath = path;
                        res += bookMetaService.saveXmlBookMeta(path);
                    }
                    if (res > 0) {
                        end = System.currentTimeMillis();
                        log.info(res + "本图书解析成功！耗时：" + (end - start) + "毫秒");
                        return "success";
                    } else if (res < 0) {
                        return "exist";
                    }
                }
            } catch (Exception e) {
                log.warn("解析xml：" + e);
            } finally {
                //删除上传文件
                boolean flag = BookUtil.deleteFile(filePath);
                if (!flag) {
                    log.debug("文件删除失败！");
                }
            }
        }
        return "error";
    }

    //上传epub文件
    @RequestMapping(value = "/epubChapterAdd")
    public String epubChapterAdd(@RequestParam("metaId") String metaId,
                                 @RequestParam("publishDate") String publishDate,
                                 Model model) {
        model.addAttribute("metaId", metaId);
        model.addAttribute("publishDate", publishDate);
        return "book/epubChapterAdd";
    }

    //获取epub文件内容
    @RequestMapping(value = "/epubChapterInsert", method = RequestMethod.POST)
    @ResponseBody
    public String epubChapterInsert(@RequestParam(value = "file", required = false) MultipartFile file,
                                    @RequestParam("metaId") String metaId,
                                    @RequestParam("publishDate") String publishDate,
                                    HttpServletRequest req) {
        if (!StringUtils.isEmpty(metaId)) {
            long res = bookMetaService.countBookMetaById(metaId);
            if (res == 0) {
                return "id_0";
            }
        } else {
            return "id_null";
        }
        if (file != null && !StringUtils.isEmpty(metaId)) {
            String filePath;
            try {
                //上传图书到服务端指定目录
                long start = System.currentTimeMillis();
                String dirPath = config.getUploadEpub() + File.separator + publishDate.substring(0, 10);
                File dir = new File(dirPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File newFile = new File(dir, metaId + ".epub");
                file.transferTo(newFile);
                long end = System.currentTimeMillis();
                log.info(file.getOriginalFilename() + "图书上传成功！耗时：" + (end - start) + "毫秒");
                //解析epub文件
                start = System.currentTimeMillis();
                filePath = dirPath + File.separator + newFile.getName();
                int res = readBook.readEpubook(filePath, metaId);
                if (res == 1) {
                    end = System.currentTimeMillis();
                    log.info(file.getOriginalFilename() + "图书上传路径：" + filePath);
                    log.info(file.getOriginalFilename() + "图书解析成功！耗时：" + (end - start) + "毫秒");
                    return "success";
                } else if (res < 0) {
                    return "exist";
                }
            } catch (Exception e) {
                e.printStackTrace();
            } /*finally {
                //删除上传文件
                boolean flag = BookUtil.deleteFile(filePath);
                if (!flag) {
                    log.debug("文件删除失败！");
                }
            }*/
        }
        return "error";
    }

    //上传cebx文件
    @RequestMapping(value = "/cebxChapterAdd")
    public String cebxChapterAdd(@RequestParam("metaid") String metaid, Model model) {
        model.addAttribute("metaid", metaid);
        return "book/cebxChapterAdd";
    }

    //获取cebx文件内容
    @RequestMapping(value = "/cebxChapterInsert", method = RequestMethod.POST)
    @ResponseBody
    public String cebxChapterInsert(@RequestParam(value = "file", required = false) MultipartFile file,
                                    @RequestParam("id") String id,
                                    HttpServletRequest req) throws Exception {
        if (!StringUtils.isEmpty(id)) {
            long res = bookMetaService.countBookMetaById(id);
            if (res == 0) {
                return "id_0";
            }
        } else {
            return "id_null";
        }
        if (file != null && !StringUtils.isEmpty(id)) {
            String filePath = "";
            try {
                //上传图书到服务端指定目录
                long start = System.currentTimeMillis();
                String dirPath = config.getUploadCebx() + File.separator + sdf.format(new Date());
                File dir = new File(dirPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                File newFile = new File(dir, file.getOriginalFilename());
                file.transferTo(newFile);
                long end = System.currentTimeMillis();
                log.info(file.getOriginalFilename() + "图书上传成功！耗时：" + (end - start) + "毫秒");
                //解析epub文件
                start = System.currentTimeMillis();
                filePath = dirPath + File.separator + file.getOriginalFilename();
                int res = readBook.readCebxBook(filePath, id);
                if (res == 1) {
                    end = System.currentTimeMillis();
                    log.info(file.getOriginalFilename() + "图书解析成功！耗时：" + (end - start) + "毫秒");
                    return "success";
                } else if (res < 0) {
                    return "exist";
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                //删除上传文件
                boolean flag = BookUtil.deleteFile(filePath);
                if (!flag) {
                    log.debug("文件删除失败！");
                }
            }
        }
        return "error";
    }

    //删除图书内容数据
    @RequestMapping(value = "/bookChapterDelete", method = RequestMethod.GET)
    @ResponseBody
    public String bookChapterDelete(String id) {
        try {
            if (id != null && id.length() > 0) {
                bookMetaService.deleteBookChapter(id);
                return "success";
            }
        } catch (Exception e) {
            log.warn("删除图书：" + id + "Exception{}" + e);
        }
        return "error";
    }

    //批量删除图书内容
    @RequestMapping(value = "/bookChapterDeleteBatch", method = RequestMethod.POST)
    @ResponseBody
    public String bookChapterDeleteBatch(@RequestParam("metaIds") String metaIds) {
        if (!StringUtils.isEmpty(metaIds)) {
            long start = System.currentTimeMillis();
            int res = bookMetaService.deleteBookChapterBatch(metaIds);
            long end = System.currentTimeMillis();
            log.info("删除{}本图书流式内容，耗时{}毫秒", res, (end - start));
            return String.valueOf(res);
        }
        return "error";
    }

    //批量删除图书内容
    @RequestMapping(value = "/bookChapterDeleteEmail", method = RequestMethod.POST)
    @ResponseBody
    public String bookChapterDeleteEmail(@RequestParam("metaIds") String metaIds,
                                         @RequestParam("toEmail") String toEmail) {
        if (!StringUtils.isEmpty(metaIds) && !StringUtils.isEmpty(toEmail)) {
            bookMetaService.deleteBookChapterEmail(metaIds, toEmail);
            return "success";
        }
        return "error";
    }

    //跳转到批量操作图书
    @RequestMapping(value = "/bookMetaBatch")
    public String bookMetaBatch() {
        return "book/bookMetaBatch";
    }

    //跳转到流式内容检查
    @RequestMapping(value = "/flowBookDetect")
    public String flowBookDetect() {
        return "book/flowBookDetect";
    }

    //乱码检查
    @RequestMapping(value = "/codeDetect")
    @ResponseBody
    public String codeDetect() {
        bookChapterService.detectBookCode();
        return "success";
    }

    //关键词检测
    @RequestMapping(value = "/sourceDetect")
    @ResponseBody
    public String sourceDetect() {
        bookChapterService.detectBookSource();
        return "success";
    }

    //批量获取页码和目录
    @RequestMapping(value = "/getPageAndCata")
    @ResponseBody
    public String getPageAndCata(@RequestParam("dridMin") Integer dridMin,
                                 @RequestParam("dridMax") Integer dridMax,
                                 @RequestParam("toEmail") String toEmail) {
        if ((dridMin > 0) && (!StringUtils.isEmpty(toEmail))) {
            bookMetaService.getPageAndCata(dridMin, dridMax, toEmail);
            return "success";
        }
        return "error";
    }

    //批量获取图书内容
    @RequestMapping(value = "/bookMetaBatch", method = RequestMethod.POST)
    @ResponseBody
    public String bookMetaBatch(@RequestParam("metaIds") String metaIds) {
        if (!StringUtils.isEmpty(metaIds)) {
            long start = System.currentTimeMillis();
            int res = bookMetaService.bookMetaBatch(metaIds);
            long end = System.currentTimeMillis();
            log.info("从书苑获取{}本图书元数据，耗时{}毫秒", res, (end - start));
            return String.valueOf(res);
        }
        return "error";
    }

    //批量获取图书内容
    @RequestMapping(value = "/bookMetaBatchEmail", method = RequestMethod.POST)
    @ResponseBody
    public String bookMetaBatchEmail(@RequestParam("metaIds") String metaIds,
                                     @RequestParam("toEmail") String toEmail) {
        if (!StringUtils.isEmpty(metaIds) && !StringUtils.isEmpty(toEmail)) {
            bookMetaService.bookMetaBatchEmail(metaIds, toEmail);
            return "success";
        }
        return "error";
    }

    //根据drid，批量获取图书内容
    @RequestMapping(value = "/getMetaByDrid", method = RequestMethod.POST)
    @ResponseBody
    public String getMetaByDrid(@RequestParam("drids") String drids,
                                @RequestParam("toEmail") String toEmail) {
        if (!StringUtils.isEmpty(drids) && !StringUtils.isEmpty(toEmail)) {
            bookMetaService.getMetaByDridEmail(drids, toEmail);
            return "success";
        }
        return "error";
    }

    //图书编辑页面，图书元数据获取
    @GetMapping("/bookChapterEditMeta")
    @ResponseBody
    public BookMetaVo bookChapterEditMeta(@RequestParam("metaid") String metaid) {
        long start = System.currentTimeMillis();
        if (!StringUtils.isEmpty(metaid)) {
            BookMetaVo bookMetaVo = bookMetaService.selectBookMetaById(metaid);
            long end = System.currentTimeMillis();
            log.info("获取图书" + metaid + "编辑页面的元数据，耗时：" + (end - start) + "毫秒");
            return bookMetaVo;
        }
        return null;
    }

    //图书编辑页面，首页获取
    @GetMapping("/bookChapterEditCover")
    @ResponseBody
    public BookChapter bookChapterEditCover(@RequestParam("metaid") String metaid) {
        long start = System.currentTimeMillis();
        if (!StringUtils.isEmpty(metaid)) {
            BookChapter bookChapter = bookChapterService.selectChapterById(metaid, 0);
            long end = System.currentTimeMillis();
            log.info("获取图书" + metaid + "编辑页面的首页，耗时：" + (end - start) + "毫秒");
            return bookChapter;
        }
        return null;
    }

    //图书编辑页面，目录获取
    @GetMapping("/bookChapterEditCata")
    @ResponseBody
    public String bookChapterEditCata(@RequestParam("metaid") String metaid) {
        long start = System.currentTimeMillis();
        if (!StringUtils.isEmpty(metaid)) {
            String cataRows = bookMetaService.getCataTreeById(metaid);
            long end = System.currentTimeMillis();
            log.info("获取图书" + metaid + "编辑页面的目录，耗时：" + (end - start) + "毫秒");
            return cataRows;
        }
        return "error";
    }

    //图书内容编辑页面跳转
    @GetMapping("/bookChapterEdit")
    public String bookChapterEdit(@RequestParam("metaid") String metaid, Model model) {
        if (!StringUtils.isEmpty(metaid)) {
            model.addAttribute("metaId", metaid);
        }
        return "book/bookChapterEdit";
    }

    //更新目录内容
    @RequestMapping(value = "/cataTreeUpdate", method = RequestMethod.POST)
    @ResponseBody
    public String cataTreeUpdate(String catalogArr, String metaId) {
        if (!StringUtils.isEmpty(metaId)) {
            if (!StringUtils.isEmpty(catalogArr)) {
                long start = System.currentTimeMillis();
                int res = bookMetaService.updateCataTree(metaId, catalogArr);
                if (res > 0) {
                    long end = System.currentTimeMillis();
                    log.info("更新图书：" + metaId + "目录，耗时：" + (end - start) + "毫秒");
                    return "success";
                }
            }
        }
        return "error";
    }

    //保存图书内容
    @RequestMapping(value = "/bookChapterSave", method = RequestMethod.POST)
    @ResponseBody
    public String bookChapterSave(BookChapter bookChapter) {
        long start = System.currentTimeMillis();
        if (bookChapter != null) {
            int res = bookChapterService.updateBookChapter(bookChapter);
            if (res > 0) {
                long end = System.currentTimeMillis();
                log.info("更新图书" + bookChapter.getComId() + "章节耗时：" + (end - start) + "毫秒");
                return "success";
            }
        }
        return "error";
    }

    //保存目录内容
    @RequestMapping(value = "/catalogUpdate", method = RequestMethod.POST)
    @ResponseBody
    public String catalogUpdate(String catalogArr, String metaId) {
        long start = System.currentTimeMillis();
        if (!StringUtils.isEmpty(catalogArr)) {
            if (!StringUtils.isEmpty(metaId)) {
                String[] cataRows = catalogArr.split(";");
                if (cataRows != null && cataRows.length > 0) {
                    String cataLog = "";
                    for (String cata : cataRows) {
                        String[] tmp = cata.split(",");
                        if (tmp != null && tmp.length > 1) {
                            cataLog += "{\"chapterName\":\"" + tmp[0] + "\"," +
                                    "\"chapterNum\":" + tmp[1] + "," +
                                    "\"ebookPageNum\":0,\"url\":\"\",\"wordSum\":0},";
                        }
                    }
                    //更新目录
                    EpubookMeta epubookMeta = new EpubookMeta();
                    epubookMeta.setMetaid(metaId);
                    epubookMeta.setStreamCatalog(cataLog);
                    epubookMeta.setUpdatetime(new Date());
                    int res = bookMetaService.saveEpubookMeta(epubookMeta);
                    if (res > 0) {
                        //更新tmp表
                        String sql = "UPDATE APABI_BOOK_METADATA_TEMP" +
                                " SET STREAMCATALOG = ?, UPDATETIME = ?" +
                                "WHERE METAID = ?";
                        Object[] objects = new Object[]{
                                epubookMeta.getStreamCatalog(), epubookMeta.getUpdatetime(),
                                epubookMeta.getMetaid()
                        };
                        int ress = jdbcTemplate.update(sql, objects);
                        if (ress > 0) {
                            long end = System.currentTimeMillis();
                            log.info("更新图书" + metaId + "目录耗时：" + (end - start) + "毫秒");
                            return "success";
                        }
                    }
                }
            }
        }
        return "error";
    }

    @GetMapping("/getBookChapter")
    @ResponseBody
    public ResultEntity getBookChapter(@RequestParam("metaid") String metaId,
                                       @RequestParam("chapterNum") Integer chapterNum) {
        long start = System.currentTimeMillis();
        BookChapter bookChapter = bookChapterService.selectChapterById(metaId, chapterNum);
        ResultEntity resultEntity = new ResultEntity();
        if (bookChapter == null) {
            resultEntity.setMsg("error");
            resultEntity.setStatus(-1);
        } else {
            resultEntity.setMsg("success");
            resultEntity.setStatus(0);
            resultEntity.setBody(bookChapter);
        }
        long end = System.currentTimeMillis();
        log.info("获取图书" + metaId + "，第" + chapterNum + "章节耗时：" + (end - start) + "毫秒");
        return resultEntity;
    }


//    /**
//     * 从书苑获取 图书每页内容信息
//     *
//     * @param metaId
//     * @return 返回是否成功，fail,success,这里需要重新定义（整体）
//     */
//    @GetMapping("/fetchShuyuanPageData/{metaId}")
//    @ResponseBody
//    public String fetchShuyuanPageData(@PathVariable String metaId) {
//        int res = 0;
//        try {
//            res = bookPageService.insertShuyuanPageData(metaId);
//        } catch (Exception e) {
//            log.error("获取书苑分页数据出错", e);
//        }
//        return res == 0 ? "fail" : "success";
//    }

    /**
     * 获取指定图书的所有分页信息
     *
     * @param metaid
     * @param model
     * @return 返回 bookPage 的逻辑视图
     */
    @RequestMapping(value = "/bookPage")
    public String bookPage(@RequestParam(value = "metaid", required = false) String metaid, Model model) {
        try {
            long start = System.currentTimeMillis();

            model.addAttribute("bookPageList", Collections.emptyList());
            model.addAttribute("tbMetaid", "");
            model.addAttribute("tbTitle", "");
            model.addAttribute("tbCreator", "");
            model.addAttribute("tbTotalPageNum", "");

            if (org.apache.commons.lang3.StringUtils.isBlank(metaid)) {
                return "book/bookPage";
            }
            BookMetaVo bookMetaVo = bookMetaService.selectBookMetaById(metaid);

            if (bookMetaVo == null) {
                return "book/bookPage";
            }

            List<BookPage> list = bookPageService.queryAllBookPagesByMetaId(metaid);

            model.addAttribute("bookPageList", list);
            model.addAttribute("tbMetaid", metaid);
            model.addAttribute("tbTitle", bookMetaVo.getTitle());
            model.addAttribute("tbCreator", bookMetaVo.getCreator());
            model.addAttribute("tbTotalPageNum", list != null ? list.size() : 0);

            long end = System.currentTimeMillis();
            log.info("图书元数据分页列表查询耗时：" + (end - start) + "毫秒");
            return "book/bookPage";
        } catch (Exception e) {
            log.warn("Exception {}" + e);
        }
        return "book/bookPage";
    }

    /**
     * 获取 图书页面内容详情
     *
     * @param metaid
     * @param pageid
     * @param model
     * @return 返回逻辑视图，附带 模板信息
     */
    @RequestMapping("/bookPageContentDetail")
    public String bookPageContentDetail(@RequestParam("metaid") String metaid,
                                        @RequestParam("pageid") Integer pageid,
                                        Model model) {
        log.debug("图书页面内容获取 参数： metaid = {}, pageid = {}", metaid, pageid);

        model.addAttribute("pageid", pageid);
        BookPage bookPage = bookPageService.getBookPageContentByMetaidAndPageid(metaid, pageid);
        model.addAttribute("content",
                Optional.ofNullable(bookPage).map(u -> u.getContent()).orElse(""));
        return "book/bookPageContentDetail";
    }

    //    /**
//     * 处理数据接口，将 图书从 页码信息 拼装成 章节信息
//     *
//     * @param metaid
//     * @return
//     * @throws Exception
//     */
//    @RequestMapping("/processBookFromPage2Chapter")
//    @ResponseBody
//    public int processBookFromPage2Chapter(@RequestParam("metaid") String metaid) throws Exception {
//        return bookPageService.processBookFromPage2Chapter(metaid);
//    }
    //跳转管理
    @RequestMapping("/bookPages")
    public String bookChapter(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNum,
                              @RequestParam(value = "metaid", required = false) String metaId,Model model) {
        PageHelper.startPage(pageNum, 10);
        Map<String, Object> paramsMap = new HashMap<>();
        ParamsUtils.checkParameterAndPut2Map(paramsMap, "metaId",metaId);
        Page<BookPage> bookPage = bookPageMapper.findBookPage(paramsMap);
        model.addAttribute("num", bookPage.size());
        model.addAttribute("bookPage", bookPage);
        model.addAttribute("pages", bookPage.getPages());
        model.addAttribute("pageNum", bookPage.getPageNum());
        model.addAttribute("pageSize", 10);
        model.addAttribute("total", bookPage.getTotal());
        model.addAttribute("metaId", metaId);
        return "book/bookPages";
    }

    //跳转首页cebx流式内容管理
    @RequestMapping("/bookPageManagement")
    public String bookPageManagement() {
        return "book/bookPageManagement";
    }

    //跳转抓取页
    @RequestMapping("/pageCrawled")
    public String pageCrawled(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNum, Model model) {
        PageHelper.startPage(pageNum, 10);
        Page<PageCrawledQueue> pageCrawledQueues = pageCrawledQueueMapper.pageAll();
//        List<PageCrawledTemp> pageCrawledTemps = pageCrawledTempMapper.findAll();
//        model.addAttribute("num", pageCrawledTemps.size());
        model.addAttribute("pageCrawledQueues", pageCrawledQueues);
        model.addAttribute("pages", pageCrawledQueues.getPages());
        model.addAttribute("pageNum", pageCrawledQueues.getPageNum());
        model.addAttribute("pageSize", 10);
        model.addAttribute("total", pageCrawledQueues.getTotal());
        return "book/bookPageManagementCrawled";
    }

    //跳转拼装页
    @RequestMapping("/pageAssembly")
    public String pageAssembly(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNum, Model model) {
        PageHelper.startPage(pageNum, 10);
        Page<PageAssemblyQueue> pageAssemblyQueues = pageAssemblyQueueMapper.pageAll();
        model.addAttribute("pages", pageAssemblyQueues.getPages());
        model.addAttribute("pageNum", pageAssemblyQueues.getPageNum());
        model.addAttribute("pageSize", 10);
        model.addAttribute("total", pageAssemblyQueues.getTotal());
        model.addAttribute("pageAssemblyQueues", pageAssemblyQueues);
        return "book/bookPageManagementAssemBly";
    }

    //跳转log页
    @RequestMapping("/bookPageLog")
    public String bookPageLog(@RequestParam(value = "time", required = false, defaultValue = "") String time,
                              @RequestParam(value = "len", required = false, defaultValue = "100") Integer len,
                              @RequestParam(value = "type", required = false, defaultValue = "0") Integer type,
                              Model model) {
        String log1 = "";
        String log2 = "";
        String log3 = "";
        String logPath = config.getLogPath();
        if (org.apache.commons.lang3.StringUtils.isNotBlank(time) && type == 1) {
            String filename = logPath + File.separator + "fetchPage" + File.separator + "fetchPage." + time + ".log";
            log1 = ReadLog.read(filename, "UTF-8", len);
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(time) && type == 2) {
            String filename = logPath + File.separator + "fetchPage1" + File.separator + "fetchPageAgain." + time + ".log";
            log2 = ReadLog.read(filename, "UTF-8", len);
        }
        if (org.apache.commons.lang3.StringUtils.isNotBlank(time) && type == 3) {
            String filename = logPath + File.separator + "fetchPage2" + File.separator + "fetchPage2." + time + ".log";
            log3 = ReadLog.read(filename, "UTF-8", len);
        }
        model.addAttribute("time", time);
        model.addAttribute("len", len);
        model.addAttribute("log1", log1);
        model.addAttribute("log2", log2);
        model.addAttribute("log3", log3);
        return "book/bookPageLog";
    }

    //跳转fail页
    @RequestMapping("/bookFailure")
    public String bookFailure(@RequestParam(value = "page", required = false, defaultValue = "1") Integer pageNum,
                              @RequestParam(value = "metaId", required = false) String metaId, Model model) {
        PageHelper.startPage(pageNum, 10);
        Map<String, Object> paramsMap = new HashMap<>();
        ParamsUtils.checkParameterAndPut2Map(paramsMap, "metaId", metaId);
        Page<PageCrawledTemp> pageCrawledTemps = pageCrawledTempMapper.pageAll(paramsMap);
        model.addAttribute("pages", pageCrawledTemps.getPages());
        model.addAttribute("pageNum", pageCrawledTemps.getPageNum());
        model.addAttribute("pageSize", 10);
        model.addAttribute("total", pageCrawledTemps.getTotal());
        model.addAttribute("pageCrawledTemps", pageCrawledTemps);
        model.addAttribute("metaId", metaId);
        return "book/bookPageManagementCrawledFial";
    }

    //单个删除
    @RequestMapping("/pageCrawledQueuesDelete")
    public String pageCrawledQueuesDelete(@RequestParam("id") String id) {
        pageCrawledQueueMapper.deleteByPrimaryKey(id);
        return "redirect:/book/pageCrawled";
    }

    //全部删除
    @RequestMapping("/pageCrawledQueuesDeleteAll")
    public String pageCrawledQueuesDeleteAll() {
        pageCrawledQueueMapper.deleteAll();
        return "redirect:/book/pageCrawled";
    }

    //单个删除
    @RequestMapping("/pageAssemblyQueuesDelete")
    public String pageAssemblyQueuesDelete(@RequestParam("id") String id) {
        pageAssemblyQueueMapper.deleteByPrimaryKey(id);
        return "redirect:/book/pageAssembly";
    }

    //全部删除
    @RequestMapping("/pageAssemblyDeleteAll")
    public String pageAssemblyDeleteAll() {
        pageAssemblyQueueMapper.deleteAll();
        return "redirect:/book/pageAssembly";
    }

    @ResponseBody
    @RequestMapping("/autoFetchPageData2")
    public Object autoFetchPageData2() {
        String cebxData = getCebxData("http://flow.apabi.com/flow/book/autoFetchPageData");
        return cebxData;
    }

    /**
     * 采集加密流式内容
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/autoFetchPageData")
    public Object autoFetchPageData() {
        ResultEntity resultEntity = new ResultEntity();
        //前端多次点击按钮控制
        SystemConf systemConf2 = systemConfMapper.selectByConfKey("switch");
        if (systemConf2 == null) {
            log.error("获取系统参数信息出错，无法查询线程池开关");
        }
        int i = 0;
        int swith = Integer.parseInt(systemConf2.getConfValue());
        if (swith == 0) {
            i = 1;
            systemConf2.setConfValue("1");
            systemConfMapper.updateByPrimaryKey(systemConf2);
            i = bookPageService.autoFetchPageData();
        } else if (swith == 1) {
            i = -1;
        }
        if (i == 1) {
            resultEntity.setMsg("采集加密流式内容已开始，请勿再次操作，耐心等待");
            resultEntity.setStatus(i);
        } else if (i == -1) {
            resultEntity.setMsg("采集加密流式内容正在进行请勿再次操作");
            resultEntity.setStatus(1);
        } else if (i == 2) {
            resultEntity.setMsg("采集加密流式内容队列没有内容");
            resultEntity.setStatus(1);
        } else {
            resultEntity.setMsg("采集加密失败！请联系管理员，系统参数被修改");
            resultEntity.setStatus(-1);
        }
        return resultEntity;
    }

    @ResponseBody
    @RequestMapping("/autoFetchPageDataAgain2")
    public Object autoFetchPageDataAgain2() {
        String cebxData = getCebxData("http://flow.apabi.com/flow/book/autoFetchPageDataAgain");
        return cebxData;
    }

    /**
     * 重新采集加密流式内容
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/autoFetchPageDataAgain")
    public Object autoFetchPageDataAgain() {
        ResultEntity resultEntity = new ResultEntity();

        //前端多次点击按钮控制
        SystemConf systemConf2 = systemConfMapper.selectByConfKey("switch");
        if (systemConf2 == null) {
            log.error("获取系统参数信息出错，无法查询线程池开关");
        }
        int i = 0;
        int swith = Integer.parseInt(systemConf2.getConfValue());
        if (swith == 0) {
            i = 1;
            systemConf2.setConfValue("1");
            systemConfMapper.updateByPrimaryKey(systemConf2);
            i = bookPageService.autoFetchPageDataAgain();
        } else if (swith == 1) {
            i = -1;
        }
        if (i == 1) {
            resultEntity.setMsg("重新采集加密流式内容已开始，请勿再次操作，耐心等待");
            resultEntity.setStatus(i);
        } else if (i == -1) {
            resultEntity.setMsg("重新采集加密流式内容正在进行请勿再次操作");
            resultEntity.setStatus(1);
        } else if (i == 2) {
            resultEntity.setMsg("重新采集加密流式队列没有内容");
            resultEntity.setStatus(1);
        } else {
            resultEntity.setMsg("重新抽取失败内容失败！请联系管理员");
            resultEntity.setStatus(-1);
        }
        return resultEntity;
    }

    /**
     * 流式内容拼装
     *
     * @return
     */
    @ResponseBody
    @RequestMapping("/autoProcessBookFromPage2Chapter")
    public Object autoProcessBookFromPage2Chapter(@RequestParam("isCover") int isCover, Model model) {
        ResultEntity resultEntity = new ResultEntity();
        int i = bookPageService.autoProcessBookFromPage2Chapter(isCover);
        if (i >= 1) {
            resultEntity.setMsg("流式内容拼装已开始，请勿再次操作，耐心等待");
            resultEntity.setStatus(i);
        } else if (i == -1) {
            resultEntity.setMsg("拼装章节队列已无数据");
            resultEntity.setStatus(1);
        } else {
            resultEntity.setMsg("流式内容拼装失败！请联系管理员");
            resultEntity.setStatus(-1);
        }
//        resultEntity.setBody()
        model.addAttribute("isCover", isCover);
        return resultEntity;
    }

    /**
     * 批量上传metaid
     *
     * @return 返回上传数据成功书id数
     */
    @ResponseBody
    @RequestMapping("/getBookMetaIds")
    public Object getBookMetaIds(@RequestParam("metaIds") String metaIds) {
        try {
            if (metaIds != null && metaIds != "") {
                ResultEntity resultEntity = new ResultEntity();
                String[] split = metaIds.split(",");
                Set<String> hashSet = Arrays.stream(split).collect(Collectors.toSet());
                int num = 0;
                for (String b : hashSet) {
                    PageCrawledQueue pageCrawledQueue = new PageCrawledQueue();
                    pageCrawledQueue.setId(b);
                    int i = pageCrawledQueueMapper.insert(pageCrawledQueue);
                    num += i;
                }
                resultEntity.setMsg("上传数据成功" + num + "条");
                resultEntity.setStatus(1);
                return resultEntity;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 批量导出metaid
     *
     * @return 返回上传数据成功书id数
     */
    @ResponseBody
    @RequestMapping("/exportData")
    public Object exportData(@RequestParam("type") String type, HttpServletResponse response) {
        try {
            // 设置响应头
            response.setContentType("application/binary;charset=UTF-8");
            response.setCharacterEncoding("utf-8");
            response.setContentType("multipart/form-data");
            List list = null;
            if ("Crawled".equalsIgnoreCase(type)) {
                List<PageCrawledQueue> list1 = pageCrawledQueueMapper.findAll();
                list = list1.stream().map(id -> id.getId()).collect(Collectors.toList());
            } else if ("Assembly".equalsIgnoreCase(type)) {
                List<PageAssemblyQueue> list1 = pageAssemblyQueueMapper.findAll();
                list = list1.stream().map(id -> id.getId()).collect(Collectors.toList());
            }
            if (list == null || list.isEmpty()) {
                return "<script type='text/javascript'>alert('当前列表为空！');history.back();</script>";
            }
            // 设置文件名
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HHmmss");
            String time = simpleDateFormat.format(date);
            File file = new File(time + "-" + UUIDCreater.nextId() + ".xlsx");
            String fileName = new String(file.getName().getBytes("UTF-8"), "ISO-8859-1");

            // 设置excel的表头
            String[] excelTitle = {"ID"};
            // 将内存中读取到的内容写入到excel
            Workbook workbook = null;
            if (fileName.toLowerCase().endsWith("xls")) {//2003
                workbook = new XSSFWorkbook();
            } else if (fileName.toLowerCase().endsWith("xlsx")) {//2007
                workbook = new HSSFWorkbook();
            } else {
                throw new BizException("文件名后缀必须是 .xls 或 .xlsx");
            }

            ServletOutputStream out = null;
            //create sheet
            Sheet sheet = workbook.createSheet("sheet1");
            //遍历数据集，将其写入excel中
            try {
                //写表头数据
                Row titleRow = sheet.createRow(0);
                for (int i = 0; i < excelTitle.length; i++) {
                    //创建表头单元格,填值
                    titleRow.createCell(i).setCellValue(excelTitle[i]);
                }
                //自动添加 excel 行数据
                for (int i = 0; i < list.size(); i++) {
                    // 创建 excel 行
                    Row row = sheet.createRow(i + 1);
                    row.createCell(0).setCellValue(list.get(i).toString());
                }
                // 设置响应头
                response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
                out = response.getOutputStream();
                workbook.write(out);
                out.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw new Exception("文件未找到！");
            } catch (IOException e) {
                e.printStackTrace();
                throw new Exception("服务器异常！");
            } catch (Exception e) {
                throw new Exception("数据解析异常！");
            } finally {
                if (out != null) {
                    try {
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return "<script type='text/javascript'>alert('导出成功！');history.back();</script>";
        } catch (Exception e) {
            e.printStackTrace();
            return "<script type='text/javascript'>alert('" + e.getMessage() + "');history.back();</script>";
        }
    }

    /**
     * 批量上传metaid
     *
     * @return 返回上传数据成功书id数
     */
    @ResponseBody
    @RequestMapping("/getBookMetaIdsToChapter")
    public Object getBookMetaIdsToChapter(@RequestParam("metaIds") String metaIds) {
        ResultEntity resultEntity = new ResultEntity();
        try {
            if (metaIds != null && metaIds != "") {
                String[] split = metaIds.split(",");
                Set<String> hashSet = Arrays.stream(split).collect(Collectors.toSet());
                int num = 0;
                for (String b : hashSet) {
                    PageAssemblyQueue pageAssemblyQueue = new PageAssemblyQueue();
                    pageAssemblyQueue.setId(b);
                    int i = pageAssemblyQueueMapper.insert(pageAssemblyQueue);
                    num += i;
                }
                resultEntity.setMsg("上传数据成功" + num + "条");
                resultEntity.setStatus(1);
                return resultEntity;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        resultEntity.setMsg("已重复上传失败");
        resultEntity.setStatus(-1);
        return resultEntity;
    }

    @PostMapping("/batch/import")
    @ResponseBody
    public String batchImportCraw(@RequestParam("file") MultipartFile file) {

        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            return "文件格式不正确，仅支持 .xlsx 格式的文件";
        }
        // 读取Excel工具类
        Map<Integer, Map<Object, Object>> data = null;
        try (InputStream inputStream = file.getInputStream()) {
            String fileName = file.getOriginalFilename();
            ReadExcelTextUtils readExcelTextUtils = new ReadExcelTextUtils(inputStream, fileName);
            // 读取Excel中的内容
            data = readExcelTextUtils.getDataByInputStream();
            if (data == null || data.isEmpty()) {
                throw new Exception();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "文件读取出错，请重新尝试或联系管理员！";
        } catch (Exception e) {
            return "文件出错，请检查文件格式是否正确或内容是否完整！";
        }
        Integer addedNum = 0;
        try {
            addedNum = bookPageService.batchAddCrawFromFile(data);
        } catch (Exception e) {
            log.error("异常信息： {}", e);
        }
        return addedNum > 0 ? "成功" : "失败";
    }

    @PostMapping("/batch/import2")
    @ResponseBody
    public String batchImportChapter(@RequestParam("file") MultipartFile file) {

        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            return "文件格式不正确，仅支持 .xlsx 格式的文件";
        }
        // 读取Excel工具类
        Map<Integer, Map<Object, Object>> data = null;
        try (InputStream inputStream = file.getInputStream()) {
            String fileName = file.getOriginalFilename();
            ReadExcelTextUtils readExcelTextUtils = new ReadExcelTextUtils(inputStream, fileName);
            // 读取Excel中的内容
            data = readExcelTextUtils.getDataByInputStream();
            if (data == null || data.isEmpty()) {
                throw new Exception();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "文件读取出错，请重新尝试或联系管理员！";
        } catch (Exception e) {
            return "文件出错，请检查文件格式是否正确或内容是否完整！";
        }
        Integer addedNum = 0;
        try {
            addedNum = bookPageService.batchAddAssemblyFromFile(data);
        } catch (Exception e) {
            log.error("异常信息： {}", e);
        }
        return addedNum > 0 ? "成功" : "失败";
    }

    @ResponseBody
    @RequestMapping("/shutdownNow2")
    public Object shutdownNow2() {
        String cebxData = getCebxData("http://flow.apabi.com/flow/book/shutdownNow");
        return cebxData;
    }

    @ResponseBody
    @RequestMapping("/shutdownNow")
    public Object shutdownNow() {
        ResultEntity resultEntity = new ResultEntity();

        //前端多次点击按钮控制
        SystemConf systemConf2 = systemConfMapper.selectByConfKey("switch");
        if (systemConf2 == null) {
            log.error("获取系统参数信息出错，无法查询线程池开关");
        }
        int i = 0;
        int swith = Integer.parseInt(systemConf2.getConfValue());
        if (swith == 0) {
            i = -1;
        } else if (swith == 1) {
            i = bookPageService.shutdownNow();
            systemConf2.setConfValue("0");
            systemConfMapper.updateByPrimaryKey(systemConf2);
        }
        if (i == 1) {
            resultEntity.setMsg("已关闭完成");
            resultEntity.setStatus(i);
        } else if (i == -1) {
            resultEntity.setMsg("无正在运行的线程池");
            resultEntity.setStatus(1);
        } else {
            resultEntity.setMsg("关闭失败！请联系管理员");
            resultEntity.setStatus(-1);
        }
        return resultEntity;
    }

    private static List<BookMetaFromExcel> FILES = null;

    //模板数据导入
    @RequestMapping(value = "/bookExcelAdd")
    public String bookExcelAdd(@RequestParam(required = false) String data, Model model) {
//        if (org.apache.commons.lang3.StringUtils.isNotBlank(data)) {
//            JSONArray objects = com.alibaba.fastjson.JSONObject.parseArray(data);
//            model.addAttribute("bookMetaFromExcels", objects);
//        }
        return "book/bookExcelAdd";
    }

    @PostMapping("/bookExcelAdd/import")
    public Object batchImportBookMeta(@RequestParam("file") MultipartFile file, Model model) {

        if (!file.getOriginalFilename().endsWith(".xlsx")) {
            return "文件格式不正确，仅支持 .xlsx 格式的文件";
        }
        // 读取Excel工具类
        Map<Integer, Map<Object, Object>> data = null;
        try (InputStream inputStream = file.getInputStream()) {
            String fileName = file.getOriginalFilename();
            ReadExcelTextUtils readExcelTextUtils = new ReadExcelTextUtils(inputStream, fileName);
            // 读取Excel中的内容
            data = readExcelTextUtils.getDataByInputStream();
            if (data == null || data.isEmpty()) {
                throw new Exception();
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "文件读取出错，请重新尝试或联系管理员！";
        } catch (Exception e) {
            return "文件出错，请检查文件格式是否正确或内容是否完整！";
        }
        List<BookMetaFromExcel> bookMetaFromExcels = null;
        try {
            bookMetaFromExcels = bookMetaService.importBookMetaFromExcel(data);
            BookController.bookMetaFromExcels = bookMetaFromExcels;
        } catch (Exception e) {
            log.error("异常信息： {}", e);
        }
        model.addAttribute("bookMetaFromExcels", bookMetaFromExcels);
        return "book/bookExcelAdd";
    }

    private static List<BookMetaFromExcel> bookMetaFromExcels = null;

    @PostMapping("/excelImportMeta")
    @ResponseBody
    public Object excelImportMeta(@RequestParam String fileInfo) {
        ResultEntity resultEntity = new ResultEntity();
        try {
            Set<BookMeta> set = new HashSet<>();
            List<BookMetaFromExcel> bookMetaFromExcels = BookController.bookMetaFromExcels;
            if (bookMetaFromExcels != null) {
                String[] split = fileInfo.split(";");
                for (String s : split) {
                    String[] split1 = s.split(",");//bookMetaFromExcel->bookMetaService.saveBookMeta(bookMetaFromExcel.getBookMetaTemp())
//                    bookMetaFromExcels.stream().filter(bm -> bm.getBookMetaTemp().getMetaId().equalsIgnoreCase(split1[0])).forEach(bookMetaFromExcel -> bookMetaService.saveBookMeta(bookMetaFromExcel.getBookMetaTemp()));
                    Iterator<BookMetaFromExcel> bookMetaFromExcel = bookMetaFromExcels.iterator();
                    while (bookMetaFromExcel.hasNext()) {
                        BookMetaFromExcel bookMetaFromExcel1 = bookMetaFromExcel.next();
                        String metaId = bookMetaFromExcel1.getBookMetaTemp().getMetaId();
                        if (org.apache.commons.lang3.StringUtils.isNotBlank(metaId) && metaId.equalsIgnoreCase(split1[0])) {
                            set.add(bookMetaFromExcel1.getBookMetaTemp());
                            bookMetaFromExcel.remove();
                        }
                    }
                }
                for (BookMeta bookMeta : set) {
                    //默认值
                    bookMeta.setHasCebx(0);
                    bookMeta.setIsReadEpub(0);
                    bookMeta.setIsReadCebxFlow(0);
                    bookMeta.setCreateTime(new Date());
                    bookMetaService.saveBookMeta(bookMeta);
                    ApabiBookMetaDataTemp apabiBookMetaDataTemp = transformApabiBookMeta(bookMeta);
                    apabiBookMetaDataTempDao.insert(apabiBookMetaDataTemp);
                }
            }
            resultEntity.setStatus(200);
            resultEntity.setMsg("上传成功");
        } catch (Exception e) {
            resultEntity.setStatus(500);
            resultEntity.setMsg("上传失败！请联系管理员");
            e.printStackTrace();
        }
        return resultEntity;
    }

    public ApabiBookMetaDataTemp transformApabiBookMeta(BookMeta bookMeta) {
        ApabiBookMetaDataTemp apabiBookMetaDataTemp = new ApabiBookMetaDataTemp();
        apabiBookMetaDataTemp.setHasCebx(0);
        apabiBookMetaDataTemp.setIsReadEpub(0);
        apabiBookMetaDataTemp.setIsReadCebxFlow(0);
        apabiBookMetaDataTemp.setCreateTime(new Date());
        apabiBookMetaDataTemp.setMetaId(bookMeta.getMetaId());
        apabiBookMetaDataTemp.setIsbn(bookMeta.getIsbn());
        apabiBookMetaDataTemp.setTitle(bookMeta.getTitle());
        apabiBookMetaDataTemp.setSubTitle(bookMeta.getSubTitle());
        apabiBookMetaDataTemp.setCreator(bookMeta.getCreator());
        apabiBookMetaDataTemp.setCreatorWord(bookMeta.getCreatorWord());
        apabiBookMetaDataTemp.setIsbn10(bookMeta.getIsbn10());
        apabiBookMetaDataTemp.setIsbn13(bookMeta.getIsbn13());
        apabiBookMetaDataTemp.setPublisher(bookMeta.getPublisher());
        apabiBookMetaDataTemp.setIssuedDate(bookMeta.getIssuedDate());
        apabiBookMetaDataTemp.setRelation(bookMeta.getRelation());
        apabiBookMetaDataTemp.setEditionOrder(bookMeta.getEditionOrder());
        apabiBookMetaDataTemp.setClassCode(bookMeta.getClassCode());
        apabiBookMetaDataTemp.setPlace(bookMeta.getPlace());
        apabiBookMetaDataTemp.setTranslator(bookMeta.getTranslator());
        apabiBookMetaDataTemp.setOriginTitle(bookMeta.getOriginTitle());
        apabiBookMetaDataTemp.setCreatorId(bookMeta.getCreatorId());
        apabiBookMetaDataTemp.setLanguage(bookMeta.getLanguage());
        apabiBookMetaDataTemp.setPreface(bookMeta.getPreface());
        apabiBookMetaDataTemp.setPaperPrice(bookMeta.getPaperPrice());
        apabiBookMetaDataTemp.setEbookPrice(bookMeta.getEbookPrice());
        apabiBookMetaDataTemp.setDoubanId(bookMeta.getDoubanId());
        apabiBookMetaDataTemp.setAmazonId(bookMeta.getAmazonId());
        apabiBookMetaDataTemp.setNlibraryId(bookMeta.getNlibraryId());
        return apabiBookMetaDataTemp;
    }

    @GetMapping("/test")
    @ResponseBody
    public String test() throws Exception {
        //bookChapterService.deleteAllChapterByMetaid("mm.20110920-DT-889-0295");
        //bookMetaService.updateHashFlow();
        //bookMetaService.updateIsbn();
        //MongoBookChapter mongoBookChapter = mongoBookService.findBookChapterByComId("20150203919413");
        //bookMetaService.insertBookMeta2Mongo("", null);
        //BookMeta bookMeta = bookMetaService.selectBookMetaDetailById("mm.20110920-DT-889-0295");
        //bookShardService.deleteAllShardByMetaid("m.201806141534996901459");
        //bookMetaService.exportMongo2Orlc();
        return "success";
    }

    //调用接口获取数据
    private String getCebxData(String url) {
        if (!StringUtils.isEmpty(url)) {
            try {
                HttpEntity httpEntity = HttpUtils.doGetEntity(url);
                String body = EntityUtils.toString(httpEntity);
                return body;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

}
