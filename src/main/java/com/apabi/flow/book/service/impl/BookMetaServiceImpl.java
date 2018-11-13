package com.apabi.flow.book.service.impl;

import com.apabi.flow.book.dao.*;
import com.apabi.flow.book.entity.MongoBookChapter;
import com.apabi.flow.book.entity.MongoBookMeta;
import com.apabi.flow.book.entity.MongoBookShard;
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
import com.apabi.flow.douban.dao.ApabiBookMetaTempRepository;
import com.apabi.flow.douban.model.ApabiBookMetaDataTemp;
import com.apabi.flow.publish.dao.ApabiBookMetaTempPublishRepository;
import com.apabi.flow.publish.model.ApabiBookMetaTempPublish;
import com.apabi.shuyuan.book.dao.SCmfMetaDao;
import com.github.pagehelper.Page;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.Identifier;
import nl.siegmann.epublib.domain.Resource;
import nl.siegmann.epublib.epub.EpubReader;
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
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.net.SocketTimeoutException;
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

    private static List<String> FILES = new ArrayList<>();

    private static List<String> XML_FILES = new ArrayList<>();

    private static List<String> CEBX_FILES = new ArrayList<>();

    private static final String EPUB_SUFFIX = "epub";

    private static final String SPLIT_VALUE = "ISBN";

    private static final String CODE_VALUE = "UTF-8";

    private final String EXCEL_XLS = "xls";
    private final String EXCEL_XLSX = "xlsx";

    @Autowired
    BookMetaDao bookMetaDao;

    @Autowired
    BookChapterDao bookChapterDao;

    @Autowired
    BookShardDao bookShardDao;

    @Autowired
    ApabiBookMetaTempRepository bookMetaTempRepository;

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
    SCmfMetaDao SCmfMetaDao;

    @Autowired
    ApabiBookMetaDataTempDao bookMetaDataTempDao;

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
    /*@Override
    public Page<BookMetaVo> queryPage(Map queryMap, int pageNumber, int pageSize) {
        //查询字段
        String tmp;
        //获取图书id
        if (queryMap.get("metaid") != null) {
            tmp = queryMap.get("metaid").toString();
        } else {
            tmp = "";
        }
        final String metaid = tmp;
        //获取书名
        if (queryMap.get("title") != null) {
            tmp = queryMap.get("title").toString();
        } else {
            tmp = "";
        }
        final String title = tmp;
        //获取作者
        if (queryMap.get("creator") != null) {
            tmp = queryMap.get("creator").toString();
        } else {
            tmp = "";
        }
        final String creator = tmp;
        //获取出版社
        if (queryMap.get("publisher") != null) {
            tmp = queryMap.get("publisher").toString();
        } else {
            tmp = "";
        }
        final String publisher = tmp;
        //获取isbn值
        if (queryMap.get("isbnVal") != null) {
            tmp = queryMap.get("isbnVal").toString();
        } else {
            tmp = "";
        }
        final String isbnVal = tmp;
        //获取isbn类型
        if (queryMap.get("isbn") != null) {
            tmp = queryMap.get("isbn").toString();
        } else {
            tmp = "";
        }
        final String isbn = tmp;
        //构造分页
        Sort sort = new Sort(Sort.Direction.DESC, "metaid");
        Pageable pageable = PageRequest.of(pageNumber - 1, pageSize, sort);
        Specification<BookMetaVo> spec = (Specification<BookMetaVo>) (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!StringUtils.isEmpty(metaid)) {
                predicates.add(criteriaBuilder.equal(root.get("metaid"), metaid));
            }
            if (!StringUtils.isEmpty(title)) {
                predicates.add(criteriaBuilder.like(root.get("title"), "%" + title + "%"));
            }
            if (!StringUtils.isEmpty(creator)) {
                predicates.add(criteriaBuilder.like(root.get("creator"), "%" + creator + "%"));
            }
            if (!StringUtils.isEmpty(publisher)) {
                predicates.add(criteriaBuilder.like(root.get("publisher"), "%" + publisher + "%"));
            }
            if (isbn.equals("isbn")) {
                if (!StringUtils.isEmpty(isbnVal)) {
                    predicates.add(criteriaBuilder.equal(root.get("isbn"), isbnVal));
                }
            } else if (isbn.equals("isbn10")) {
                if (!StringUtils.isEmpty(isbnVal)) {
                    predicates.add(criteriaBuilder.equal(root.get("isbn10"), isbnVal));
                }
            } else if (isbn.equals("isbn13")) {
                if (!StringUtils.isEmpty(isbnVal)) {
                    predicates.add(criteriaBuilder.equal(root.get("isbn13"), isbnVal));
                }
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<BookMetaVo> pages = bookMetaVoRepository.findAll(spec, pageable);
        return pages;
    }*/

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
                //清空list
                XML_FILES.clear();
                CEBX_FILES.clear();
                FILES.clear();
                func(dir);
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
                                //获取文件名
                                fileName = newFile.getName();
                                if (!org.apache.commons.lang.StringUtils.isEmpty(fileName)) {
                                    //如果文件名符合"m."规则，则从数据库中查询
                                    if (fileName.length() > 1 && fileName.substring(0, 2).equals("m.")) {
                                        metaBatches = bookMetaDao.findBookMetaBatchById(fileName);
                                    } else {
                                        //使用插件获取isbn
                                        isbnMeta = getIsbn4Meta(book);
                                        //获取文件中的isbn
                                        isbn = getIsbn4Content(book);
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
                                        }
                                    }
                                }
                                if (metaBatches.size() > 0) {
                                    for (BookMetaBatch bookMetaBatch : metaBatches) {
                                        //文件名
                                        bookMetaBatch.setFileName(fileName);
                                        //从文件中获取的isbn
                                        if (StringUtils.isEmpty(isbn)) {
                                            bookMetaBatch.setFileIsbn(isbnMeta);
                                        } else {
                                            bookMetaBatch.setFileIsbn(isbn);
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
                                    //文件isbn
                                    if (StringUtils.isEmpty(isbn)) {
                                        bookMetaBatch.setFileIsbn(isbnMeta);
                                    } else {
                                        bookMetaBatch.setFileIsbn(isbn);
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
                //清空list
                XML_FILES.clear();
                CEBX_FILES.clear();
                FILES.clear();
                func(dir);
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
                            } else {
                                BookMetaBatch bookMetaBatch = new BookMetaBatch();
                                bookMetaBatch.setFileName(newFile.getName().replace(".xml", ".cebx"));
                                metaBatches.add(bookMetaBatch);
                            }
                        } catch (Exception e) {
                            log.warn("{\"status\":\"{}\",\"file\":\"{}\",\"message\":\"{}\"}", -1, newFile.getName(), e.getMessage());
                        }
                        bookMetaBatches.addAll(metaBatches);
                        metaBatches.clear();
                    }
                } else {
                    //扫描cebx文件，通过文件名从书苑获取metaId
                    for (String cebxFile : CEBX_FILES) {
                        File newFile = new File(cebxFile);
                        String metaId = SCmfMetaDao.getMetaIdByFileName(newFile.getName().replace("cebx", "ceb"));
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
                        } else {
                            BookMetaBatch bookMetaBatch = new BookMetaBatch();
                            bookMetaBatch.setFileName(newFile.getName());
                            metaBatches.add(bookMetaBatch);
                        }
                    }
                    bookMetaBatches.addAll(metaBatches);
                    metaBatches.clear();
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

    //递归获取文件路径
    private static void func(File file) {
        File[] fs = file.listFiles();
        if (fs != null && fs.length > 0) {
            for (File f : fs) {
                //若是目录，则递归该目录下的文件
                if (f.isDirectory()) {
                    func(f);
                }
                //若是文件，则进行保存
                if (f.isFile()) {
                    String suffix = f.getName().substring(f.getName().lastIndexOf(".") + 1);
                    if (suffix.toLowerCase().equals("xml")) {
                        XML_FILES.add(f.getPath());
                    } else if (suffix.toLowerCase().equals("cebx")) {
                        CEBX_FILES.add(f.getPath());
                    } else {
                        FILES.add(f.getPath());
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
                    EpubReader epubReader = new EpubReader();
                    InputStream inputStr = new FileInputStream(path);
                    Book book = epubReader.readEpub(inputStr);
                    return book;
                }
            } catch (Exception e) {
                log.warn("请检查文件" + path);
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
                        sum++;
                    } catch (Exception e) {
                        log.warn("图书：" + metaId + "内容删除异常{}", e.getMessage());
                    }
                }
                return sum;
            }
        }
        return 0;
    }
}
