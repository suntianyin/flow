package com.apabi.flow.book.service;

import com.apabi.flow.book.model.BookPage;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * 功能描述： <br>
 * <有关 图书分页拉取和组装章节的接口>
 *
 * @author supeng
 * @date 2018/8/23 16:33
 * @since 1.0.0
 */
public interface BookPageService {

    /**
     * 通过xml，获取书苑图书页码信息
     * @param metaId
     * @return 返回插入页数
     * @throws Exception
     */
//    int insertShuyuanPageData(String metaId) throws Exception;

    /**
     * 获取 当前 metaId 下的 所有 页码列表信息
     * @param metaId
     * @return 返回页码列表
     */
    List<BookPage> queryAllBookPagesByMetaId(String metaId);

    /**
     * 查询特定 metaid 下的某一页 内容信息
     * @param metaid
     * @param pageid
     * @return 返回 bookPage 页面信息实体
     */
    BookPage getBookPageContentByMetaidAndPageid(String metaid, Integer pageid);

    /**
     * 通过xml，获取书苑图书元数据
     * @param metaId
     * @return
     * @throws Exception
     */
    int processBookFromPage2Chapter(String metaId) throws Exception;

    /**
     * 根据配置文件自动拉取分页数据
     * @return 返回本次拉取的总页数
     */
    int autoFetchPageData();

    int autoFetchPageDataAgain();

    /**
     * 根据配置文件自动组装章节数据数据
     * @return 返回本次拉取的总页数
     */
    int autoProcessBookFromPage2Chapter();

//    /**
//     * 通过xml，获取书苑图书元数据
//     * @param metaId
//     * @return
//     * @throws Exception
//     */
//    int insertShuyuanData(String metaId) throws Exception;

    int batchAddAuthorFromFile(Map<Integer, Map<Object, Object>> data) throws Exception;
}
