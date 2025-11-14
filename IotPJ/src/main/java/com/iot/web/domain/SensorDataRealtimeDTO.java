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
public class SensorDataRealtimeDTO {
    private Long recordId;
    private String deviceId;
    private LocalDateTime measuredAt;
    private Double temperature;
    private Integer humidity;
}
