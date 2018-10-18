package com.apabi.flow.book.controller;

import cn.org.rapid_framework.page.PageRequest;
import com.apabi.flow.book.dao.BookMetaVoRepository;
import com.apabi.flow.book.entity.BookMetaMap;
import com.apabi.flow.book.model.*;
import com.apabi.flow.book.service.*;
import com.apabi.flow.book.util.BookUtil;
import com.apabi.flow.book.util.ReadBook;
import com.apabi.flow.common.CommEntity;
import com.apabi.flow.common.PageRequestFactory;
import com.apabi.flow.common.ResultEntity;
import com.apabi.flow.config.ApplicationConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
    BookMetaVoRepository bookMetaVoRepository;

    @Autowired
    ReadBook readBook;

    @Autowired
    private BookPageService bookPageService;

    @Autowired
    MongoBookService mongoBookService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    ApplicationConfig config;

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
                               @RequestParam("filePath") String filePath) {
        if (!StringUtils.isEmpty(fileInfo)) {
            if (!StringUtils.isEmpty(filePath)) {
                long start = System.currentTimeMillis();
                try {
                    List<BookBatchRes> bookBatchResList = readBook.batchChapter(fileInfo, filePath);
                    if (bookBatchResList != null && bookBatchResList.size() > 0) {
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
                    bookMetaList = bookMetaService.getBookMetaBatch(filePath);
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
    public String bookMeta(HttpServletRequest request, Model model) {
        try {
            long start = System.currentTimeMillis();
            PageRequest pageRequest = new PageRequest();
            PageRequestFactory.bindPageRequest(pageRequest, request);
            Map<String, Object> params = (Map<String, Object>) pageRequest.getFilters();
            String metaid = "";
            if (params.get("metaid") != null) {
                metaid = params.get("metaid").toString();
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
            String isbn = "";
            if (params.get("isbn") != null) {
                isbn = params.get("isbn").toString();
            }
            String isbnVal = "";
            if (params.get("isbnVal") != null) {
                isbnVal = params.get("isbnVal").toString();
            }
            //List<BookMetaVo> list = new ArrayList<>();
            //Page<BookMetaVo> page = new PageImpl<>(list);
            Page<BookMetaVo> page = null;
            if (params.size() > 0) {
                page = bookMetaService.queryPage(params, pageRequest.getPageNumber(), DEFAULT_PAGESIZE);
            }
            if (page == null) {
                model.addAttribute("bookMetaList", null);
                model.addAttribute("page", null);
            } else {
                model.addAttribute("bookMetaList", page.getContent());
                model.addAttribute("page", page);
            }

            model.addAttribute("metaid", metaid);
            model.addAttribute("title", title);
            model.addAttribute("creator", creator);
            model.addAttribute("publisher", publisher);
            model.addAttribute("isbn", isbn);
            model.addAttribute("isbnVal", isbnVal);
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
            } catch (ParseException e) {
                log.warn("出版日期格式转换失败");
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

    //编辑图书内容
    @GetMapping("/bookChapterEdit")
    public String bookChapterEdit(@RequestParam("metaid") String metaid, Model model) {
        long start = System.currentTimeMillis();
        if (!StringUtils.isEmpty(metaid)) {
            List<BookCataRows> cataRowsList = new ArrayList<>();
            List<BookCataRows> cataRows = bookMetaService.getCataRowsById(metaid);
            BookMetaVo bookMetaVo = bookMetaService.selectBookMetaById(metaid);
            //存放目录的章节编号
            List<Integer> chapterNums = new ArrayList<>();
            //存放目录的章节编号和目录
            Map<Integer, String> cataMap = new HashMap<>();
            if (cataRows != null && cataRows.size() > 0) {
                //拼接目录准备
                for (BookCataRows cata : cataRows) {
                    chapterNums.add(cata.getChapterNum());
                    cataMap.put(cata.getChapterNum(), cata.getChapterName());
                }
                chapterNums.add(bookMetaVo.getChapterSum());
                //拼接完整目录
                int startNum = 0;
                for (Integer num : chapterNums) {
                    int index = 1;
                    for (int i = startNum; i < num; i++) {
                        BookCataRows cata = new BookCataRows();
                        cata.setChapterNum(i);
                        String name = cataMap.get(i);
                        if (i == 0) {
                            cata.setChapterName("封面");
                            index--;
                        } else if (StringUtils.isEmpty(name)) {
                            cata.setChapterName("小节" + index);
                        } else {
                            cata.setChapterName(name);
                            index--;
                        }
                        cataRowsList.add(cata);
                        index++;
                    }
                    startNum = num;
                }

            }
            BookChapter bookChapter = bookChapterService.selectChapterById(metaid, 0);
            model.addAttribute("bookMetaVo", bookMetaVo);
            model.addAttribute("cataRows", cataRowsList);
            model.addAttribute("bookChapter", bookChapter);
        }
        long end = System.currentTimeMillis();
        log.info("获取图书" + metaid + "编辑页面耗时：" + (end - start) + "毫秒");
        return "book/bookChapterEdit";
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

    @GetMapping("/getShardById")
    @ResponseBody
    public ResultEntity getShardById(@RequestParam("metaid") String metaId,
                                     @RequestParam("chapterNum") Integer chapterNum,
                                     @RequestParam("index") Integer index) {
        long start = System.currentTimeMillis();
        BookShard bookShard = bookShardService.selectShardById(metaId, chapterNum, index);
        ResultEntity resultEntity = new ResultEntity();
        if (bookShard == null) {
            resultEntity.setMsg("error");
            resultEntity.setStatus(-1);
        } else {
            resultEntity.setMsg("success");
            resultEntity.setStatus(0);
            resultEntity.setBody(bookShard);
        }
        long end = System.currentTimeMillis();
        log.info("获取图书" + metaId + "，第" + chapterNum + "章节，第" +
                index + "分组耗时：" + (end - start) + "毫秒");
        return resultEntity;
    }

    @GetMapping("/getBookMetaSum")
    @ResponseBody
    public ResultEntity getBookMetaSum(@RequestParam("metaid") String metaId) {
        long start = System.currentTimeMillis();
        BookMetaVo bookMeta = bookMetaService.selectBookMetaById(metaId);
        List<BookChapterSum> chapterSums = bookChapterService.selectBookChapterSum(metaId);
        List<BookCataRows> bookCataRows = bookMetaService.getCataRowsById(metaId);
        Map<String, Object> metaMap = new HashMap<>();
        metaMap.put("bookMeta", bookMeta);
        metaMap.put("chapterSums", chapterSums);
        metaMap.put("bookCataRows", bookCataRows);
        ResultEntity resultEntity = new ResultEntity();
        if (bookMeta == null && chapterSums == null && bookCataRows == null) {
            resultEntity.setMsg("error");
            resultEntity.setStatus(-1);
        } else {
            resultEntity.setMsg("success");
            resultEntity.setStatus(0);
            resultEntity.setBody(metaMap);
        }
        long end = System.currentTimeMillis();
        log.info("获取图书章节及分组简要信息，耗时" + (end - start) + "毫秒");
        return resultEntity;
    }

    @GetMapping("/getBookChapterSum")
    @ResponseBody
    public ResultEntity getBookChapterSum(@RequestParam("metaid") String metaId) {
        long start = System.currentTimeMillis();
        List<BookChapterSum> chapterSums = bookChapterService.selectBookChapterSum(metaId);
        ResultEntity resultEntity = new ResultEntity();
        if (chapterSums == null) {
            resultEntity.setMsg("error");
            resultEntity.setStatus(-1);
        } else {
            resultEntity.setMsg("success");
            resultEntity.setStatus(0);
            resultEntity.setBody(chapterSums);
        }
        long end = System.currentTimeMillis();
        log.info("获取图书章节及分组简要信息，耗时" + (end - start) + "毫秒");
        return resultEntity;
    }

    @GetMapping("/getBookMeta")
    @ResponseBody
    public ResultEntity getBookMeta(@RequestParam("metaid") String metaId) {
        long start = System.currentTimeMillis();
        BookMetaVo bookMeta = bookMetaService.selectBookMetaById(metaId);
        ResultEntity resultEntity = new ResultEntity();
        if (bookMeta == null) {
            resultEntity.setMsg("error");
            resultEntity.setStatus(-1);
        } else {
            resultEntity.setMsg("success");
            resultEntity.setStatus(0);
            resultEntity.setBody(bookMeta);
        }
        long end = System.currentTimeMillis();
        log.info("获取图书元数据信息，耗时" + (end - start) + "毫秒");
        return resultEntity;
    }

    @GetMapping("/getCataRowsById")
    @ResponseBody
    public ResultEntity getCataRowsById(@RequestParam("metaid") String metaId) {
        long start = System.currentTimeMillis();
        List<BookCataRows> bookCataRows = bookMetaService.getCataRowsById(metaId);
        ResultEntity resultEntity = new ResultEntity();
        if (bookCataRows == null) {
            resultEntity.setMsg("error");
            resultEntity.setStatus(-1);
        } else {
            resultEntity.setMsg("success");
            resultEntity.setStatus(0);
            resultEntity.setBody(bookCataRows);
        }
        long end = System.currentTimeMillis();
        log.info("获取图书元目录信息，耗时" + (end - start) + "毫秒");
        return resultEntity;
    }

    /**
     * 从书苑获取 图书每页内容信息
     *
     * @param metaId
     * @return 返回是否成功，fail,success,这里需要重新定义（整体）
     */
    @GetMapping("/fetchShuyuanPageData/{metaId}")
    @ResponseBody
    public String fetchShuyuanPageData(@PathVariable String metaId) {
        int res = 0;
        try {
            res = bookPageService.insertShuyuanPageData(metaId);
        } catch (Exception e) {
            log.error("获取书苑分页数据出错", e);
        }
        return res == 0 ? "fail" : "success";
    }

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

    /**
     * 处理数据接口，将 图书从 页码信息 拼装成 章节信息
     *
     * @param metaid
     * @return
     * @throws Exception
     */
    @RequestMapping("/processBookFromPage2Chapter")
    @ResponseBody
    public int processBookFromPage2Chapter(@RequestParam("metaid") String metaid) throws Exception {
        return bookPageService.processBookFromPage2Chapter(metaid);
    }

    /**
     * 根据配置文件自动拉取分页数据
     *
     * @return 返回本次拉取的总页数
     */
    @ResponseBody
    @RequestMapping("/autoFetchPageData")
    public int autoFetchPageData() {
        return bookPageService.autoFetchPageData();
    }

    /**
     * 根据配置文件自动拉取分页数据
     *
     * @return 返回本次组装成功的图书数
     */
    @ResponseBody
    @RequestMapping("/autoProcessBookFromPage2Chapter")
    public int autoProcessBookFromPage2Chapter() {
        return bookPageService.autoProcessBookFromPage2Chapter();
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
}
