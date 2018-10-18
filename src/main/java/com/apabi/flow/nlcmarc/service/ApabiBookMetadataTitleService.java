package com.apabi.flow.nlcmarc.service;

import com.apabi.flow.nlcmarc.model.ApabiBookMetadataTitle;

/**
 * Created by pipi on 2018/10/10.
 */
public interface ApabiBookMetadataTitleService {
    void insert(ApabiBookMetadataTitle apabiBookMetadataTitle);
    ApabiBookMetadataTitle findById(String id);
    ApabiBookMetadataTitle parseTitle(String info);
}
