package com.gft.api.crawler.domain.dto;

import lombok.Data;

@Data
public class DeliveryPayload {
    private String address;
    private String productName;
    private String customerName;
}
