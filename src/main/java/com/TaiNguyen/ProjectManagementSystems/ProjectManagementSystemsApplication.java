package com.TaiNguyen.ProjectManagementSystems;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ProjectManagementSystemsApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProjectManagementSystemsApplication.class, args);
	}

}
