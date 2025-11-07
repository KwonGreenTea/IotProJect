package com.iot.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.extern.log4j.Log4j2;

@Controller
@Log4j2
public class UserController {
	
	@GetMapping("/user/prod/info")
	public String prodInfo() {
	    return "user/prod/info";
	}
}
