package com.apabi.flow.auth.service;

import com.apabi.flow.auth.model.CopyrightAgreement;
import com.github.pagehelper.Page;

import java.util.List;
import java.util.Map;

/**
 * @author: sunty
 * @date: 2018/12/07 15:02
 * @description:
 */

public interface AuthService {
    Page<CopyrightAgreement> listCopyrightAgreement(Map<String, Object> paramsMap);

    int add(CopyrightAgreement copyrightAgreement);

    CopyrightAgreement selectByPrimaryKey(String caid);

    int updateByPrimaryKeySelective(CopyrightAgreement record);

    int deleteByPrimaryKey(String caid);

    int saveAgreementFileNameAndPath(String caid, String filePath);

    List<CopyrightAgreement> findAll();

    CopyrightAgreement findByCopyrightOwnerId(String copyrightOwnerId);

    int updateStatusByPrimaryKeySelective(CopyrightAgreement record);


}
