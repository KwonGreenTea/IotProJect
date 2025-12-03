package com.iot.web.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.iot.web.domain.AlarmLogDTO;

@Mapper
public interface LogMapper {

	void insertLog(@Param("logDTO") AlarmLogDTO logDTO);

	List<AlarmLogDTO> retrieveErrData(@Param("startDate") String startDate, @Param("endDate") String endDate);
	
	
}