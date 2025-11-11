package com.iot.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class ShopPortalController {

    /** 사용자 홈/카탈로그 */
    @GetMapping({"/", "/catalog"})
    public String catalog() {
        // templates/user/catalog.html
        return "shop/index";
    }

    /** 사용자 주문 목록 */
    @GetMapping("/orders")
    public String orderList() {
        // templates/user/order/orders.html
        return "user/order/orders";
    }

    /** 사용자 주문 상세 */
    @GetMapping("/orders/{id}")
    public String orderDetail(@PathVariable String id) {
        // templates/user/order/orderDetail.html
        return "user/order/orderDetail";
    }

    /** 장바구니 (파일 만들었을 때만 사용) */
    @GetMapping("/cart")
    public String cart() {
        // templates/user/cart/cart.html
        return "cart/index";
    }
    
}
