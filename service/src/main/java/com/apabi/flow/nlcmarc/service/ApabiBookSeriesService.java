package com.apabi.flow.nlcmarc.service;

import com.apabi.flow.nlcmarc.model.ApabiBookSeries;

import java.util.List;

/**
 * Created by pipi on 2018/10/11.
 */
public interface ApabiBookSeriesService {
    List<String> findAllTitles();
    void insert(ApabiBookSeries apabiBookSeries);
}
