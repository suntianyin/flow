package com.apabi.flow.nlcmarc_category.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @Author pipi
 * @Date 2018/11/21 9:53
 **/
@Data
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
public class NlcBookMarcCategory {
    private String id;

    private String name;

    private Integer page;

    private String status;
}
