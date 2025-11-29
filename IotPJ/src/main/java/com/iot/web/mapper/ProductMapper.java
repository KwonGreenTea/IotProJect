package com.iot.web.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.iot.web.domain.SensorDataRealtimeDTO;

@Mapper
public interface ProductMapper {

	String retreiveProductDataYn(@Param("orderId") String orderId, @Param("dataDTO") SensorDataRealtimeDTO dataDTO);

}