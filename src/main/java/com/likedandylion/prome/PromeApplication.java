package com.likedandylion.prome;

import com.likedandylion.prome.global.jwt.JwtProperties; // 1. JwtProperties 클래스를 임포트
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties; // 2. 어노테이션을 임포트

@SpringBootApplication
@EnableConfigurationProperties(JwtProperties.class) // 3. [핵심] JwtProperties 클래스를 설정 빈으로 등록
public class PromeApplication {

	public static void main(String[] args) {
		SpringApplication.run(PromeApplication.class, args);
	}

}