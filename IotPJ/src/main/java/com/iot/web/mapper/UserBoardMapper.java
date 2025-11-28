package com.iot.web.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.iot.web.domain.OrderInfoDTO;
import com.iot.web.domain.ProductInfoDTO;


@Mapper
public interface UserBoardMapper {

	// 전체 상품 조회
	List<ProductInfoDTO> selectProductList(); 
						 
	ProductInfoDTO selectProductById(int productId);
	
	// 주문 넣기
	int createOrder(OrderInfoDTO orderInfoDTO);
	
}
