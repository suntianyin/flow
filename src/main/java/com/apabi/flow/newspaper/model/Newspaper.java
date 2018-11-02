package com.apabi.flow.newspaper.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author pipipi
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Newspaper {
    private String url;
    private String title;
    private String abstract_;
    private String htmlContent;
}