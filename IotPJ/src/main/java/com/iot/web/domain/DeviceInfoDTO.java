package com.iot.web.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter 
@Setter
@ToString 
public class DeviceInfoDTO {
    private String deviceId;
    private String deviceName;
    private String startDate;
    private String endDate;
    private String isActive;
    private int orderId;
}
