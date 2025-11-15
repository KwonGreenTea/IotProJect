package com.iot.web.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.iot.web.domain.OrderInfoDTO;

@Mapper
public interface AdminMapper {

	List<OrderInfoDTO> retrieveOrderData();

}
