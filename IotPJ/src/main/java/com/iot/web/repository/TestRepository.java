package com.iot.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.iot.web.domain.Test;

public interface TestRepository extends JpaRepository<Test, Integer> {
	
	@Query("SELECT a FROM Test a")
	List<Test> retrieveAllData();
}

