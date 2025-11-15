package com.iot.web.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter 
@Setter
@ToString 
public class OrderInfoDTO {
    private int orderId;
    private int productId;
    private String sellerId;
    private String userId;
    private String deliveryId;
    private String orderedAt;
    private int totalPrice;
    private String isActive;
}
