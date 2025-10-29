package com.iot.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

@SpringBootApplication
public class IotPjApplication {

	public static void main(String[] args) {
		SpringApplication.run(IotPjApplication.class, args);
	}

}
