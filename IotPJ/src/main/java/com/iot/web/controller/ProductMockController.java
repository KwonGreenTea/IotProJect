package com.iot.web.controller;

import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/mock/products")
public class ProductMockController {

    // 메모리 안에 임시 저장하는 리스트 (DB 아님)
    private final List<Map<String, Object>> products = new ArrayList<>();

    // 상품 등록 (POST)
    @PostMapping
    public Map<String, Object> addProduct(@RequestBody Map<String, Object> product) {
        product.put("id", UUID.randomUUID().toString()); // 랜덤 ID 생성
        products.add(product);
        return product; // 저장된 상품 반환
    }

    // 상품 목록 조회 (GET)
    @GetMapping
    public List<Map<String, Object>> getProducts() {
        return products;
    }
}
