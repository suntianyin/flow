package com.apabi.flow.nlcmarc.service;

import com.apabi.flow.nlcmarc.model.ApabiBookMetadataAuthor;

import java.util.List;

/**
 * Created by pipi on 2018/9/26.
 */
public interface ApabiBookMetadataAuthorService {
    void insert(ApabiBookMetadataAuthor apabiBookMetadataAuthor);
    ApabiBookMetadataAuthor findById(String id);
    List<ApabiBookMetadataAuthor> parseAuthor(String info);
}
