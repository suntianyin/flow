package com.apabi.flow.book.dao;

import com.apabi.flow.book.model.BookPage;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author supeng
 */
public interface BookPageMapper {
    /**
     * 物理删除，谨慎使用
     * @param id
     * @return
     */
    int deleteByPrimaryKey(String id);

    /**
     * 添加分页内容数据
     * @param record
     * @return
     */
    int insert(BookPage record);

    /**
     * 选择性插入数据
     * @param record
     * @return
     */
    int insertSelective(BookPage record);

    /**
     * 根据主键查询实体
     * @param id
     * @return
     */
    BookPage selectByPrimaryKey(String id);

    /**
     * 非空性 更新实体，在oracle 下，貌似不会更新 LOB 类型的字段
     * @param record
     * @return
     */
    int updateByPrimaryKeySelective(BookPage record);

    /**
     * 除主键外的其他全部字段更新，包括 LOB 字段
     * @param record
     * @return
     */
    int updateByPrimaryKeyWithBLOBs(BookPage record);

    /**
     * 除主键和LOB 类型 外的其他全部字段更新
     * @param record
     * @return
     */
    int updateByPrimaryKey(BookPage record);

    /**
     * 查找 metaId 数据集下的 页面信息列表，但不查询页面内容，按 pageId 排序
     * @param metaId
     * @return 返回除页面详细内容的其他字段信息列表
     */
    List<BookPage> findAllByMetaIdOrderByPageId(String metaId);

    /**
     * 查找 metaId 数据集下的 页面信息列表，包括内容，按 pageId 排序
     * @param metaId
     * @return 返回除页面详细内容的其他字段信息列表
     */
    List<BookPage> findAllByMetaIdOrderByPageIdWithContent(String metaId);

    /**
     * 查询当前 metaId 组下的 最大的 pageId，便于查询是否需要断点续传，该语句只适用于oracle，匹配其他数据库请自行适配
     * @param metaId
     * @return 返回前最大 pageId，如果为空，则返回0
     */
    Integer findMaxPageIdByMetaId(@Param("metaId") String metaId);

    /**
     * 查询某个数据集下的 某一页内容信息
     * @param metaId
     * @param pageId
     * @return 返回当前数据集下的 第 pageid 页的页面信息实体
     */
    BookPage findBookPageByMetaIdAndPageId(@Param("metaId") String metaId, @Param("pageId") Integer pageId);
    /**
     * 修改某个数据集下的 某一页内容信息
     * @param metaId
     * @param pageId
     * @return
     */
    Integer updateBookPageByMetaIdAndPageId(BookPage bookPage);

    Integer updataOrInsert(BookPage bookPage);
}