package com.iot.web.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.iot.web.domain.TestDTO;

@Mapper
public interface TestMapper {

	TestDTO retrieveAllData();

	void insertData(TestDTO testDTO);

	
	
}
