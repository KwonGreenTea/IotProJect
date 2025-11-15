package com.iot.web.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.iot.web.domain.SensorDataRealtimeDTO;

@Mapper
public interface SensorMapper {

	void insertData(SensorDataRealtimeDTO dataDTO);

	List<SensorDataRealtimeDTO> retrieveDataList(String deviceId);
	
}
