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
    private Integer productId;
    private String sellerId;
    private String productName;
    private Integer price;
    private Double minTemperature;
    private Double maxTemperature;
    private Integer minHumidity;
    private Integer maxHumidity;
    private String imageUrl;
}
