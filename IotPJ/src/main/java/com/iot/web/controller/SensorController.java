package com.iot.web.controller;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.web.domain.SensorDataRealtimeDTO;
import com.iot.web.service.SensorService;

import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
public class SensorController {

    @Autowired
    private SensorService sensorService;

    private static final String API_KEY = "1234567890abcdef";

    // deviceId별 emitter 리스트
    private final Map<String, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

    private String getSysDt() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @GetMapping("/data/stream/{deviceId}")
    public SseEmitter stream(@PathVariable String deviceId) {
        SseEmitter emitter = new SseEmitter(0L);

        CopyOnWriteArrayList<SseEmitter> deviceEmitters =
            emitters.computeIfAbsent(deviceId, k -> new CopyOnWriteArrayList<>());
        deviceEmitters.add(emitter);

        log.info("Register SSE for deviceId={}", deviceId);

        Runnable removeEmitter = () -> {
            CopyOnWriteArrayList<SseEmitter> list = emitters.get(deviceId);
            if (list != null) {
                list.remove(emitter);
                if (list.isEmpty()) {
                    emitters.remove(deviceId);
                }
            }
        };

        emitter.onCompletion(removeEmitter);
        emitter.onTimeout(removeEmitter);
        emitter.onError(e -> removeEmitter.run());

        return emitter;
    }

    @PostMapping("/api/data")
    public ResponseEntity<String> receiveDataPost(
            @RequestHeader(value = "x-api-key", required = false) String apiKey,
            @RequestBody Map<String, Object> jsonData) throws JsonProcessingException {

    	// POST 인증 확인
    	if (!API_KEY.equals(apiKey)) {
    		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("인증되지 않은 사용자");
    	}

    	// 가져온 데이터 TB에 INSERT
    	String deviceId = (String) jsonData.get("sensorCd");

    	SensorDataRealtimeDTO dataDTO = new SensorDataRealtimeDTO();
    	dataDTO.setDeviceId(deviceId);
    	dataDTO.setMeasuredAt(getSysDt());
    	dataDTO.setTemperature(Double.parseDouble((String) jsonData.get("temperature")));
    	dataDTO.setHumidity(Integer.parseInt((String) jsonData.get("humidity")));

    	sensorService.insertData(dataDTO);

    			
    	// 온도/습도 데이터를 가져와 평균 수치 계산 (최대 10개)
    	List<SensorDataRealtimeDTO> dataList = sensorService.retrieveDataList(deviceId);
   
    	log.info(dataList);
    	
    	Double maxTemperature = null;
    	int maxHumidity = 0;
    	// 최소값 변수 추가
    	Double minTemperature = null;
    	int minHumidity = 0;
    	
    	if (dataList != null && !dataList.isEmpty()) {
    	    SensorDataRealtimeDTO firstData = dataList.get(0);
    	    
    	 // 최대값 추출
    	    maxTemperature = firstData.getMaxTemperature(); 
    	    maxHumidity = firstData.getMaxHumidity();
    	    
    	    // 최소값 추출 (새로 추가)
    	    minTemperature = firstData.getMinTemperature();
    	    minHumidity = firstData.getMinHumidity();
    	    
    	    // 현재 삽입된 dataDTO에 최대/최소값을 넣어줍니다.
    	    dataDTO.setMaxTemperature(maxTemperature);
    	    dataDTO.setMaxHumidity(maxHumidity);
    	    dataDTO.setMinTemperature(minTemperature); // 최소 온도 설정
    	    dataDTO.setMinHumidity(minHumidity);       // 최소 습도 설정
    	}
   
    	log.info(dataDTO);

        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(dataDTO);

        // deviceId에 해당하는 클라이언트에게만 전송
        CopyOnWriteArrayList<SseEmitter> deviceEmitters = emitters.get(deviceId);
        if (deviceEmitters != null && !deviceEmitters.isEmpty()) {
            for (SseEmitter emitter : deviceEmitters) {
                try {
                    emitter.send(SseEmitter.event().name("newData").data(json));
                } catch (IOException e) {
                    emitter.completeWithError(e);
                }
            }
        }

        return ResponseEntity.ok("저장 및 전송 완료");
    }
}
