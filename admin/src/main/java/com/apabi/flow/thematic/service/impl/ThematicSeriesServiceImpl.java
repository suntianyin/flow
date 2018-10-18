package com.apabi.flow.thematic.service.impl;

import com.apabi.flow.common.UUIDCreater;
import com.apabi.flow.thematic.dao.ThematicSeriesDao;
import com.apabi.flow.thematic.dao.ThematicSeriesDataDao;
import com.apabi.flow.thematic.model.ThematicSeries;
import com.apabi.flow.thematic.model.ThematicSeriesData;
import com.apabi.flow.thematic.service.ThematicSeriesService;
import com.apabi.flow.thematic.util.ReadExcelUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author pipi
 * @Date 2018/8/21 17:56
 **/
@Service
public class ThematicSeriesServiceImpl implements ThematicSeriesService {

    @Autowired
    ThematicSeriesDao thematicDao;

    @Autowired
    ThematicSeriesDataDao thematicSeriesDataDao;

    @Override
    public ThematicSeries findThematicSeriesById(String id) {
        ThematicSeries thematicSeries = thematicDao.findThematicSeriesById(id);
        return thematicSeries;
    }

    @Override
    public int addThematicSeries(ThematicSeries thematicSeries) {
        int i = thematicDao.addThematicSeries(thematicSeries);
        return i;
    }

    @Override
    public List<ThematicSeries> findAllThematicSeries() {
        List<ThematicSeries> allThematicSeries = thematicDao.findAllThematicSeries();
        return allThematicSeries;
    }

    @Override
    public void updateThematic(ThematicSeries thematicSeries) {
        thematicDao.updateThematicSeries(thematicSeries);
    }

    @Override
    public void addThematicSeriesDataFromExcel(MultipartFile file, String id) throws IOException {
        // 读取Excel工具类
        InputStream inputStream = file.getInputStream();
        String fileName = file.getOriginalFilename();
        ReadExcelUtils readExcelUtils = new ReadExcelUtils(inputStream, fileName);
        ThematicSeries thematicSeries = thematicDao.findThematicSeriesById(id);
        // 读取Excel中的内容
        Map<Integer, Map<Object, Object>> data = readExcelUtils.getDataByInputStream();
        for (Map.Entry<Integer, Map<Object, Object>> entry : data.entrySet()) {
            ThematicSeriesData thematicSeriesData = new ThematicSeriesData();
            // 设置主id
            thematicSeriesData.setId(UUIDCreater.nextId());
            // 设置thematicId
            thematicSeriesData.setThematicId(id);
            String metaId = (String) entry.getValue().get("图书ID");
            String title = (String) entry.getValue().get("书名");
            String author = (String) entry.getValue().get("作者");
            String ISBN = (String) entry.getValue().get("ISBN");
            String ISBN13 = (String) entry.getValue().get("ISBN13");
            String publisher = (String) entry.getValue().get("出版社");

            if (ISBN == null) {
                ISBN = "";
            }
            if (metaId == null) {
                metaId = "";
            }
            if (title == null) {
                title = "";
            }
            if (author == null) {
                author = "";
            }
            if (publisher == null) {
                publisher = "";
            }
            if (ISBN13 == null) {
                if (ISBN != null && ISBN.contains("-")) {
                    ISBN13 = ISBN.replaceAll("-", "");
                }
                if (ISBN != null && !ISBN.contains("-")) {
                    ISBN13 = ISBN;
                }
                if (ISBN == null) {
                    ISBN13 = "";
                }
            }
            thematicSeriesData.setIsbn(ISBN);
            thematicSeriesData.setMetaId(metaId);
            thematicSeriesData.setTitle(title);
            thematicSeriesData.setAuthor(author);
            thematicSeriesData.setPublisher(publisher);
            thematicSeriesData.setIsbn13(ISBN13);
            // 需要根据实际操作用户来设置
            UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = userDetails.getUsername();
            thematicSeriesData.setOperator(username);
            thematicSeriesData.setUpdateTime(new Date());
            thematicSeriesData.setCreateTime(new Date());
            // 添加thematicSeriesData
            if (!"".equalsIgnoreCase(title) && title != null) {
                thematicSeriesDataDao.addThematicSeriesData(thematicSeriesData);
            }
        }
        inputStream.close();
    }
}
