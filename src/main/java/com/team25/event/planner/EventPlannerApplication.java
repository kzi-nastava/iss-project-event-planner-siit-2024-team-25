package com.team25.event.planner;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class EventPlannerApplication {

	public static void main(String[] args) {
		SpringApplication.run(EventPlannerApplication.class, args);
	}

}
