package com.apabi.flow.nlcmarc.service.impl;

import com.apabi.flow.nlcmarc.dao.ApabiBookSeriesDao;
import com.apabi.flow.nlcmarc.model.ApabiBookSeries;
import com.apabi.flow.nlcmarc.service.ApabiBookSeriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author pipi
 * @Date 2018/10/11 15:03
 **/
@Service
public class ApabiBookSeriesServiceImpl implements ApabiBookSeriesService {
    @Autowired
    private ApabiBookSeriesDao apabiBookSeriesDao;

    @Override
    public List<String> findAllTitles() {
        List<String> titles = apabiBookSeriesDao.findAllTitles();
        return titles;
    }

    @Override
    public void insert(ApabiBookSeries apabiBookSeries) {
        apabiBookSeriesDao.insert(apabiBookSeries);
    }
}
