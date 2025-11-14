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
public class DeliveryInfoDTO {
    private String deliveryId;
    private Long orderId;
    private Long productId;
    private String deviceId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private String hasAlarm;
}
