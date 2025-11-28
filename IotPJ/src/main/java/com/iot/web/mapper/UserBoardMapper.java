package com.iot.web.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.iot.web.domain.DeviceInfoDTO;
import com.iot.web.domain.OrderInfoDTO;
import com.iot.web.domain.ProductInfoDTO;


@Mapper
public interface UserBoardMapper {

	// 전체 상품 조회
	List<ProductInfoDTO> selectProductList(); 
						 
	ProductInfoDTO selectProductById(int productId);
	
	// 주문 넣기
	int createOrder(OrderInfoDTO orderInfoDTO);
	
	// 주문 조회
	List<OrderInfoDTO> selectOrderList(String userId);

	// 센서 정보 조회
	DeviceInfoDTO retrieveDeviceData(String orderId);
	
	// 주문 정보 조회
	OrderInfoDTO retrieveOrderDataForOrderId(String orderId);

}



