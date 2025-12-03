package com.iot.web.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.iot.web.domain.DeviceInfoDTO;
import com.iot.web.domain.OrderInfoDTO;
import com.iot.web.domain.ProductInfoDTO;
import com.iot.web.domain.SensorDataRealtimeDTO;
import com.iot.web.service.AdminService;
import com.iot.web.service.SensorService;

import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
public class AdminPortalController {

	@Autowired
	private AdminService adminService;
	
	@Autowired
	private SensorService sensorService;

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

        for (OrderInfoDTO tempDTO : orderList) {
            log.info(tempDTO);
        }

        model.addAttribute("orderList", orderList);

        // templates/admin/orders/list.html
        return "admin/orders/list";
    }
    
    
 // 2. [API] 배송 시작 처리 (새로 추가)
    // 경로: 
    @PostMapping("/admin/orders/{orderId}/ship")
    @ResponseBody // ★ 중요: 이게 있어야 HTML을 안 찾고 JSON/Text 데이터를 반환합니다.
    public ResponseEntity<String> startShipping(@PathVariable String orderId) {
        try {
            log.info("배송 시작 요청 받음: Order ID {}", orderId);

            adminService.updateOrderIsActive(orderId);

            return ResponseEntity.ok("배송이 시작되었습니다."); // 200 OK

        } catch (Exception e) {
            log.error("배송 시작 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("오류 발생: " + e.getMessage());
        }
    }
        
      
    
    
    /** 운영자 주문 상세/모니터 */
    @GetMapping("/admin/orders/{orderId}")
    public String adminOrderMonitor(Model model, @PathVariable String orderId) {
        // 현재 활성화 되지 않은 센서(장비)에 orderId를 맵핑
        String startDate = getSysDt();
        log.info("orderId : " + orderId); 
        adminService.updateDeviceId(orderId, startDate);
        log.info("updateDeviceId 완료");

        // orderId에 대한 센서(장비) 데이터 정보
        DeviceInfoDTO deviceInfoDTO = adminService.retrieveDeviceData(orderId);
        log.info("retrieveDeviceData 완료");
        log.info("deviceInfoDTO 장비 정보 : " + deviceInfoDTO.toString());

        // orderId에 대한 주문 데이터 정보
        OrderInfoDTO orderInfoDTO = adminService.retrieveOrderDataForOrderId(orderId);
        //log.info("retrieveOrderDataForOrderId");
        
        String deviceId = deviceInfoDTO.getDeviceId();
    	
    	// 센서 데이터 불러옴
    	List<SensorDataRealtimeDTO> dataDTOList = sensorService.retrieveDataList(deviceId);
    	SensorDataRealtimeDTO dataDTO = new SensorDataRealtimeDTO();
    	 
    	if (dataDTOList != null && !dataDTOList.isEmpty()) {
            SensorDataRealtimeDTO firstData = dataDTOList.get(0);
            dataDTO.setMaxTemperature(firstData.getMaxTemperature()); 
            dataDTO.setMaxHumidity(firstData.getMaxHumidity());
            dataDTO.setMinTemperature(firstData.getMinTemperature());
            dataDTO.setMinHumidity(firstData.getMinHumidity());       
        }

        log.info("deviceInfoDTO : {}", deviceInfoDTO);
        log.info("orderInfoDTO : {}", orderInfoDTO);

        model.addAttribute("deviceInfoDTO", deviceInfoDTO);
        model.addAttribute("deviceId", deviceInfoDTO.getDeviceId());
        model.addAttribute("orderInfoDTO", orderInfoDTO);
        model.addAttribute("dataDTO", dataDTO);
        model.addAttribute("dataDTOList", dataDTOList);

        return "admin/orders/monitor";
    }

    /** 운영자 상품 목록 */
    @GetMapping("/admin/products")
    public String adminProducts() {
        // templates/admin/products/list.html
        return "admin/products/list";
    }

    /** 운영자 상품 신규 등록 화면 */
    @GetMapping("/admin/products/new")
    public String adminProductNew() {
        // templates/admin/products/new.html
        return "admin/products/new";
    }

    /** 운영자 상품 신규 등록 처리 (AJAX) */
    @PostMapping("/admin/products")
    @ResponseBody
    public String createProduct(@RequestBody ProductInfoDTO dto) {
        try {
            log.info("상품 등록 요청: {}", dto);
            adminService.createProduct(dto);
            return "OK";
        } catch (Exception e) {
            log.error("상품 등록 중 오류", e);
            return "ERROR";
        }
    }

    private String getSysDt() {
        return LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
