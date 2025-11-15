package com.iot.web.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.iot.web.domain.DeviceInfoDTO;
import com.iot.web.domain.OrderInfoDTO;

@Mapper
public interface AdminMapper {

	List<OrderInfoDTO> retrieveOrderData();

	DeviceInfoDTO retrieveDeviceData(String orderId);

	void updateDeviceId(@Param("orderId") String orderId, @Param("startDate") String startDate);

	OrderInfoDTO retrieveOrderDataForOrderId(String orderId);

}
