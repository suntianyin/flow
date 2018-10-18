package com.apabi.flow.book.service.impl;

import com.apabi.flow.book.dao.BookShardDao;
import com.apabi.flow.book.model.BookShard;
import com.apabi.flow.book.service.BookShardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.SQLException;
import java.util.List;

/**
 * @author guanpp
 * @date 2018/8/1 14:19
 * @description
 */
@Service("bookShardService")
public class BookShardServiceImpl implements BookShardService {

    private static final Logger log = LoggerFactory.getLogger(BookShardServiceImpl.class);

    @Autowired
    BookShardDao bookShardDao;

    //根据图书id、章节号和分组号，获取章节内容
    @Override
    public BookShard selectShardById(String metaid, Integer chapterNum, Integer index) {
        if (!StringUtils.isEmpty(metaid)) {
            String comId = metaid + chapterNum + index;
            //return bookShardRepository.findBookShardByComIdIs(comId);
            return bookShardDao.findBookShardByComId(comId);
        }
        return null;
    }

    //添加图书章节分组内容
    @Override
    public int insertBookShard(List<BookShard> bookShardList) {
        if (bookShardList != null && bookShardList.size() > 0) {
            //bookShardRepository.saveAll(bookShardList);
            for (BookShard bookShard : bookShardList) {
                try {
                    bookShardDao.insertBookShard(bookShard);
                } catch (Exception e) {
                    log.warn(bookShard.getComId() + e);
                }
            }
            return 1;
        }
        return 0;
    }

    //删除图书metaid下的所有章节分组内容
    @Override
    public int deleteAllShardByMetaid(String metaid) {
        if (!StringUtils.isEmpty(metaid)) {
            //List<BookShard> bookShardList = bookShardRepository.findAllByComIdIsAfter(metaid);
            List<BookShard> bookShardList = bookShardDao.findAllBookShard(metaid);
            if (bookShardList != null && bookShardList.size() > 0) {
                //bookShardRepository.deleteAll(bookShardList);
            }
            return 1;
        }
        return 0;
    }
}
