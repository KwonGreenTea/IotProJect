package com.iot.web.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.iot.web.domain.Test;
import com.iot.web.service.TestService;

import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
public class MainController {
	
	@Autowired
	private TestService testService;
	
	@GetMapping("/")
	public String main() {
		return "Hello";
	}
	
	@GetMapping("/test")
	public void testGET() {
		log.info("testGET()");
		
		List<Test> testList = testService.retrieveAllData();
		
		System.out.println(testList.get(0).getDataCd() + " " + testList.get(0).getData1() + " " + testList.get(0).getData2() + " " + testList.get(0).getData3());
	}
}
