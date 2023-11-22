package com.gft.api.crawler.domain.dto;

import lombok.Builder;
import lombok.Data;
import java.util.Set;

/**
 * Dto to export crawl data result
 */

@Data
@Builder
public class CrawlDataDto {

    /**
     * id field
     */
    private String id;

    /**
     * url field
     */
    private String url;

    /**
     * keyword field
     */
    private String keyword;

    /**
     * status field
     */
    private String status;

    /**
     * matched urls field
     */
    private Set<String> matchedUrls;

}
