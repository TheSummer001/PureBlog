package com.github.toran;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class PersonalBlogApplication {

	public static void main(String[] args) {
		SpringApplication.run(PersonalBlogApplication.class, args);
	}

}
