package com.iot.web.util;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.iot.web.domain.SensorDataRealtimeDTO;
import com.iot.web.service.ProductService;

import lombok.extern.log4j.Log4j2;

@Log4j2
@Service
public class SseEmitterManager {
	
	@Autowired
    private ProductService productService;

	private final Map<String, CopyOnWriteArrayList<SseEmitter>> alertEmitters = new ConcurrentHashMap<>();
    private final Map<String, CopyOnWriteArrayList<SseEmitter>> dataEmitters = new ConcurrentHashMap<>();
    
     // --- 1. 알림용
    public void subscribe(String orderId, String userId, SensorDataRealtimeDTO dataDTO, String json) {
    	//log.info("SseEmitter subscribe - Parameter {} => " + dataDTO);
    	
    	// 상품 최대/최저 온도,습도 비교
    	String resultCd = productService.retreiveProductDataYn(orderId, dataDTO);
    	
    	log.info("resultCd = " + resultCd);
    	
    	// 비정상 데이터만 데이터 전송
    	if("Y".equals(resultCd)) {
    		CopyOnWriteArrayList<SseEmitter> deviceEmitters = alertEmitters.get(userId);

            if (deviceEmitters != null && !deviceEmitters.isEmpty()) {

            	for (SseEmitter emitter : deviceEmitters) {
                    try {
                        emitter.send(SseEmitter.event()
                                .id(String.valueOf(System.currentTimeMillis()))
                                .name("errorData")
                                .data(json));
                    } catch (IOException | IllegalStateException e) {
                        //emitter.completeWithError(e);
                        
                        deviceEmitters.remove(emitter);
                    } catch (Exception e) {
                        // [선택] 그 외 알 수 없는 에러로 멈추는 것 방지
                        deviceEmitters.remove(emitter);
                    }
            	}
            }
    	}
    }

    public void sendData(String deviceId, String json) {
        CopyOnWriteArrayList<SseEmitter> deviceEmitters = dataEmitters.get(deviceId);

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
                    
                   // emitter.completeWithError(e);
                    
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


    public SseEmitter retreiveSubscribe(String userId) {

        // 새로운 Emitter 생성 (타임아웃 무제한)
        SseEmitter emitter = new SseEmitter(0L);

        // 리스트 가져오기
        CopyOnWriteArrayList<SseEmitter> deviceEmitters =
                alertEmitters.computeIfAbsent(userId, k -> new CopyOnWriteArrayList<>());

        // 리스트에 추가
        deviceEmitters.add(emitter);

        // emitter 종료/에러 발생 시 리스트에서 제거
        emitter.onCompletion(() -> deviceEmitters.remove(emitter));
        emitter.onTimeout(() -> deviceEmitters.remove(emitter));
        emitter.onError((e) -> deviceEmitters.remove(emitter));

        return emitter;
    }

    public SseEmitter retreiveData(String deviceId) {

        SseEmitter emitter = new SseEmitter(0L);

        CopyOnWriteArrayList<SseEmitter> deviceEmitters =
                dataEmitters.computeIfAbsent(deviceId, k -> new CopyOnWriteArrayList<>());

        deviceEmitters.add(emitter);

        emitter.onCompletion(() -> deviceEmitters.remove(emitter));
        emitter.onTimeout(() -> deviceEmitters.remove(emitter));
        emitter.onError((e) -> deviceEmitters.remove(emitter));

        return emitter;
    }
    
}