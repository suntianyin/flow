package com.apabi.flow.book.util;

import com.apabi.flow.book.dao.BookChapterDao;
import com.apabi.flow.book.dao.BookFileDao;
import com.apabi.flow.book.dao.BookMetaDao;
import com.apabi.flow.book.dao.BookShardDao;
import com.apabi.flow.book.model.BookChapter;
import com.apabi.flow.book.model.BookFile;
import com.apabi.flow.book.model.BookShard;
import com.apabi.flow.book.model.EpubookMeta;
import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.config.ApplicationConfig;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author guanpp
 * @date 2018/9/4 10:32
 * @description
 */
@Service("getCebxChapter")
public class GetCebxChapter {

    private static final Logger log = LoggerFactory.getLogger(GetCebxChapter.class);

    //样式和图片文件的加载路径
    private String cssUrl;

    //样式和图片文件的路径
    private String cssPath;

    //命令返回结果
    private static final String RES_SUCCESS = "success";

    //文件后缀
    private static final String FILE_HTML = ".html";
    private static final String FILE_CEBX = ".cebx";

    //cebx命令
    //private static final String CEBX_HTML_EXE = "E:/cebxHtml/Cebx2Html.exe -source ";

    @Autowired
    ApplicationConfig config;

    @Autowired
    BookMetaDao bookMetaDao;

    @Autowired
    BookChapterDao bookChapterDao;

    @Autowired
    BookShardDao bookShardDao;

    @Autowired
    BookFileDao bookFileDao;

    @Autowired
    JdbcTemplate jdbcTemplate;

    //获取章节内容
    public EpubookMeta insertCebx(String path, EpubookMeta epubookMeta, String fileName) throws IOException {
        if (StringUtils.isNotBlank(path)) {
            //解析cebx文件
            boolean res = getHtmlCss(path);
            if (res) {
                //解析html文件
                File dir = new File(config.getTargetCebxDir());
                if (!dir.exists()) {
                    return null;
                }
                long start = System.currentTimeMillis();
                String[] fileList = dir.list();
                int contentSum = 0;
                if (fileList != null && fileList.length > 0) {
                    //List<String> cataRows = new ArrayList<>();
                    StringBuffer cataRows = new StringBuffer();
                    String cata;
                    String[] tmp;
                    String content;
                    int wordSum = 0;
                    Document doc;
                    //按章节存放
                    List<BookChapter> chapterList = new ArrayList<>();
                    //对章节进行分组存放
                    List<BookShard> chapterShardList = new ArrayList<>();
                    for (String file : fileList) {
                        //只获取html文件
                        if (file.toLowerCase().contains(FILE_HTML)) {
                            //获取章节内容
                            content = readToString(config.getTargetCebxDir() + File.separator + file);
                            if (StringUtils.isNotBlank(content)) {
                                wordSum = content.replaceAll("\\u3000|\\s*", "").length();
                            }
                            //获取目录和章节号
                            file = file.replace(FILE_HTML, "");
                            tmp = file.split(BookConstant.FILE_NAME_LABEL);
                            //拼接目录
                            if (tmp != null && tmp.length > 1) {
                                int chapterNum = Integer.parseInt(tmp[1]);
                                cata = "{\"chapterName\":\"" + tmp[0] + "\",\"chapterNum\":" + chapterNum + "," +
                                        "\"ebookPageNum\":0,\"url\":\"\",\"wordSum\":" + wordSum + "},";
                                cataRows.append(cata);
                                //生成章节
                                BookChapter bookChapter = new BookChapter();
                                bookChapter.setComId(epubookMeta.getMetaid() + chapterNum);
                                bookChapter.setContent(content);
                                bookChapter.setWordSum(wordSum);
                                bookChapter.setChapterNum(chapterNum);
                                bookChapter.setCreateTime(new Date());
                                bookChapter.setUpdateTime(new Date());
                                contentSum += wordSum;
                                //拆分章节
                                doc = Jsoup.parse(content, BookConstant.CODE_UTF8);
                                Elements children = doc.body().children();
                                List<String> tags = new ArrayList<>();
                                String contentP = "";
                                for (int j = 0; j < children.size(); j++) {
                                    contentP += children.get(j).outerHtml();
                                    boolean flag = (j != 0 && j % BookConstant.shardSize == 0) || (j == children.size() - 1);
                                    if (flag) {
                                        tags.add(contentP);
                                        contentP = "";
                                    }
                                }
                                //添加分组数
                                bookChapter.setShardSum(tags.size());
                                chapterList.add(bookChapter);
                                //每组分片都新建一个shard
                                for (int k = 0; k < tags.size(); k++) {
                                    BookShard chapterShard = new BookShard();
                                    chapterShard.setComId(epubookMeta.getMetaid() + chapterNum + "S" + k);
                                    chapterShard.setChapterNum(chapterNum);
                                    chapterShard.setIndex(k);
                                    chapterShard.setCreateTime(new Date());
                                    chapterShard.setUpdateTime(new Date());
                                    Document shards = Jsoup.parse(tags.get(k), "utf-8");
                                    int wordS = shards.text().replaceAll("\\u3000|\\s*", "").length();
                                    chapterShard.setWordSum(wordS);
                                    String contentS = tags.get(k);
                                    chapterShard.setContent(contentS);
                                    chapterShardList.add(chapterShard);
                                }
                            }
                        }
                    }
                    long end = System.currentTimeMillis();
                    log.info("图书章节及分组拆分耗时：" + (end - start) + "毫秒");
                    //获取样式文件
                    boolean res1 = saveCss(epubookMeta);
                    if (res1) {
                        //样式连接
                        String styleUrl = "<link href=\"" +
                                "" + BookConstant.BASE_URL + "/" + cssUrl + "\"" +
                                " rel=\"stylesheet\" type=\"text/css\">";
                        epubookMeta.setStyleUrl(styleUrl);
                        //总字数
                        epubookMeta.setContentNum(contentSum);
                        //章节数
                        epubookMeta.setChapterNum(chapterList.size());
                        //目录
                        epubookMeta.setStreamCatalog(cataRows.toString());
                        epubookMeta.setHasflow(1);
                        epubookMeta.setIsoptimize(1);
                        epubookMeta.setUpdatetime(new Date());
                        int rs = saveBook(epubookMeta, chapterList, chapterShardList);
                        if (rs == 1) {
                            //保存图书和文件的对应关系
                            saveFile(epubookMeta.getMetaid(), fileName);
                            //更新metadata_tmp表
                            updateBookMetaTmp(epubookMeta);
                            //清空cebxHtml文件夹
                            deleteFiles(fileList);
                            return epubookMeta;
                        }
                    }
                }
            }
        }
        return null;
    }

    //删除文件
    private void deleteFiles(String[] filePaths) {
        if (filePaths != null && filePaths.length > 0) {
            for (String filePath : filePaths) {
                File file = new File(config.getTargetCebxDir() + File.separator + filePath);
                if (file.exists()) {
                    file.delete();
                }
            }
            log.info("cebx的html文件删除成功");
        }
    }

    //获取样式文件
    private boolean saveCss(EpubookMeta epubookMeta) throws IOException {
        if (epubookMeta != null) {
            long start = System.currentTimeMillis();
            String issuedDate = epubookMeta.getIssueddate().replace("-", "");
            String metaId = epubookMeta.getMetaid();
            String sp = File.separator;
            cssPath = issuedDate + sp + metaId + sp + BookConstant.CEBX_STYLE_DIR + sp + BookConstant.CEBX_STYLE_NAME;
            cssUrl = issuedDate + "/" + metaId + "/" + BookConstant.CEBX_STYLE_DIR + "/" + BookConstant.CEBX_STYLE_NAME;
            File target = new File(config.getStyleUrl() + sp + cssPath);
            File fileParent = target.getParentFile();
            if (!fileParent.exists()) {
                fileParent.mkdirs();
            }
            if (target.exists()) {
                target.delete();
            }
            File source = new File(config.getTargetCebxDir() + sp + BookConstant.CEBX_STYLE_DIR + sp + BookConstant.CEBX_STYLE_NAME);
            Files.copy(source.toPath(), target.toPath());
            long end = System.currentTimeMillis();
            log.info("样式文件已提取到" + target.getPath() + "耗时：" + (end - start) + "毫秒");
            return true;
        }
        return false;
    }

    //读取文件内容
    public String readToString(String filePath) {
        if (StringUtils.isNotBlank(filePath)) {
            File file = new File(filePath);
            Long filelength = file.length();
            byte[] filecontent = new byte[filelength.intValue()];
            try {
                FileInputStream in = new FileInputStream(file);
                in.read(filecontent);
                in.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                return new String(filecontent, BookConstant.CODE_UTF8);
            } catch (UnsupportedEncodingException e) {
                System.err.println("The OS does not support " + BookConstant.CODE_UTF8);
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    //解析cebx文件
    private boolean getHtmlCss(String path) throws IOException {
        if (StringUtils.isNotBlank(path)) {
            long start = System.currentTimeMillis();
            //调用命令
            /*String cmd = CEBX_HTML_EXE + "\"" + path + "\"" + " -target " + "\"" + BookConstant.TARGET_CEBX_DIR + "\""
                    + " -width " + BookConstant.imgWidth + " -height " + BookConstant.imgHeigth;*/
            String cmd = config.getCebxHtmlExe() + " -source " + "\"" + path + "\"" + " -target " + "\"" + config.getTargetCebxDir() + "\""
                    + " -width " + BookConstant.imgWidth + " -height " + BookConstant.imgHeigth;
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(cmd);
            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(process.getInputStream(), BookConstant.CODE_UTF8));
            String line;
            String res = null;
            while ((line = bufferedReader.readLine()) != null) {
                res += line;
            }
            //如果命令执行成功
            if (StringUtils.isNotBlank(res)) {
                if (res.toLowerCase().contains(RES_SUCCESS)) {
                    long end = System.currentTimeMillis();
                    log.info("生成html文件耗时：" + (end - start) + "毫秒");
                    return true;
                } else {
                    log.warn(res);
                }
            }
        }
        return false;
    }

    //保存和更新图书内容
    private int saveBook(EpubookMeta epubookMeta,
                         List<BookChapter> bookChapters,
                         List<BookShard> bookShards) {
        if (epubookMeta != null) {
            if (bookShards != null && bookShards.size() > 0) {
                //保存章节内容
                for (BookShard bookShard : bookShards) {
                    bookShardDao.insertBookShard(bookShard);
                }
                if (bookChapters != null && bookChapters.size() > 0) {
                    //保存章节分组内容
                    for (BookChapter bookChapter : bookChapters) {
                        bookChapterDao.insertBookChapter(bookChapter);
                    }
                    //更新图书元数据
                    bookMetaDao.updateEpubookMeta(epubookMeta);
                    return 1;
                }
            }
        }
        return 0;
    }

    //保存图书和文件映射关系
    private int saveFile(String metaid, String fileName) {
        if (StringUtils.isNotBlank(metaid) && StringUtils.isNotBlank(fileName)) {
            BookFile bookFile = new BookFile();
            bookFile.setFileName(fileName);
            bookFile.setFileType(FILE_CEBX);
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
