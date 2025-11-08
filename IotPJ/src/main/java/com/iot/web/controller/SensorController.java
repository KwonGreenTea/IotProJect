package com.iot.web.controller;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.iot.web.domain.TestDTO;
import com.iot.web.service.TestService;

import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
public class SensorController {
	
	@Autowired
	private TestService testService;
	
	private static final String API_KEY = "1234567890abcdef";
	
	// SSE 통신 방식
	private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

	// 클라이언트별로 emitter 등록
    @GetMapping("/data/stream")
    public SseEmitter stream(@RequestHeader(value = "Client-Id", required = false) String clientId) {
        if (clientId == null || clientId.isEmpty()) {
            clientId = "default-" + System.currentTimeMillis();
        }

        final String finalClientId = clientId; 

        SseEmitter emitter = new SseEmitter(0L);
        emitters.put(finalClientId, emitter);

        emitter.onCompletion(() -> emitters.remove(finalClientId));
        emitter.onTimeout(() -> emitters.remove(finalClientId));
        emitter.onError(e -> emitters.remove(finalClientId));

        return emitter;
    }

    // 데이터 수신 시마다 웹으로 푸시
    @PostMapping("/api/data")
    public ResponseEntity<String> receiveDataPost(
    		@RequestHeader(value = "x-api-key", required = false) String apiKey,
    		@RequestBody Map<String, Object> jsonData) {
    	log.info("receiveDataPost()");
    	
    	if (!API_KEY.equals(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid API Key");
        }
		
		TestDTO testDTO = new TestDTO();
		
		testDTO.setData1((String) jsonData.get("data1"));
		testDTO.setData2((String) jsonData.get("data2"));
		testDTO.setData3((String) jsonData.get("data3"));
		testDTO.setSensorCd((String) jsonData.get("sensorCd"));
        
        testService.insertData(testDTO);

        // 모든 연결된 클라이언트에 이벤트 전송
        emitters.forEach((id, emitter) -> {
            try {
                emitter.send(SseEmitter.event().name("newData").data(testDTO));
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(id);
            }
        });

        return ResponseEntity.ok("저장 및 전송 완료");
    }
}
