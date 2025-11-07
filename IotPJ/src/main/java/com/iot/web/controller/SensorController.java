package com.iot.web.controller;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
	
	// SSE 통신 방식
	private final SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

    @GetMapping("/data/stream")
    public SseEmitter stream() {
        return emitter;
    }

    // 데이터 수신 시마다 웹으로 푸시
    @GetMapping("/api/data")
    public ResponseEntity<String> receiveDataPost(@RequestBody Map<String, Object> jsonData) {
    	log.info("receiveDataPost()");
		
		TestDTO testDTO = new TestDTO();
		
		testDTO.setData1((String) jsonData.get("data1"));
		testDTO.setData2((String) jsonData.get("data2"));
		testDTO.setData3((String) jsonData.get("data3"));
		testDTO.setSensorCd((String) jsonData.get("sensorCd"));
        
        testService.insertData(testDTO);

        try {
            emitter.send(SseEmitter.event().name("newData").data(testDTO));
        } catch (IOException e) {
            emitter.completeWithError(e);
        }

        return ResponseEntity.ok("저장 및 전송 완료");
    }
}
