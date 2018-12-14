package com.apabi.flow.auth.service.impl;

import com.apabi.flow.auth.dao.CopyrightAgreementMapper;
import com.apabi.flow.auth.model.CopyrightAgreement;
import com.apabi.flow.auth.service.AuthService;
import com.github.pagehelper.Page;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @author: sunty
 * @date: 2018/12/07 15:03
 * @description:
 */
@Service
public class AuthServiceImpl implements AuthService {

    @Autowired
    CopyrightAgreementMapper copyrightAgreementMapper;

    @Override
    public Page<CopyrightAgreement> listCopyrightAgreement(Map<String, Object> paramsMap) {
        return copyrightAgreementMapper.listCopyrightAgreement(paramsMap);
    }

    @Override
    public int add(CopyrightAgreement copyrightAgreement) {
        return copyrightAgreementMapper.insert(copyrightAgreement);
    }

    @Override
    public CopyrightAgreement selectByPrimaryKey(String caid) {
        return copyrightAgreementMapper.selectByPrimaryKey(caid);
    }

    @Override
    public int updateByPrimaryKeySelective(CopyrightAgreement record) {
        return copyrightAgreementMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int deleteByPrimaryKey(String caid) {
        return copyrightAgreementMapper.deleteByPrimaryKey(caid);
    }

    @Override
    public int saveAgreementFileNameAndPath(String caid, String filePath) {
//        CopyrightAgreement copyrightAgreement = copyrightAgreementMapper.selectByPrimaryKey(caid);
//        if(StringUtils.isNotBlank(copyrightAgreement.getAgreementFileName())||StringUtils.isNotBlank(copyrightAgreement.getAgreementFilePath())){
//            return -1;
//        }
        String[] split = filePath.split("/");
        String fileName=split[split.length-1];
        int i = copyrightAgreementMapper.updateFileNameAndFilePathByCaid(caid, fileName, filePath);
        return i;
    }

    @Override
    public List<CopyrightAgreement> findAll() {
        return copyrightAgreementMapper.findAll();
    }
}
