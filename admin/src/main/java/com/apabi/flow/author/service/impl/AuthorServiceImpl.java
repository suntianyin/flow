package com.apabi.flow.author.service.impl;

import com.apabi.flow.author.dao.AuthorMapper;
import com.apabi.flow.author.model.Author;
import com.apabi.flow.author.service.AuthorService;
import com.apabi.flow.author.util.CsvFileUtils;
import com.github.pagehelper.Page;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.*;

/**
 * 功能描述： <br>
 * <Author 服务实现>
 *
 * @author supeng
 * @date 2018/8/27 11:05
 * @since 1.0.0
 */
@Service
public class AuthorServiceImpl implements AuthorService {

    private static final Logger log = LoggerFactory.getLogger(AuthorServiceImpl.class);

    @Autowired
    private AuthorMapper authorMapper;

    @Override
    public Author getAuthorById(String id) {

        if (StringUtils.isBlank(id)){
            log.error("参数 id 不能为空");
            return null;
        }
        List<Author> authors = listAuthorsByIdAndTitle(id, null);
        if (authors != null && !authors.isEmpty()){
            return authors.get(0);
        }
        return null;
    }

    /**
     * 根据id 或 title 查询作者列表
     *
     * @param id
     * @param title
     * @return 可能会有多个重名的作者，故返回list 列表
     */
    @Override
    public List<Author> listAuthorsByIdAndTitle(String id, String title) {

        if (StringUtils.isBlank(id) && StringUtils.isBlank(title)){
            log.error("参数列表 id，title 不能同时为空");
            return null;
        }
        List<Author> list = authorMapper.findAuthorsByIdAndTitle(id, title);
        return list;
    }

    /**
     * 根据条件进行分页查询
     * @param id
     * @param title
     * @return
     */
    @Override
    public Page<Author> listAuthorsByPage(String id, String title) {
        return authorMapper.findAuthorByPage(id,title);
    }

    /**
     * 添加作者信息
     *
     * @param author
     * @return 添加是否成功
     */
    @Override
    public boolean addAuthor(Author author) {
        return authorMapper.insertSelective(author) == 1;
    }

    /**
     * 更新作者信息
     * @// FIXME: 2018/8/29 需要确定更新的 "" 与 null 的问题
     *
     * @param author
     * @return
     */
    @Override
    public boolean updateAuthor(Author author) {
        return authorMapper.updateByPrimaryKeySelective(author) == 1;
    }

    /**
     * 移除作者信息
     *
     * @param id
     * @return
     */
    @Override
    public boolean removeAuthor(String id) {
        return authorMapper.deleteByPrimaryKey(id) == 1;
    }

    /**
     * 从文件中批量添加作者信息
     *
     * @param file
     * @return 成功插入的条数
     */
    @Override
    public Integer batchAddAuthorFromFile(MultipartFile file) throws Exception {
        List<Author> authorList = CsvFileUtils.getAuthorsFromCSV(file.getInputStream());
        int num = 0;
        for (Author author: authorList){
            num += authorMapper.insertSelective(author);
        }
        return num;
    }

    /**
     * 批量添加作者信息
     * @// TODO: 2018/8/31 是否有必要，还要看需求，做需要做适当的限制，数据需要分段插入
     *
     * @param authors
     * @return
     */
    @Override
    @Deprecated
    public Integer batchAddAuthor(List<Author> authors) {
        return null;
    }
}
