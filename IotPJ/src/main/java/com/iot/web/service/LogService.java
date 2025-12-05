package com.iot.web.service;

import java.util.List;
import java.util.Map;

import com.iot.web.domain.AlarmLogDTO;

public interface LogService {

	void insertLog(AlarmLogDTO logDTO);

	Map<String, Integer> retrieveErrData();

	List<AlarmLogDTO> retrieveAllDataForUserId(String userId);

   
}
