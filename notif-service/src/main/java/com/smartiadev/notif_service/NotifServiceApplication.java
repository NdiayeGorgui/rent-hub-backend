package com.smartiadev.notif_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class NotifServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotifServiceApplication.class, args);
	}

}
