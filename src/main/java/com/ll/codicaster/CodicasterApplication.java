package com.ll.codicaster;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class CodicasterApplication {

	public static void main(String[] args) {
		SpringApplication.run(CodicasterApplication.class, args);
	}

}
