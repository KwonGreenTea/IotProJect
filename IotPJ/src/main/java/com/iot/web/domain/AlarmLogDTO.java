package com.iot.web.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter 
@Setter
@ToString 
public class AlarmLogDTO {
    private int logId;
    private String deliveryId;
    private int orderId;
    private String deviceId;
    private String loggedAt;
    private String isChecked;
    private String adminChecked;
    private String sensorData;
    private String logCd; // 1 : 정상 로그 , 2 : 비 정상 로그, 3 :
    private String userId;
    
    // 쿼리 결과
    private String resultDay;
    private Integer resultCnt;
}
