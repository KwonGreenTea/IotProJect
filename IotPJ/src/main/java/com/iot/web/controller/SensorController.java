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

import com.iot.web.domain.SensorDataRealtimeDTO;
import com.iot.web.service.SensorService;

import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
public class SensorController {

	@Autowired
	private SensorService sensorService;

	private static final String API_KEY = "1234567890abcdef";

	// SSE 통신 방식
	private final Map<String, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();
	
	@PostMapping("/api/data")
	public ResponseEntity<String> receiveDataPost(@RequestHeader(value = "x-api-key", required = false) String apiKey,
			@RequestBody Map<String, Object> jsonData) {

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
		int dataCnt = dataList.size();
		
		Double totalTemperature = 0.0;
		Integer totalHumidity = 0;
		for(SensorDataRealtimeDTO tempDTO : dataList) {
			totalTemperature += tempDTO.getTemperature();
			totalHumidity += tempDTO.getHumidity();
		}
		
		log.info("totalTemperature : " + totalTemperature);
		log.info("totalHumidity : " + totalHumidity);
		log.info("dataCnt : " + dataCnt);

		dataDTO.setTemperature(totalTemperature / dataCnt);
		dataDTO.setHumidity(totalHumidity / dataCnt);
		
		log.info(dataDTO);
		
		// SSE 방식으로 데이터 클라이언트로 전송
		CopyOnWriteArrayList<SseEmitter> specificEmitters = emitters.get(deviceId);

		// 해당 리스트가 존재하고 비어있지 않다면, deviceId의 Emitter 에게만 데이터 전송
		if (specificEmitters != null && !specificEmitters.isEmpty()) {

			for (SseEmitter emitter : specificEmitters) {
				try {
					emitter.send(SseEmitter.event().name("newData").data(dataDTO));
				} catch (IOException e) {
					emitter.completeWithError(e);
				}
			}
		}

		return ResponseEntity.ok("저장 및 전송 완료");
	}

	// 클라이언트별로 emitter 등록
	@GetMapping("/data/stream/{deviceId}")
	public SseEmitter stream(@PathVariable String deviceId) {
		SseEmitter emitter = new SseEmitter(0L);

		// deviceId에 해당하는 리스트를 가져오거나 새로 생성하여 emitter 추가
        CopyOnWriteArrayList<SseEmitter> sensorEmitters = emitters.computeIfAbsent(deviceId, k -> new CopyOnWriteArrayList<>());
        sensorEmitters.add(emitter);
        
        Runnable removeEmitter = () -> {
            CopyOnWriteArrayList<SseEmitter> emittersList = emitters.get(deviceId);
            
            if (emittersList != null) {
                emittersList.remove(emitter);
                
                if (emittersList.isEmpty()) {
                    emitters.remove(deviceId, emittersList);
                }
            }
        };

        emitter.onCompletion(removeEmitter);
        emitter.onTimeout(removeEmitter);
        emitter.onError(e -> {
            removeEmitter.run();
        });

        return emitter;
	}
	
	private String getSysDt() {
		return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
	}
	
}
