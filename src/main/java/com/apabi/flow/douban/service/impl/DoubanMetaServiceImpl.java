package com.apabi.flow.douban.service.impl;

import com.apabi.flow.crawlTask.util.IpPoolUtils;
import com.apabi.flow.crawlTask.util.NlcIpPoolUtils;
import com.apabi.flow.douban.dao.ApabiBookMetaDataDao;
import com.apabi.flow.douban.dao.ApabiBookMetaDataTempDao;
import com.apabi.flow.douban.dao.DoubanMetaDao;
import com.apabi.flow.douban.model.AmazonMeta;
import com.apabi.flow.douban.model.ApabiBookMetaData;
import com.apabi.flow.douban.model.ApabiBookMetaDataTemp;
import com.apabi.flow.douban.model.DoubanMeta;
import com.apabi.flow.douban.service.AmazonMetaService;
import com.apabi.flow.douban.service.DoubanMetaService;
import com.apabi.flow.douban.util.BeanTransformUtil;
import com.apabi.flow.douban.util.HttpUtils2;
import com.apabi.flow.douban.util.Isbn13ToIsbnUtil;
import com.apabi.flow.douban.util.StringToolUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import net.sf.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.util.EntityUtils;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author pipi
 * @date 2018/8/8 16:23
 * @description
 */
@Service("doubanMetaService")
public class DoubanMetaServiceImpl implements DoubanMetaService {
    private org.slf4j.Logger log = LoggerFactory.getLogger(DoubanMetaServiceImpl.class);
    @Autowired
    private ApabiBookMetaDataTempDao apabiBookMetaDataTempDao;

    @Autowired
    private ApabiBookMetaDataDao apabiBookMetaDataDao;

    @Autowired
    private AmazonMetaService amazonMetaService;

    @Autowired
    private DoubanMetaDao doubanMetaDao;

    @Override
    public DoubanMeta searchDoubanMetaByISBN(String isbn) {
        return null;
    }

    @Override
    public List<DoubanMeta> searchDoubanMetasByISBN(String isbn13) {
        List<DoubanMeta> doubanMetaList = new ArrayList<>();
        // List<ApabiBookMetaTemp> apabiBookMetaTempReturnedList = new ArrayList<>();
        if (!StringUtils.isEmpty(isbn13)) {
            // 最终需要返回的List
            if (isbn13.contains("-")) {
                isbn13 = isbn13.replace("-", "");
            }
            // 查询meta_data库中是否有该数据
            List<ApabiBookMetaData> apabiBookMetaList = apabiBookMetaDataDao.findByIsbn13(isbn13);
            List<String> metaIdList = new ArrayList<>();
            // 查看temp库中是否有该数据
            List<ApabiBookMetaDataTemp> apabiBookMetaTempList = apabiBookMetaDataTempDao.findByIsbn13(isbn13);
            // 获取是否发布
            if (apabiBookMetaList != null && apabiBookMetaList.size() > 0) {
                for (int i = 0; i < apabiBookMetaList.size(); i++) {
                    metaIdList.add(apabiBookMetaList.get(i).getMetaId());
                }
            }
            System.out.println(apabiBookMetaList);
            // 如果在meta_data库中查询到直接返回
            if (apabiBookMetaList != null && apabiBookMetaList.size() != 0) {
                // 遍历apabiBookMetaList，将doubanMetaList返回
                for (int i = 0; i < apabiBookMetaList.size(); i++) {
                    ApabiBookMetaData apabiBookMeta = apabiBookMetaList.get(i);
                    if (apabiBookMeta != null) {
                        DoubanMeta doubanMeta = BeanTransformUtil.transform2DoubanMeta(apabiBookMeta);
                        if (apabiBookMeta.getCreateTime() == null) {
                            doubanMeta.setCreateTime(null);
                        }
                        if (apabiBookMeta.getUpdateTime() == null) {
                            doubanMeta.setUpdateTime(null);
                        }
                        // 设置是否发布
                        doubanMeta.setHasPublish(apabiBookMeta.getHasPublish());
                        // 设置metaId
                        doubanMeta.setMetaId(metaIdList.get(i));

                        Integer hasPublish = apabiBookMetaTempList.get(i).getHasPublish();
                        if (hasPublish == null || hasPublish == 0) {
                            doubanMeta.setHasPublish(0);
                        }
                        doubanMetaList.add(doubanMeta);
                        // apabiBookMetaTempReturnedList.add(apabiBookMetaTempList.get(i));
                    }
                }
                // 直接返回
                return doubanMetaList;
            } else {
                // 如果meta_data库中没有数据，则去douban库查询
                List<DoubanMeta> doubanMetaSearchList = doubanMetaDao.findByIsbn13(isbn13);
                // 不论在豆瓣上有没有检索到,都首先查询amazon数据,没有数据则抓取amazon页面
                AmazonMeta amazonMeta = null;
                try {
                    amazonMeta = amazonMetaService.findOrCrawlAmazonMetaByIsbn(isbn13);
                } catch (Exception e) {
                    // 查询或者抓取amazon数据，如果抛出异常，则不做处理
                    e.printStackTrace();
                    log.info("抓取amazon，处理" + isbn13 + "出错...");
                    log.info(e.getMessage());
                }
                // 获取metaId
                String metaId2 = "";
                // 获取是否发布
                Integer hasPublish = 0;
                if (apabiBookMetaTempList != null && apabiBookMetaTempList.size() > 0) {
                    metaId2 = apabiBookMetaTempList.get(0).getMetaId();
                    hasPublish = apabiBookMetaTempList.get(0).getHasPublish() == null ? 0 : apabiBookMetaTempList.get(0).getHasPublish();
                }
                // 如果豆瓣库中有，则直接返回
                if (doubanMetaSearchList != null && doubanMetaSearchList.size() > 0) {
                    // 如果在temp库中没有该数据
                    if (apabiBookMetaTempList == null || apabiBookMetaTempList.size() == 0) {
                        for (DoubanMeta doubanMeta : doubanMetaSearchList) {
                            String metaId3 = "";
                            ApabiBookMetaDataTemp apabiBookMetaTemp = BeanTransformUtil.transform2ApabiBookMetaTemp(doubanMeta);
                            // 把该数据写入到temp库中
                            if (doubanMeta.getIssueddate() != null) {
                                apabiBookMetaTemp.setUpdateTime(new Date());
                                apabiBookMetaTemp.setCreateTime(new Date());
                                metaId3 = StringToolUtil.metaidFormat(doubanMeta.getIssueddate());
                                apabiBookMetaTemp.setMetaId(metaId3);
                                if (amazonMeta != null) {
                                    apabiBookMetaTemp = BeanTransformUtil.mergeApabiBookMetaTempWithAmazon(apabiBookMetaTemp, amazonMeta);
                                }
                                // 根据isbn13设置isbn
                                String isbn = null;
                                if (apabiBookMetaTemp.getIsbn13() != null && (apabiBookMetaTemp.getIsbn() == null || "".equalsIgnoreCase(apabiBookMetaTemp.getIsbn()))) {
                                    isbn = Isbn13ToIsbnUtil.transform(apabiBookMetaTemp.getIsbn13());
                                }
                                apabiBookMetaTemp.setIsbn(isbn);
                                apabiBookMetaDataTempDao.insert(apabiBookMetaTemp);
                            }
                            // 设置metaId
                            doubanMeta.setMetaId(metaId3);
                            // 设置是否发布
                            doubanMeta.setHasPublish(hasPublish);
                            if (doubanMeta.getCreateTime() == null) {
                                doubanMeta.setCreateTime(new Date());
                            }
                            doubanMeta.setUpdateTime(new Date());
                            // 将doubanMetaSearchList的内容添加到doubanMetaList中
                            doubanMetaList.add(doubanMeta);
                        }
                    } else {
                        for (DoubanMeta doubanMeta : doubanMetaSearchList) {
                            doubanMeta.setMetaId(metaId2);
                            doubanMeta.setHasPublish(hasPublish);
//                            if (doubanMeta.getCreateTime() == null) {
//                                doubanMeta.setCreateTime(new Date());
//                            }
                            // doubanMeta.setUpdateTime(new Date());
                            doubanMetaList.add(doubanMeta);
                        }
                    }
                    return doubanMetaList;
                } else {
                    DoubanMeta doubanMeta = new DoubanMeta();
                    String resUrl = "https://api.douban.com/v2/book/isbn/" + isbn13;
                    try {
                        // 发送请求，返回Json数据
                        JSONObject book = getJson(resUrl);
                        // 如果能爬取到douban的数据，以douban的数据为主！
                        if (book != null) {
                            // 创建douban数据实例
                            // 将返回的Json数据实例Json对象
                            JSONObject jsonobject = JSONObject.fromObject(book);
                            // 设置DouBan实例对象
                            if (book.toString().contains("id")) {
                                doubanMeta.setDoubanId(jsonobject.getString("id"));
                            }
                            if (book.toString().contains("title")) {
                                doubanMeta.setTitle(jsonobject.getString("title").replace("'", "''"));
                            }
                            if (book.toString().contains("author")) {
                                if (jsonobject.getJSONArray("author").size() != 0) {
                                    doubanMeta.setAuthor(jsonobject.getJSONArray("author").getString(0).replace("'", "''"));
                                }
                            }
                            if (book.toString().contains("publisher")) {
                                doubanMeta.setPublisher(jsonobject.getString("publisher").replace("'", "''"));
                            }
                            if (book.toString().contains("alt_title")) {
                                doubanMeta.setAltTitle(jsonobject.getString("alt_title").replace("'", "''"));
                            }
                            if (book.toString().contains("subtitle")) {
                                doubanMeta.setSubtitle(jsonobject.getString("subtitle").replace("'", "''"));
                            }
                            if (book.toString().contains("translator")) {
                                if (jsonobject.getJSONArray("translator").size() != 0) {
                                    doubanMeta.setTranslator(jsonobject.getJSONArray("translator").getString(0).replace("'", "''"));
                                }
                            }
                            if (book.toString().contains("isbn10")) {
                                doubanMeta.setIsbn10(jsonobject.getString("isbn10"));
                            }
                            if (book.toString().contains("isbn13")) {
                                doubanMeta.setIsbn13(jsonobject.getString("isbn13"));
                            }
                            if (book.toString().contains("pubdate")) {
                                doubanMeta.setIssueddate(jsonobject.getString("pubdate"));
                            }
                            if (book.toString().contains("pages")) {
                                doubanMeta.setPages(jsonobject.getString("pages"));
                            }
                            if (book.toString().contains("price")) {
                                doubanMeta.setPrice(jsonobject.getString("price"));
                            }
                            if (book.toString().contains("binding")) {
                                doubanMeta.setBinding(jsonobject.getString("binding").replace("'", "''"));
                            }
                            if (book.toString().contains("series\":")) {
                                doubanMeta.setSeries(JSONObject.fromObject(jsonobject.getString("series")).getString("title").replace("'", "''"));
                            }
                            if (book.toString().contains("rating")) {
                                doubanMeta.setAverage(JSONObject.fromObject(jsonobject.getString("rating")).getString("average").replace("'", "''"));
                            }
                            if (book.toString().contains("summary")) {
                                doubanMeta.setSummary(jsonobject.getString("summary").replace("'", "''"));
                            }
                            if (book.toString().contains("author_intro")) {
                                doubanMeta.setAuthorIntro(jsonobject.getString("author_intro").replace("'", "''"));
                            }
                            if (book.toString().contains("catalog")) {
                                doubanMeta.setCatalog(jsonobject.getString("catalog").replace("'", "''"));
                            }
                            if (book.toString().contains("tags")) {
                                String tags = "";
                                for (Object titles : jsonobject.getJSONArray("tags")) {
                                    tags += JSONObject.fromObject(titles).getString("title") + " ";
                                }
                                doubanMeta.setTags(tags.replace("'", "''"));
                            }
                            if (book.toString().contains("images")) {
                                doubanMeta.setSmallCover(jsonobject.getJSONObject("images").getString("small"));
                                doubanMeta.setLargeCover(jsonobject.getJSONObject("images").getString("large"));
                                doubanMeta.setMediumCover(jsonobject.getJSONObject("images").getString("medium"));
                            }
                            if (book.toString().contains("ebook_price")) {
                                doubanMeta.setEbookPrice(jsonobject.getString("ebook_price"));
                            }
                            if (book.toString().contains("origin_title")) {
                                doubanMeta.setOriginTitle(jsonobject.getString("origin_title").replace("'", "''"));
                            }
                            // 将爬取到的数据添加到豆瓣数据库中
                            doubanMeta.setCreateTime(new Date());
                            doubanMeta.setUpdateTime(new Date());
                            // 对出版日期进行清洗
                            if (StringUtils.isNotEmpty(doubanMeta.getIssueddate())) {
                                String s = StringToolUtil.issuedDateFormat(doubanMeta.getIssueddate());
                                if (s.contains(" 00:00:00")) {
                                    s = s.replaceAll(" 00:00:00", "");
                                }
                                doubanMeta.setIssueddate(s);
                            }
                            doubanMetaDao.insert(doubanMeta);
                            // 将爬取到的数据添加到apabi数据库中
                            // apabiBookMeta = BeanTransformUtil.transform2ApabiBookMeta(doubanMeta);
                            // apabiBookMetaRepository.saveAndFlush(apabiBookMeta);
                            // 将爬取到的数据添加到apabi_temp数据库中,如果没有出版时间，则不添加
                            if (doubanMeta.getIssueddate() != null && !"".equalsIgnoreCase(doubanMeta.getIssueddate())) {
                                ApabiBookMetaDataTemp apabiBookMetaTemp = BeanTransformUtil.transform2ApabiBookMetaTemp(doubanMeta);
                                // 设置创建时间和更新时间
                                apabiBookMetaTemp.setCreateTime(new Date());
                                apabiBookMetaTemp.setUpdateTime(new Date());
                                // 根据isbn13设置isbn
                                String isbn = null;
                                if (apabiBookMetaTemp.getIsbn13() != null && (apabiBookMetaTemp.getIsbn() == null || "".equalsIgnoreCase(apabiBookMetaTemp.getIsbn()))) {
                                    isbn = Isbn13ToIsbnUtil.transform(apabiBookMetaTemp.getIsbn13());
                                }
                                apabiBookMetaTemp.setIsbn(isbn);
                                if (amazonMeta != null) {
                                    // 如果amazon的数据不为空，将amazon的数据内容补充到apabiBookMetaTemp中
                                    apabiBookMetaTemp = BeanTransformUtil.mergeApabiBookMetaTempWithAmazon(apabiBookMetaTemp, amazonMeta);
                                }
                                apabiBookMetaDataTempDao.insert(apabiBookMetaTemp);
                                metaId2 = apabiBookMetaTemp.getMetaId();
                            }
                            Thread.sleep(1000 * 3);
                            doubanMeta.setMetaId(metaId2);
                            doubanMeta.setHasPublish(0);
                            doubanMetaList.add(doubanMeta);
                        } else {
                            // 如果douban数据抓取不到，则以amazon的数据为主
                            if (amazonMeta != null) {
                                doubanMeta = BeanTransformUtil.transform2DoubanMetaFromAmazon(amazonMeta);
                                doubanMeta.setHasPublish(0);
                                ApabiBookMetaDataTemp apabiBookMetaTemp = BeanTransformUtil.transform2ApabiBookMetaTemp(doubanMeta);
                                // 将amazon的数据补充到apabiBookMetaTemp
                                apabiBookMetaTemp = BeanTransformUtil.mergeApabiBookMetaTempWithAmazon(apabiBookMetaTemp, amazonMeta);
                                apabiBookMetaTemp.setHasPublish(0);
                                apabiBookMetaTemp.setDataSource("amazon");
                                if (apabiBookMetaTempList.size() == 0) {
                                    doubanMeta.setMetaId(apabiBookMetaTemp.getMetaId());
                                    apabiBookMetaTemp.setCreateTime(new Date());
                                    apabiBookMetaTemp.setUpdateTime(new Date());
                                    // 根据isbn13设置isbn
                                    String isbn = null;
                                    if (apabiBookMetaTemp.getIsbn13() != null && (apabiBookMetaTemp.getIsbn() == null || "".equalsIgnoreCase(apabiBookMetaTemp.getIsbn()))) {
                                        isbn = Isbn13ToIsbnUtil.transform(apabiBookMetaTemp.getIsbn13());
                                    }
                                    apabiBookMetaTemp.setIsbn(isbn);
                                    apabiBookMetaDataTempDao.insert(apabiBookMetaTemp);
                                } else {
                                    ApabiBookMetaDataTemp apabiBookMetaTemp1 = apabiBookMetaTempList.get(0);
                                    String metaId = apabiBookMetaTemp1.getMetaId();
                                    doubanMeta.setMetaId(metaId);
                                    apabiBookMetaTemp.setMetaId(metaId);
                                }
                                doubanMetaList.add(doubanMeta);
                            }
                        }
                        return doubanMetaList;
                    } catch (Exception e) {
                        e.printStackTrace();
                        System.out.println("获取数据发生异常，【异常信息】：" + e.getMessage());
                    }
                }
                return doubanMetaList;
            }
        }
        return doubanMetaList;
    }

    //    @Transactional(value = "transactionManager",isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class, timeout = 6)
    @Override
    public List<ApabiBookMetaDataTemp> searchMetaDataTempsByISBNMultiThread(String isbn13) {
        // 创建ApabiBookMetaTempReturnedList
        List<ApabiBookMetaDataTemp> apabiBookMetaTempReturnedList = new ArrayList<>();

        if (!StringUtils.isEmpty(isbn13)) {
            // 对isbn13进行清洗
            if (isbn13.contains("-")) {
                isbn13 = isbn13.replace("-", "");
            }
            // 查询temp表中是否有该isbn的数据
            List<ApabiBookMetaDataTemp> apabiBookMetaTempList = apabiBookMetaDataTempDao.findByIsbn13(isbn13);
            String metaId = "";
            if (apabiBookMetaTempList != null && apabiBookMetaTempList.size() > 0) {
                metaId = apabiBookMetaTempList.get(0).getMetaId();
            }
            // 查询meta_data表中是否有该数据
            List<ApabiBookMetaData> apabiBookMetaList = apabiBookMetaDataDao.findByIsbn13(isbn13);
            // 首先查出meta_data表中hasFlow和hasCebx字段的值
            Integer hasFlow = 0;
            Integer hasCebx = 0;
            if (apabiBookMetaList != null && apabiBookMetaList.size() > 0) {
                if (apabiBookMetaList.get(0).getHasFlow() != null) {
                    hasFlow = apabiBookMetaList.get(0).getHasFlow();
                }
                if (apabiBookMetaList.get(0).getHasCebx() != null) {
                    hasCebx = apabiBookMetaList.get(0).getHasCebx();
                }
            }
            // 如果在meta_data库中查询到直接返回
            if (apabiBookMetaList != null && apabiBookMetaList.size() != 0) {
                // 遍历apabiBookMetaList
                for (int i = 0; i < apabiBookMetaList.size(); i++) {
                    ApabiBookMetaData apabiBookMeta = apabiBookMetaList.get(i);
                    if (apabiBookMeta != null) {
                        // 如果apabiBookMeta不为null，则将apabiBookMeta的属性值拷贝到新创建的apabiBookMetaTemp
                        ApabiBookMetaDataTemp apabiBookMetaTemp = new ApabiBookMetaDataTemp();
                        BeanUtils.copyProperties(apabiBookMeta, apabiBookMetaTemp);
                        // 将apabiBookMetaTemp添加到返回结果列表
                        apabiBookMetaTempReturnedList.add(apabiBookMetaTemp);
                    }
                }
                // 直接返回
                return apabiBookMetaTempReturnedList;
            } else {
                // 如果meta_data库中没有数据，则去douban库查询
                List<DoubanMeta> doubanMetaSearchList = doubanMetaDao.findByIsbn13(isbn13);
                // 不论在豆瓣上有没有检索到,都首先查询amazon数据,没有数据则抓取amazon页面
                AmazonMeta amazonMeta = null;
                try {
                    amazonMeta = amazonMetaService.findOrCrawlAmazonMetaByIsbn(isbn13);
                } catch (Exception e) {
                    // 查询或者抓取amazon数据，如果catch住异常，则不做处理
                    e.printStackTrace();
                    log.info("抓取amazon，处理" + isbn13 + "出错...");
                    log.info(e.getMessage());

                }
                // 如果豆瓣库中有，则直接返回
                if (doubanMetaSearchList != null && doubanMetaSearchList.size() > 0) {
                    for (DoubanMeta doubanMeta : doubanMetaSearchList) {
                        if (doubanMeta.getIssueddate() != null && "".equalsIgnoreCase("")) {
                            ApabiBookMetaDataTemp apabiBookMetaTemp = BeanTransformUtil.transform2ApabiBookMetaTemp(doubanMeta);
                            if (StringUtils.isNotEmpty(metaId)) {
                                apabiBookMetaTemp.setMetaId(metaId);
                            }
                            // 把该数据写入到temp库中
                            if (doubanMeta.getIssueddate() != null) {
                                // 根据查询到的temp表中的数据设置apabiBookMetaTemp的更新时间和创建时间
                                if (apabiBookMetaTempList != null && apabiBookMetaTempList.size() > 0 && apabiBookMetaTempList.get(0).getCreateTime() != null) {
                                    apabiBookMetaTemp.setCreateTime(apabiBookMetaTempList.get(0).getCreateTime());
                                } else {
                                    apabiBookMetaTemp.setCreateTime(new Date());
                                }
                                if (apabiBookMetaTempList != null && apabiBookMetaTempList.size() > 0 && apabiBookMetaTempList.get(0).getUpdateTime() != null) {
                                    apabiBookMetaTemp.setUpdateTime(apabiBookMetaTempList.get(0).getUpdateTime());
                                } else {
                                    apabiBookMetaTemp.setUpdateTime(new Date());
                                }
                                if (amazonMeta != null) {
                                    apabiBookMetaTemp = BeanTransformUtil.mergeApabiBookMetaTempWithAmazon(apabiBookMetaTemp, amazonMeta);
                                }

                                // 判断并设置是否发布
                                if (apabiBookMetaTempList != null && apabiBookMetaTempList.size() > 0) {
                                    if (apabiBookMetaTempList.get(0).getHasPublish() == null) {
                                        apabiBookMetaTemp.setHasPublish(0);
                                    } else {
                                        apabiBookMetaTemp.setHasPublish(apabiBookMetaTempList.get(0).getHasPublish());
                                    }
                                }
                                // 当temp表中没有该数据，才将其插入到temp表中
                                if (apabiBookMetaTempList == null || apabiBookMetaTempList.size() == 0) {
                                    apabiBookMetaTemp.setCreateTime(new Date());
                                    apabiBookMetaTemp.setUpdateTime(new Date());
                                    apabiBookMetaTemp.setHasPublish(0);
                                    apabiBookMetaTemp.setHasCebx(hasCebx);
                                    apabiBookMetaTemp.setHasFlow(hasFlow);
                                    apabiBookMetaDataTempDao.insert(apabiBookMetaTemp);
                                }
                            }
                            apabiBookMetaTempReturnedList.add(apabiBookMetaTemp);
                        } else {
                            ApabiBookMetaDataTemp apabiBookMetaTemp = BeanTransformUtil.transform2ApabiBookMetaTemp(doubanMeta);
                            apabiBookMetaTemp.setMetaId(metaId);
                            apabiBookMetaTemp.setUpdateTime(doubanMeta.getUpdateTime());
                            apabiBookMetaTemp.setCreateTime(doubanMeta.getCreateTime());
                            apabiBookMetaTemp.setHasFlow(hasFlow);
                            apabiBookMetaTemp.setHasCebx(hasCebx);
                            apabiBookMetaTempReturnedList.add(apabiBookMetaTemp);
                        }
                    }
                    return apabiBookMetaTempReturnedList;
                } else {
                    // 如果douban表中没有，则取douban抓取
                    DoubanMeta doubanMeta = new DoubanMeta();
                    String resUrl = "https://api.douban.com/v2/book/isbn/" + isbn13;
                    try {
                        // 发送请求，返回Json数据
                        JSONObject book = getJson2(resUrl);
                        // 如果能爬取到douban的数据，以douban的数据为主！
                        if (book != null) {
                            // 创建douban数据实例
                            // 将返回的Json数据实例Json对象
                            JSONObject jsonobject = JSONObject.fromObject(book);
                            // 设置DouBan实例对象
                            if (book.toString().contains("id")) {
                                doubanMeta.setDoubanId(jsonobject.getString("id"));
                            }
                            if (book.toString().contains("title")) {
                                doubanMeta.setTitle(jsonobject.getString("title").replace("'", "''"));
                            }
                            if (book.toString().contains("author")) {
                                if (jsonobject.getJSONArray("author").size() != 0) {
                                    doubanMeta.setAuthor(jsonobject.getJSONArray("author").getString(0).replace("'", "''"));
                                }
                            }
                            if (book.toString().contains("publisher")) {
                                doubanMeta.setPublisher(jsonobject.getString("publisher").replace("'", "''"));
                            }
                            if (book.toString().contains("alt_title")) {
                                doubanMeta.setAltTitle(jsonobject.getString("alt_title").replace("'", "''"));
                            }
                            if (book.toString().contains("subtitle")) {
                                doubanMeta.setSubtitle(jsonobject.getString("subtitle").replace("'", "''"));
                            }
                            if (book.toString().contains("translator")) {
                                if (jsonobject.getJSONArray("translator").size() != 0) {
                                    doubanMeta.setTranslator(jsonobject.getJSONArray("translator").getString(0).replace("'", "''"));
                                }
                            }
                            if (book.toString().contains("isbn10")) {
                                doubanMeta.setIsbn10(jsonobject.getString("isbn10"));
                            }
                            if (book.toString().contains("isbn13")) {
                                doubanMeta.setIsbn13(jsonobject.getString("isbn13"));
                            }
                            if (book.toString().contains("pubdate")) {
                                doubanMeta.setIssueddate(jsonobject.getString("pubdate"));
                            }
                            if (book.toString().contains("pages")) {
                                doubanMeta.setPages(jsonobject.getString("pages"));
                            }
                            if (book.toString().contains("price")) {
                                doubanMeta.setPrice(jsonobject.getString("price"));
                            }
                            if (book.toString().contains("binding")) {
                                doubanMeta.setBinding(jsonobject.getString("binding").replace("'", "''"));
                            }
                            if (book.toString().contains("series\":")) {
                                doubanMeta.setSeries(JSONObject.fromObject(jsonobject.getString("series")).getString("title").replace("'", "''"));
                            }
                            if (book.toString().contains("rating")) {
                                doubanMeta.setAverage(JSONObject.fromObject(jsonobject.getString("rating")).getString("average").replace("'", "''"));
                            }
                            if (book.toString().contains("summary")) {
                                doubanMeta.setSummary(jsonobject.getString("summary").replace("'", "''"));
                            }
                            if (book.toString().contains("author_intro")) {
                                doubanMeta.setAuthorIntro(jsonobject.getString("author_intro").replace("'", "''"));
                            }
                            if (book.toString().contains("catalog")) {
                                doubanMeta.setCatalog(jsonobject.getString("catalog").replace("'", "''"));
                            }
                            if (book.toString().contains("tags")) {
                                String tags = "";
                                for (Object titles : jsonobject.getJSONArray("tags")) {
                                    tags += JSONObject.fromObject(titles).getString("title") + " ";
                                }
                                doubanMeta.setTags(tags.replace("'", "''"));
                            }
                            if (book.toString().contains("images")) {
                                doubanMeta.setSmallCover(jsonobject.getJSONObject("images").getString("small"));
                                doubanMeta.setLargeCover(jsonobject.getJSONObject("images").getString("large"));
                                doubanMeta.setMediumCover(jsonobject.getJSONObject("images").getString("medium"));
                            }
                            if (book.toString().contains("ebook_price")) {
                                doubanMeta.setEbookPrice(jsonobject.getString("ebook_price"));
                            }
                            if (book.toString().contains("origin_title")) {
                                doubanMeta.setOriginTitle(jsonobject.getString("origin_title").replace("'", "''"));
                            }
                            // 将amazon数据merge到doubanMeta
                            doubanMeta = BeanTransformUtil.mergeDoubanWithAmazon(doubanMeta, amazonMeta);
                            // 将爬取到的数据添加到豆瓣数据库中
                            doubanMeta.setCreateTime(new Date());
                            doubanMeta.setUpdateTime(new Date());
                            // 对出版日期进行清洗
                            if (StringUtils.isNotEmpty(doubanMeta.getIssueddate())) {
                                String s = StringToolUtil.issuedDateFormat(doubanMeta.getIssueddate());
                                if (s.contains(" 00:00:00")) {
                                    s = s.replaceAll(" 00:00:00", "");
                                }
                                doubanMeta.setIssueddate(s);
                            }
                            // 将douban抓取到的数据添加到douban表
                            doubanMetaDao.insert(doubanMeta);
                            // 如果issuedDate字段不为null，则把douban的数据添加到temp表
                            if (doubanMeta.getIssueddate() != null && !"".equalsIgnoreCase(doubanMeta.getIssueddate())) {
                                ApabiBookMetaDataTemp apabiBookMetaTemp = BeanTransformUtil.transform2ApabiBookMetaTemp(doubanMeta);
                                // 设置新插入数据的创建时间和更新时间
                                if (apabiBookMetaTempList == null || apabiBookMetaTempList.size() == 0) {
                                    apabiBookMetaTemp.setCreateTime(new Date());
                                    apabiBookMetaTemp.setUpdateTime(new Date());
                                } else {
                                    Date createTime = apabiBookMetaTempList.get(0).getCreateTime();
                                    Date updateTime = apabiBookMetaTempList.get(0).getUpdateTime();
                                    if (createTime != null) {
                                        apabiBookMetaTemp.setCreateTime(createTime);
                                    } else {
                                        apabiBookMetaTemp.setCreateTime(new Date());
                                    }
                                    if (updateTime != null) {
                                        apabiBookMetaTemp.setUpdateTime(updateTime);
                                    } else {
                                        apabiBookMetaTemp.setUpdateTime(new Date());
                                    }
                                }
                                // 根据isbn13设置isbn
                                String isbn = null;
                                if (apabiBookMetaTemp.getIsbn13() != null && (apabiBookMetaTemp.getIsbn() == null || "".equalsIgnoreCase(apabiBookMetaTemp.getIsbn()))) {
                                    isbn = Isbn13ToIsbnUtil.transform(apabiBookMetaTemp.getIsbn13());
                                }
                                if (apabiBookMetaTemp.getIsbn() != null && !"".equalsIgnoreCase(apabiBookMetaTemp.getIsbn())) {
                                    isbn = apabiBookMetaTemp.getIsbn();
                                }
                                apabiBookMetaTemp.setIsbn(isbn);
                                if (amazonMeta != null) {
                                    // 如果amazon的数据不为空，将amazon的数据内容补充到apabiBookMetaTemp中
                                    apabiBookMetaTemp = BeanTransformUtil.mergeApabiBookMetaTempWithAmazon(apabiBookMetaTemp, amazonMeta);
                                }
                                // 设置apabiBookMetaTemp是否发布为0
                                apabiBookMetaTemp.setHasPublish(0);
                                apabiBookMetaTempReturnedList.add(apabiBookMetaTemp);
                                // 判断并设置是否发布
                                if (apabiBookMetaTempList != null && apabiBookMetaTempList.size() > 0) {
                                    if (apabiBookMetaTempList.get(0).getHasPublish() == null) {
                                        apabiBookMetaTemp.setHasPublish(0);
                                    } else {
                                        apabiBookMetaTemp.setHasPublish(apabiBookMetaTempList.get(0).getHasPublish());
                                    }
                                }
                                // 只有当temp表中没有该数据，才向temp中添加该数据
                                if (apabiBookMetaTempList == null || apabiBookMetaTempList.size() == 0) {
                                    // 由于temp表中没有数据，则没有发布；且当前时间为创建时间和更新时间
                                    apabiBookMetaTemp.setUpdateTime(new Date());
                                    apabiBookMetaTemp.setCreateTime(new Date());
                                    apabiBookMetaTemp.setHasPublish(0);
                                    apabiBookMetaTemp.setHasCebx(hasCebx);
                                    apabiBookMetaTemp.setHasFlow(hasFlow);
                                    apabiBookMetaDataTempDao.insert(apabiBookMetaTemp);
                                }
                            }
                            Thread.sleep(1000 * 3);
                        } else {
                            // 如果douban数据抓取不到，则以amazon的数据为主
                            if (amazonMeta != null) {
                                doubanMeta = BeanTransformUtil.transform2DoubanMetaFromAmazon(amazonMeta);
                                ApabiBookMetaDataTemp apabiBookMetaTemp = BeanTransformUtil.transform2ApabiBookMetaTemp(doubanMeta);
                                if (doubanMeta.getIssueddate() != null && !"".equalsIgnoreCase(doubanMeta.getIssueddate())) {
                                    String s = StringToolUtil.issuedDateFormat(doubanMeta.getIssueddate());
                                    if (s.contains(" 00:00:00")) {
                                        s = s.replace(" 00:00:00", "");
                                    }
                                    apabiBookMetaTemp.setIssuedDate(s);
                                }
                                // 将amazon的数据补充到apabiBookMetaTemp
                                if (apabiBookMetaTempList != null && apabiBookMetaTempList.size() > 0) {
                                    apabiBookMetaTemp = BeanTransformUtil.mergeApabiBookMetaTempWithAmazon(apabiBookMetaTemp, amazonMeta);
                                    apabiBookMetaTemp.setHasPublish(0);
                                    apabiBookMetaTemp.setDataSource("amazon");
                                    apabiBookMetaTemp.setMetaId(metaId);
                                    apabiBookMetaTemp.setHasFlow(hasFlow);
                                    apabiBookMetaTemp.setHasCebx(hasCebx);
                                    Date updateTime = apabiBookMetaTempList.get(0).getUpdateTime();
                                    Date createTime = apabiBookMetaTempList.get(0).getCreateTime();
                                    apabiBookMetaTemp.setUpdateTime(updateTime);
                                    apabiBookMetaTemp.setCreateTime(createTime);
                                } else {
                                    // 只有temp表中没有该数据，才向表中添加该数据
                                    apabiBookMetaTemp.setHasPublish(0);
                                    apabiBookMetaTemp.setUpdateTime(new Date());
                                    apabiBookMetaTemp.setCreateTime(new Date());
                                    apabiBookMetaTemp.setHasCebx(hasCebx);
                                    apabiBookMetaTemp.setHasFlow(hasFlow);
                                    apabiBookMetaTemp.setDataSource("amazon");
                                    apabiBookMetaTemp.setAmazonId(amazonMeta.getAmazonId());
                                    apabiBookMetaTemp.setAbstract_(amazonMeta.getAbstract_());
                                    apabiBookMetaTemp.setTranslator(amazonMeta.getTranslator());
                                    apabiBookMetaTemp.setPostScript(amazonMeta.getPostScript());
                                    apabiBookMetaTemp.setPreface(amazonMeta.getPreface());
                                    apabiBookMetaTemp.setOriginTitle(amazonMeta.getOriginTitle());
                                    apabiBookMetaDataTempDao.insert(apabiBookMetaTemp);
                                }
                                apabiBookMetaTempReturnedList.add(apabiBookMetaTemp);
                            }
                        }
                        // 如果查询不到结果，则将展示结果定义为---
                        if (doubanMeta == null && (apabiBookMetaTempReturnedList == null || apabiBookMetaTempReturnedList.size() == 0)) {
                            ApabiBookMetaDataTemp apabiBookMetaTemp = new ApabiBookMetaDataTemp();
                            apabiBookMetaTemp.setMetaId("---");
                            apabiBookMetaTemp.setIsbn13(isbn13);
                            apabiBookMetaTemp.setIsbn("---");
                            apabiBookMetaTemp.setTitle("---");
                            apabiBookMetaTemp.setCreator("---");
                            apabiBookMetaTemp.setPublisher("---");
                            apabiBookMetaTemp.setIssuedDate("---");
                            apabiBookMetaTemp.setUpdateTime(null);
                            apabiBookMetaTemp.setHasCebx(0);
                            apabiBookMetaTemp.setHasFlow(0);
                            apabiBookMetaTemp.setPaperPrice("---");
                            apabiBookMetaTempReturnedList.add(apabiBookMetaTemp);
                        }
                        // 如果在douban中抓取到数据，且apabiBookMetaTempReturnedList中没有数据，则将douban数据转化为temp数据，并添加到展示列表
                        if (doubanMeta != null && StringUtils.isNotEmpty(doubanMeta.getDoubanId()) && (apabiBookMetaTempReturnedList == null || apabiBookMetaTempReturnedList.size() == 0)) {
                            ApabiBookMetaDataTemp apabiBookMetaTemp = BeanTransformUtil.transform2ApabiBookMetaTemp(doubanMeta);
                            apabiBookMetaTempList.add(apabiBookMetaTemp);
                        }
                        return apabiBookMetaTempReturnedList;
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.info("获取数据发生异常，【异常信息】：" + e.getMessage());
                        return apabiBookMetaTempReturnedList;
                    }
                }
            }
        }
        return apabiBookMetaTempReturnedList;
    }

    @Override
    public void reUpdateDoubanByCrawl() {
        int count = doubanMetaDao.countShouldUpdate();
        int pageSize = 3000;
        int pageNum = (count / pageSize) + 1;
        ExecutorService executorService = Executors.newFixedThreadPool(100);
        for (int i = 1; i <= pageNum; i++) {
            NlcIpPoolUtils nlcIpPoolUtils = new NlcIpPoolUtils();
            PageHelper.startPage(i, pageSize);
            Page<DoubanMeta> doubanMetaList = doubanMetaDao.findShouldUpdate();
            int listSize = doubanMetaList.size();
            LinkedBlockingQueue<DoubanMeta> doubanMetaQueue = new LinkedBlockingQueue<>(doubanMetaList);
            CountDownLatch countDownLatch = new CountDownLatch(listSize);
            ReUpdateDoubanMetaConsumer consumer = new ReUpdateDoubanMetaConsumer(doubanMetaDao,doubanMetaQueue,countDownLatch,nlcIpPoolUtils);
            for (int j = 0; j < listSize; j++) {
                executorService.execute(consumer);
            }
            try {
                countDownLatch.await();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();
    }

    @Override
    public List<ApabiBookMetaDataTemp> searchMetaDataTempsByISBN(String isbn13) {
        // 创建ApabiBookMetaTempReturnedList
        List<ApabiBookMetaDataTemp> apabiBookMetaTempReturnedList = new ArrayList<>();

        if (!StringUtils.isEmpty(isbn13)) {
            // 对isbn13进行清洗
            if (isbn13.contains("-")) {
                isbn13 = isbn13.replace("-", "");
            }
            // 查询temp表中是否有该isbn的数据
            List<ApabiBookMetaDataTemp> apabiBookMetaTempList = apabiBookMetaDataTempDao.findByIsbn13(isbn13);
            String metaId = "";
            if (apabiBookMetaTempList != null && apabiBookMetaTempList.size() > 0) {
                metaId = apabiBookMetaTempList.get(0).getMetaId();
            }
            // 查询meta_data表中是否有该数据
            List<ApabiBookMetaData> apabiBookMetaList = apabiBookMetaDataDao.findByIsbn13(isbn13);
            // 首先查出meta_data表中hasFlow和hasCebx字段的值
            Integer hasFlow = 0;
            Integer hasCebx = 0;
            if (apabiBookMetaList != null && apabiBookMetaList.size() > 0) {
                if (apabiBookMetaList.get(0).getHasFlow() != null) {
                    hasFlow = apabiBookMetaList.get(0).getHasFlow();
                }
                if (apabiBookMetaList.get(0).getHasCebx() != null) {
                    hasCebx = apabiBookMetaList.get(0).getHasCebx();
                }
            }
            // 如果在meta_data库中查询到直接返回
            if (apabiBookMetaList != null && apabiBookMetaList.size() != 0) {
                // 遍历apabiBookMetaList
                for (int i = 0; i < apabiBookMetaList.size(); i++) {
                    ApabiBookMetaData apabiBookMeta = apabiBookMetaList.get(i);
                    if (apabiBookMeta != null) {
                        // 如果apabiBookMeta不为null，则将apabiBookMeta的属性值拷贝到新创建的apabiBookMetaTemp
                        ApabiBookMetaDataTemp apabiBookMetaTemp = new ApabiBookMetaDataTemp();
                        BeanUtils.copyProperties(apabiBookMeta, apabiBookMetaTemp);
                        // 将apabiBookMetaTemp添加到返回结果列表
                        apabiBookMetaTempReturnedList.add(apabiBookMetaTemp);
                    }
                }
                // 直接返回
                return apabiBookMetaTempReturnedList;
            } else {
                // 如果meta_data库中没有数据，则去douban库查询
                List<DoubanMeta> doubanMetaSearchList = doubanMetaDao.findByIsbn13(isbn13);
                // 不论在豆瓣上有没有检索到,都首先查询amazon数据,没有数据则抓取amazon页面
                AmazonMeta amazonMeta = null;
                try {
                    amazonMeta = amazonMetaService.findOrCrawlAmazonMetaByIsbn(isbn13);
                } catch (Exception e) {
                    // 查询或者抓取amazon数据，如果catch住异常，则不做处理
                    e.printStackTrace();
                    log.info("抓取amazon，处理" + isbn13 + "出错...");
                    log.info(e.getMessage());

                }
                // 如果豆瓣库中有，则直接返回
                if (doubanMetaSearchList != null && doubanMetaSearchList.size() > 0) {
                    for (DoubanMeta doubanMeta : doubanMetaSearchList) {
                        if (doubanMeta.getIssueddate() != null && "".equalsIgnoreCase("")) {
                            ApabiBookMetaDataTemp apabiBookMetaTemp = BeanTransformUtil.transform2ApabiBookMetaTemp(doubanMeta);
                            if (StringUtils.isNotEmpty(metaId)) {
                                apabiBookMetaTemp.setMetaId(metaId);
                            }
                            // 把该数据写入到temp库中
                            if (doubanMeta.getIssueddate() != null) {
                                // 根据查询到的temp表中的数据设置apabiBookMetaTemp的更新时间和创建时间
                                if (apabiBookMetaTempList != null && apabiBookMetaTempList.size() > 0 && apabiBookMetaTempList.get(0).getCreateTime() != null) {
                                    apabiBookMetaTemp.setCreateTime(apabiBookMetaTempList.get(0).getCreateTime());
                                } else {
                                    apabiBookMetaTemp.setCreateTime(new Date());
                                }
                                if (apabiBookMetaTempList != null && apabiBookMetaTempList.size() > 0 && apabiBookMetaTempList.get(0).getUpdateTime() != null) {
                                    apabiBookMetaTemp.setUpdateTime(apabiBookMetaTempList.get(0).getUpdateTime());
                                } else {
                                    apabiBookMetaTemp.setUpdateTime(new Date());
                                }
                                if (amazonMeta != null) {
                                    apabiBookMetaTemp = BeanTransformUtil.mergeApabiBookMetaTempWithAmazon(apabiBookMetaTemp, amazonMeta);
                                }

                                // 判断并设置是否发布
                                if (apabiBookMetaTempList != null && apabiBookMetaTempList.size() > 0) {
                                    if (apabiBookMetaTempList.get(0).getHasPublish() == null) {
                                        apabiBookMetaTemp.setHasPublish(0);
                                    } else {
                                        apabiBookMetaTemp.setHasPublish(apabiBookMetaTempList.get(0).getHasPublish());
                                    }
                                }
                                // 当temp表中没有该数据，才将其插入到temp表中
                                if (apabiBookMetaTempList == null || apabiBookMetaTempList.size() == 0) {
                                    apabiBookMetaTemp.setCreateTime(new Date());
                                    apabiBookMetaTemp.setUpdateTime(new Date());
                                    apabiBookMetaTemp.setHasPublish(0);
                                    apabiBookMetaTemp.setHasCebx(hasCebx);
                                    apabiBookMetaTemp.setHasFlow(hasFlow);
                                    apabiBookMetaDataTempDao.insert(apabiBookMetaTemp);
                                }
                            }
                            apabiBookMetaTempReturnedList.add(apabiBookMetaTemp);
                        } else {
                            ApabiBookMetaDataTemp apabiBookMetaTemp = BeanTransformUtil.transform2ApabiBookMetaTemp(doubanMeta);
                            apabiBookMetaTemp.setMetaId(metaId);
                            apabiBookMetaTemp.setUpdateTime(doubanMeta.getUpdateTime());
                            apabiBookMetaTemp.setCreateTime(doubanMeta.getCreateTime());
                            apabiBookMetaTemp.setHasFlow(hasFlow);
                            apabiBookMetaTemp.setHasCebx(hasCebx);
                            apabiBookMetaTempReturnedList.add(apabiBookMetaTemp);
                        }
                    }
                    return apabiBookMetaTempReturnedList;
                } else {
                    // 如果douban表中没有，则取douban抓取
                    DoubanMeta doubanMeta = new DoubanMeta();
                    String resUrl = "https://api.douban.com/v2/book/isbn/" + isbn13;
                    try {
                        // 发送请求，返回Json数据
                        JSONObject book = getJson(resUrl);
                        // 如果能爬取到douban的数据，以douban的数据为主！
                        if (book != null) {
                            // 创建douban数据实例
                            // 将返回的Json数据实例Json对象
                            JSONObject jsonobject = JSONObject.fromObject(book);
                            // 设置DouBan实例对象
                            if (book.toString().contains("id")) {
                                doubanMeta.setDoubanId(jsonobject.getString("id"));
                            }
                            if (book.toString().contains("title")) {
                                doubanMeta.setTitle(jsonobject.getString("title").replace("'", "''"));
                            }
                            if (book.toString().contains("author")) {
                                if (jsonobject.getJSONArray("author").size() != 0) {
                                    doubanMeta.setAuthor(jsonobject.getJSONArray("author").getString(0).replace("'", "''"));
                                }
                            }
                            if (book.toString().contains("publisher")) {
                                doubanMeta.setPublisher(jsonobject.getString("publisher").replace("'", "''"));
                            }
                            if (book.toString().contains("alt_title")) {
                                doubanMeta.setAltTitle(jsonobject.getString("alt_title").replace("'", "''"));
                            }
                            if (book.toString().contains("subtitle")) {
                                doubanMeta.setSubtitle(jsonobject.getString("subtitle").replace("'", "''"));
                            }
                            if (book.toString().contains("translator")) {
                                if (jsonobject.getJSONArray("translator").size() != 0) {
                                    doubanMeta.setTranslator(jsonobject.getJSONArray("translator").getString(0).replace("'", "''"));
                                }
                            }
                            if (book.toString().contains("isbn10")) {
                                doubanMeta.setIsbn10(jsonobject.getString("isbn10"));
                            }
                            if (book.toString().contains("isbn13")) {
                                doubanMeta.setIsbn13(jsonobject.getString("isbn13"));
                            }
                            if (book.toString().contains("pubdate")) {
                                doubanMeta.setIssueddate(jsonobject.getString("pubdate"));
                            }
                            if (book.toString().contains("pages")) {
                                doubanMeta.setPages(jsonobject.getString("pages"));
                            }
                            if (book.toString().contains("price")) {
                                doubanMeta.setPrice(jsonobject.getString("price"));
                            }
                            if (book.toString().contains("binding")) {
                                doubanMeta.setBinding(jsonobject.getString("binding").replace("'", "''"));
                            }
                            if (book.toString().contains("series\":")) {
                                doubanMeta.setSeries(JSONObject.fromObject(jsonobject.getString("series")).getString("title").replace("'", "''"));
                            }
                            if (book.toString().contains("rating")) {
                                doubanMeta.setAverage(JSONObject.fromObject(jsonobject.getString("rating")).getString("average").replace("'", "''"));
                            }
                            if (book.toString().contains("summary")) {
                                doubanMeta.setSummary(jsonobject.getString("summary").replace("'", "''"));
                            }
                            if (book.toString().contains("author_intro")) {
                                doubanMeta.setAuthorIntro(jsonobject.getString("author_intro").replace("'", "''"));
                            }
                            if (book.toString().contains("catalog")) {
                                doubanMeta.setCatalog(jsonobject.getString("catalog").replace("'", "''"));
                            }
                            if (book.toString().contains("tags")) {
                                String tags = "";
                                for (Object titles : jsonobject.getJSONArray("tags")) {
                                    tags += JSONObject.fromObject(titles).getString("title") + " ";
                                }
                                doubanMeta.setTags(tags.replace("'", "''"));
                            }
                            if (book.toString().contains("images")) {
                                doubanMeta.setSmallCover(jsonobject.getJSONObject("images").getString("small"));
                                doubanMeta.setLargeCover(jsonobject.getJSONObject("images").getString("large"));
                                doubanMeta.setMediumCover(jsonobject.getJSONObject("images").getString("medium"));
                            }
                            if (book.toString().contains("ebook_price")) {
                                doubanMeta.setEbookPrice(jsonobject.getString("ebook_price"));
                            }
                            if (book.toString().contains("origin_title")) {
                                doubanMeta.setOriginTitle(jsonobject.getString("origin_title").replace("'", "''"));
                            }
                            // 将amazon数据merge到doubanMeta
                            doubanMeta = BeanTransformUtil.mergeDoubanWithAmazon(doubanMeta, amazonMeta);
                            // 将爬取到的数据添加到豆瓣数据库中
                            doubanMeta.setCreateTime(new Date());
                            doubanMeta.setUpdateTime(new Date());
                            // 对出版日期进行清洗
                            if (StringUtils.isNotEmpty(doubanMeta.getIssueddate())) {
                                String s = StringToolUtil.issuedDateFormat(doubanMeta.getIssueddate());
                                if (s.contains(" 00:00:00")) {
                                    s = s.replaceAll(" 00:00:00", "");
                                }
                                doubanMeta.setIssueddate(s);
                            }
                            // 将douban抓取到的数据添加到douban表
                            doubanMetaDao.insert(doubanMeta);
                            // 如果issuedDate字段不为null，则把douban的数据添加到temp表
                            if (doubanMeta.getIssueddate() != null && !"".equalsIgnoreCase(doubanMeta.getIssueddate())) {
                                ApabiBookMetaDataTemp apabiBookMetaTemp = BeanTransformUtil.transform2ApabiBookMetaTemp(doubanMeta);
                                // 设置新插入数据的创建时间和更新时间
                                if (apabiBookMetaTempList == null || apabiBookMetaTempList.size() == 0) {
                                    apabiBookMetaTemp.setCreateTime(new Date());
                                    apabiBookMetaTemp.setUpdateTime(new Date());
                                } else {
                                    Date createTime = apabiBookMetaTempList.get(0).getCreateTime();
                                    Date updateTime = apabiBookMetaTempList.get(0).getUpdateTime();
                                    if (createTime != null) {
                                        apabiBookMetaTemp.setCreateTime(createTime);
                                    } else {
                                        apabiBookMetaTemp.setCreateTime(new Date());
                                    }
                                    if (updateTime != null) {
                                        apabiBookMetaTemp.setUpdateTime(updateTime);
                                    } else {
                                        apabiBookMetaTemp.setUpdateTime(new Date());
                                    }
                                }
                                // 根据isbn13设置isbn
                                String isbn = null;
                                if (apabiBookMetaTemp.getIsbn13() != null && (apabiBookMetaTemp.getIsbn() == null || "".equalsIgnoreCase(apabiBookMetaTemp.getIsbn()))) {
                                    isbn = Isbn13ToIsbnUtil.transform(apabiBookMetaTemp.getIsbn13());
                                }
                                if (apabiBookMetaTemp.getIsbn() != null && !"".equalsIgnoreCase(apabiBookMetaTemp.getIsbn())) {
                                    isbn = apabiBookMetaTemp.getIsbn();
                                }
                                apabiBookMetaTemp.setIsbn(isbn);
                                if (amazonMeta != null) {
                                    // 如果amazon的数据不为空，将amazon的数据内容补充到apabiBookMetaTemp中
                                    apabiBookMetaTemp = BeanTransformUtil.mergeApabiBookMetaTempWithAmazon(apabiBookMetaTemp, amazonMeta);
                                }
                                // 设置apabiBookMetaTemp是否发布为0
                                apabiBookMetaTemp.setHasPublish(0);
                                apabiBookMetaTempReturnedList.add(apabiBookMetaTemp);
                                // 判断并设置是否发布
                                if (apabiBookMetaTempList != null && apabiBookMetaTempList.size() > 0) {
                                    if (apabiBookMetaTempList.get(0).getHasPublish() == null) {
                                        apabiBookMetaTemp.setHasPublish(0);
                                    } else {
                                        apabiBookMetaTemp.setHasPublish(apabiBookMetaTempList.get(0).getHasPublish());
                                    }
                                }
                                // 只有当temp表中没有该数据，才向temp中添加该数据
                                if (apabiBookMetaTempList == null || apabiBookMetaTempList.size() == 0) {
                                    // 由于temp表中没有数据，则没有发布；且当前时间为创建时间和更新时间
                                    apabiBookMetaTemp.setUpdateTime(new Date());
                                    apabiBookMetaTemp.setCreateTime(new Date());
                                    apabiBookMetaTemp.setHasPublish(0);
                                    apabiBookMetaTemp.setHasCebx(hasCebx);
                                    apabiBookMetaTemp.setHasFlow(hasFlow);
                                    apabiBookMetaDataTempDao.insert(apabiBookMetaTemp);
                                }
                            }
                            Thread.sleep(1000 * 3);
                        } else {
                            // 如果douban数据抓取不到，则以amazon的数据为主
                            if (amazonMeta != null) {
                                doubanMeta = BeanTransformUtil.transform2DoubanMetaFromAmazon(amazonMeta);
                                ApabiBookMetaDataTemp apabiBookMetaTemp = BeanTransformUtil.transform2ApabiBookMetaTemp(doubanMeta);
                                if (doubanMeta.getIssueddate() != null && !"".equalsIgnoreCase(doubanMeta.getIssueddate())) {
                                    String s = StringToolUtil.issuedDateFormat(doubanMeta.getIssueddate());
                                    if (s.contains(" 00:00:00")) {
                                        s = s.replace(" 00:00:00", "");
                                    }
                                    apabiBookMetaTemp.setIssuedDate(s);
                                }
                                // 将amazon的数据补充到apabiBookMetaTemp
                                if (apabiBookMetaTempList != null && apabiBookMetaTempList.size() > 0) {
                                    apabiBookMetaTemp = BeanTransformUtil.mergeApabiBookMetaTempWithAmazon(apabiBookMetaTemp, amazonMeta);
                                    apabiBookMetaTemp.setHasPublish(0);
                                    apabiBookMetaTemp.setDataSource("amazon");
                                    apabiBookMetaTemp.setMetaId(metaId);
                                    apabiBookMetaTemp.setHasFlow(hasFlow);
                                    apabiBookMetaTemp.setHasCebx(hasCebx);
                                    Date updateTime = apabiBookMetaTempList.get(0).getUpdateTime();
                                    Date createTime = apabiBookMetaTempList.get(0).getCreateTime();
                                    apabiBookMetaTemp.setUpdateTime(updateTime);
                                    apabiBookMetaTemp.setCreateTime(createTime);
                                } else {
                                    // 只有temp表中没有该数据，才向表中添加该数据
                                    apabiBookMetaTemp.setHasPublish(0);
                                    apabiBookMetaTemp.setUpdateTime(new Date());
                                    apabiBookMetaTemp.setCreateTime(new Date());
                                    apabiBookMetaTemp.setHasCebx(hasCebx);
                                    apabiBookMetaTemp.setHasFlow(hasFlow);
                                    apabiBookMetaTemp.setDataSource("amazon");
                                    apabiBookMetaTemp.setAmazonId(amazonMeta.getAmazonId());
                                    apabiBookMetaTemp.setAbstract_(amazonMeta.getAbstract_());
                                    apabiBookMetaTemp.setTranslator(amazonMeta.getTranslator());
                                    apabiBookMetaTemp.setPostScript(amazonMeta.getPostScript());
                                    apabiBookMetaTemp.setPreface(amazonMeta.getPreface());
                                    apabiBookMetaTemp.setOriginTitle(amazonMeta.getOriginTitle());
                                    apabiBookMetaDataTempDao.insert(apabiBookMetaTemp);
                                }
                                apabiBookMetaTempReturnedList.add(apabiBookMetaTemp);
                            }
                        }
                        // 如果查询不到结果，则将展示结果定义为---
                        if (doubanMeta == null && (apabiBookMetaTempReturnedList == null || apabiBookMetaTempReturnedList.size() == 0)) {
                            ApabiBookMetaDataTemp apabiBookMetaTemp = new ApabiBookMetaDataTemp();
                            apabiBookMetaTemp.setMetaId("---");
                            apabiBookMetaTemp.setIsbn13(isbn13);
                            apabiBookMetaTemp.setIsbn("---");
                            apabiBookMetaTemp.setTitle("---");
                            apabiBookMetaTemp.setCreator("---");
                            apabiBookMetaTemp.setPublisher("---");
                            apabiBookMetaTemp.setIssuedDate("---");
                            apabiBookMetaTemp.setUpdateTime(null);
                            apabiBookMetaTemp.setHasCebx(0);
                            apabiBookMetaTemp.setHasFlow(0);
                            apabiBookMetaTemp.setPaperPrice("---");
                            apabiBookMetaTempReturnedList.add(apabiBookMetaTemp);
                        }
                        // 如果在douban中抓取到数据，且apabiBookMetaTempReturnedList中没有数据，则将douban数据转化为temp数据，并添加到展示列表
                        if (doubanMeta != null && (apabiBookMetaTempReturnedList == null || apabiBookMetaTempReturnedList.size() == 0)) {
                            ApabiBookMetaDataTemp apabiBookMetaTemp = BeanTransformUtil.transform2ApabiBookMetaTemp(doubanMeta);
                            apabiBookMetaTempList.add(apabiBookMetaTemp);
                        }
                        return apabiBookMetaTempReturnedList;
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.info("获取数据发生异常，【异常信息】：" + e.getMessage());
                    }
                }
                return apabiBookMetaTempReturnedList;
            }
        }
        return apabiBookMetaTempReturnedList;
    }

    @Override
    public DoubanMeta searchDoubanMetaById(String doubanId) {
        if (StringUtils.isNotEmpty(doubanId)) {
            return doubanMetaDao.findById(doubanId);
        }
        return null;
    }

    @Override
    public Page<DoubanMeta> searchDoubanMetaByPage(Map<String, String> params) {
        return doubanMetaDao.findByPage(params);
    }

    @Override
    public void addDoubanMeta(DoubanMeta doubanMeta) {
        doubanMetaDao.insert(doubanMeta);
    }

    @Override
    public void deleteDoubanMeta(String doubanId) {
        doubanMetaDao.deleteById(doubanId);
    }

    @Override
    public void updateDoubanMeta(DoubanMeta doubanMeta) {
        doubanMetaDao.update(doubanMeta);
    }

    private static HttpEntity getEntityFromResponse(HttpResponse response) {
        return response.getStatusLine().getStatusCode() == 200 ? response.getEntity() : null;
    }

    private JSONObject getJson(String resUrl) {
        JSONObject jsonObjects = null;
        try {
            HttpResponse response = HttpUtils2.doGetEntity(resUrl);
            if (response.getStatusLine().getStatusCode() == 404) {
                System.out.println("ISBN:" + resUrl.substring(resUrl.lastIndexOf("/"), resUrl.length()) + "不存在...");
                jsonObjects = null;
            }
            if (response.getStatusLine().getStatusCode() == 200) {
                String sr = EntityUtils.toString(getEntityFromResponse(response));
                jsonObjects = JSONObject.fromObject(sr);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info(e.getMessage());
        }
        return jsonObjects;
    }

    private JSONObject getJson2(String resUrl) {
        JSONObject jsonObjects = null;
        IpPoolUtils ipPoolUtils = new IpPoolUtils();
        int retryCount = 0;
        int statusCode = 404;
        HttpResponse response = null;
        String host = ipPoolUtils.getIp();
        String ip = host.split(":")[0];
        String port = host.split(":")[1];
        while (true) {
            retryCount++;
            if (retryCount > 5) {
                break;
            }
            try {
                response = HttpUtils2.doGetEntity(resUrl, ip, port);
                statusCode = response.getStatusLine().getStatusCode();
                if (statusCode == HttpStatus.SC_OK) {
                    String sr = EntityUtils.toString(getEntityFromResponse(response));
                    jsonObjects = JSONObject.fromObject(sr);
                    break;
                }
                if (statusCode == HttpStatus.SC_NOT_FOUND) {
                    System.out.println("ISBN:" + resUrl.substring(resUrl.lastIndexOf("/") + 1, resUrl.length()) + "不存在...");
                    break;
                }
            } catch (Exception e) {
                host = ipPoolUtils.getIp();
                ip = host.split(":")[0];
                port = host.split(":")[1];
            }
        }
        return jsonObjects;
    }

    public static void main(String[] args) {
        DoubanMetaServiceImpl doubanMetaService = new DoubanMetaServiceImpl();
        String isbn13 = "978-7-5375-9116-4";
        JSONObject json = doubanMetaService.getJson2("https://api.douban.com/v2/book/isbn/" + isbn13);
        System.out.println(json);
    }
}
