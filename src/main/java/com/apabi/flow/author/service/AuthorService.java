package com.apabi.flow.author.service;

import com.apabi.flow.author.model.Author;
import com.github.pagehelper.Page;
import org.apache.http.auth.AUTH;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 功能描述： <br>
 * <作者 服务接口>
 *
 * @author supeng
 * @date 2018/8/27 10:47
 * @since 1.0.0
 */
public interface AuthorService {

    /**
     * 获取作者信息
     * @param id
     * @return
     */
    Author getAuthorById(String id);

    /**
     * 根据id 或 title 查询作者列表
     * @param id
     * @param title
     * @return 可能会有多个重名的作者，故返回list 列表
     */
    List<Author> listAuthorsByIdAndTitle(String id, String title);

    /**
     * 分页查询作者信息
     * @param id
     * @param title
     * @return
     */
    Page<Author> listAuthorsByPage(String id, String title);

    /**
     * 添加作者信息
     * @param author
     * @return 更新是否成功
     */
    boolean addAuthor(Author author);

    /**
     * 更新作者信息
     * @param author
     * @return
     */
    boolean updateAuthor(Author author);

    /**
     * 移除作者信息
     * @param id
     * @return
     */
    boolean removeAuthor(String id);

    /**
     * 从文件中批量添加作者信息
     * @param file
     * @return
     */
    Integer batchAddAuthorFromFile(MultipartFile file) throws Exception;

    /**
     * 批量添加作者信息
     * @param authors
     * @return
     */
    Integer batchAddAuthor(List<Author> authors);
}
