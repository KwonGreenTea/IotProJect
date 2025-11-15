package com.iot.web.service;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;

import com.iot.web.domain.ProductInfoDTO;

public interface userBoardService {
	List<ProductInfoDTO> selectProductList(); // 전체 상품 조회
	
	ProductInfoDTO selectProductById(@PathVariable Integer productId); // 특정 상품 조회
	
}
