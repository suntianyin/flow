package com.apabi.flow.match_data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author pipi
 * @Date 2019-1-9 11:10
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApabiBookMetaDoubanChecker {
    private String doubanId;

    private String metaId;

    private String doubanTitle;

    private String apabiMetaTitle;

    private String doubanAuthor;

    private String apabiMetaAuthor;

    private String doubanPublisher;

    private String apabiMetaPublisher;
}
