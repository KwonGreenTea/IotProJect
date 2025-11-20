package com.iot.web.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter 
@Setter
@ToString 
public class SensorDataRealtimeDTO {
    private int recordId;
    private String deviceId;
    private String measuredAt;
    private Double temperature;
    private int humidity;
    private Double maxTemperature;
    private int maxHumidity;
}
