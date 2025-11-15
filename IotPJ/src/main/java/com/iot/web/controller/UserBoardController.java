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

	@GetMapping({ "/", "/catalog" })
	public String caltalog() {

		return "shop/index";
	}
	
	@GetMapping("/catalog/{productId}")
	public String caltalogById() {
		log.info("caltalogById");
		
		return "shop/detail";
	}
	
	

	@GetMapping({ "/productList" })
	@ResponseBody
	public List<ProductInfoDTO> productList() {

		log.info("productList");

		List<ProductInfoDTO> productList = userBoardService.selectProductList();

		log.info("상품 리스트 : " + productList);

		return userBoardService.selectProductList();
	}

	

	  @GetMapping("/productList/{productId}") 
	  @ResponseBody
	  public ProductInfoDTO selectProductById(@PathVariable Integer productId) {
	  log.info("productList/{}", productId);
	  
	  ProductInfoDTO productInfoById = userBoardService.selectProductById(productId);
	  
	  log.info("조회 상품 : " + productInfoById);
	  
	  return productInfoById;
	 
	  }
}
