// AdminProductApiController.java

package com.iot.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.iot.web.domain.ProductInfoDTO;
import com.iot.web.service.AdminProductService;

@RestController
public class AdminProductApiController {

    private final AdminProductService adminProductService;

    public AdminProductApiController(AdminProductService adminProductService) {
        this.adminProductService = adminProductService;
    }

    /** 상품 등록 */
    @PostMapping("/admin/products")
    public ResponseEntity<String> createProduct(@RequestBody ProductInfoDTO dto) {
        adminProductService.createProduct(dto);
        return ResponseEntity.ok("OK");
    }
}