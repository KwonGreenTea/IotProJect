package com.iot.web.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.iot.web.domain.SensorDataRealtimeDTO;

@Mapper
public interface SensorMapper {

	void insertData(SensorDataRealtimeDTO dataDTO);

	List<SensorDataRealtimeDTO> retrieveDataList(String deviceId);

	String retrieveOrderIdByDeviceId(String deviceId);
	
	String retrieveUserIdByDeviceId(String deviceId);

	String retrieveDelivIdByOrderId(String orderId);

	List<String> selectDevicesNoDataForSeconds(@Param("seconds") int seconds);

	int updateSersorData(@Param("dataDTO") SensorDataRealtimeDTO dataDTO, @Param("data") String json);
	
}
