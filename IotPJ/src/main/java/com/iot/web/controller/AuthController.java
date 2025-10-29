package com.iot.web.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.extern.log4j.Log4j2;

@Controller
@RequestMapping("/auth")
@Log4j2
public class AuthController {
	
	// 접근 제한 요청을 처리하여 jsp 페이지를 호출하는 메서드
	@GetMapping("/accessDenied")
	public void accessDenied(Authentication auth, Model model) {
	    // Authentication : 현재 사용자의 인증 정보를 갖고 있음
	    log.info("accessDenied()");
	    log.info(auth);

	    model.addAttribute("msg", "권한이 없습니다.");
	}
	
	// 로그인 페이지 요청을 처리하여 jsp 페이지를 호출하는 메서드
	@GetMapping("/login")
	public void loginGET(String error, String logout, Model model) {
	    // error : 에러 발생시 정보 저장
		// logout : 로그아웃 정보 저장
	    log.info("loginGET()");
	    log.info("error : " + error);
	    log.info("logout : " + logout);
	    
	    // 에러가 발생한 경우, 에러 메시지를 모델에 추가하여 전달
	    if(error != null) {
	        model.addAttribute("errorMsg", "로그인 에러! 아이디 비밀번호를 확인하세요.");
	    }
	    
	    // 로그아웃이 발생한 경우, 로그아웃 메시지를 모델에 추가하여 전달
	    if(logout != null) {
	        model.addAttribute("logoutMsg", "로그아웃 되었습니다!");
	    }
	}

}
