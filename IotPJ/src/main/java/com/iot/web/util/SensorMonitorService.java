package com.iot.web.util;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.iot.web.domain.AlarmLogDTO;
import com.iot.web.mapper.SensorMapper;
import com.iot.web.service.LogService;
import com.iot.web.service.SensorService;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@RequiredArgsConstructor
@Log4j2
public class SensorMonitorService {

    private final SensorMapper sensorMapper;
    private final SensorService sensorService;  // 이미 사용 중인 서비스
    private final LogService logService;        // AlarmLog INSERT 하는 서비스

    // 60초마다 한번씩 “1분 동안 안 들어온 장비” 체크
    //@Scheduled(fixedDelay = 60000)
    public void checkNoDataDevices() {
        int seconds = 60; // 1분 기준

        List<String> deviceList = sensorMapper.selectDevicesNoDataForSeconds(seconds);

        if (deviceList == null || deviceList.isEmpty()) {
            return;
        }

        for (String deviceId : deviceList) {
            try {
                insertAbnormalLog(deviceId);
            } catch (Exception e) {
                log.warn("이상 로그 INSERT 실패 - deviceId={}", deviceId, e);
            }
        }
    }

    private void insertAbnormalLog(String deviceId) {

        String orderId   = sensorService.retrieveOrderIdByDeviceId(deviceId);
        String userId    = sensorService.retrieveUserIdByDeviceId(deviceId);
        String deliveryId = sensorService.retrieveDelivIdByOrderId(orderId);

        AlarmLogDTO logDTO = new AlarmLogDTO();
        logDTO.setUserId(userId);
        logDTO.setDeviceId(deviceId);
        logDTO.setOrderId(Integer.parseInt(orderId));
        logDTO.setDeliveryId(deliveryId);
        logDTO.setLoggedAt(getSysDt()); 
        logDTO.setLogCd("2");           
        logDTO.setSensorData("1분 동안 데이터 없음");

        logService.insertLog(logDTO);
        log.info("비정상 로그 INSERT 완료 - deviceId={}, orderId={}", deviceId, orderId);
    }

    private String getSysDt() {
        return java.time.LocalDateTime.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}
