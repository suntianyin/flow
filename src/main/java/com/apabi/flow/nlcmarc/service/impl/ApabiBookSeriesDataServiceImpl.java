package com.apabi.flow.nlcmarc.service.impl;

import com.apabi.flow.nlcmarc.dao.ApabiBookSeriesDataDao;
import com.apabi.flow.nlcmarc.model.ApabiBookSeriesData;
import com.apabi.flow.nlcmarc.service.ApabiBookSeriesDataService;
import com.apabi.flow.nlcmarc.util.ParseMarcSeriesDataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @Author pipi
 * @Date 2018/10/10 17:01
 **/
@Service
public class ApabiBookSeriesDataServiceImpl implements ApabiBookSeriesDataService {
    @Autowired
    private ApabiBookSeriesDataDao apabiBookSeriesDataDao;

    @Override
    public List<ApabiBookSeriesData> parse(String info) throws IOException {
        List<ApabiBookSeriesData> apabiBookSeriesDataList = ParseMarcSeriesDataUtil.parseMarcTitle(info);
        return apabiBookSeriesDataList;
    }

    @Override
    public void insert(ApabiBookSeriesData apabiBookSeriesData) {
        apabiBookSeriesDataDao.insert(apabiBookSeriesData);
    }
}
