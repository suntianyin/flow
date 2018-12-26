package com.apabi.flow.book.util;

import com.apabi.flow.book.dao.BookFileDao;
import com.apabi.flow.book.epublib.EpubReader2;
import com.apabi.flow.book.model.*;
import com.apabi.flow.book.service.BookChapterService;
import com.apabi.flow.book.service.BookMetaService;
import com.apabi.flow.book.service.BookShardService;
import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.config.ApplicationConfig;
import com.apabi.flow.douban.dao.ApabiBookMetaTempRepository;
import net.sf.jazzlib.ZipEntry;
import net.sf.jazzlib.ZipException;
import net.sf.jazzlib.ZipInputStream;
import net.sf.json.JSONArray;
import nl.siegmann.epublib.domain.*;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.service.MediatypeService;
import nl.siegmann.epublib.util.ResourceUtil;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

/**
 * @author guanpp
 * @date 2018/8/1 9:37
 * @description
 */
@Service("GetEpubookChapter")
public class GetEpubookChapter {


    private static final Logger log = LoggerFactory.getLogger(GetEpubookChapter.class);

    //private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //章节编号
    //private static int chapterNum = 0;

    //目录和章节的对应关系
    //private Map<String, Integer> cataChapter = new Hashtable<>();

    //样式和图片文件的生成路径
    //private String styleUrl;

    @Autowired
    BookMetaService bookMetaService;

    @Autowired
    BookChapterService bookChapterService;

    @Autowired
    BookShardService bookShardService;

    @Autowired
    BookFileDao bookFileDao;

    @Autowired
    ApabiBookMetaTempRepository bookMetaTempRepository;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    ApplicationConfig config;

    //解析epub文件
    public EpubookMeta insertEpubook(String path, EpubookMeta epubookMeta, String fileName) throws IOException, ParseException {
        if (path != null && path.length() > 0) {
            //String fileName = epubookMeta.getFileName();
            ThreadLocal<DateFormat> threadLocal = new ThreadLocal<>();
            DateFormat df = threadLocal.get();
            if (df == null) {
                df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                threadLocal.set(df);
            }
            if (epubookMeta != null && fileName != null && fileName.length() > 0) {
                Book book = getEpubook(path);
                if (book != null) {
                    //获取meta数据
                    epubookMeta = getMetaData(epubookMeta, book, df);
                    //该书样式文件根目录
                    String styleUrl = epubookMeta.getTmp();
                    if (epubookMeta != null) {
                        //获取head标签中的样式引用路径
                        Document doc;
                        List<nl.siegmann.epublib.domain.Resource> contents = book.getContents();
                        if (contents != null && contents.size() > 0) {
                            String cssPath = "";
                            boolean flag = true;
                            int index = 0;
                            if (index < contents.size()) {
                                while (!((flag == false) && (index == 3))) {
                                    doc = Jsoup.parse(new String(contents.get(index).getData(), "utf-8"));
                                    Elements links = doc.head().select("link");
                                    if (links == null || links.size() == 0) {
                                        links = doc.body().select("link");
                                    }
                                    for (Element link : links) {
                                        String[] cssLink = link.attr("href").split("/");
                                        String cssName = cssLink[cssLink.length - 1];
                                        link.attr("href",
                                                BookConstant.BASE_URL + styleUrl + "/styles/" + cssName);
                                        if (!cssPath.contains(link.toString())) {
                                            cssPath += link.toString();
                                        }
                                    }
                                    if (!StringUtils.isBlank(cssPath)) {
                                        flag = false;
                                    }
                                    index++;
                                    if (index > contents.size() - 1) {
                                        break;
                                    }
                                }
                            }
                            epubookMeta.setStyleUrl(cssPath);

                            long start2 = System.currentTimeMillis();
                            //图书总字数
                            int wordSum = 0;
                            //获取章节内容，body标签中的所有子标签
                            //存储章节目录映射信息
                            //Map<String, Integer> map = new HashMap<>();
                            //按章节存放
                            List<BookChapter> chapterList = new ArrayList<>();
                            //对章节进行分组存放
                            List<BookShard> chapterShardList = new ArrayList<>();
                            Map<String, Integer> cataChapter = new Hashtable<>();
                            for (int i = 0; i < contents.size(); i++) {
                                BookChapter chapter = new BookChapter();
                                //图书号和章节号拼接id
                                chapter.setComId(epubookMeta.getMetaid() + i);
                                chapter.setChapterNum(i);
                                cataChapter.put(contents.get(i).getHref(), i);

                                doc = Jsoup.parse(new String(contents.get(i).getData(), "utf-8"));
                                //替换章节末尾引用文献序号
                                Element body = doc.body();
                                Elements elements = body.select("aside");
                                for (Element element : elements) {
                                    element.select("a").first().before(element.val() + "\u3000");
                                }
                                body.select("aside").remove();
                                body.select("section").append(elements.outerHtml());
                                //章节字数
                                int word = body.children().text().replaceAll("\\u3000|\\s*", "").length();
                                chapter.setWordSum(word);
                                wordSum += word;
                                //替换图书内容中的图片路径
                                String content = replaceImgeUrl(body.toString(), styleUrl);
                                chapter.setContent(content);
                                chapter.setBodyClass(body.attr("class"));
                                chapter.setCreateTime(df.parse(df.format(new Date())));
                                chapter.setUpdateTime(df.parse(df.format(new Date())));
                                //对章节内容进行分片
                                Elements children = body.children();
                                List<String> tags = new ArrayList<>();
                                String contentP = "";
                                for (int j = 0; j < children.size(); j++) {
                                    contentP += children.get(j).outerHtml();
                                    if ((j != 0 && j % BookConstant.shardSize == 0) || (j == children.size() - 1)) {
                                        tags.add(contentP);
                                        contentP = "";
                                    }
                                }
                                //添加分组数
                                chapter.setShardSum(tags.size());
                                chapterList.add(chapter);
                                //每组分片都新建一个EpubookChapter
                                for (int k = 0; k < tags.size(); k++) {
                                    BookShard chapterShard = new BookShard();
                                    chapterShard.setComId(epubookMeta.getMetaid() + i + "S" + k);
                                    chapterShard.setChapterNum(i);
                                    chapterShard.setIndex(k);
                                    chapterShard.setBodyClass(body.attr("class"));
                                    chapterShard.setCreateTime(df.parse(df.format(new Date())));
                                    chapterShard.setUpdateTime(df.parse(df.format(new Date())));
                                    Document shards = Jsoup.parse(tags.get(k), "utf-8");
                                    int wordS = shards.text().replaceAll("\\u3000|\\s*", "").length();
                                    chapterShard.setWordSum(wordS);
                                    //替换图书内容中的图片路径
                                    String contentS = replaceImgeUrl(tags.get(k), styleUrl);
                                    /*String contentS = tags.get(k);
                                    contentS = contentS.replaceAll("..(?i)/images/", BookConstant.BASE_URL + styleUrl + "/imgs/");*/
                                    chapterShard.setContent(contentS);
                                    chapterShardList.add(chapterShard);
                                }
                            }
                            epubookMeta.setContentNum(wordSum);
                            //获取目录信息，并添加章节号和章节字数
                            //List<BookCataRows> catalogList = getCataRowData(book, map);
                            BookCataRows cataTree = getCataRows(book, cataChapter);
                            if (cataTree != null && cataTree.getChildren().size() > 0) {
                                //目录结构树
                                JSONArray json = JSONArray.fromObject(cataTree.getChildren());
                                epubookMeta.setStreamCatalog(json.toString());
                            } else {
                                log.warn("图书《" + epubookMeta.getMetaid() + "》目录获取有误！");
                                return null;
                            }
                            long end2 = System.currentTimeMillis();
                            log.info("获取图书：" + epubookMeta.getMetaid() + "所有章节，耗时：" + (end2 - start2) + "毫秒");
                            //抽取文件
                            long start1 = System.currentTimeMillis();
                            //抽取css样式文件，并保存
                            saveCssFile(book, styleUrl);
                            //抽取图片文件，并保存
                            saveImageFile(book, epubookMeta, styleUrl);
                            //抽取字体文件，并保存
                            saveTtfFile(book, styleUrl);
                            long end1 = System.currentTimeMillis();
                            log.info("获取图书：" + epubookMeta.getMetaid() + "，样式文件及图片，耗时：" + (end1 - start1) + "毫秒");

                            epubookMeta.setHasflow(1);
                            epubookMeta.setIsoptimize(1);
                            epubookMeta.setFlowSource("epub");
                            int res = insertEpubookByChapterShard(epubookMeta,
                                    chapterList,
                                    chapterShardList);
                            if (res == 1) {
                                //保存图书和文件的对应关系
                                saveFile(epubookMeta.getMetaid(), fileName);
                                //更新metadata_tmp表
                                updateBookMetaTmp(epubookMeta);
                                return epubookMeta;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

    //替换图片路径
    private String replaceImgeUrl(String content, String styleUrl) {
        if (!StringUtils.isEmpty(content)) {
            Document doc = Jsoup.parse(content);
            Elements imgs = doc.body().select("img");
            if (imgs != null && imgs.size() == 0) {
                imgs = doc.body().select("image");
            }
            if (imgs != null && imgs.size() > 0) {
                for (Element img : imgs) {
                    String imgSrc = img.attr("src");
                    if (!StringUtils.isEmpty(imgSrc)) {
                        String[] imgPath = imgSrc.split("/");
                        String imgName = imgPath[imgPath.length - 1];
                        img.attr("src",
                                BookConstant.BASE_URL + styleUrl + "/imgs/" + imgName);
                    }
                    String imgHref = img.attr("xlink:href");
                    if (!StringUtils.isEmpty(imgHref)) {
                        String[] imgPath = imgHref.split("/");
                        String imgName = imgPath[imgPath.length - 1];
                        img.attr("xlink:href",
                                BookConstant.BASE_URL + styleUrl + "/imgs/" + imgName);
                    }
                }
            }
            return doc.body().children().toString();
        }
        return null;
    }

    //通过路径读取epub文件，并生成Book对象
    private Book getEpubook(String path) throws IOException {
        if (path != null && path.length() > 0) {
            String suffix = path.substring(path.lastIndexOf(".") + 1);
            if (suffix.toLowerCase().equals("epub")) {
                log.info("解析图书{}开始", path);
                //EpubReader epubReader = new EpubReader();
                EpubReader2 epubReader = new EpubReader2();
                InputStream inputStr = new FileInputStream(path);
                File file = new File(path);
                if (file != null && file.length() == 0) {
                    log.error("文件:{},无内容", path);
                    return null;
                }
                //Book book = epubReader.readEpub(inputStr);
                Book book = epubReader.readEpub(inputStr);
                return book;
                /*boolean res = checkEpub(inputStr);
                if (res) {
                    Book book = epubReader.readEpub(inputStr);
                    return book;
                } else {
                    log.error("文件{}存在错误！", path);
                }*/
            }
        }
        return null;
    }

    //获取图书标题、作者、出版社、出版日期、类型、语言等数据
    private EpubookMeta getMetaData(EpubookMeta epubookMeta, Book book, DateFormat df) throws ParseException {
        if (book != null) {
            //获取日期
            String date = epubookMeta.getIssueddate().substring(0, 10);
            //获取图书号
            String id = epubookMeta.getMetaid();
            epubookMeta.setChapterNum(book.getContents().size());
            epubookMeta.setUpdatetime(df.parse(df.format(new Date())));
            if (!StringUtils.isEmpty(id) && !StringUtils.isEmpty(date)) {
                epubookMeta.setTmp("/" + date.replace("-", "") + "/" + id);
                return epubookMeta;
            }
        }
        return null;
    }

    //获取目录
    private List<BookCataRows> getCataRowData(Book book, Map map) {
        if (book != null) {
            try {
                int catalogSum;
                int fileSum;
                //获取文件数
                List<nl.siegmann.epublib.domain.Resource> contents = book.getContents();
                fileSum = contents.size();
                //获取目录（判断获取几级目录）
                List<TOCReference> tocs = book.getTableOfContents().getTocReferences();
                List<BookCataRows> catalogList = BookUtil.getThirdCata(tocs);
                if (catalogList != null) {
                    catalogSum = catalogList.size();
                    if (catalogSum > fileSum) {
                        catalogList.clear();
                        catalogList = BookUtil.getSecondCata(tocs);
                        catalogSum = catalogList.size();
                        if (catalogSum > fileSum) {
                            catalogList.clear();
                            catalogList = BookUtil.getFirstCata(tocs);
                        }
                    }
                    //给目录添加章节号
                    for (BookCataRows cataRows : catalogList) {
                        cataRows.setChapterNum((Integer) map.get(cataRows.getUrl()));
                    }
                    return catalogList;
                }
            } catch (Exception e) {
                log.warn("图书" + book.getTitle() + e.getMessage());
                return null;
            }
        }
        return null;
    }

    //获取层次目录
    private BookCataRows getCataRows(Book book, Map<String, Integer> cataChapter) {
        if (book != null) {
            try {
                List<TOCReference> tocs = book.getTableOfContents().getTocReferences();
                BookCataRows root = new BookCataRows();
                //如果toc不存在，则从ncx文件中获取目录数据
                if (tocs != null && tocs.size() == 0) {
                    root = getCataRowsByNcx(book, cataChapter);
                    return root;
                }
                int startNum = 0;
                if (tocs != null && tocs.size() > 0) {
                    startNum = cataChapter.get(tocs.get(0).getResource().getHref());
                }
                if (startNum > 0) {
                    for (int i = 0; i < startNum; i++) {
                        BookCataRows cata = new BookCataRows();
                        cata.setChapterNum(i);
                        if (i == 0) {
                            cata.setChapterName("封面");
                        } else {
                            cata.setChapterName("序言" + i);
                        }
                        root.getChildren().add(cata);
                    }
                }
                for (TOCReference toc : tocs) {
                    createCataTree(toc, root, cataChapter);
                }
                return root;
            } catch (Exception e) {
                log.warn("图书" + book.getTitle() + e.getMessage());
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    //通过分析目录文件，获取层次目录
    private BookCataRows getCataRowsByNcx(Book book, Map<String, Integer> cataChapter) {
        if (book != null) {
            Resource ncx = book.getNcxResource();
            try {
                org.dom4j.Document doc = DocumentHelper.parseText(new String(ncx.getData(), "UTF-8"));
                List<org.dom4j.Element> lis = doc.getRootElement().element("body").element("nav").element("ol").elements("li");
                BookCataRows root = new BookCataRows();
                for (org.dom4j.Element element : lis) {
                    createCataLog(element, root, cataChapter);
                }
                return root;
            } catch (Exception e) {
                log.warn("获取图书{}的NCX文件数据时，发生异常{}", book.getTitle(), e.getMessage());
            }

        }
        return null;
    }

    //构建目录树
    private void createCataLog(org.dom4j.Element element, BookCataRows parentCata, Map<String, Integer> cataChapter) {
        if (element != null) {
            List<org.dom4j.Element> childEle = element.elements();
            if (childEle != null && childEle.size() > 1) {
                BookCataRows cataRows = new BookCataRows();
                cataRows.setChapterName(element.element("a").getTextTrim());
                String url = element.element("a").attributeValue("href");
                cataRows.setUrl(url);
                cataRows.setChapterNum(cataChapter.get(url));
                for (org.dom4j.Element child : childEle) {
                    createCataLog(child, cataRows, cataChapter);
                }
                parentCata.getChildren().add(cataRows);
            } else {
                BookCataRows bookCataRows = new BookCataRows();
                bookCataRows.setChapterName(element.element("a").getTextTrim());
                String url = element.element("a").attributeValue("href");
                bookCataRows.setUrl(url);
                bookCataRows.setChapterNum(cataChapter.get(url));
                parentCata.getChildren().add(bookCataRows);
            }
        }
    }

    //构建目录树
    private void createCataTree(TOCReference toc, BookCataRows parentCata, Map<String, Integer> cataChapter) {
        if (toc != null) {
            List<TOCReference> childTocs = toc.getChildren();
            if (childTocs != null && childTocs.size() > 0) {
                BookCataRows cataRows = new BookCataRows();
                cataRows.setChapterName(toc.getTitle());
                String url = toc.getResource().getHref();
                cataRows.setUrl(url);
                cataRows.setChapterNum(cataChapter.get(url));
                for (TOCReference child : childTocs) {
                    createCataTree(child, cataRows, cataChapter);
                }
                parentCata.getChildren().add(cataRows);
            } else {
                BookCataRows bookCataRows = new BookCataRows();
                bookCataRows.setChapterName(toc.getTitle());
                String url = toc.getResource().getHref();
                bookCataRows.setUrl(url);
                bookCataRows.setChapterNum(cataChapter.get(url));
                parentCata.getChildren().add(bookCataRows);
            }
        }
    }

    //获取css文件，并保存
    private void saveCssFile(Book book, String styleUrl) throws IOException {
        FileOutputStream fos = null;
        try {
            //ServletContext context = ContextLoader.getCurrentWebApplicationContext().getServletContext();
            MediaType[] mCss = {MediatypeService.CSS};
            List<nl.siegmann.epublib.domain.Resource> cssFiles = book.getResources().getResourcesByMediaTypes(mCss);
            if (cssFiles != null && cssFiles.size() > 0) {
                for (nl.siegmann.epublib.domain.Resource res : cssFiles) {
                    //拼接css文件的路径和名称
                    String href = res.getHref();
                    String name;
                    if (href.contains("/")) {
                        name = res.getHref().split("/")[1];
                    } else {
                        name = res.getHref();
                    }
                    File file = new File(config.getStyleUrl() + styleUrl + "/styles/" + name);
                    File fileParent = file.getParentFile();
                    if (!fileParent.exists()) {
                        fileParent.mkdirs();
                    }
                    file.createNewFile();
                    fos = new FileOutputStream(file);
                    String content = new String(res.getData(), "utf-8");
                    //替换css文件中的引用路径
                    content = content.replaceAll("(?i)/images/", "/imgs/");

                    content = content.replaceAll("(?i)/cover.jpg", "/cover.jpg");
                    content = content.replaceAll("(?i)/cover.png", "/cover.jpg");

                    content = content.replaceAll("(?i)/fonts/", "/fonts/");

                    content = content.replaceAll("(?i)/styles/", "/styles/");
                    content = content.replaceAll("(?i)/css/", "/styles/");

                    content = content.replaceAll("\nbody", "\ndiv");
                    content = content.replaceAll(" body.", " div.");
                    fos.write(content.getBytes());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) fos.close();
        }
    }

    //获取图片文件，并保存
    private void saveImageFile(Book book, EpubookMeta bookMeta, String styleUrl) throws IOException {
        FileOutputStream fos = null;
        try {
            //ServletContext context = ContextLoader.getCurrentWebApplicationContext().getServletContext();
            MediaType[] mImg = {MediatypeService.JPG, MediatypeService.PNG, MediatypeService.GIF};
            List<nl.siegmann.epublib.domain.Resource> imgFiles = book.getResources().getResourcesByMediaTypes(mImg);
            if (imgFiles != null && imgFiles.size() > 0) {
                for (nl.siegmann.epublib.domain.Resource res : imgFiles) {
                    File file;
                    //拼接图片文件的路径和名称
                    String href = res.getHref();
                    String name;
                    if (href.contains("/")) {
                        name = res.getHref().split("/")[1];
                    } else {
                        name = res.getHref();
                    }
                    file = new File(config.getStyleUrl() + styleUrl + "/imgs/" + name);
                    File fileParent = file.getParentFile();
                    if (!fileParent.exists()) {
                        fileParent.mkdirs();
                    }
                    file.createNewFile();
                    fos = new FileOutputStream(file);
                    fos.write(res.getData());
                    fos.flush();
                    //生成封面缩略图
                    if (res.getId().toLowerCase().contains("cover")) {
                        file = new File(config.getStyleUrl() + styleUrl + "/imgs/cover.jpg");
                        File fileParent1 = file.getParentFile();
                        if (!fileParent1.exists()) {
                            fileParent1.mkdirs();
                        }
                        file.createNewFile();
                        fos = new FileOutputStream(file);
                        fos.write(res.getData());
                        fos.flush();

                        bookMeta.setCoverUrl(BookConstant.BASE_URL + styleUrl + "/imgs/cover.jpg");
                        String prevfix = "cover" + "_" + String.valueOf(BookConstant.imgWidth) + "_" + String.valueOf(BookConstant.imgHeigth);
                        //生成450*600缩略图
                        BookUtil.thumbnailImage(file.getPath(), BookConstant.imgWidth, BookConstant.imgHeigth, prevfix, true);
                        bookMeta.setThumimgUrl(BookConstant.BASE_URL + styleUrl + "/imgs/" + prevfix + ".jpg");
                        //生成200*200缩略图
                        prevfix = "cover" + "_" + String.valueOf(BookConstant.imgW) + "_" + String.valueOf(BookConstant.imgH);
                        BookUtil.thumbnailImage(file.getPath(), BookConstant.imgW, BookConstant.imgH, prevfix, true);
                        //生成180*240缩略图
                        prevfix = "cover" + "_" + String.valueOf(BookConstant.imgWgth) + "_" + String.valueOf(BookConstant.imgHgth);
                        BookUtil.thumbnailImage(file.getPath(), BookConstant.imgWgth, BookConstant.imgHgth, prevfix, true);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) fos.close();
        }
    }

    //获取ttf文件，并保存
    private void saveTtfFile(Book book, String styleUrl) throws IOException {
        FileOutputStream fos = null;
        try {
            //ServletContext context = ContextLoader.getCurrentWebApplicationContext().getServletContext();
            MediaType[] mTff = {MediatypeService.TTF};
            List<nl.siegmann.epublib.domain.Resource> ttfFiles = book.getResources().getResourcesByMediaTypes(mTff);
            if (ttfFiles != null && ttfFiles.size() > 0) {
                for (nl.siegmann.epublib.domain.Resource res : ttfFiles) {
                    //拼接字体文件的路径和名称
                    String href = res.getHref();
                    String name;
                    if (href.contains("/")) {
                        name = res.getHref().split("/")[1];
                    } else {
                        name = res.getHref();
                    }
                    File file = new File(config.getStyleUrl() + styleUrl + "/fonts/" + name);
                    File fileParent = file.getParentFile();
                    if (!fileParent.exists()) {
                        fileParent.mkdirs();
                    }
                    file.createNewFile();
                    fos = new FileOutputStream(file);
                    fos.write(res.getData());
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) fos.close();
        }
    }

    //按章节分组进行分页
    private int insertEpubookByChapterShard(EpubookMeta epubookMeta,
                                            List<BookChapter> chapterList,
                                            List<BookShard> chapterShardList) {
        int res = bookShardService.insertBookShard(chapterShardList);
        if (res == 1) {
            int res1 = bookChapterService.insertBookChapter(chapterList);
            if (res1 == 1) {
                bookMetaService.saveEpubookMeta(epubookMeta);
                return 1;
            }
        }
        return 0;
    }

    //保存图书和文件映射关系
    private int saveFile(String metaid, String fileName) {
        if (org.apache.commons.lang3.StringUtils.isNotBlank(metaid) && org.apache.commons.lang3.StringUtils.isNotBlank(fileName)) {
            BookFile bookFile = new BookFile();
            bookFile.setFileName(fileName);
            bookFile.setFileType("epub");
            bookFile.setMetaid(metaid);
            bookFile.setId(UUIDCreater.nextId());
            bookFile.setCreateTime(new Date());
            bookFile.setUpdateTime(new Date());
            return bookFileDao.insertBookFile(bookFile);
        }
        return 0;
    }

    //更新图书元数据tmp表
    private int updateBookMetaTmp(EpubookMeta epubookMeta) {
        if (epubookMeta != null) {
            String sql = "UPDATE APABI_BOOK_METADATA_TEMP" +
                    " SET COVERURL = ?, THUMIMGURL = ?, STREAMCATALOG = ?, " +
                    "STYLEURL = ?, CHAPTERNUM = ?, UPDATETIME = ?," +
                    "CONTENTNUM = ?, HASFLOW = ?, ISOPTIMIZE = ? , FLOWSOURCE = ? " +
                    "WHERE METAID = ?";
            Object[] objects = new Object[]{
                    epubookMeta.getCoverUrl(), epubookMeta.getThumimgUrl(), epubookMeta.getStreamCatalog(),
                    epubookMeta.getStyleUrl(), epubookMeta.getChapterNum(), epubookMeta.getUpdatetime(),
                    epubookMeta.getContentNum(), epubookMeta.getHasflow(), epubookMeta.getIsoptimize(),
                    epubookMeta.getFlowSource(),
                    epubookMeta.getMetaid()
            };
            return jdbcTemplate.update(sql, objects);
        }
        return 0;
    }
}
