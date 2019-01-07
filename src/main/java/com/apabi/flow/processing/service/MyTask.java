package com.apabi.flow.processing.service;

import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.config.ApplicationConfig;
import com.apabi.flow.crawlTask.util.IpPoolUtils;
import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.douban.model.ApabiBookMetaTemp;
import com.apabi.flow.douban.service.DoubanMetaService;
import com.apabi.flow.douban.util.StringToolUtil;
import com.apabi.flow.processing.constant.BibliothecaStateEnum;
import com.apabi.flow.processing.dao.BibliothecaMapper;
import com.apabi.flow.processing.model.Bibliotheca;
import com.apabi.flow.processing.util.IsbnCheck;
import com.apabi.flow.publisher.dao.PublisherDao;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

//书目解析功能
public class MyTask implements Runnable {
    private static final Logger logger = LoggerFactory.getLogger(MyTask.class);

    private int i;
    private File f;
    private String username;
    private String batchId;
    private DoubanMetaService doubanMetaService;
    private PublisherDao publisherDao;
    private BibliothecaMapper bibliothecaMapper;
    private ApplicationConfig config;

    public MyTask(int i, File f, String username, String batchId, DoubanMetaService doubanMetaService, PublisherDao publisherDao, BibliothecaMapper bibliothecaMapper,ApplicationConfig config) {
        this.i = i;
        this.f = f;
        this.username = username;
        this.batchId = batchId;
        this.doubanMetaService = doubanMetaService;
        this.publisherDao = publisherDao;
        this.bibliothecaMapper = bibliothecaMapper;
        this.config=config;
    }

    @Override
    public void run() {
        System.out.println("正在执行task " + i);
        String ip = "";
        String port = "";
        Bibliotheca bibliotheca = new Bibliotheca();
        String isbn = "";
        String edition = "";
        String title = "";
        String author = "";
        String publisherTitle = "";
        String publisher="";
        String publishTime = "";
        String paperPrice = "";
        boolean a = true;
        try {
            File file = new File(config.getTargetCopyRightDir() + File.separator +batchId);
            if(!file.exists()){
                file.mkdirs();
            }
            String target = config.getTargetCopyRightDir() + File.separator +batchId +File.separator+ f.getName().split("[.]")[0]+".xml";
            String cmd = config.getCopyRightExtractExe() +
                    " -i " + "\"" + f.getAbsolutePath() + "\"" +
                    " -o " + "\"" + target + "\"" ;
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec(cmd);
            Thread.sleep(1000);
            SAXReader saxReader = new SAXReader();
            Document doc = saxReader.read(new File(target));
            Element root = doc.getRootElement();
            //判断exe生成xml是否成功
            if (root.attributeValue("Code").equals("0")) {
                List<Element> childElements = root.elements();
                Element element = childElements.get(0);
                String textTrim = element.getTextTrim();
                textTrim=ToDBC(textTrim);
                textTrim=replaceBlank(textTrim);
                int i = textTrim.indexOf("图书在版编目(CIP)数据");
                String ciptext = textTrim.substring(i + 13);
                String[] split = ciptext.split("/", 2);
                title = replaceBlank(split[0]);
                String[] split1 = split[1].split("[.]", 2);
                author = replaceBlank(split1[0]);
                String[] split2 = split1[1].split(":", 2)[1].split(",", 2);
                publisherTitle = replaceBlank(split2[0]);
                String str=split2[1];
                int i1 = str.indexOf("(");
                int i2 = str.lastIndexOf(")");
                str = str.substring(0,i1)+ str.substring(i2+1);
                String[] split3 = str.split("ISBN", 2);
                publishTime = replaceBlank(split3[0]);
                if (StringUtils.isNotEmpty(publishTime)) {
                    publishTime = StringToolUtil.issuedDateFormat(publishTime);
                    if (publishTime.contains(" 00:00:00")) {
                        // 获取清洗后的issuedDate的值
                        publishTime = publishTime.replace(" 00:00:00", "");
                    }
                }
                String[] split4 = split3[1].split("[.]", 2);
                isbn = replaceBlank(split4[0].substring(0,split4[0].length()-1));
                if (!IsbnCheck.CheckISBN(isbn)) {
                    isbn = "";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //通过isbn13进行查询元数据  包含-清洗
            if (StringUtils.isNotBlank(isbn)) {
                List<ApabiBookMetaTemp> doubanMetaList = doubanMetaService.searchMetaDataTempsByISBNMultiThread(isbn);
                if (doubanMetaList.size() > 0) {
                    ApabiBookMetaTemp apabiBookMetaTemp = doubanMetaList.get(0);
                    if (StringUtils.isNotBlank(publisherTitle)) {
                        publisher= publisherDao.findIdByTitle(publisherTitle);
                        if (StringUtils.isBlank(publisher)) {
                            publisher = publisherDao.findIdByTitle(apabiBookMetaTemp.getPublisher());
                        }
                    }
                    if(StringUtils.isBlank(title)){
                        title = apabiBookMetaTemp.getTitle();
                    }
                    if(StringUtils.isBlank(publishTime)) {
                        publishTime = apabiBookMetaTemp.getIssueddate();
                    }
                    if(StringUtils.isBlank(author)) {
                        author = apabiBookMetaTemp.getCreator();
                    }
                    edition=apabiBookMetaTemp.getEditionOrder();
                    paperPrice = apabiBookMetaTemp.getPaperPrice();
                    //信息不全
                    if (StringUtils.isBlank(publisherTitle) || StringUtils.isBlank(title) || StringUtils.isBlank(publishTime) || StringUtils.isBlank(paperPrice) || StringUtils.isBlank(author) || StringUtils.isBlank(edition)) {
                        a = false;
                    }
                } else {
                    a = false;
                }
                bibliotheca.setIsbn(isbn);
                if (StringUtils.isNotBlank(publisher)) {
                    bibliotheca.setPublisher(publisher);
                }
                bibliotheca.setPublisherName(publisherTitle);
                bibliotheca.setPublishTime(publishTime);
                bibliotheca.setTitle(title);
                bibliotheca.setPaperPrice(paperPrice);
                bibliotheca.setAuthor(author);
                logger.info("原文件:{}解析--完成，ISBN信息:{} ,版次信息:{} ,通过ISBN查询:{}条元数据", f.getName(), isbn, edition, doubanMetaList.size());
            } else {
                a = false;
                logger.info("原文件:{}解析--完成，ISBN未解析成功 ,版次信息:{} ,", f.getName(), edition);
            }
            //基本信息部分
            bibliotheca.setId(UUIDCreater.nextId());
            bibliotheca.setOriginalFilename(f.getName());
            bibliotheca.setCreateTime(new Date());
            bibliotheca.setCreator(username);
            Date date = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd");
            String d = simpleDateFormat.format(date);
            bibliotheca.setIdentifier(d +batchId+putong(i++));
            bibliotheca.setDocumentFormat("pdf");
            bibliotheca.setBatchId(batchId);
            bibliotheca.setEdition(edition);
            if (a) {
                //新建
                bibliotheca.setBibliothecaState(BibliothecaStateEnum.NEW);
            } else {
                //信息不全
                bibliotheca.setBibliothecaState(BibliothecaStateEnum.INFORMATION_NO);
            }
            bibliothecaMapper.insertSelective(bibliotheca);
        }
        System.out.println("task " + i + "执行完毕");
    }
    //编号
    public static String putong(int i) {
        if (i < 10) {
            return "000" + i;
        }else if (i >= 10&&i<=99) {
            return "00"+i;
        } else if (i >= 100&&i<=999) {
            return "0"+i;
        } else
            return  String.valueOf(i);
    }

    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            str = str.replace((char) 12288, ' ').trim();
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }
    //全角转半角
    public static String ToDBC(String input) {
        char c[] = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == '\u3000') {
                c[i] = ' ';
            } else if (c[i] > '\uFF00' && c[i] < '\uFF5F') {
                c[i] = (char) (c[i] - 65248);
            }
        }
        String returnString = new String(c);
        return returnString;
    }
}

