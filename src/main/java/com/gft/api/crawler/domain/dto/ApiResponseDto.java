package com.gft.api.crawler.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Class for managing the http object body responses
 */
@Data
@AllArgsConstructor
public class ApiResponseDto {

    /**
     * code field
     */
    private int code;

    /**
     * message field
     */
    private String message;
}
