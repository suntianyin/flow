package com.apabi.flow.douban.service.impl;

import com.apabi.flow.douban.dao.AmazonMetaDao;
import com.apabi.flow.douban.model.AmazonMeta;
import com.apabi.flow.douban.service.AmazonMetaService;
import com.apabi.flow.douban.util.DomParseUtil;
import com.apabi.flow.douban.util.StringToolUtil;
import org.apache.commons.lang.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @Author pipi
 * @Date 2018/9/4 14:56
 **/
@Service
public class AmazonMetaServiceImpl implements AmazonMetaService {

    @Autowired
    private AmazonMetaDao amazonMetaDao;

    /**
     * 根据isbn在表AMAZON_METADATA中查询数据，如果没有，则去AMAZON爬取
     *
     * @param isbn 传入isbn参数
     * @return AmazonMeta实体
     * @throws NullPointerException 如果在爬取过程中解析不到Document则抛出该异常
     * @throws Exception            如果爬取过程中报错，则抛出该异常
     */
    @Override
    public AmazonMeta findOrCrawlAmazonMetaByIsbn(String isbn) throws NullPointerException, Exception {
        if (!StringUtils.isEmpty(isbn)) {
            // 如果在数据库中查询到该数据，则直接返回
            AmazonMeta amazonMeta = null;
            if (isbn.contains("-")) {
                isbn = isbn.replaceAll("-", "");
            }
            if (isbn.length() == 10) {
                amazonMeta = amazonMetaDao.getAmazonMetaByIsbn10(isbn);
            }
            if (isbn.length() == 13) {
                amazonMeta = amazonMetaDao.getAmazonMetaByIsbn13(isbn);
            }
            if (amazonMeta == null) {
                // 如果在表中查询不到数据，则去爬取
                amazonMeta = crawl(isbn);
                if (amazonMeta != null) {
                    String issuedDate = amazonMeta.getIssuedDate();
                    issuedDate = StringToolUtil.issuedDateFormat(issuedDate);
                    if (issuedDate.contains(" 00:00:00")) {
                        issuedDate = issuedDate.replaceAll(" 00:00:00", "");
                    }
                    amazonMeta.setIssuedDate(issuedDate);
                    String amazonId = amazonMeta.getAmazonId();
                    // 根据amazonId在AMAZON_METADATA表中查询AmazonMeta
                    AmazonMeta amazonMetaByAmazonId = amazonMetaDao.getAmazonMetaByAmazonId(amazonId);
                    // 如果amazonId已经存在，则直接返回；不存在则将爬取到的数据入库
                    if (amazonMetaByAmazonId != null) {
                        if (amazonMeta.getPostScript() != null && amazonMetaByAmazonId.getPostScript() == null) {
                            amazonMetaDao.updateAmazonMeta(amazonMeta);
                        }
                        System.out.println(amazonMeta);
                        System.out.println(amazonMetaByAmazonId);
                        // 设置isbn字段的值
                        if (isbn.length() == 13) {
                            amazonMetaByAmazonId.setIsbn13(isbn);
                        } else if (isbn.length() == 10) {
                            amazonMetaByAmazonId.setIsbn10(isbn);
                        }
                        return amazonMetaByAmazonId;
                    } else {
                        // 第一次入库设置创建时间
                        amazonMeta.setCreateTime(new Date());
                        // 第一次入库设置更新时间
                        amazonMeta.setUpdateTime(new Date());
                        // 将是否爬取的标记设置为1
                        amazonMeta.setHasCrawled(1);
                        // 入库之前设置isbn字段的值
                        if (isbn.length() == 13) {
                            amazonMeta.setIsbn13(isbn);
                        } else if (isbn.length() == 10) {
                            amazonMeta.setIsbn10(isbn);
                        }
                        // 由于数据库中没有该数据，故将爬取的数据写入到数据库
                        amazonMetaDao.addAmazonMeta(amazonMeta);
                        return amazonMeta;
                    }
                }
            }
            return amazonMeta;
        }
        return null;
    }

    // 根据amazonId更新数据
    @Override
    public void updateAmazon(AmazonMeta amazonMeta) {
        amazonMetaDao.updateAmazonMeta(amazonMeta);
    }


    // 根据isbn在amazon网站爬取数据
    private AmazonMeta crawl(String isbn) throws Exception {
        try {
            AmazonMeta bean = new AmazonMeta();
            // 根据isbn搜索amazon
            Document document = DomParseUtil.getDomByURL("https://www.amazon.cn/s/ref=nb_sb_noss?__mk_zh_CN=%E4%BA%9A%E9%A9%AC%E9%80%8A%E7%BD%91%E7%AB%99&url=search-alias%3Dstripbooks&field-keywords=" + isbn);
            // 获取图书amazonId
            String val = getAmazonIdInPage(document);

            // 根据amazonId获取图书详情页
            String url = "https://www.amazon.cn/dp/" + val + "/ref=sr_1_1?s=books&ie=UTF8&qid=1536026272&sr=1-1&keywords=" + isbn;
            // 解析图书信息
            Document doc = DomParseUtil.getDomByURL(url);
            if (doc == null) {
                throw new NullPointerException();
            }
            Elements BasicInformations = doc.select("#detail_bullets_id").select("ul").select("li");
            for (Element BasicInformation : BasicInformations) {
                if (BasicInformation.select("b").text().equals("出版社:") && BasicInformation.text().contains(";")) {
                    bean.setPublisher(BasicInformation.text().replace("出版社:", "").split(";")[0]);
                    bean.setEditionOrder(BasicInformation.text().replace("出版社:", "").split(";")[1].split("\\(")[0]);
                    bean.setIssuedDate(BasicInformation.text().replace("出版社:", "").split(";")[1].split("\\(")[1].replace(")", ""));
                } else if (BasicInformation.select("b").text().equals("出版社:") && !BasicInformation.text().contains(";")) {
                    bean.setPublisher(BasicInformation.text().replace("出版社:", "").split("\\(")[0]);
                    bean.setIssuedDate(BasicInformation.text().replace("出版社:", "").split("\\(")[1].replace(")", ""));
                }
                if (BasicInformation.select("b").text().equals("丛书名:")) {
                    bean.setSeries(BasicInformation.text().replace("丛书名:", ""));
                }
                if (BasicInformation.select("b").text().equals("外文书名:")) {
                    bean.setOriginSeries(BasicInformation.text().replace("外文书名:", "").replace("'", "\\'"));
                }
                if (BasicInformation.select("b").text().equals("原书名:")) {
                    bean.setOriginTitle(BasicInformation.text().replace("原书名:", ""));
                }
                if (BasicInformation.select("b").text().equals("精装:") || BasicInformation.select("b").text().equals("平装:")) {
                    if (BasicInformation.select("b").text().equals("精装:")) {
                        bean.setPages(BasicInformation.text().replace("精装:", ""));
                    }
                    if (BasicInformation.select("b").text().equals("平装:")) {
                        bean.setPages(BasicInformation.text().replace("平装:", ""));
                    }
                }
                if (BasicInformation.select("b").text().equals("语种：")) {
                    bean.setLanguage(BasicInformation.text().replace("语种：", ""));
                }
                if (BasicInformation.select("b").text().equals("开本:")) {
                    bean.setFormat(BasicInformation.text().replace("开本:", ""));
                }
                if (BasicInformation.select("b").text().equals("ISBN:")) {
                    if (BasicInformation.text().replace("ISBN:", "").contains(",")) {
                        String ISBN = BasicInformation.text().replace("ISBN:", "");
                        if (ISBN.split(",")[1].trim().length() == 10) {
                            String isbn10 = ISBN.split(",")[1].trim();
                            if (isbn10.length() == 10) {
                                bean.setIsbn10(isbn10);
                            }
                            String isbn13 = ISBN.split(",")[0].trim();
                            if (isbn13.length() == 13) {
                                bean.setIsbn13(isbn13);
                            }
                        } else {
                            String isbn10 = ISBN.split(",")[0].trim();
                            if (isbn10.length() == 10) {
                                bean.setIsbn10(isbn10);
                            }

                            String isbn13 = ISBN.split(",")[1].trim();
                            if (isbn13.length() == 13) {
                                bean.setIsbn13(isbn13);
                            }
                        }
                    } else {
                        if (BasicInformation.text().replace("ISBN:", "").trim().length() == 10) {
                            String isbn10 = BasicInformation.text().replace("ISBN:", "").trim();
                            bean.setIsbn10(isbn10);
                        }
                        if (BasicInformation.text().replace("ISBN:", "").trim().length() == 13) {
                            String isbn13 = BasicInformation.text().replace("ISBN:", "").trim();
                            bean.setIsbn13(isbn13);
                        }
                    }
                }
//            if(BasicInformation.select("b").text().equals("条形码:")){
//                System.out.println(BasicInformation.text().replace("条形码:",""));
//            }
                if (BasicInformation.select("b").text().equals("商品尺寸:")) {
                    bean.setProductSize(BasicInformation.text().replace("商品尺寸:", ""));
                }
                if (BasicInformation.select("b").text().equals("商品重量:")) {
                    bean.setCommodityWeight(BasicInformation.text().replace("商品重量:", ""));
                }
                if (BasicInformation.select("b").text().equals("品牌:")) {
                    bean.setBrand(BasicInformation.text().replace("品牌:", ""));
                }
                if (BasicInformation.select("b").text().equals("ASIN:")) {
                    bean.setAsin(BasicInformation.text().replace("ASIN:", "").replaceAll(" ", ""));
                    bean.setAmazonId(BasicInformation.text().replace("ASIN:", "").replaceAll(" ", ""));
                }
                if (BasicInformation.select("b").text().contains("亚马逊热销商品排名:")) {
                    bean.setClassification(BasicInformation.select("b").select("a").text());
                }
            }
//            if(doc.select("div[class=a-box rbbSection]").select("span[class=a-list-item]").select("span[class=a-color-secondary]").get(0).text().equals("定价:")){
//                String price = doc.select("div[class=a-box rbbSection]").select("span[class=a-list-item]").select("span[class=a-color-secondary]").get(1).text();
//                bean.setPaperPrice(price);
//            }
            if (!doc.select("div[id=rightCol]").text().contains("电子书定价:") || !doc.select("div[id=rightCol]").text().contains("Kindle电子书价格:")) {
                if (doc.select("div[class=a-box rbbSection]").select("span[class=a-list-item]").select("span[class=a-color-secondary]").size() > 1) {
                    String price = doc.select("div[class=a-box rbbSection]").select("span[class=a-list-item]").select("span[class=a-color-secondary]").get(1).text();
                    bean.setPaperPrice(price);
                }
            }
            if (doc.select("h1[class=a-size-large a-spacing-none]").select("span").attr("id").contains("ebooksProductTitle")) {
                String title = doc.select("h1[class=a-size-large a-spacing-none]").select("span[id=ebooksProductTitle]").first().text();
                bean.setTitle(title);
            } else {
                String title = doc.select("h1[class=a-size-large a-spacing-none]").select("span[id=productTitle]").first().text();
                bean.setTitle(title);
            }
            if (doc.select("span[class=author notFaded]").select("a[class=a-link-normal]").first() != null) {
                String author = doc.select("span[class=author notFaded]").select("a[class=a-link-normal]").first().text();
                bean.setAuthor(author);
            }
            if (doc.select("span[class=author notFaded]").select("a[class=a-link-normal]").size() > 1) {
                String translator = doc.select("span[class=author notFaded]").select("a[class=a-link-normal]").get(1).text();
                bean.setTranslator(translator);
            }
            if (doc.select("span[class=a-size-small a-color-price]") != null) {
                if (doc.select("span[class=a-size-small a-color-price]").first() != null) {
                    if (doc.select("span[class=a-size-small a-color-price]").first().text() != null) {
                        String kindlePrice = doc.select("span[class=a-size-small a-color-price]").first().text();
                        bean.setKindlePrice(kindlePrice);
                    }
                }
            }
            if (doc.select("h1[class=a-size-large a-spacing-none]").select("span").size() > 2) {
                String binding = doc.select("span[class=a-size-medium a-color-secondary a-text-normal]").first().text();
                bean.setBinding(binding);
            }
            String content = doc.select("div[id=bookDescription_feature_div]").select("noscript").text().replace("海报：", "");
            bean.setSummary(content);
            if (doc.select("div[id=bookDescription_feature_div]").select("noscript").select("img") != null) {
                String poster = doc.select("div[id=bookDescription_feature_div]").select("noscript").select("img").attr("src");
                bean.setPoster(poster);
            }
            String merchantID = doc.select("form[action=/gp/product/handle-buy-box/ref=dp_start-bbf_1_glance]").select("input[id=merchantID]").attr("value");
            String qid = doc.select("form[action=/gp/product/handle-buy-box/ref=dp_start-bbf_1_glance]").select("input[id=qid]").attr("value");

            String detailurl = "https://www.amazon.cn/gp/product-description/ajaxGetProuductDescription.html?ref_=dp_apl_pc_loaddesc&asin=" + bean.getAsin() + "&merchantId=" + merchantID + "&deviceType=web";
            Document detaildoc = DomParseUtil.getDomByURL(detailurl);
            Elements details = detaildoc.select("div[class=a-section s-content]");

            for (Element detail : details) {
                if (detail.select("h3").text().equals("编辑推荐")) {
                    bean.setEditRecommend(detail.select("p").text().replace("'", "\\'"));
                }
                if (detail.select("h3").text().equals("名人推荐")) {
                    bean.setCelebrityRecommend(detail.select("p").text().replace("'", "\\'"));
                }
                if (detail.select("h3").text().equals("媒体推荐")) {
                    bean.setMediaRecommendation(detail.select("p").text().replace("'", "\\'"));
                }
                if (detail.select("h3").text().equals("作者简介")) {
                    bean.setAuthorIntroduction(detail.select("p").text().replace("'", "\\'"));
                }
                if (detail.select("h3").text().equals("序言")) {
                    bean.setPreface(detail.select("p").text().replace("'", "\\'"));
                }
                if (detail.select("h3").text().equals("目录")) {
                    bean.setCatalog(detail.select("p").text().replace("'", "\\'"));
                }
                if (detail.select("h3").text().equals("文摘")) {
                    bean.setAbstract_(detail.select("p").text().replace("'", "\\'"));
                }
                if (detail.select("h3").text().equals("后记")) {
                    bean.setPostScript(detail.select("p").text().replace("'", "\\'"));
                }
            }
            return bean;
        } catch (NullPointerException e1) {
            throw new NullPointerException();
        } catch (Exception e2) {
            throw new Exception();
        }
    }

    // 解析页面获取图书id
    private String getAmazonIdInPage(Document document) {
        if (document != null) {
            String val = "";
            if (document.select("input[name='asin']") != null) {
                val = document.select("input[name='asin']").val();
            }
            if ("".equalsIgnoreCase(val)) {
                val = document.select("#result_0").attr("data-asin");
            }
            if ("".equalsIgnoreCase(val)) {
                Element select = document.select("a[class='a-link-normal a-text-normal']").get(0);
                String[] split = select.attr("href").split("/");
                val = split[4];
            }
            return val;
        }
        return "";
    }
}
