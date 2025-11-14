package com.iot.web.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter 
@Setter
@ToString 
public class SellerInfoDTO {
    private String sellerId;
    private String passwordHash;
    private String sellerName;
    private String storeName;
}
