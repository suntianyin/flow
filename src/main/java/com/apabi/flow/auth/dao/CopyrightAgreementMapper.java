package com.apabi.flow.auth.dao;

import com.apabi.flow.auth.model.CopyrightAgreement;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface CopyrightAgreementMapper {
    int deleteByPrimaryKey(String caid);

    int insert(CopyrightAgreement record);

    int insertSelective(CopyrightAgreement record);

    CopyrightAgreement selectByPrimaryKey(String caid);

    int updateByPrimaryKeySelective(CopyrightAgreement record);

    int updateByPrimaryKey(CopyrightAgreement record);

    Page<CopyrightAgreement> listCopyrightAgreement(Map map);

    int updateFileNameAndFilePathByCaid(@Param("caid")String caid, @Param("agreementFileName")String agreementFileName, @Param("agreementFilePath")String agreementFilePath);

    List<CopyrightAgreement> findAll();

    List<CopyrightAgreement> findByCopyrightOwnerId(String copyrightOwnerId);

    //获取当前所有版权期内的协议
    List<CopyrightAgreement> findAgreementValidity();

    int updateStatusByPrimaryKeySelective(CopyrightAgreement record);
}