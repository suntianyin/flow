package com.apabi.flow.auth.service;

import com.apabi.flow.auth.model.Resource;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Param;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface ResourceService {
    int deleteByPrimaryKey(String resrId);

    int insert(Resource record);

    int insertSelective(Resource record);

    Resource selectByPrimaryKey(String resrId);

    int updateByPrimaryKeySelective(Resource record);

    int updateByPrimaryKey(Resource record);

    Page<Resource> listResource(Map<String, Object> paramsMap);

    int updateByBooklistNum( String booklistNum,  String batchNum);

    List<Resource> listResource1(Map<String, Object> paramsMap);

    String writeData2Excel(int type,String fileName, String[] excelTitle, List<Resource> excelModelList, String sheet1, HttpServletResponse response) throws Exception;

    List<Resource> resolveResource(Map<Integer, Map<Object, Object>> data) throws Exception;

    boolean updateByBatchNumAndMetaId(List<Resource> list) throws Exception;
}