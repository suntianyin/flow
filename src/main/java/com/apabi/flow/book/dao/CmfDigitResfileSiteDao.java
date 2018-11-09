package com.apabi.flow.book.dao;

import com.apabi.flow.book.model.CmfDigitResfileSite;
import com.apabi.flow.book.model.CmfMeta;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author guanpp
 * @date 2018/11/7 10:20
 * @description
 */
@Repository
@Mapper
public interface CmfDigitResfileSiteDao {

    //新增数据
    int insertCmfDigitResfileSite(CmfDigitResfileSite cmfDigitResfileSite) throws Exception;

    //获取数据
    List<CmfDigitResfileSite> findCmfDigitResfileSiteByFileId(Integer fileId) throws Exception;
}
