package com.iot.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.iot.web.util.SseEmitterManager;

@RestController
public class NotificationController {

	@Autowired
    private SseEmitterManager sseEmitterManager; 

    @GetMapping("/alert/subscribe/{orderId}")
    public SseEmitter errDataStream(@PathVariable String orderId) {
    	
    	// 테스트 하드코딩
		orderId = "0";
    	
    	return sseEmitterManager.retreiveSubscribe(orderId);
    }
    
    @GetMapping("/data/stream/{deviceId}")
    public SseEmitter dataStream(@PathVariable String deviceId) {
        return sseEmitterManager.retreiveData(deviceId);
    }
    
}