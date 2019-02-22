package com.apabi.flow.match_data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author pipi
 * @Date 2019-2-21 11:23
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApabiBookMetaNlcMatched {
    private String metaId;

    private String nlcMarcId;

    private String isbn;

    private String isbn13;

    private String isbn10;

    private String apabiTitle;

    private String apabiTitleClean;

    private String nlcMarcTitle;

    private String apabiAuthor;

    private String nlcMarcAuthor;

    private String nlcMarcAuthorClean;

    private String apabiPublisher;

    private String nlcMarcPublisher;
}
