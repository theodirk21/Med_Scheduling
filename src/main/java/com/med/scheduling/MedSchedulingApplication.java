package com.med.scheduling;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MedSchedulingApplication {

	public static void main(String[] args) {
		SpringApplication.run(MedSchedulingApplication.class, args);
	}

}
