package com.gft.api.crawler.domain.entity;

/**
 * Enum for Crawl status
 */
public enum CrawlStatus {
    /**
     * Active process
     */
    ACTIVE("active"),

    /**
     * Completed process
     */
    DONE("done");

    private final String name;

    private CrawlStatus(String name) {
        this.name = name;
    }

    @Override public String toString() {
        return name;
    }
}
