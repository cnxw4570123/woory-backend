package com.woory.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class WooryApplication {

	public static void main(String[] args) {
		SpringApplication.run(WooryApplication.class, args);
	}

}
