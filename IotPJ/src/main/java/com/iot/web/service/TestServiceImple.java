package com.iot.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iot.web.domain.TestDTO;
import com.iot.web.mapper.TestMapper;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class TestServiceImple implements TestService {
	
	@Autowired
	private TestMapper testMapper;
	
	@Override
	public TestDTO retrieveAllData() {
		return testMapper.retrieveAllData();
	}
	
	@Override
	public void insertData(TestDTO testDTO) {
		testMapper.insertData(testDTO);
	}
	
}
