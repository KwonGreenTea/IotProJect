package com.iot.web.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iot.web.domain.AlarmLogDTO;
import com.iot.web.mapper.LogMapper;

@Service
public class LogServiceImple implements LogService {

    @Autowired
    private LogMapper logMapper;

	@Override
	public void insertLog(AlarmLogDTO logDTO) {
		logMapper.insertLog(logDTO);
	}

	@Override
	public Map<String, Integer> retrieveErrData() {
		LocalDate today = LocalDate.now();
	    LocalDate start = today.minusDays(6);      // 포함
	    LocalDate end   = today.plusDays(1);       // 미포함(익일)

	    DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd");
	    List<AlarmLogDTO> rows = logMapper.retrieveErrData(start.format(f), end.format(f));

	    Map<String, Integer> dayToCnt = new HashMap<>();
	    for (AlarmLogDTO r : rows) {
	    	dayToCnt.put(r.getResultDay(), r.getResultCnt());
	    }

	    // 최근 7일을 고정 순서로 만들고, 없는 날짜는 0
	    Map<String, Integer> filled = new LinkedHashMap<>();
	    for (int i = 0; i < 7; i++) {
	        String day = start.plusDays(i).format(f);
	        filled.put(day, dayToCnt.getOrDefault(day, 0));
	    }
	    return filled;
	}

	@Override
	public List<AlarmLogDTO> retrieveAllDataForUserId(String userId) {
		return logMapper.retrieveAllDataForUserId(userId);
	}

	
}
