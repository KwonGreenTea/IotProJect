package com.iot.web.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// 파일 저장용
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.iot.web.domain.DeviceInfoDTO;
import com.iot.web.domain.OrderInfoDTO;
import com.iot.web.domain.ProductInfoDTO;
import com.iot.web.domain.SensorDataRealtimeDTO;
import com.iot.web.service.AdminService;
import com.iot.web.service.LogService;
import com.iot.web.service.SensorService;

import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
public class AdminPortalController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private SensorService sensorService;

    @Autowired
    private LogService logService;

    /** 운영자 홈 */
    @GetMapping("/admin")
    public String adminHome(Model model) {

        Map<String, Integer> filled = logService.retrieveErrData();

        List<String> labels = new ArrayList<>();
        List<Integer> data = new ArrayList<>();

        for (var e : filled.entrySet()) {
            labels.add(e.getKey());
            data.add(e.getValue());
        }

        model.addAttribute("weekLabels", labels);
        model.addAttribute("weekCounts", data);

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
    @PostMapping("/admin/orders/{orderId}/ship")
    @ResponseBody
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
    	OrderInfoDTO orderInfoDTO = null;
    	List<SensorDataRealtimeDTO> dataDTOList = null;
        SensorDataRealtimeDTO dataDTO = null;
        // 현재 활성화 되지 않은 센서(장비)에 orderId를 맵핑
        String startDate = getSysDt();
        log.info("orderId : " + orderId);
        adminService.updateDeviceId(orderId, startDate);
        log.info("updateDeviceId 완료");

        // orderId에 대한 센서(장비) 데이터 정보
        DeviceInfoDTO deviceInfoDTO = adminService.retrieveDeviceData(orderId);
        log.info("retrieveDeviceData 완료");
        
        if(deviceInfoDTO != null) {
        	 log.info("deviceInfoDTO 장비 정보 : " + deviceInfoDTO.toString());

             // orderId에 대한 주문 데이터 정보
             orderInfoDTO = adminService.retrieveOrderDataForOrderId(orderId);

             String deviceId = deviceInfoDTO.getDeviceId();

             // 센서 데이터 불러옴
             dataDTOList = sensorService.retrieveDataList(deviceId);
             dataDTO = new SensorDataRealtimeDTO();

             if (dataDTOList != null && !dataDTOList.isEmpty()) {
                 SensorDataRealtimeDTO firstData = dataDTOList.get(0);
                 dataDTO.setMaxTemperature(firstData.getMaxTemperature());
                 dataDTO.setMaxHumidity(firstData.getMaxHumidity());
                 dataDTO.setMinTemperature(firstData.getMinTemperature());
                 dataDTO.setMinHumidity(firstData.getMinHumidity());
             }

             log.info("deviceInfoDTO : {}", deviceInfoDTO);
             log.info("orderInfoDTO : {}", orderInfoDTO);
             
             model.addAttribute("dataDTO", dataDTO);
             model.addAttribute("dataDTOList", dataDTOList);
             model.addAttribute("deviceId", deviceInfoDTO.getDeviceId());
        }

        model.addAttribute("deviceInfoDTO", deviceInfoDTO);
        model.addAttribute("orderInfoDTO", orderInfoDTO);

        return "admin/orders/monitor";
    }

    // 배송종료
    @PostMapping("/admin/orders/{orderId}/complete")
    public ResponseEntity<String> adminProductEnd(@PathVariable String orderId) {
    	log.info("배송 종료 요청 받음: Order ID {}", orderId);

        adminService.updateOrderIsActiveFn(orderId);
        
        return ResponseEntity.ok("배송이 종료 되었습니다.");
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

    /** 운영자 상품 신규 등록 처리 (AJAX, multipart + 이미지 업로드) */
    @PostMapping(
            value = "/admin/products",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    @ResponseBody
    public String createProduct(
            @RequestParam String sellerId,
            @RequestParam String productName,
            @RequestParam Integer price,
            @RequestParam(required = false) Double minTemperature,
            @RequestParam(required = false) Double maxTemperature,
            @RequestParam(required = false) Integer minHumidity,
            @RequestParam(required = false) Integer maxHumidity,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile
    ) {
        try {
            // 1) 이미지 파일 저장
            String imageUrl = null;

            if (imageFile != null && !imageFile.isEmpty()) {
                // TODO: 추후 설정 파일로 분리 가능
                String uploadDir = "C:/uploads";

                String originalName = imageFile.getOriginalFilename();
                String ext = "";
                if (originalName != null && originalName.lastIndexOf(".") != -1) {
                    ext = originalName.substring(originalName.lastIndexOf("."));
                }

                String savedName = java.util.UUID.randomUUID().toString() + ext;

                Path target = Paths.get(uploadDir, savedName);
                Files.createDirectories(target.getParent());
                Files.copy(imageFile.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

                // 브라우저에서 접근할 URL
                imageUrl = "/uploads/" + savedName;
            }

            // 2) DTO 구성
            ProductInfoDTO dto = new ProductInfoDTO();
            dto.setSellerId(sellerId);
            dto.setProductName(productName);
            dto.setPrice(price);
            dto.setMinTemperature(minTemperature);
            dto.setMaxTemperature(maxTemperature);
            dto.setMinHumidity(minHumidity);
            dto.setMaxHumidity(maxHumidity);
            dto.setImageUrl(imageUrl);

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
