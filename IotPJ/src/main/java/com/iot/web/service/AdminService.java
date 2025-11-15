package com.iot.web.service;

import java.util.List;

import com.iot.web.domain.DeviceInfoDTO;
import com.iot.web.domain.OrderInfoDTO;

public interface AdminService {
	List<OrderInfoDTO> retrieveOrderData();
	
	void updateDeviceId(String orderId, String startDate);

	DeviceInfoDTO retrieveDeviceData(String orderId);

	OrderInfoDTO retrieveOrderDataForOrderId(String orderId);

	
}
