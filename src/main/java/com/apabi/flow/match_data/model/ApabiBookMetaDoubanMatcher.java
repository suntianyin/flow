package com.apabi.flow.match_data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author pipi
 * @Date 2019-1-2 13:46
 **/
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApabiBookMetaDoubanMatcher {
    private String metaId;

    private String doubanId;

    private String isbn13;

    private String isbn10;

    private String apabiTitle;

    private String doubanTitle;

    private String apabiAuthor;

    private String doubanAuthor;

    private String apabiPublisher;

    private String doubanPublisher;
}
