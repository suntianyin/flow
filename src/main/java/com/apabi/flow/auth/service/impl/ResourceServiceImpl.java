package com.apabi.flow.auth.service.impl;

import com.apabi.flow.auth.constant.ResourceStatusEnum;
import com.apabi.flow.auth.dao.ResourceMapper;
import com.apabi.flow.auth.model.Resource;
import com.apabi.flow.auth.service.ResourceService;
import com.apabi.flow.douban.util.StringToolUtil;
import com.apabi.flow.processing.constant.BizException;
import com.github.pagehelper.Page;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author: sunty
 * @date: 2018/12/07 15:03
 * @description:
 */
@Service
public class ResourceServiceImpl implements ResourceService {

    @Autowired
    ResourceMapper resourceMapper;


    @Override
    public int deleteByPrimaryKey(Integer resrId) {
        return resourceMapper.deleteByPrimaryKey(resrId);
    }

    @Override
    public int insert(Resource record) {
        return resourceMapper.insert(record);
    }

    @Override
    public int insertSelective(Resource record) {
        return resourceMapper.insertSelective(record);
    }

    @Override
    public Resource selectByPrimaryKey(Integer resrId) {
        return resourceMapper.selectByPrimaryKey(resrId);
    }

    @Override
    public int updateByPrimaryKeySelective(Resource record) {
        return resourceMapper.updateByPrimaryKeySelective(record);
    }

    @Override
    public int updateByPrimaryKey(Resource record) {
        return resourceMapper.updateByPrimaryKey(record);
    }

    @Override
    public Page<Resource> listResource(Map<String, Object> paramsMap) {
        return resourceMapper.listResource(paramsMap);
    }

    @Override
    public int updateByBooklistNum(String booklistNum, String batchNum) {
        return resourceMapper.updateByBooklistNum(booklistNum,batchNum);
    }

    @Override
    public List<Resource> listResource1(Map<String, Object> paramsMap) {
        return resourceMapper.listResource1(paramsMap);
    }
    @Override
    public String writeData2Excel(int type,String fileName, String[] excelTitle, List<Resource> list, String sheetName, HttpServletResponse response) throws Exception {
        Workbook workbook = null;
        if (fileName.toLowerCase().endsWith("xls")) {//2003
            workbook = new XSSFWorkbook();
        } else if (fileName.toLowerCase().endsWith("xlsx")) {//2007
            workbook = new HSSFWorkbook();
        } else {
            throw new BizException("文件名后缀必须是 .xls 或 .xlsx");
        }
        ServletOutputStream out = null;

        //create sheet
        Sheet sheet = workbook.createSheet(sheetName);
        //遍历数据集，将其写入excel中
        try {
            //写表头数据
            Row titleRow = sheet.createRow(0);
            for (int i = 0; i < excelTitle.length; i++) {
                //创建表头单元格,填值
                titleRow.createCell(i).setCellValue(excelTitle[i]);
            }
                autoCreateRow(sheet, list);
            // 设置响应头
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            out = response.getOutputStream();
            workbook.write(out);
            out.flush();
            return fileName;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new Exception("文件未找到！");
        } catch (IOException e) {
            e.printStackTrace();
            throw new Exception("服务器异常！");
        } catch (Exception e) {
            throw new Exception("数据解析异常！");
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 自动添加 excel 行数据
     *
     * @param sheet
     * @param
     */
    private void autoCreateRow(Sheet sheet, List<Resource> list1) throws Exception {

        List<String> list = null;
        // 遍历所有数据
        for (int i = 0; i < list1.size(); i++) {
            Resource bem = list1.get(i);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            list =  Arrays.asList(String.valueOf(i+1),bem.getIdentifier(), bem.getIsbn(), bem.getTitle(),bem.getCreator(), bem.getPublisher(), bem.getIssuedDate()== null ? "" : new SimpleDateFormat("yyyy/MM/dd").format(bem.getIssuedDate()),
                   String.valueOf(bem.getPaperPrice()) , String.valueOf(bem.getePrice()),bem.getNcStartDate()== null ? "" : new SimpleDateFormat("yyyy/MM/dd").format(bem.getNcStartDate()),bem.getNcEndDate()== null ? "" : new SimpleDateFormat("yyyy/MM/dd").format(bem.getNcEndDate()),bem.getMetaId(),bem.getBatchNum());
            Row row = sheet.createRow(i + 1);
            // 逐个单元格添加数据
            for (int j = 0; j < list.size(); j++) {
                row.createCell(j).setCellValue(list.get(j));
            }
        }
    }

    @Override
    public List<Resource> resolveResource(Map<Integer, Map<Object, Object>> data) throws Exception {
        List<Resource> list = new ArrayList<>(data.size());

        try {
            for (Map.Entry<Integer, Map<Object, Object>> entry : data.entrySet()) {
                Resource resource = new Resource();
                String ePrice = (String) entry.getValue().get("电子书价格（元）");
                String metaId = (String) entry.getValue().get("唯一标识（MetaId）");
                String batchNum = (String) entry.getValue().get("批次号");
                String authStartDate = (String) entry.getValue().get("版权期限起始时间");
                String authEndDate = (String) entry.getValue().get("版权期限终止时间");

                if (StringUtils.isBlank(ePrice)) {
                    throw new BizException("电子书价格（元）不能为空，请检查数据重新导入！");
                }
                if (StringUtils.isBlank(metaId)) {
                    throw new BizException("唯一标识（MetaId）不能为空，请检查数据重新导入！");
                }
                if (StringUtils.isBlank(batchNum)) {
                    throw new BizException("批次号不能为空，请检查数据重新导入！");
                }
                if (StringUtils.isBlank(authStartDate)) {
                    throw new BizException("版权期限起始时间不能为空，请检查数据重新导入！");
                }
                if (StringUtils.isBlank(authEndDate)) {
                    throw new BizException("版权期限终止时间不能为空，请检查数据重新导入！");
                }

                resource.setePrice(Double.valueOf(ePrice));
                resource.setMetaId(metaId);
                resource.setBatchNum(batchNum);
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//                resource.setAuthStartDate(sdf.parse(authStartDate));
//                resource.setAuthEndDate(sdf.parse(authEndDate));
                resource.setAuthStartDate(authStartDate);
                resource.setAuthEndDate(authEndDate);
                UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
                resource.setOperator(userDetails.getUsername());
                resource.setOperateDate(new Date());
                resource.setStatus(ResourceStatusEnum.OUTSIDE);
                list.add(resource);
            }
        } catch (Exception e) {
            if (e instanceof BizException) {
                throw new Exception(e.getMessage());
            } else {
                throw new Exception("数据解析异常，请检查文件合适是否正确或联系管理员！");
            }
        }
        return list;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED, rollbackFor = Exception.class)
    public boolean updateByBatchNumAndMetaId(List<Resource> list) throws Exception {
        if (list == null) {
            return false;
        }
        int size = 0;
        for (Resource resource : list) {
            try {
                size += resourceMapper.updateByBatchNumAndMetaId(resource);
            } catch (DataAccessException e) {
                throw new Exception("操作失败！", e);
            }
        }
        if (size == list.size()) {
            return true;
        } else {
            throw new Exception("数据条目不一致！");
        }
    }
}
