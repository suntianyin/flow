package com.apabi.flow.book.service.impl;

import com.apabi.flow.book.entity.MongoBookChapter;
import com.apabi.flow.book.entity.MongoBookMeta;
import com.apabi.flow.book.entity.MongoBookShard;
import com.apabi.flow.book.service.MongoBookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

/**
 * @author guanpp
 * @date 2018/8/23 16:17
 * @description
 */
@Service("MongoBookService")
public class MongoBookServiceImpl implements MongoBookService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public MongoBookChapter findBookChapterByComId(String combId) {
        if (combId != null && combId.length() > 0) {
            Query query = Query.query(Criteria.where("id").is(combId));
            MongoBookChapter chapter = mongoTemplate.findOne(query, MongoBookChapter.class);
            return chapter;
        }
        return null;
    }

    //获取所有章节
    @Override
    public List<MongoBookChapter> findBookChapterById(String id) {
        if (id != null && id.length() > 0) {
            Query query = new Query();
            Pattern pattern = Pattern.compile(id + ".*$", Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("id").regex(pattern));
            return mongoTemplate.find(query, MongoBookChapter.class);
        }
        return null;
    }

    //获取所有章节分组
    @Override
    public List<MongoBookShard> findBookShardById(String id) {
        if (id != null && id.length() > 0) {
            Query query = new Query();
            Pattern pattern = Pattern.compile(id + ".*$", Pattern.CASE_INSENSITIVE);
            query.addCriteria(Criteria.where("id").regex(pattern));
            return mongoTemplate.find(query, MongoBookShard.class);
        }
        return null;
    }

    //获取图书元数据
    @Override
    public MongoBookMeta findBookMetaById(String id) {
        if (id != null && id.length() > 0) {
            Query query = Query.query(Criteria.where("id").is(id));
            MongoBookMeta bookMeta = mongoTemplate.findOne(query, MongoBookMeta.class);
            return bookMeta;
        }
        return null;
    }

    //获取图书元数据
    @Override
    public MongoBookMeta findBookMetaByIsbnAndTitle(String isbn, String title) {
        if (isbn != null && isbn.length() > 0) {
            Query query = Query.query(Criteria.where("isbn").is(isbn).and("title").is(title));
            MongoBookMeta bookMeta = mongoTemplate.findOne(query, MongoBookMeta.class);
            return bookMeta;
        }
        return null;
    }

    //获取图书元数据
    @Override
    public MongoBookMeta findBookMetaByIsbn(String isbn) {
        if (isbn != null && isbn.length() > 0) {
            Query query = Query.query(Criteria.where("isbn").is(isbn));
            MongoBookMeta bookMeta = mongoTemplate.findOne(query, MongoBookMeta.class);
            return bookMeta;
        }
        return null;
    }

    //获取图书元数据
    @Override
    public List<MongoBookMeta> findAllBookByIsbn(String isbn) {
        if (isbn != null && isbn.length() > 0) {
            Query query = Query.query(Criteria.where("isbn").is(isbn));
            List<MongoBookMeta> bookMetaList = mongoTemplate.find(query, MongoBookMeta.class);
            return bookMetaList;
        }
        return null;
    }

    //获取图书元数据
    @Override
    public MongoBookMeta findBookMetaByTitle(String title) {
        if (title != null && title.length() > 0) {
            Query query = Query.query(Criteria.where("title").is(title));
            MongoBookMeta bookMeta = mongoTemplate.findOne(query, MongoBookMeta.class);
            return bookMeta;
        }
        return null;
    }

    //根据图书id获取元数据
    @Override
    public Long countBookMetaById(String metaid) {
        if (metaid != null && metaid.length() > 0) {
            Query query = Query.query(Criteria.where("id").is(metaid));
            Long cnt = mongoTemplate.count(query, MongoBookMeta.class);
            return cnt;
        }
        return null;
    }

    //存储图书元数据
    @Override
    public int saveBookMeta(MongoBookMeta bookMeta) {
        if (bookMeta != null) {
            mongoTemplate.save(bookMeta);
            return 1;
        }
        return 0;
    }

    //更新图书元数据
    @Override
    public int updateBookMeta(MongoBookMeta bookMeta) {
        if (bookMeta != null) {
            //查询条件
            Query query = Query.query(Criteria.where("id").is(bookMeta.getId()));
            Update update = new Update();
            update.set("optimized", bookMeta.getOptimized());
            update.set("original", bookMeta.getOriginal());
            update.set("platform", bookMeta.getPlatform());
            update.set("_route_", bookMeta.get_route_());
            update.set("libId", bookMeta.getLibId());

            update.set("id", bookMeta.getId());
            update.set("title", bookMeta.getTitle());
            update.set("creator", bookMeta.getCreator());
            update.set("publisher", bookMeta.getPublisher());
            update.set("summary", bookMeta.getSummary());
            update.set("publishDate", bookMeta.getPublishDate());
            update.set("isbn", bookMeta.getIsbn());
            update.set("chapterSum", bookMeta.getChapterSum());
            update.set("wordSum", bookMeta.getWordSum());
            update.set("language", bookMeta.getLanguage());
            update.set("type", bookMeta.getType());
            update.set("cssUrl", bookMeta.getCssUrl());
            update.set("coverUrl", bookMeta.getCoverUrl());
            update.set("coverMiniUrl", bookMeta.getCoverMiniUrl());
            update.set("bodyClass", bookMeta.getBodyClass());
            update.set("cataRows", bookMeta.getCataRows());
            update.set("lastModifiedDate", bookMeta.getLastModifiedDate());
            update.set("createdDate", bookMeta.getCreatedDate());
            update.set("fileName",bookMeta.getFileName());
            mongoTemplate.upsert(query, update, MongoBookMeta.class);
            return 1;
        }
        return 0;
    }

    //存储章节内容
    @Override
    public int saveBookChapter(List<MongoBookChapter> chapters) {
        if (chapters != null && chapters.size() > 0) {
            for (MongoBookChapter chapter : chapters) {
                mongoTemplate.save(chapter);
            }
            return 1;
        }
        return 0;
    }

    //存储或更新章节内容
    @Override
    public int updateBookChapter(List<MongoBookChapter> chapters) {
        if (chapters != null && chapters.size() > 0) {
            for (MongoBookChapter chapter : chapters) {
                //查询条件
                Query query = Query.query(Criteria.where("id").is(chapter.getId()));
                Update update = new Update();
                update.set("id", chapter.getId());
                update.set("chapterNum", chapter.getChapterNum());
                update.set("shardSum", chapter.getShardSum());
                update.set("wordSum", chapter.getWordSum());
                update.set("bodyClass", chapter.getBodyClass());
                update.set("content", chapter.getContent());
                update.set("createdDate", chapter.getCreatedDate());
                update.set("lastModifiedDate", chapter.getBodyClass());
                mongoTemplate.upsert(query, update, MongoBookChapter.class);
            }
            return 1;
        }
        return 0;
    }

    //存储或更新章节内容分组
    @Override
    public int updateBookShard(List<MongoBookShard> shards) {
        if (shards != null && shards.size() > 0) {
            for (MongoBookShard shard : shards) {
                //查询条件
                Query query = Query.query(Criteria.where("id").is(shard.getId()));
                Update update = new Update();
                update.set("id", shard.getId());
                update.set("chapterNum", shard.getChapterNum());
                update.set("index", shard.getIndex());
                update.set("wordSum", shard.getWordSum());
                update.set("bodyClass", shard.getBodyClass());
                update.set("content", shard.getContent());
                update.set("createdDate", shard.getCreatedDate());
                update.set("lastModifiedDate", shard.getBodyClass());
                mongoTemplate.upsert(query, update, MongoBookShard.class);
            }
            return 1;
        }
        return 0;
    }
}
