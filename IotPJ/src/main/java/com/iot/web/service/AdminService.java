package com.iot.web.service;

import java.util.List;

import com.iot.web.domain.OrderInfoDTO;

public interface AdminService {
	List<OrderInfoDTO> retrieveOrderData();
}
