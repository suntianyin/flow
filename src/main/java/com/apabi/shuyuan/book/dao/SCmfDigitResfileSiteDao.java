package com.apabi.shuyuan.book.dao;

import com.apabi.shuyuan.book.model.SCmfDigitResfileSite;
import com.apabi.shuyuan.book.model.SCmfMeta;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author guanpp
 * @date 2018/11/7 13:38
 * @description
 */
@Repository
@Mapper
public interface SCmfDigitResfileSiteDao {

    //获取数据
    List<SCmfDigitResfileSite> findSCmfDigitResfileSiteByFileId(Integer fileId)throws Exception;
}
