package com.apabi.flow.book.service.impl;

import com.apabi.flow.book.dao.*;
import com.apabi.flow.book.entity.MongoBookChapter;
import com.apabi.flow.book.entity.MongoBookMeta;
import com.apabi.flow.book.entity.MongoBookShard;
import com.apabi.flow.book.epublib.EpubReader2;
import com.apabi.flow.book.model.*;
import com.apabi.flow.book.service.BookChapterService;
import com.apabi.flow.book.service.BookMetaService;
import com.apabi.flow.book.service.BookShardService;
import com.apabi.flow.book.service.MongoBookService;
import com.apabi.flow.book.util.*;
import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.common.model.ZtreeNode;
import com.apabi.flow.config.ApplicationConfig;
import com.apabi.flow.douban.dao.ApabiBookMetaDataTempDao;
import com.apabi.flow.douban.model.ApabiBookMetaDataTemp;
import com.apabi.flow.douban.util.StringToolUtil;
import com.apabi.flow.publish.dao.ApabiBookMetaTempPublishRepository;
import com.apabi.flow.publish.model.ApabiBookMetaTempPublish;
import com.apabi.flow.systemconf.dao.SystemConfMapper;
import com.apabi.shuyuan.book.dao.SCmfDigitObjectDao;
import com.apabi.shuyuan.book.dao.SCmfDigitResfileSiteDao;
import com.apabi.shuyuan.book.dao.SCmfMetaDao;
import com.apabi.shuyuan.book.model.SCmfDigitObject;
import com.apabi.shuyuan.book.model.SCmfDigitResfileSite;
import com.apabi.shuyuan.book.model.SCmfMeta;
import com.github.pagehelper.Page;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.domain.Resource;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.stream.Collectors;

/**
 * @author guanpp
 * @date 2018/8/1 10:49
 * @description
 */
@Service("bookMetaService")
public class BookMetaServiceImpl implements BookMetaService {

    private static final Logger log = LoggerFactory.getLogger(BookMetaServiceImpl.class);

    public static String shuyuanOrgCode = PropertiesUtil.get("iyzhiCode");

    public static String baseUrlType = "comm/htmlpage.ashx";

    public static String urlType = "htmlpage.ashx";

    public static String serviceType = "htmlpage";

    public static final String iyzhiMobileMookCatalogUrl = PropertiesUtil.get("iyzhi.mobile.book.catalog.url");

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");

    private SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd");

    /*private static List<String> FILES = new ArrayList<>();

    private static List<String> XML_FILES = new ArrayList<>();

    private static List<String> CEBX_FILES = new ArrayList<>();*/

    private static final String FILE_KEY = "FILE_KEY";

    private static final String XML_KEY = "XML_KEY";

    private static final String CEBX_KEY = "CEBX_KEY";

    private static final String EPUB_SUFFIX = "epub";

    private static final String SPLIT_VALUE = "ISBN";

    private static final String CODE_VALUE = "UTF-8";

    private final static String getCataLog = "http://flow.apabi.com/flow/book/getFoamatCatalogByMetaId?metaid=";

    private final static String getCebxPage = "http://flow.apabi.com/flow/book/getCebxPageByMetaId?metaid=";

    private final String EXCEL_XLS = "xls";
    private final String EXCEL_XLSX = "xlsx";

    @Autowired
    BookMetaDao bookMetaDao;

    @Autowired
    BookChapterDao bookChapterDao;

    @Autowired
    BookShardDao bookShardDao;

    @Autowired
    private ApabiBookMetaTempPublishRepository apabiBookMetaTempPublishRepository;

    @Autowired
    BookFileDao bookFileDao;

    @Autowired
    BookChapterService bookChapterService;

    @Autowired
    BookShardService bookShardService;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    MongoBookService mongoBookService;

    @Autowired
    ApplicationConfig config;

    @Autowired
    private SCmfMetaDao sCmfMetaDao;

    @Autowired
    private SCmfDigitResfileSiteDao sCmfDigitResfileSiteDao;

    @Autowired
    private SCmfDigitObjectDao sCmfDigitObjectDao;

    @Autowired
    ApabiBookMetaDataTempDao bookMetaDataTempDao;

    @Autowired
    private CmfMetaDao cmfMetaDao;

    @Autowired
    private CmfDigitObjectDao cmfDigitObjectDao;

    @Autowired
    private CmfDigitResfileSiteDao cmfDigitResfileSiteDao;

    @Autowired
    SystemConfMapper systemConfMapper;

    //根据图书metaid，获取图书元数据
    @Override
    public BookMetaVo selectBookMetaById(String metaid) {
        if (!StringUtils.isEmpty(metaid)) {
            //return bookMetaVoRepository.findBookMetaVoByMetaidIs(metaid);
            return bookMetaDao.findBookMetaVoById(metaid);
        }
        return null;
    }

    //根据图书metaid，获取图书元数据
    @Override
    public EpubookMeta selectEpubookMetaById(String metaid) {
        if (!StringUtils.isEmpty(metaid)) {
            EpubookMeta epubookMeta = bookMetaDao.findEpubookMetaById(metaid);
            return epubookMeta;
        }
        return null;
    }

    //保存图书元数据
    @Override
    public int saveBookMeta(BookMeta bookMeta) {
//        BookMeta res = bookMetaRepository.save(bookMeta);
//        if (res != null) {
//            return 1;
//        }
        if (bookMeta != null) {
            bookMetaDao.insertBookMeta(bookMeta);
            return 1;
        }
        return 0;
    }

    //更新epub图书元数据
    @Override
    public int saveEpubookMeta(EpubookMeta epubookMeta) {
//        EpubookMeta res = epubookMetaRepository.save(epubookMeta);
//        if (res != null) {
//            return 1;
//        }
        if (epubookMeta != null) {
            bookMetaDao.updateEpubookMeta(epubookMeta);
            return 1;
        }
        return 0;
    }

    //更新图书元数据
    @Override
    public int updateBookMetaById(BookMeta bookMeta) {
        if (bookMeta != null) {
            //更新ISBN10或ISBN13
            String isbn = bookMeta.getIsbn();
            if (org.apache.commons.lang3.StringUtils.isNotBlank(isbn)) {
                isbn = isbn.replace("-", "");
                if (isbn.length() == 10) {
                    bookMeta.setIsbn10(isbn);
                } else if (isbn.length() == 13) {
                    bookMeta.setIsbn13(isbn);
                }
            }
            bookMetaDao.updateBookMetaById(bookMeta);
            return 1;
        }
        return 0;
    }

    //更新图书目录
    @Override
    public int updateCataTree(String metaId, String cataTree) {
        if (!StringUtils.isEmpty(metaId)) {
            if (!StringUtils.isEmpty(cataTree)) {
                JSONArray jsonArray = JSONArray.fromObject(cataTree);
                BookCataRows root = new BookCataRows();
                Iterator it = jsonArray.iterator();
                while (it.hasNext()) {
                    JSONObject jsonObject = (JSONObject) it.next();
                    createCataRows(jsonObject, root);
                }
                JSONArray json = JSONArray.fromObject(root.getChildren());
                BookMeta bookMeta = new BookMeta();
                bookMeta.setMetaId(metaId);
                bookMeta.setStreamCatalog(json.toString());
                bookMeta.setUpdateTime(new Date());
                int res = bookMetaDao.updateBookMetaById(bookMeta);
                if (res > 0) {
                    //更新tmp表
                    String sql = "UPDATE APABI_BOOK_METADATA_TEMP" +
                            " SET STREAMCATALOG = ?, UPDATETIME = ?" +
                            "WHERE METAID = ?";
                    Object[] objects = new Object[]{
                            bookMeta.getStreamCatalog(), bookMeta.getUpdateTime(),
                            bookMeta.getMetaId()
                    };
                    int ress = jdbcTemplate.update(sql, objects);
                    if (ress > 0) {
                        return 1;
                    }
                }
            }
        }
        return 0;
    }

    //构建目录对象list
    private void createCataRows(JSONObject jsonObject, BookCataRows parentCata) {
        if (jsonObject != null) {
            List<JSONObject> childE = jsonObject.getJSONArray("children");
            if (childE != null && childE.size() > 0) {
                BookCataRows cataRows = new BookCataRows();
                cataRows.setChapterName(jsonObject.getString("name"));
                cataRows.setChapterNum(jsonObject.getInt("nodeId"));
                cataRows.setUrl(jsonObject.getString("src"));
                cataRows.setEbookPageNum(jsonObject.getInt("ebookPageNum"));
                cataRows.setWordSum(jsonObject.getInt("wordSum"));
                for (JSONObject child : childE) {
                    createCataRows(child, cataRows);
                }
                parentCata.getChildren().add(cataRows);
            } else {
                BookCataRows bookCataRows = new BookCataRows();
                bookCataRows.setChapterName(jsonObject.getString("name"));
                bookCataRows.setChapterNum(jsonObject.getInt("nodeId"));
                bookCataRows.setUrl(jsonObject.getString("src"));
                bookCataRows.setEbookPageNum(jsonObject.getInt("ebookPageNum"));
                bookCataRows.setWordSum(jsonObject.getInt("wordSum"));
                parentCata.getChildren().add(bookCataRows);
            }
        }
    }

    //删除图书元数据
    @Override
    public int deleteBookMeta(String metaid) {
        if (!StringUtils.isEmpty(metaid)) {
            //bookMetaRepository.deleteById(metaid);
            return 1;
        }
        return 0;
    }

    //删除图书内容
    @Override
    public int deleteBookChapter(String metaid) {
        if (!StringUtils.isEmpty(metaid)) {
            //删除图书metaid下的章节内容
            bookChapterService.deleteAllChapterByMetaid(metaid);
            //删除图书metaid下的章节分组内容
            bookShardService.deleteAllShardByMetaid(metaid);
        }
        return 0;
    }

    //查询图书是否存在
    @Override
    public int countBookMetaById(String metaid) {
        if (!StringUtils.isEmpty(metaid)) {
            //return bookMetaVoRepository.countByMetaidIs(metaid);
            return bookMetaDao.countBookMetaVoById(metaid);
        }
        return 0;
    }

    //获取目录信息
    @Override
    public List<BookCataRows> getCataRowsById(String metaid) {
        if (!StringUtils.isEmpty(metaid)) {
            String cataRow = bookMetaDao.findCataRowsById(metaid);
            if (!StringUtils.isEmpty(cataRow)) {
                String tmp = String.valueOf(cataRow.charAt(0));
                if (tmp.equals("[")) {
                    //获取层次目录
                    JSONArray jsonArray = JSONArray.fromObject(cataRow);
                    List<BookCataRows> rowsList = new ArrayList<>();
                    BookCataRows root = new BookCataRows();
                    Iterator it = jsonArray.iterator();
                    while (it.hasNext()) {
                        JSONObject jsonObject = (JSONObject) it.next();
                        createCataTree(jsonObject, root);
                    }
                    rowsList.addAll(root.getChildren());
                    return rowsList;
                } else {
                    //获取非层次目录
                    List<String> cataRows = Arrays.asList(cataRow.split("},"));
                    List<BookCataRows> cataRowsList = new ArrayList<>();
                    //生成目录
                    JSONObject jsonObject;
                    for (String cata : cataRows) {
                        jsonObject = JSONObject.fromObject(cata + "}");
                        BookCataRows cataLog = (BookCataRows) JSONObject.toBean(jsonObject, BookCataRows.class);
                        cataRowsList.add(cataLog);
                    }
                    //按章节号进行排序
                    List<BookCataRows> rowsList = cataRowsList.stream()
                            .sorted(Comparator.comparingInt(BookCataRows::getChapterNum))
                            .collect(Collectors.toList());
                    return rowsList;
                }
            }
        }
        return null;
    }

    //构建目录对象list
    private void createCataTree(JSONObject jsonObject, BookCataRows parentCata) {
        if (jsonObject != null) {
            List<JSONObject> childE = jsonObject.getJSONArray("children");
            if (childE != null && childE.size() > 0) {
                BookCataRows cataRows = new BookCataRows();
                cataRows.setChapterName(jsonObject.getString("chapterName"));
                cataRows.setChapterNum(jsonObject.getInt("chapterNum"));
                cataRows.setUrl(jsonObject.getString("url"));
                cataRows.setEbookPageNum(jsonObject.getInt("ebookPageNum"));
                cataRows.setWordSum(jsonObject.getInt("wordSum"));
                for (JSONObject child : childE) {
                    createCataTree(child, cataRows);
                }
                parentCata.getChildren().add(cataRows);
            } else {
                BookCataRows bookCataRows = new BookCataRows();
                bookCataRows.setChapterName(jsonObject.getString("chapterName"));
                bookCataRows.setChapterNum(jsonObject.getInt("chapterNum"));
                bookCataRows.setUrl(jsonObject.getString("url"));
                bookCataRows.setEbookPageNum(jsonObject.getInt("ebookPageNum"));
                bookCataRows.setWordSum(jsonObject.getInt("wordSum"));
                parentCata.getChildren().add(bookCataRows);
            }
        }
    }

    //获取目录树
    @Override
    public String getCataTreeById(String metaid) {
        if (!StringUtils.isEmpty(metaid)) {
            String cataRow = bookMetaDao.findCataRowsById(metaid);
            if (!StringUtils.isEmpty(cataRow)) {
                String tmp = String.valueOf(cataRow.charAt(0));
                if (tmp.equals("[")) {
                    //获取层次目录
                    JSONArray jsonArray = JSONArray.fromObject(cataRow);
                    ZtreeNode root = new ZtreeNode();
                    Iterator it = jsonArray.iterator();
                    while (it.hasNext()) {
                        JSONObject jsonObject = (JSONObject) it.next();
                        createZTree(jsonObject, root);
                    }
                    JSONArray json = JSONArray.fromObject(root.getChildren());
                    return json.toString();
                } else {
                    //获取非层次目录
                    cataRow = "[" + cataRow + "]";
                    JSONArray jsonArray = JSONArray.fromObject(cataRow);
                    ZtreeNode root = new ZtreeNode();
                    Iterator it = jsonArray.iterator();
                    while (it.hasNext()) {
                        JSONObject jsonObject = (JSONObject) it.next();
                        createZTree(jsonObject, root);
                    }
                    JSONArray json = JSONArray.fromObject(root.getChildren());
                    return json.toString();
                }
            }
        }
        return null;
    }

    //构建目录对象list
    private void createZTree(JSONObject jsonObject, ZtreeNode parentCata) {
        if (jsonObject != null) {
            boolean flag = jsonObject.has("children");
            List<JSONObject> childE = null;
            if (flag) {
                childE = jsonObject.getJSONArray("children");
            }
            if (childE != null && childE.size() > 0) {
                ZtreeNode cataRows = new ZtreeNode();
                cataRows.setName(jsonObject.getString("chapterName"));
                cataRows.setNodeId(jsonObject.getInt("chapterNum"));
                cataRows.setSrc(jsonObject.getString("url"));
                boolean res = jsonObject.containsKey("wordSum");
                if (res) {
                    cataRows.setWordSum(jsonObject.getInt("wordSum"));
                }
                cataRows.setEbookPageNum(jsonObject.getInt("ebookPageNum"));
                for (JSONObject child : childE) {
                    createZTree(child, cataRows);
                }
                parentCata.getChildren().add(cataRows);
            } else {
                ZtreeNode bookCataRows = new ZtreeNode();
                bookCataRows.setName(jsonObject.getString("chapterName"));
                bookCataRows.setNodeId(jsonObject.getInt("chapterNum"));
                bookCataRows.setSrc(jsonObject.getString("url"));
                boolean res = jsonObject.containsKey("wordSum");
                if (res) {
                    bookCataRows.setWordSum(jsonObject.getInt("wordSum"));
                }
                bookCataRows.setEbookPageNum(jsonObject.getInt("ebookPageNum"));
                parentCata.getChildren().add(bookCataRows);
            }
        }
    }

    //解析xml数据，并保存
    @Override
    public int saveXmlBookMeta(String path) throws Exception {
        if (!StringUtils.isEmpty(path)) {
            Document doc = Xml2BookMeta.getXmlSourceFromUrl(path);
            if (doc != null) {
                BookMeta bookMeta = Xml2BookMeta.docToBookMeta(doc);
                //int count = bookMetaVoRepository.countByMetaidIs(bookMeta.getMetaId());
                int count = bookMetaDao.countBookMetaVoById(bookMeta.getMetaId());
                if (count == 0) {
                    BookFile bookFile = new BookFile();
                    File tmp = new File(path);
                    bookFile.setFileName(tmp.getName());
                    bookFile.setFileType("xml");
                    bookFile.setMetaid(bookMeta.getMetaId());
                    bookFile.setId(UUIDCreater.nextId());
                    bookFile.setCreateTime(new Date());
                    bookFile.setUpdateTime(new Date());
                    //保存图书和文件的对应关系
                    //bookFileRepository.save(bookFile);
                    bookFileDao.insertBookFile(bookFile);
                    //保存图书元数据
                    //bookMetaRepository.save(bookMeta);
                    bookMetaDao.insertBookMeta(bookMeta);
                    //保存图书元数据到tmp
                    ApabiBookMetaTempPublish bookMetaTemp = new ApabiBookMetaTempPublish();
                    BeanUtils.copyProperties(bookMeta, bookMetaTemp);
                    if (bookMetaTemp != null) {
                        apabiBookMetaTempPublishRepository.save(bookMetaTemp);
                    }
                    return 1;
                } else if (count > 0) {
                    return -1;
                }
            }
        }
        return 0;
    }

    //从书苑获取css样式
    private String getCss(String metaId, Date publishDate) throws IOException {
        if (org.apache.commons.lang.StringUtils.isNotBlank(metaId) && publishDate != null) {
            SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd");
            String pubDate = sdf1.format(publishDate).replace("-", "");
            FileOutputStream fos = null;
            String css = null;
            String cssPath = config.getStyleUrl()
                    + File.separator + pubDate
                    + File.separator + metaId
                    + File.separator + "styles"
                    + File.separator + "styles.css";

            String cssLink = BookConstant.BASE_URL
                    + File.separator + pubDate
                    + File.separator + metaId
                    + File.separator + "styles"
                    + File.separator + "styles.css";
            try {
                //从redis获取css
                String key = "apabi_wx_flow_css_" + metaId;
                try {
                    log.debug("======从redis获取css数据======");
                    long a = System.currentTimeMillis();
                    //css = redisService.get(key);
                    css = null;
                    log.debug("从redis获取css数据耗时 {} 毫秒", (System.currentTimeMillis() - a));

                } catch (Exception e) {
                    log.error("从redis获取css数据异常：{}", e);
                }
                //从书苑获取css
                if (org.apache.commons.lang.StringUtils.isBlank(css)) {
                    String url = EbookUtil.makeCssUrl(shuyuanOrgCode, metaId);
                    long a = System.currentTimeMillis();
                    //HttpEntity httpEntity = HttpUtils.doGetEntity(url);
                    HttpEntity httpEntity;
                    while (true) {
                        try {
                            httpEntity = HttpUtils.doGetEntity(url);
                            break;
                        } catch (SocketTimeoutException e) {
                            log.debug("获取图书：" + metaId + "css样式文件超时，将发起重新请求");
                            continue;
                        }
                    }
                    long b = System.currentTimeMillis();
                    log.info("getcss请求耗时[" + (b - a) + "毫秒]url[" + url + "]");
                    css = EntityUtils.toString(httpEntity);
                }
                //生成css文件
                if (org.apache.commons.lang.StringUtils.isNotBlank(css)) {
                    File file = new File(cssPath);
                    File fileParent = file.getParentFile();
                    if (!fileParent.exists()) {
                        fileParent.mkdirs();
                    }
                    file.createNewFile();
                    fos = new FileOutputStream(file);
                    fos.write(css.getBytes());
                    return cssLink;
                }
            } catch (Exception e) {
                log.error("微信流式阅读获取Css异常 :", e);
            } finally {
                if (fos != null) {
                    fos.close();
                }
            }
        }
        return null;
    }

    //分页查询
    @Override
    public Page<BookMetaVo> findBookMetaVoByPage(Map<String, Object> queryMap) {
        return bookMetaDao.findBookMetaVoByPage(queryMap);
    }

    //获取指定图书的所有章节内容
    private List getAllChapter(String metId, String newId) throws Exception {
        String url = "http://172.18.82.34:8080/nr2kserver/epub/getAllChapterById?id=";
        HttpEntity httpEntity;
        String tmp;
        String comId;
        httpEntity = HttpUtils.doGetEntity(url + metId);
        tmp = EntityUtils.toString(httpEntity);
        JSONArray array = JSONArray.fromObject(tmp);
        List<BookChapter> bookChapters = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            BookChapter bookChapter = new BookChapter();
            JSONObject jsonObject = array.getJSONObject(i);
            comId = newId + jsonObject.getInt("chapterNum");
            bookChapter.setComId(comId);
            bookChapter.setChapterNum(jsonObject.getInt("chapterNum"));
            bookChapter.setShardSum(jsonObject.getInt("shardSum"));
            bookChapter.setWordSum(jsonObject.getInt("wordSum"));
            bookChapter.setBodyClass(jsonObject.getString("bodyClass"));
            bookChapter.setContent(jsonObject.getString("content"));

            String date1 = jsonObject.getString("createdDate");
            Date createTime = new Date(Long.parseLong(date1));
            bookChapter.setCreateTime(createTime);

            String date2 = jsonObject.getString("lastModifiedDate");
            Date updateTime = new Date(Long.parseLong(date2));
            bookChapter.setUpdateTime(updateTime);

            bookChapters.add(bookChapter);
        }
        return bookChapters;
    }

    //获取指定图书的所有章节分组
    private List getAllShard(String metId, String newId) throws Exception {
        String url = "http://172.18.82.34:8080/nr2kserver/epub/getAllShardById?id=";
        HttpEntity httpEntity;
        String tmp;
        String comId;
        httpEntity = HttpUtils.doGetEntity(url + metId);
        tmp = EntityUtils.toString(httpEntity);
        JSONArray array = JSONArray.fromObject(tmp);
        List<BookShard> bookShards = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            BookShard bookShard = new BookShard();
            JSONObject jsonObject = array.getJSONObject(i);
            comId = newId + jsonObject.getInt("chapterNum") + jsonObject.getInt("index");
            bookShard.setComId(comId);
            bookShard.setChapterNum(jsonObject.getInt("chapterNum"));
            bookShard.setIndex(jsonObject.getInt("index"));
            bookShard.setWordSum(jsonObject.getInt("wordSum"));
            bookShard.setBodyClass(jsonObject.getString("bodyClass"));
            bookShard.setContent(jsonObject.getString("content"));

            String date1 = jsonObject.getString("createdDate");
            Date createTime = new Date(Long.parseLong(date1));
            bookShard.setCreateTime(createTime);

            String date2 = jsonObject.getString("lastModifiedDate");
            Date updateTime = new Date(Long.parseLong(date2));
            bookShard.setUpdateTime(updateTime);

            bookShards.add(bookShard);
        }
        return bookShards;
    }

    public Workbook getWorkbok(File file) throws IOException {
        Workbook wb = null;
        FileInputStream in = new FileInputStream(file);
        if (file.getName().endsWith(EXCEL_XLS)) {
            //Excel&nbsp;2003
            wb = new HSSFWorkbook(in);
        } else if (file.getName().endsWith(EXCEL_XLSX)) {
            // Excel 2007/2010
            wb = new XSSFWorkbook(in);
        }
        in.close();
        return wb;
    }

    //将指定图书存入爱读爱看
    @Override
    public int insertBookMeta2Mongo(String ids, Map<String, String> conds) throws ParseException {
        if (!StringUtils.isEmpty(ids)) {
            int sum = 0;
            List<String> idList = Arrays.asList(ids.split(","));
            String original = conds.get("original");
            String optimized = conds.get("optimized");
            String platform = conds.get("platform");
            String libId = conds.get("libId");
            for (String metaid : idList) {
                if (metaid.equals("on")) {
                    continue;
                }
                MongoBookMeta mongo = mongoBookService.findBookMetaById(metaid);
                if (mongo == null || mongo.getChapterSum() == 0) {
                    BookMeta bookMeta = bookMetaDao.findBookMetaById(metaid);
                    if (bookMeta != null) {
                        MongoBookMeta mongoBookMeta = new MongoBookMeta();
                        mongoBookMeta.setOriginal(original);
                        mongoBookMeta.setOptimized(optimized);
                        mongoBookMeta.setPlatform(platform);
                        List<String> libs = new ArrayList<>();
                        libs.add(libId);
                        mongoBookMeta.setLibId(libs);
                        mongoBookMeta.setId(bookMeta.getMetaId());
                        mongoBookMeta.setTitle(bookMeta.getTitle());
                        mongoBookMeta.setCreator(bookMeta.getCreator());
                        mongoBookMeta.setPublisher(bookMeta.getPublisher());
                        mongoBookMeta.setSummary(bookMeta.getAbstract_());
                        String publishDate = bookMeta.getIssuedDate();
                        if (!StringUtils.isEmpty(publishDate)) {
                            if (publishDate.contains("/")) {
                                mongoBookMeta.setPublishDate(sdf2.parse(publishDate));
                            } else {
                                mongoBookMeta.setPublishDate(sdf1.parse(publishDate));
                            }
                        }
                        mongoBookMeta.setIsbn(bookMeta.getIsbn());
                        mongoBookMeta.setChapterSum(bookMeta.getChapterNum());
                        mongoBookMeta.setWordSum(bookMeta.getContentNum());
                        mongoBookMeta.setLanguage(bookMeta.getLanguage());
                        mongoBookMeta.setType(bookMeta.getType());
                        mongoBookMeta.setCssUrl(bookMeta.getStyleUrl());
                        mongoBookMeta.setCoverUrl(bookMeta.getCoverUrl());
                        mongoBookMeta.setCoverMiniUrl(bookMeta.getThumImgUrl());
                        mongoBookMeta.setBodyClass(bookMeta.getStyleClass());
                        mongoBookMeta.set_route_("shard1");
                        mongoBookMeta.setCreatedDate(new Date());
                        mongoBookMeta.setLastModifiedDate(new Date());
                        //发布图书元数据
                        mongoBookService.updateBookMeta(mongoBookMeta);
                        sum++;
                        //获取章节内容分组
                        /*List<MongoBookShard> shardList = getMongoShards(metaid);
                        if (shardList != null && shardList.size() > 0) {
                            int res1 = mongoBookService.updateBookShard(shardList);
                            if (res1 > 0) {
                                //获取章节内容
                                List<MongoBookChapter> chapterList = getMongoChapters(metaid);
                                if (chapterList != null && chapterList.size() > 0) {
                                    int res2 = mongoBookService.updateBookChapter(chapterList);
                                    if (res2 > 0) {
                                        //发布图书元数据
                                        mongoBookService.updateBookMeta(mongoBookMeta);
                                        sum++;
                                    }
                                }
                            }
                        }*/
                    } else {
                        log.warn("流式服务中图书" + metaid + "不存在");
                    }
                }
            }
            return sum;
        }
        return 0;
    }

    //将章节内容转换为爱读爱看格式
    private List<MongoBookChapter> getMongoChapters(String metaId) {
        if (!StringUtils.isEmpty(metaId)) {
            List<BookChapter> chapterList = bookChapterDao.findAllBookChapter(metaId);
            if (chapterList != null && chapterList.size() > 0) {
                List<MongoBookChapter> mongoBookChapters = new ArrayList<>();
                for (BookChapter chapter : chapterList) {
                    MongoBookChapter mongoBookChapter = new MongoBookChapter();
                    mongoBookChapter.setId(metaId + chapter.getChapterNum());
                    mongoBookChapter.setChapterNum(chapter.getChapterNum());
                    mongoBookChapter.setWordSum(chapter.getWordSum());
                    mongoBookChapter.setShardSum(chapter.getShardSum());
                    mongoBookChapter.setBodyClass(chapter.getBodyClass());
                    mongoBookChapter.setContent(chapter.getContent());
                    mongoBookChapter.setCreatedDate(sdf.format(new Date()));
                    mongoBookChapter.setLastModifiedDate(sdf.format(new Date()));
                    mongoBookChapters.add(mongoBookChapter);
                }
                return mongoBookChapters;
            }
        }
        return null;
    }

    //将章节内容分组转换为爱读爱看格式
    private List<MongoBookShard> getMongoShards(String metaId) {
        if (!StringUtils.isEmpty(metaId)) {
            List<BookShard> shardList = bookShardDao.findAllBookShard(metaId);
            if (shardList != null && shardList.size() > 0) {
                List<MongoBookShard> mongoBookShards = new ArrayList<>();
                for (BookShard bookShard : shardList) {
                    MongoBookShard mongoBookShard = new MongoBookShard();
                    mongoBookShard.setId(metaId + bookShard.getChapterNum() + bookShard.getIndex());
                    mongoBookShard.setChapterNum(bookShard.getChapterNum());
                    mongoBookShard.setIndex(bookShard.getIndex());
                    mongoBookShard.setWordSum(bookShard.getWordSum());
                    mongoBookShard.setBodyClass(bookShard.getBodyClass());
                    mongoBookShard.setContent(bookShard.getContent());
                    mongoBookShard.setCreatedDate(sdf.format(new Date()));
                    mongoBookShard.setLastModifiedDate(sdf.format(new Date()));
                    mongoBookShards.add(mongoBookShard);
                }
                return mongoBookShards;
            }
        }
        return null;
    }

    //获取图书元数据
    @Override
    public BookMeta selectBookMetaDetailById(String metaid) {
        return bookMetaDao.findBookMetaById(metaid);
    }

    //获取epub文件的图书元数据
    @Override
    public List<BookMetaBatch> getBookMetaEpubBatch(String dirPath) {
        if (!StringUtils.isEmpty(dirPath)) {
            long start = System.currentTimeMillis();
            File dir = new File(dirPath);
            if (!dir.exists()) {
                log.warn("目录" + dirPath + "不存在");
            } else {
                //读取所有文件
                List<String> FILES;
                Map<String, List> fileMap = new HashMap<>();
                fileMap.put(FILE_KEY, new ArrayList());
                func(dir, fileMap);
                //将文件分别存储
                FILES = fileMap.get(FILE_KEY);
                if (FILES.size() > 0) {
                    List<BookMetaBatch> bookMetaBatches = new ArrayList<>();
                    for (String file : FILES) {
                        File newFile = new File(file);
                        List<BookMetaBatch> metaBatches = new ArrayList<>();
                        try {
                            Book book = getBook(file);
                            if (book != null) {
                                String fileName;
                                String title;
                                String isbn = null;
                                String isbnMeta = null;
                                String isbnFileName = null;
                                //获取文件名
                                fileName = newFile.getName();
                                if (!org.apache.commons.lang.StringUtils.isEmpty(fileName)) {
                                    //如果文件名符合"m."规则，则从数据库中查询
                                    if (fileName.length() > 1 && fileName.substring(0, 2).equals("m.")) {
                                        String metaId = fileName.replace(".epub", "");
                                        metaBatches = bookMetaDao.findBookMetaBatchById(metaId);
                                    } else {
                                        //使用插件获取isbn
                                        isbnMeta = getIsbn4Meta(book);
                                        //获取文件中的isbn
                                        isbn = getIsbn4Content(book);
                                        //从文件名中获取isbn
                                        isbnFileName = getIsbn4FileName(fileName);
                                        //isbn = getIsbnForContent(book);
                                        //使用文件中获取的isbn
                                        if (!StringUtils.isEmpty(isbn)) {
                                            metaBatches = bookMetaDao.findBookMetaBatchByIsbn(isbn);
                                            //使用isbn13查找
                                            if (metaBatches.size() == 0) {
                                                String isbn13 = isbn.replace("-", "");
                                                metaBatches = bookMetaDao.findBookMetaBatchByIsbn13(isbn13);
                                            }
                                        } else if (!StringUtils.isEmpty(isbnMeta)) {
                                            //如果文件中的isbn为空，则使用插件获取的isbn
                                            metaBatches = bookMetaDao.findBookMetaBatchByIsbn(isbnMeta);
                                            //使用isbn13查找
                                            if (metaBatches.size() == 0) {
                                                String isbn13 = isbnMeta.replace("-", "");
                                                metaBatches = bookMetaDao.findBookMetaBatchByIsbn13(isbn13);
                                            }
                                        } else if (!StringUtils.isEmpty(isbnFileName)) {
                                            //如果文件中的isbn和插件获取的isbn为空，则使用文件名中的isbn
                                            metaBatches = bookMetaDao.findBookMetaBatchByIsbn(isbnFileName);
                                            //使用isbn13查找
                                            if (metaBatches.size() == 0) {
                                                String isbn13 = isbnFileName.replace("-", "");
                                                metaBatches = bookMetaDao.findBookMetaBatchByIsbn13(isbn13);
                                            }
                                        }
                                    }
                                }
                                if (metaBatches.size() > 0) {
                                    for (BookMetaBatch bookMetaBatch : metaBatches) {
                                        //文件名
                                        bookMetaBatch.setFileName(fileName);
                                        //从文件中获取的isbn
                                        if (!StringUtils.isEmpty(isbn)) {
                                            bookMetaBatch.setFileIsbn(isbn);
                                        } else if (!StringUtils.isEmpty(isbnMeta)) {
                                            bookMetaBatch.setFileIsbn(isbnMeta);
                                        } else {
                                            bookMetaBatch.setFileIsbn(isbnFileName);
                                        }
                                        //获取书名
                                        title = book.getTitle();
                                        if (!org.apache.commons.lang.StringUtils.isEmpty(fileName)) {
                                            bookMetaBatch.setTitle(title);
                                        }
                                        bookMetaBatches.add(bookMetaBatch);
                                    }
                                } else {
                                    //数据库中不存在
                                    BookMetaBatch bookMetaBatch = new BookMetaBatch();
                                    //文件名
                                    bookMetaBatch.setFileName(fileName);
                                    //从文件中获取isbn
                                    if (StringUtils.isEmpty(isbnMeta)) {
                                        isbnMeta = getIsbn4Meta(book);
                                    }
                                    if (StringUtils.isEmpty(isbn)) {
                                        isbn = getIsbn4Content(book);
                                    }
                                    if (StringUtils.isEmpty(isbnFileName)) {
                                        isbnFileName = getIsbn4FileName(fileName);
                                    }
                                    //文件isbn
                                    if (!StringUtils.isEmpty(isbn)) {
                                        bookMetaBatch.setFileIsbn(isbn);
                                    } else if (!StringUtils.isEmpty(isbnMeta)) {
                                        bookMetaBatch.setFileIsbn(isbnMeta);
                                    } else {
                                        bookMetaBatch.setFileIsbn(isbnFileName);
                                    }
                                    //获取书名
                                    title = book.getTitle();
                                    if (!org.apache.commons.lang.StringUtils.isEmpty(fileName)) {
                                        bookMetaBatch.setTitle(title);
                                    }
                                    bookMetaBatches.add(bookMetaBatch);
                                }
                            } else {
                                BookMetaBatch bookMetaBatch = new BookMetaBatch();
                                //文件名
                                bookMetaBatch.setFileName(newFile.getName());
                                bookMetaBatches.add(bookMetaBatch);
                            }
                        } catch (Exception e) {
                            log.warn(newFile.getName() + "," + e.getMessage());
                        }
                    }
                    long end = System.currentTimeMillis();
                    log.info("获取epub文件的元数据，耗时：" + (end - start) + "毫秒");
                    return bookMetaBatches;
                } else {
                    log.warn("文件目录" + dirPath + "不存在文件");
                }
            }
        }
        return null;
    }

    //获取cebx文件的图书元数据
    @Override
    public List<BookMetaBatch> getBookMetaCebxBatch(String dirPath) {
        if (!StringUtils.isEmpty(dirPath)) {
            long start = System.currentTimeMillis();
            File dir = new File(dirPath);
            if (!dir.exists()) {
                log.warn("目录" + dirPath + "不存在");
            } else {
                //读取所有文件
                List<String> XML_FILES;
                List<String> CEBX_FILES;
                Map<String, List> fileMap = new HashMap<>();
                fileMap.put(XML_KEY, new ArrayList());
                fileMap.put(CEBX_KEY, new ArrayList());
                func(dir, fileMap);
                //将文件分别存储
                XML_FILES = fileMap.get(XML_KEY);
                CEBX_FILES = fileMap.get(CEBX_KEY);
                List<BookMetaBatch> bookMetaBatches = new ArrayList<>();
                List<BookMetaBatch> metaBatches = new ArrayList<>();
                if (XML_FILES != null && XML_FILES.size() > 0) {
                    //扫描xml文件
                    for (String path : XML_FILES) {
                        File newFile = new File(path);
                        try {
                            String metaId = Xml2BookMeta.getMetaId4Xml(path);
                            if (!StringUtils.isEmpty(metaId)) {
                                metaBatches = bookMetaDao.findBookMetaBatchById(metaId);
                                if (metaBatches.size() == 0) {
                                    BookMetaBatch bookMetaBatch = new BookMetaBatch();
                                    bookMetaBatch.setFileName(newFile.getName().replace(".xml", ".cebx"));
                                    metaBatches.add(bookMetaBatch);
                                } else {
                                    //用于排序
                                    for (BookMetaBatch bookMetaBatch : metaBatches) {
                                        bookMetaBatch.setFileName(newFile.getName().replace(".xml", ".cebx"));
                                    }
                                }
                                bookMetaBatches.addAll(metaBatches);
                            } else {
                                BookMetaBatch bookMetaBatch = new BookMetaBatch();
                                bookMetaBatch.setFileName(newFile.getName().replace(".xml", ".cebx"));
                                metaBatches.add(bookMetaBatch);
                                bookMetaBatches.add(bookMetaBatch);
                            }
                        } catch (Exception e) {
                            log.warn("{\"status\":\"{}\",\"file\":\"{}\",\"message\":\"{}\"}", -1, newFile.getName(), e.getMessage());
                        }
                    }
                } else {
                    //扫描cebx文件
                    for (String cebxFile : CEBX_FILES) {
                        File newFile = new File(cebxFile);
                        String fileName = newFile.getName();
                        String metaId;
                        if (fileName.length() > 1 && fileName.substring(0, 2).equals("m.")) {
                            //如果文件名符合"m."规则，则从数据库中查询
                            metaId = fileName.replace(".cebx", "");
                        } else {
                            //扫描cebx文件，通过文件名从书苑获取metaId
                            metaId = sCmfMetaDao.getMetaIdByFileName(fileName.replace("cebx", "ceb"));
                        }
                        if (!StringUtils.isEmpty(metaId)) {
                            metaBatches = bookMetaDao.findBookMetaBatchById(metaId);
                            if (metaBatches.size() == 0) {
                                BookMetaBatch bookMetaBatch = new BookMetaBatch();
                                bookMetaBatch.setFileName(newFile.getName());
                                metaBatches.add(bookMetaBatch);
                            } else {
                                //用于排序
                                for (BookMetaBatch bookMetaBatch : metaBatches) {
                                    bookMetaBatch.setFileName(newFile.getName());
                                }
                            }
                            bookMetaBatches.addAll(metaBatches);
                        } else {
                            BookMetaBatch bookMetaBatch = new BookMetaBatch();
                            bookMetaBatch.setFileName(newFile.getName());
                            bookMetaBatches.add(bookMetaBatch);
                        }
                    }
                }
                long end = System.currentTimeMillis();
                log.info("{\"status\":\"{}\",\"file\":\"{}\",\"useTime\":\"{}\"}", 0, dirPath, (end - start));
                return bookMetaBatches;
            }
        }
        return null;
    }

    //正则获取isbn13
    private static String getIsbn13(String chapter) {
        if (!org.apache.commons.lang.StringUtils.isEmpty(chapter)) {
            String isbn;
            Matcher matcher = BookConstant.REG_ISBN1.matcher(chapter);
            if (matcher.find()) {
                isbn = matcher.group();
                return isbn;
            } else {
                matcher = BookConstant.REG_ISBN2.matcher(chapter);
                if (matcher.find()) {
                    isbn = matcher.group();
                    return isbn;
                } else {
                    matcher = BookConstant.REG_ISBN3.matcher(chapter);
                    if (matcher.find()) {
                        isbn = matcher.group();
                        return isbn;
                    } else {
                        matcher = BookConstant.REG_ISBN4.matcher(chapter);
                        if (matcher.find()) {
                            isbn = matcher.group();
                            return isbn;
                        }
                    }
                }
            }
        }
        return null;
    }

    //正则获取isbn10
    private static String getIsbn10(String chapter) {
        if (!org.apache.commons.lang.StringUtils.isEmpty(chapter)) {
            String isbn;
            Matcher matcher = BookConstant.REG_ISBN5.matcher(chapter);
            if (matcher.find()) {
                isbn = matcher.group();
                return isbn;
            } else {
                matcher = BookConstant.REG_ISBN6.matcher(chapter);
                if (matcher.find()) {
                    isbn = matcher.group();
                    return isbn;
                } else {
                    matcher = BookConstant.REG_ISBN7.matcher(chapter);
                    if (matcher.find()) {
                        isbn = matcher.group();
                        return isbn;
                    } else {
                        matcher = BookConstant.REG_ISBN8.matcher(chapter);
                        if (matcher.find()) {
                            isbn = matcher.group();
                            return isbn;
                        }
                    }
                }
            }
        }
        return null;
    }

    //从元数据中获取
    private static String getIsbn4Meta(Book book) {
        if (book != null) {
            List<Identifier> ids = book.getMetadata().getIdentifiers();
            if (ids != null && ids.size() > 0) {
                String isbn;
                String scheme;
                for (Identifier id : ids) {
                    scheme = id.getScheme();
                    isbn = id.getValue();
                    if (scheme.toUpperCase().contains(SPLIT_VALUE)) {
                        return isbn;
                    }
                }
            }
        }
        return null;
    }

    //递归获取文件
    private static void func(File file, Map<String, List> fileMap) {
        File[] fs = file.listFiles();
        if (fs != null && fs.length > 0) {
            for (File f : fs) {
                //若是目录，则递归该目录下的文件
                if (f.isDirectory()) {
                    func(f, fileMap);
                }
                //若是文件，则进行保存
                if (f.isFile()) {
                    String suffix = f.getName().substring(f.getName().lastIndexOf(".") + 1);
                    if (suffix.toLowerCase().equals("xml")) {
                        fileMap.get(XML_KEY).add(f.getPath());
                    } else if (suffix.toLowerCase().equals("cebx")) {
                        fileMap.get(CEBX_KEY).add(f.getPath());
                    } else {
                        fileMap.get(FILE_KEY).add(f.getPath());
                    }
                }
            }
        }
    }

    //获取epub图书
    private static Book getBook(String path) {
        if (path != null && path.length() > 0) {
            try {
                String suffix = path.substring(path.lastIndexOf(".") + 1);
                if (suffix.toLowerCase().equals(EPUB_SUFFIX)) {
                    log.info("扫描文件{}开始", path);
                    //EpubReader epubReader = new EpubReader();
                    EpubReader2 epubReader = new EpubReader2();
                    InputStream inputStr = new FileInputStream(path);
                    File file = new File(path);
                    if (file != null && file.length() == 0) {
                        log.error("文件:{}，无内容", path);
                        return null;
                    }
                    Book book = epubReader.readEpub(inputStr);
                    return book;
                    //验证epub文件的有效性
                    /*boolean res = checkEpub(inputStr);
                    if (res) {
                        Book book = epubReader.readEpub(inputStr);
                        return book;
                    } else {
                        log.error("文件{}存在错误！", path);
                    }*/
                }
            } catch (Exception e) {
                log.warn("文件{}存在错误！请检查文件", path);
            }
        }
        return null;
    }

    //从内容中获取isbn
    private static String getIsbn4Content(Book book) throws IOException {
        if (book != null) {
            String[] chapters;
            String chapter;
            List<Resource> contents = book.getContents();
            String isbn;
            if (contents != null && contents.size() > 0) {
                for (int j = 0; j < contents.size(); j++) {
                    chapter = new String(contents.get(j).getData(), CODE_VALUE);
                    chapters = chapter.split(SPLIT_VALUE);
                    if (chapters.length > 1) {
                        isbn = getIsbn13(chapters[1].replace(" ", ""));
                        if (StringUtils.isEmpty(isbn)) {
                            isbn = getIsbn10(chapters[1].replace(" ", ""));
                        }
                        return isbn;
                    }
                    //如果前8个content，仍未获取到isbn，则从末尾获取
                    if (j > 8) {
                        for (int k = contents.size() - 1; k > contents.size() - 4; k--) {
                            chapter = new String(contents.get(k).getData(), CODE_VALUE);
                            if (!org.apache.commons.lang.StringUtils.isEmpty(chapter)) {
                                chapters = chapter.split(SPLIT_VALUE);
                                if (chapters.length > 1) {
                                    isbn = getIsbn13(chapters[1].replace(" ", ""));
                                    if (StringUtils.isEmpty(isbn)) {
                                        isbn = getIsbn10(chapters[1].replace(" ", ""));
                                    }
                                    return isbn;
                                }
                            }
                        }
                    }
                    if (j > 9) {
                        break;
                    }
                }
            }
        }
        return null;
    }

    //从文件名获取isbn
    private static String getIsbn4FileName(String fileName) throws Exception {
        if (!StringUtils.isEmpty(fileName)) {
            String isbn = getIsbn13(fileName);
            if (StringUtils.isEmpty(isbn)) {
                isbn = getIsbn10(fileName);
            }
            return isbn;
        }
        return null;
    }

    //从内容中获取isbn
    private static String getIsbnForContent(Book book) throws IOException {
        if (book != null) {
            //String[] chapters;
            String chapter;
            List<Resource> contents = book.getContents();
            String isbn;
            if (contents != null && contents.size() > 0) {
                for (int j = 0; j < contents.size(); j++) {
                    chapter = new String(contents.get(j).getData(), CODE_VALUE);
                    isbn = getIsbn13(chapter);
                    if (StringUtils.isEmpty(isbn)) {
                        isbn = getIsbn10(chapter);
                        if (!StringUtils.isEmpty(isbn)) {
                            return isbn;
                        }
                    }
                    //如果前8个content，仍未获取到isbn，则从末尾获取
                    if (j > 8) {
                        for (int k = contents.size() - 1; k > contents.size() - 4; k--) {
                            chapter = new String(contents.get(k).getData(), CODE_VALUE);
                            if (!org.apache.commons.lang.StringUtils.isEmpty(chapter)) {
                                isbn = getIsbn13(chapter);
                                if (StringUtils.isEmpty(isbn)) {
                                    isbn = getIsbn10(chapter);
                                    if (!StringUtils.isEmpty(isbn)) {
                                        return isbn;
                                    }
                                }
                            }
                        }
                    }
                    if (j > 9) {
                        break;
                    }
                }
            }
        }
        return null;
    }

    //批量删除图书章节内容
    @Override
    public int deleteBookChapterBatch(String conMetaId) {
        if (!StringUtils.isEmpty(conMetaId)) {
            String[] metaIds = conMetaId.split("\r\n");
            if (metaIds != null && metaIds.length > 0) {
                int sum = 0;
                for (String metaId : metaIds) {
                    try {
                        if (!StringUtils.isEmpty(metaId)) {
                            //删除章节内容
                            bookChapterDao.deleteAllBookChapter(metaId);
                            //删除分组内容
                            bookShardDao.deleteAllBookShard(metaId);
                            //更新图书元数据
                            BookMeta bookMeta = new BookMeta();
                            bookMeta.setMetaId(metaId);
                            bookMeta.setChapterNum(0);
                            bookMeta.setStyleUrl("");
                            bookMeta.setContentNum(0);
                            bookMeta.setStreamCatalog("");
                            bookMeta.setHasFlow(0);
                            bookMeta.setIsOptimize(0);
                            bookMeta.setFlowSource("");
                            bookMeta.setUpdateTime(new Date());
                            bookMetaDao.updateBookMetaById(bookMeta);
                            //更新图书元数据，temp表
                            ApabiBookMetaDataTemp bookMetaDataTemp = new ApabiBookMetaDataTemp();
                            bookMetaDataTemp.setMetaId(metaId);
                            bookMetaDataTemp.setChapterNum(0);
                            bookMetaDataTemp.setStyleUrl("");
                            bookMetaDataTemp.setContentNum(0);
                            bookMetaDataTemp.setStreamCatalog("");
                            bookMetaDataTemp.setHasFlow(0);
                            bookMetaDataTemp.setIsOptimize(0);
                            bookMetaDataTemp.setFlowSource("");
                            bookMeta.setUpdateTime(new Date());
                            bookMetaDataTempDao.update(bookMetaDataTemp);
                            sum++;
                        }
                    } catch (Exception e) {
                        log.warn("图书：" + metaId + "内容删除异常{}", e.getMessage());
                    }
                }
                return sum;
            }
        }
        return 0;
    }

    //批量删除图书章节内容
    @Override
    @Async
    public void deleteBookChapterEmail(String conMetaId, String toEmail) {
        if (!StringUtils.isEmpty(conMetaId)) {
            String[] metaIds = conMetaId.split("\r\n|\n\r|\r|\n");
            if (metaIds != null && metaIds.length > 0) {
                List<EmailResult> emailResults = new ArrayList<>();
                //获取日期格式转换
                ThreadLocal<DateFormat> threadLocal = new ThreadLocal<>();
                DateFormat df = threadLocal.get();
                if (df == null) {
                    df = new SimpleDateFormat("yyyyMMddHHmmss");
                    threadLocal.set(df);
                }
                for (String metaId : metaIds) {
                    EmailResult emailResult = new EmailResult();
                    emailResult.setId(metaId);
                    try {
                        if (!StringUtils.isEmpty(metaId)) {
                            //删除章节内容
                            bookChapterDao.deleteAllBookChapter(metaId);
                            //删除分组内容
                            bookShardDao.deleteAllBookShard(metaId);
                            //更新图书元数据
                            BookMeta bookMeta = new BookMeta();
                            bookMeta.setMetaId(metaId);
                            bookMeta.setChapterNum(0);
                            bookMeta.setStyleUrl("");
                            bookMeta.setContentNum(0);
                            bookMeta.setStreamCatalog("");
                            bookMeta.setHasFlow(0);
                            bookMeta.setIsOptimize(0);
                            bookMeta.setFlowSource("");
                            bookMetaDao.updateBookMetaById(bookMeta);
                            //更新图书元数据，temp表
                            ApabiBookMetaDataTemp bookMetaDataTemp = new ApabiBookMetaDataTemp();
                            bookMetaDataTemp.setMetaId(metaId);
                            bookMetaDataTemp.setChapterNum(0);
                            bookMetaDataTemp.setStyleUrl("");
                            bookMetaDataTemp.setContentNum(0);
                            bookMetaDataTemp.setStreamCatalog("");
                            bookMetaDataTemp.setHasFlow(0);
                            bookMetaDataTemp.setIsOptimize(0);
                            bookMetaDataTemp.setFlowSource("");
                            bookMetaDataTempDao.update(bookMetaDataTemp);
                            emailResult.setMessage("成功");
                            log.info("删除图书{}章节内容成功", metaId);
                        }
                    } catch (Exception e) {
                        emailResult.setMessage("失败");
                        log.warn("图书：" + metaId + "内容删除异常{}", e.getMessage());
                    }
                    emailResults.add(emailResult);
                }
                //生成检查结果
                String resultPath = config.getEmail() + File.separator + df.format(new Date()) + "deleteChapter.xlsx";
                BookUtil.exportExcelEmail(emailResults, resultPath);
                log.info("批量删除流式内容结果已生成到{}", config.getEmail());
                //表格路径
                List<String> results = new ArrayList<>();
                results.add(resultPath);
                //将发送结果发送邮件
                EMailUtil eMailUtil = new EMailUtil(systemConfMapper);
                eMailUtil.createSender();
                eMailUtil.sendAttachmentsMail(results, "批量删除流式内容结果", toEmail);
                log.info("批量删除流式内容结果已发送邮件");
            }
        }
    }

    //批量获取图书元数据
    @Override
    public int bookMetaBatch(String conMetaId) {
        if (!StringUtils.isEmpty(conMetaId)) {
            String[] metaIds = conMetaId.split("\r\n");
            if (metaIds != null && metaIds.length > 0) {
                int sum = 0;
                for (String metaId : metaIds) {
                    try {
                        if (!StringUtils.isEmpty(metaId)) {
                            BookMeta bookMeta = bookMetaDao.findBookMetaById(metaId);
                            //如果磐石没有，则从书苑获取
                            if (bookMeta == null) {
                                SCmfMeta sCmfMeta = sCmfMetaDao.findSCmfBookMetaById(metaId);
                                bookMeta = BookUtil.createBookMeta(sCmfMeta);
                                //新增到磐石数据库
                                bookMeta.setHasCebx(1);
                                bookMetaDao.insertBookMeta(bookMeta);
                                ApabiBookMetaDataTemp bookMetaDataTemp = BookUtil.createBookMetaTemp(sCmfMeta);
                                bookMetaDataTemp.setHasCebx(1);
                                bookMetaDataTempDao.insert(bookMetaDataTemp);
                                //获取书苑数据，更新到流式图书
                                boolean ress = insertShuyuanData(sCmfMeta);
                                if (ress) {
                                    log.info("{\"status\":\"{}\",\"metaId\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                                            0, metaId, "success", new Date());
                                } else {
                                    log.debug("{\"status\":\"{}\",\"metaId\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                                            -2, metaId, "新增书苑数据异常", new Date());
                                }
                                sum++;
                            }
                        }
                    } catch (Exception e) {
                        log.warn("{\"status\":\"{}\",\"metaId\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                                -1, metaId, e.getMessage(), new Date());
                    }
                }
                return sum;
            }
        }
        return 0;
    }

    //批量从书苑获取图书元数据
    @Override
    @Async
    public void bookMetaBatchEmail(String conMetaId, String toEmail) {
        if (!StringUtils.isEmpty(conMetaId)) {
            String[] metaIds = conMetaId.split("\r\n");
            if (metaIds != null && metaIds.length > 0) {
                List<EmailResult> emailResults = new ArrayList<>();
                //获取日期格式转换
                ThreadLocal<DateFormat> threadLocal = new ThreadLocal<>();
                DateFormat df = threadLocal.get();
                if (df == null) {
                    df = new SimpleDateFormat("yyyyMMddHHmmss");
                    threadLocal.set(df);
                }
                for (String metaId : metaIds) {
                    EmailResult emailResult = new EmailResult();
                    emailResult.setId(metaId);
                    try {
                        if (!StringUtils.isEmpty(metaId)) {
                            BookMeta bookMeta = bookMetaDao.findBookMetaById(metaId);
                            //如果磐石没有，则从书苑获取
                            if (bookMeta == null) {
                                SCmfMeta sCmfMeta = sCmfMetaDao.findSCmfBookMetaById(metaId);
                                bookMeta = BookUtil.createBookMeta(sCmfMeta);
                                //新增到磐石数据库
                                bookMeta.setHasCebx(1);
                                bookMetaDao.insertBookMeta(bookMeta);
                                ApabiBookMetaDataTemp bookMetaDataTemp = BookUtil.createBookMetaTemp(sCmfMeta);
                                bookMetaDataTemp.setHasCebx(1);
                                bookMetaDataTempDao.insert(bookMetaDataTemp);
                                //获取书苑数据，更新到流式图书
                                boolean ress = insertShuyuanData(sCmfMeta);
                                if (ress) {
                                    emailResult.setMessage("成功");
                                    log.info("{\"status\":\"{}\",\"metaId\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                                            0, metaId, "success", new Date());
                                    log.info("开始获取图书{}的目录和页码");
                                    getPageAndCata(bookMeta.getMetaId());
                                } else {
                                    emailResult.setMessage("失败");
                                    log.debug("{\"status\":\"{}\",\"metaId\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                                            -2, metaId, "新增书苑数据异常", new Date());
                                }
                            } else {
                                emailResult.setMessage("磐石已存在");
                            }
                        }
                    } catch (Exception e) {
                        emailResult.setMessage("失败");
                        log.warn("{\"status\":\"{}\",\"metaId\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                                -1, metaId, e.getMessage(), new Date());
                    }
                    emailResults.add(emailResult);
                }
                //生成检查结果
                String resultPath = config.getEmail() + File.separator + df.format(new Date()) + "getShuyuan.xlsx";
                BookUtil.exportExcelEmail(emailResults, resultPath);
                log.info("获取书苑数据结果已生成到{}", config.getEmail());
                //表格路径
                List<String> results = new ArrayList<>();
                results.add(resultPath);
                //将发送结果发送邮件
                EMailUtil eMailUtil = new EMailUtil(systemConfMapper);
                eMailUtil.createSender();
                eMailUtil.sendAttachmentsMail(results, "获取书苑数据结果", toEmail);
                log.info("获取书苑数据结果已发送邮件");
            }
        }
    }

    //根据drid，批量获取图书元数据
    @Override
    @Async
    public void getMetaByDridEmail(String drids, String toEmail) {
        if (!StringUtils.isEmpty(drids) && !StringUtils.isEmpty(toEmail)) {
            String[] dridStr = drids.split("\r\n");
            if (dridStr != null && dridStr.length > 0) {
                List<EmailResult> emailResults = new ArrayList<>();
                //获取日期格式转换
                ThreadLocal<DateFormat> threadLocal = new ThreadLocal<>();
                DateFormat df = threadLocal.get();
                if (df == null) {
                    df = new SimpleDateFormat("yyyyMMddHHmmss");
                    threadLocal.set(df);
                }
                for (String drid : dridStr) {
                    EmailResult emailResult = new EmailResult();
                    emailResult.setId(drid);
                    try {
                        if (!StringUtils.isEmpty(drid)) {
                            List<String> metaIds = bookMetaDao.findMetaIdByDrid(Integer.valueOf(drid));
                            //如果磐石没有，则从书苑获取
                            if (metaIds == null) {
                                SCmfMeta sCmfMeta = sCmfMetaDao.findSCmfBookMetaByDrid(Integer.valueOf(drid));
                                BookMeta bookMeta = BookUtil.createBookMeta(sCmfMeta);
                                //新增到磐石数据库
                                bookMeta.setHasCebx(1);
                                bookMetaDao.insertBookMeta(bookMeta);
                                ApabiBookMetaDataTemp bookMetaDataTemp = BookUtil.createBookMetaTemp(sCmfMeta);
                                bookMetaDataTemp.setHasCebx(1);
                                bookMetaDataTempDao.insert(bookMetaDataTemp);
                                //获取书苑数据，更新到流式图书
                                boolean ress = insertShuyuanData(sCmfMeta);
                                if (ress) {
                                    emailResult.setMessage("成功");
                                    log.info("{\"status\":\"{}\",\"drid\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                                            0, drid, "success", new Date());
                                    log.info("开始获取图书{}的目录和页码");
                                    getPageAndCata(bookMeta.getMetaId());
                                } else {
                                    emailResult.setMessage("失败");
                                    log.debug("{\"status\":\"{}\",\"drid\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                                            -2, drid, "新增书苑数据异常", new Date());
                                }
                            } else {
                                emailResult.setMessage("磐石已存在");
                            }
                        }
                    } catch (Exception e) {
                        emailResult.setMessage("失败");
                        log.warn("{\"status\":\"{}\",\"drid\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                                -1, drid, e.getMessage(), new Date());
                    }
                    emailResults.add(emailResult);
                }
                //生成检查结果
                String resultPath = config.getEmail() + File.separator + df.format(new Date()) + "shuyuanByDrid.xlsx";
                BookUtil.exportExcelEmail(emailResults, resultPath);
                log.info("获取书苑数据结果已生成到{}", config.getEmail());
                //表格路径
                List<String> results = new ArrayList<>();
                results.add(resultPath);
                //将发送结果发送邮件
                EMailUtil eMailUtil = new EMailUtil(systemConfMapper);
                eMailUtil.createSender();
                eMailUtil.sendAttachmentsMail(results, "获取书苑数据结果", toEmail);
                log.info("获取书苑数据结果已发送邮件");
            }
        }
    }

    //异步获取书苑数据的页码和目录
    @Async
    public void getPageAndCata(String metaId) {
        if (!StringUtils.isEmpty(metaId)) {
            try {
                BookMeta bookMeta = new BookMeta();
                bookMeta.setMetaId(metaId);
                String cebxPage = getCebxData(getCebxPage + bookMeta.getMetaId());
                String cata = getCebxData(getCataLog + bookMeta.getMetaId());
                bookMeta.setCebxPage(cebxPage);
                bookMeta.setFoamatCatalog(cata);
                bookMeta.setUpdateTime(new Date());
                bookMetaDao.updateBookMetaById(bookMeta);
                //更新到temp表
                ApabiBookMetaDataTemp temp = new ApabiBookMetaDataTemp();
                temp.setMetaId(bookMeta.getMetaId());
                temp.setCebxPage(bookMeta.getCebxPage());
                temp.setFoamatCatalog(bookMeta.getFoamatCatalog());
                temp.setUpdateTime(new Date());
                bookMetaDataTempDao.update(temp);
                log.info("获取图书{}页码和目录成功", metaId);
            } catch (Exception e) {
                log.info("获取图书{}页码和目录异常", metaId);
            }
        }
    }

    //根据drid，从书苑获取页码和目录
    @Override
    @Async
    public void getPageAndCata(Integer dridMin, Integer dridMax, String toEmail) {
        if (dridMin > 0 && !StringUtils.isEmpty(toEmail)) {
            //如果没有输入最大值，则从数据库中获取
            int maxDrid;
            if (dridMax != null) {
                maxDrid = dridMax;
                if (dridMin > dridMax) {
                    maxDrid = bookMetaDao.getMaxDrid();
                }
            } else {
                maxDrid = bookMetaDao.getMaxDrid();
            }
            //获取日期格式转换
            ThreadLocal<DateFormat> threadLocal = new ThreadLocal<>();
            DateFormat df = threadLocal.get();
            if (df == null) {
                df = new SimpleDateFormat("yyyyMMddHHmmss");
                threadLocal.set(df);
            }
            //用于邮件发送
            List<EmailResult> emailResults = new ArrayList<>();
            while (dridMin < maxDrid + 1) {
                EmailResult emailResult = new EmailResult();
                emailResult.setId(String.valueOf(dridMin));
                try {
                    List<String> metaIds = bookMetaDao.findMetaIdByDrid(dridMin);
                    if (metaIds != null && metaIds.size() > 0) {
                        for (String metaId : metaIds) {
                            long start = System.currentTimeMillis();
                            BookMeta bookMeta = bookMetaDao.findBookMetaById(metaId);
                            //补充页码和目录
                            if (bookMeta != null) {
                                boolean flag = false;
                                if (StringUtils.isEmpty(bookMeta.getCebxPage())) {
                                    String cebxPage = getCebxData(getCebxPage + bookMeta.getMetaId());
                                    bookMeta.setCebxPage(cebxPage);
                                    flag = true;
                                }
                                if (StringUtils.isEmpty(bookMeta.getFoamatCatalog())) {
                                    String cata = getCebxData(getCataLog + bookMeta.getMetaId());
                                    bookMeta.setFoamatCatalog(cata);
                                    flag = true;
                                }
                                if (flag) {
                                    bookMeta.setUpdateTime(new Date());
                                    bookMetaDao.updateBookMetaById(bookMeta);
                                    //temp表补充页码和目录
                                    ApabiBookMetaDataTemp temp = new ApabiBookMetaDataTemp();
                                    temp.setMetaId(bookMeta.getMetaId());
                                    temp.setCebxPage(bookMeta.getCebxPage());
                                    temp.setFoamatCatalog(bookMeta.getFoamatCatalog());
                                    temp.setUpdateTime(new Date());
                                    bookMetaDataTempDao.update(temp);
                                    emailResult.setMessage("成功");
                                    long end = System.currentTimeMillis();
                                    log.info("获取图书{}的页码和目录，耗时{}毫秒", metaId, (end - start));
                                } else {
                                    emailResult.setMessage("目录和页码已存在");
                                }
                            }
                        }
                    } else {
                        emailResult.setMessage("数据库中不存在");
                    }
                } catch (Exception e) {
                    emailResult.setMessage("失败");
                    log.warn("获取图书{}的页码和目录时，出现异常{}", dridMin, e.getMessage());
                }
                dridMin++;
                emailResults.add(emailResult);
            }
            //生成检查结果
            String resultPath = config.getEmail() + File.separator + df.format(new Date()) + "pageAndCata.xlsx";
            BookUtil.exportExcelEmail(emailResults, resultPath);
            log.info("从书苑获取目录和页码已结束{}", config.getEmail());
            //表格路径
            List<String> results = new ArrayList<>();
            results.add(resultPath);
            //将发送结果发送邮件
            EMailUtil eMailUtil = new EMailUtil(systemConfMapper);
            eMailUtil.createSender();
            eMailUtil.sendAttachmentsMail(results, "从书苑获取目录和页码已结束", toEmail);
            log.info("从书苑获取目录和页码已结束");
        }
    }

    @Override
    public List<BookMetaFromExcel> importBookMetaFromExcel(Map<Integer, Map<Object, Object>> data) {
        List<BookMetaFromExcel> list = new ArrayList<>();
        for (Map.Entry<Integer, Map<Object, Object>> entry : data.entrySet()) {
            BookMeta bookMetaTemp = getBookMetaFromDate(entry);
            //根据isbn
            List<BookMeta> bookMetas = bookMetaDao.listBookMetaByIsbn(bookMetaTemp.getIsbn());
            if (bookMetas == null || bookMetas.isEmpty()) {
                //根据isbn13
                bookMetas = bookMetaDao.listBookMetaByIsbn13(bookMetaTemp.getIsbn13());
                if (bookMetas == null || bookMetas.isEmpty()) {
                    list.add(new BookMetaFromExcel(null, bookMetaTemp, 0));
                } else {
                    bookMetas.forEach(bm -> list.add(new BookMetaFromExcel(bm, bookMetaTemp, 1)));
                }
            } else {
                bookMetas.forEach(bm -> list.add(new BookMetaFromExcel(bm, bookMetaTemp, 1)));

            }
        }
        return list;
    }

    private BookMeta getBookMetaFromDate(Map.Entry<Integer, Map<Object, Object>> entry) {
        String isbn = (String) entry.getValue().get("ISBN");
        String title = (String) entry.getValue().get("书名");
        String subTitle = (String) entry.getValue().get("副标题");
        String creator = (String) entry.getValue().get("作者");
        String creatorWord = (String) entry.getValue().get("creatorWord");
        String isbn10 = (String) entry.getValue().get("ISBN10");
        String isbn13 = (String) entry.getValue().get("ISBN13");
        String publisher = (String) entry.getValue().get("出版社");
        String issuedDate = (String) entry.getValue().get("出版日期");
        String relation = (String) entry.getValue().get("丛书relation");
        String editionOrder = (String) entry.getValue().get("版次");
        String classCode = (String) entry.getValue().get("中图法分类");
        String place = (String) entry.getValue().get("出版地");
        String translator = (String) entry.getValue().get("翻译");
        String originTitle = (String) entry.getValue().get("原书名originTitle");
        String creatorid = (String) entry.getValue().get("阿帕比作者id");
        String language = (String) entry.getValue().get("语种");
        String preface = (String) entry.getValue().get("序言");
        String paperPrice = (String) entry.getValue().get("纸书价格");
        String ebookPrice = (String) entry.getValue().get("电子书价格");
        String doubanId = (String) entry.getValue().get("doubanId");
        String amazonId = (String) entry.getValue().get("amazonId");
        String nlibraryId = (String) entry.getValue().get("nlibraryId");

        String metaId = "";
        if (org.apache.commons.lang3.StringUtils.isNotBlank(issuedDate)) {
            issuedDate = StringToolUtil.issuedDateFormat(issuedDate);
            if (issuedDate.contains(" 00:00:00")) {
                // 获取清洗后的issuedDate的值
                issuedDate = issuedDate.replace(" 00:00:00", "");
            }
            metaId = StringToolUtil.metaidFormat(issuedDate);
        }
        BookMeta bookMeta = new BookMeta();
        bookMeta.setMetaId(metaId);
        bookMeta.setIsbn(isbn);
        bookMeta.setTitle(title);
        bookMeta.setSubTitle(subTitle);
        bookMeta.setCreator(creator);
        bookMeta.setCreatorWord(creatorWord);
        bookMeta.setIsbn10(isbn10);
        bookMeta.setIsbn13(isbn13);
        bookMeta.setPublisher(publisher);
        bookMeta.setIssuedDate(issuedDate);
        bookMeta.setRelation(relation);
        bookMeta.setEditionOrder(editionOrder);
        bookMeta.setClassCode(classCode);
        bookMeta.setPlace(place);
        bookMeta.setTranslator(translator);
        bookMeta.setOriginTitle(originTitle);
        bookMeta.setCreatorId(creatorid);
        bookMeta.setLanguage(language);
        bookMeta.setPreface(preface);
        bookMeta.setPaperPrice(paperPrice);
        bookMeta.setEbookPrice(ebookPrice);
        bookMeta.setDoubanId(doubanId);
        bookMeta.setAmazonId(amazonId);
        bookMeta.setNlibraryId(nlibraryId);
        return bookMeta;
    }

    //调用接口获取数据
    private String getCebxData(String url) {
        if (!StringUtils.isEmpty(url)) {
            try {
                HttpEntity httpEntity = HttpUtils.doGetEntity(url);
                String body = EntityUtils.toString(httpEntity);
                JSONObject jsonObject = JSONObject.fromObject(body);
                return jsonObject.get("body").toString();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    //获取书苑数据cmf_meta、cmf_digitobject、cmf_digitresfile_site，更新到流式图书
    private boolean insertShuyuanData(SCmfMeta sCmfMeta) throws Exception {
        if (sCmfMeta != null) {
            //cmf_meta表新增
            CmfMeta cmfMeta = BookUtil.createCmfMeta(sCmfMeta);
            if (cmfMeta != null) {
                cmfMetaDao.insertCmfMeta(cmfMeta);
            } else {
                log.warn("{\"status\":\"{}\",\"metaId\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                        -1, sCmfMeta.getIdentifier(), "生成CmfMeta失败", new Date());
            }
            List<SCmfDigitObject> sCmfDigitObjects =
                    sCmfDigitObjectDao.findSCmfDigitObjectByDrid(sCmfMeta.getDrid());
            if (sCmfDigitObjects != null && sCmfDigitObjects.size() > 0) {
                for (SCmfDigitObject sCmfDigitObject : sCmfDigitObjects) {
                    //cmf_digitobject表新增
                    CmfDigitObject cmfDigitObject = BookUtil.createCmfDigitObject(sCmfDigitObject);
                    if (cmfDigitObject != null) {
                        cmfDigitObjectDao.insertCmfDigitObject(cmfDigitObject);
                        List<SCmfDigitResfileSite> sCmfDigitResfileSites =
                                sCmfDigitResfileSiteDao.findSCmfDigitResfileSiteByFileId(sCmfDigitObject.getFileId());
                        if (sCmfDigitResfileSites != null && sCmfDigitResfileSites.size() > 0) {
                            for (SCmfDigitResfileSite sCmfDigitResfileSite : sCmfDigitResfileSites) {
                                //cmf_digitresfile_site表新增
                                CmfDigitResfileSite cmfDigitResfileSite = BookUtil.createCmfDigitResfileSite(sCmfDigitResfileSite);
                                if (cmfDigitResfileSite != null) {
                                    cmfDigitResfileSiteDao.insertCmfDigitResfileSite(cmfDigitResfileSite);
                                } else {
                                    log.warn("{\"status\":\"{}\",\"metaId\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                                            -1, sCmfMeta.getIdentifier(), "生成CmfDigitResfileSite失败", new Date());
                                }
                            }
                        } else {
                            log.warn("{\"status\":\"{}\",\"metaId\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                                    -1, sCmfMeta.getIdentifier(), "获取SCmfDigitResfileSite失败", new Date());
                        }
                    } else {
                        log.warn("{\"status\":\"{}\",\"metaId\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                                -1, sCmfMeta.getIdentifier(), "生成CmfDigitObject失败", new Date());
                    }
                }
            } else {
                log.warn("{\"status\":\"{}\",\"metaId\":\"{}\",\"message\":\"{}\",\"time\":\"{}\"}",
                        -1, sCmfMeta.getIdentifier(), "获取SCmfDigitObject失败", new Date());
            }
            return true;

        }
        return false;
    }
}
