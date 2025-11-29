package com.iot.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.iot.web.service.SensorService;

@RestController
public class UserController {
	
	@Autowired
    private SensorService sensorService;
	
	@GetMapping("/retrieve/orderId/{deviceId}")
	public String retreiveOrderIdByDeviceId(@PathVariable String deviceId) {
	    return sensorService.retrieveOrderIdByDeviceId(deviceId);
	}
}
