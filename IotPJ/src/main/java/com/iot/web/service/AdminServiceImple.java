package com.iot.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iot.web.domain.DeviceInfoDTO;
import com.iot.web.domain.OrderInfoDTO;
import com.iot.web.mapper.AdminMapper;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class AdminServiceImple implements AdminService {
	
	@Autowired
	private AdminMapper adminMapper;

	@Override
	public List<OrderInfoDTO> retrieveOrderData() {
		return adminMapper.retrieveOrderData();
	}
	
	@Override
	public void updateDeviceId(String orderId, String startDate) {
		adminMapper.updateDeviceId(orderId, startDate);
	}

	@Override
	public DeviceInfoDTO retrieveDeviceData(String orderId) {
		return adminMapper.retrieveDeviceData(orderId);
	}

	@Override
	public OrderInfoDTO retrieveOrderDataForOrderId(String orderId) {
		return adminMapper.retrieveOrderDataForOrderId(orderId);
	}

	
	
	
}
