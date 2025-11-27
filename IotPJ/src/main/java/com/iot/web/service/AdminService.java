package com.iot.web.service;

import java.util.List;

import com.iot.web.domain.DeviceInfoDTO;
import com.iot.web.domain.OrderInfoDTO;
import com.iot.web.domain.ProductInfoDTO;

public interface AdminService {

    // 주문/디바이스 관련
    List<OrderInfoDTO> retrieveOrderData();
    
    int updateOrderIsActive(String orderId);
    
    void updateDeviceId(String orderId, String startDate);

    DeviceInfoDTO retrieveDeviceData(String orderId);

    OrderInfoDTO retrieveOrderDataForOrderId(String orderId);

    // 상품 등록 관련
    void createProduct(ProductInfoDTO dto);
}
