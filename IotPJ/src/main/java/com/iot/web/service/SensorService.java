package com.iot.web.service;

import java.util.List;

import com.iot.web.domain.SensorDataRealtimeDTO;

public interface SensorService {
	void insertData(SensorDataRealtimeDTO dataDTO);

	List<SensorDataRealtimeDTO> retrieveDataList(String deviceId);

	String retrieveOrderIdByDeviceId(String deviceId);
	
	String retrieveUserIdByDeviceId(String deviceId);

	String retrieveDelivIdByOrderId(String orderId);
}
