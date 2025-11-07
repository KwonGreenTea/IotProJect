package com.iot.web.service;

import com.iot.web.domain.TestDTO;

public interface TestService {
	TestDTO retrieveAllData();

	void insertData(TestDTO testDTO);
}
