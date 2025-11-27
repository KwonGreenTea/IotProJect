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

    public void sendData(String deviceId, String json) {
        CopyOnWriteArrayList<SseEmitter> deviceEmitters = emitters.get(deviceId);

        if (deviceEmitters != null && !deviceEmitters.isEmpty()) {
            for (SseEmitter emitter : deviceEmitters) {
                try {
                    emitter.send(SseEmitter.event()
                            .id(String.valueOf(System.currentTimeMillis()))
                            .name("newData")
                            .data(json));
                } catch (IOException | IllegalStateException e) {
                    // [수정 1] IllegalStateException도 같이 잡아야 합니다.
                    // 이 에러는 타임아웃/종료된 연결에 보낼 때 발생합니다.
                    
                    emitter.completeWithError(e);
                    
                    // [수정 2] 에러 난 Emitter는 리스트에서 즉시 제거해주는 것이 좋습니다.
                    // (물론 onCompletion 콜백에서 제거하도록 되어있다면 생략 가능하지만, 명시적으로 지우는 게 안전합니다)
                    deviceEmitters.remove(emitter);
                } catch (Exception e) {
                    // [선택] 그 외 알 수 없는 에러로 멈추는 것 방지
                    deviceEmitters.remove(emitter);
                }
            }
        }
    }
}