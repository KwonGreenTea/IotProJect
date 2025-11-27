package com.iot.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
	    
	    log.info("productList/{}", productId);
	  
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


}
