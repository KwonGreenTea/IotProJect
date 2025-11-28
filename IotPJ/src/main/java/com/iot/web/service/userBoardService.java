package com.iot.web.service;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;

import com.iot.web.domain.DeviceInfoDTO;
import com.iot.web.domain.OrderInfoDTO;
import com.iot.web.domain.ProductInfoDTO;

public interface userBoardService {
	List<ProductInfoDTO> selectProductList(); // 전체 상품 조회
	
	ProductInfoDTO selectProductById(@PathVariable Integer productId); // 특정 상품 조회
	
	int createOrder(OrderInfoDTO orderInfoDTO); 	// 물품 구매 
	
	List<OrderInfoDTO> selectOrderList(); // 구매 물품 조회
	
	 DeviceInfoDTO retrieveDeviceData(String orderId); // 센서(장비) 조회

	 OrderInfoDTO retrieveOrderDataForOrderId(String orderId); // 주문 상세 데이터 조회
}
