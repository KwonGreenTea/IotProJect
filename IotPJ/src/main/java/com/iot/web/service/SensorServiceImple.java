package com.iot.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iot.web.domain.SensorDataRealtimeDTO;
import com.iot.web.mapper.SensorMapper;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class SensorServiceImple implements SensorService {
	
	@Autowired
	private SensorMapper sensorMapper;

	@Override
	public void insertData(SensorDataRealtimeDTO dataDTO) {
		sensorMapper.insertData(dataDTO);
	}

	@Override
	public List<SensorDataRealtimeDTO> retrieveDataList(String deviceId) {
		return sensorMapper.retrieveDataList(deviceId);
	}

	@Override
	public String retrieveOrderIdByDeviceId(String deviceId) {
		return sensorMapper.retrieveOrderIdByDeviceId(deviceId);
	}
	
}
