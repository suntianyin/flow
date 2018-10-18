package com.apabi.flow.thematic.service;

import com.apabi.flow.thematic.model.ThematicSeries;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

/**
 * Created by pipi on 2018/8/21.
 */
public interface ThematicSeriesService {
    ThematicSeries findThematicSeriesById(String id);
    int addThematicSeries(ThematicSeries thematicSeries);
    List<ThematicSeries> findAllThematicSeries();
    void updateThematic(ThematicSeries thematicSeries);
    void addThematicSeriesDataFromExcel(MultipartFile file, String id) throws IOException;
}
