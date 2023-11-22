package com.gft.api.crawler.domain.dto;

import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Dto for crawl request body
 */
@Data
public class CrawlPayload {

    /**
     * url field
     */
    private String url;

    /**
     * keyword field
     */
    @Size.List({
            @Size(min = 4, message = "{validation.keyword.size.too-short}"),
            @Size(max = 32, message = "{validation.keyword.size.too-long}")
    })
    private String keyword;


}
