package com.iot.web.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

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
	
	
	
}
