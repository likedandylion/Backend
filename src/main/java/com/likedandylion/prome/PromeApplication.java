package com.likedandylion.prome;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class PromeApplication {

	public static void main(String[] args) {
		SpringApplication.run(PromeApplication.class, args);
	}

}