package com.iot.web.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter 
@Setter
@ToString 
public class AlarmCodeDTO {
    private Long codeId;
    private String codeName;
    private String description;
    private String category;
}
