package com.iot.web.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter 
@Setter
@ToString 
public class ProductInfoDTO {
    private Long productId;
    private String sellerId;
    private String productName;
    private Long price;
    private Double minTemperature;
    private Double maxTemperature;
    private Integer minHumidity;
    private Integer maxHumidity;
}
