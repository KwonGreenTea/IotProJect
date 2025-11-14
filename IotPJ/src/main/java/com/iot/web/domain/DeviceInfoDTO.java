package com.iot.web.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;

@NoArgsConstructor
@Getter 
@Setter
@ToString
public class DeviceInfoDTO {
    private String deviceId;
    private String deviceName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String isActive;
}
