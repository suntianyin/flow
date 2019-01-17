package com.apabi.flow.match_data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author pipi
 * @Date 2019-1-14 17:54
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApabiBookMetaNlcChecker {
    private String nlibraryId;

    private String metaId;

    private String nlcTitle;

    private String apabiMetaTitle;

    private String nlcAuthor;

    private String apabiMetaAuthor;

    private String nlcPublisher;

    private String apabiMetaPublisher;

    private String apabiMetaTitleClean;

    private String nlcAuthorClean;
}
