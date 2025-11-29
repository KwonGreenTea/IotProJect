package com.iot.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iot.web.domain.SensorDataRealtimeDTO;
import com.iot.web.mapper.ProductMapper;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class ProductServiceImple implements ProductService {

    @Autowired
    private ProductMapper productMapper;

	@Override
	public String retreiveProductDataYn(String orderId, SensorDataRealtimeDTO dataDTO) {
		return productMapper.retreiveProductDataYn(orderId, dataDTO);
	}
	
}
