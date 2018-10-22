package com.apabi.flow.book.service.impl;

import com.apabi.flow.book.dao.BookChapterDao;
import com.apabi.flow.book.dao.BookMetaDao;
import com.apabi.flow.book.dao.BookShardDao;
import com.apabi.flow.book.model.BookChapter;
import com.apabi.flow.book.model.BookChapterSum;
import com.apabi.flow.book.model.BookMetaVo;
import com.apabi.flow.book.model.BookShard;
import com.apabi.flow.book.service.BookChapterService;
import com.apabi.flow.book.util.BookConstant;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author guanpp
 * @date 2018/7/31 14:13
 * @description
 */
@Service("bookChapterService")
public class BookChapterServiceImpl implements BookChapterService {

    private static final Logger log = LoggerFactory.getLogger(BookChapterServiceImpl.class);

    @Autowired
    BookMetaDao bookMetaDao;

    @Autowired
    BookChapterDao bookChapterDao;

    @Autowired
    BookShardDao bookShardDao;

    @Autowired
    JdbcTemplate jdbcTemplate;

    //根据图书id和章节号，获取章节内容
    @Override
    public BookChapter selectChapterById(String metaid, Integer chapterNum) {
        if (!StringUtils.isEmpty(metaid)) {
            String comId = metaid + chapterNum;
            return bookChapterDao.findBookChapterByComId(comId);
        }
        return null;
    }

    //添加图书章节内容
    @Override
    public int insertBookChapter(List<BookChapter> bookChapterList) {
        if (bookChapterList != null && bookChapterList.size() > 0) {
            for (BookChapter bookChapter : bookChapterList) {
                try {
                    bookChapterDao.insertBookChapter(bookChapter);
                } catch (Exception e) {
                    log.warn(bookChapter.getComId() + e);
                }
            }
            return 1;
        }
        return 0;
    }

    //删除metaid下的所有内容
    @Override
    public int deleteAllChapterByMetaid(String metaid) {
        if (!StringUtils.isEmpty(metaid)) {
            List<BookChapter> bookChapterList = bookChapterDao.findAllBookChapter(metaid);
            //BookMetaVo bookMetaVo = bookMetaVoRepository.findBookMetaVoByMetaidIs(metaid);
            //比较章节总数和查询出来的章节条目
//            if (bookChapterList.size() == bookMetaVo.getChapterSum()) {
//                bookChapterRepository.deleteAll(bookChapterList);
//            }
            return 1;
        }
        return 0;
    }

    //获取图书章节及分组信息
    @Override
    public List<BookChapterSum> selectBookChapterSum(String metaid) {
        if (!StringUtils.isEmpty(metaid)) {
            //List<BookChapterSum> chapterSumList = bookChapterSumRepository.findAllByComIdStartingWith(metaid);
            List<BookChapterSum> chapterSumList = bookChapterDao.findAllBookChapterSum(metaid);
            List<BookChapterSum> chapterSums = chapterSumList.stream()
                    .sorted(Comparator.comparingInt(BookChapterSum::getChapterNum))
                    .collect(Collectors.toList());
            return chapterSums;
        }
        return null;
    }

    //更新图书章节内容
    @Override
    public int updateBookChapter(BookChapter bookChapter) {
        if (!StringUtils.isEmpty(bookChapter.getComId())) {
            //更新图书字数
            String content = bookChapter.getContent();
            String comid = bookChapter.getComId();
            BookChapter oChapter = bookChapterDao.findBookChapterByComId(bookChapter.getComId());
            int oword = oChapter.getWordSum();
            int chapterNum = bookChapter.getChapterNum();
            Element body = Jsoup.parse(content).body();
            int cword = body.children().text().replaceAll("\\u3000|\\s*", "").length();
            //更新图书总字数
            String metaid = comid.substring(0, comid.length()
                    - String.valueOf(chapterNum).length());
            //BookMetaVo bookMetaVo = bookMetaVoRepository.findByMetaidIs(metaid);
            BookMetaVo bookMetaVo = bookMetaDao.findBookMetaVoById(metaid);
            int wordSum = bookMetaVo.getWordSum();
            wordSum = wordSum + (cword - oword);
            bookMetaVo.setWordSum(wordSum);
            //更新入库
            bookChapter.setContent(content);
            bookChapter.setWordSum(cword);
            bookChapter.setUpdateTime(new Date());
            bookChapterDao.updateBookChapter(bookChapter);
            //bookMetaVoRepository.save(bookMetaVo);
            bookMetaDao.updateBookMetaVo(bookMetaVo);
            //更新分组内容
            updateBookShard(comid, content);
            return 1;
        }
        return 0;
    }

    //更新章节分组内容
    private void updateBookShard(String comid, String content) {
        if (!StringUtils.isEmpty(comid) && !StringUtils.isEmpty(content)) {
            Element body = Jsoup.parse(content).body();
            //对章节内容进行分片
            Elements children = body.children();
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
            //获取分片内容，更新分片内容
            String shardComid;
            String shardContent;
            int cword;
            for (int k = 0; k < tags.size(); k++) {
                shardComid = comid + k;
                shardContent = tags.get(k);
                cword = body.children().text().replaceAll("\\u3000|\\s*", "").length();
                BookShard bookShard = new BookShard();
                bookShard.setComId(shardComid);
                bookShard.setContent(shardContent);
                bookShard.setWordSum(cword);
                bookShard.setUpdateTime(new Date());
                bookShardDao.updateBookShard(bookShard);
            }
        }
    }
}
