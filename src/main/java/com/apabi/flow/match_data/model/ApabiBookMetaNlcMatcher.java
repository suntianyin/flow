package com.apabi.flow.match_data.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author pipi
 * @Date 2019-1-7 17:12
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ApabiBookMetaNlcMatcher {
    private String metaId;

    private String nlcMarcId;

    private String isbn13;

    private String isbn10;

    private String apabiTitle;

    private String nlcMarcTitle;

    private String apabiAuthor;

    private String nlcMarcAuthor;

    private String apabiPublisher;

    private String nlcMarcPublisher;
}
