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

    @GetMapping("/data/stream/{orderId}")
    public SseEmitter stream(@PathVariable String orderId) {
    	// 알림
    	
        // SSE 연결 호출
        return sseEmitterManager.subscribe(orderId);
    }
    
}