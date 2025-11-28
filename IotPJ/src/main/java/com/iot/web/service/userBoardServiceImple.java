package com.iot.web.service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import com.iot.web.domain.OrderInfoDTO;
import com.iot.web.domain.ProductInfoDTO;
import com.iot.web.domain.TestDTO;
import com.iot.web.mapper.TestMapper;
import com.iot.web.mapper.UserBoardMapper;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class userBoardServiceImple implements userBoardService {
	
	@Autowired
	private UserBoardMapper userBoardMapper;

	@Override
	public List<ProductInfoDTO> selectProductList() {
		log.info("selectProductList");
		
		return userBoardMapper.selectProductList() ;
	}

	@Override
	public ProductInfoDTO selectProductById(@PathVariable Integer productId) {
		log.info("selectProductById");
		
		return userBoardMapper.selectProductById(productId);
	}

	@Override
	public int createOrder(OrderInfoDTO orderInfoDTO) {
		log.info("createOrder()");
		
		// 사용자 ID 하드코딩
		orderInfoDTO.setUserId("test");
        
        // 주문 상태 (Active: Y)
		orderInfoDTO.setIsActive("N");
        
        // 주문 시간 (현재 날짜 YYYY-MM-DD)
		orderInfoDTO.setOrderedAt(LocalDate.now().toString());
        
        // 운송장 번호 생성 (랜덤 UUID 앞 12자리 대문자)   
        String deliveryId = UUID.randomUUID().toString().replace("-", "").substring(0, 12).toUpperCase();
        orderInfoDTO.setDeliveryId(deliveryId);
		
		
		return userBoardMapper.createOrder(orderInfoDTO);
	
	}
	
	
	
}
