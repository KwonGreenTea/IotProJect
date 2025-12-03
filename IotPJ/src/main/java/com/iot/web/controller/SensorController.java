package com.iot.web.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.iot.web.domain.AlarmLogDTO;
import com.iot.web.domain.SensorDataRealtimeDTO;
import com.iot.web.service.LogService;
import com.iot.web.service.SensorService;
import com.iot.web.util.SseEmitterManager;

import lombok.extern.log4j.Log4j2;

@RestController
@Log4j2
public class SensorController {

    @Autowired
    private SensorService sensorService;
    
    @Autowired
    private LogService logService;

    @Autowired
    private SseEmitterManager sseEmitterManager;
    
    private static final String API_KEY = "1234567890abcdef";

    private String getSysDt() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @PostMapping("/api/data")
    public ResponseEntity<String> receiveDataPost(
            @RequestHeader(value = "x-api-key", required = false) String apiKey,
            @RequestBody Map<String, Object> jsonData) throws JsonProcessingException {

        // 1. POST 인증 확인
        if (!API_KEY.equals(apiKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("인증되지 않은 사용자");
        }
        
        log.info("receiveDataPost");
        
        // 2. 데이터 파싱 및 DB 저장
        String deviceId = (String) jsonData.get("sensorCd");
        String orderId = sensorService.retrieveOrderIdByDeviceId(deviceId);
        String userId = sensorService.retrieveUserIdByDeviceId(deviceId);
        //log.info(orderId + " " + userId);
        
        // 로그용 데이터 입력
        String deliveryId = sensorService.retrieveDelivIdByOrderId(orderId);
        
        AlarmLogDTO logDTO = new AlarmLogDTO();
        logDTO.setUserId(userId);
        logDTO.setDeviceId(deviceId);
        logDTO.setOrderId(Integer.parseInt(orderId));
        logDTO.setDeliveryId(deliveryId);
        logDTO.setLoggedAt(getSysDt());
        logDTO.setLogCd("1"); // 1 : 센서 데이터 로그 INSERT
        
        SensorDataRealtimeDTO dataDTO = new SensorDataRealtimeDTO();
        dataDTO.setDeviceId(deviceId);
        dataDTO.setMeasuredAt(getSysDt());
        dataDTO.setTemperature(Double.parseDouble((String) jsonData.get("temperature")));
        dataDTO.setHumidity(Integer.parseInt((String) jsonData.get("humidity")));
        
        sensorService.insertData(dataDTO);
        log.info("insertData() 완료" );
        
        // 3. 통계 데이터 계산 (Max/Min)
        List<SensorDataRealtimeDTO> dataList = sensorService.retrieveDataList(deviceId);
        log.info(dataList);
        
        if (dataList != null && !dataList.isEmpty()) {
            SensorDataRealtimeDTO firstData = dataList.get(0);
            dataDTO.setMaxTemperature(firstData.getMaxTemperature()); 
            dataDTO.setMaxHumidity(firstData.getMaxHumidity());
            dataDTO.setMinTemperature(firstData.getMinTemperature());
            dataDTO.setMinHumidity(firstData.getMinHumidity());       
        }

        log.info(dataDTO);

        // 4. SSE 전송 (여기에 예외 처리 추가)
        ObjectMapper mapper = new ObjectMapper();
        String json = mapper.writeValueAsString(dataDTO);

        // 로그 데이터
        logDTO.setSensorData(json);
        
        try {
            sseEmitterManager.sendData(deviceId, json);
            
            sseEmitterManager.subscribe(orderId, userId, dataDTO, logDTO, json);
            
            // 로그 INSERT
            try {
            	logService.insertLog(logDTO);
    		} catch (Exception e) {
    			//e.printStackTrace();
    			log.warn("OrderId = " + orderId + "의 대한 로그 데이터 INSERT 실패");
    		}             
            
        } catch (Exception e) {
            // SSE 전송 실패는 데이터 저장 성공과는 별개이므로 로그만 남기고 넘어갑니다.
            // 여기서 에러를 잡지 않으면 클라이언트(센서 기기)에게 500 에러가 날아갑니다.
        	//e.printStackTrace();
        	
            log.warn("SSE 실시간 알림 전송 실패 (저장은 완료됨): {}", e.getMessage());
        }

        // 5. 응답 반환 (전송 실패해도 저장은 성공했으므로 OK 반환)
        return ResponseEntity.ok("저장 및 전송(시도) 완료");
    }
}
