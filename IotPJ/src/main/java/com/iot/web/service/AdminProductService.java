package com.iot.web.service;

import org.springframework.stereotype.Service;

import com.iot.web.domain.ProductInfoDTO;
import com.iot.web.mapper.AdminMapper;

@Service
public class AdminProductService {

    private final AdminMapper adminMapper;

    public AdminProductService(AdminMapper adminMapper) {
        this.adminMapper = adminMapper;
    }

    public void createProduct(ProductInfoDTO dto) {
        adminMapper.insertProduct(dto);
    }
}
