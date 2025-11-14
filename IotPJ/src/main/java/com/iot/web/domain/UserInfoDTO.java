package com.iot.web.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter 
@Setter
@ToString 
public class UserInfoDTO {
    private String userId;
    private String passwordHash;
    private String userName;
    private String address;
    private String phoneNumber;
}
