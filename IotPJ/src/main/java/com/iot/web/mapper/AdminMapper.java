package com.iot.web.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.iot.web.domain.DeviceInfoDTO;
import com.iot.web.domain.OrderInfoDTO;
import com.iot.web.domain.ProductInfoDTO;

@Mapper
public interface AdminMapper {

	List<OrderInfoDTO> retrieveOrderData();
	
	int updateOrderIsActive(String orderId);

	DeviceInfoDTO retrieveDeviceData(String orderId);

	void updateDeviceId(@Param("orderId") String orderId, @Param("startDate") String startDate);

	OrderInfoDTO retrieveOrderDataForOrderId(String orderId);

	void insertProduct(ProductInfoDTO product);

	int updateOrderIsActiveFn(String orderId);
}