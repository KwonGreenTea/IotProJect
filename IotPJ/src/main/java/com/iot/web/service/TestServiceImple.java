package com.iot.web.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.iot.web.domain.Test;
import com.iot.web.domain.TestDTO;
import com.iot.web.repository.TestRepository;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class TestServiceImple implements TestService {
	
	@Autowired
	private TestRepository testRepository;
	
	@Override
	public TestDTO retrieveAllData() {
		Test test = testRepository.retrieveAllData();
		
		return toDto(test);
	}
	
	@Override
	public void insertData(TestDTO testDTO) {
		Test test = toEntity(testDTO);
		testRepository.save(test);
	}

	public TestDTO toDto(Test test) {
		TestDTO testDTO = new TestDTO();
		testDTO.setDataCd(test.getDataCd());
		testDTO.setData1(test.getData1());
		testDTO.setData2(test.getData2());
		testDTO.setData3(test.getData3());
		testDTO.setSensorCd(test.getSensorCd());
		
		return testDTO;
	}

	public Test toEntity(TestDTO testDTO) {
		Test test = new Test();
		test.setDataCd(testDTO.getDataCd());
		test.setData1(testDTO.getData1());
		test.setData2(testDTO.getData2());
		test.setData3(testDTO.getData3());
		test.setSensorCd(testDTO.getSensorCd());
		
		return test;
	}
	
}
