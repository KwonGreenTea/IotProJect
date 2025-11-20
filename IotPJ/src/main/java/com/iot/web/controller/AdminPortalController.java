package com.iot.web.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.iot.web.domain.DeviceInfoDTO;
import com.iot.web.domain.OrderInfoDTO;
import com.iot.web.service.AdminService;

import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
public class AdminPortalController {

	@Autowired
	private AdminService adminService;
	
    /** 운영자 홈 */
    @GetMapping("/admin")
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
    @GetMapping("/admin/orders/{orderId}")
    public String adminOrderMonitor(Model model, @PathVariable String orderId) {
    	// 현재 활성화 되지 않은 센서(장비)에 orderId를 맵핑
    	String startDate = getSysDt();
    	adminService.updateDeviceId(orderId, startDate);
    	log.info("updateDeviceId 완료");
    	
    	// orderId에 대한 센서(장비) 데이터 정보 가져옴
    	DeviceInfoDTO deviceInfoDTO = adminService.retrieveDeviceData(orderId);
    	log.info("retrieveDeviceData");
    	
    	// orderId에 대한 주문 데이터 정보 가져옴
    	OrderInfoDTO orderInfoDTO = adminService.retrieveOrderDataForOrderId(orderId);

    	log.info("retrieveOrderDataForOrderId");
    	
    	log.info("deviceInfoDTO : " + deviceInfoDTO);
    	log.info("orderInfoDTO : " + orderInfoDTO);
    	
    	model.addAttribute("deviceInfoDTO", deviceInfoDTO);
    	model.addAttribute("deviceId", deviceInfoDTO.getDeviceId());
    	model.addAttribute("orderInfoDTO", orderInfoDTO);
    	
    	// 
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
    
    private String getSysDt() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}
}
