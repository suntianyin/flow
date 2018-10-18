package com.apabi.flow.nlcmarc.service;

import com.apabi.flow.nlcmarc.model.ApabiBookSeriesData;

import java.io.IOException;
import java.util.List;

/**
 * @Author pipi
 * @Date 2018/10/10 17:01
 **/
public interface ApabiBookSeriesDataService {
    List<ApabiBookSeriesData> parse(String info) throws IOException;
    void insert(ApabiBookSeriesData apabiBookSeriesData);
}
