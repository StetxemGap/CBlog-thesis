package com.korenko.CBlog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication
public class CBlogApplication {

	public static void main(String[] args) {

		SpringApplication.run(CBlogApplication.class, args);
	}

}
