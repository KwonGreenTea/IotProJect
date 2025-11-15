package com.iot.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
		log.info("getAllProduct");
		
		return userBoardMapper.selectProductList() ;
	}

	@Override
	public ProductInfoDTO selectProductById(int productId) {
		log.info("getProductById");
		
		return userBoardMapper.selectProductById(productId);
	}
	
	
	
}
