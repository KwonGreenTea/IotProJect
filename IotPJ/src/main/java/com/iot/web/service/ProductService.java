package com.iot.web.service;

import com.iot.web.domain.SensorDataRealtimeDTO;

public interface ProductService {

	String retreiveProductDataYn(String orderId, SensorDataRealtimeDTO dataDTO);

}
