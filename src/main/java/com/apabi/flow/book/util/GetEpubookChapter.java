package com.apabi.flow.book.util;

import com.apabi.flow.book.dao.BookFileDao;
import com.apabi.flow.book.model.*;
import com.apabi.flow.book.service.BookChapterService;
import com.apabi.flow.book.service.BookMetaService;
import com.apabi.flow.book.service.BookShardService;
import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.config.ApplicationConfig;
import com.apabi.flow.douban.dao.ApabiBookMetaTempRepository;
import net.sf.json.JSONObject;
import nl.siegmann.epublib.domain.Book;
import nl.siegmann.epublib.domain.MediaType;
import nl.siegmann.epublib.domain.TOCReference;
import nl.siegmann.epublib.epub.EpubReader;
import nl.siegmann.epublib.service.MediatypeService;
import org.apache.commons.lang.StringUtils;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author guanpp
 * @date 2018/8/1 9:37
 * @description
 */
@Service("GetEpubookChapter")
public class GetEpubookChapter {


    private static final Logger log = LoggerFactory.getLogger(GetEpubookChapter.class);

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    //样式和图片文件的生成路径
    private String styleUrl;

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
            if (epubookMeta != null && fileName != null && fileName.length() > 0) {
                Book book = getEpubook(path);
                if (book != null) {
                    //获取meta数据
                    epubookMeta = getMetaData(epubookMeta, book);
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
                            Map<String, Integer> map = new HashMap<>();
                            //按章节存放
                            List<BookChapter> chapterList = new ArrayList<>();
                            //对章节进行分组存放
                            List<BookShard> chapterShardList = new ArrayList<>();
                            for (int i = 0; i < contents.size(); i++) {
                                BookChapter chapter = new BookChapter();
                                //图书号和章节号拼接id
                                chapter.setComId(epubookMeta.getMetaid() + i);
                                chapter.setChapterNum(i);
                                map.put(contents.get(i).getHref(), i);

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
                                String content = replaceImgeUrl(body.toString());
                                chapter.setContent(content);
                                chapter.setBodyClass(body.attr("class"));
                                chapter.setCreateTime(sdf.parse(sdf.format(new Date())));
                                chapter.setUpdateTime(sdf.parse(sdf.format(new Date())));
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
                                    chapterShard.setCreateTime(sdf.parse(sdf.format(new Date())));
                                    chapterShard.setUpdateTime(sdf.parse(sdf.format(new Date())));
                                    Document shards = Jsoup.parse(tags.get(k), "utf-8");
                                    int wordS = shards.text().replaceAll("\\u3000|\\s*", "").length();
                                    chapterShard.setWordSum(wordS);
                                    //替换图书内容中的图片路径
                                    String contentS = replaceImgeUrl(tags.get(k));
                                    /*String contentS = tags.get(k);
                                    contentS = contentS.replaceAll("..(?i)/images/", BookConstant.BASE_URL + styleUrl + "/imgs/");*/
                                    chapterShard.setContent(contentS);
                                    chapterShardList.add(chapterShard);
                                }
                            }
                            epubookMeta.setContentNum(wordSum);
                            //获取目录信息，并添加章节号和章节字数
                            List<BookCataRows> catalogList = getCataRowData(book, map);
                            if (catalogList != null && catalogList.size() > 0) {
                                //List<String> cataArr = new ArrayList<>();
                                String cataArr = "";
                                JSONObject jsonObject;
                                for (BookCataRows cataRows : catalogList) {
                                    jsonObject = JSONObject.fromObject(cataRows);
                                    //cataArr.add(jsonObject.toString());
                                    cataArr += jsonObject.toString() + ",";
                                }
                                epubookMeta.setStreamCatalog(cataArr);
                            } else {
                                log.warn("图书《" + epubookMeta.getTitle() + "》目录获取有误！");
                                return null;
                            }
                            long end2 = System.currentTimeMillis();
                            log.info("获取图书：" + epubookMeta.getTitle() + "所有章节，耗时：" + (end2 - start2) + "毫秒");
                            //抽取文件
                            long start1 = System.currentTimeMillis();
                            //抽取css样式文件，并保存
                            saveCssFile(book);
                            //抽取图片文件，并保存
                            saveImageFile(book, epubookMeta);
                            //抽取字体文件，并保存
                            saveTtfFile(book);
                            long end1 = System.currentTimeMillis();
                            log.info("获取图书：" + epubookMeta.getTitle() + "，样式文件及图片，耗时：" + (end1 - start1) + "毫秒");

                            epubookMeta.setHasflow(1);
                            epubookMeta.setIsoptimize(1);
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
    private String replaceImgeUrl(String content) {
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
                EpubReader epubReader = new EpubReader();
                InputStream inputStr = new FileInputStream(path);
                Book book = epubReader.readEpub(inputStr);
                return book;
            }
        }
        return null;
    }

    //获取图书标题、作者、出版社、出版日期、类型、语言等数据
    private EpubookMeta getMetaData(EpubookMeta epubookMeta, Book book) throws ParseException {
        if (book != null) {
            //获取日期
            String date = epubookMeta.getIssueddate().substring(0, 10);
            //获取图书号
            String id = epubookMeta.getMetaid();
            epubookMeta.setChapterNum(book.getContents().size());
            epubookMeta.setUpdatetime(sdf.parse(sdf.format(new Date())));
            if (!StringUtils.isEmpty(id) && !StringUtils.isEmpty(date)) {
                styleUrl = "/" + date.replace("-", "") + "/" + id;
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

    //获取css文件，并保存
    private void saveCssFile(Book book) throws IOException {
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
    private void saveImageFile(Book book, EpubookMeta bookMeta) throws IOException {
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
    private void saveTtfFile(Book book) throws IOException {
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
                    "CONTENTNUM = ?, HASFLOW = ?, ISOPTIMIZE = ? " +
                    "WHERE METAID = ?";
            Object[] objects = new Object[]{
                    epubookMeta.getCoverUrl(), epubookMeta.getThumimgUrl(), epubookMeta.getStreamCatalog(),
                    epubookMeta.getStyleUrl(), epubookMeta.getChapterNum(), epubookMeta.getUpdatetime(),
                    epubookMeta.getContentNum(), epubookMeta.getHasflow(), epubookMeta.getIsoptimize(),
                    epubookMeta.getMetaid()
            };
            return jdbcTemplate.update(sql, objects);
        }
        return 0;
    }
}
