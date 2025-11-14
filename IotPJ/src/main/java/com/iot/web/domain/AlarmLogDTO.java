package com.iot.web.domain;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter 
@Setter
@ToString 
public class AlarmLogDTO {
    private Long logId;
    private Long codeId;
    private String deliveryId;
    private Long recordId;
    private String deviceId;
    private LocalDateTime loggedAt;
    private String isChecked;
    private String fieldValue;
}
