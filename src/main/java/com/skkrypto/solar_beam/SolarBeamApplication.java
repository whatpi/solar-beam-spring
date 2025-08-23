package com.skkrypto.solar_beam;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SolarBeamApplication {

	public static void main(String[] args) {
		SpringApplication.run(SolarBeamApplication.class, args);
	}

}
