package com.apabi.flow.book.service.impl;

import com.apabi.flow.book.dao.BookChapterDao;
import com.apabi.flow.book.dao.BookLogMapper;
import com.apabi.flow.book.dao.BookPageMapper;
import com.apabi.flow.book.model.*;
import com.apabi.flow.book.service.BookPageService;
import com.apabi.flow.book.util.*;
import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.config.DataConfig;
import net.sf.json.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.dom4j.Document;
import org.dom4j.Element;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 功能描述： <br>
 * <拉取分页数据、拼装章节>
 *
 * @author supeng
 * @date 2018/8/23 16:34
 * @since 1.0.0
 */
@Service
public class BookPageServiceImpl implements BookPageService{

    private static final Logger log = LoggerFactory.getLogger(BookPageServiceImpl.class);

    @Autowired
    private DataConfig dataConfig;

//    @Autowired
//    private BookLogRepository bookLogRepository;

    @Autowired
    private BookLogMapper bookLogMapper;

    /*@Autowired
    private BookPageRepository bookPageRepository;*/

    @Autowired
    private BookPageMapper bookPageMapper;

    @Autowired
//    BookChapterRepository bookChapterRepository;
    private BookChapterDao bookChapterDao;

    /**
     * 从solr获取图书元数据
     * @param metaId
     * @return
     * @throws Exception
     */
    private JSONObject getByMetaId(String metaId) throws Exception {
        if (StringUtils.isNotBlank(metaId)) {
            String bookDetailUrl = GlobalConstant.BOOK_DETAIL_URL;
            String baseUrl = GlobalConstant.SERVER_SOLR_URL + bookDetailUrl + "&id=";
            String resQuery = metaId;
            String resUrl = baseUrl + resQuery;
            HttpEntity entity = HttpUtils.doGetEntity(resUrl.toString());
            long a = System.currentTimeMillis();
            String str = EntityUtils.toString(entity);
            long b = System.currentTimeMillis();
            log.info("请求solr获取图书简单信息耗时[" + (b - a) + "毫秒]url[" + resUrl + "]");
            String number = JSONObject.fromObject(str).getJSONObject("book").get("numFound").toString();
            JSONObject json = null;
            if (StringUtils.equals(number,"0")) {
                return json;
            }
            json = (JSONObject) JSONObject.fromObject(str).getJSONObject("book").getJSONArray("docs").get(0);
            return json;
        }
        return null;
    }

    /**
     * 通过xml，获取书苑图书页码信息
     * 书苑流式内容采集
     *
     * @param metaId
     * @return 返回插入页数
     * @throws Exception
     */
    @Override
    public int insertShuyuanPageData(String metaId) {
        if (StringUtils.isNotBlank(metaId)) {
            //备用 从数据库中获取，但是目前没有数据，则通过接口去获取 总页数
            /*BookMeta bookMeta = bookMetaRepository.findBookMetaByMetaidIs(metaId);
            Integer cebxPage = Integer.valueOf(bookMeta.getCebxPage());*/

            // 从接口中获取总页数
            JSONObject jsonBook = null;
            try {
                jsonBook = getByMetaId(metaId);
                if (jsonBook == null) {
                    return 0;
                }
            } catch (Exception e) {
                log.error("获取元数据信息出错，无法得到书本页数信息，退出数据获取");
                return 0;
            }
            //总页数
            int cebxPage = jsonBook.getInt("cebxPage");
            int maxPageid = bookPageMapper.findMaxPageIdByMetaId(metaId);
            int start = 1;
            //当 maxPageid 处于 0 - cebxPage 之间时，将 maxPageid + 1 作为循环拉取的初始值，否则返回不符合循环条件的值 cebxPage + 1
            if (maxPageid >= 0 && maxPageid <= cebxPage) {
                start = maxPageid + 1;
            } else {
                start = cebxPage + 1;
            }
            //记录当前远程拉取次数
            int num = 0;
            //页数 从 第一页开始，直到 总页数
            for (long i = start; i <= cebxPage; i++) {
                long a = System.currentTimeMillis();
                HttpEntity httpEntity = null;
                String url = null;
                try {
                    //计数需要放在最上面才能保证数据的正确性，在后或中间都有可能会执行不到
                    num++;
                    Thread.sleep(1000);
                    url = EbookUtil.makeHtmlUrl(BookMetaServiceImpl.shuyuanOrgCode, metaId, BookMetaServiceImpl.baseUrlType, BookMetaServiceImpl.serviceType, "1920", "1080", i);
                    httpEntity = HttpUtils.doGetEntity(url);
                    String tmp = EntityUtils.toString(httpEntity);
                    BookPage bookPage = new BookPage();
                    bookPage.setId(ApabiIDUtils.nextId());
                    bookPage.setMetaId(metaId);
                    bookPage.setPageId(i);
                    bookPage.setWordSum(0L);
                    bookPage.setContent(tmp);
                    bookPage.setCreateTime(new Date());
                    bookPageMapper.insert(bookPage);
                } catch (Exception e) {
                    log.info("获取图书：{} 的第{}页时超时或出现错误，错误信息：{}，将重新发起请求",metaId, i, e);
                    i--;
                    num--;
                    continue;
                }
                long b = System.currentTimeMillis();
                log.debug("gethtml请求耗时 {}， url = {}", b - a, url);
            }
            BookLog bookLog = new BookLog(UUIDCreater.nextId(),metaId, DataType.PAGE, num, cebxPage, start, start + num -1, new Date(), null);
            bookLogMapper.insert(bookLog);

            log.info("总页数：{}，获取到的页数：{}，从第{}页开始添加",cebxPage, num, start);
            return num;
        }
        return 0;
    }

    /**
     * 借道，内部类，常量
     */
    private interface DataType{
        String PAGE = "page";
        String CHAPTER = "chapter";
    }

    /**
     * 根据data-config.yml 中的pageMetaIdList 的数组自动抓取多本书的流式分页内容
     * @return
     */
    @Override
    public int autoFetchPageData(){
        int totalNum = 0;
        for (String metaid : dataConfig.getPageMetaIdList()){
            totalNum += insertShuyuanPageData(metaid);
        }
        log.info("拉取总页数为 - {}", totalNum);
        return totalNum;
    }

    /**
     * 根据data-config.yml 中的 chapterMetaIdList 的数组自动将多本图书从 页面内容 组合成 对应的章节内容
     * @return
     */
    @Override
    public int autoProcessBookFromPage2Chapter() {
        int totalNum = 0;
        for (String metaid : dataConfig.getChapterMetaIdList()){
            try {
                totalNum += processBookFromPage2Chapter(metaid);
            }catch (Exception e){
                log.error("metaid = {} 的图书组装出错，错误信息：{}", metaid, e);
            }
        }
        log.info("成功组装图书数 - {}", totalNum);
        return totalNum;
    }

    /**
     * 获取 当前 metaId 下的 所有 页码列表信息
     * @param metaId
     * @return 返回页码列表
     */
    @Override
    public List<BookPage> queryAllBookPagesByMetaId(String metaId) {
        List<BookPage> list = bookPageMapper.findAllByMetaIdOrderByPageId(metaId);
        if (list == null || list.size() == 0) {
            return list;
        }
        return list;
    }

    /**
     * 查询特定 metaid 下的某一页 内容信息
     * @param metaid
     * @param pageid
     * @return 返回 bookPage 页面信息实体
     */
    @Override
    public BookPage getBookPageContentByMetaidAndPageid(String metaid, Integer pageid) {
        BookPage bookPage = null;
        if (StringUtils.isNotBlank(metaid) && pageid != null && pageid > 0) {
            bookPage = bookPageMapper.findBookPageByMetaIdAndPageId(metaid, pageid);
        }
        return bookPage;
    }

    /**
     * 通过获取书苑xml 章节目录，将本地图书分页数据拼装成章节数据
     * 书苑按页流式内容封装成流式章节内容
     * @param metaId
     * @return
     * @throws Exception
     */
    @Override
    public int processBookFromPage2Chapter(String metaId) throws Exception {
        if (StringUtils.isNotBlank(metaId)) {
            //从该接口中获取 xml 资源
            String url = BookMetaServiceImpl.iyzhiMobileMookCatalogUrl + metaId;
            Document document = BookUtil.getXmlSourceFromRemoteUrl(url);
            //在这种情况下抛出 自定义异常 会比较好一些
            if (document == null) {
                log.error("章节目录获取失败！");
                saveBookLog(metaId,DataType.CHAPTER,-1, -1, 0, -1);
                return 0;
            }
            List<Element> cataList = BookUtil.remoteDocToBookMeta(document);

            //获取cataRows目录数据
            //存储目录页码
            List<Integer> pageNums = new ArrayList<>();
            List<String> catas = new ArrayList<>();
            if (cataList != null && !cataList.isEmpty()) {
                StringBuilder cata = new StringBuilder();
                for (int i = 0; i < cataList.size(); i++) {
                    pageNums.add(Integer.valueOf(cataList.get(i).attributeValue("ebookPageNum")));
                    cataList.get(i).addAttribute("chapterNum", String.valueOf(i));
                    cata.append("{\"chapterName\":\"").append(cataList.get(i).attributeValue("chapterName")).append("\",")
                            .append("\"chapterNum\":\"").append(cataList.get(i).attributeValue("chapterNum")).append("\",")
                            .append("\"ebookPageNum\":\"").append(cataList.get(i).attributeValue("ebookPageNum")).append("\"}");
                    catas.add(cata.toString());
                }
            }
            if (pageNums.isEmpty()){
                log.error("章节页码获取失败！");
                saveBookLog(metaId,DataType.CHAPTER,-1, -1, 0, -1);
                return 0;
            }
            // 从本地数据源中获取当前metaid 的所有页面内容信息列表
            //todo 是否需要验证数据的一致性 或者 合理性？
            List<BookPage> bookPageList = bookPageMapper.findAllByMetaIdOrderByPageIdWithContent(metaId);
            if (bookPageList == null) {
                log.info("图书分页表中没有 metaid = {} 的数据列表", metaId);
                saveBookLog(metaId,DataType.CHAPTER,0, -1, 0, -1);
                return 0;
            }

            pageNums.add(bookPageList.size());

            //从书苑获取图书内容，并分章节
            org.jsoup.nodes.Document doc;
            //图书总字数
            int wordSum = 0;
            int word;
            int start = 1;
            int chapterNum = 0;
            //按章节存放
            List<BookChapter> chapterList = new ArrayList<>();
            //存储章节内容为空的目录
            List<String> nullCata = new ArrayList<>();

            //判断，如果本地数据库中没有取出数据，两种解决方案: 1. 将 pageNums 清空，则一条数据都不插入 2. 尝试从书苑接口中获取数据（不建议，缺少流程步骤）
            if (bookPageList.size() < pageNums.get(pageNums.size() - 2)) {
                //总页数 小于 最后一章的页码，数据不合理，这里暂时不予 数据插入
                log.info("总页数{} 小于 最后一章的页码{}，数据不合理，这里暂时不予 数据插入", bookPageList.size(), pageNums.get(pageNums.size() - 2));
                saveBookLog(metaId,DataType.CHAPTER,chapterList.size(), chapterList.size(), 0, chapterList.size()-1);
                return 0;
            }
            for (Integer pageNum : pageNums) {
                if (pageNum == 1) {
                    continue;
                }
                BookChapter chapter = new BookChapter();
                chapter.setComId(metaId + chapterNum);
                chapter.setChapterNum(chapterNum);
                StringBuilder content = new StringBuilder("");

                for (int i = start; i < pageNum; i++) {
                    content.append(bookPageList.get(i - 1).getContent());
                }
                start = pageNum;
                chapterNum++;
                //获取章节字数
                doc = Jsoup.parse(content.toString());
                word = doc.body().children().text().replaceAll("\\u3000|\\s*", "").length();
                chapter.setWordSum(word);
                wordSum += word;
                //判断该章节为空页，在目录中删除
                if (word == 0 && chapterNum < catas.size() && StringUtils.contains(content.toString(), "<img")) {
                    nullCata.add(catas.get(chapterNum));
                }
                //给获取内容为空的章节添加一个p标签
                if (content.length() == 0) {
                    content.append("<p></p>");
                }
                //todo 这里需要添加 换行符？ 什么换行符<br> ?
                chapter.setContent(content.toString());
                //线程不安全，需在方法中定义和初始化
                SimpleDateFormat localSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                chapter.setCreateTime(localSdf.parse(localSdf.format(new Date())));
                chapter.setUpdateTime(localSdf.parse(localSdf.format(new Date())));

                //添加章节信息
                chapterList.add(chapter);
            }
            if (!chapterList.isEmpty()){
                for (BookChapter chapter:chapterList){
                    bookChapterDao.insertBookChapter(chapter);
                }
                saveBookLog(metaId,DataType.CHAPTER,chapterList.size(), chapterList.size(), 0, chapterList.size()-1);
                return 1;
            }
            saveBookLog(metaId,DataType.CHAPTER,chapterList.size(), chapterList.size(), 0, chapterList.size()-1);
        }
        return 0;
    }

    /**
     * 记录 本次bookLog 的日志信息
     * @param metaid
     * @param dataType
     * @param addedNum
     * @param totals
     * @param startIndex
     * @param endIndex
     */
    private void saveBookLog(String metaid, String dataType, Integer addedNum, Integer totals, Integer startIndex, Integer endIndex){
        if (DataType.PAGE.equals(dataType)){
            BookLog bookLog = new BookLog(UUIDCreater.nextId(), metaid, DataType.PAGE, addedNum, totals, startIndex, startIndex + addedNum -1, new Date(), null);
            bookLogMapper.insert(bookLog);
            log.info("总页数：{}，从第 {} 页开始添加，本次共添加了 {} 页",totals, startIndex, addedNum);
        }else if (DataType.CHAPTER.equals(dataType)){
            BookLog bookLog = new BookLog(UUIDCreater.nextId(), metaid, DataType.CHAPTER, addedNum, totals, startIndex, startIndex + addedNum -1, new Date(), null);
            bookLogMapper.insert(bookLog);
            log.info("总章节数：{}，从第 {} 章开始添加，本次共添加了 {} 章",totals, startIndex, addedNum);
        }
    }

//    /**
//     * 通过xml，获取书苑图书元数据
//     *
//     * @param metaId
//     * @return
//     * @throws Exception
//     */
//    @Override
//    @Deprecated
//    public int insertShuyuanData(String metaId) throws Exception {
//        //String path = "C:\\Users\\supeng\\Desktop\\xml\\m.20110920-DT-889-0295.xml";
//        //org.apache.commons.lang3.StringUtils.isNotBlank(path) &&
//        if (org.apache.commons.lang3.StringUtils.isNotBlank(metaId)) {
//            HttpEntity httpEntity = null;
//            //从该接口中获取 xml 资源
//            String url = String.format("http://www.apabi.com/apadlibrary/iyzhi/ServiceEntry/Mobile.aspx?api=bookdetail&metaid=%s&type=1&multiplePart=0&orgcode=iyzhi", metaId);
//            Document document = BookUtil.getXmlSourceFromRemoteUrl(url);
//            if (document == null) {
//                return 0;
//            }
//            //Document doc1 = new DOMDocument();
//            List<Element> cataList = BookUtil.remoteDocToBookMeta(document);
//
//            //获取图书元数据
//            BookMetaVo bookMetaVo = bookMetaVoRepository.findBookMetaVoByMetaidIs(metaId);
//            //加载图书的基本信息
//            //JSONObject jsonBook = getByMetaId(metaId);
//            //获取css文件
//           /* SimpleDateFormat cssSdf = new SimpleDateFormat("yyyy-MM-dd");
//            String cssLink = getCss(bookMetaVo.getMetaid(), cssSdf.parse(bookMetaVo.getIssueddate()));
//            bookMetaVo.setStyleUrl("<link href=\"" + cssLink + "\" rel=\"stylesheet\" type=\"text/css\">");
//            //生成封面及缩略图
//            bookMetaVo.setCoverUrl(EpubookConstant.coverUrl + metaId);
//            bookMetaVo.setThumimgUrl(EpubookConstant.coverUrl + metaId);*/
//            //获取cataRows目录数据
//            //存储目录页码
//            List<Integer> pageNums = new ArrayList<>();
//            List<String> catas = new ArrayList<>();
//            if (cataList != null && cataList.size() > 0) {
//                StringBuilder cata = new StringBuilder();
////                String cata;
//                for (int i = 0; i < cataList.size(); i++) {
//                    pageNums.add(Integer.valueOf(cataList.get(i).attributeValue("ebookPageNum")));
//                    cataList.get(i).addAttribute("chapterNum", String.valueOf(i));
//                    cata.append("{\"chapterName\":\"").append(cataList.get(i).attributeValue("chapterName")).append("\",")
//                            .append("\"chapterNum\":\"").append(cataList.get(i).attributeValue("chapterNum")).append("\",")
//                            .append("\"ebookPageNum\":\"").append(cataList.get(i).attributeValue("ebookPageNum")).append("\"}");
//                    /*cata = "{\"chapterName\":\"" + cataList.get(i).attributeValue("chapterName") + "\"," +
//                            "\"chapterNum\":\"" + cataList.get(i).attributeValue("chapterNum") + "\"," +
//                            "\"ebookPageNum\":\"" + cataList.get(i).attributeValue("ebookPageNum") + "\"}";*/
//                    catas.add(cata.toString());
//                }
//                //bookMetaVo.setCataRows(catas);
//            }
//            //更改平台
//            //bookMetaVo.setPlatform(EpubookConstant.PLATFORM);
//            //获取总页数
//            //int cebxPage = jsonBook.getInt("cebxPage");
//            //int cebxPage = 233;
//
//            // 从本地数据源中获取当前metaid 的所有页面内容信息列表
//            //todo 是否需要验证数据的一致性 或者 合理性？
//            List<BookPage> bookPageList = bookPageRepository.findAllByMetaidIsOrderByPageid(metaId);
//            if (bookPageList == null) {
//                bookPageList = Collections.emptyList();
//            }
//            pageNums.add(bookPageList.size());
//
//            //pageNums.add(cebxPage);
//
//            //从书苑获取图书内容，并分章节
//            org.jsoup.nodes.Document doc;
//            //图书总字数
//            int wordSum = 0;
//            int word;
//            int start = 1;
//            int chapterNum = 0;
//            //按章节存放
//            List<BookChapter> chapterList = new ArrayList<>();
//            //存储章节内容为空的目录
//            List<String> nullCata = new ArrayList<>();
//            //对章节进行分组存放
//            List<BookShard> chapterShardList = new ArrayList<>();
//
//            //判断，如果本地数据库中没有取出数据，两种解决方案: 1. 将 pageNums 清空，则一条数据都不插入 2. 尝试从书苑接口中获取数据（不建议，缺少流程步骤）
//            if (bookPageList.size() < pageNums.get(pageNums.size() - 1)) {
//                //总页数 小于 最后一章的页码，数据不合理，这里暂时不予 数据插入
//                log.info("总页数{} 小于 最后一章的页码{}，数据不合理，这里暂时不予 数据插入", bookPageList.size(), pageNums.get(pageNums.size() - 1));
//                pageNums.clear();
//            }
//            for (Integer pageNum : pageNums) {
//                if (pageNum == 1) {
//                    continue;
//                }
//                BookChapter chapter = new BookChapter();
//                chapter.setComId(metaId + chapterNum);
//                chapter.setChapterNum(chapterNum);
//                StringBuilder content = new StringBuilder("");
//                //String content = "";
//                String key;
//                String tmp;
//                for (int i = start; i < pageNum; i++) {
//                    //key = "apabi_wx_flow_html_" + metaId + "_" + i;
//                    //tmp = redisService.get(key);
//                    //tmp = null;
//
//                    //old: 从书苑接口获取 图书每页内容信息 => new: 数据已经同步到本地数据库中，从本地数据源中拉取每页内容信息即可
//                    /*if (org.apache.commons.lang.StringUtils.isBlank(tmp)) {
//                        //Thread.sleep(1000);
//                        String url = EbookUtil.makeHtmlUrl(shuyuanOrgCode, metaId, baseUrlType, serviceType, "1920", "1080", i);
//                        long a = System.currentTimeMillis();
//                        HttpEntity httpEntity;
//                        try {
//                            httpEntity = HttpUtils.doGetEntity(url);
//                        } catch (SocketTimeoutException e) {
//                            log.debug("获取图书：" + metaId + "的第" + i + "页时超时，将发起重新请求");
//                            i--;
//                            continue;
//                        }
//                        long b = System.currentTimeMillis();
//                        log.debug("gethtml请求耗时[" + (b - a) + "]url[" + url + "]");
//                        tmp = EntityUtils.toString(httpEntity);
//                    }*/
//                    //content += tmp;
//                    content.append(bookPageList.get(i - 1));
//                }
//                start = pageNum;
//                chapterNum++;
//                //获取章节字数
//                doc = Jsoup.parse(content.toString());
//                word = doc.body().children().text().replaceAll("\\u3000|\\s*", "").length();
//                chapter.setWordSum(word);
//                wordSum += word;
//                //判断该章节为空页，在目录中删除
//                //!content.contains("<img")
//                if (word == 0 && chapterNum < catas.size() && org.apache.commons.lang3.StringUtils.contains(content.toString(), "<img")) {
//                    nullCata.add(catas.get(chapterNum));
//                }
//                //给获取内容为空的章节添加一个p标签
//                if (content.length() == 0) {
//                    content.append("<p></p>");
//                }
//                chapter.setContent(content.toString());
//                //线程不安全，需在方法中定义和初始化
//                SimpleDateFormat localSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//                chapter.setCreateTime(localSdf.parse(localSdf.format(new Date())));
//                chapter.setUpdateTime(localSdf.parse(localSdf.format(new Date())));
//                //对章节内容进行分片
//                Elements children = doc.body().children();
//                List<String> tags = new ArrayList<>();
//                StringBuilder contentP = new StringBuilder("");
//                //String contentP = "";
//                for (int j = 0; j < children.size(); j++) {
//                    contentP.append(children.get(j).outerHtml());
//                    //contentP += children.get(j).outerHtml();
//                    if ((j != 0 && j % EpubookConstant.shardSize == 0) || (j == children.size() - 1)) {
//                        tags.add(contentP.toString());
//                        //清空 stringBuilder
//                        contentP.delete(0, contentP.length());
//                    }
//                }
//                //添加分组数
//                chapter.setShardSum(tags.size());
//                chapterList.add(chapter);
//                //每组分片都新建一个EpubookChapter
//                for (int k = 0; k < tags.size(); k++) {
//                    BookShard chapterShard = new BookShard();
//                    chapterShard.setComId(bookMetaVo.getMetaid() + chapterNum + k);
//                    chapterShard.setChapterNum(chapterNum);
//                    chapterShard.setIndex(k);
//                    //chapterShard.setBodyClass(body.attr("class"));
//                    chapterShard.setCreateTime(localSdf.parse(localSdf.format(new Date())));
//                    chapterShard.setUpdateTime(localSdf.parse(localSdf.format(new Date())));
//                    org.jsoup.nodes.Document shards = Jsoup.parse(tags.get(k), "utf-8");
//                    int wordS = shards.text().replaceAll("\\u3000|\\s*", "").length();
//                    chapterShard.setWordSum(wordS);
//                    String contentS = tags.get(k);
//                    chapterShard.setContent(contentS);
//                    chapterShardList.add(chapterShard);
//                }
//
//            }
//            //将空页的目录去掉
//            for (String nullc : nullCata) {
//                catas.remove(nullc);
//            }
//            //将目录转为字符串
//            //String catalogs = "";
//            /*StringBuilder catalogs = new StringBuilder("");
//            for (String catalog : catas) {
//                catalogs.append(catalog).append(",");
//                //catalogs += catalog + ",";
//            }
//            bookMetaVo.setStreamCatalog(catalogs);*/
//            /*//获取章节数
//            bookMetaVo.setChapterNum(chapterList.size());
//            //获取总字数
//            bookMetaVo.setContentNum(wordSum);*/
//
//            //暂时屏蔽数据插入
//            bookChapterRepository.saveAll(chapterList);
//            /*bookMetaVoRepository.save(bookMetaVo);
//            bookChapterRepository.saveAll(chapterList);
//            bookShardRepository.saveAll(chapterShardList);*/
//            return 1;
//        }
//        return 0;
//    }
}
