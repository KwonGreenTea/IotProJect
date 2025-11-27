package com.iot.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iot.web.domain.DeviceInfoDTO;
import com.iot.web.domain.OrderInfoDTO;
import com.iot.web.domain.ProductInfoDTO;
import com.iot.web.mapper.AdminMapper;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class AdminServiceImple implements AdminService {

    @Autowired
    private AdminMapper adminMapper;

    // 주문/디바이스 관련 -----------------------------

    @Override
    public List<OrderInfoDTO> retrieveOrderData() {
        return adminMapper.retrieveOrderData();
    }
    
    @Override
	public int updateOrderIsActive(String orderId) {
		 log.info("updateOrderIsActive()");
		return adminMapper.updateOrderIsActive(orderId);
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

    // 상품 등록 -------------------------------------

    @Override
    public void createProduct(ProductInfoDTO dto) {
        log.info("createProduct 호출: {}", dto);
        adminMapper.insertProduct(dto);
    }

	
}
