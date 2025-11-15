package com.iot.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.iot.web.domain.OrderInfoDTO;
import com.iot.web.service.AdminService;

import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
public class AdminPortalController {

	@Autowired
	private AdminService adminService;
	
    /** 운영자 홈 */
    @GetMapping({"/admin", "/admin/home"})
    public String adminHome() {
        // templates/admin/home.html
        return "admin/home";
    }

    /** 운영자 주문 목록 */
    @GetMapping("/admin/orders")
    public String adminOrders(Model model) {
    	List<OrderInfoDTO> orderList = adminService.retrieveOrderData();
    	
    	for(OrderInfoDTO tempDTO : orderList) {
    		log.info(tempDTO);
    	}
    	
    	model.addAttribute("orderList", orderList);
    	
        // templates/admin/orders/list.html
        return "admin/orders/list";
    }

    /** 운영자 주문 상세/모니터 */
    @GetMapping("/admin/orders/{id}")
    public String adminOrderMonitor(@PathVariable String id) {
        // templates/admin/orders/monitor.html
        return "admin/orders/monitor";
    }

    /** 운영자 상품 목록 */
    @GetMapping("/admin/products")
    public String adminProducts() {
        // templates/admin/products/list.html
        return "admin/products/list";
    }

    /** 운영자 상품 신규 등록 */
    @GetMapping("/admin/products/new")
    public String adminProductNew() {
        // templates/admin/products/new.html
        return "admin/products/new";
    }
}
