package com.iot.web.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter 
@Setter
@ToString
public class OrderInfoDTO {
    private Long orderId;
    private Long productId;
    private String sellerId;
    private String userId;
    private String deliveryId;
    private LocalDateTime orderedAt;
    private Long totalPrice;
}
