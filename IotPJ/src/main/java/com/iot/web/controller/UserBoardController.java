package com.iot.web.controller;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.iot.web.domain.DeviceInfoDTO;
import com.iot.web.domain.OrderInfoDTO;
import com.iot.web.domain.ProductInfoDTO;
import com.iot.web.service.userBoardService;

import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
public class UserBoardController {

	@Autowired
	private userBoardService userBoardService;

	/*
	 * @GetMapping({ "/", "/catalog" }) public String caltalog() {
	 * 
	 * return "shop/index"; }
	 */
	
	@GetMapping("/catalog/{productId}")
	public String caltalogById() {
		log.info("caltalogById");
		
		return "shop/detail";
	}
	
	

	@GetMapping({ "/" })
	public String productList(Model model) {

		log.info("productList");

		List<ProductInfoDTO> productList = userBoardService.selectProductList();

		log.info("상품 리스트 : " + productList);

		model.addAttribute("productList", productList);
		
		return "shop/index";
	}

	
	
	@GetMapping("/productList/{productId}")
	public String selectProductById(@PathVariable Integer productId, Model model) {
	    
	    log.info("productList : ", productId);
	  
	    // 서비스에서 데이터 조회
	    ProductInfoDTO productInfoById = userBoardService.selectProductById(productId);
	  
	    log.info("조회 상품 : " + productInfoById);
	  
	    // 타임리프로 데이터 전달 ("productInfoById" 라는 이름으로)
	    model.addAttribute("productInfoById", productInfoById);
	  
	    // templates/shop/detail.html 로 이동
	    return "shop/detail";
	}

	 /** 장바구니 (파일 만들었을 때만 사용) */
    @GetMapping("/cart")
    public String cart() {
    	
    	log.info("cart()");
        // templates/user/cart/cart.html
        return "cart/index";
    }
    
    
    
    
    @PostMapping("/orders/buy")
    @ResponseBody 
    public ResponseEntity<String> buyProduct(@RequestBody OrderInfoDTO orderDto) {
        try {
            // 1. 서비스 호출 후 결과값(행 개수) 받기
            int result = userBoardService.createOrder(orderDto);
            
            // 2. 결과 확인 (1 이상이면 성공)
            if (result > 0) {
                log.info("주문 성공: DB Insert 완료");
                return ResponseEntity.ok("주문 성공");
            } else {
                // 결과가 0이면 DB에는 접근했으나 저장이 안 된 경우
                log.warn("주문 실패: DB Insert 0건");
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                     .body("주문 처리에 실패했습니다. (저장된 데이터 없음)");
            }
  
        } catch (Exception e) {
            // SQL 에러 등 예외 발생 시
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("주문 실패: " + e.getMessage());
        }
    }

    
    @GetMapping("/order/list")
    public String userOrderList(Model model) {
        
    	log.info("userOrderList()");
        // 1. 서비스 호출
        // (Service 내부에서 userId="test"로 고정해서 DB 조회를 수행합니다)
        List<OrderInfoDTO> myOrderList = userBoardService.selectOrderList();
        
        // 2. 로그 확인 (개발 편의용)
         log.info("조회된 주문 건수: " + myOrderList.size());

        // 3. 모델에 담기
        // HTML에서 th:each="order : ${myOrderList}" 로 사용할 이름입니다.
        model.addAttribute("myOrderList", myOrderList);
        
        // 4. 뷰 페이지로 이동 (templates/shop/order.html)
        return "shop/order";
    }
    
    
    // 사용자 모니터링 부분 
    /** * 사용자용 주문 상세 모니터링 페이지 
     * 경로: GET /order/monitor/{orderId}
     */
    @GetMapping("/order/monitor/{orderId}")
    public String userOrderMonitor(@PathVariable String orderId, Model model) {
        log.info("사용자 모니터링 진입: orderId = {}", orderId);

        // 1. 센서(장비) 데이터 정보 조회
        // (주의: 사용자는 매핑 로직 updateDeviceId를 실행하지 않음! 조회만 수행)
        DeviceInfoDTO deviceInfoDTO = userBoardService.retrieveDeviceData(orderId);
        
        // 2. 주문 상세 데이터 조회
        OrderInfoDTO orderInfoDTO = userBoardService.retrieveOrderDataForOrderId(orderId);

        // 3. 로그 확인
        log.info("deviceInfoDTO : {}", deviceInfoDTO);
        log.info("orderInfoDTO : {}", orderInfoDTO);

        // 4. 모델에 담기
        // (배송 전이라 device가 없을 수도 있으므로 null 체크가 필요할 수 있음)
        if (deviceInfoDTO != null) {
            model.addAttribute("deviceInfoDTO", deviceInfoDTO);
            model.addAttribute("deviceId", deviceInfoDTO.getDeviceId());
        } else {
            // 아직 배송 시작 전이거나 센서가 없는 경우 처리
            model.addAttribute("deviceId", ""); 
        }
        
        model.addAttribute("orderInfoDTO", orderInfoDTO);
        model.addAttribute("orderId", orderId); // HTML에서 쓰기 위해 명시적으로 추가

        // 아까 만든 사용자용 모니터링 화면 (templates/shop/monitor.html)
        return "shop/monitor";
    }
    
    
    
}
    
    
    
    
    
    


