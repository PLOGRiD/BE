package com.example.plogrid;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class PlogridApplication {

	public static void main(String[] args) {
		SpringApplication.run(PlogridApplication.class, args);
	}

}
