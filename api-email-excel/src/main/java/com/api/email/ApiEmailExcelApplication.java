package com.api.email;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = {"controller", "service"})
public class ApiEmailExcelApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiEmailExcelApplication.class, args);
	}

}
