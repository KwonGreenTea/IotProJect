package com.iot.web.util;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class SseEmitterManager {

    private final Map<String, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();
    
    private static final Long DEFAULT_TIMEOUT = 0L; 

    // --- 1. 알림용
    public SseEmitter subscribe(String orderId) {
        SseEmitter emitter = new SseEmitter(DEFAULT_TIMEOUT); 

        CopyOnWriteArrayList<SseEmitter> deviceEmitters =
            emitters.computeIfAbsent(orderId, k -> new CopyOnWriteArrayList<>());
        deviceEmitters.add(emitter);

        // Emitter 제거 로직 추가. 연결 종료, 타임아웃, 에러 발생 시 목록에서 제거
        Runnable removeEmitter = () -> {
            CopyOnWriteArrayList<SseEmitter> list = emitters.get(orderId);
            if (list != null) {
                list.remove(emitter);
                if (list.isEmpty()) { 
                    emitters.remove(orderId); 
                }
            }
        };

        emitter.onCompletion(removeEmitter);
        emitter.onTimeout(removeEmitter);
        emitter.onError(e -> {
            removeEmitter.run();
        });
        
        // 초기 연결 메시지 전송 추가 (503 에러 방지 및 연결 확인)
        try {
             emitter.send(SseEmitter.event()
                    .id("0")
                    .name("connect") 
                    .data("Connected to " + orderId));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        return emitter;
    }

    // --- 2. 아두이노에서 데이터를 받아와 해당 deviceId 관리자 페이지에 표시
    public void sendData(String deviceId, String json) {
        CopyOnWriteArrayList<SseEmitter> deviceEmitters = emitters.get(deviceId); 

        if (deviceEmitters != null && !deviceEmitters.isEmpty()) {
            for (SseEmitter emitter : deviceEmitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .id(String.valueOf(System.currentTimeMillis()))
                            .name("newData") 
                            .data(json)); 
                } catch (IOException e) {
                    emitter.completeWithError(e); 
                }
            }
        }
    }
}